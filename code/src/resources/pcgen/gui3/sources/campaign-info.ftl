<#ftl output_format="HTML">
<#--
  Campaign source-info card for the JavaFX source-selection WebView.
  Model (built by ModernHtmlInfoBuilder):
    baseHref     - file: URL of the data dir, for the <base> tag (resolves relative image src)
    maxImagePx   - max rendered cover/logo edge, so large art cannot blow out the layout
    title        - campaign display name (plain text)
    images       - list of { src, alt } cover/logo images (src already resolved)
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

      a {
          color: var(--link);
          text-decoration: none;
      }

      a:hover {
          text-decoration: underline;
      }

      /* Identity card (hero + status + facts). Breaks out of .wrap's side padding
         to sit flush against the pane edges; top/bottom borders only. */
      .card {
          border-top: 1px solid var(--rule);
          border-bottom: 1px solid var(--rule);
          background: var(--surface);
          overflow: hidden;
          margin: 0 -16px 14px -16px;
      }

      .hero {
          display: flex;
          flex-wrap: wrap;
          gap: 16px;
          align-items: center;
          padding: 10px 14px;
          background: var(--faint);
          border-bottom: 1px solid var(--rule);
      }

      .hero img {
          max-width: ${maxImagePx}px;
          max-height: 56px;
          width: auto;
          height: auto;
          object-fit: contain;
      }

      .card-body {
          padding: 12px 14px;
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
<div class="wrap">
<#if images?has_content || status?? || facts?has_content>
  <div class="card">
      <#if images?has_content>
      <div class="hero">
          <#list images as img><img src="${img.src}" alt="${img.alt}"></#list>
      </div>
      </#if>
      <div class="card-body">
          <#if status??>
          <div><span class="pill" style="color:${status.color}; border-color:${status.color}; background:${status.color}22;">${status.label}</span></div>
          </#if>
          <#if facts?has_content>
          <dl class="facts">
              <#list facts as f>
              <dt>${f.label}</dt>
              <dd>${f.valueHtml?no_esc}</dd>
              </#list>
          </dl>
          </#if>
      </div>
  </div>
</#if>
<#if description??>
  <div class="description">${description}</div>
</#if>
<#if requirements??>
  <div class="requirements">${requirements?no_esc}</div>
</#if>
<#if allow??>
  <div class="requirements"><b>${allow.label}:</b>&nbsp;${allow.valueHtml?no_esc}</div>
</#if>
<#list sections as s>
  <h2>${s.heading}</h2>
  <div class="section-body<#if s.muted> muted</#if><#if s.columns> columns</#if>">
      <#list s.lines as line>${line?no_esc}<br>
      </#list>
  </div>
</#list>
</div>
<div class="pccpath">${pccLabel}: ${pccPath}</div>
</body>
</html>
