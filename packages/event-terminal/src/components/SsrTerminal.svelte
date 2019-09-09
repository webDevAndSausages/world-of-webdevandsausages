<script>
	import {setContext, tick} from 'svelte'
	import TerminalTitle from './TerminalTitle.svelte'
	import TerminalOut from './TerminalOut.svelte'
	import Spinner from './Spinner.svelte'

	export let event = null
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

	.terminal-centered {
		position: relative;
		max-height: 600px;
	}

	.terminal-centered-return {
		animation: retract 0.3s ease-in;
	}

	.screen {
		border: var(--term-border-width) solid var(--term-border-color);
		box-shadow: 0 10px 20px rgba(2, 1, 1, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
		border-radius: 8px;
		height: 450px;
	}

	.container-centered .screen {
		height: 100%;
	}
</style>

<div class="terminal-centered">
	<div id="term" class="screen p-6 text-term-text bg-term-base">
		{#if !event}
			<Spinner show={true} />
		{:else}
			<TerminalTitle>VOLUME {event.volume || '?'}</TerminalTitle>
			<TerminalOut title="When" detail={event.date} />
			<TerminalOut title="What" detail={event.details} html />
			<TerminalOut title="Where" detail={event.location} />
			<TerminalOut title="Contact" detail={event.contact} html />
		{/if}
	</div>
</div>
