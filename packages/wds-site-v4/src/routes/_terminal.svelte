<script>
	import {Terminal} from '@webdevandsausages/event-terminal'
	export let event
</script>

<div class="terminal-wrapper">
	{#if process.browser}
		<Terminal {event} />
	{/if}
</div>
