<script>
	import {createEventStore} from './eventStore.js'
	import Spinner from './Spinner.svelte'
	let result
	let error
	let loading
	// if the event is not passed a prop
	// the store will fetch it
	export let eventData = null
	const event = createEventStore(eventData)

	$: $event.cata({
		NotAsked: () => {},
		Pending: () => 'loading',
		Ok: data => {
			result = JSON.stringify(data, null, 2)
		},
		Failure: err => {
			error = err
		},
	})
</script>

<div>
	{#if loading}
	<Spinner show={true} />
	{:else if result}
	<pre>{result}</pre>
	{:else if error}
	<pre>{error}</pre>
	{/if}
</div>
