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
				<fo:simple-page-master master-name="Portrait" page-height="5in" page-width="9in" margin-top="0in" margin-bottom="0in" margin-left="0in" margin-right="0in" reference-orientation="90">
					<fo:region-body column-count="3" column-gap="1.0in" margin="0.5in"/>
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="Portrait">
				<xsl:attribute name="font-family"><xsl:value-of select="$PCGenFont"/></xsl:attribute>
				<fo:flow flow-name="xsl-region-body">
					<xsl:apply-templates select=".//spell" mode="spell.card">
						<xsl:sort select="name"/>
						<xsl:with-param name="break" select="'column'"/>
					</xsl:apply-templates>
				</fo:flow>
			</fo:page-sequence>

		</fo:root>	
	</xsl:template>

</xsl:stylesheet>


