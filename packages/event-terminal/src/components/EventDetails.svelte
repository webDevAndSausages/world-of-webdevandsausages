<script>
	import {getContext} from 'svelte'
	import Spinner from './Spinner.svelte'
	import TerminalOut from './TerminalOut.svelte'
	import TerminalTitle from './TerminalTitle.svelte'

	let result
	let error
	let loading

	const event = getContext('eventStore')

	$: $event.cata({
		NotAsked: () => {},
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
</script>

<div class="font-term">
	{#if loading}
		<Spinner show={true} />
	{:else if result}
		<TerminalTitle>VOLUME {result.volume}</TerminalTitle>
		<TerminalOut title="When" detail={result.date} />
		<TerminalOut title="What" detail={result.details} html />
		<TerminalOut title="Where" detail={result.location} />
		<TerminalOut title="Contact" detail={result.contact} html />
	{:else if error}
		<pre>{error}</pre>
	{/if}
</div>
