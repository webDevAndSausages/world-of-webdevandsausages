<script>
	import {setContext, tick} from 'svelte'
	import Controls from './Controls.svelte'
	import {controlsStore as store} from './stores/controlsStore'
	import {terminalStore} from './stores/terminalStore'
	let containerClass = 'centered'

	async function scroll() {
		await tick()
		const lastId = `history-${$terminalStore.history.length - 1}`
		const elem = document.getElementById(lastId)
		if (elem) {
			elem.scrollIntoView({
        start: 'block',
        bahavior: 'smooth'
      })
		}
	}

	$: $terminalStore.history.length &&
		$terminalStore.history.length > 1 &&
		scroll()

	setContext('controlsStore', store)

	$: containerClass = $store.open ? 'full' : 'centered terminal-centered-return'
</script>

<style>
	.terminal-full {
		@apply fixed top-0 left-0 z-10 w-full h-full;
		transform: scale(1);
		transition: all 1s;
		overflow: auto;
	}

	.terminal-centered {
		margin: 20px 10%;
		max-height: 600px;
	}

	@media (max-width: 650px) {
		.terminal-centered {
			margin: 20px 2%;
		}
	}

	.terminal-centered-return {
		transition: all 1s;
	}

	.screen {
		border: var(--term-border-width) solid var(--term-border-color);
		box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
		border-radius: 8px;
	}

	.container-centered .screen {
		height: 100%;
	}

	.terminal-full .screen {
		border-radius: 0;
		min-height: 100vh;
	}

	.controls-container {
		top: 7px;
		left: 7px;
	}
</style>

<div
	class="terminal-{containerClass} relative"
	data-simplebar
	data-simplebar-auto-hide="false">
	<div class="controls-container absolute">
		<Controls />
	</div>
	<div id="term" class="screen p-6 text-term-text bg-term-base">
		<slot name="details">No current event</slot>
		<slot name="output" class="block" />
	</div>
</div>
