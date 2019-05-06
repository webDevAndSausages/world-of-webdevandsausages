<style>
    /* we don't want any truly global styles since this is a widget */
    :global(aside#site-feedback-widget-container) {
      /* global variables */
      --font-color: #555;
      --border-color: #333;
      --spinner-size: 28px;
      --white: rgb(255,255,255);
      --off-white: rgb(217,217,217, 0.5);
      --spin-duration: 1s;
      --primary-blue: rgb(6, 93, 138);
      --hover-blue: rgb(8, 116, 170);
      --off-blue: rgb(6, 93, 218, 0.2);
      --star-color: rgb(255, 193, 0);
      --border-radius: 5px;

      box-sizing: border-box;
      margin: 0;
      background: #fff;
  
      color: var(--font-color);
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
        Helvetica, Arial, sans-serif, 'Apple Color Emoji',
        'Segoe UI Emoji', 'Segoe UI Symbol';
      font-size: 14px;
      line-height: 1.4;
    }
  </style>
