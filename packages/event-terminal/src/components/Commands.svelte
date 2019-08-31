<script>
	import {getContext} from 'svelte'
	import {showForStatusOf} from './utils'
	import {Result} from './models/Result'

	const event = getContext('eventStore')

	let commandButtons = [
		{
			text: 'register',
			cmd: 'r',
			clickHandler: () => {},
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST'),
		},
		{
			text: 'cancel',
			cmd: 'x',
			clickHandler: () => {},
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL'),
		},
		{
			text: 'check',
			cmd: 'c',
			clickHandler: () => {},
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL'),
		},
		{
			text: 'help',
			cmd: 'h',
			clickHandler: () => {},
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL'),
		},
	]

	$: visibleCmdButtons = $event.okOrNull($event)
		? commandButtons.filter(({show}) => show($event.okOrNull($event)))
		: []
</script>

<style>
	button {
		cursor: pointer;
		display: inline-block;
		letter-spacing: 0.075em;
		padding: 0.25rem 0.5rem;
		margin: auto 1em 1em;
		position: relative;
		align-self: center;
		border: 1px var(--term-brand-primary) solid;
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 100%
		);
		border-image-slice: 1 1 0 0;
		z-index: 1;
		box-shadow: -0.5em 0.5em rgba(16, 24, 50, 0);
		transform-origin: left bottom;
		transition: all 200ms ease-in-out;
	}
	button:before,
	button:after {
		border: 1px var(--term-brand-primary) solid;
		content: '';
		display: block;
		position: absolute;
		z-index: -1;
	}

	button:before {
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			#0097dd 100%
		);
		border-image-slice: 1 1 1 1;
		left: -0.28em;
		top: 0.094em;
		width: 0.33em;
		height: 101%;
		transform: skewY(-45deg);
	}

	button:after {
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

	button:hover {
		background-size: 90%;
		transform: translate(0.2em, -0.2em);
		box-shadow: -1em 1em 0.15em rgba(16, 24, 50, 0.1);
	}

	button:hover::after {
		background-size: 100%;
		background-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 101%
		);
		width: 101%;
		border-image-slice: 1;
	}

	button:hover::before {
		background-size: 100%;
		background-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			#0097dd 102%
		);
		height: 102%;
		border-image-slice: 1;
	}
</style>

<div class="pl-6 pt-0 mt-0 pb-4 text-term-brand-2 flex">
	<div class="fix-initial" style="min-width: 60px;">
		$ cmds:
	</div>
	<div class="fix-initial">
		{#each visibleCmdButtons as b}
		<button class="text-md text-term-brand-2" on:click="{b.clickHandler}">
			[<span class="text-term-brand-1">{b.cmd}</span>] {b.text}
		</button>
		{/each}
	</div>
</div>
