<script>
	import Commands from './Commands.svelte'

	export let active
	export let id
	export let index
</script>

<div class="w-full" {id}>
	<Commands {active} {index} />
</div>
