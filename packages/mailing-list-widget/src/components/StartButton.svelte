<script>
	import {fly} from 'svelte/transition'
	import {quintOut} from 'svelte/easing'
	import {stateMachine} from './stores/use-machine'
	const start = () => stateMachine.send('START')
</script>

<style>
	button:hover {
		opacity: 0.9;
	}
</style>

{#if $stateMachine === 'idle'}
	<button
		transition:fly={{duration: 500, y: -200, opacity: 0.5, easing: quintOut}}
		class="bg-term-brand-2 py-2 px-4 uppercase text-md font-bold relative
		overflow-hidden text-white transition hover:elevation-5 elevation-3 w-full"
		on:click={start}>
		<slot />
	</button>
{/if}
