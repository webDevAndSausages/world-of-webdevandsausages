<script>
	export let href;
</script>

<li class="social-icon">
	<a {href}>
		<slot />
	</a>
</li>

<style>
	:global(.social-icon svg) {
		stroke: var(--brand-secondary);
	}

	li {
		display: block;
		float: right;
		cursor: pointer;
	}

	a {
		text-decoration: none;
		@apply block pt-4 pb-4 pl-3 pr-3;
	}
</style>
