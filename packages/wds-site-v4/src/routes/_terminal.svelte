<script>
	import {Terminal} from '@webdevandsausages/event-terminal'
	import SsrTerminal from '@webdevandsausages/event-terminal/src/components/SsrTerminal.svelte'
	export let event
</script>

<div class="terminal-wrapper">
	{#if process.browser}
		<Terminal {event} />
	{:else}
		<SsrTerminal {event} />
	{/if}
</div>
