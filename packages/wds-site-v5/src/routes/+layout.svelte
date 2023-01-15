<script>
	import '../app.css';
	import Nav from '$lib/Nav.svelte';
	import Footer from '$lib/Footer.svelte';
</script>

<Nav />

<div class="container mx-auto px-0 sm:px-4 lg:px-8">
	<main>
		<slot />
	</main>
</div>

<Footer />

<style>
	:global(body) {
		@apply bg-base;
	}
	main {
		@apply relative p-8 my-0 w-full h-full;
		box-sizing: border-box;
	}
</style>
