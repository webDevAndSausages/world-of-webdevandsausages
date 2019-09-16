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
	import SpeakerLink from '../components/SpeakerLink.svelte'

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
		@apply h-full min-w-0 content-around text-lg overflow-hidden;
		grid-column-end: span 1;
		grid-row-end: span 1;
		color: rgb(11, 126, 188);
	}

	.section-title {
		color: var(--brand-secondary);
	}

	.title:hover {
		animation: titleGlow 1.5s ease-in-out infinite alternate;
		animation-delay: 0.2s;
	}

	.section-title:hover {
		animation: subTitleGlow 1.5s ease-in-out infinite alternate;
		animation-delay: 0.2s;
	}

	@keyframes titleGlow {
		from {
			text-shadow: 0 0 10px #fff, 0 0 20px #fff, 0 0 30px #fff,
				0 0 40px var(--brand-primary), 0 0 70px var(--brand-secondary),
				0 0 80px var(--brand-primary), 0 0 100px var(--brand-primary),
				0 0 150px var(--brand-primary);
		}
		to {
			text-shadow: 0 0 5px #fff, 0 0 10px #fff, 0 0 15px #fff,
				0 0 20px var(--brand-primary), 0 0 35px var(--brand-secondary),
				0 0 40px var(--brand-primary), 0 0 50px var(--brand-primary),
				0 0 75px var(--brand-primary);
		}
	}

	@keyframes subTitleGlow {
		from {
			text-shadow: 0 0 10px #fff, 0 0 20px #fff, 0 0 30px #fff,
				0 0 40px var(--brand-secondary), 0 0 70px var(--brand-secondary),
				0 0 80px var(--brand-secondary), 0 0 100px var(--brand-secondary),
				0 0 150px var(--brand-secondary);
		}
		to {
			text-shadow: 0 0 5px #fff, 0 0 10px #fff, 0 0 15px #fff,
				0 0 20px var(--brand-secondary), 0 0 35px var(--brand-secondary),
				0 0 40px var(--brand-secondary), 0 0 50px var(--brand-secondary),
				0 0 75px var(--brand-secondary);
		}
	}
</style>

<svelte:head>
	<title>Web Dev & Sausages</title>
</svelte:head>

<div class="pt-20 pb-20">
	<h1 class="title text-center text-term-brand-1">WEB DEV & SAUSAGES</h1>
</div>

<aside class="mailing-list-join-wrapper m-auto pt-10 pb-10">
	{#if process.browser}
		<JoinMailingList />
	{/if}
</aside>

<section class="terminal-wrapper mr-auto pt-20">
	<Terminal {event} />
</section>

<section class="pt-20 pb-20 text-center">
	<h3 class="section-title uppercase text-4xl">SPEAKER ALUMNI</h3>
	<div class="speakers pt-10">
		{#each speakers as speaker}
			{#if speaker.talks.length}
				<SpeakerLink slug={speaker.slug}>{speaker.name}</SpeakerLink>
			{:else}
				<div class="speaker text-term-brand-1 text-xl">{speaker.name}</div>
			{/if}
		{/each}
	</div>
</section>

<SpeakerModal {speakers} />
