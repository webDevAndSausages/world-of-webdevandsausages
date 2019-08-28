<script>
	import {scale} from 'svelte/animation'
	import {quintOut} from 'svelte/easing'
	let containerClass = 'centered'

	function toggle() {
		if (containerClass === 'centered') {
			containerClass = 'full'
		} else {
			containerClass = 'centered'
		}
	}
</script>

<style>
	.container-full {
		position: absolute;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		z-index: 0;
	}

	.container-centered {
		dispay: flex;
		margin: 10%;
	}

	.container-centered.screen {
		height: 100%;
	}

	.screen {
		--border-color: red;
		--border-width: 2px;
		--font-family: Inconsolata;
		--font-size: 1.2rem;
		padding: 30px;
		border: var(--border-width) solid var(--border-color);
		color: white;
		box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19),
			0 6px 6px rgba(0, 0, 0, 0.23);
		background-color: black;
		height: 100%;
	}

	button {
		background: green;
		padding: 20px;
	}
</style>

<div class="container container-{containerClass}" transition:scale>
	<div class="screen">
		<slot name="details">No current event</slot>
		<button on:click="{toggle}">full</button>
	</div>
</div>
