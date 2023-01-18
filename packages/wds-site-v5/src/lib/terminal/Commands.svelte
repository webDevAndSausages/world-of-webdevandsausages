<script>
	import { getContext, onMount } from 'svelte';
	import CmdInput from './CmdInput.svelte';
	import { showForStatusOf, getPixelWidthOfText } from './utils';
	import { Result } from './models/Result';
	import { isValidCmd, getFullTerminalCmd, normalizeCmd } from './utils';
	import CmdButton from './CmdButton.svelte';
	import { REGISTER, CANCEL, CHECK, HELP } from './constants';

	function isValid(char) {
		const cmd = visibleCmdButtons.find((cb) => cb.cmd === char.toLowerCase());
		return cmd && cmd.show;
	}

	function handleKeyPress(e) {
		if (active && isValid(e.key)) {
			cmdInputValue = e.key;
			onCmd(e.key);
		}
	}

	onMount(() => {
		document.addEventListener('keypress', handleKeyPress);
		return () => {
			document.removeEventListener('keypress', handleKeyPress);
		};
	});

	// TODO: inactive state
	export let active;
	export let index;

	const event = getContext('eventStore');
	const { cmds } = getContext('terminalStore');

	let cmdInputValue = '';

	let commandButtons = [
		{
			text: 'register',
			cmd: REGISTER,
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST')
		},
		{
			text: 'cancel',
			cmd: CANCEL,
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL')
		},
		{
			text: 'check',
			cmd: CHECK,
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL')
		},
		{
			text: 'help',
			cmd: HELP,
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL')
		}
	];

	$: visibleCmdButtons = $event.okOrNull($event)
		? commandButtons.filter(({ show }) => show($event.okOrNull($event)))
		: [];

	function onCmd(cmd) {
		const c = cmd ? cmd.trim().toLowerCase() : '';
		if (c.length && isValidCmd(c)) {
			const cmdLetter = normalizeCmd(c);
			switch (cmdLetter) {
				case REGISTER:
					return cmds.register();
				case CANCEL:
					return cmds.cancel();
				case CHECK:
					return cmds.check();
				case HELP:
					return cmds.help();
				default:
					return cmds.invalid({ cmd });
			}
		} else if (c.length) {
			return cmds.invalid({ cmd });
		}
		return;
	}

	function handleBtnClick(cmd) {
		if (!active) return;
		if (isValidCmd(cmd.trim().toLowerCase())) cmdInputValue = getFullTerminalCmd(cmd);
		onCmd(cmd);
	}
</script>

<div class="pl-2 pr-2 pt-4 pb-4 text-term-brand-2">
	<div class="flex">
		<div class="flex-initial text-term-brand-2" style="min-width: 60px;">$ cmds:</div>
		<div class="flex-initial">
			{#each visibleCmdButtons as b, i}
				<CmdButton
					cmd={b.cmd}
					tabindex={i}
					disabled={!active}
					on:cmd={({ detail }) => handleBtnClick(detail)}
				>
					{b.text}
				</CmdButton>
			{/each}
		</div>
	</div>
	<CmdInput
		on:cmd={({ detail }) => onCmd(detail)}
		bind:value={cmdInputValue}
		tabindex={visibleCmdButtons.length}
		{index}
		{active}
	/>
</div>
