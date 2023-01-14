<script>
	import {getContext} from 'svelte'

	// export let index

	const terminalStore = getContext('terminalStore')
</script>

<div class="text-term-error p-2">
	Command not found:
	<span class="text-white pl-2">{$terminalStore.cmd}</span>
</div>
