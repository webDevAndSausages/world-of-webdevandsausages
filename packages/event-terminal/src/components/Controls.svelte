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
		background: red;
		opacity: 0.8;
		position: relative;
		top: -10px !important;
		left: 1px !important;
	}

	.v-line {
		width: 2px;
		height: 16px;
		background: red;
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

<div class="term-controls">
	<div class="term-expander" on:click="{store.actions.toggleExpander}">
		<div class="circle"></div>
		<div class="h-line"></div>
		{#if $store.open}
		<div class="v-line"></div>
		{/if}
	</div>
</div>
