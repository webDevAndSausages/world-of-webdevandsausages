<script>
	import {getContext, onMount, onDestroy, tick} from 'svelte'
	import {writable} from 'svelte/store'
	// api
	import api from './api'
	import {apiDelete} from './utils/request'
	// components
	import Input from './Input.svelte'
	import Spinner from './Spinner.svelte'
	import FormButtons from './FormButtons.svelte'
	import CmdInput from './CmdInput.svelte'
	import TerminalTitle from './TerminalTitle.svelte'
	import FailureOut from './FailureOut.svelte'
	import SuccessOut from './SuccessOut.svelte'
	// model & store
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

	let result = writable(Result.None)
	let resultLoading = null
	let successData = null
	let failureData = null
	let success = ''
	let cmdInputValue = ''
	let formId = `cancellation-${index}`

	let eventId = $event.okOrNull($event).id

	async function submit(_ev) {
		if (!eventId) return
		$result = await apiDelete(api.cancelRegistration($token.trim()))
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
		None: () => {},
		Pending: () => (resultLoading = true),
		Ok: data => {
			resultLoading = false
			successData = data
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

<TerminalTitle>CANCEL YOUR REGISTRATION</TerminalTitle>
<div {id} class="cancellation flex-col p-2 pt-4">
	<form on:submit|preventDefault={submit} id={formId}>
		<fieldset class="flex-1">
			<Input
				label="verification token"
				bind:value={$token}
				error={$tokenError}
				disabled={!active} />
		</fieldset>
		<FormButtons
			handleClick={handleBtnClick}
			submitDisabled={$tokenError}
			readOnly={!active} />
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
				<div class="flex-initial">
					Your registration is {successData.status}.
				</div>
			</SuccessOut>
		</div>
	{/if}

	{#if failureData}
		<FailureOut status={failureData.status} message={failureData.message} />
	{/if}
</section>
