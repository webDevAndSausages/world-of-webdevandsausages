<script>
  import Commands from './Commands.svelte'
  
  export let active
</script>

<div class="w-full">
  <Commands active={active} />
</div>
