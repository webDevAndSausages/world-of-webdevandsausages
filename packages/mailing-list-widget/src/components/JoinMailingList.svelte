<script>
	import {stateMachine} from './stores'
	import StartButton from './StartButton.svelte'
	import EmailForm from './EmailForm.svelte'
	import Success from './Success.svelte'
	import Failure from './Failure.svelte'
</script>

<div>
	<StartButton>Join our mailing List</StartButton>
	<EmailForm />
	<Success />
	<Failure />
</div>
