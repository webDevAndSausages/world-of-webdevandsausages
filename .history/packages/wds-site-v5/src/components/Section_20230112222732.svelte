<script>
	export let title = undefined
	let className = ''
	export let style = ''
	export {className as class}
	let classNames = 'pt-16 pb-16 text-center ' + className
</script>

<style>
	section.centered {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
	}

	section.pb-0 {
		padding-bottom: 0 !important;
	}

	section.pb-0 > .section-title {
		padding-bottom: 0 !important;
	}

	.section-title:hover {
		animation: subTitleGlow 1.5s ease-in-out infinite alternate;
		animation-delay: 0.2s;
	}

	@keyframes subTitleGlow {
		from {
			text-shadow: 0 0 10px #fff, 0 0 20px #fff, 0 0 30px #fff,
				0 0 40px var(--brand-primary), 0 0 70px var(--brand-primary),
				0 0 80px var(--brand-primary), 0 0 100px var(--brand-primary),
				0 0 150px var(--brand-primary);
		}
		to {
			text-shadow: 0 0 5px #fff, 0 0 10px #fff, 0 0 15px #fff,
				0 0 20px var(--brand-primary), 0 0 35px var(--brand-primary),
				0 0 40px var(--brand-primary), 0 0 50px var(--brand-primary),
				0 0 75px var(--brand-primary);
		}
	}
</style>

<section class={classNames} {style}>
	{#if title}
		<h3
			class="section-title lowercase text-term-brand-1 text-2xl md:text-4xl
			pb-10 text-term-brand-1">
			$ {title}
		</h3>
	{/if}
	<slot />
</section>
