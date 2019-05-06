<script>
  import { stateStore as store } from '../store'

  let collapsed = $store.current === 'idle'

  function onClick() {
    if (collapsed) {
      return store.actions.show()
    }
    return store.actions.close()
  }
</script>

<style>
  .open-close-tab {
    text-align: center;
    width: 110px;
    height: 34px;
    padding-top: 2px;
    cursor: pointer;
    border: 0;
    border-top-right-radius: var(--border-radius);
    border-top-left-radius: var(--border-radius);
    outline: 0;
    font-size: 100%;
    vertical-align: baseline;
    line-height: 2.5;
    margin: 0;
    padding: 0;
    float: none !important;
    touch-action: manipulation;
    background: var(--primary-blue);
    color: var(--white);
    text-transform: uppercase;
    transition: color 0.3s;
  }

  .open-close-tab:hover {
    background: var(--hover-blue);
  }

  .tab-open {
    position: absolute;
    top: -34px;
    right: 20px;
  }

  .tab-collapsed {
    position: fixed;
    bottom: 0px;
    right: 120px;
  }

  @media only screen and (max-width: 600px) {
    .tab-collapsed {
      left: 0; 
      right: 0; 
      margin-left: auto; 
      margin-right: auto; 
    }

    .tab-open {
      top: -34px;
      right: 0;
      left: 0;
      margin-left: auto; 
      margin-right: auto; 
    }
  }

  svg {
    margin-bottom: -4px;
  }
</style>

<div
  class="open-close-tab"
  on:click="{onClick}"
  class:tab-collapsed="{collapsed}"
  class:tab-open="{!collapsed}"
>
  Feedback {#if collapsed}
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="18"
    height="18"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    class="feather feather-chevron-up"
  >
    <polyline points="18 15 12 9 6 15"></polyline>
  </svg>
  {:else}
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="18"
    height="18"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    class="feather feather-x"
  >
    <line x1="18" y1="6" x2="6" y2="18"></line>
    <line x1="6" y1="6" x2="18" y2="18"></line>
  </svg>
  {/if}
</div>
