<script>
	import SponsorLogo from '$lib/SponsorLogo.svelte';
	export let sponsors;
</script>

<div class="flex flex-wrap w-full justify-center">
	{#each sponsors as sponsor}
		<SponsorLogo {sponsor} height={sponsor.name.includes('mylab') ? '2.5rem' : undefined} />
	{/each}
</div>
