<script>
	import { createEventDispatcher } from 'svelte';
	import { pick } from 'ramda';
	export let cmd;
	export let disabled = false;
	// export let style = ''
	export let type = 'button';

	const dispatch = createEventDispatcher();
</script>

<button
	class="term text-md text-term-brand-2"
	on:click|preventDefault={() => dispatch('cmd', cmd)}
	class:disabled
	class:active={!disabled}
	{type}
>
	[
	<span class="text-term-brand-1">{cmd}</span>
	]
	<slot />
</button>

<style>
	button.term {
		cursor: pointer;
		display: inline-block;
		letter-spacing: 0.075em;
		padding: 0.25rem 0.5rem;
		margin: -0.2em 1em 1em;
		position: relative;
		align-self: center;
		z-index: 1;
	}

	button.term {
		border: 1px var(--term-brand-primary) solid;
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 100%
		);
		border-image-slice: 1 1 0 0;
		box-shadow: -0.5em 0.5em rgba(16, 24, 50, 0);
		transform-origin: left bottom;
	}

	button.term:before,
	button.term:after {
		border: 1px var(--term-brand-primary) solid;
		content: '';
		display: block;
		position: absolute;
		z-index: -1;
	}

	button.term:before {
		border-image: linear-gradient(45deg, var(--term-brand-primary) 0%, #0097dd 100%);
		border-image-slice: 1 1 1 1;
		left: -0.28em;
		top: 0.094em;
		width: 0.33em;
		height: 101%;
		transform: skewY(-45deg);
	}

	button.term:after {
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 100%
		);
		border-image-slice: 1 1 1 0;
		bottom: -0.27em;
		right: 0.092em;
		width: 102%;
		height: 0.33em;
		transform: skewX(-45deg);
	}

	button.term.active:hover {
		background-size: 90%;
		transform: translate(0.2em, -0.2em);
		box-shadow: -1em 1em 0.15em rgba(16, 24, 50, 0.1);
	}

	button.term.active:hover::after {
		background-size: 100%;
		background-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 101%
		);
		width: 101%;
		border-image-slice: 1;
	}

	button.term.active:hover::before {
		background-size: 100%;
		background-image: linear-gradient(45deg, var(--term-brand-primary) 0%, #0097dd 102%);
		height: 102%;
		border-image-slice: 1;
	}

	button.term.disabled {
		cursor: not-allowed;
		opacity: 0.6;
	}
</style>
