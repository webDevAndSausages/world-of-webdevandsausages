<script>
	export let title = undefined
	export let className = ''
	export let style = ''
	let classNames = 'pt-16 pb-16 text-center ' + className
</script>

<style>
	.section-title {
		color: #fff;
	}

	.section-title:hover {
		animation: subTitleGlow 1.5s ease-in-out infinite alternate;
		animation-delay: 0.2s;
	}

	@keyframes subTitleGlow {
		from {
			text-shadow: 0 0 10px #fff, 0 0 20px #fff, 0 0 30px #fff,
				0 0 40px var(--brand-secondary), 0 0 70px var(--brand-secondary),
				0 0 80px var(--brand-secondary), 0 0 100px var(--brand-secondary),
				0 0 150px var(--brand-secondary);
		}
		to {
			text-shadow: 0 0 5px #fff, 0 0 10px #fff, 0 0 15px #fff,
				0 0 20px var(--brand-secondary), 0 0 35px var(--brand-secondary),
				0 0 40px var(--brand-secondary), 0 0 50px var(--brand-secondary),
				0 0 75px var(--brand-secondary);
		}
	}
</style>

<section class={classNames} {style}>
	{#if title}
		<h3
			class="section-title uppercase text-term-brand-2 text-2xl md:text-4xl
			pb-10">
			{title}
		</h3>
	{/if}
	<slot />
</section>
