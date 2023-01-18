<script>
	import { fly } from 'svelte/transition';
	import { linear } from 'svelte/easing';
	import { stateMachine } from './stores';
	const start = () => stateMachine.send('START');
</script>

{#if $stateMachine === 'idle'}
	<button
		transition:fly={{ duration: 100, y: -100, opacity: 0.0, easing: linear }}
		class="bg-term-brand-2 py-2 px-4 uppercase text-md font-bold relative
		overflow-hidden text-white transition hover:elevation-5 elevation-3 w-full"
		on:click={start}
	>
		<slot />
	</button>
{/if}

<style>
	button:hover {
		opacity: 0.9;
	}
</style>
