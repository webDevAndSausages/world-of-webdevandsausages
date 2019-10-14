<script>
	import Mailto from './Mailto.svelte'
</script>

<style>
	.footer {
		min-height: 10rem;
		border-bottom: 1px solid var(--brand-primary);
	}
</style>

<div class="footer flex text-center justify-center align-middle">
	<div class="flex flex-col justify-center mb-8">
		<Mailto email="richard.vancamp@gmail.com">Contact us</Mailto>
	</div>
</div>
