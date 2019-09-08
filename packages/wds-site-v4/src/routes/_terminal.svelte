<script>
  import {Terminal} from '@webdevandsausages/event-terminal'

</script>

<div class="terminal-wrapper">
	{#if process.browser}
		<Terminal event={null} />
	{/if}
</div>