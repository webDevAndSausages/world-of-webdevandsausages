<script>
	import {setContext, tick} from 'svelte'
	import Controls from './Controls.svelte'
	import {controlsStore as store} from './stores/controlsStore'
	import {terminalStore} from './stores/terminalStore'
	let containerClass = 'centered'

	// TODO: should the terminal size be constrained?
	/* async function scroll() {
		await tick()
		const lastId = `history-${$terminalStore.history.length - 1}`
		const elem = document.getElementById(lastId);
		if(elem) {
			elem.scrollIntoView()
		}
	}

	$: $terminalStore.history.length && $terminalStore.history.length > 1 && scroll()
	*/

	setContext('controlsStore', store)

	$: containerClass = $store.open ? 'full' : 'centered terminal-centered-return'
</script>

<style>
	.terminal-centered {
		/* max-height: 900px;
		overflow-x: scroll; */
	}
	.controls-container {
		top: 7px;
		left: 7px;
	}
</style>

<div class="terminal-{containerClass} relative">
	<div class="controls-container absolute">
		<Controls />
	</div>
	<div id="term" class="screen p-6 text-term-text bg-term-base">
		<slot name="details">No current event</slot>
		<slot name="output" />
	</div>
</div>
