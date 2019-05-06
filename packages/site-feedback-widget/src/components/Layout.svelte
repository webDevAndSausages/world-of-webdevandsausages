<script>
  import { fly } from 'svelte/transition';
  import OpenCloseTab from "./OpenCloseTab.svelte"
</script>

<style>
  div {
    font-size: 13px !important;
    position: fixed;
    display: flex;
    flex-direction: column;
    align-content: space-around;
    z-index: 999999;
    bottom: -50px;
    padding-bottom: 100px;
    right: 100px;
    width: 300px;
    border-radius: 5px 5px 0 0;
    box-shadow: 0 0 7px 0 rgba(0, 0, 0, 0.3) !important;
    background: #fff;
  }

  @media only screen and (max-width: 600px) {
      div {
        left: 0; 
        right: 0; 
        margin-left: auto; 
        margin-right: auto; 
      }
  }

  header,
  main {
    padding-bottom: 20px;
    margin: auto;
  }

  header {
    font-family: "Special Elite", sans-serif;
    padding-top: 20px;
  }

  footer {
    margin: 10px auto 0px;
  }

</style>

<div in:fly="{{y: 200, duration: 1000}}" out:fly="{{y: 200, duration: 500}}">
  <OpenCloseTab />
  <header>
    <slot name="header" />
  </header>
  <main>
    <slot />
  </main>
  <footer>
    <a
      href="https://github.com/webDevAndSausages/world-of-webdevandsausages" 
      target="_blank" 
      rel="noopener noreferrer"
    >
      Make an issue on Github
    </a>
  </footer>
</div>