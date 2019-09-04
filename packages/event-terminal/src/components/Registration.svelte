<script>
	import Input from './Input.svelte'
	import CmdButton from './CmdButton.svelte'
	import {
		registrationStore,
		validationStore,
	} from './stores/registrationStore'

	function submit(_event) {
		console.log($registrationStore.email)
	}

	function handleBtnClick(cmd) {
		console.log(cmd)
	}
</script>

<style>
	.registration {
	}
</style>

<form on:submit|preventDefault="{submit}" class="registration flex-col m-5">
	<h2 class="term-title">~ REGISTRATION ~</h2>
	<fieldset class="flex-1">
		<Input
			label="email"
			type="email"
			bind:value="{$registrationStore.values.email}"
			error="{$validationStore.errors.email}"
		/>
	</fieldset>
	<fieldset class="flex-1">
		<Input
			label="first name"
			bind:value="{$registrationStore.values.firstName}"
			error="{$validationStore.errors.firstName}"
		/>
	</fieldset>
	<fieldset class="flex-1">
		<Input
			label="last name"
			bind:value="{$registrationStore.values.lastName}"
			error="{$validationStore.errors.lastName}"
		/>
	</fieldset>
	<fieldset class="flex-1">
		<Input
			label="affiliation"
			bind:value="{$registrationStore.values.affiliation}"
			error="{$validationStore.errors.affiliation}"
		/>
	</fieldset>
	<div class="flex-initial pt-5">
		<div class="flex">
			<div
				class="flex-initial text-term-brand-2"
				style="min-width: 60px;"
			>
				$ cmds:
			</div>
			<CmdButton
				cmd="s"
				type="submit"
				tabindex="1"
				on:cmd="{({detail}) => handleBtnClick(detail)}"
				style="margin-top: -0.5rem;"
			>
				save
			</CmdButton>
			<CmdButton
				cmd="r"
				tabindex="2"
				on:cmd="{({detail}) => handleBtnClick(detail)}"
				style="margin-top: -0.5rem;"
			>
				reset
			</CmdButton>
			<CmdButton
				cmd="x"
				tabindex="3"
				on:cmd="{({detail}) => handleBtnClick(detail)}"
				style="margin-top: -0.5rem;"
			>
				close
			</CmdButton>
		</div>
	</div>
</form>
