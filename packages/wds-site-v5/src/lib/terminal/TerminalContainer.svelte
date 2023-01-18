<script>
	import { setContext, tick } from 'svelte';
	import Controls from './Controls.svelte';
	import { controlsStore as store } from './stores/controlsStore';
	import { terminalStore } from './stores/terminalStore';
	let containerClass = 'centered';

	async function scroll() {
		await tick();
		const lastId = `history-${$terminalStore.history.length - 1}`;
		const elem = document.getElementById(lastId);
		if (elem) {
			elem.scrollIntoView({
				block: 'nearest',
				bahavior: 'smooth'
			});
		}
	}

	$: $terminalStore.history.length && $terminalStore.history.length > 1 && scroll();

	setContext('controlsStore', store);

	$: containerClass = $store.open ? 'full' : 'centered terminal-centered-return';
</script>

<span class="terminal-wrapper-{containerClass}">
	<div class="terminal terminal-{containerClass}" data-simplebar data-simplebar-auto-hide="true">
		<div class="controls-container absolute">
			<Controls />
		</div>
		<div id="term" class="screen p-6 text-term-text bg-term-background">
			<slot name="details">No current event</slot>
			<slot name="output" class="block" />
		</div>
	</div>
</span>

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

	:global(div.simplebar-track.simplebar-vertical > div) {
		max-height: 590px !important;
	}

	:global(div.simplebar-scrollbar):before {
		background: var(--term-brand-secondary);
	}

	:global(.sidebar-scrollbar) {
		width: 10px;
	}

	:global(.simplebar-track .simplebar-scrollbar.simplebar-visible):before {
		opacity: 0.7;
	}

	:global(.simplebar-content-wrapper),
	:global(.simplebar-mask) {
		border-radius: var(--term-border-radius);
	}

	.terminal-wrapper-full :global(.simplebar-content-wrapper),
	.terminal-wrapper-full :global(.simplebar-mask) {
		border-radius: 0;
	}

	.terminal-full {
		animation: expand 0.3s linear;
		@apply fixed top-0 left-0 z-10 w-full h-full;
	}

	.terminal-centered {
		border: var(--term-border-width) solid var(--term-border-color);
		box-shadow: var(--term-box-shadow);
		border-radius: var(--term-border-radius);
		position: relative;
		height: 675px;
	}

	.terminal-centered .screen {
		min-height: 675px;
	}

	.terminal-centered-return {
		animation: retract 0.3s ease-in;
	}

	.terminal-centered .screen,
	.terminal-centered-return .screen {
		border-radius: var(--term-border-radius);
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
