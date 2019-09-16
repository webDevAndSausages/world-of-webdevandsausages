<script>
	import Facebook from './social-links/Facebook.svelte'
	import Twitter from './social-links/Twitter.svelte'
	import Github from './social-links/Github.svelte'
	import Youtube from './social-links/Youtube.svelte'

	export let segment
</script>

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

	.selected {
		position: relative;
		display: inline-block;
	}

	.selected::after {
		position: absolute;
		content: '';
		width: calc(100% - 1em);
		height: 2px;
		background-color: var(--term-brand-secondary);
		display: block;
		bottom: -1px;
	}
</style>

<nav>
	<ul>
		<Facebook />
		<Twitter />
		<Youtube />
		<Github />
	</ul>
</nav>
