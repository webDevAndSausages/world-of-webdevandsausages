<script>
  import EventDetails from './EventDetails.svelte'
  import TerminalContainer from './TerminalContainer.svelte'
  export let event
</script>


<TerminalContainer>
  <span slot="details">
    <EventDetails eventData={event} />
  </span>
</TerminalContainer>
