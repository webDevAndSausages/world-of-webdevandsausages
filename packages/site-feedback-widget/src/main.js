import App from './App.svelte'

const widget = new App({
  target: document.getElementById('site-feedback-widget'),
  props: {
    feedbackOffset: 700
  }
})

export default widget
