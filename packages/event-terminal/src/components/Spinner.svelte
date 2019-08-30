<script>
  export let show = false
  let frames = [
			"-",
			"\\",
			"|",
			"/"
    ]

  let currentFrame = 0
    
  function timeoutHandler() {
    if (!show) return
    if (currentFrame <= 2) {
      currentFrame++
    } else if (currentFrame == 3) {
      currentFrame = 0
    }
    setTimeout(timeoutHandler, 100)
	}

	setTimeout(timeoutHandler)
</script>

<style>
  .term-spinner {
    font-size: 30px;
    padding: 20px;
  }
</style>

<div class="term-spinner">
  {frames[currentFrame]}
</div>