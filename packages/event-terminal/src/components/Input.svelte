<script>
	import {crossfade, scale} from 'svelte/transition'
	import {onMount} from 'svelte'
	import {fly} from 'svelte/transition'
	import {quadOut} from 'svelte/easing'
	import cc from 'classcat'

	const [send, receive] = crossfade({
		duration: 200,
		fallback: scale,
	})

	// input props
	export let value = ''
	export let label = ''
	export let id = ''
	export let error = null
	export let type = 'text'
	export let loading = false
	export let maxChars = null
	export let disabled = false

	let _id
	let focused = false
	let focusChange = 0
	let labelClasses = ''
	let inputClasses = ''
	let inputEl
	$: labelOnTop = focused || value
	$: labelClasses = cc([
		{
			'ml-6 p-1 pt-0 text-term-brand-2 text-xs label-top bg-black h-3 z-30': labelOnTop,
			'pb-2 px-4 pt-2': !labelOnTop,
		},
		'absolute top-0 label-transition block pointer-events-none cursor-text text-term-brand-1 block',
	])
	$: inputClasses = cc([
		{},
		'outline-none px-2 pb-2 pt-2 text-black w-full border border-term-brand-2 bg-black text-white bg-transparent',
	])

	onMount(() => {
		_id = !id && label ? label.split(' ').join('-') : id

		if (maxChars != null) inputEl.setAttribute('maxlength', maxChars)

		document.addEventListener('reset', reset, {passive: true})

		return () =>
			document.removeEventListener('reset', reset, {passive: true})
	})

	$: showError = focusChange >= 2

	function reset() {
		focused = false
		focusChange = 0
		value = ''
	}

	function toggleFocused() {
		focused = !focused
		focusChange++
	}

	function onInput(ev) {
		value = ev.target.value
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
		background-color: #000;
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

<div class="mt-1 relative pb-4">
	<div class="relative" class:text-error-500="{error}">
		{#if labelOnTop}
		<label
			class="{labelClasses}"
			for="{_id}"
			in:receive="{{key:_id}}"
			out:send="{{key:_id}}"
		>
			<div>$ {label}</div>
		</label>
		{:else}
		<label
			class="{labelClasses}"
			for="{_id}"
			in:receive="{{key:_id}}"
			out:send="{{key:_id}}"
		>
			{label}
		</label>
		{/if}
		<input
			id="{_id}"
			bind:this="{inputEl}"
			aria-label="{label}"
			class="{inputClasses}"
			on:focus="{toggleFocused}"
			on:blur="{toggleFocused}"
			type="{type}"
			{value}
			on:input="{onInput}"
			class:active="{labelOnTop}"
			class:disabled="{disabled}"
			{disabled}
			accesskey="r"
		/>
	</div>
	{#if showError && error}
	<span class="text-yellow-300 text-sm">{error}</span>
	{/if}
</div>
