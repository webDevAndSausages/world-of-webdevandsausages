<script>
	import {stateMachine, form} from './stores'
	import Send from './svg/Send.svelte'
	import {fly, crossfade, scale} from 'svelte/transition'
	import {linear} from 'svelte/easing'
	import cc from 'classcat'
	import {validateEmail} from './utils'
	import {postEmail, Result} from './utils/request'
	import Loading from './Loading.svelte'

	const onEscape = e => {
		if (
			e.key.includes('Esc') &&
			($stateMachine === 'active' || $stateMachine === 'warning')
		) {
			$form.email = ''
			stateMachine.send('ABORT')
		}
	}

	const [send, receive] = crossfade({
		duration: 200,
		fallback: scale,
	})

	let result = Result.None
	let show = false
	let focused = false
	let focusChange = 0
	let labelOnTop = false

	$: labelOnTop = focused || $form.email
	$: labelClasses = cc([
		{
			'ml-6 p-1 pt-0 text-term-brand-2 text-xs label-top bg-white h-3 z-30': labelOnTop,
			'pb-2 px-4 pt-2': !labelOnTop,
		},
		'absolute top-0 label-transition block pointer-events-none cursor-text text-term-brand-1 block',
	])
	$: inputClasses = cc([
		{},
		'outline-none text-black px-2 pb-2 pt-2 text-black w-full border border-term-brand-2 text-white bg-transparent',
	])

	$: showError = focusChange >= 2 && $form.error

	$: showEscapeMsg = $stateMachine === 'active' || $stateMachine === 'warning'

	function toggleFocused() {
		focused = !focused
		focusChange++
	}

	$: $form.error = validateEmail($form.email)
		? validateEmail($form.email)
		: null

	async function handleSubmit() {
		if ($form.error) {
			stateMachine.send('INPUT_INVALID')
		} else {
			stateMachine.send('SUBMIT')
			result = await postEmail({email: $form.email, receivesMail: true})
			console.log(result)
		}
	}

	function handleReturn(e) {
		if (e.key === 'Enter') handleSubmit()
  }
  
  function reset() {
    setTimeout(() => stateMachine.send('RESET'), 4500)
  }

	$: result.cata({
		None: () => {},
		Pending: () => {},
		Ok: () => {
      stateMachine.send('COMPLETE_SUCCESSFULLY')
      reset()
    },
		Failure: () => {
      stateMachine.send('COMPLETE_WITH_ERROR')
      reset()
    }
	})

	$: show =
		$stateMachine === 'active' ||
		$stateMachine === 'warning' ||
		$stateMachine === 'loading'
</script>

<style>
	input {
		color: black;
	}

	.label-transition {
		transition: font-size 0.05s, line-height 0.1s;
	}

	.label-top {
		line-height: 0.05;
		top: -5;
		left: 0;
	}

	.input-transition {
		transition: border 0.05s;
	}

	input {
		border: 1px var(--term-brand-primary) solid;
		border-image: linear-gradient(
			45deg,
			var(--term-brand-primary) 0%,
			var(--term-brand-secondary) 100%
		);
		border-image-slice: 1 1 1 1;
	}

	input.active {
		border: 1px var(--term-brand-secondary) solid;
		border-image: linear-gradient(
			45deg,
			var(--term-brand-secondary) 0%,
			var(--term-brand-primary) 100%
		);
		border-image-slice: 1 1 1 1;
	}

	input.disabled {
		opacity: 0.7;
		cursor: not-allowed !important;
	}
</style>

<svelte:window on:keydown={onEscape} />
{#if show}
	<div
		class="relative"
		transition:fly={{duration: 100, y: 100, opacity: 0.0, easing: linear}}>
		<div class="relative" class:text-error-500={$form.error}>
			{#if labelOnTop}
				<label
					class={labelClasses}
					for="email"
					in:receive={{key: 'email'}}
					out:send={{key: 'email'}}>
					<div>email</div>
				</label>
			{:else}
				<label
					class={labelClasses}
					for="email"
					in:receive={{key: 'email'}}
					out:send={{key: 'email'}}>
					email
				</label>
			{/if}
			<input
				aria-label="email"
				class={inputClasses}
				on:focus={toggleFocused}
				on:blur={toggleFocused}
				on:keypress={handleReturn}
				type="email"
				bind:value={$form.email}
				class:active={labelOnTop}
				disabled={$stateMachine === 'loading'} />
		</div>
		{#if $stateMachine === 'loading'}
			<Loading />
		{:else}
			<Send onClick={handleSubmit} />
		{/if}
		<div class="flex justify-between">
			{#if $form.error && $stateMachine === 'warning'}
				<span class="text-gray-600 text-sm">{$form.error}</span>
			{:else}
				<div />
			{/if}
			<div class="escape-msg -mb-1 text-gray-600 text-sm">ESC to exit</div>
		</div>
	</div>
{/if}
