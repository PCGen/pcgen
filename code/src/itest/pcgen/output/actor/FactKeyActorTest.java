/*
 * Copyright (c) 2014-15 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.output.actor;

import pcgen.base.format.NumberManager;
import pcgen.base.util.BasicIndirect;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.facet.CDOMWrapperInfoFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.core.Race;
import pcgen.output.publish.OutputDB;
import pcgen.output.testsupport.AbstractOutputTestCase;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FactKeyActorTest extends AbstractOutputTestCase
{

	private static final RaceFacet DF = new RaceFacet();

	@BeforeAll
	static void classSetUp()
	{
		OutputDB.reset();
		DF.init();
	}

	@AfterAll
	static void classTearDown()
	{
		OutputDB.reset();
	}

	@Test
	public void testFactKeyActor()
	{
		Race d = new Race();
		d.setName("Bob");
		Integer expectedResult = 475;
		DF.set(id, d);
		NumberManager mgr = new NumberManager();
		FactKey<Number> fk = FactKey.getConstant("cost", mgr);
		d.put(fk, new BasicIndirect<>(mgr, expectedResult));
		FactKeyActor<?> ika = new FactKeyActor<>(fk);
		CDOMWrapperInfoFacet wiFacet =
				FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
		wiFacet.set(dsid, d.getClass(), "cost", ika);
		processThroughFreeMarker("${race.cost}", expectedResult.toString());
	}

	@Test
	public void testListKeyActorMissingSafe()
	{
		NumberManager mgr = new NumberManager();
		FactKey<Number> fk = FactKey.getConstant("cost", mgr);
		FactKeyActor<?> ika = new FactKeyActor<>(fk);
		CDOMWrapperInfoFacet wiFacet =
				FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
		wiFacet.set(dsid, Race.class, "cost", ika);
		processThroughFreeMarker("${race.cost!}", "");
	}
}
