<script lang="ts">
	import type { PageData } from '$types/data';
	import { browser } from '$app/environment';
	import Meta from '$lib/Meta.svelte';
	import Image from '$lib/Image.svelte';
	import Section from '$lib/Section.svelte';
	import { JoinMailingList } from '$lib/mailing-list-form';
	import Terminal from './home/Terminal.svelte';
	import Sponsors from './home/Sponsors.svelte';
	import About from './home/About.svelte';
	import SpeakerModal from './home/SpeakerModal.svelte';
	import SpeakerAlumni from './home/SpeakerAlumni.svelte';
	import SwagModal from './home/SwagModal.svelte';

	export let data: PageData;
	const { speakers, sponsors } = data;
</script>

<Meta title="Web Dev &amp; Sausages Registration" />

<Section class="pb-0">
	<Image src="/images/wds-logo-rgb.png" alt="Wed Dev & Sausages logo" class="wds-logo" />
</Section>

<aside class="mailing-list-join-wrapper m-auto pt-10 pb-10">
	{#if browser}
		<JoinMailingList />
	{/if}
</aside>

<!-- Sponsor logo when event is open -->
<!--<Section title="sponsored by" class="centered pb-0">
	<a href="https://www.gofore.com/"><Image
		src="/sponsors/gofore-logo.svg"
		class="sponsor-logo"
		alt="Gofore logo"
		width="200" /></a>
</Section>-->

<Section class="terminal-wrapper mr-auto" centerText={false}>
	<Terminal event={null} />
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
