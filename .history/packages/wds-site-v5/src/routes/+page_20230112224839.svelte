<script>
import Meta from '$lib/Meta.svelte';
import Image from '$lib/Image.svelte';
import Section from '$lib/Section.svelte';
</script>

<Meta title="Web Dev &amp; Sausages Registration" />


<Section class="pb-0">
	<Image
		src="/images/wds-logo-rgb.png"
		alt="Wed Dev & Sausages logo"
		class="wds-logo" />
</Section>

<!-- Sponsor logo when event is open -->
<!-- <Section title="sponsored by" class="centered pb-0">
	<a href="https://www.thriv.dev/"><Image
		src="/sponsors/thriv-logo.svg"
		class="sponsor-logo"
		alt="Thriv logo"
		width="200" /></a>
</Section> -->

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

