<script>
	export let email;

	function handleClick(event) {
		event.preventDefault();
		window.location.href = `mailto:${email}`;
	}
</script>

<a class="flex no-underline" on:click={handleClick} href="mailto:obfuscated">
	<div class="mr-2">
		<slot />
	</div>
	<svg
		xmlns="http://www.w3.org/2000/svg"
		width="24"
		height="24"
		viewBox="0 0 24 24"
		fill="none"
		stroke="currentColor"
		stroke-width="2"
		stroke-linecap="round"
		stroke-linejoin="round"
	>
		<title>Contact Web Dev & Sausages by email</title>
		<path
			d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2
			2-2z"
		/>
		<polyline points="22,6 12,13 2,6" />
	</svg>
</a>

<style>
	a > svg {
		stroke: var(--brand-primary);
	}
</style>
