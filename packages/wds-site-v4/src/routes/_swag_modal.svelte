<script>
	import Modal from '../components/Modal.svelte'
	// import Image from '../components/Image.svelte'

	let show = false

	let items = [
		'stickers',
		'hat',
		'cup',
		'shirt',
		'pipo',
		'mugs',
		'beer',
		'hoodie',
		'halloween_stuff',
		'towel',
	]

	function showModal() {
		show = true
	}

	function close() {
		show = false
	}

	function handleOutsideClick(e) {
		if (show && e.keyCode && e.keyCode === 27) {
			close()
		}
	}
</script>

<style>
	.swag-grid {
		display: grid;
		grid-auto-flow: row;
		grid-auto-rows: minmax(20px, auto);
		grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
		grid-gap: 8px;
	}
	.cell {
		height: 100%;
		min-width: 0;
		overflow: hidden;
		align-content: space-around;
		grid-column-end: span 1;
		grid-row-end: span 1;
		display: inline-flex;
		flex-flow: column wrap;
		justify-content: center;
	}
	img {
		max-height: 200px;
		padding: 10px;
	}

	.cell:hover {
		transform: scale(1.3, 1.3);
		transition: 0.2s;
	}

	button {
		min-width: 365px;
	}
</style>

{#if show}
	<Modal {close} {handleOutsideClick} containerClass="full">
		<div class="swag-grid bg-white p-2">
			{#each items as item}
				<div class="cell">
					<img src={`/swag/${item}.jpg`} alt={item} className="swag" />
				</div>
			{/each}
		</div>
	</Modal>
{/if}
<button
	class="bg-term-brand-2 py-2 px-4 uppercase text-md font-bold relative
	overflow-hidden text-white transition hover:elevation-5 elevation-3"
	on:click={showModal}>
	Check out the swag
</button>
