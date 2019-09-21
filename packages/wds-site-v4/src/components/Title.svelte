<script>
	export let main
	export let sub
</script>

<style>
	.title:hover {
		animation: titleGlow 1.5s ease-in-out infinite alternate;
		animation-delay: 0.2s;
	}

	@keyframes titleGlow {
		from {
			text-shadow: 0 0 10px #fff, 0 0 20px #fff, 0 0 30px #fff,
				0 0 40px var(--brand-primary), 0 0 70px var(--brand-secondary),
				0 0 80px var(--brand-primary), 0 0 100px var(--brand-primary),
				0 0 150px var(--brand-primary);
		}
		to {
			text-shadow: 0 0 5px #fff, 0 0 10px #fff, 0 0 15px #fff,
				0 0 20px var(--brand-primary), 0 0 35px var(--brand-secondary),
				0 0 40px var(--brand-primary), 0 0 50px var(--brand-primary),
				0 0 75px var(--brand-primary);
		}
	}
</style>

<h1 class="title text-center text-term-brand-1">{main}</h1>
<p class="text-2xl text-white text-center">{sub}</p>
