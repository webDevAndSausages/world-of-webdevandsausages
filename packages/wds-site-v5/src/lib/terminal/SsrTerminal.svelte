<script>
	import TerminalTitle from './TerminalTitle.svelte'
	import TerminalOut from './TerminalOut.svelte'
	import {formatDate} from './utils'

	export let event = null
	let formattedDate = ''
	$: formattedDate = event && event.date ? formatDate(event.date) : ''
</script>

<style>
	.terminal-centered {
		position: relative;
		height: 600px;
	}

	.screen {
		border: var(--term-border-width) solid var(--term-border-color);
		box-shadow: 0 10px 20px rgba(2, 1, 1, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
		border-radius: 8px;
		height: 450px;
	}
</style>

{#if event}
	<div class="terminal-centered">
		<div id="term" class="screen p-6 text-term-text bg-term-background">
			<TerminalTitle>VOLUME {event.volume || '?'}</TerminalTitle>
			<TerminalOut title="When" detail={formattedDate} />
			<TerminalOut title="What" detail={event.details} html />
			<TerminalOut title="Where" detail={event.location} />
			<TerminalOut title="Contact" detail={event.contact} html />
		</div>
	</div>
{/if}
