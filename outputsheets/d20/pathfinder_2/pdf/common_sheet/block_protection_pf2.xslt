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
		TEMPLATE - Protection
	====================================
	====================================-->
	<xsl:template match="protection">
		<xsl:if test="count(armor|shield|item) &gt; 0" >
		<!-- BEGIN Armor table -->
		<fo:table table-layout="fixed" width="100%" border-collapse="collapse" space-before="2mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'protection.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 49" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-header>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'protection.title'"/></xsl:call-template>
					<fo:table-cell padding-top="1pt">
						<fo:block font-size="7pt">
							ARMOR
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							TYPE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							AC
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							MAXDEX
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							CHECK
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							n/a
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:for-each select="armor|shield|item">
					<xsl:if test="(not(contains(fulltype,'BARDING'))) or (contains(location,'Equipped'))">
					<xsl:variable name="shade">
						<xsl:choose>
							<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
							<xsl:otherwise>lightline</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<fo:table-row>
											<xsl:message>Test</xsl:message>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('protection.', $shade)"/></xsl:call-template>
						<fo:table-cell>
							<fo:block font-size="8pt">
								<xsl:value-of select="name"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block font-size="8pt">
								<xsl:value-of select="type"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block font-size="8pt">
								<xsl:value-of select="totalac"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block font-size="8pt">
								<xsl:value-of select="maxdex"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block font-size="8pt">
								<xsl:value-of select="accheck"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block font-size="8pt">
								
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
											<xsl:message>Test END</xsl:message>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="concat('protection.', $shade)"/>
						</xsl:call-template>
						<fo:table-cell number-columns-spanned="6" text-align="center">
							<fo:block font-size="6pt">
								<xsl:value-of select="special_properties"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					</xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>
