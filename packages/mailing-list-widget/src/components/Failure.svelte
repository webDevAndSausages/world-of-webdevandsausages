<script>
	import {fly} from 'svelte/transition'
	import {linear} from 'svelte/easing'
	import {stateMachine} from './stores'
</script>

{#if $stateMachine === 'failure'}
	<div
		class="bg-red-600 py-2 px-4 text-center uppercase text-md font-bold
		text-white elevation-3"
		transition:fly={{duration: 100, y: 100, opacity: 0.0, easing: linear}}>
		GREAT FAILURE!
	</div>
{/if}
