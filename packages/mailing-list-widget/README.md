# `@webdevandsauages/mailing-list-widget`

### This is component is intended for use in [webdevandsausages.org](webdevandsausages.org) and is not suitable for other projects. The original boilerplate is from [svelte component template](https://github.com/YogliB/svelte-component-template).

## Table of Contents

1. [Getting started](#getting-started)
2. [Developing](#developing)
3. [Consuming components](#consuming-components)
4. [Backward Compatibility](#backward-compatibility)
5. [Preprocessors](#preprocessors)


## Getting Started usin the component

```bash
npm i @webdevandsauages/mailing-list-widget --save
```

### Include in your project

```js
<script>
	import {JoinMailingList} from '@webdevandsausages/event-terminal'
</script>

<div>
	{#if process.browser}
		<JoinMailingList />
	{/if}
</div>
```

## Developing

1. Start [Rollup](https://rollupjs.org):

```bash
npm run dev
```

2. Edit a component file in `src/components`, save it, and reload the page to see your changes.

3. Make sure your component is exported in `src/components/components.module.js`.

4. Make sure your component is imported and nested in `src/App.svelte`.

5. Navigate to [localhost:3000](http://localhost:3000) to see your components live.

6. In development the express dev server will proxy `/api` requests to[localhost:5000](http://localhost:5000)

## Consuming Components

Your package.json has a `"svelte"` field pointing to `src/components/components.module.js`, which allows Svelte apps to import the source code directly, if they are using a bundler plugin like [rollup-plugin-svelte](https://github.com/rollup/rollup-plugin-svelte) or [svelte-loader](https://github.com/sveltejs/svelte-loader) (where [`resolve.mainFields`](https://webpack.js.org/configuration/resolve/#resolve-mainfields) in your webpack config includes `"svelte"`). **This is recommended.**

For everyone else, `npm run build` will bundle your component's source code into a plain JavaScript module (`index.mjs`) and a UMD script (`index.js`), in the `dist` folder.<br>
This will happen automatically when you publish your component to npm, courtesy of the `prepublishOnly` hook in package.json.

## Backward Compatibility

This template uses [svelte-preprocess](https://github.com/kaisermann/svelte-preprocess) in order to integrate [PostCSS](https://postcss.org) auto-prefixing capabilities into the build process.

### Browserlist

`PostCSS` uses [browserlist](https://github.com/browserslist/browserslist) under the hood, in order to "know" what css to prefix.

The `browserlist` configuration is located inside the `package.json`.

## Preprocessors

This template comes with the [svelte-preprocess](https://github.com/kaisermann/svelte-preprocess) by default, which simplifies the use of preprocessors in components.

### Usage

1. [Install the required preprocessors.](https://github.com/kaisermann/svelte-preprocess#installation)
2. [Enable the preprocessor in the component.](https://github.com/kaisermann/svelte-preprocess#preprocessors-support)

