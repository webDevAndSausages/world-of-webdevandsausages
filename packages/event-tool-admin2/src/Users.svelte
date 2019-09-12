<script>
    import { onMount } from 'svelte'
    import Spinner from 'svelte-spinner'
    import { events, loading, ACTIONS } from './stores/events-store'
    import Button from '@svelte-material-ui/button'
    import Select, {Option} from '@svelte-material-ui/select'
    import Paper, {Title, Subtitle, Content} from '@svelte-material-ui/paper';
    import UsersTable from './components/UsersTable.svelte'
    import { navigate } from "svelte-routing";

    let selectedEvent = null;
    let editedUser = {};

    onMount(async () => {
        await ACTIONS.getAllEvents()
        selectedEvent = ($events[0] || {}).id || null
    });

    const getUsers = eventId => ($events.find(e => e.id === +eventId) || {}).participants || [];
    const handleSaveUser = ({ detail: user }) => {
        ACTIONS.saveUser(user);
    };
    const handleRemoveUser = ({ detail: user }) => {
        ACTIONS.removeUser(user)
    };
    const editEvent = () => {
        navigate(`/event/${selectedEvent}`)
    }
</script>

<style>
    .event-selector {
        padding: 20px 0;
    }

    .info-item {
        display: inline-block;
    }

    .participant-amount {
        padding-left: 20px;
    }

    .edit-event {
        padding-top: 20px;
    }
</style>

<h1>Admin panel</h1>
{#if $loading}
<Spinner
        size="50"
        speed="750"
        color="#A82124"
        thickness="2"
        gap="40"
/>
{:else}
    <div class="event-selector">
        <Paper elevation="2">
            <Title>Event info</Title>
            <Content>
                <div class="info-item select-box">

                <Select variant="outlined" bind:value={selectedEvent} label="Event" disabled={editedUser.id} >
                    {#each $events as event}
                        <Option value={event.id} selected={selectedEvent === event.id}>
                            {event.name}
                        </Option>
                    {/each}
                </Select>
                </div>
                <div class="info-item participant-amount">
                    Amount of participants: {getUsers(selectedEvent).length}
                </div>
                <div class="edit-event">
                    <Button on:click={editEvent}>Edit</Button>
                </div>
            </Content>
        </Paper>
    </div>

    <UsersTable users={getUsers(selectedEvent)} on:save={handleSaveUser} on:remove={handleRemoveUser} />
{/if}
