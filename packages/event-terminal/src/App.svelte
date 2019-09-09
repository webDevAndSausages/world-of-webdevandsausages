<script>
	import {Terminal} from './components/components.module.js'
	import {SsrTerminal} from './components/components.module.js'
	import './tailwind.css'
</script>

<style>
	.terminal-wrapper {
		margin: 20px 10%;
	}

	@media (max-width: 650px) {
		.terminal-wrapper {
			margin: 20px 2%;
		}
	}
</style>

<div class="terminal-wrapper">
	<Terminal />
</div>

<!--
<div class="terminal-wrapper">
	<SsrTerminal event={null}/>
</div>
-->