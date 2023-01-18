<script>
	import { onMount } from 'svelte';
	import { slide } from 'svelte/transition';
	export let app = false;
	let initialized = false;
	onMount(() => {
		if (!app) return;
		setTimeout(() => {
			initialized = true;
		}, 200);
	});
</script>

<div
	class:fixed={app}
	class:z-50={app}
	class="top-0 left-0 w-full h-1 background overflow-hidden relative"
	class:hidden={app && !initialized}
	transition:slide={{ duration: 300 }}
>
	<div class="bg-term-brand-2 h-1 absolute inc" />
	<div class="bg-term-brand-2 h-1 absolute dec" />
</div>

<style>
	/* from https://codepen.io/shalimano/pen/wBmNGJ */
	.inc {
		animation: increase 2s ease-in-out infinite;
	}
	.dec {
		animation: decrease 2s 0.9s ease-in-out infinite;
	}
	@keyframes increase {
		from {
			left: -5%;
			width: 5%;
		}
		to {
			left: 130%;
			width: 150%;
		}
	}
	@keyframes decrease {
		from {
			left: -90%;
			width: 90%;
		}
		to {
			left: 110%;
			width: 10%;
		}
	}

	.background {
		background: #ffbfff;
	}
</style>
