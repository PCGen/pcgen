<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:Psionics="my:Psionics"
	xmlns:myAttribs="my:Attribs"
	exclude-result-prefixes="myAttribs Psionics"
	>

	<xsl:import href="../../../../xsltsl-1.1/stdlib.xsl"/>
	<xsl:import href="../inc_pagedimensions.xslt"/>


	<!--
	====================================
	====================================
		TEMPLATE - PSIONICS ATTACK / DEFENCE TABLE
	====================================
	====================================-->
	<Psionics:attacks>
		<attack name="ego.whip" title="Ego Whip" damage="1d4 DEX" pp="3"/>
		<attack name="id.insinuation" title="Id Insinuation" damage="1d2 STR" pp="3"/>
		<attack name="mind.blast" title="Mind Blast" damage="1d4 CHA" pp="9"/>
		<attack name="mind.thrust" title="Mind Thrust" damage="1d2 INT" pp="1"/>
		<attack name="psychic.crush" title="Psychic Crush" damage="2d4 WIS" pp="5"/>
		<defences>
			<defence name="Empty Mind" pp="1" mentalhardness="">
				<attack name="ego.whip" value="+1" />
				<attack name="id.insinuation" value="-2" />
				<attack name="mind.blast" value="+3" />
				<attack name="mind.thrust" value="-3" />
				<attack name="psychic.crush" value="-5" />
			</defence>
			<defence name="Intellect Fortress" pp="5" mentalhardness="3">
				<attack name="ego.whip" value="-2" />
				<attack name="id.insinuation" value="+1" />
				<attack name="mind.blast" value="+0" />
				<attack name="mind.thrust" value="+6" />
				<attack name="psychic.crush" value="+4" />
			</defence>
			<defence name="Mental Barrier" pp="3" mentalhardness="2">
				<attack name="ego.whip" value="-1" />
				<attack name="id.insinuation" value="+4" />
				<attack name="mind.blast" value="-3" />
				<attack name="mind.thrust" value="+1" />
				<attack name="psychic.crush" value="+3" />
			</defence>
			<defence name="Thought Shield" pp="1" mentalhardness="1">
				<attack name="ego.whip" value="-4" />
				<attack name="id.insinuation" value="-1" />
				<attack name="mind.blast" value="-2" />
				<attack name="mind.thrust" value="+4" />
				<attack name="psychic.crush" value="+2" />
			</defence>
			<defence name="Tower of Iron Will" pp="5" mentalhardness="2">
				<attack name="ego.whip" value="+3" />
				<attack name="id.insinuation" value="+0" />
				<attack name="mind.blast" value="-1" />
				<attack name="mind.thrust" value="+5" />
				<attack name="psychic.crush" value="-3" />
			</defence>
			<defence name="Nonpsionic Buffer" pp="" mentalhardness="">
				<attack name="ego.whip" value="-8" />
				<attack name="id.insinuation" value="-9" />
				<attack name="mind.blast" value="+4" />
				<attack name="mind.thrust" value="-8" />
				<attack name="psychic.crush" value="-8" />
			</defence>
			<defence name="Flat-footed or out of Power Points" pp="" mentalhardness="">
				<attack name="ego.whip" value="+8" />
				<attack name="id.insinuation" value="+7" />
				<attack name="mind.blast" value="+8" />
				<attack name="mind.thrust" value="+8" />
				<attack name="psychic.crush" value="+8" />
			</defence>
		</defences>
	</Psionics:attacks>
	<!--
====================================
====================================
	TEMPLATE - PSIONICS
====================================
====================================-->
	<xsl:template name="psionic.entry">
		<xsl:param name="title"/>
		<xsl:param name="title.cols" select="1"/>
		<xsl:param name="value"/>
		<xsl:param name="value.cols" select="1"/>
		<fo:table-cell padding-top="1pt" text-align="end">
			<xsl:attribute name="number-columns-spanned"><xsl:value-of select="$title.cols"/></xsl:attribute>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
			<fo:block font-size="8pt"><xsl:value-of select="$title"/>:</fo:block>
		</fo:table-cell>
		<fo:table-cell padding-top="1pt" text-align="center">
			<xsl:attribute name="number-columns-spanned"><xsl:value-of select="$value.cols"/></xsl:attribute>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics'"/></xsl:call-template>
			<fo:block font-size="8pt"><xsl:value-of select="$value"/></fo:block>
		</fo:table-cell>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PSIONICS
====================================
====================================-->
	<xsl:template match="psionics">
		<!-- BEGIN psionicsTable -->
		<xsl:variable name="endpoints" select="7"/>
		<fo:table table-layout="fixed" space-before="2mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="0.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="0.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="0.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="0.5mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="1pt" number-columns-spanned="12">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
						<fo:block font-size="10pt" font-weight="bold">Psionics</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'Base PP'"/>
						<xsl:with-param name="value" select="base_pp"/>
					</xsl:call-template>
					<fo:table-cell/>
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'BonusPP'"/>
						<xsl:with-param name="value" select="bonus_pp"/>
					</xsl:call-template>
					<fo:table-cell/>
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'Total PP'"/>
						<xsl:with-param name="value" select="total_pp"/>
					</xsl:call-template>
					<fo:table-cell/>
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'Current PP'"/>
						<xsl:with-param name="value" select="''"/>
					</xsl:call-template>
					<fo:table-cell/>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	<xsl:if test = "type = '3.0'">
		<!-- Attack / Defence table -->
		<fo:table table-layout="fixed" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 70" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="34mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
						<fo:block font-size="5pt"/>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
						<fo:block font-size="5pt">Mental Hardness</fo:block>
					</fo:table-cell>
					<xsl:variable name="attacks" select="document('')/*/Psionics:attacks/attack"/>
					<xsl:for-each select="$attacks">
						<fo:table-cell padding-top="1pt">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
							<fo:block font-size="5pt"><xsl:value-of select="@title"/></fo:block>
							<fo:block font-size="4pt">(<xsl:value-of select="@damage"/>) <xsl:value-of select="@pp"/>pp</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>

				<xsl:variable name="defences" select="document('')/*/Psionics:attacks/defences/defence"/>
				<xsl:for-each select="$defences">
					<fo:table-row keep-with-previous.within-column="always">
											<xsl:message>Test END</xsl:message>
						<fo:table-cell padding-top="1pt" padding-left="3pt">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
							<fo:block font-size="7pt"><xsl:value-of select="@name"/>
								<xsl:if test="@pp != ''"> (<xsl:value-of select="@pp"/>pp)</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics'"/></xsl:call-template>
							<fo:block font-size="7pt"><xsl:value-of select="@mentalhardness"/></fo:block>
						</fo:table-cell>
						<xsl:for-each select="attack">
							<fo:table-cell padding-top="1pt">
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics'"/></xsl:call-template>
								<fo:block font-size="7pt"><xsl:value-of select="@value"/></fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
		<!-- END psionicsTable -->
	</xsl:if>
	</xsl:template>

</xsl:stylesheet>