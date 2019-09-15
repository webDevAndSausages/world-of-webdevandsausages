<script>
	import {onDestroy, onMount} from 'svelte'
	import {scale} from 'svelte/transition'
	import {goto} from '@sapper/app'
	import Video from './Video.svelte'

	export let active = false
	export let speakers
	let speaker = null

	let speaker_lookup = new Map()
	let modal_slug
	let modal_active = false

	$: if (speakers) {
		speakers.forEach(speaker => {
			speaker_lookup.set(speaker.slug, speaker)
		})
	}

	onMount(() => {
		const getFragment = () => window.location.hash.slice(1)
		const onhashchange = () => {
			modal_slug = getFragment()
			speaker = speaker_lookup.get(modal_slug)
			active = true
		}
		window.addEventListener('hashchange', onhashchange, false)
		const fragment = getFragment()
		if (fragment) {
			modal_slug = fragment
		}
		return () => {
			window.removeEventListener('hashchange', onhashchange, false)
		}
	})

	function close() {
		active = false
		speaker = null
		goto('/')
	}

	function keydown(e) {
		if (active && e.keyCode && e.keyCode === 27) {
			close()
		}
	}

	const animProps = {start: 1.2}
</script>

<style>
	.modal-background {
		background-color: rgba(0, 188, 221, 0.76);
	}

	.modal-content {
		max-height: calc(100vh - 160px);
		overflow: auto;
		position: relative;
		width: 100%;
	}

	@media screen and (min-width: 769px) {
		.modal-content {
			margin: 0 auto;
			max-height: calc(100vh - 40px);
			width: 640px;
		}
	}

	.modal-close {
		right: 20px;
		top: 20px;
		background: none;
		height: 32px;
		max-height: 32px;
		max-width: 32px;
		min-height: 32px;
		min-width: 32px;
		width: 32px;
		border: none;
		border-radius: 290486px;
		cursor: pointer;
		pointer-events: auto;
		display: inline-block;
		flex-grow: 0;
		flex-shrink: 0;
		font-size: 0;
		outline: none;
		vertical-align: top;
		user-select: none;
	}

	.modal-close::before,
	.modal-close::after {
		background-color: #fff;
		content: '';
		display: block;
		left: 50%;
		position: absolute;
		top: 50%;
		transform: translateX(-50%) translateY(-50%) rotate(45deg);
		transform-origin: center center;
	}

	.modal-close::before {
		height: 2px;
		width: 50%;
	}

	.modal-close::after {
		height: 50%;
		width: 2px;
		background-color: #fff;
		content: '';
		display: block;
		left: 50%;
		position: absolute;
		top: 50%;
		transform: translateX(-50%) translateY(-50%) rotate(45deg);
		transform-origin: center center;
	}

	.modal-close:hover,
	.modal-close:focus {
		background-color: rgba(10, 10, 10, 0.3);
	}

	.modal-close:active {
		background-color: rgba(10, 10, 10, 0.4);
	}
</style>

<svelte:window on:keydown={keydown} />
{#if active && speaker}
	<div
		class="modal fixed w-full h-full top-0 left-0 z-40 overflow-hidden
		justify-center flex-col flex">
		<div
			class="modal-background left-0 bottom-0 absolute right-0 top-0"
			on:click={close} />
		<div class="modal-content bg-black" transition:scale|local={animProps}>
			<!-- transition:_animation|local -->
			<h3 class="text-white">{speaker.name}</h3>
			{#each speaker.talks as talk}
				<p class="text-white">{talk.details}</p>
				<Video id={speaker.youtubeId} />
			{/each}
		</div>
		<button class="modal-close fixed" aria-label="close" on:click={close} />
	</div>
{/if}
