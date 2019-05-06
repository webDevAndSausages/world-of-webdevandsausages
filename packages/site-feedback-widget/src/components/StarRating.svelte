<script>
  import { stateStore as store } from '../store'
  import {db} from '../firebase'

  const ratings = [1, 2, 3, 4, 5]

  let selected = 0

  async function rate(number) {
    selected = number
    // let user confirm selection before transitioning
    await db.saveRating(number)
		store.actions.rate(number)
	}
</script>

<style>
    svg {
      fill: none;
    }
    svg.selected {
      fill: var(--star-color)
    }
  </style>

<div>
  {#each ratings as rating}
  <span on:click={() => rate(rating)}>
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      class:selected={rating <= selected}
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      class="feather feather-star"
    >
      <polygon
        points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
      ></polygon>
    </svg>
  </span>
  {/each}
</div>
