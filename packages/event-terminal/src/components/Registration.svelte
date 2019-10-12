<script>
	import {getContext, tick, onMount} from 'svelte'
	import {writable} from 'svelte/store'
	// components
	import Input from './Input.svelte'
	import Spinner from './Spinner.svelte'
	import FormButtons from './FormButtons.svelte'
	import CmdInput from './CmdInput.svelte'
	import TerminalTitle from './TerminalTitle.svelte'
	import SuccessOut from './SuccessOut.svelte'
	import FailureOut from './FailureOut.svelte'
	// api request
	import {apiPost} from './utils/request'
	import api from './api'
	import {Result} from './models/Result'
	// stores
	import {
		createFormValuesStore,
		createValidationStore,
		initialState,
		successAsciiStore,
	} from './stores/registrationStore'
	import {REGISTER, SUBMIT, CANCEL} from './constants'

	const validCmds = [REGISTER, SUBMIT, CANCEL]

	import {getFullFormCmd, normalizeCmd} from './utils'

	const formValuesStore = createFormValuesStore()
	const validationStore = createValidationStore(formValuesStore)

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
	let scrollAnchorId = `scroll-anchor-${index}`
	let blurred = true

	let eventId = $event.okOrNull($event).id
	async function submit(_ev) {
		if (!eventId) return
		$result = await apiPost(api.register(eventId), $formValuesStore.values)
	}

	async function scroll() {
		await tick()
		const elem = document.getElementById(scrollAnchorId)
		if (elem) {
			elem.scrollIntoView({
				block: 'nearest',
				bahavior: 'smooth',
			})
		}
	}
	// any props which make cause something to appear in the terminal
	// include here to trigger a scroll
	$: ($result || $validationStore.isValid) && scroll()

	$: values = $formValuesStore[index]

	function onCmd(cmd) {
		const c = cmd && cmd.length ? normalizeCmd(cmd) : ''
		if (c.length) {
			switch (c) {
				case REGISTER: {
					$formValuesStore.validationOff = true
					cmds.reset({component: 'Registration'})
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

	function handleBtnClick(cmd) {
		if (validCmds.includes(cmd)) {
			onCmd(cmd)
			cmdInputValue = getFullFormCmd(cmd)
		}
	}

	function getMsg() {
		// must be manual, with  autosubribe $, the whole message is created before rendering
		successAsciiStore.subscribe(val => (success = val))
		setTimeout(() => cmds.wait(), 3000)
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
			if (successData.status === 'REGISTERED') {
				getMsg()
			} else {
				cmds.wait()
			}
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

<style>
	.success-ascii {
		min-height: 400px;
	}
</style>

<section {id}>
	<TerminalTitle>REGISTRATION</TerminalTitle>

	<div class="registration flex-col p-2 pt-4">
		<form>
			<fieldset class="flex-1">
				<Input
					label="email"
					type="email"
					bind:value={$formValuesStore.values.email}
					error={$validationStore.errors.email}
					disabled={!active}
					on:focused={onFocusChange} />
			</fieldset>
			<fieldset class="flex-1">
				<Input
					label="first name"
					bind:value={$formValuesStore.values.firstName}
					error={$validationStore.errors.firstName}
					disabled={!active}
					on:focused={onFocusChange} />
			</fieldset>
			<fieldset class="flex-1">
				<Input
					label="last name"
					bind:value={$formValuesStore.values.lastName}
					error={$validationStore.errors.lastName}
					disabled={!active}
					on:focused={onFocusChange} />
			</fieldset>
			<fieldset class="flex-1">
				<Input
					label="affiliation"
					bind:value={$formValuesStore.values.affiliation}
					error={$validationStore.errors.affiliation}
					disabled={!active}
					on:focused={onFocusChange} />
			</fieldset>
			<FormButtons
				handleClick={handleBtnClick}
				submitDisabled={!$validationStore.isValid}
				readOnly={!active} />
		</form>

		{#if $validationStore.isValid}
			<CmdInput
				on:cmd={({detail}) => onCmd(detail)}
				bind:value={cmdInputValue}
				active={!!active}
				{index} />
		{/if}
	</div>

	<div id={`result-${index}`}>
		{#if resultLoading}
			<Spinner show={true} style="padding-left:10px" />
		{:else if successData}
			<div class="flex flex-col text-white pb-8">
				<SuccessOut>
					<div class="flex-initial">You are {successData.displayStatus}</div>
					{#if successData.status === 'WAIT_LISTED' && successData.orderNumber}
						<div class="flex-initial pt-1">
							You are number {successData.orderNumber} in the waiting list.
						</div>
					{/if}
					<!-- z-index hack so text is copyable with negative margin on ascii printout -->
					<div class="flex-initial pt-1 z-10">
						Your verification token:
						<span class="text-term-brand-2 pl-2">
							{successData.verificationToken}
						</span>
					</div>
				</SuccessOut>
				{#if successData.status === 'REGISTERED'}
					<div
						class="flex w-full justify-center success-ascii text-term-brand-1
						-m-6 relative">
						<pre>{success}</pre>
					</div>
				{/if}
			</div>
		{:else if failureData}
			<FailureOut status={failureData.status} message={failureData.message} />
		{/if}
	</div>
	<div id={scrollAnchorId} style="height: 1px" />
</section>
