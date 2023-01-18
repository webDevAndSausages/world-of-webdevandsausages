<script>
	import { fly } from 'svelte/transition';
	import { linear } from 'svelte/easing';
	import { stateMachine } from './stores';
</script>

{#if $stateMachine === 'success'}
	<div
		class="bg-green-400 py-2 px-4 text-center uppercase text-md font-bold
		text-white elevation-3 relative w-full"
		transition:fly={{ duration: 100, delay: 10, y: 100, opacity: 0.0, easing: linear }}
	>
		GREAT SUCCESS!
	</div>
{/if}
