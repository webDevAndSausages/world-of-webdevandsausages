<script>
	import {getContext, onMount, onDestroy} from 'svelte'
	import {writable} from 'svelte/store'
	import Input from './Input.svelte'
	import CmdButton from './CmdButton.svelte'
	import CmdInput from './CmdInput.svelte'
	import ky from 'ky'
	import config from './config'
	import api from './api'
	import {Result} from './models/Result'
	import {
		registrationStore,
		validationStore,
		initialState,
		successAsciiStore,
	} from './stores/registrationStore'
	import {getFullRegistrationCmd, normalizeCmd} from './utils'

	const {cmds} = getContext('terminalStore')
	const event = getContext('eventStore')

	let subscription = null

	export let active

	let result = writable(Result.NotAsked)
	let resultLoading = null
	let successData = null
	let failureMsg = null
	let failureStatus = null
	let success = ''
	let cmdInputValue = ''

	let eventId = $event.okOrNull($event).id
	async function submit(_ev) {
		if (!eventId) return
		const response = await ky(api.register(eventId), {
			method: 'POST',
			headers: config.headers,
			body: JSON.stringify($registrationStore.values),
			hooks: {
				afterResponse: [
					async (_input, _options, response) => {
						if (response.status >= 400) {
							const errorParsed = await response.json()
							$result = Result.Failure({
								...errorParsed,
								status: response.status,
							})
						}
					},
				],
			},
		})

		const parsed = await response.json()
		$result = Result.Ok(parsed)
	}

	const cmdsMap = {
			r: () => {
				document.getElementById('registration').reset()
			},
			s: submit,
			x: cmds.wait
		}

	function onCmd(cmd) {
			const c = cmd && cmd.length ? normalizeCmd(cmd) : ''
			console.log(c)
			if (c.length) {
				switch (c) {
					case 'r':
						return cmdsMap.r()
					case 's':
						return cmdsMap.s()
					case 'x':
						return cmds.wait()
					default:
						return cmds.invalid({ cmd })
				}
			} else if (c.length) {
				return cmds.invalid({ cmd })
			}
			return
		}

	function handleBtnClick(cmd) {
		if(cmdsMap[cmd]) {
			cmdsMap[cmd]()
			cmdInputValue = getFullRegistrationCmd(cmd)
		}
	}

	function getMsg() {
		// must be manual, with  autosubribe $, the whole message is created before rendering
		successAsciiStore.subscribe(val => (success = val))
	}

	$: $result.cata({
		NotAsked: () => {},
		Pending: () => (resultLoading = true),
		Ok: data => {
			resultLoading = false
			successData = data
			active = false
			getMsg()
		},
		Failure: error => {
			resultLoading = false
			failureMsg = error.message
				? error.message
				: 'A mysterious error occurred on the server.'
			failureStatus = error.status
			active = false
		},
	})

	onDestroy(() => {
		subscription.unsubscribe && subscription.unsubscribe()
	})
</script>

<style>
	.registration {
	}

	.success-ascii {
		min-height: 200px;
	}
</style>

<div class="registration flex-col m-5">
	<form
		on:submit|preventDefault="{submit}"
		id="registration"
	>
		<h2 class="term-title mb-5">~ REGISTRATION ~</h2>
		<fieldset class="flex-1">
			<Input
				label="email"
				type="email"
				bind:value="{$registrationStore.values.email}"
				error="{$validationStore.errors.email}"
				disabled="{!active}"
			/>
		</fieldset>
		<fieldset class="flex-1">
			<Input
				label="first name"
				bind:value="{$registrationStore.values.firstName}"
				error="{$validationStore.errors.firstName}"
				disabled="{!active}"
			/>
		</fieldset>
		<fieldset class="flex-1">
			<Input
				label="last name"
				bind:value="{$registrationStore.values.lastName}"
				error="{$validationStore.errors.lastName}"
				disabled="{!active}"
			/>
		</fieldset>
		<fieldset class="flex-1">
			<Input
				label="affiliation"
				bind:value="{$registrationStore.values.affiliation}"
				error="{$validationStore.errors.affiliation}"
				disabled="{!active}"
			/>
		</fieldset>
		<div class="flex-initial pt-5">
			<div class="flex align-middle">
				<div
					class="flex-initial text-term-brand-2"
					style="min-width: 60px;"
				>
					$ cmds:
				</div>
				<CmdButton
					cmd="s"
					type="submit"
					tabindex="1"
					on:cmd="{({detail}) => handleBtnClick(detail)}"
					disabled="{!active || !$validationStore.isValid}"
				>
					submit
				</CmdButton>
				<CmdButton
					cmd="r"
					type="reset"
					tabindex="2"
					on:cmd="{({detail}) => handleBtnClick(detail)}"
					disabled="{!active}"
				>
					reset
				</CmdButton>
				<CmdButton
					cmd="x"
					tabindex="3"
					on:cmd="{({detail}) => handleBtnClick(detail)}"
					disabled="{!active}"
				>
					cancel
				</CmdButton>
			</div>
		</div>
	</form>
	{#if $validationStore.isValid}
		<CmdInput on:cmd="{({detail}) => onCmd(detail)}" bind:value={cmdInputValue} active={active} />
	{/if}
</div>

<div>
	{#if resultLoading} loading... {/if} {#if successData}
	<div class="ml-16 success-ascii text-term-brand-2">
		<pre>{success}</pre>
	</div>
	{/if}
	<div>
		{#if failureMsg} {failureStatus}: {failureMsg} {/if}
	</div>
</div>
