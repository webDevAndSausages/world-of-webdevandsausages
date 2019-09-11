<script context="module">
	export function preload() {
		return this.fetch(`index.json`)
			.then(r => r.json())
			.then(({event, speakers, events}) => {
				return {event, speakers, events}
			})
	}
</script>

<script>
	import Terminal from './_terminal.svelte'
	import {JoinMailingList} from '@webdevandsausages/mailing-list-widget'
	export let event
	export let speakers
	export let events
</script>

<style>
	h1 {
		font-size: 2em;
	}

	@media (min-width: 480px) {
		h1 {
			font-size: 4em;
		}
	}

	.terminal-wrapper {
		margin: 20px 10%;
	}

	.mailing-list-join-wrapper {
		width: 400px;
		margin: auto;
		background: var(--mailing-list-join-background);
		border-radius: 5px;
		padding: 20px;
	}

	@media (max-width: 650px) {
		.terminal-wrapper {
			margin: 0;
		}
		.mailing-list-join-wrapper {
			width: 100%;
		}
	}
</style>

<svelte:head>
	<title>Web Dev & Sausages</title>
</svelte:head>
<div class="pt-20 pb-20 bg-glow">
	<h1 class="text-center text-term-brand-1">WEB DEV & SAUSAGES</h1>
</div>

<div class="mailing-list-join-wrapper m-auto pt-10 pb-10">
	{#if process.browser}
		<JoinMailingList />
	{/if}
</div>

<div class="terminal-wrapper mr-auto">
	<Terminal {event} />
</div>

<pre class="text-white">{JSON.stringify(speakers, null, 2)}</pre>
<pre class="text-white">{JSON.stringify(events, null, 2)}</pre>
