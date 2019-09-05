<script>
  import {createEventDispatcher} from 'svelte'
  import {getPixelWidthOfText} from './utils'

  export let active;
// handle size of terminal input
  export let value = ''
  let cmdInputWidth = 20

  function updateInputSize() {
    cmdInputWidth = getPixelWidthOfText(value) + 10
  }
  $: style = `width:${cmdInputWidth}px;`


  const dispatch = createEventDispatcher()

  function handleCmdInput(e) {
    if (e.keyCode === 13) dispatch('cmd', value)
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

<div class="flex">
  <div class="flex-initial pr-2 text-term-brand-2">
    $ root@webdev:
  </div>
  <div class="flex-initial">
    {#if active}
        <span class="input-wrapper flex">
          <input
            id="cmd-input"
            name="command"
            bind:value="{value}"
            style="{style}"
            class="bg-term-base text-term-output"
            on:keyup="{updateInputSize}"
            on:keydown="{handleCmdInput}"
            disabled={!active}
          />
          <span class="cursor bg-term-brand-2" class:initial-cursor={value === ''}></span>
        </span>
        <span id="ruler" class="absolute"></span>
      {:else}
        <div class="output text-term-output pl-5">{value}</div>
      {/if}
    </div>
  </div>