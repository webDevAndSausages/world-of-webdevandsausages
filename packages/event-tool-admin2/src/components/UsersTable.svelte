<script>
    import { createEventDispatcher } from 'svelte';
    import DataTable, {Head, Body, Row, Cell} from '@svelte-material-ui/data-table';
    import EditableField from './EditableField.svelte'
    import IconButton from '@svelte-material-ui/icon-button';
    import Button, {Label} from '@svelte-material-ui/button'
    import Dialog, {Title, Content, Actions} from '@svelte-material-ui/dialog'

    export let users = [];
    export let editedUser = {};
    let confirmSaveDialog;
    let confirmRemoveDialog;
    let userSelectedForRemove = null;
    const dispatch = createEventDispatcher();

    const userFieldEdited = field => e => editedUser[field] = e.target.value;
    const editUser = (user) => {
        editedUser = JSON.parse(JSON.stringify(user));
    };
    const saveUser = () => {
        confirmSaveDialog.open();
    };
    const removeUser = (user) => {
        userSelectedForRemove = user;
        confirmRemoveDialog.open();
    };
    const cancelEditUser = () => editedUser = {};
    const confirmSaveDialogCloseHandler = e => {
        if (e.detail.action === 'accept') {
            dispatch("save", editedUser);
        }
    };
    const confirmRemoveDialogCloseHandler = e => {
        if (e.detail.action === 'accept') {
            dispatch("remove", userSelectedForRemove);
        }
    };
</script>

<style>
    .app-button {
        margin-bottom: 0;
    }

</style>
{#if users.length === 0}
    No users :(
{:else}
    <DataTable table$aria-label="People list" style="width: 100%">
        <Head>
            <Row>
                <Cell>ID</Cell>
                <Cell>First name</Cell>
                <Cell>Last name</Cell>
                <Cell>Email</Cell>
                <Cell>Affiliation</Cell>
                <Cell>Edit</Cell>
            </Row>
        </Head>
        <Body>

        {#each users as user}
            <Row>
                <Cell>
                    {user.id}
                </Cell>
                <Cell>
                    <EditableField value={user.firstName || ''} on:input={userFieldEdited('firstName')} label="First name" editModeEnabled={user.id === editedUser.id} />
                </Cell>
                <Cell>
                    <EditableField value={user.lastName || ''} on:input={userFieldEdited('lastName')} label="Last name" editModeEnabled={user.id === editedUser.id} />
                </Cell>
                <Cell>
                    <EditableField value={user.email || ''} on:input={userFieldEdited('email')} label="Email" editModeEnabled={user.id === editedUser.id} />
                </Cell>
                <Cell>
                    <EditableField value={user.affiliation || ''} on:input={userFieldEdited('affiliation')} label="Affiliation" editModeEnabled={user.id === editedUser.id} />
                </Cell>
                <Cell>
                    {#if user.id === editedUser.id}
                        <IconButton class="material-icons app-button" on:click={cancelEditUser}>cancel</IconButton>
                        <IconButton class="material-icons app-button" on:click={saveUser}>save</IconButton>
                    {:else}
                        <IconButton class="material-icons app-button" disabled={editedUser.id} on:click={() => editUser(user)}>edit</IconButton>
                        <IconButton class="material-icons app-button" disabled={editedUser.id} on:click={() => removeUser(user)}>delete</IconButton>
                    {/if}
                </Cell>
            </Row>
        {/each}
        </Body>
    </DataTable>
{/if}

<Dialog bind:this={confirmSaveDialog} aria-labelledby="list-selection-title" aria-describedby="list-selection-content" on:MDCDialog:closed={confirmSaveDialogCloseHandler}>
    <Title id="list-selection-title">Save user</Title>
    <Content id="list-selection-content">
        Are you sure you want to save user?
    </Content>
    <Actions>
        <Button>
            <Label>Cancel</Label>
        </Button>
        <Button action="accept">
            <Label>Save</Label>
        </Button>
    </Actions>
</Dialog>

<Dialog bind:this={confirmRemoveDialog} aria-labelledby="list-selection-title" aria-describedby="list-selection-content" on:MDCDialog:closed={confirmRemoveDialogCloseHandler}>
    <Title id="list-selection-title">Remove user</Title>
    <Content id="list-selection-content">
        Are you sure you want to remove selected user?
    </Content>
    <Actions>
        <Button>
            <Label>Cancel</Label>
        </Button>
        <Button action="accept">
            <Label>Remove</Label>
        </Button>
    </Actions>
</Dialog>
