<script context="module">
	export function preload() {
		return this.fetch(`index.json`)
			.then(r => r.json())
			.then(({event, speakers, events, sponsors}) => {
				return {event, speakers, events, sponsors: sponsors}
			})
	}
</script>

<script>
	import {onMount} from 'svelte'
	import Terminal from './_terminal.svelte'
	import Sponsors from './_sponsors.svelte'
	import About from './_about.svelte'
	import SpeakerModal from '../components/SpeakerModal.svelte'
	import {JoinMailingList} from '@webdevandsausages/mailing-list-widget'
	import SpeakerAlumni from '../components/SpeakerAlumni.svelte'
	import Section from '../components/Section.svelte'
	import Title from '../components/Title.svelte'

	export let event
	export let speakers
	export let sponsors
</script>

<style>
	:global(h1) {
		font-size: 2em;
	}

	@media (min-width: 580px) {
		:global(h1) {
			font-size: 3em;
		}
	}

	:global(.terminal-wrapper) {
		text-align: left;
	}

	.mailing-list-join-wrapper {
		width: 400px;
		margin: auto;
		background: var(--mailing-list-join-background);
		border-radius: 5px;
		padding: 20px;
	}

	@media (max-width: 650px) {
		:global(.terminal-wrapper) {
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

<Section>
	<Title
		main="WEB DEV & SAUSAGES"
		sub="The most awesome meetup in the world, possibly" />
</Section>

<aside class="mailing-list-join-wrapper m-auto pt-10 pb-10">
	{#if process.browser}
		<JoinMailingList />
	{/if}
</aside>

<Section className="terminal-wrapper mr-auto">
	<Terminal {event} />
</Section>

<Section title="About">
	<About />
</Section>

<Section title="Speaker Alumni">
	<SpeakerAlumni {speakers} />
</Section>

<Section title="Sponsors">
	<Sponsors sponsors={sponsors.data} />
</Section>

<SpeakerModal {speakers} />
