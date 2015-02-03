<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xalan="http://xml.apache.org/xalan" 
	xmlns:math="http://exslt.org/math"
 	xmlns:myAttribs="my:Attribs" 
	xmlns:Leadership="my:Leadership" 
	exclude-result-prefixes="myAttribs Leadership">

	<xsl:import href="4e_common.xsl"/>
	<xsl:output indent="yes"/>
	
	<!-- 
		Set up the attributes that wll be used for this file.
		The entries in this block need to have globally unique names,
		so prepend each of them with the name of the file.
		
		The stylesheet that includes this one will have to put a bit of code in like this:
		
		<xsl:import href="leadership.xsl"/>
		<xsl:variable name="vAttribs_tree">
			<myAttribs:myAttribs>
				<xsl:copy-of select="$vAttribs/*"/>
				<xsl:copy-of select="document('leadership.xsl')/*/myAttribs:*/*"/>
			</myAttribs:myAttribs>
		</xsl:variable>
		<xsl:variable name="vAttribs_all" select="xalan:nodeset($vAttribs_tree)"/>

		Each attribute is a tag where the name of the tag is the name of the attribute. 
		All of the attributes of the tag will become attributes of the calling element,
		All attributes of the	 subattrib sub-element will be names of top level attribute
		elements so that the system can work recursively.
	-->
	<myAttribs:myAttribs>
		<leadership.title padding-top="1pt"  font-size="9pt" text-align="center"><subattrib centre="" inverse=""/></leadership.title>
		<leadership.table padding="0.5pt" table-layout="fixed" space-before.optimum="2mm"><subattrib border="" inverse=""/></leadership.table>
		<leadership.follower.title padding-top="1pt" font-size="8pt" text-align="end" padding-right="2pt"><subattrib  medium=""/></leadership.follower.title>
		<leadership.follower.level  font-size="7pt" text-align="end" padding="1pt" padding-right="1mm"><subattrib  medium=""/></leadership.follower.level>
		<leadership.follower.count  font-size="7pt" text-align="start" padding="1pt" padding-left="1mm"><subattrib  light=""/></leadership.follower.count>
		<leadership.score.title padding-top="1pt" padding-right="1mm" font-size="8pt" text-align="end"><subattrib  medium=""/></leadership.score.title>
		<leadership.score.value  font-size="8pt" padding-top="1pt" padding-left="1mm"><subattrib  light=""/></leadership.score.value>
		<leadership.cohort.title padding-top="1pt" padding-right="1mm" font-size="8pt" text-align="end"><subattrib  medium=""/></leadership.cohort.title>
		<leadership.cohort.value  font-size="8pt" padding-top="1pt" padding-left="1mm"><subattrib  light=""/></leadership.cohort.value>
	</myAttribs:myAttribs>


	<!--xsl:template match="/">
		<xsl:apply-templates select="." mode="leadership" />
	</xsl:template>
	<xsl:variable name="vAttribs_tree">
		<myAttribs:myAttribs>
			<xsl:copy-of select="document('leadership.xsl')/*/myAttribs:*/*"/>
		</myAttribs:myAttribs>
	</xsl:variable>
	<xsl:variable name="vAttribs_all" select="xalan:nodeset($vAttribs_tree)"/>
	<xsl:template name="attrib">
		<xsl:param name="attribute"/>
		<xsl:copy-of select="$vAttribs_all/*/*[name() = $attribute]/@*"/>
		<xsl:for-each select="$vAttribs_all/*/*[name() = $attribute]/subattrib/@*">
			<xsl:variable name="bar" select="name()"/>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="$bar"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template-->

	
	<!--
		Top level template that should be called from the parent document.
		
		All blocks should have a "/character" match with the mode being unique
		for the block.
	 -->
	<xsl:template match="/character" mode="leadership">
		<xsl:if test="class_features/leadership">
			<!-- Build the leadership table, whcih will return a tree-fragment, then
				use the xalan:nodeset() function to convert that to a nodeset so
				that we can use it for other calculations -->
				
			<xsl:variable name="score">
				<xsl:call-template name="stripLeadingPlus"><xsl:with-param name="string" select="class_features/leadership/score"/></xsl:call-template>
			</xsl:variable>

			<xsl:variable name="leader">
				<xsl:call-template name="build_leadership_table">
					<xsl:with-param name="score" select="$score"/>
					<xsl:with-param name="max_cohort_level" select="class_features/leadership/max_cohort_level"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="leadership_table" select="xalan:nodeset($leader)"/>
			<!-- Generate the table from the new leadership nodeset -->
			<xsl:apply-templates select="$leadership_table" mode="table" />
		</xsl:if>
	</xsl:template>
	
	
	<!--
		Create a pair of cells for a follower level and the number of followers at that level.
	-->
	<xsl:template match="follower">
		<fo:table-cell >
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.follower.level'"/></xsl:call-template>
			<fo:block>level <xsl:value-of select="@level"/></fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.follower.count'"/></xsl:call-template>
			<fo:block><xsl:value-of select="@number"/></fo:block>
		</fo:table-cell>
	</xsl:template>
	

	<!-- 
		Show the whole of the leadership table. This will be passed the calculated <leadership/> element.
	-->
	<xsl:template match="leadership" mode="table" >
		<fo:table >
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.table'"/></xsl:call-template>
			<fo:table-column column-width="30mm"/>
			<fo:table-column column-width="17mm"/>
			<fo:table-column column-width="30mm"/>
			<fo:table-column column-width="17mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell number-columns-spanned="4"  >
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.title'"/></xsl:call-template>
						<fo:block>LEADERSHIP</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.score.title'"/></xsl:call-template>
						<fo:block>Leadership Score:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.score.value'"/></xsl:call-template>
						<fo:block><xsl:value-of select="score"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.cohort.title'"/></xsl:call-template>
						<fo:block>Max Cohort Level:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.cohort.value'"/></xsl:call-template>
						<fo:block><xsl:value-of select="cohort/@level"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="1mm" />
				<fo:table-row>
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'leadership.follower.title'"/></xsl:call-template>
						<fo:block font-size="8pt" text-align="end" padding-right="2pt">Followers <fo:inline font-size="6pt">(level/count)</fo:inline>: </fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<fo:table table-layout="fixed">
								<fo:table-column column-width="10mm"/>
								<fo:table-column column-width="6mm"/>
								<fo:table-column column-width="10mm"/>
								<fo:table-column column-width="6mm"/>
								<fo:table-column column-width="10mm"/>
								<fo:table-column column-width="6mm"/>
								<fo:table-column column-width="10mm"/>
								<fo:table-column column-width="6mm"/>
								<fo:table-body>
									<fo:table-row keep-with-next.within-column="always">
										<xsl:apply-templates select="followers/follower[@level &lt;= 4]"/>
									</fo:table-row>
									<fo:table-row keep-with-next.within-column="always">
										<xsl:apply-templates select="followers/follower[@level &gt; 4 and @level &lt;= 8]"/>
									</fo:table-row>
									<fo:table-row keep-with-next.within-column="always">
										<xsl:apply-templates select="followers/follower[@level &gt; 8 and @level &lt;= 12]"/>
									</fo:table-row>
									<fo:table-row keep-with-next.within-column="always">
										<xsl:apply-templates select="followers/follower[@level &gt; 12 and @level &lt;= 16]"/>
									</fo:table-row>
									<fo:table-row keep-with-next.within-column="always">
										<xsl:apply-templates select="followers/follower[@level &gt; 16 and @level &lt;= 20]"/>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	
	
	<!-- 
		For Leadership scores above 40 the follower numbers and cohort levels
		are calculated by a formula rather then the lookup table
	-->
	<xsl:template name="calculated_follower_number">
		<xsl:param name="level"/>
		<xsl:param name="count"/>
		
		<xsl:choose>
			<xsl:when test="$level=1">
				<xsl:variable name="subcount">
					<xsl:choose>
						<xsl:when test="$count &gt;= 10">
							<xsl:value-of select="ceiling($count div 10)"/>
						</xsl:when>
						<xsl:otherwise></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<follower>
					<xsl:attribute name="level"><xsl:value-of select="$level"/></xsl:attribute>
					<xsl:attribute name="number"><xsl:value-of select="$subcount"/></xsl:attribute>
				</follower>
				<xsl:call-template name="calculated_follower_number">
					<xsl:with-param name="level" select="$level+1"/>
					<xsl:with-param name="count" select="$subcount"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="subcount">
					<xsl:choose>
						<xsl:when test="$count &gt;= 2">
							<xsl:value-of select="ceiling($count div 2)"/>
						</xsl:when>
						<xsl:otherwise>0</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:if test="$subcount &gt; 0">
					<follower>
						<xsl:attribute name="level"><xsl:value-of select="$level"/></xsl:attribute>
						<xsl:attribute name="number"><xsl:value-of select="$subcount"/></xsl:attribute>
					</follower>
					<xsl:call-template name="calculated_follower_number">
						<xsl:with-param name="level" select="$level+1"/>
						<xsl:with-param name="count" select="$subcount"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	
	
	
	
	<!-- 
		Generate a leadership tree fragment from the 'score' passed in.
		
		For scores <=40 we use the Leadership:leadership lookup table,
		for scores >40 we use the formula in 'calculated_follower_number'
	-->
	<xsl:template name="build_leadership_table">
		<xsl:param name="score"/>
		<xsl:param name="max_cohort_level"/>

		<xsl:variable name="leadership_table">
			<leadership>
				<score><xsl:value-of select="$score"/></score>
				<xsl:choose>
					<xsl:when test="$score &lt;= 40">
						<xsl:variable name="scores" select="document('')/*/Leadership:leadership/score[@value=$score]"/>
						<xsl:variable name="cohort_levels">
							<level><xsl:value-of select="$max_cohort_level"/></level>
							<level><xsl:value-of select="$scores/@cohort"/></level>
						</xsl:variable>
						<xsl:variable name="cohort_levels_nodeset" select="xalan:nodeset($cohort_levels)"/>
						<cohort>
							<xsl:attribute name="level">
								<xsl:value-of select="math:min($cohort_levels_nodeset/*)"/>
							</xsl:attribute>
						</cohort>
						<followers>
							<xsl:for-each select="$scores/followers">
								<follower>
									<xsl:attribute name="level"><xsl:value-of select="@level"/></xsl:attribute>
									<xsl:attribute name="number"><xsl:value-of select="@number"/></xsl:attribute>
								</follower>
							</xsl:for-each>
						</followers>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="cohort_levels">
							<level><xsl:value-of select="$max_cohort_level"/></level>
							<level><xsl:value-of select="($score div 2)+5"/></level>
						</xsl:variable>
						<cohort>
							<xsl:attribute name="level"><xsl:value-of select="math:min(xalan:nodeset($cohort_levels)/*)"/></xsl:attribute>
						</cohort>
						<followers>
							<xsl:call-template name="calculated_follower_number">
								<xsl:with-param name="level" select="1"/>
								<xsl:with-param name="count" select="1000 + ($score - 40)*100"/>
							</xsl:call-template>
						</followers>
					</xsl:otherwise>
				</xsl:choose>		
			</leadership>
		</xsl:variable>
		
		<xsl:copy-of select="$leadership_table"/>
	</xsl:template>
	
	
	
	<!-- 
		As the leadership table resists all attempts at calculating it by a formula
		we are using a big lookup table instead.
	-->
	<Leadership:leadership>
		<score value="1" cohort="0"></score>
		<score value="2" cohort="1"></score>
		<score value="3" cohort="2"></score>
		<score value="4" cohort="3"></score>
		<score value="5" cohort="3"></score>
		<score value="6" cohort="4"></score>
		<score value="7" cohort="5"></score>
		<score value="8" cohort="5"></score>
		<score value="9" cohort="6"></score>
		<score value="10" cohort="7">
			<followers level="1" number="5"/>
		</score>
		<score value="11" cohort="7">
			<followers level="1" number="6"/>
		</score>
		<score value="12" cohort="8">
			<followers level="1" number="8"/>
		</score>
		<score value="13" cohort="9">
			<followers level="1" number="10"/>
			<followers level="2" number="1"/>
		</score>
		<score value="14" cohort="10">
			<followers level="1" number="15"/>
			<followers level="2" number="1"/>
		</score>
		<score value="15" cohort="10">
			<followers level="1" number="20"/>
			<followers level="2" number="2"/>
			<followers level="3" number="1"/>
		</score>
		<score value="16" cohort="11">
			<followers level="1" number="25"/>
			<followers level="2" number="2"/>
			<followers level="3" number="1"/>
		</score>
		<score value="17" cohort="12">
			<followers level="1" number="30"/>
			<followers level="2" number="3"/>
			<followers level="3" number="1"/>
			<followers level="4" number="1"/>
		</score>
		<score value="18" cohort="12">
			<followers level="1" number="35"/>
			<followers level="2" number="3"/>
			<followers level="3" number="1"/>
			<followers level="4" number="1"/>
		</score>
		<score value="19" cohort="13">
			<followers level="1" number="40"/>
			<followers level="2" number="4"/>
			<followers level="3" number="2"/>
			<followers level="4" number="1"/>
			<followers level="5" number="1"/>
		</score>
		<score value="20" cohort="14">
			<followers level="1" number="50"/>
			<followers level="2" number="5"/>
			<followers level="3" number="3"/>
			<followers level="4" number="2"/>
			<followers level="5" number="1"/>
		</score>
		<score value="21" cohort="15">
			<followers level="1" number="60"/>
			<followers level="2" number="6"/>
			<followers level="3" number="3"/>
			<followers level="4" number="2"/>
			<followers level="5" number="1"/>
			<followers level="6" number="1"/>
		</score>
		<score value="22" cohort="15">
			<followers level="1" number="75"/>
			<followers level="2" number="7"/>
			<followers level="3" number="4"/>
			<followers level="4" number="2"/>
			<followers level="5" number="2"/>
			<followers level="6" number="1"/>
		</score>
		<score value="23" cohort="16">
			<followers level="1" number="90"/>
			<followers level="2" number="9"/>
			<followers level="3" number="5"/>
			<followers level="4" number="3"/>
			<followers level="5" number="2"/>
			<followers level="6" number="1"/>
		</score>
		<score value="24" cohort="17">
			<followers level="1" number="110"/>
			<followers level="2" number="11"/>
			<followers level="3" number="6"/>
			<followers level="4" number="3"/>
			<followers level="5" number="2"/>
			<followers level="6" number="1"/>
		</score>
		<score value="25" cohort="17">
			<followers level="1" number="135"/>
			<followers level="2" number="13"/>
			<followers level="3" number="7"/>
			<followers level="4" number="4"/>
			<followers level="5" number="2"/>
			<followers level="6" number="2"/>
		</score>
		<score value="26" cohort="18">
			<followers level="1" number="160"/>
			<followers level="2" number="16"/>
			<followers level="3" number="8"/>
			<followers level="4" number="4"/>
			<followers level="5" number="2"/>
			<followers level="6" number="2"/>
			<followers level="7" number="1"/>
		</score>
		<score value="27" cohort="18">
			<followers level="1" number="190"/>
			<followers level="2" number="19"/>
			<followers level="3" number="10"/>
			<followers level="4" number="5"/>
			<followers level="5" number="3"/>
			<followers level="6" number="2"/>
			<followers level="7" number="1"/>
		</score>
		<score value="28" cohort="19">
			<followers level="1" number="220"/>
			<followers level="2" number="22"/>
			<followers level="3" number="11"/>
			<followers level="4" number="6"/>
			<followers level="5" number="3"/>
			<followers level="6" number="2"/>
			<followers level="7" number="1"/>
		</score>
		<score value="29" cohort="19">
			<followers level="1" number="260"/>
			<followers level="2" number="26"/>
			<followers level="3" number="13"/>
			<followers level="4" number="7"/>
			<followers level="5" number="4"/>
			<followers level="6" number="2"/>
			<followers level="7" number="1"/>
		</score>
		<score value="30" cohort="20">
			<followers level="1" number="300"/>
			<followers level="2" number="30"/>
			<followers level="3" number="15"/>
			<followers level="4" number="8"/>
			<followers level="5" number="4"/>
			<followers level="6" number="2"/>
			<followers level="7" number="1"/>
		</score>
		<score value="31" cohort="20">
			<followers level="1" number="350"/>
			<followers level="2" number="35"/>
			<followers level="3" number="18"/>
			<followers level="4" number="9"/>
			<followers level="5" number="5"/>
			<followers level="6" number="3"/>
			<followers level="7" number="2"/>
			<followers level="8" number="1"/>
		</score>
		<score value="32" cohort="21">
			<followers level="1" number="400"/>
			<followers level="2" number="40"/>
			<followers level="3" number="20"/>
			<followers level="4" number="10"/>
			<followers level="5" number="5"/>
			<followers level="6" number="3"/>
			<followers level="7" number="2"/>
			<followers level="8" number="1"/>
		</score>
		<score value="33" cohort="21">
			<followers level="1" number="460"/>
			<followers level="2" number="46"/>
			<followers level="3" number="23"/>
			<followers level="4" number="12"/>
			<followers level="5" number="6"/>
			<followers level="6" number="3"/>
			<followers level="7" number="2"/>
			<followers level="8" number="1"/>
		</score>
		<score value="34" cohort="22">
			<followers level="1" number="520"/>
			<followers level="2" number="52"/>
			<followers level="3" number="26"/>
			<followers level="4" number="13"/>
			<followers level="5" number="6"/>
			<followers level="6" number="3"/>
			<followers level="7" number="2"/>
			<followers level="8" number="1"/>
		</score>
		<score value="35" cohort="22">
			<followers level="1" number="590"/>
			<followers level="2" number="59"/>
			<followers level="3" number="30"/>
			<followers level="4" number="15"/>
			<followers level="5" number="8"/>
			<followers level="6" number="4"/>
			<followers level="7" number="2"/>
			<followers level="8" number="1"/>
		</score>
		<score value="36" cohort="23">
			<followers level="1" number="660"/>
			<followers level="2" number="66"/>
			<followers level="3" number="33"/>
			<followers level="4" number="17"/>
			<followers level="5" number="9"/>
			<followers level="6" number="5"/>
			<followers level="7" number="3"/>
			<followers level="8" number="2"/>
			<followers level="9" number="1"/>
		</score>
		<score value="37" cohort="23">
			<followers level="1" number="740"/>
			<followers level="2" number="74"/>
			<followers level="3" number="37"/>
			<followers level="4" number="19"/>
			<followers level="5" number="10"/>
			<followers level="6" number="5"/>
			<followers level="7" number="3"/>
			<followers level="8" number="2"/>
			<followers level="9" number="1"/>
		</score>
		<score value="38" cohort="24">
			<followers level="1" number="820"/>
			<followers level="2" number="82"/>
			<followers level="3" number="41"/>
			<followers level="4" number="21"/>
			<followers level="5" number="11"/>
			<followers level="6" number="6"/>
			<followers level="7" number="3"/>
			<followers level="8" number="2"/>
			<followers level="9" number="1"/>
		</score>
		<score value="39" cohort="24">
			<followers level="1" number="910"/>
			<followers level="2" number="91"/>
			<followers level="3" number="46"/>
			<followers level="4" number="23"/>
			<followers level="5" number="12"/>
			<followers level="6" number="6"/>
			<followers level="7" number="3"/>
			<followers level="8" number="2"/>
			<followers level="9" number="1"/>
		</score>
		<score value="40" cohort="25">
			<followers level="1" number="1000"/>
			<followers level="2" number="100"/>
			<followers level="3" number="50"/>
			<followers level="4" number="25"/>
			<followers level="5" number="13"/>
			<followers level="6" number="7"/>
			<followers level="7" number="4"/>
			<followers level="8" number="2"/>
			<followers level="9" number="1"/>
		</score>

	</Leadership:leadership>	

	
</xsl:stylesheet>
