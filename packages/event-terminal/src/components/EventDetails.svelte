<script>
	import {getContext} from 'svelte'
	import Spinner from './Spinner.svelte'
	import TerminalOut from './TerminalOut.svelte'

	let result
	let error
	let loading

	const event = getContext('eventStore')

	$: $event.cata({
		NotAsked: () => {},
		Pending: () => 'loading',
		Ok: data => {
			result = data
		},
		Failure: err => {
			error = err
		},
	})
</script>

<div class="font-term">
	{#if loading}
	<Spinner show={true} />
	{:else if result}
	<div class="pl-5 pr-5 pt-2">
		<h2 class="term-title">~ VOLUME {result.volume} ~</h2>
		<TerminalOut title="When" detail={result.date} />
		<TerminalOut title="What" detail={result.details} html />
		<TerminalOut title="Where" detail={result.location} />
		<TerminalOut title="Contact" detail={result.contact} html />
	</div>
	{:else if error}
	<pre>{error}</pre>
	{/if}
</div>
