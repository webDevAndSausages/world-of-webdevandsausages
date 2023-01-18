<script>
	import CmdHelp from './CmdHelp.svelte';
	import TerminalTitle from './TerminalTitle.svelte';
	// export let index
	export let id;
	// export let active
</script>

<section {id}>
	<TerminalTitle>Web Dev & Sausages CLI commands</TerminalTitle>
	<div class="p-2 text-term-output">
		You may register for, cancel your registration (modifying), or check your registration status
		for an event with the commands below. Both Cancelling and checking your registration require a
		verification token that you received upon registering (check your email).
	</div>
	<CmdHelp
		cmd="r|register"
		help="Enters registration mode. the form requires at least a valid email.
		Please note that if you give a fake email, you will not receive email
		notifications about changes to your status. To exit without registering, use
		'x|exit'."
	/>
	<CmdHelp
		cmd="x|cancel"
		help="To cancel your registration, enter a valid verification token (check
		your email)."
	/>
	<CmdHelp
		cmd="c|check"
		help="To check your registration, including your position on a waiting list,
		enter your valid verification token."
	/>
	<CmdHelp cmd="h|help" help="Return you to this help page." />
</section>
