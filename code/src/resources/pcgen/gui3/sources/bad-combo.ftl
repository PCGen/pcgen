<#ftl output_format="HTML">
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>${title}</title>
  <style>
    body {
      font-family: "${fontFamily}", sans-serif;
      font-size: ${fontSize?c}px;
      margin: 0;
      padding: 16px 16px 0 0;
    }
  </style>
</head>
<body>
<#if rejections?size == 1 && !covered?has_content>
  <#assign details><@requirements report=rejections[0]/></#assign>
  ${messages("in_src_badComboMsg", details?markup_string?trim)?no_esc}
<#else>
  <#if rejections?has_content>
    <#assign rejectionList>
      <ul>
      <#list rejections as rejection>
        <li><b>${rejection.campaignName}</b><br><@requirements report=rejection/></li>
      </#list>
      </ul>
    </#assign>
    ${messages("in_src_badComboMsgMultiBody", rejectionList?markup_string)?no_esc}
  </#if>
  <#if covered?has_content>
    <#assign coveredList>
      <ul>
      <#list covered as entry>
        <#assign campaignName>${entry.campaignName}</#assign>
        <#assign containerName>${entry.containerName}</#assign>
        ${messages("in_src_alreadyIncludedEntry", campaignName?markup_string, containerName?markup_string)?no_esc}
      </#list>
      </ul>
    </#assign>
    ${messages("in_src_alreadyIncludedMsg", coveredList?markup_string)?no_esc}
  </#if>
</#if>
</body>
</html>

<#-- Captured names are auto-escaped before entering localized HTML patterns. -->
<#macro requirements report>
  <#if report.blockerNames?size == 1>
    <#assign name>${report.blockerNames[0]}</#assign>
    ${messages("in_src_blockedBySelected", name?markup_string)?no_esc}
  <#elseif report.blockerNames?has_content>
    <#assign names><#list report.blockerNames as name><b>${name}</b><#sep>, </#list></#assign>
    ${messages("in_src_blockedBySelectedMulti", names?markup_string)?no_esc}
  <#else>
    ${report.requirementsHtml?no_esc}
  </#if>
</#macro>
