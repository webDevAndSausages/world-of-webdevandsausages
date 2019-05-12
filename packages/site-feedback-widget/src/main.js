import App from './App.svelte'

window.site_feedback_widget = function(props) {
  return new App({
    target: document.getElementById('site-feedback-widget'),
    props: {
      ...props,
      feedbackOffset: 700,
      autoOpen: true
    }
  })
}
