<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:xalan="http://xml.apache.org/xalan"
	>

	<xsl:import href="../../../xsltsl-1.1/stdlib.xsl"/>
  	<xsl:import href="inc_pagedimensions.xslt"/>

	<xsl:variable name="PCGenFont">Noto Sans</xsl:variable>
	<xsl:template match="spell" mode="spell.card">
		<xsl:param name="break" select="'page'" />
		<fo:block>
			<xsl:if test="position() != last()">
				<xsl:attribute name="break-after"><xsl:value-of select="$break"/></xsl:attribute>
			</xsl:if>

			<fo:block font-size="14pt" space-before="2mm" font-weight="bold" margin-left="5mm" text-indent="-5mm"><xsl:value-of select="bonusspell"/> <xsl:value-of select="name"/></fo:block>
			<fo:block font-size="10pt" text-indent="5mm"><xsl:value-of select="school/fullschool"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Level: </fo:inline><xsl:value-of select="source/sourcelevel"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Components: </fo:inline><xsl:value-of select="components"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Casting Time: </fo:inline><xsl:value-of select="castingtime"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Range: </fo:inline><xsl:value-of select="range"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Target: </fo:inline><xsl:value-of select="target"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Duration: </fo:inline><xsl:value-of select="duration"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Saving Throw: </fo:inline><xsl:value-of select="saveinfo"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Spell Resistance: </fo:inline><xsl:value-of select="spell_resistance"/></fo:block>
			<fo:block font-size="10pt" margin-left="5mm" text-indent="-5mm"><fo:inline font-weight="bold">Effect: </fo:inline><xsl:value-of select="effect"/></fo:block>
			<fo:block font-size="5pt" text-indent="3mm"><xsl:value-of select="source/source"/></fo:block>

		</fo:block>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - GENERIC OUTPUT-FOR-LOOP
====================================
====================================-->
	<xsl:template name="for.loop">
		<xsl:param name="i" select="1"/>
		<xsl:param name="count" select="0"/>
		<xsl:param name="display" select="'&#x274F;'"/>
		<xsl:if test="$i &lt;= $count">
			<!-- Show this box -->
			<xsl:value-of select="$display"/>
			<xsl:if test="$i mod 5 = 0">
				<xsl:text> </xsl:text>
			</xsl:if>
			<!-- Show all of the remaining boxes -->
			<xsl:call-template name="for.loop">
				<xsl:with-param name="i" select="$i + 1"/>
				<xsl:with-param name="display" select="$display"/>
				<xsl:with-param name="count" select="$count"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - GENERIC OUTPUT Cumulative total
====================================
====================================-->
	<xsl:template name="Total">
		<xsl:param name="Items"/>
		<xsl:param name="RunningTotal"/>
		<xsl:choose>
			<xsl:when test="not($Items)">
				<!-- No more Items so return Running Total -->
				<xsl:copy-of select="$RunningTotal"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- Call template for remaining Items -->
				<xsl:variable name="CurrentTotal" select="$RunningTotal + ($Items[1]/quantity * $Items[1]/cost)"/>
				<xsl:call-template name="Total">
					<xsl:with-param name="Items" select="$Items[position()>1]"/>
					<xsl:with-param name="RunningTotal" select="$CurrentTotal"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



<xsl:template match="*">
	<xsl:call-template name="process.attack.string">
		<xsl:with-param name="bab" select="'+7'" />
        <xsl:with-param name="maxrepeat" select="4" />
	</xsl:call-template>
</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - GENERIC Process
====================================
====================================-->

<!-- use "attack" for what you want to Output
          "bab" for the character's bab (and so the limit on reiterative attacks)
     backwards compatible with the default of attack and bab being the same.
   - Tir Gwaith
-->
	<xsl:template name="process.attack.string">
		<xsl:param name="bab"/>
        <xsl:param name="maxrepeat"/>
		<xsl:param name="attack" select="$bab"/>
		<xsl:param name="string" select="''"/>

		<xsl:choose>
			<xsl:when test="starts-with($attack, '+')">
				<xsl:call-template name="process.attack.string">
					<xsl:with-param name="attack" select="substring($attack, 2)"/>
					<xsl:with-param name="bab" select="substring($bab, 2)"/>
                    <xsl:with-param name="maxrepeat" select="$maxrepeat - 1"/>
					<xsl:with-param name="string" select="$string"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="($bab &gt; 5) and ($maxrepeat &gt; 0)">
						<xsl:call-template name="process.attack.string">
							<xsl:with-param name="attack" select="$attack - 5"/>
							<xsl:with-param name="bab" select="$bab - 5"/>
                            <xsl:with-param name="maxrepeat" select="$maxrepeat - 1"/>
							<xsl:with-param name="string">
								<xsl:value-of select="$string"/><xsl:if test="starts-with($attack, '-')=false">+</xsl:if><xsl:value-of select="$attack"/>/</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$string"/><xsl:if test="starts-with($attack, '-')=false">+</xsl:if><xsl:value-of select="$attack"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - LIST
====================================
====================================-->
	<xsl:template name="shade">
		<xsl:choose>
			<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
			<xsl:otherwise>lightline</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



	<!--
====================================
====================================
	TEMPLATE - LIST
====================================
====================================-->
	<xsl:template name="list">
		<xsl:param name="attribute"/>
		<xsl:param name="title" />
		<xsl:param name="value" />

		<fo:table table-layout="fixed" width="100%" space-before.optimum="2mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 2" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="9pt"><xsl:value-of select="$title"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
						<xsl:for-each select="$value">
							<fo:block font-size="7pt">
								<xsl:value-of select="."/>
							</fo:block>
						</xsl:for-each>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - STRIPPED LIST
====================================
====================================-->
	<xsl:template name="stripped.list">
		<xsl:param name="attribute"/>
		<xsl:param name="title" />
		<xsl:param name="list" />
		<xsl:param name="name.tag" />
		<xsl:param name="desc.tag" select="''" />
		<xsl:param name="col1width" select="0.36 * ($pagePrintableWidth - 2) div 2"/>
		<xsl:param name="col2width" select="0.64 * ($pagePrintableWidth - 2) div 2"/>

		<fo:table table-layout="fixed" width="100%" space-before="2mm" border-collapse="collapse">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="$col1width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="$col2width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="9pt"><xsl:value-of select="$title"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="$list">
					<xsl:variable name="shade">
						<xsl:choose>
							<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
							<xsl:otherwise>lightline</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:if test="string-length(./*[name()=$name.tag]) &gt; 1">
						<fo:table-row keep-with-next.within-column="always">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
							<fo:table-cell padding="1pt">
								<xsl:if test="$desc.tag=''"><xsl:attribute name="number-columns-spanned">2</xsl:attribute></xsl:if>
								<fo:block font-size="7pt"><xsl:value-of select="./*[name()=$name.tag]"/></fo:block>
							</fo:table-cell>
							<xsl:if test="$desc.tag!=''">
								<fo:table-cell padding="1pt">
									<fo:block font-size="7pt" text-align="justify" text-indent="5pt">
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$desc.tag"/>
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
							</xsl:if>
						</fo:table-row>
					</xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - BOLD LIST
====================================
====================================-->
	<xsl:template name="bold.list">
		<xsl:param name="attribute"/>
		<xsl:param name="title" />
		<xsl:param name="list" />
		<xsl:param name="name.tag" />
		<xsl:param name="desc.tag" select="''" />
		<xsl:param name="benefit.tag" select="''" />

		<fo:table table-layout="fixed" width="100%" space-before="2mm" border-collapse="collapse">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="3">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="9pt"><xsl:value-of select="$title"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="$list">
					<xsl:variable name="shade">
						<xsl:choose>
							<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
							<xsl:otherwise>lightline</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:if test="string-length(./*[name()=$name.tag]) &gt; 1">
						<fo:table-row>	<!--	 keep-with-next.within-column="always"	-->
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
							<xsl:choose>
								<xsl:when test="source!=''">
									<fo:table-cell padding="0pt" number-columns-spanned="2">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
										<fo:block font-size="7pt" font-weight="bold"><xsl:value-of select="./*[name()=$name.tag]"/></fo:block>
									</fo:table-cell>
									<fo:table-cell padding="0pt" text-align="end">
										<fo:block  font-size="7pt" font-weight="bold">[<xsl:value-of select="source"/>]</fo:block>
									</fo:table-cell>
								</xsl:when>
								<xsl:otherwise>
									<fo:table-cell number-columns-spanned="3" padding="0pt">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
										<fo:block font-size="7pt" font-weight="bold"><xsl:value-of select="./*[name()=$name.tag]"/></fo:block>
									</fo:table-cell>
								</xsl:otherwise>
							</xsl:choose>
						</fo:table-row>

						<xsl:if test="$desc.tag!=''">
							<fo:table-row>	<!--	 keep-with-next.within-column="always"	-->
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
								<fo:table-cell padding="1pt" number-columns-spanned="3">
									<fo:block font-size="7pt" text-align="justify" text-indent="5pt">
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$desc.tag"/>
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>

						<xsl:if test="$benefit.tag!=''">
							<fo:table-row>	<!--	 keep-with-next.within-column="always"	-->
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
								<fo:table-cell padding="1pt" number-columns-spanned="3">
									<fo:block font-size="7pt" text-align="justify" text-indent="5pt">
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$benefit.tag"/>
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - POWER
====================================
====================================-->
	<xsl:template name="power.list">
		<xsl:param name="attribute"/>
		<xsl:param name="title" />
		<xsl:param name="list" />
		<xsl:param name="name.tag" />
		<xsl:param name="desc.tag" />
		<xsl:param name="power_use.tag"  />
		<xsl:param name="power_type.tag"  />
		<xsl:param name="action_type.tag"  />
		<xsl:param name="special.tag" />
		<xsl:param name="target.tag"  />
		<xsl:param name="trigger.tag"  />
		<xsl:param name="attack.tag"  />
		<xsl:param name="hit.tag"  />
		<xsl:param name="miss.tag"  />
		<xsl:param name="effect.tag"  />
		<xsl:param name="sustain.tag"  />

		<fo:table table-layout="fixed" width="100%" space-before="2mm" border-collapse="collapse">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 2" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="9pt"><xsl:value-of select="$title"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="$list">
					<xsl:variable name="shade">
						<xsl:choose>
							<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
							<xsl:otherwise>lightline</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:if test="string-length(./*[name()=$name.tag]) &gt; 1">
						<fo:table-row keep-with-next.within-column="always">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
							<fo:table-cell padding="1pt">
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
								<fo:block font-size="9pt"  text-align="left" font-weight="bold">
									<xsl:value-of select="./*[name()=$name.tag]"/>
								</fo:block>
								<fo:block font-size="7pt" text-align="right" font-weight="bold">
									<xsl:value-of select="./*[name()=$power_type.tag]"/>
								</fo:block>
								<xsl:if test="string-length(./*[name()=$desc.tag]) &gt; 0">
									<fo:block font-size="5pt" font-style="italic"  text-align="center" >
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$desc.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$power_use.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$power_use.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$action_type.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Action Type  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$action_type.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$special.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Special  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$special.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$target.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Target  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$target.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$attack.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Attack  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$attack.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$trigger.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Trigger  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$trigger.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$hit.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Hit  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$hit.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$miss.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Miss  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$miss.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$effect.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Effect  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$effect.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$sustain.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Sustain  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$sustain.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
							</fo:table-cell>
						</fo:table-row>
					</xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - Deity Display
====================================
====================================-->
	<xsl:template name="deity.display">
		<xsl:param name="attribute"/>
		<xsl:param name="title" />
		<xsl:param name="deitytitle.tag"  />
		<xsl:param name="list" />
		<xsl:param name="name.tag" />
		<xsl:param name="desc.tag" />
		<xsl:param name="alignment.tag"  />
		<xsl:param name="domainlist.tag"  />
		<xsl:param name="favoredweapon.tag"  />
		<xsl:param name="holyitem.tag" />
		<xsl:param name="pantheonlist.tag"  />
		<xsl:param name="source.tag"  />
		<xsl:param name="special_abilities.tag"  />
		<xsl:param name="appearance.tag"  />
		<xsl:param name="worshippers.tag"  />

		<fo:table table-layout="fixed" width="100%" space-before="2mm" border-collapse="collapse">
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="concat($attribute, '.border')"/>
			</xsl:call-template>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 2" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="1">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="9pt"><xsl:value-of select="$title"/></fo:block>
					</fo:table-cell>
				</fo:table-row>

					<xsl:variable name="shade">
						<xsl:choose>
							<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
							<xsl:otherwise>lightline</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

<!-->					<xsl:if test="string-length(./*[name()=$name.tag]) &gt; 1">	-->
						<fo:table-row keep-with-next.within-column="always">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
							<fo:table-cell padding="1pt">
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
								<fo:block font-size="9pt"  text-align="left" font-weight="bold">
									<xsl:value-of select="./*[name()=$name.tag]"/>
								</fo:block>
								<fo:block font-size="7pt" text-align="right" font-weight="bold">
									<xsl:value-of select="./*[name()=$deitytitle.tag]"/>
								</fo:block>
								<xsl:if test="string-length(./*[name()=$desc.tag]) &gt; 0">
									<fo:block font-size="5pt" font-style="italic"  text-align="center" >
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$desc.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
						<!-->		<xsl:if test="string-length(./*[name()=$alignment.tag]) &gt; 0">	-->
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$alignment.tag"/>
										</xsl:call-template>
									</fo:block>
						<!-->		</xsl:if>	-->


							<!-->	<xsl:if test="string-length(./*[name()=$domainlist.tag]) &gt; 0">	-->
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$domainlist.tag"/>
										</xsl:call-template>
									</fo:block>
						<!-->		</xsl:if>	-->
								<xsl:if test="string-length(./*[name()=$favoredweapon.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$favoredweapon.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$holyitem.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$holyitem.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$pantheonlist.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$pantheonlist.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$source.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$source.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$special_abilities.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$special_abilities.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$appearance.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$appearance.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="string-length(./*[name()=$worshippers.tag]) &gt; 0">
									<fo:block font-size="7pt" text-align="left" text-indent="5pt">
										<fo:inline font-weight="bold">Power Use  </fo:inline>
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$worshippers.tag"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
							</fo:table-cell>
						</fo:table-row>
	<!-->				</xsl:if>	-->

			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - EQUIPMENT_LIST_TO_TREE
====================================
====================================-->
	<xsl:template name="equipment_list_to_tree">
		<xsl:param name="list"/>
		<xsl:param name="count"/>
		<xsl:param name="sofar"/>

		<xsl:choose>
			<xsl:when test="not($list)">
				<xsl:copy-of select="$sofar"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- Call template for remaining Items -->
				<xsl:variable name="fooz" select="$list[1]/name"/>
				<xsl:variable name="froboz">
					<xsl:call-template name="equipment_list_to_tree">
						<xsl:with-param name="list" select="//equipment/item[location = $fooz]"/>
						<xsl:with-param name="count" select="$count+1"/>
						<xsl:with-param name="sofar" select="''"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="bar">
					<xsl:copy-of select="$sofar"/>
					<subitem>
						<xsl:copy-of select="$list[1]"/>
						<xsl:if test="$froboz != '' ">
							<subitems>
								<xsl:copy-of select="$froboz"/>
							</subitems>
						</xsl:if>
					</subitem>
				</xsl:variable>
				<xsl:call-template name="equipment_list_to_tree">
					<xsl:with-param name="list" select="$list[position()>1]"/>
					<xsl:with-param name="count" select="$count+1"/>
					<xsl:with-param name="sofar">
						<xsl:copy-of select="$bar"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - EQUIPMENT [TREE]
====================================
====================================-->
	<xsl:template match="equipment" mode="tree">
		<xsl:param name="total_width"/>

		<xsl:variable name="spam">
			<subitems>
			<xsl:call-template name="equipment_list_to_tree">
				<xsl:with-param name="list" select="item[location = 'Equipped'] | item[location = 'Carried'] | item[location = '']"/>
				<xsl:with-param name="count" select="100"/>
				<xsl:with-param name="sofar" select="''"/>
			</xsl:call-template>
			</subitems>
		</xsl:variable>
		<fo:table table-layout="fixed" width="100%" space-before="2mm" >
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'equipment.border'"/></xsl:call-template>
			<xsl:attribute name="text-align">left</xsl:attribute>
			<fo:table-column><xsl:attribute name="column-width"><xsl:value-of select="$total_width - (12+16+20)"/>mm</xsl:attribute></fo:table-column>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="20mm"/>
			<fo:table-header>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="4">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'equipment.title'"/></xsl:call-template>
						<fo:block font-size="9pt">EQUIPMENT</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'equipment.title'"/></xsl:call-template>
					<fo:table-cell>
						<fo:block>Name</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">Qty</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">Weight</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">Cost</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-footer>
				<fo:table-row xsl:use-attribute-sets="equipment.title">
					<fo:table-cell padding-top="1pt" number-columns-spanned="2" >
						<fo:block font-size="7pt">TOTAL WEIGHT CARRIED/VALUE</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt">
						<fo:block font-size="7pt" text-align="right">
							<xsl:call-template name="normalize_number">
								<xsl:with-param name="string" select="total/weight"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt">
						<fo:block font-size="7pt" text-align="right">
							<xsl:variable name="TotalValue">
								<xsl:call-template name="Total">
									<xsl:with-param name="Items" select="item[contains(type, 'COIN')=false and contains(type, 'GEM')=false]"/>
									<xsl:with-param name="RunningTotal" select="0"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:value-of select="format-number($TotalValue, '####0.0#')"/> gp
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-footer>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<xsl:variable name="foo" select="xalan:nodeset($spam)"/>
							<xsl:apply-templates select="$foo" mode="equipment_tree">
								<xsl:with-param name="total_width" select="94"/>
							</xsl:apply-templates>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>



	<!--
====================================
====================================
	TEMPLATE - SUBITEMS [EQUIPMENT_TREE]
====================================
====================================-->
	<xsl:template match="subitems" mode="equipment_tree">
		<xsl:param name="total_width" select="94"/>
		<xsl:param name="depth" select="0"/>

		<xsl:variable name="subitem">
			<fo:table table-layout="fixed" width="100%">
				<fo:table-column><xsl:attribute name="column-width"><xsl:value-of select="( $total_width - (12+16+20)) - $depth*5"/>mm</xsl:attribute></fo:table-column>
				<fo:table-column column-width="12mm"/>
				<fo:table-column column-width="16mm"/>
				<fo:table-column column-width="20mm"/>
				<fo:table-body>
					<xsl:apply-templates select="subitem" mode="equipment_tree">
						<xsl:with-param name="total_width" select="$total_width"/>
						<xsl:with-param name="depth" select="$depth+1"/>
					</xsl:apply-templates>
				</fo:table-body>
			</fo:table>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$depth = 0">
				<xsl:copy-of select="$subitem"/>
			</xsl:when>
			<xsl:otherwise>
				<fo:table table-layout="fixed" width="100%">
					<fo:table-column column-width="5mm"/>
					<fo:table-column><xsl:attribute name="column-width"><xsl:value-of select="$total_width - $depth*5"/>mm</xsl:attribute></fo:table-column>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell >
								<fo:block></fo:block>
							</fo:table-cell>
							<fo:table-cell >
								<fo:block>
									<xsl:copy-of select="$subitem"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



	<!--
====================================
====================================
	TEMPLATE - SUBITEM [EQUIPMENT_TREE]
====================================
====================================-->
	<xsl:template match="subitem" mode="equipment_tree">
		<xsl:param name="depth" select="0"/>
		<xsl:param name="total_width"/>

		<xsl:variable name="shade"><xsl:call-template name="shade"/></xsl:variable>
		<fo:table-row>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('equipment.', $shade)"/></xsl:call-template>
			<fo:table-cell>
				<fo:block>
					<xsl:apply-templates select="item" mode="equipment_name_details" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right"  space-before.optimum="1pt" font-size="7pt">
					<xsl:value-of select="item/quantity" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" space-before.optimum="1pt" font-size="7pt">
					<xsl:value-of select="format-number(item/weight, '####0.0#')" />
					<xsl:if test="item/quantity &gt; 1">
						<fo:inline font-size="5pt">(<xsl:value-of select="format-number(item/weight * item/quantity, '####0.0#')" />)</fo:inline>
					</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="right" space-before.optimum="1pt" font-size="7pt">
				<xsl:value-of select="format-number(item/cost, '####0.0#')" />
					<xsl:if test="item/quantity &gt; 1">
						<fo:inline font-size="5pt">(<xsl:value-of select="format-number(item/cost * item/quantity, '####0.0#')" />)</fo:inline>
					</xsl:if>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<xsl:if test="subitems">
			<fo:table-row>
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('equipment.', $shade)"/></xsl:call-template>
				<fo:table-cell number-columns-spanned="2">
					<fo:block>
						<xsl:apply-templates  select="subitems" mode="equipment_tree">
							<xsl:with-param name="depth" select="$depth"/>
							<xsl:with-param name="total_width" select="$total_width"/>
						</xsl:apply-templates>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row height="1.5pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('equipment.', $shade)"/></xsl:call-template>
			</fo:table-row>
		</xsl:if>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - ITEM [EQUIPMENT_NAME_DETAILS]
====================================
====================================-->
	<xsl:template match="item" mode="equipment_name_details" >
		<fo:block space-before.optimum="1pt" font-size="8pt">
			<xsl:if test="contains(type, 'MAGIC') or contains(type, 'PSIONIC')">
				<xsl:attribute name="font-weight">bold</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="name"/>
			<xsl:variable name="contentList" >
				<xsl:call-template name="str:substring-before-first">
					<xsl:with-param name="text" select="contents"/>
					<xsl:with-param name="chars" select="','"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($contentList) &gt; 0">
				<fo:inline font-size="6pt">
					 [<xsl:value-of select="$contentList"/>]
				</fo:inline>
			</xsl:if>
		</fo:block>
		<!--fo:block space-before.optimum="1pt" font-size="5pt">
			<xsl:value-of select="type"/>
		</fo:block-->
		<fo:block space-before.optimum="1pt" font-size="5pt">
			<xsl:value-of select="special_properties"/>
			<xsl:value-of select="quality"/>
		</fo:block>
		<fo:block space-before.optimum="1pt" font-size="5pt">
			<xsl:value-of select="note"/>
		</fo:block>
		<!-- Display the number of charges left if any -->
		<xsl:if test="charges &gt; 0">
			<fo:block font-size="7pt" font-family="ZapfDingbats">
				<xsl:call-template name="for.loop">
					<xsl:with-param name="count" select="charges"/>
				</xsl:call-template>
			</fo:block>
		</xsl:if>
		<!-- Display the ammunition as a series of checkboxes -->
		<xsl:if test="contains(type, 'POTION') or contains(type, 'AMMUNITION') or contains(type, 'CONSUMABLE') or contains(type, 'ALCHEMICAL') or contains(type, 'VIAL') or contains(type, 'FOOD')">
			<fo:block font-size="7pt" font-family="ZapfDingbats">
				<xsl:call-template name="for.loop">
					<xsl:with-param name="count" select="quantity"/>
				</xsl:call-template>
			</fo:block>
		</xsl:if>
	</xsl:template>



	<xsl:template name="normalize_number">
		<xsl:param name="string"/>

		<xsl:variable name="number">
			<xsl:call-template name="str:substring-before-first">
				<xsl:with-param name="text" select="$string"/>
				<xsl:with-param name="chars" select="' '"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="label">
			<xsl:call-template name="str:substring-after-last">
				<xsl:with-param name="text" select="$string"/>
				<xsl:with-param name="char" select="' '"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:value-of select="format-number($number, '####0.0#')" /><xsl:text> </xsl:text><xsl:value-of select="$label"/>
	</xsl:template>

	<xsl:template name="stripLeadingPlus">
		<xsl:param name="string"/>

		<xsl:choose>
			<xsl:when test="starts-with($string, '+')">
				<xsl:value-of select="substring($string, 2)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - PARAGRAGH LIST
====================================
====================================-->
	<xsl:template name="paragraghlist">
		<xsl:param name="tag"/>
		<xsl:if test="count(./*[name()=$tag]/*[name()='para']) = 0">
			<xsl:value-of select="./*[name()=$tag]"/>
		</xsl:if>
		<xsl:if test="count(./*[name()=$tag]/*[name()='para']) &gt; 0">
			<xsl:for-each select="./*[name()=$tag]/*[name()='para']">
				<xsl:if test="count(./*[name()='table']) &gt; 0">
					<xsl:call-template name="paragraghlist.table"/>
				</xsl:if>
				<xsl:if test="count(./*[name()='table']) = 0">
					<xsl:if test="string-length(.) &gt; 0">
						<fo:block text-indent="5pt">
							<xsl:value-of select="." />
						</fo:block>
					</xsl:if>
					<xsl:if test="string-length(.) = 0">
						<fo:block text-indent="5pt">
							&#160;
						</fo:block>
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - PARAGRAGH LIST
====================================
====================================-->
	<xsl:template name="paragraghlist.table">
		<xsl:for-each select="./table">
			<fo:table table-layout="fixed" width="100%" inline-progression-dimension="auto">
				<xsl:for-each select="./table-column">
					<fo:table-column>
						<xsl:attribute name="column-width">
							<xsl:value-of select="@column-width" />
						</xsl:attribute>
					</fo:table-column>
				</xsl:for-each>
				<xsl:for-each select="./table-body">
					<fo:table-body>
						<xsl:for-each select="./table-row">
							<fo:table-row>
								<xsl:for-each select="./table-cell">
									<fo:table-cell>
										<fo:block text-indent="5pt">
											<xsl:value-of select="." />
										</fo:block>
									</fo:table-cell>
								</xsl:for-each>
							</fo:table-row>
						</xsl:for-each>
					</fo:table-body>
				</xsl:for-each>
			</fo:table>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>

