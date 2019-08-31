<script>
    import { setContext } from 'svelte'
  import EventDetails from './EventDetails.svelte'
  import TerminalContainer from './TerminalContainer.svelte'
  import Commands from './Commands.svelte'
  import { createEventStore } from './stores/eventStore.js'
  
  // if the event is not passed a prop
	// the store will fetch it
  export let event
  const eventStore = createEventStore(event)

  setContext('eventStore', eventStore)
</script>


<TerminalContainer>
  <span slot="details">
    <EventDetails />
  </span>
  <span slot="commands">
    <Commands />
  </span>
</TerminalContainer>
