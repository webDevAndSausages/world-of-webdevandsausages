# `Site Feedback Widget`

## Adding to a website

Include `sfw-bundle.js` and `sfw-bundle.css` in your public directory and add then to the `index.html`. The initialize the widget in a script:

```html
<script>
  // options passed here are the defaults
  // if you do not pass options
  site_feedback_widget({
  	feedbackOffset: 700,
  	autoOpen: true,
  	autoOpenDelay = 8000
  })
</script>
```

The styles are scoped and should not effect the main site.

## Getting started

In the project directory, you can run:

### `npm run dev`

Runs the app in the development mode.<br>
Open [http://localhost:5000](http://localhost:5000) to view it in the browser. A dummy page is included in the build for dev purposes.

The page will reload if you make edits.<br>

### `npm run build`

Will create minified bundles of the widget under the `public` folder

### `npm run build`

Builds the app for production to the `build` folder.<br>
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br>
Your app is ready to be deployed!

### `npm start`

This simply serves your production bundle for quick testing with reloading.

## How it works

The widget will be available from a tab at the bottom of the page. If clicked the feedback form will appear. The first question asks you to rate the app from 5 stars. If the app is rated at 3 or less stars, it will then ask for follow suggestions for how to improve the app. All the answers are sent anonymously to firebase, stored as follows:

```
'responses2019' [collection] ->
  '159' [document] // number is day of year ->
    { rating: [2, 3, 4], suggestions: ["Remove videos."] }
```

The widget will also pop up for a user the first time he scrolls down the page after some seconds. If he answers, it will not pop up again. If he closes it, it will pop up again only after three days have passed.
