<script>
  import {setContext} from 'svelte'
  import Controls from './Controls.svelte'
  import {controlsStore as store} from './stores/controlsStore'
  let containerClass = 'centered'
  
  setContext('controlsStore', store)
  
	$: containerClass = $store.open ?  'full' : 'centered terminal-centered-return'
</script>

<div class="terminal-{containerClass}">
	<div class="screen p-3 text-term-text bg-term-base">
    <div class="controls-container">
      <Controls />
    </div>
		<slot name="details">No current event</slot>
		<slot name="commands"></slot>
	</div>
</div>
