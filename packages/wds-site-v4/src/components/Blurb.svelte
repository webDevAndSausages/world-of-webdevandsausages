<script>

</script>

<style>
	.blurbs {
		display: grid;
		grid-row-gap: 1em;
		grid-template-areas:
			'one'
			'two';
	}

	.box {
		padding: 1em;
		display: flex;
		flex-direction: column;
		border: 1px solid var(--brand-primary);
		border-radius: var(--term-border-radius);
		box-shadow: var(--term-box-shadow);
	}

	.box a {
		@apply text-xl p-0 text-white text-left border-none;
	}

	.box h2 {
		@apply text-xl p-0 text-white text-left uppercase;
		margin: 0 0 0.5em 0;
	}

	.box .learn-more {
		display: block;
		position: relative;
		text-align: right;
		margin-top: auto;
		padding: 0 1.2em 0 0;
		border-radius: var(--term-border-radius);
	}

	.box:hover {
		cursor: pointer;
	}

	.box:hover .learn-more {
		color: white;
		text-decoration: underline;
	}

	.box .learn-more::after,
	.box a::after {
		content: '';
		position: absolute;
		display: block;
		right: 0;
		top: 0.15em;
		width: 1em;
		height: 1em;
		background: url(/icons/arrow-right.svg);
	}

	@media (min-width: 1000px) {
		.blurbs {
			grid-column-gap: 1em;
			grid-row-gap: 1em;
			grid-template-columns: repeat(2, 1fr);
			grid-template-areas: 'one two';
		}
	}
</style>

<div class="blurbs">
	<div class="box" style="background: var(--prime); grid-area: one;">
		<a>
			<h2>Sponsoring</h2>
			<p>here</p>
			<span class="learn-more">learn more</span>
		</a>
	</div>
	<div class="box" style="background: var(--flash); grid-area: two;">
		<a>
			<h2>Speaking</h2>
			<p>here</p>
			<span class="learn-more">learn more</span>
		</a>
	</div>
</div>
