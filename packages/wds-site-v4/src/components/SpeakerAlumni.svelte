<script>
	import SpeakerLink from './SpeakerLink.svelte'
	export let speakers = {}
</script>

<style>
	.speakers {
		margin: auto;
		display: grid;
		grid-auto-flow: row;
		grid-auto-rows: minmax(20px, auto);
		grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
		gap: 12px 12px;
	}

	.speaker {
		@apply h-full min-w-0 content-around text-lg overflow-hidden;
		grid-column-end: span 1;
		grid-row-end: span 1;
		color: rgb(11, 126, 188);
	}
</style>

<div class="speakers">
	{#each speakers as speaker}
		{#if speaker.talks.length}
			<SpeakerLink slug={speaker.slug}>{speaker.name}</SpeakerLink>
		{:else}
			<div class="speaker text-term-brand-1 text-xl">{speaker.name}</div>
		{/if}
	{/each}
</div>
