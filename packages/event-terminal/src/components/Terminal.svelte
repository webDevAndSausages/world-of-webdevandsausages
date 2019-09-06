<script>
	import {setContext} from 'svelte'
	import EventDetails from './EventDetails.svelte'
	import TerminalContainer from './TerminalContainer.svelte'
	import Commands from './Commands.svelte'
	import {createEventStore} from './stores/eventStore.js'
	import {terminalStore} from './stores/terminalStore.js'

	// if the event is not passed a prop
	// the store will fetch it
	export let event = null
	const eventStore = createEventStore(event)

	$: showInteractiveTerminal = $eventStore.okOrNull($eventStore)

	setContext('eventStore', eventStore)
	setContext('terminalStore', terminalStore)
</script>

<TerminalContainer>
	<span slot="details">
		<EventDetails />
	</span>
	<div slot="output">
		{#if showInteractiveTerminal}
			{#each $terminalStore.history as h, i}
				<svelte:component
					this={h}
					active={$terminalStore.currentIdx === i}
					index={i}
					id={`history-${i}`} />
			{/each}
		{/if}
	</div>
</TerminalContainer>
