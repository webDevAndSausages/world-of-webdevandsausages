<script>
	import {getContext} from 'svelte'
	import {writable} from 'svelte/store'
	import Input from './Input.svelte'
	import CmdButton from './CmdButton.svelte'
	import ky from 'ky'
	import config from './config'
	import api from './api'
	import {Result} from './models/Result'
	import {
		registrationStore,
		validationStore,
		initialState,
	} from './stores/registrationStore'

	export let active

	let result = writable(Result.NotAsked)
	let resultLoading = null
	let successData = null
	let failureMsg = null
	let failureStatus = null

	let event = getContext('eventStore')
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

	const cmds = {
		r: () => {
			document.getElementById('registration').reset()
		},
		s: submit,
	}

	function handleBtnClick(cmd) {
		cmds[cmd] && cmds[cmd]()
	}

	$: $result.cata({
		NotAsked: () => {},
		Pending: () => (resultLoading = true),
		Ok: data => {
			resultLoading = false
			successData = data
			active = false
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
</script>

<style>
	.registration {
	}
</style>

<form
	on:submit|preventDefault="{submit}"
	class="registration flex-col m-5"
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
				disabled="{!active}"
			>
				save
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
				close
			</CmdButton>
		</div>
	</div>
</form>
<div style="color:white;">
	{#if resultLoading} loading... {/if} {#if successData}
	<pre>{JSON.stringify(successData, null, 2)}</pre>
	{/if} {#if failureMsg} {failureStatus}: {failureMsg} {/if}
</div>
