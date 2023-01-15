<script>
	import {Terminal, SsrTerminal} from '$lib/index.js'
  import {browser} from '$app/env'
	import '../tailwind.css'
</script>

<style>
	:global(body) {
		background: #fff;
	}

	.terminal-wrapper {
		margin: 20px 10%;
		margin-top: 200px;
		margin-bottom: 2000px;
	}

	@media (max-width: 650px) {
		.terminal-wrapper {
			margin: 20px 2%;
		}
	}
</style>

<div class="terminal-wrapper">
	<Terminal event={undefined} />
</div>

<div class="terminal-wrapper">
	<SsrTerminal event={null} />
</div>