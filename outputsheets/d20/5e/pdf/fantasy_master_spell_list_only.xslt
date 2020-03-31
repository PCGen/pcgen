<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:myAttribs="my:Attribs"
	xmlns:Psionics="my:Psionics"
	exclude-result-prefixes="myAttribs Psionics">
<!-- 		-->

	<xsl:import href="fantasy_common.xsl"/>


<!-- Blocks to put together PC Sheet -->
	<xsl:import href="common_sheet/block_spells_list.xslt"/>	
<!-- END -->

	<xsl:output indent="yes"/>

	<xsl:variable name="vAttribs_tree">
		<myAttribs:myAttribs>
			<xsl:copy-of select="$vAttribs/*"/>
		</myAttribs:myAttribs>
	</xsl:variable>
	<xsl:variable name="vAttribs_all" select="xalan:nodeset($vAttribs_tree)"/>
	<xsl:variable name="pageHeight">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/height, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/height, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/height, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/height, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/height, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/height, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains(/character/export/paperinfo/name, 'Letter')">
						<xsl:value-of select="25.4 * 11.0"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="297"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageWidth">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/width, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/width, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/width, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/width, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/width, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/width, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains(/character/export/paperinfo/name, 'Letter')">
						<xsl:value-of select="25.4 * 8.5"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="210"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginTop">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/top, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/top, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/top, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/top, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/top, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/top, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginBottom">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/bottom, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/bottom, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/bottom, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/bottom, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/bottom, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/bottom, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginLeft">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/left, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/left, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/left, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/left, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/left, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/left, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginRight">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/right, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/right, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/right, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/right, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/right, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/right, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pagePrintableWidth">
		<xsl:value-of select="($pageWidth - $pageMarginLeft - $pageMarginRight)"/>
	</xsl:variable>
	<xsl:variable name="pagePrintableHeight">
		<xsl:value-of select="($pageHeight - $pageMarginTop - $pageMarginBottom)"/>
	</xsl:variable>
	<xsl:variable name="skillmastery">
		<xsl:for-each select="/character/special_qualities/special_quality">
			<xsl:if test="substring(name,1,13)='Skill Mastery'">
				<xsl:value-of select="associated"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>


	<!-- Include all of the output attributes -->
	<!-- vAttribs will be set up in the stylesheet that calls this one -->
	<xsl:template name="attrib">
		<xsl:param name="attribute"/>
		<xsl:copy-of select="$vAttribs_all/*/*[name() = $attribute]/@*"/>
		<xsl:for-each select="$vAttribs_all/*/*[name() = $attribute]/subattrib/@*">
			<xsl:variable name="bar" select="name()"/>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="$bar"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="page.layouts">
			<!--	PAGE DEFINITIONS	-->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Portrait 2 Column">
					<xsl:attribute name="page-height"><xsl:value-of select="$pageHeight" />mm</xsl:attribute>
					<xsl:attribute name="page-width"><xsl:value-of select="$pageWidth" />mm</xsl:attribute>
					<xsl:attribute name="margin-top"><xsl:value-of select="$pageMarginTop" />mm</xsl:attribute>
					<xsl:attribute name="margin-bottom"><xsl:value-of select="$pageMarginBottom" />mm</xsl:attribute>
					<xsl:attribute name="margin-left"><xsl:value-of select="$pageMarginLeft" />mm</xsl:attribute>
					<xsl:attribute name="margin-right"><xsl:value-of select="$pageMarginRight" />mm</xsl:attribute>
					<fo:region-body region-name="body" column-count="2" column-gap="2mm" margin-bottom="7mm"/>
					<fo:region-after region-name="footer" extent="4.4mm"/>
				</fo:simple-page-master>
				<fo:simple-page-master master-name="Portrait">
					<xsl:attribute name="page-height"><xsl:value-of select="$pageHeight" />mm</xsl:attribute>
					<xsl:attribute name="page-width"><xsl:value-of select="$pageWidth" />mm</xsl:attribute>
					<xsl:attribute name="margin-top"><xsl:value-of select="$pageMarginTop" />mm</xsl:attribute>
					<xsl:attribute name="margin-bottom"><xsl:value-of select="$pageMarginBottom" />mm</xsl:attribute>
					<xsl:attribute name="margin-left"><xsl:value-of select="$pageMarginLeft" />mm</xsl:attribute>
					<xsl:attribute name="margin-right"><xsl:value-of select="$pageMarginRight" />mm</xsl:attribute>
					<fo:region-body region-name="body" margin-bottom="7mm"/>
					<fo:region-after region-name="footer" extent="4.4mm"/>
				</fo:simple-page-master>
	
		</fo:layout-master-set>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PAGE FOOTER
====================================
====================================-->
	<xsl:template name="page.footer">
		<fo:static-content flow-name="footer" font-family="sans-serif">
			<xsl:call-template name="page.footer.content"/>
		</fo:static-content>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - PAGE FOOTER
====================================
====================================-->
	<xsl:template name="page.footer.content">
		<fo:table table-layout="fixed">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.25 * $pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.5 * $pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.25 * $pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next="always" keep-together="always">
					<fo:table-cell text-align="start" wrap-option="no-wrap" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block font-size="5pt">Character: <fo:inline font-weight="bold"><xsl:value-of select="/character/basics/name"/></fo:inline></fo:block>
						<fo:block font-size="5pt">Player: <fo:inline font-weight="bold"><xsl:value-of select="/character/basics/playername"/></fo:inline></fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center" wrap-option="no-wrap" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block text-align="center" font-size="5pt">PCGen Character Template by Frugal, based on work by ROG, Arcady, Barak, Dimrill, Dekker &amp; Andrew Maitland (LegacyKing).</fo:block>
						<fo:block text-align="center" font-size="5pt">Created using <fo:basic-link external-destination="http://pcgen.org/" color="blue" text-decoration="underline">PCGen</fo:basic-link> v<xsl:value-of select="/character/export/version"/> on <xsl:value-of select="/character/export/date"/><xsl:text> at </xsl:text><xsl:value-of select="/character/export/time"/></fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block font-size="5pt">
						Level:<xsl:value-of select="/character/basics/classes/levels_total"/> (CR:<xsl:value-of select="/character/basics/cr"/>)</fo:block>
						<fo:block font-size="5pt">Page <fo:page-number/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--		Start the character		-->
	<xsl:template match="character">
		<!-- calculate the number of weapons and skills on the first page -->
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<xsl:call-template name="page.layouts"/>
			<!--
				Start the first page
				-->
			<fo:page-sequence>
				<xsl:attribute name="master-reference">Portrait</xsl:attribute>
				<xsl:call-template name="page.footer"/>
				<!--	CHARACTER BODY STARTS HERE !!!	-->
				<fo:flow flow-name="body" font-size="8pt">
					<!--	CHARACTER HEADER	-->
					<fo:block span="all" space-after.optimum="3pt">
 <!-- We Display only the Spells in List Form -->
						<xsl:apply-templates select="spells"/>	
					</fo:block>
					<fo:block span="all">
						<fo:table table-layout="fixed">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.29 * $pagePrintableWidth" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.26 * $pagePrintableWidth" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.45 * $pagePrintableWidth" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="2">
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<!-- End Character -->
</xsl:stylesheet>
