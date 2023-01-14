<script>
	import {getContext} from 'svelte'
	import Spinner from './Spinner.svelte'
	import TerminalOut from './TerminalOut.svelte'
	import TerminalTitle from './TerminalTitle.svelte'
	import CmdInput from './CmdInput.svelte'
	import MailTo from './MailTo.svelte'
	import EventDownload from './EventDownload.svelte'
	import {showForStatusOf} from './utils'

	let result
	let error
	let loading
	let showNoEvent
	let showVisibleStatusCmdInput = false

	const event = getContext('eventStore')

	$: $event.cata({
		None: () => {
			loading = false
			showNoEvent = true
		},
		Pending: () => (loading = true),
		Ok: data => {
			result = data
			loading = false
		},
		Failure: err => {
			error = err
			loading = false
		},
	})

	$: showVisibleStatusCmdInput = showForStatusOf('VISIBLE')(
		$event.okOrNull($event)
	)
</script>

<div class="font-term">
	{#if loading}
		<Spinner show={true} />
	{:else if result}
		<div class="event-header flex flex-row justify-between">
			<TerminalTitle>VOLUME {result.volume}</TerminalTitle>
			<EventDownload event={result} />
		</div>
		<TerminalOut title="When" detail={result.date} />
		<TerminalOut title="What" detail={result.details} html />
		<TerminalOut title="Where" detail={result.location} />
		<TerminalOut title="Contact" detail={result.contact} html />
		{#if showVisibleStatusCmdInput}
			<div class="p-2">
				<CmdInput
					value="sudo kill -9 all other plans"
					disabled={true}
					on:cmd={null}
					active={false}
					index={0}
					showReadOnlyCursor />
			</div>
		{/if}
	{:else if showNoEvent || error}
		<TerminalTitle>THE NEXT VOLUME</TerminalTitle>
		<TerminalOut title="What" detail="The next volume is in the works..." />
		<div class="terminal-out">
			<h3 class="p-2 text-lg lowercase text-term-brand-2">$ Help out</h3>
			<p class="p-2 text-term-output">
				Get involved by sponsoring, organizing, or speaking. Contact
				<MailTo email="richard.vancamp@gmail.com">Rick</MailTo>
				to get started.
			</p>
		</div>
		<div class="p-2">
			<CmdInput
				value="sudo reboot"
				disabled={true}
				on:cmd={null}
				active={false}
				index={0}
				showReadOnlyCursor />
		</div>
	{/if}
</div>
