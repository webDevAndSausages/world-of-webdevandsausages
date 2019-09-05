<script>
	import {getContext} from 'svelte'
	import {showForStatusOf, getPixelWidthOfText} from './utils'
  import {Result} from './models/Result'
	import {isValidCmd, getFullCmd, normalizeCmd} from './utils'
	import CmdButton from './CmdButton.svelte'
  
  // TODO: inactive state
  export let active;

  const event = getContext('eventStore')
  const {cmds} = getContext('terminalStore')

	let commandButtons = [
		{
			text: 'register',
			cmd: 'r',
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST'),
		},
		{
			text: 'cancel',
			cmd: 'x',
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL'),
		},
		{
			text: 'check',
			cmd: 'c',
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL'),
		},
		{
			text: 'help',
			cmd: 'h',
			show: showForStatusOf('OPEN', 'OPEN_WITH_WAITLIST', 'OPEN_FULL'),
		},
	]

	$: visibleCmdButtons = $event.okOrNull($event)
		? commandButtons.filter(({show}) => show($event.okOrNull($event)))
		: []

  // handle size of terminal input
	let cmdInputValue = ''
  let cmdInputWidth = 20
  
	function updateInputSize() {
		cmdInputWidth = getPixelWidthOfText(cmdInputValue) + 10
  }
  $: style = `width:${cmdInputWidth}px;`

  function onCmd(cmd) {
    const c = cmd ? cmd.trim().toLowerCase() : ''
    if (c.length && isValidCmd(c)) {
      const cmdLetter = normalizeCmd(c)
      switch(cmdLetter) {
        case 'r':
          return cmds.register()
        case 'x':
          return cmds.cancel()
        case 'c':
          return cmds.check()
        case 'h':
          return cmds.help()
        default:
          return cmds.invalid({cmd})
      }
    } else if (c.length) {
      return cmds.invalid({cmd})
    }
    return 
  }
  
  function handleCmdInput(e) {
    if(e.keyCode === 13) onCmd(cmdInputValue)
  }

  function handleBtnClick(cmd) {
    if (!active) return
    if (isValidCmd(cmd.trim().toLowerCase())) cmdInputValue = getFullCmd(cmd)
    onCmd(cmd)
  }
</script>

<style>
	@keyframes blink {
		0% {
			opacity: 0;
		}
		40% {
			opacity: 0;
		}
		50% {
			opacity: 1;
		}
		90% {
			opacity: 1;
		}
		100% {
			opacity: 0;
		}
	}

	#cmd-input {
    padding-left: 10px;
	}

	.cursor {
		padding-top: 12px;
		height: 22px;
		width: 10px;
    animation: 1s blink 1s infinite;
    pointer-events: none;
  }
  
  .initial-cursor {
    margin-left: -10px;
  }

	/* this span is just used to measure the length of the input and shouldn't be visible */
	#ruler {
		right: -1000px;
	}
</style>

<div class="pl-6 pt-4 pb-4 text-term-brand-2">
	<div class="flex">
		<div class="flex-initial text-term-brand-2" style="min-width: 60px;">
			$ cmds:
		</div>
		<div class="flex-initial">
			{#each visibleCmdButtons as b, i}
				<CmdButton
					cmd={b.cmd}
					tabindex="{i}"
					disabled={!active}
					on:cmd='{({detail}) => handleBtnClick(detail)}'
				>
					{b.text}
				</CmdButton>
			{/each}
		</div>
	</div>
	<div class="flex">
		<div class="flex-initial pr-2">
			$ root@webdev:
		</div>
		<div class="flex-initial">
      {#if active}
        <span class="input-wrapper flex">
          <input
            id="cmd-input"
            name="command"
            bind:value="{cmdInputValue}"
            style="{style}"
            class="bg-term-base text-term-output"
            on:keyup="{updateInputSize}"
            on:keydown="{handleCmdInput}"
            disabled={!active}
          />
          <span class="cursor bg-term-brand-2" class:initial-cursor={cmdInputValue === ''}></span>
        </span>
        <span id="ruler" class="absolute"></span>
      {:else}
        <div class="output text-term-output pl-5">{cmdInputValue}</div>
      {/if}
		</div>
	</div>
</div>
