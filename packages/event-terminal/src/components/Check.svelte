<script>
	import {getContext, onMount, onDestroy, tick} from 'svelte'
	import {writable} from 'svelte/store'
	import Input from './Input.svelte'
	import Spinner from './Spinner.svelte'
	import CmdButton from './CmdButton.svelte'
	import CmdInput from './CmdInput.svelte'
	import TerminalTitle from './TerminalTitle.svelte'
	import FailureOut from './FailureOut.svelte'
	import SuccessOut from './SuccessOut.svelte'
	import ky from 'ky'
	import config from './config'
	import api from './api'
	import {Result} from './models/Result'
	import {
		createTokenStore,
		createTokenValidationStore,
	} from './stores/tokenStore'
	import {getFullRegistrationCmd, normalizeCmd} from './utils'

	onMount(() => {
		cmdsMap.r()
	})

	/*
	async function scroll() {
		await tick()
		const resultId = `result-${index}`
		const elem = document.getElementById(resultId);
		if(elem) {
			elem.scrollIntoView()
		}
  }
  */

	const token = createTokenStore()
	const tokenError = createTokenValidationStore(token)

	const {cmds} = getContext('terminalStore')
	const event = getContext('eventStore')

	export let active = true
	export let index
	export let id

	let result = writable(Result.NotAsked)
	let resultLoading = null
	let successData = null
	let failureData = null
	let success = ''
	let cmdInputValue = ''
	let formId = `check-${index}`

	let eventId = $event.okOrNull($event).id
	async function submit(_ev) {
		if (!eventId) return
		const response = await ky(api.checkRegistration(eventId, $token.trim()), {
			method: 'GET',
			headers: config.headers,
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
			document.getElementById(formId).reset()
		},
		s: submit,
		x: cmds.wait,
	}

	function onCmd(cmd) {
		const c = cmd && cmd.length ? normalizeCmd(cmd) : ''
		if (c.length) {
			switch (c) {
				case 'r':
					return cmdsMap.r()
				case 's':
					return cmdsMap.s()
				case 'x':
					return cmds.wait()
				default:
					return cmds.invalid({cmd})
			}
		} else if (c.length) {
			return cmds.invalid({cmd})
		}
		return
	}

	function handleBtnClick(cmd) {
		if (cmdsMap[cmd]) {
			cmdsMap[cmd]()
			cmdInputValue = getFullRegistrationCmd(cmd)
		}
	}

	$: $result.cata({
		NotAsked: () => {},
		Pending: () => (resultLoading = true),
		Ok: data => {
			resultLoading = false
			successData = {
				...data.registered,
				displayStatus: data.registered.status.split('_').join('-'),
			}
			active = false
			cmds.wait()
		},
		Failure: error => {
			resultLoading = false
			failureData = {
				message: error.message
					? error.message
					: 'A mysterious error occurred on the server.',
				status: error.status,
			}
			cmds.wait()
		},
	})
</script>

<style>
	.registration {
	}

	.success-ascii {
		min-height: 200px;
	}
</style>

<TerminalTitle>CHECK YOUR REGISTRATION</TerminalTitle>
<div {id} class="registration flex-col p-2 pt-4">
	<form on:submit|preventDefault={submit} id={formId}>
		<fieldset class="flex-1">
			<Input
				label="verification token"
				bind:value={$token}
				error={$tokenError}
				disabled={!active} />
		</fieldset>
		<div class="flex-initial pt-5">
			<div class="flex align-middle">
				<div class="flex-initial text-term-brand-2" style="min-width: 60px;">
					$ cmds:
				</div>
				<CmdButton
					cmd="s"
					type="submit"
					tabindex="1"
					on:cmd={({detail}) => handleBtnClick(detail)}
					disabled={!active || $tokenError}>
					submit
				</CmdButton>
				<CmdButton
					cmd="r"
					type="reset"
					tabindex="2"
					on:cmd={({detail}) => handleBtnClick(detail)}
					disabled={!active}>
					reset
				</CmdButton>
				<CmdButton
					cmd="x"
					tabindex="3"
					on:cmd={({detail}) => handleBtnClick(detail)}
					disabled={!active}>
					cancel
				</CmdButton>
			</div>
		</div>
	</form>
	{#if !$tokenError}
		<CmdInput
			on:cmd={({detail}) => onCmd(detail)}
			bind:value={cmdInputValue}
			active={!!active}
			{index} />
	{/if}
</div>

<section id={`result-${index}`}>
	{#if resultLoading}
		<Spinner show={true} />
	{/if}

	{#if successData}
		<div class="flex flex-col text-white pb-4">
			<SuccessOut>
				<div class="flex-initial">You are {successData.displayStatus}</div>
				{#if successData.status === 'WAIT_LISTED' && successData.orderNumber}
					<div class="flex-initial pt-1">
						You are number {successData.orderNumber} in the waiting list.
					</div>
				{/if}
			</SuccessOut>
		</div>
	{/if}

	{#if failureData}
		<FailureOut status={failureData.status} message={failureData.message} />
	{/if}
</section>
