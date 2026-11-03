<#ftl output_format="HTML">
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <style>
    html, body {
      margin: 0;
      padding: 0;
      background: transparent;
      font-family: <#if systemFont>system-ui, sans-serif<#else>"${fontFamilyCss?no_esc}", sans-serif</#if>;
      font-size: ${fontSize?c}px;
      color: rgba(${red?c}, ${green?c}, ${blue?c}, ${opacity?c});
    }
  </style>
</head>
<body>${tipHtml?no_esc}</body>
</html>
