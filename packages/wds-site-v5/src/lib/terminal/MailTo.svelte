<script>
	export let email;

	function handleClick(event) {
		event.preventDefault();
		window.location.href = `mailto:${email}`;
	}
</script>

<a class="underline text-term-brand-1" on:click={handleClick} href="mailto:obfuscated">
	<slot />
</a>
