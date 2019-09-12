<script>
    import { createEventDispatcher } from 'svelte';
    import Textfield from '@svelte-material-ui/textfield';
    let dirty = false;
    let invalid = false;
    export let value = '';
    export let label = 'text';
    export let editModeEnabled = false;
    let editedValue = value;
    const dispatch = createEventDispatcher();
</script>

<style>
    i {
        color: #cccccc;
    }
    :global(.mdc-text-field__input) {
        padding-top: 35px;
    }

    :global(.mdc-line-ripple) {
        bottom: 8px;
    }

    span {
        display: inline-block;
        min-width: 250px;
    }
</style>

{#if editModeEnabled}
    <Textfield type="text" on:input bind:dirty={dirty} bind:invalid={invalid} updateInvalid bind:input={editedValue} value={value} label={label} style="min-width: 250px;">
    </Textfield>
{:else}
    {#if value === ''}
        <i>&lt;NOT SET&gt;</i>
    {:else}
        <span>{value}</span>
    {/if}
{/if}
