<script>
export let onClick
</script>

<style>
  .send {
    right: 10px;
    top: 10px;
  }
  .send:hover svg {
    color: red;
  }
</style>

<div class="send absolute cursor-pointer transition" on:click={onClick}>
  <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 stroke-current text-term-brand-2" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
    <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
  </svg>
</div>