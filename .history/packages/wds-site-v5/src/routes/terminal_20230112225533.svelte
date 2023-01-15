<script>
	import {Terminal} from '@webdevandsausages/event-terminal'
	import SsrTerminal from '@webdevandsausages/event-terminal/src/components/SsrTerminal.svelte'
  import {browser} from '$app/env'
	export let event
</script>

<div class="terminal-wrapper">
	{#if browser}
		<Terminal {event} />
		<!-- TODO: handle no event placeholder ssr -->
	{:else if event && event.volume}
		<SsrTerminal {event} />
	{/if}
</div>
