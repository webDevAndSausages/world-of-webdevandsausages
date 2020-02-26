<script>
	import {onDestroy, onMount} from 'svelte'
	import {goto} from '@sapper/app'
	import Modal from '../components/Modal.svelte'
	import Video from '../components/Video.svelte'

	export let active = false
	export let speakers
	let speaker = null

	let speaker_lookup = new Map()
	let modal_slug =
		typeof window !== 'undefined' && window.location.hash.slice(1)
	let modal_active = false

	$: if (speakers) {
		speakers.forEach(speaker => {
			speaker_lookup.set(speaker.slug, speaker)
		})
	}

	// this watchers is needed to ensure modal loads if app loads via link to this the url
	// otherwise event listener in setup in onMount takes over

	$: speaker = speaker_lookup.get(modal_slug)

	$: if (modal_slug && speaker) active = true

	onMount(() => {
		const getFragment = () => window.location.hash.slice(1)
		const onhashchange = () => {
			modal_slug = getFragment()
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

	function handleOutsideClick(e) {
		if (active && e.keyCode && e.keyCode === 27) {
			close()
		}
	}
</script>

{#if active && speaker}
	<Modal {close} {handleOutsideClick} containerClass="p-2">
		<h3>{speaker.name}</h3>
		{#each speaker.talks as talk}
			<p class="pt-1 pb-1">{talk.details}</p>
			{#if talk.with}
				<p class="pt-1 pb-1">together with {talk.with}</p>
			{/if}
			<Video
				id={talk.youtubeId}
				title={talk.details}
				start={talk.startSeconds} />
		{/each}
	</Modal>
{/if}
