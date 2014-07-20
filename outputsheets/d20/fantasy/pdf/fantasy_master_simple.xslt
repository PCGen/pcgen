<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id$ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:import href="fantasy_master_common_blocks.xslt"/>

	<xsl:output indent="yes"/>

	<xsl:template match="melee">
		<xsl:param name="column_width" select="'wide'" />
		<xsl:choose>
			<xsl:when test="w1_h1_p/to_hit = /character/export/invalidtext/tohit">
				<xsl:call-template name="simple_weapon">
					<xsl:with-param name="to_hit" select="w1_h2/to_hit"/>
					<xsl:with-param name="damage" select="w1_h2/damage"/>
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="hand='Both' ">
						<xsl:call-template name="simple_weapon">
							<xsl:with-param name="to_hit" select="w1_h2/to_hit"/>
							<xsl:with-param name="damage" select="w1_h2/damage"/>
							<xsl:with-param name="column_width" select="$column_width"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="simple_weapon">
							<xsl:with-param name="to_hit" select="w1_h1_p/to_hit"/>
							<xsl:with-param name="damage" select="w1_h1_p/damage"/>
							<xsl:with-param name="column_width" select="$column_width"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


</xsl:stylesheet>

