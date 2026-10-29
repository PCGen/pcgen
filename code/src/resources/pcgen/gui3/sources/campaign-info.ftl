<#ftl output_format="HTML">
<#--
  Campaign source-info card for the JavaFX source-selection WebView.
  Page model (built by ModernHtmlInfoBuilder):
    baseHref     - file: URL of the data dir, for the <base> tag (resolves relative image src)
    title        - campaign or selection display name (plain text)
    campaigns    - campaign models, each with the fields below
    title        - campaign display name (plain text)
    images       - list of { src, alt, kind } cover/logo images (src already resolved; kind is "cover"/"logo")
    status       - { label, color } for the status pill (color is #RRGGBB)
    facts        - ordered list of { label, valueHtml } short metadata (label plain, value trusted HTML)
    description  - prose blurb (plain text), or missing
    requirements - trusted HTML fragment ("<br><b>Requirements:</b>…"), or missing
    allow        - { label, valueHtml } trusted HTML, or missing
    sections     - ordered list of { heading, lines[], muted, columns } blocks (INFORMATION, COPYRIGHT, INCLUDED SOURCES)
    pccLabel/pccPath - the trailing PCCPATH footer (plain text)
  valueHtml / requirements / section lines are trusted HTML inserted with ?no_esc; everything
  else is plain text escaped by FreeMarker's HTML output format.
-->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <base href="${baseHref}">
  <style>
      :root {
          color-scheme: light dark;
          --ink: #1a1a1a;
          --muted: #555;
          --faint: #f2f2f2;
          --rule: #d8d8d8;
          --surface: #ffffff;
          --link: #1a5fb4;
      }

      @media (prefers-color-scheme: dark) {
          :root {
              --ink: #e8e8e8;
              --muted: #a8a8a8;
              --faint: #242424;
              --rule: #3a3a3a;
              --surface: #2b2b2b;
              --link: #77b6ff;
          }
      }

      html, body {
          margin: 0;
      }

      html {
          overflow-y: auto;
          overflow-x: hidden;
      }

      body {
          box-sizing: border-box;
          min-height: 100vh;
          font-family: -apple-system, "Segoe UI", Roboto, sans-serif;
          font-size: 13px;
          line-height: 1.5;
          color: var(--ink);
          background: var(--surface);
          /* No horizontal padding: full-bleed elements (h1, .card, pccpath) span
             the whole width; .wrap supplies the 16px side padding for text. This
             avoids negative-margin bleeds clipping asymmetrically vs. the scrollbar. */
          padding: 16px 0;
          overflow-wrap: anywhere;
      }

      .wrap {
          padding: 0 16px;
      }

      h1 {
          font-size: 1.35em;
          font-weight: 600;
          margin: 0 0 8px 0;
          padding: 0 16px 6px 16px;
          border-bottom: 1px solid var(--rule);
      }

      h2 {
          font-size: 0.85em;
          font-weight: 600;
          margin: 18px 0 4px 0;
          color: var(--muted);
          text-transform: uppercase;
          letter-spacing: 0.05em;
      }

      .campaign-title {
          padding: 0 16px;
          color: var(--ink);
          font-size: 1.2em;
          text-transform: none;
          letter-spacing: normal;
      }

      a {
          color: var(--link);
          text-decoration: none;
      }

      a:hover {
          text-decoration: underline;
      }

      /* Identity card (cover + status + facts). Breaks out of .wrap's side padding
         to sit flush against the pane edges; top/bottom borders only. The cover
         floats left and the status pill + facts wrap to its right, so the short
         metadata is visible without scrolling past the art. */
      .card {
          border-top: 1px solid var(--rule);
          border-bottom: 1px solid var(--rule);
          background: var(--surface);
          margin: 0 -16px 14px -16px;
          padding: 12px 14px;
      }

      /* Clear the floated cover so the card wraps its full height. */
      .card::after {
          content: "";
          display: block;
          clear: both;
      }

      .cover {
          float: left;
          margin: 0 16px 8px 0;
          max-width: 180px;
          max-height: 240px;
          width: auto;
          height: auto;
          object-fit: contain;
          border: 1px solid var(--rule);
      }

      /* Reserve room for readable metadata when the preview splitter is narrowed. */
      @media (max-width: 480px) {
          .cover {
              max-width: 25%;
              margin-right: 10px;
          }
      }

      .logo {
          display: block;
          margin: 8px 0 0 0;
          max-width: 120px;
          max-height: 48px;
          width: auto;
          height: auto;
          object-fit: contain;
      }

      .pill {
          display: inline-block;
          margin: 0 0 10px 0;
          padding: 2px 10px;
          border: 1px solid;
          font-size: 0.82em;
          font-weight: 600;
          letter-spacing: 0.02em;
      }

      .facts {
          display: grid;
          grid-template-columns: auto 1fr;
          column-gap: 12px;
          row-gap: 3px;
          margin: 0;
      }

      .facts dt {
          color: var(--muted);
          text-transform: uppercase;
          font-size: 0.72em;
          letter-spacing: 0.05em;
          font-weight: 600;
          padding-top: 2px;
          white-space: nowrap;
      }

      .facts dd {
          margin: 0;
      }

      .description {
          margin: 0 0 8px 0;
      }

      .requirements {
          margin: 8px 0;
      }

      .section-body {
          margin: 0;
      }

      .section-body.columns {
          columns: 3 180px;
          column-gap: 24px;
      }

      .muted {
          color: var(--muted);
          font-size: 0.85em;
      }

      .pccpath {
          margin: 16px 0 0 0;
          padding: 8px 16px 0 16px;
          border-top: 1px solid var(--rule);
          color: var(--muted);
          font-size: 0.75em;
          font-family: ui-monospace, "SF Mono", Menlo, monospace;
      }
  </style>
  <title>Campaign Preview</title>
</head>
<body>
<h1>${title}</h1>
<#list campaigns as campaign>
<#if campaigns?size gt 1>
<h2 class="campaign-title">${campaign.title}</h2>
</#if>
<div class="wrap">
<#if campaign.images?has_content || campaign.status?? || campaign.facts?has_content>
  <div class="card">
      <#list campaign.images as img><#if img.kind == "cover"><img class="cover" src="${img.src}" alt="${img.alt}"></#if></#list>
      <#if campaign.status??>
      <div><span class="pill" style="color:${campaign.status.color}; border-color:${campaign.status.color}; background:${campaign.status.color}22;">${campaign.status.label}</span></div>
      </#if>
      <#if campaign.facts?has_content>
      <dl class="facts">
          <#list campaign.facts as f>
          <dt>${f.label}</dt>
          <dd>${f.valueHtml?no_esc}</dd>
          </#list>
      </dl>
      </#if>
      <#list campaign.images as img><#if img.kind == "logo"><img class="logo" src="${img.src}" alt="${img.alt}"></#if></#list>
  </div>
</#if>
<#if campaign.description??>
  <div class="description">${campaign.description}</div>
</#if>
<#if campaign.requirements??>
  <div class="requirements">${campaign.requirements?no_esc}</div>
</#if>
<#if campaign.allow??>
  <div class="requirements"><b>${campaign.allow.label}:</b>&nbsp;${campaign.allow.valueHtml?no_esc}</div>
</#if>
<#list campaign.sections as s>
  <h2>${s.heading}</h2>
  <div class="section-body<#if s.muted> muted</#if><#if s.columns> columns</#if>">
      <#list s.lines as line>${line?no_esc}<br>
      </#list>
  </div>
</#list>
</div>
<div class="pccpath">${campaign.pccLabel}: ${campaign.pccPath}</div>
</#list>
</body>
</html>
