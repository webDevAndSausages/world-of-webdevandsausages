<script>
  import {setContext} from 'svelte'
  import Controls from './Controls.svelte'
  import {controlsStore as store} from './controlsStore'
  let containerClass = 'centered'
  
  setContext('controlsStore', store)
  
  $: containerClass = $store.open ?  'full' : 'centered container-centered-back'
</script>

<style>
	.container-full {
		position: absolute;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		z-index: 10;
		overflow: hidden;
		transform: scale(100%);
		transition: all 1s;
	}

	.container-centered {
		margin: 10%;
	}

	.container-centered-back {
		transform: scale(50%);
		transition: all 1s;
	}

	.container-centered.screen {
		height: 100%;
	}

	.screen {
		padding: 15px;
		border: var(--term-border-width) solid var(--term-border-color);
		color: white;
		box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19),
			0 6px 6px rgba(0, 0, 0, 0.23);
		background-color: var(--term-background-color);
		height: 100%;
    border-radius: 8px;
	}
  
  .container-full .screen {
		border-radius: 0;
	}

	button {
		background: var(--term-color);
		padding: 20px;
	}
</style>

<div class="container container-{containerClass}">
	<div class="screen">
    <div class="controls-container">
      <Controls />
    </div>
		<slot name="details">No current event</slot>
	</div>
</div>
