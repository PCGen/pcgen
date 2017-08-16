/*
 * Copyright 2009 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.UnitSet;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.rules.context.LoadContext;

/**
 * <code>VisionTokenTest</code> tests the function of the VISION token.  
 */
public class VisionTokenTest extends AbstractCharacterTestCase
{
	private PCTemplate darkvisionT;
	private PCTemplate lowlightT;
	private PCTemplate astralT;
	private UnitSet metricUS;

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(VisionTokenTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		final GenericLoader<PCTemplate> loader = new GenericLoader<>(PCTemplate.class);
		final LoadContext context = Globals.getContext();
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}

		
		loader.parseLine(context, null, "Darkvision		VISION:Darkvision (60')",
			source);
		darkvisionT =
				context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class,
					"Darkvision");

		loader.parseLine(context, null, "Low-light		VISION:Low-light",
			source);
		lowlightT =
				context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class,
					"Low-light");

		loader.parseLine(context, null, "Astral		VISION:Astral (130')",
			source);
		astralT =
				context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class,
					"Astral");

		assertTrue(context.getReferenceContext().resolveReferences(null));
		
		metricUS = new UnitSet();
		metricUS.setName("Metric");
		metricUS.setDistanceUnit("m");
		metricUS.setDistanceFactor(new BigDecimal(0.3));
		metricUS.setDistanceDisplayPattern(new DecimalFormat("#.##"));
		SettingsHandler.getGame().getModeContext().getReferenceContext().importObject(metricUS);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		Globals.getContext().getReferenceContext().forget(darkvisionT);
		Globals.getContext().getReferenceContext().forget(lowlightT);
		Globals.getContext().getReferenceContext().forget(astralT);

		super.tearDown();
	}

	/**
	 * Test the list output of the vision tag.
	 * @throws Exception
	 */
	public void testList() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		assertEquals("no vision", "", new VisionToken().getToken(
			"VISION", pc, null));

		pc.addTemplate(darkvisionT);
		pc.setDirty(true);
		assertEquals("One vision method", "Darkvision (60')", new VisionToken().getToken(
			"VISION", pc, null));

		pc.addTemplate(lowlightT);
		pc.setDirty(true);
		assertEquals("Two vision", "Darkvision (60'), Low-light", new VisionToken().getToken(
			"VISION", pc, null));
	}

	/**
	 * Test the output of individual vision entries.
	 * @throws Exception
	 */
	public void testPositional() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		assertEquals("no vision", "", new VisionToken().getToken(
			"VISION.0", pc, null));

		pc.addTemplate(darkvisionT);
		pc.setDirty(true);
		assertEquals("Darkvision", "Darkvision (60')", new VisionToken().getToken(
			"VISION.0", pc, null));
		assertEquals("vision over the maximum", "", new VisionToken().getToken(
			"VISION.1", pc, null));
		assertEquals("vision over the maximum", "", new VisionToken().getToken(
			"VISION.100", pc, null));
		assertEquals("vision under the minimum", "", new VisionToken().getToken(
			"VISION.-1", pc, null));

		pc.addTemplate(lowlightT);
		pc.addTemplate(astralT);
		pc.setDirty(true);
		assertEquals("Vision 1", "Darkvision (60')", new VisionToken().getToken(
			"VISION.1", pc, null));
		assertEquals("Vision 2", "Low-light", new VisionToken().getToken(
			"VISION.2", pc, null));
		assertEquals("Vision 0", "Astral (130')", new VisionToken().getToken(
			"VISION.0", pc, null));
	}


	/**
	 * Test the list output of the vision tag with metric units.
	 * @throws Exception
	 */
	public void testListMetric() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(darkvisionT);
		pc.setDirty(true);
		assertTrue(SettingsHandler.getGame().selectUnitSet(metricUS.getDisplayName()));

		assertEquals("Metric range of one vision method", "Darkvision (18 m)", new VisionToken().getToken(
			"VISION", pc, null));

		pc.addTemplate(lowlightT);
		pc.setDirty(true);
		assertEquals("Two vision metric", "Darkvision (18 m), Low-light", new VisionToken().getToken(
			"VISION", pc, null));
	}
	
}
