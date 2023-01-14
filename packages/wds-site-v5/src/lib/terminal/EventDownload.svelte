<script>
	import {onMount, onDestroy} from 'svelte'
	import DownloadIcon from './DownloadIcon.svelte'
	import {ElementObserver} from 'viewprt'
	import {createIcs} from './utils/ics'

	export let event

	let className = ''
	// string
	let ics = undefined
	// Blob object
	let blob = undefined
	// handle to ObjectURL for cleanup
	let href = undefined
	let fileNamePrefix = 'webdevandsausages_volume_'

	let isUnsupportedBrowser = /edge|msie\s|trident\//i.test(
		window.navigator.userAgent
	)

	onMount(() => {
		ElementObserver(document.getElementById('event-download-link'), {
			onEnter: () => (className = 'shake-animation'),
			onExit: () => (className = ''),
		})
	})

	$: ics =
		event &&
		createIcs({
			volume: event.volume,
			now: event.icsNow,
			start: event.icsStart,
			end: event.icsEnd,
			description: event.details,
			location: event.location,
		})

	$: blob = ics && event && new Blob([ics], {type: 'text/calendar'})

	function createHref() {
		blob.name = 'myblob'
		href = URL.createObjectURL(blob)
		return href
	}

	onDestroy(() => {
		href && URL.revokeObjectURL(href)
	})
</script>

<style>
	@keyframes shake {
		10%,
		90% {
			transform: translate3d(-1px, 0, 0);
		}

		20%,
		80% {
			transform: translate3d(2px, 0, 0);
		}

		30%,
		50%,
		70% {
			transform: translate3d(-4px, 0, 0);
		}

		40%,
		60% {
			transform: translate3d(4px, 0, 0);
		}
	}

	a {
		color: white;
		margin-right: 10px;
		display: inline-block;
	}

	a:hover {
		color: var(--term-brand-secondary);
	}

	.shake-animation {
		animation: shake s cubic-bezier(0.36, 0.07, 0.19, 0.97) 0.82s both;
		transform: translate3d(0, 0, 0);
	}
</style>

{#if !isUnsupportedBrowser && blob}
	<a
		id="event-download-link"
		class={className}
		href={createHref()}
		download={fileNamePrefix + event.volume}>
		<span>Save the date (.ics)</span>
		<DownloadIcon />
	</a>
{/if}
