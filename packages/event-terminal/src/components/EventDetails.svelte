<script>
	import {event} from './eventStore.js'
	let result
	let error
	let loading
	$: $event.cata({
		NotAsked: () => {},
		Pending: () => 'loading',
		Ok: data => {
			result = JSON.stringify(data, null, 2)
		},
		Failure: error => {
			error = error
		},
	})
</script>

<div>
	{#if loading}
	<p>...loading</p>
	{:else if result}
	<pre>{result}</pre>
	{:else if error}
	<pre>{error}</pre>
	{/if}
</div>
