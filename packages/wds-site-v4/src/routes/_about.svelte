<script>
	import Blurb from '../components/Blurb.svelte'
	import Image from '../components/Image.svelte'
</script>

<style>
	dt {
		font-style: italic;
		padding-top: 10px;
	}
</style>

<Blurb>
	<h2 slot="sponsoring-title">Sponsoring</h2>
	<div slot="sponsoring-blurb">
		{#if process.browser}
			<Image
				class="blurb-image full"
				src="/images/wds-sponsoring.jpg"
				alt="meetup speaker" />
		{/if}
		<p>
			Help sustain the most popular meetup in Tampere, while gaining greater
			visibility for your company.
		</p>
	</div>
	<div slot="sponsoring-text">
		<p>
			Web Dev & Sausages is completely volunteer event which exists thanks to
			sponsors and volunteers. Since it began in 2016, it has attracted large,
			enthusiastic groups of developers from Tampere and beyond, with events
			filling up within hours of being announced. Sponsors contribute not only
			money and spaces, but also their culture and ideas. Our goal is to keep
			the events high quality and exciting. Sponsoring companies benefit in a
			number of ways:
		</p>
		<ul class="list-disc p-4">
			<li>Visibility among a large group of developers of their company</li>
			<li>
				A present to developers what the company is about and, e.g., if they are
				recruiting
			</li>
			<li>
				A developer from the company is invited to speak on a topic of interest
			</li>
			<li>
				WD&S sponsors our proudly listed as supporters on our website for
				eternity
			</li>
		</ul>
		<p>
			If you are interested in becoming a sponsor, please get in touch with
			<a class="underline" href="mailto:juhani.kumara@solita.fi">Juhani</a>
			,
			<a class="underline" href="mailto:leo.melin@mylab.fi">Leo</a>
			, or
			<a class="underline" href="mailto:richard.vancamp@gmail.com">Richard</a>
			and we will make a great event together!
		</p>
	</div>
	<h2 slot="speaking-title">Speaking</h2>
	<div slot="speaking-blurb">
		<div class="mt-2 mb-2">
			{#if process.browser}
				<Image
					class="blurb-image full"
					src="/images/wds-speaker.jpg"
					alt="meetup speaker" />
			{/if}
		</div>
		<p>
			Dare to share something your interested in with your fellow developers. It
			is an empowering experience.
		</p>
	</div>
	<div slot="speaking-text">
		<p>
			Web Dev & Sausages loves its speakers and we take pride in the wide
			variety of people, such as developers, architects, designers, managers,
			and artists, as well as topics on everything, from languages and tools, to
			making music and even mental strategies. So you belong too!
		</p>
		<dl class="list-inside pb-4">
			<dt>- Is x topic ok?</dt>
			<dd>
				The most important thing is that the topic interests you. We are happy
				to give feedback if you would like to suggest an idea.
			</dd>
			<dt>- How long should the presentation be?</dt>
			<dd>20 minutes is a good target. Over 30 is too long.</dd>
			<dt>- Does it have to involve code/coding?</dt>
			<dd>absolutely not</dd>
			<dt>- Do I have to be filmed?</dt>
			<dd>
				No, it is just fine if you prefer not to be. Sometimes it is better to
				be on the safe side if the talk touches on a customer project.
			</dd>
			<dt>- Do I get a cool prize for this?</dt>
			<dd>Yep, you do. Check out our swag link below.</dd>
		</dl>
		<p>
			If you are interested in becoming a sponsor, please get in touch with
			<a class="underline" href="mailto:juhani.kumara@solita.fi">Juhani</a>
			,
			<a class="underline" href="mailto:leo.melin@mylab.fi">Leo</a>
			, or
			<a class="underline" href="mailto:richard.vancamp@gmail.com">Richard</a>
			and we will make a great event together!
		</p>
	</div>
</Blurb>
