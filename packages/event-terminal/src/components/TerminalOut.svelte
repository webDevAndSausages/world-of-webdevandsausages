<script>
	export let title = ''
	export let detail = '-'
	export let html = false
</script>

<div class="terminal-out">
	<h3 class="p-2 text-lg lowercase text-term-brand-2">
		$ {title}
	</h3>
	{#if html}
	<p class="p-2 text-term-output">{@html detail}</p>
	{:else}
	<p class="p-2 text-term-output">{detail}</p>
	{/if}
</div>
