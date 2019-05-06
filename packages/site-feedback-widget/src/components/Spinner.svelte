<style>
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading {
    margin: auto;
    border-radius: 50%;
    width: var(--spinner-size);
    height: var(--spinner-size);
    border: .25rem solid var(--off-blue);
    border-top-color: var(--primary-blue);
    animation: spin var(--spin-duration) infinite linear;
}

</style>

<div class="loading" />