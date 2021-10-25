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
	import Terminal from './_terminal.svelte'
	import Sponsors from './_sponsors.svelte'
	import About from './_about.svelte'
	import SpeakerModal from './_speaker_modal.svelte'
	import {JoinMailingList} from '@webdevandsausages/mailing-list-widget'
	import SpeakerAlumni from './_speaker_alumni.svelte'
	import SwagModal from './_swag_modal.svelte'
	import Section from '../components/Section.svelte'
	import Image from '../components/Image.svelte'

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

	:global(.sponsor-logo) {
		padding-top: 20px;
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

<Section class="pb-0">
	<Image
		src="/images/wds-logo-rgb.png"
		alt="Wed Dev & Sausages logo"
		class="wds-logo" />
</Section>

<aside class="mailing-list-join-wrapper m-auto pt-10 pb-10">
	{#if process.browser}
		<JoinMailingList />
	{/if}
</aside>

<Section title="sponsored by" class="centered pb-0">
	<a href="https://monad.fi/"><Image
		src="/sponsors/monad-logo.svg"
		class="sponsor-logo"
		alt="Monad logo"
		width="200" /></a>
</Section>

<Section class="terminal-wrapper mr-auto">
	<Terminal {event} />
</Section>

<Section title="About">
	<About />
</Section>

<Section title="Speaker Alumni">
	{#if speakers}
		<SpeakerAlumni {speakers} />
	{/if}
</Section>

<Section title="Sponsors">
	<Sponsors sponsors={sponsors.data} />
</Section>

<Section>
	<SwagModal />
</Section>

<SpeakerModal {speakers} />
