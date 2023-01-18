<script>
	import smoothscroll from 'smoothscroll-polyfill';
	smoothscroll.polyfill();
	import { setContext } from 'svelte';
	import 'simplebar';
	import 'simplebar/dist/simplebar.css';
	import EventDetails from './EventDetails.svelte';
	import TerminalContainer from './TerminalContainer.svelte';
	import { createEventStore } from './stores/eventStore.js';
	import { terminalStore } from './stores/terminalStore.js';
	import { showForStatusOf } from './utils';

	// if the event is not passed a prop
	// the store will fetch it
	export let event = null;
	const eventStore = createEventStore(event);
	let components = [];

	$: showInteractiveTerminal =
		$eventStore.okOrNull($eventStore) &&
		showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL')($eventStore.okOrNull($eventStore));
	// this ensures an update when the array size does not change but an item is switched
	// ie for reseting a component
	$: components = [...$terminalStore.history];

	setContext('eventStore', eventStore);
	setContext('terminalStore', terminalStore);
</script>

<TerminalContainer>
	<span slot="details">
		<EventDetails />
	</span>
	<div slot="output">
		{#if showInteractiveTerminal}
			<!-- (i) adds key to the component and ensures a new instance is rendered -->
			{#each components as h, i (i)}
				<svelte:component
					this={h}
					active={$terminalStore.currentIdx === i}
					index={i}
					id={`history-${i}`}
				/>
			{/each}
		{/if}
	</div>
</TerminalContainer>
