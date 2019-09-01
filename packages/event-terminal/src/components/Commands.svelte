<script>
	import {getContext} from 'svelte'
	import {showForStatusOf, getPixelWidthOfText} from './utils'
  import {Result} from './models/Result'
  import {isValidCmd} from './utils'
  
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
    const c = cmd.trim()
    if (c.length && isValidCmd(c)) {
      const cmdLetter = c.slice(0, 1).toLowerCase()
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
</script>

<style>
	button {
		cursor: pointer;
		display: inline-block;
		letter-spacing: 0.075em;
		padding: 0.25rem 0.5rem;
		margin: -1em 1em 1em;
		position: relative;
		align-self: center;
		border: 1px var(--term-brand-primary) solid;
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 100%
		);
		border-image-slice: 1 1 0 0;
		z-index: 1;
		box-shadow: -0.5em 0.5em rgba(16, 24, 50, 0);
		transform-origin: left bottom;
		transition: all 200ms ease-in-out;
	}
	button:before,
	button:after {
		border: 1px var(--term-brand-primary) solid;
		content: '';
		display: block;
		position: absolute;
		z-index: -1;
	}

	button:before {
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			#0097dd 100%
		);
		border-image-slice: 1 1 1 1;
		left: -0.28em;
		top: 0.094em;
		width: 0.33em;
		height: 101%;
		transform: skewY(-45deg);
	}

	button:after {
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 100%
		);
		border-image-slice: 1 1 1 0;
		bottom: -0.27em;
		right: 0.092em;
		width: 102%;
		height: 0.33em;
		transform: skewX(-45deg);
	}

	button:hover {
		background-size: 90%;
		transform: translate(0.2em, -0.2em);
		box-shadow: -1em 1em 0.15em rgba(16, 24, 50, 0.1);
	}

	button:hover::after {
		background-size: 100%;
		background-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 101%
		);
		width: 101%;
		border-image-slice: 1;
	}

	button:hover::before {
		background-size: 100%;
		background-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			#0097dd 102%
		);
		height: 102%;
		border-image-slice: 1;
	}

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
		background-color: #000;
    color: #fff;
    padding-left: 10px;
    cursor: none;
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
		position: absolute;
		right: -1000px;
	}
</style>

<div class="pl-6 pt-4 pb-4 text-term-brand-2">
	<div class="flex">
		<div class="flex-initial" style="min-width: 60px;">
			$ cmds:
		</div>
		<div class="flex-initial">
			{#each visibleCmdButtons as b, i}
			<button
				class="text-md text-term-brand-2"
				on:click|preventDefault="{() => onCmd(b.cmd)}"
				tabindex="{i}"
			>
				[<span class="text-term-brand-1">{b.cmd}</span>] {b.text}
			</button>
			{/each}
		</div>
	</div>
	<div class="flex">
		<div class="flex-initial pr-2">
			$ root@webdev:
		</div>
		<div class="flex-initial">
			<span class="input-wrapper flex">
				<input
					id="cmd-input"
					name="command"
					bind:value="{cmdInputValue}"
					style="{style}"
					className="bg-term-base"
          on:keyup="{updateInputSize}"
          on:keydown="{handleCmdInput}"
				/>
				<span class="cursor bg-term-brand-2" class:initial-cursor={cmdInputValue === ''}></span>
			</span>
			<span id="ruler"></span>
		</div>
	</div>
</div>
