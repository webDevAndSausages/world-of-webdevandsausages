
<script>
    import "../app.css";
	import Nav from '../components/Nav.svelte'
	import Footer from '../components/Footer.svelte'
	export let segment
</script>

<style>
	:global(body) {
		@apply bg-base;
	}
	main {
		@apply relative p-8 my-0 w-full h-full;
		box-sizing: border-box;
	}
</style>

<Nav {segment} />

<div class="container mx-auto px-0 sm:px-4 lg:px-8">
	<main>
		<slot />
	</main>
</div>

<Footer />