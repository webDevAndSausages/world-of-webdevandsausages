<script>
	export let sponsor
	export let height = '2rem'
	let src = `/sponsors/${sponsor.name}-logo.svg`
</script>

<style>
	img {
		position: relative;
		height: 2rem;
		margin: 1rem;
		bottom: 0;
		opacity: 0.8;
		filter: brightness(0) invert(1);
		transition: opacity 125ms ease-in-out;
	}

	img:hover {
		opacity: 1;
	}
</style>

<a href={sponsor.href}>
  <img {src} alt={sponsor.name} style="height:{height};" />
</a>
