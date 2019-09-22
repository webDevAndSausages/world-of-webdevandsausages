<script>
	import {fade} from 'svelte/transition'
	export let alt = ''
	export let width = ''
	export let height = ''
	export let src = ''
	export let thumbnail = ''
	let className = ''
	let el
	export {className as class}
	let loaded = false
	let loading = true

	function load() {
		loading = false
	}
	//TODO: handle broken image
	function error() {
		loading = false
	}
</script>

<img
	id
	class={className}
	on:load={load}
	on:error={error}
	{src}
	{alt}
	{width}
	{height} />
{#if thumbnail && loading}
	<img class={className} src={thumbnail} {alt} {width} {height} />
{:else if loading}
	<slot name="loading" />
{/if}
