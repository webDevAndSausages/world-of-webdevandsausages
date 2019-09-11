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
				block: 'nearest',
				bahavior: 'smooth',
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
	@keyframes expand {
		from {
			transform: scale3d(0.9, 0.9, 0.9);
		}
	}

	@keyframes retract {
		from {
			transform: scale3d(1.02, 1.02, 1.02);
		}
		50% {
			transform: scale3d(0.98, 0.98, 0.98);
		}
		to {
			transform: scale3d(1, 1, 1);
		}
	}

	.terminal-full {
		animation: expand 0.3s linear;
		@apply fixed top-0 left-0 z-10 w-full h-full;
	}

	.terminal-centered {
		position: relative;
		height: 600px;
	}

	.terminal-centered-return {
		animation: retract 0.3s ease-in;
	}

	.screen {
		border: var(--term-border-width) solid var(--term-border-color);
		box-shadow: 0 10px 20px rgba(2, 1, 1, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
		border-radius: 8px;
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
	class="terminal-{containerClass}"
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
