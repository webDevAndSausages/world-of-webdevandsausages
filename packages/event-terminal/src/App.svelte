<script>
  import { Terminal } from './components/components.module.js';
</script>

<style>
  /* these properties need to be set in the application consuming the component */
  :global(:root){
    --term-border-color: red;
    --term-border-width: 2px;
    --term-font-family: Inconsolata;
    --term-font-size: 1.2rem;
    --term-background-color: black;
    --term-color: white;
    --term-expander-color: #191919
  }

</style>

<Terminal />
