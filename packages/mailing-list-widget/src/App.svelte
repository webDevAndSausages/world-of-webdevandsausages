<script>
	import {JoinMailingList} from './components/components.module.js'
	import './tailwind.css'
</script>

<style>
	.terminal-wrapper {
		margin: 20% auto;
		width: 500px;
		background: black;
	}

	@media (max-width: 650px) {
		.terminal-wrapper {
			margin: 20px 2%;
		}
	}
</style>

<div class="terminal-wrapper">
	<JoinMailingList />
</div>
