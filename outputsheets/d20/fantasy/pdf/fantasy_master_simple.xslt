<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id$ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:import href="fantasy_master_std.xslt" />

	<xsl:output indent="yes"/>



	<xsl:template match="melee">
		<xsl:param name="column_width" select="'wide'" />
		<xsl:call-template name="simple_weapon">
					<xsl:with-param name="to_hit" select="to_hit"/>
					<xsl:with-param name="damage" select="damage"/>
					<xsl:with-param name="column_width" select="$column_width"/>
		</xsl:call-template>
	</xsl:template>


</xsl:stylesheet>

