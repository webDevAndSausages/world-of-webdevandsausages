<script>
	export let id
	export let title
	export let start = 0
	let YOUTUBE_BASE_URL = 'https://www.youtube.com/embed/'
</script>

<style>
	.video-container {
		position: relative;
		width: 100%;
		height: 0;
		padding-bottom: 56.25%;
	}
	.video {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}
</style>

<div class="video-container">
	<iframe
		class="video"
		{title}
		src={`${YOUTUBE_BASE_URL}${id}?rel=0&showinfo=0&start=${start}`}
		frameborder="0"
		allowfullscreen />
</div>
