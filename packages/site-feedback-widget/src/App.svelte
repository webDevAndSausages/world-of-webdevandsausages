<svelte:options immutable={true} />
<svelte:head>
	<link href="https://fonts.googleapis.com/css?family=Special+Elite" rel="stylesheet">
</svelte:head>

<script>
	import "./global.svelte"
	import Layout from "./components/Layout.svelte";
	import StarRating from "./components/StarRating.svelte"
	import Spinner from "./components/Spinner.svelte"
	import OpenCloseTab from "./components/OpenCloseTab.svelte"
	import { onMount } from 'svelte'
	import { elasticOut } from 'svelte/easing'
	import { fade } from 'svelte/transition';
	import { stateStore as store, hide } from './store'
	import debounce from 'lodash.debounce'
	import storage from './localStorage'
	import {db} from './firebase'

	// prop
	export let feedbackOffset = 700
	// state
	let autoOpened = false
	let rating = 0
	let text = ''
	$: isHidden = $store.current === 'idle'
	$: thanks = $store.current === 'thanks'
	// give thanks and close automatically
	$: if(thanks) {
		setTimeout(() => store.actions.close(), 3000)
	}
	$: form = $store.current === 'form'

	const listenerConfig = {capture: true, passive: true}
   const handleScroll = debounce(e => {
			const shouldShow = window.pageYOffset > feedbackOffset
			if (isHidden && !$store.init && !$hide) {
				store.actions.show()
			}
	 }, 2000)

	onMount(() => {
		!$hide && document.addEventListener('scroll', handleScroll, listenerConfig)
		return () => document.removeEventListener('scroll', handleScroll, listerConfig);
	})

	async function onSubmit() {
		// could implement retry logic here?
		store.actions.submit(text)
		await db.saveSuggestion(text)
		store.actions.finish('ok')
		text = ''
	}
</script>

<style>
	textarea {
		display: block;
    margin-bottom: 2rem;
    border: 1px solid var(--border-color);
    font-size: 1rem;
	  width: 100%;
		height: 100px;
	  border-radius: var(--border-radius);
	}

	textarea:disabled {
		background: var(--off-white);
	}

	button {
    background: var(--primary-blue);
    appearance: none;
    border: none;
    text-transform: uppercase;
    color: white;
    letter-spacing: 0.5px;
    font-weight: bold;
    padding: 0.7rem 1.2rem;
    border-radius: 20rem;
    align-self: flex-end;
    cursor: pointer;
    font-size: 1rem;
	}

	button:hover {
    background: var(--hover-blue);
  }

	form {
		display: flex;
		flex-direction: column;
	}

	form > button {
		margin: auto;
	}
</style>

<aside id="site-feedback-widget-container">
	{#if !isHidden && !thanks && !form}
	<Layout>
		<h1 slot="header">Rate this site</h1>
		<StarRating />
	</Layout>
	{:else if thanks}
		<Layout>
			<h1 slot="header">Thanks</h1>
		</Layout>
	{:else if form}
		<Layout>
			<h1 slot="header">Tell us what to fix</h1>
			<form on:submit|preventDefault={onSubmit}>
				<textarea bind:value={text} disabled={$store.submitting} />
				{#if $store.submitting}
					<Spinner />		
				{:else}
					<button>Submit</button>
				{/if}
			</form>
		</Layout>
		{:else}
		<div in:fade="{{duration: 1000, delay: 200}}"><OpenCloseTab /></div>
	{/if}
	</aside>