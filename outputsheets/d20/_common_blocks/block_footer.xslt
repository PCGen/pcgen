<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:xalan="http://xml.apache.org/xalan"
	>

	<xsl:import href="../../../../xsltsl-1.1/stdlib.xsl"/>
	<xsl:import href="../inc_pagedimensions.xslt"/>

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



</xsl:stylesheet>