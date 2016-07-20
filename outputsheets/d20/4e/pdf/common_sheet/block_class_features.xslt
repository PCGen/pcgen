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
	TEMPLATE - CLASS FEATURES

Returns the size in MM the class
features take up on the LHS of the
first page
====================================
====================================-->
	<xsl:template name="features.left">
		<xsl:param name="features"/>
		<xsl:param name="RunningTotal" select="0"/>

		<xsl:choose>
			<xsl:when test="not($features)">
				<!--No more Items so return Running Total -->
				<xsl:copy-of select="$RunningTotal"/>
			</xsl:when>
			<xsl:otherwise>
				<!--Call template for remaining Items -->
				<xsl:variable name="ClassLength">
					<xsl:choose>
						<xsl:when test="name($features[1]) = 'rage'">25</xsl:when>
						<xsl:when test="name($features[1]) = 'wildshape'">25</xsl:when>
						<xsl:when test="name($features[1]) = 'stunning_fist'">14</xsl:when>
						<xsl:when test="name($features[1]) = 'ki_pool'">11</xsl:when>
						<xsl:when test="name($features[1]) = 'wholeness_of_body'">14</xsl:when>
						<xsl:when test="name($features[1]) = 'psionics'">56</xsl:when>
						<xsl:when test="name($features[1]) = 'layonhands'">14</xsl:when>
						<xsl:otherwise>0</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:call-template name="features.left">
					<xsl:with-param name="features" select="$features[position() &gt; 1]"/>
					<xsl:with-param name="RunningTotal" select="$ClassLength + $RunningTotal"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS FEATURES

Returns the size in MM the class
features take up on the RHS of the
first page
====================================
====================================-->
	<xsl:template name="features.right">
		<xsl:param name="features"/>
		<xsl:param name="RunningTotal" select="0"/>
		<xsl:variable name="bardic_music">
			<xsl:choose>
				<xsl:when test="count($features/bardic_music) &gt; 0">18</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="turning">
			<xsl:choose>
				<xsl:when test="count($features/turning) &gt; 0">44</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="eclipse_channeling">
			<xsl:choose>
				<xsl:when test="count($features/eclipse_channeling) &gt; 0">44</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="channel_energy">
			<xsl:choose>
				<xsl:when test="count($features/channel_energy) &gt; 0">14</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:copy-of select="$bardic_music + $turning + $eclipse_channeling + $channel_energy"/>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - CLASS FEATURE PER DAY
====================================
====================================-->
	<xsl:template name="class.feature.perday">
		<xsl:param name="attribute"/>
		<xsl:param name="name" />
		<xsl:param name="uses" />
		<xsl:param name="uses.title" select="'Uses per day'" />
		<xsl:param name="description.title" select="''"/>
		<xsl:param name="description" />
		<xsl:param name="width" select="'wide'" />

		<fo:table table-layout="fixed" space-before="2mm" keep-together="always" border-collapse="collapse">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
			<fo:table-column column-width="18mm"/>
			<fo:table-column>
				<xsl:if test="$width = 'wide' ">
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 20" />mm</xsl:attribute>
				 </xsl:if>
				<xsl:if test="$width = 'narrow' ">
<xsl:attribute name="column-width"><xsl:value-of select="0.45 * $pagePrintableWidth - 18" />mm</xsl:attribute>
				</xsl:if>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="10pt" font-weight="bold">
							<xsl:value-of select="$name"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="1pt" text-align="end">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="8pt"><xsl:value-of select="$uses.title"/></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="3pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
						<fo:block font-size="9pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count">
									<xsl:call-template name="stripLeadingPlus">
										<xsl:with-param name="string" select="$uses"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:if test="$description != '' ">
					<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
			<!--	Remove Line
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>	-->
						<xsl:choose>
							<xsl:when test="$description.title != '' ">
								<fo:table-cell padding-top="1pt" text-align="end">
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
									<fo:block font-size="6pt"><xsl:value-of select="$description.title"/></fo:block>
								</fo:table-cell>
								<fo:table-cell padding-top="1pt" padding-left="3pt">
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
									<fo:block font-size="6pt"><xsl:value-of select="$description"/></fo:block>
								</fo:table-cell>
							</xsl:when>
							<xsl:otherwise>
								<fo:table-cell padding="3pt" number-columns-spanned="2">
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
									<fo:block font-size="5pt">
										<xsl:value-of select="$description"/>
									</fo:block>
								</fo:table-cell>
							</xsl:otherwise>
						</xsl:choose>
					</fo:table-row>
				</xsl:if>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - RAGE
====================================
====================================-->
	<xsl:template match="rage">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'rage'"/>
			<xsl:with-param name="name" select="'BARBARIAN RAGE'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="uses.title" select="uses_per_day.title"/>
			<xsl:with-param name="description" select="description"/>
			<xsl:with-param name="description.title" select="' '"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - WILDSHAPE
====================================
====================================-->
	<xsl:template match="wildshape">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'wildshape'"/>
			<xsl:with-param name="name" select="'Druid Wildshape'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="description" select="concat('Duration = ',duration,' Hours')"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PERFORM
====================================
====================================-->
	<xsl:template match="bardic_music">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'bard'"/>
			<xsl:with-param name="name" select="'Bardic Music'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
<!--			<xsl:with-param name="description.title" select="effects"/> -->
			<xsl:with-param name="description" select="text"/>
			<xsl:with-param name="width" select="'narrow'"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling
====================================
====================================-->
	<xsl:template name="eclipse_channeling.intensity">
		<xsl:param name="die"/>
		<xsl:param name="number"/>

		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="$number mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('turning.', $shade)"/></xsl:call-template>
			<fo:table-cell>
				<fo:block font-size="7pt"><xsl:value-of select="$die"/></fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-size="7pt"><xsl:value-of select="$number"/></fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling Info
====================================
====================================-->
	<xsl:template name="eclipse_channeling.info">
		<xsl:param name="title"/>
		<xsl:param name="info"/>
		<xsl:param name="info2"/>
		<xsl:param name="info3"/>
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<fo:table-cell padding-top="1pt" text-align="end">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
				<fo:block font-size="8pt">
					<xsl:value-of select="$title"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block font-size="8pt">
					<xsl:value-of select="$info"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling
====================================
====================================-->
	<xsl:template match="eclipse_channeling">
		<xsl:param name="column_width" select="0.45 * $pagePrintableWidth"/>
		<xsl:variable name="channel_intensity">
			<xsl:value-of select="/channel_intensity"/>
		</xsl:variable>
		<!-- BEGIN Channeling Table -->
		<fo:table table-layout="fixed" space-before="1mm" keep-together.within-column="always" border-collapse="collapse" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.60 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.40 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<fo:block font-size="10pt" font-weight="bold">
							<xsl:value-of select="concat(@type, ' ', @kind)"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell>
						<fo:table table-layout="fixed">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row>
											<xsl:message>Test</xsl:message>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="7pt">Intensity Check</fo:block>
										<fo:block font-size="7pt">Result</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="7pt"><xsl:value-of select="@kind"/> Intensity</fo:block>
										<fo:block font-size="6pt">(Level)</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Intensity Check'"/>
									<xsl:with-param name="info" select="channeling_check" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row height="1pt">
											<xsl:message>Test</xsl:message>
									<fo:table-cell />
								</fo:table-row>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'Up to 0'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-8"/>

								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'1 - 3'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-7" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'4 - 6'"/>
									<xsl:with-param name="number" select="(channel_intensity)-6" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'7 - 9'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-5" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'10 - 12'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-4" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'13 - 15'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-3" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'16 - 18'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-2" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'19 - 21'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-1" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'22 - 25'"/>
									<xsl:with-param name="number" select="channel_intensity" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'26+'"/>
									<xsl:with-param name="number" select="number(channel_intensity)+1" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Channeling level'"/>
									<xsl:with-param name="info" select="level" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Magnitude'"/>
									<xsl:with-param name="info" select="damage" />
									<xsl:with-param name="info2" select="damage_bonus" />
									<xsl:with-param name="info3" select="factor" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Range'"/>
									<xsl:with-param name="info" select="range" />
								</xsl:call-template>
								<fo:table-row  keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
									<fo:table-cell number-columns-spanned="2" padding-top="1pt" text-align="end">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="8pt" padding-top="2pt">
											<xsl:value-of select="notes"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="2">
						<fo:table border-collapse="collapse" padding="0.5pt" table-layout="fixed">
							<fo:table-column column-width="22mm"/>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 22" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="eclipse_channeling.per.day">
									<xsl:with-param name="title" select="concat(@type, '/DAY')"/>
									<xsl:with-param name="value" select="uses_per_day"/>
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END Eclipse Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling - Uses Per Day
====================================
====================================-->
	<xsl:template name="eclipse_channeling.per.day">
		<xsl:param name="title" />
		<xsl:param name="value"/>
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<fo:table-cell  padding-top="2pt" padding-right="2pt">
				<fo:block text-align="end" display-align="center" font-size="9pt"><xsl:value-of select="$title"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block text-align="start" font-size="10pt" font-family="ZapfDingbats">
					<xsl:call-template name="for.loop">
						<xsl:with-param name="count" select="$value"/>
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TURNING
====================================
====================================-->
	<xsl:template name="turning.hitdice">
		<xsl:param name="die"/>
		<xsl:param name="number"/>

		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="$number mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('turning.', $shade)"/></xsl:call-template>
			<fo:table-cell>
				<fo:block font-size="7pt"><xsl:value-of select="$die"/></fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-size="7pt"><xsl:value-of select="$number"/></fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TURNING
====================================
====================================-->
	<xsl:template name="turning.info">
		<xsl:param name="title"/>
		<xsl:param name="info"/>
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<fo:table-cell padding-top="1pt" text-align="end">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
				<fo:block font-size="8pt">
					<xsl:value-of select="$title"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block font-size="8pt">
					<xsl:value-of select="$info"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TURNING
====================================
====================================-->
	<xsl:template match="turning">
		<xsl:param name="column_width" select="0.45 * $pagePrintableWidth"/>
		<!-- BEGIN Turning Table -->
		<fo:table table-layout="fixed" space-before="1mm" keep-together.within-column="always" border-collapse="collapse" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.60 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.40 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<fo:block font-size="10pt" font-weight="bold">
							<xsl:value-of select="concat(@type, ' ', @kind)"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell>
						<fo:table table-layout="fixed">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row>
											<xsl:message>Test</xsl:message>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="7pt">Turning Check</fo:block>
										<fo:block font-size="7pt">Result</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="7pt"><xsl:value-of select="@kind"/> Affected</fo:block>
										<fo:block font-size="6pt">(Maximum Hit Dice)</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="turning.info">
									<xsl:with-param name="title" select="'Turning Check'"/>
									<xsl:with-param name="info" select="turn_check" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row height="1pt">
											<xsl:message>Test</xsl:message>
									<fo:table-cell />
								</fo:table-row>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'Up to 0'"/>
									<xsl:with-param name="number" select="number(level)-4" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'1 - 3'"/>
									<xsl:with-param name="number" select="number(level)-3" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'4 - 6'"/>
									<xsl:with-param name="number" select="number(level)-2" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'7 - 9'"/>
									<xsl:with-param name="number" select="number(level)-1" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'10 - 12'"/>
									<xsl:with-param name="number" select="level" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'13 - 15'"/>
									<xsl:with-param name="number" select="number(level)+1" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'16 - 18'"/>
									<xsl:with-param name="number" select="number(level)+2" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'19 - 21'"/>
									<xsl:with-param name="number" select="number(level)+3" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'22+'"/>
									<xsl:with-param name="number" select="number(level)+4" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="turning.info">
									<xsl:with-param name="title" select="'Turn level'"/>
									<xsl:with-param name="info" select="level" />
								</xsl:call-template>
								<xsl:call-template name="turning.info">
									<xsl:with-param name="title" select="'Turn damage'"/>
									<xsl:with-param name="info" select="damage" />
								</xsl:call-template>
								<fo:table-row  keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
									<fo:table-cell number-columns-spanned="2" padding-top="1pt" text-align="end">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="8pt" padding-top="2pt">
											<xsl:value-of select="notes"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="2">
						<fo:table border-collapse="collapse" padding="0.5pt" table-layout="fixed">
							<fo:table-column column-width="22mm"/>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 22" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="turns.per.day">
									<xsl:with-param name="title" select="concat(@type, '/DAY')"/>
									<xsl:with-param name="value" select="uses_per_day"/>
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END Turning Table -->
	</xsl:template>
	<xsl:template name="turns.per.day">
		<xsl:param name="title" />
		<xsl:param name="value"/>
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<fo:table-cell  padding-top="2pt" padding-right="2pt">
				<fo:block text-align="end" display-align="center" font-size="9pt"><xsl:value-of select="$title"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block text-align="start" font-size="10pt" font-family="ZapfDingbats">
					<xsl:call-template name="for.loop">
						<xsl:with-param name="count" select="$value"/>
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CHANNEL ENERGY
====================================
====================================-->
	<xsl:template match="channel_energy">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'bard'"/>
			<xsl:with-param name="name" select="'Channel Energy'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="uses.title" select="uses_per_day.title"/>
			<xsl:with-param name="description.title" select="' '"/>
			<xsl:with-param name="description" select="description"/>
			<xsl:with-param name="width" select="'narrow'"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Stunning Fist
====================================
====================================-->
	<xsl:template match="stunning_fist">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'stunningfist'"/>
			<xsl:with-param name="name" select="'Stunning Fist'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="description.title" select="' '"/>
			<xsl:with-param name="description" select="description"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - KI Pool
====================================
====================================-->
	<xsl:template match="ki_pool">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'stunningfist'"/>
			<xsl:with-param name="name" select="'Ki Pool'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - WHOLENESS OF BODY
====================================
====================================-->
	<xsl:template match="wholeness_of_body">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'wholeness'"/>
			<xsl:with-param name="name" select="'WHOLENESS OF BODY'"/>
			<xsl:with-param name="uses" select="hp_per_day"/>
			<xsl:with-param name="uses.title" select="'HP per day'"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - LAY ON HANDS
====================================
====================================-->
	<xsl:template match="layonhands">

		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'bard'"/>
			<xsl:with-param name="name" select="'LAY ON HANDS'"/>
			<xsl:with-param name="uses" select="hp_per_day"/>
			<xsl:with-param name="uses.title" select="hp_per_day.title"/>
			<xsl:with-param name="description.title" select="' '"/>
			<xsl:with-param name="description" select="description"/>
		</xsl:call-template>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - CHECKLISTS
====================================
====================================-->
	<xsl:template match="checklists">
	<xsl:for-each select="checklist">
		<!-- BEGIN Use Per Day Ability table -->
		<fo:table table-layout="fixed" space-before="2mm" keep-together.within-column="always" border-collapse="collapse" >
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'checklist.border'"/></xsl:call-template>
			<fo:table-column column-width="23mm"/>
			<fo:table-column column-width="63mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist.title'"/>
						</xsl:call-template>
						<fo:block font-size="10pt" font-weight="bold" text-align="center">
							<xsl:value-of select="header"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="1pt" text-align="end">
							<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" text-align="center">
							<xsl:value-of select="check_type"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="9pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="9pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="check_count"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test END</xsl:message>
					<fo:table-cell padding="3pt" number-columns-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="5pt" font-weight="bold">
						<xsl:if test="name != ''"> <xsl:value-of select="name"/>:</xsl:if>
							<fo:inline font-size="5pt" font-weight="normal"><xsl:value-of select="description"/><xsl:if test="source != ''"> [<xsl:value-of select="source"/>]</xsl:if></fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="subability">
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding="3pt" number-columns-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="5pt" font-weight="bold">
							<xsl:if test="name != ''"> <xsl:value-of select="name"/>:</xsl:if>
								<fo:inline font-size="5pt" font-weight="normal"><xsl:value-of select="description"/><xsl:if test="source != ''"> [<xsl:value-of select="source"/>]</xsl:if></fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
|%|
		<!-- END Checklists table -->
	</xsl:for-each>

	</xsl:template>

</xsl:stylesheet>