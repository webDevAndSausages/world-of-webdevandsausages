<svelte:head>
	<title>Sapper project template</title>
</svelte:head>

<script>
	import Terminal from './_terminal.svelte'
</script>

<style>
	p {
		margin: 1em auto;
	}

	@media (min-width: 480px) {
		h1 {
			font-size: 4em;
		}
	}
	
	.terminal-wrapper {
		margin: 20px 10%;
	}

	@media (max-width: 650px) {
		.terminal-wrapper {
			margin: 20px 2%;
		}
	}
</style>

<h1 class="text-center text-term-brand-1">WEB DEV & SAUSAGES</h1>

<div class="terminal-wrapper">
	<!-- TODO: animations when expanding to full-screen broken -->
	<Terminal />
</div>

<p><strong>Try editing this file (src/routes/index.svelte) to test live reloading.</strong></p>
