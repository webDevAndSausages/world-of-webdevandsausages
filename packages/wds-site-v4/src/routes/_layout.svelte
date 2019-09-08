<script>
	import Nav from '../components/Nav.svelte';

	export let segment;
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

<main>
	<slot></slot>
</main>