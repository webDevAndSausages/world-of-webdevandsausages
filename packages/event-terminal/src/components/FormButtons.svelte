<script>
  import CmdButton from './CmdButton.svelte'
  export let handleClick
  export let disabled

  function onCmd({detail}) {
    handleClick(detail)
  }

</script>

<div class="flex-initial pt-5">
  <div class="flex align-middle">
    <div class="flex-initial text-term-brand-2" style="min-width: 60px;">
      $ cmds:
    </div>
    <CmdButton
      cmd="s"
      type="submit"
      tabindex="1"
      on:cmd={onCmd}
      {disabled}>
      submit
    </CmdButton>
    <CmdButton
      cmd="r"
      type="reset"
      tabindex="2"
      on:cmd={onCmd}
      {disabled}>
      reset
    </CmdButton>
    <CmdButton
      cmd="x"
      tabindex="3"
      on:cmd={onCmd}
      {disabled}>
      exit
    </CmdButton>
  </div>
</div>