<script>
	import {getContext, tick} from 'svelte'
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
	import {getFullFormCmd, normalizeCmd} from './utils'

	const tokenStore = createTokenStore()
	const tokenError = createTokenValidationStore(tokenStore)

	const {cmds} = getContext('terminalStore')
	const event = getContext('eventStore')

	export let active = true
	export let index
	export let id

	async function scroll() {
		await tick()
		const elem = document.getElementById(id)
		if (elem) {
			elem.scrollIntoView({
				block: 'nearest',
				bahavior: 'smooth',
			})
		}
	}
	// any props which make cause something to appear in the terminal
	// include here to trigger a scroll
	$: ($result || $tokenError) && scroll()

	let result = writable(Result.None)
	let resultLoading = null
	let successData = null
	let failureData = null
	let success = ''
	let cmdInputValue = ''
	let formId = `cancellation-${index}`
	let formEl

	let eventId = $event.okOrNull($event).id

	async function submit(_ev) {
		if (!eventId) return
		$result = await apiDelete(api.cancelRegistration($tokenStore.token.trim()))
	}

	function onCmd(cmd) {
		const c = cmd && cmd.length ? normalizeCmd(cmd) : ''
		if (c.length) {
			switch (c) {
				case 'r': {
					$tokenStore.validationOff = true
					cmds.reset({component: 'Cancellation'})
					return
				}
				case 's':
					return submit()
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

	const validCmds = ['r', 's', 'x']

	function handleBtnClick(cmd) {
		if (validCmds.includes(cmd)) {
			onCmd(cmd)
			cmdInputValue = getFullFormCmd(cmd)
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

<section {id}>
	<TerminalTitle>CANCEL YOUR REGISTRATION</TerminalTitle>
	<div {id} class="cancellation flex-col p-2 pt-4">
		<form id={formId}>
			<fieldset class="flex-1">
				<Input
					label="verification token"
					bind:value={$tokenStore.token}
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

	<div id={`result-${index}`}>
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
	</div>
</section>
