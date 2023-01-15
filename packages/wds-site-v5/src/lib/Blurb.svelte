<script>
	import { onMount } from 'svelte';
	import { blurbStore, isWideGrid, SPEAKING, SPONSORING } from './stores/blurb-store';
	let blurbs_classes = 'blurbs';

	onMount(async () => {
		const { default: animate } = await import('animate-css-grid');
		const blurbs = document.querySelector('.blurbs');
		animate.wrapGrid(blurbs);
	});

	$: blurbs_classes = $isWideGrid ? 'blurbs blurbs-expanded' : 'blurbs';
</script>

<div class={blurbs_classes}>
	<div
		class="box"
		style="background: var(--prime); grid-area: one;"
		data-open={$blurbStore.speaking}
	>
		<div class="box-link" on:click={() => blurbStore.toggleBlurb(SPONSORING)}>
			<!-- <h2> -->
			<slot name="sponsoring-title">missing title</slot>
			{#if !$blurbStore.sponsoring}
				<!-- <p> -->
				<slot name="sponsoring-blurb">missing blurb</slot>
				<span class="read-more">Read more</span>
			{:else}
				<slot name="sponsoring-text">missing text</slot>
				<span class="show-less">Show less</span>
			{/if}
		</div>
	</div>
	<div
		class="box"
		style="background: var(--flash); grid-area: two;"
		data-open={$blurbStore.speaking}
	>
		<div class="box-link" on:click={() => blurbStore.toggleBlurb(SPEAKING)}>
			<!-- <h2> -->
			<slot name="speaking-title">missing title</slot>
			{#if !$blurbStore.speaking}
				<!-- <p> -->
				<slot name="speaking-blurb">missing blurb</slot>
				<span class="read-more">Read more</span>
			{:else}
				<slot name="speaking-text">missing text</slot>
				<span class="show-less">Show less</span>
			{/if}
		</div>
	</div>
</div>

<style>
	.blurbs {
		display: grid;
		grid-row-gap: 1em;
		grid-template-areas:
			'one'
			'two';
	}

	.box {
		padding: 2em;
		display: flex;
		flex-direction: column;
		border: 1px solid var(--brand-primary);
		border-radius: var(--term-border-radius);
		box-shadow: var(--term-box-shadow);
	}

	.box .box-link {
		@apply text-xl p-0 text-white text-left border-none;
	}

	.box :global(h2) {
		@apply text-xl p-0 text-white text-left lowercase text-term-brand-1;
		margin: 0 0 0.5em 0;
	}

	.box .read-more,
	.box .show-less {
		display: block;
		position: relative;
		text-align: right;
		margin-top: auto;
		padding: 0 1.2em 0 0;
		border-radius: var(--term-border-radius);
		color: #fff;
	}

	.box:hover {
		cursor: pointer;
	}

	.box:hover .read-more,
	.box:hover .show-less {
		color: white;
		text-decoration: underline;
	}

	.box .read-more::after,
	.box [data-open='false']::after,
	.box .show-less::after,
	.box [data-open='true']::after {
		content: '';
		position: absolute;
		display: block;
		right: 0;
		top: 0.15em;
		width: 1em;
		height: 1em;
	}

	.box .show-less::after,
	.box [data-open='true']::after {
		background: url(/icons/arrow-up.svg);
	}

	.box .read-more::after,
	.box [data-open='false']::after {
		background: url(/icons/arrow-down.svg);
	}

	.box .show-less::after,
	.box [data-open='true']::after {
		content: '';
		position: absolute;
		display: block;
		right: 0;
		top: 0.15em;
		width: 1em;
		height: 1em;
		background: url(/icons/arrow-up.svg);
	}

	@media (min-width: 1000px) {
		.blurbs {
			grid-column-gap: 1em;
			grid-row-gap: 1em;
			grid-template-columns: repeat(2, 1fr);
			grid-template-areas: 'one two';
		}
		.blurbs-expanded {
			grid-template-columns: repeat(1, 1fr);
			grid-template-areas:
				'one'
				'two';
		}
	}

	.blurbs-expanded :global(.blurb-image) {
		display: none;
	}

	:global(.tint) {
		position: relative;
		float: left;
	}

	:global(.tint:before) {
		content: '';
		display: block;
		position: absolute;
		top: 0;
		bottom: 0;
		left: 0;
		right: 0;
		background: var(--brand-secondary);
		opacity: 0.6;
		transition: all 0.3s linear;
		z-index: 100;
	}
	:global(.tint:hover:before) {
		background: none;
	}

	:global(.tint > img) {
		filter: saturate(2);
	}

	:global(.tint:hover > img) {
		filter: saturate(1);
	}
</style>
