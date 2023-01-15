<script>
	import { scale } from 'svelte/transition';
	export let close;
	export let handleOutsideClick;
	export let animProps = { start: 1.2 };
	export let containerClass = '';
	$: contentClasses = `modal-content bg-black text-white ${containerClass}`;
</script>

<svelte:window on:keydown={handleOutsideClick} />
<div
	class="modal fixed w-full h-full top-0 left-0 overflow-hidden justify-center
	flex-col flex"
>
	<div class="modal-background left-0 bottom-0 absolute right-0 top-0" on:click={close} />
	<div class={contentClasses} transition:scale|local={animProps}>
		<!-- transition:_animation|local -->
		<slot />
	</div>
	<button class="modal-close fixed" aria-label="close" on:click={close} />
</div>

<style>
	.modal {
		z-index: 300;
	}

	.modal-background {
		background-color: rgba(255, 188, 225, 0.76);
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
		.modal-content.full {
			margin: 0 auto;
			max-height: calc(100vh - 40px);
			width: 90%;
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
