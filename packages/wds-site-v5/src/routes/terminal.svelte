<script>
	import { Terminal, SsrTerminal } from '$lib/terminal';
	import { browser } from '$app/environment';
	export let event;
</script>

<div class="terminal-wrapper">
	{#if browser}
		<Terminal {event} />
		<!-- TODO: handle no event placeholder ssr -->
	{:else if event && event.volume}
		<SsrTerminal {event} />
	{/if}
</div>
