<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id$ -->
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:myAttribs="my:Attribs"
	exclude-result-prefixes="myAttribs"
	>
	<xsl:import href="4e_master_powers.xslt"/>
	<xsl:output indent="yes"/>

	<myAttribs:myAttribs>
		<border border-width="0.5pt" border-style="solid" />
		<centre text-align="center" />
		<border.temp border-width="2pt" border-style="solid" border-color="lightgrey"><subattrib centre=""/></border.temp>

		<normal color="black" background-color="white" border-color="black"/>
		<light color="black" background-color="white" border-color="black"/>
		<medium color="black" background-color="lightgrey" border-color="black"/>
		<dark color="black" background-color="lightgrey" border-color="black"/>
		<very.dark color="black" background-color="white" border-color="black"/>
		<inverse color="black" background-color="white" border-color="black"/>

		<powers_classfeatures.title><subattrib centre="" inverse=""/></powers_classfeatures.title>
		<powers_classfeatures.border padding="0.5pt"><subattrib border="" inverse=""/></powers_classfeatures.border>
		<powers_classfeatures.lightline><subattrib light=""/></powers_classfeatures.lightline>
		<powers_classfeatures.darkline><subattrib medium=""/></powers_classfeatures.darkline>

		<powers_featpowers.title><subattrib centre="" inverse=""/></powers_featpowers.title>
		<powers_featpowers.border padding="0.5pt"><subattrib border="" inverse=""/></powers_featpowers.border>
		<powers_featpowers.lightline><subattrib light=""/></powers_featpowers.lightline>
		<powers_featpowers.darkline><subattrib medium=""/></powers_featpowers.darkline>

		<powers_atwills.title><subattrib centre="" inverse=""/></powers_atwills.title>
		<powers_atwills.border padding="0.5pt"><subattrib border="" inverse=""/></powers_atwills.border>
		<powers_atwills.lightline><subattrib light=""/></powers_atwills.lightline>
		<powers_atwills.darkline><subattrib medium=""/></powers_atwills.darkline>

		<powers_encounters.title><subattrib centre="" inverse=""/></powers_encounters.title>
		<powers_encounters.border padding="0.5pt"><subattrib border="" inverse=""/></powers_encounters.border>
		<powers_encounters.lightline><subattrib light=""/></powers_encounters.lightline>
		<powers_encounters.darkline><subattrib medium=""/></powers_encounters.darkline>

		<powers_dailies.title><subattrib centre="" inverse=""/></powers_dailies.title>
		<powers_dailies.border padding="0.5pt"><subattrib border="" inverse=""/></powers_dailies.border>
		<powers_dailies.lightline><subattrib light=""/></powers_dailies.lightline>
		<powers_dailies.darkline><subattrib medium=""/></powers_dailies.darkline>

		<powers_utilities.title><subattrib centre="" inverse=""/></powers_utilities.title>
		<powers_utilities.border padding="0.5pt"><subattrib border="" inverse=""/></powers_utilities.border>
		<powers_utilities.lightline><subattrib light=""/></powers_utilities.lightline>
		<powers_utilities.darkline><subattrib medium=""/></powers_utilities.darkline>

	</myAttribs:myAttribs>
	<xsl:variable name="vAttribs" select="document('')/*/myAttribs:*"/>

</xsl:stylesheet>

