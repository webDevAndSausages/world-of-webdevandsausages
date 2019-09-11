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
	@media (min-width: 480px) {
		h1 {
			font-size: 4em;
		}
	}

	.terminal-wrapper {
		margin: 20px 10%;
	}

	.terminal-wrapper .screen {
		box-shadow: 2px 3px 15px #fff;
	}

	.mailing-list-join-wrapper {
		width: 400px;
		margin: auto;
	}

	@media (max-width: 650px) {
		.terminal-wrapper {
			margin: 20px 2%;
		}
	}
</style>

<svelte:head>
	<title>Web Dev & Sausages</title>
</svelte:head>
<h1 class="text-center text-term-brand-1">WEB DEV & SAUSAGES</h1>

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
