<script>
	import Commands from './Commands.svelte'

	export let active
	export let id
</script>

<div class="w-full" {id}>
	<Commands {active} />
</div>
