<script>
	export let status
	export let message
</script>

<div class="flex text-white">
	<div class="text-term-brand-2 p-2">
		$ server [
		<span class="text-term-error">{status}</span>
		]:
	</div>
	<div class="text-white pt-2">{message}</div>
</div>
