<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	>

	<xsl:import href="fantasy_common.xsl"/>
	<xsl:output indent="yes" media-type="xml" version="1.0" encoding="utf-8"/>
	
	<xsl:template name="attrib">
		<xsl:param name="attribute"/>
	</xsl:template>

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Landscape" page-height="3in" page-width="5in" margin-top="0.25in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="Landscape">
				<xsl:attribute name="font-family"><xsl:value-of select="$PCGenFont"/></xsl:attribute>
				<fo:flow flow-name="xsl-region-body">
					<xsl:apply-templates select=".//spell" mode="spell.card">
						<xsl:sort select="name"/>
					</xsl:apply-templates>
				</fo:flow>
			</fo:page-sequence>

		</fo:root>	
	</xsl:template>


</xsl:stylesheet>
