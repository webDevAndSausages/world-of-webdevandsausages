<script>
    import { Router, Route } from "svelte-routing";
    import Users from './Users.svelte'
    import EditEvent from './EditEvent.svelte'

</script>

<style>
    :global(h1) {
        color: purple;
    }
</style>

<svelte:head>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,600,700">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto+Mono">
</svelte:head>

<Router>
    <Route path="/" component={Users} />
    <Route path="/event/:id" component={EditEvent} />
</Router>
