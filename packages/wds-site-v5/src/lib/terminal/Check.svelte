<script>
	import {getContext, tick, onMount} from 'svelte'
	import {writable} from 'svelte/store'
	// api call
	import api from './api'
	import {apiGet} from './utils/request'
	// components
	import Input from './Input.svelte'
	import Spinner from './Spinner.svelte'
	import CmdInput from './CmdInput.svelte'
	import TerminalTitle from './TerminalTitle.svelte'
	import FailureOut from './FailureOut.svelte'
	import SuccessOut from './SuccessOut.svelte'
	import FormButtons from './FormButtons.svelte'
	import {CANCEL, SUBMIT, RESET} from './constants'
	// models & stores
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
	let formId = `check-${index}`
	let blurred = true

	let eventId = $event.okOrNull($event).id

	async function submit(_ev) {
		if (!eventId) return
		$result = await apiGet(
			api.checkRegistration(eventId, $tokenStore.token.trim())
		)
	}

	function onCmd(cmd) {
		const c = cmd && cmd.length ? normalizeCmd(cmd) : ''
		if (c.length) {
			switch (c) {
				case RESET: {
					$tokenStore.validationOff = true
					cmds.reset({component: 'Check'})
					return
				}
				case SUBMIT:
					return submit()
				case CANCEL:
					return cmds.wait()
				default:
					return cmds.invalid({cmd})
			}
		} else if (c.length) {
			return cmds.invalid({cmd})
		}
		return
	}

	const validCmds = [RESET, SUBMIT, CANCEL]

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

	function onFocusChange(event) {
		blurred = !event.detail
	}

	// if the form is blurred and active you can
	// execute commands with key press
	function handleKeyPress(e) {
		if (active && blurred && validCmds.includes(e.key.toLowerCase())) {
			cmdInputValue = e.key
			onCmd(e.key)
		}
	}

	onMount(() => {
		document.addEventListener('keypress', handleKeyPress)
		return () => {
			document.removeEventListener('keypress', handleKeyPress)
		}
	})
</script>

<section {id}>
	<TerminalTitle>CHECK YOUR REGISTRATION</TerminalTitle>
	<div class="registration flex-col p-2 pt-4">
		<form id={formId}>
			<fieldset class="flex-1">
				<Input
					label="verification token"
					bind:value={$tokenStore.token}
					error={$tokenError}
					disabled={!active}
					on:focused={onFocusChange} />
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
	</div>
</section>
