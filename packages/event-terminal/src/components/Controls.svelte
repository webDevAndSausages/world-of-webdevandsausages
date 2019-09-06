<script>
	import {getContext} from 'svelte'

	const store = getContext('controlsStore')
</script>

<style>
	.term-expander {
		height: 20px;
		width: 20px;
	}

	.term-expander:hover .h-line,
	.term-expander:hover .v-line {
		opacity: 1;
	}

	.h-line {
		width: 16px;
		height: 2px;
		opacity: 0.8;
		position: relative;
		top: -10px !important;
		left: 1px !important;
	}

	.v-line {
		width: 2px;
		height: 16px;
		opacity: 0.8;
		position: relative;
		top: -19px !important;
		left: 8px !important;
	}

	.circle {
		height: 16px;
		width: 16px;
		border-radius: 50px;
	}
</style>

<div class="term-controls p-1">
	<div class="term-expander" on:click={store.actions.toggleExpander}>
		<div class="circle" />
		<div class="h-line bg-term-brand-2" />
		{#if $store.open}
			<div class="v-line bg-term-brand-2" />
		{/if}
	</div>
</div>
