<script>
  import {stateMachine, form} from './stores/use-machine.js'
  import Send from './svg/Send.svelte'
 	import {fly, crossfade, scale} from 'svelte/transition'
	import {linear} from 'svelte/easing'
	import cc from 'classcat'

	const [send, receive] = crossfade({
		duration: 200,
		fallback: scale,
	})

	let focused = false
	let showError = false
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
		'outline-none px-2 pb-2 pt-2 text-black w-full border border-term-brand-2 text-white bg-transparent',
	])

	$: showError = focusChange >= 2 && $form.error

	function toggleFocused() {
		focused = !focused
		focusChange++
	}
</script>

<style>
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

{#if $stateMachine === 'active' || $stateMachine === 'warning'}
	<div
		class="mt-1 relative pb-4"
		in:fly={{duration: 500, y: 100, opacity: 0.5, easing: linear}}
		out:fly={{duration: 500, y: -100, opacity: 0.5, easing: linear}}>
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
				type="email"
				bind:value={$form.email}
				class:active={labelOnTop} />
    </div>
    <Send />
    <div class="text-xs absolute bottom-0 right-0 -mb-1">ESC to exit</div>
		{#if $form.error && showError}
			<span class="text-term-error text-sm">{$form.error}</span>
		{/if}
	</div>
{/if}
