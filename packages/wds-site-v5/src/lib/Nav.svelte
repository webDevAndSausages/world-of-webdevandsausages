<script>
	import Facebook from './social-links/Facebook.svelte';
	import Twitter from './social-links/Twitter.svelte';
	import Github from './social-links/Github.svelte';
	import Youtube from './social-links/Youtube.svelte';
</script>

<nav>
	<ul>
		<Facebook />
		<Twitter />
		<Youtube />
		<Github />
	</ul>
</nav>

<style>
	nav {
		position: absolute;
		width: 100%;
		border-top: 1px solid var(--term-brand-primary);
		font-weight: 300;
		padding: 0 1em;
		z-index: 10;
	}

	ul {
		margin: 0;
		padding: 0;
	}

	/* clearfix */
	ul::after {
		content: '';
		display: block;
		clear: both;
	}
</style>
