<script>
	import StartButton from './StartButton.svelte';
	import EmailForm from './EmailForm.svelte';
	import Success from './Success.svelte';
	import Failure from './Failure.svelte';
</script>

<StartButton>
	<slot>Join our mailing List</slot>
</StartButton>
<Success />
<Failure />
<EmailForm />
