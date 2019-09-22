<script>
	import SponsorLogo from '../components/SponsorLogo.svelte'
	export let sponsors
</script>

<div class="flex flex-wrap w-full justify-center">
	{#each sponsors as sponsor}
		<SponsorLogo {sponsor} />
	{/each}
</div>
