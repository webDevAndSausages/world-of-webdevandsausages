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
	import {onMount} from 'svelte'
	import Terminal from './_terminal.svelte'
	import SpeakerModal from '../components/SpeakerModal.svelte'
	import {JoinMailingList} from '@webdevandsausages/mailing-list-widget'

	export let event
	export let speakers
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

	.bracket {
		font-weight: bold;
	}

	code,
	pre {
		font-family: inherit;
		font-size: 1em;
		max-width: 60vw;
	}

	em {
		color: #fff;
		font-style: normal;
	}

	.speakers {
		max-width: 800px;
		margin: auto;
		display: grid;
		grid-auto-flow: row;
		grid-auto-rows: minmax(20px, auto);
		grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
		gap: 8px 8px;
	}

	.speaker {
		height: 100%;
		min-width: 0px;
		align-content: space-around;
		grid-column-end: span 1;
		grid-row-end: span 1;
		color: rgb(11, 126, 188);
		font-size: 20px;
		overflow: hidden;
	}
</style>

<svelte:head>
	<title>Web Dev & Sausages</title>
</svelte:head>

<div class="pt-20 pb-20">
	<h1 class="text-center text-term-brand-1">WEB DEV & SAUSAGES</h1>
</div>

<aside class="mailing-list-join-wrapper m-auto pt-10 pb-10">
	{#if process.browser}
		<JoinMailingList />
	{/if}
</aside>

<section class="terminal-wrapper mr-auto pt-20">
	<Terminal {event} />
</section>

<section class="pt-20 text-center">
	<h3 class="text-term-brand-2 uppercase text-2xl pb-5">SPEAKERS</h3>
	<div class="speakers">
		{#each speakers as speaker}
			{#if speaker.talks.length}
				<a
					class="speaker text-term-brand-1 cursor-pointer"
					href={`/#${speaker.slug}`}>
					{speaker.name}
				</a>
			{:else}
				<div class="speaker text-term-brand-1">{speaker.name}</div>
			{/if}
		{/each}
	</div>
</section>

<SpeakerModal {speakers} />
