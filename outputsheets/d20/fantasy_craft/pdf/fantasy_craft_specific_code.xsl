<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:xalan="http://xml.apache.org/xalan" 
	>

  	<xsl:import href="inc_pagedimensions.xslt"/>




	<!--
====================================
====================================
	TEMPLATE - Proficiency Check Box Style
====================================
====================================-->
	<xsl:template match="proficiency_specials">
	<!--	
		&#x25A0;	< Checked Box >
		&#x274F;	< Unchecked Box >
		-->
	
		<!-- BEGIN proficiency Special -->
		<fo:table table-layout="fixed" space-before="2mm" keep-together="always" border-collapse="collapse" >
			<fo:table-column column-width="5mm"/> <!-- Symbol -->
			<fo:table-column column-width="5mm"/> <!-- Space -->
			<fo:table-column column-width="5mm"/> <!-- Symbol -->
			<fo:table-column column-width="5mm"/> <!-- Space -->
			<fo:table-column column-width="25mm"/> <!-- Name -->
			<fo:table-body>
			<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'rage.border'"/>
						</xsl:call-template>
				<fo:table-row keep-with-next.within-column="always">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'rage.title'"/>
					</xsl:call-template>
					<fo:table-cell padding="3pt" number-columns-spanned="2">
						<fo:block font-size="5pt" font-weight="bold">
							<xsl:text> Proficient </xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="3pt" number-columns-spanned="2">
						<fo:block font-size="5pt" font-weight="bold">
							<xsl:text> Forte </xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="3pt" number-columns-spanned="1">
						<fo:block font-size="5pt" font-weight="bold">
							<xsl:text> Proficiency </xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			<xsl:for-each select="proficiency">
			
				<fo:table-row keep-with-next.within-column="always">

					<fo:table-cell padding-top="1pt" number-columns-spanned="1">
					<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'rage.title'"/>
						</xsl:call-template>
						<fo:block font-size="10pt" font-family="ZapfDingbats">
						<!--	<xsl:value-of select="proficient"/>-->
							<xsl:choose>
								<xsl:when test="proficient &gt; 0">
									&#x25A0;
								</xsl:when>
								<xsl:otherwise>
									&#x274F;
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
				
					<fo:table-cell><xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'rage.title'"/>
						</xsl:call-template></fo:table-cell>
					<fo:table-cell padding-top="1pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'rage.title'"/>
						</xsl:call-template>
				<!-->		<fo:block font-size="10pt" font-weight="bold" font-family="ZapfDingbats">-->
						<!--	<xsl:value-of select="forte"/>-->
							<xsl:choose>
								<xsl:when test="proficient &gt; 1">
									<fo:block font-size="10pt" font-family="ZapfDingbats">
										<fo:inline font-family="ZapfDingbats" font-size="10pt" border="1pt black solid">&#x25A0;</fo:inline>
									</fo:block>
								</xsl:when>
								<xsl:otherwise>
									<fo:block font-size="10pt" font-family="ZapfDingbats">
									<fo:inline font-family="ZapfDingbats" font-size="10pt" border="1pt black solid">&#x274F;</fo:inline>
									</fo:block>
								</xsl:otherwise>
							</xsl:choose>
						<!--</fo:block>-->
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'rage.title'"/>
						</xsl:call-template>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'rage.title'"/>
						</xsl:call-template>
						<fo:block font-size="10pt" font-weight="bold">
							<xsl:value-of select="name"/>
						</fo:block>
					</fo:table-cell>
			
				</fo:table-row>
			</xsl:for-each>
			</fo:table-body>
		</fo:table>
		<!-- END Special Proficiency table -->
	
	</xsl:template>

</xsl:stylesheet>
