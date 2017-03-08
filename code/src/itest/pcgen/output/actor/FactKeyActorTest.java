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
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.core.Deity;
import pcgen.output.base.OutputActor;
import pcgen.output.publish.OutputDB;
import pcgen.output.testsupport.AbstractOutputTestCase;

import org.junit.BeforeClass;
import org.junit.Test;

public class FactKeyActorTest extends AbstractOutputTestCase
{

	private static final DeityFacet df = new DeityFacet();

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@BeforeClass
	private void classSetUp()
	{
		OutputDB.reset();
		df.init();
	}

	@Test
	public void testFactKeyActor()
	{
		Deity d = new Deity();
		d.setName("Bob");
		Integer expectedResult = 475;
		df.set(id, d);
		NumberManager mgr = new NumberManager();
		FactKey<Number> fk = FactKey.getConstant("cost", mgr);
		d.put(fk, new BasicIndirect<>(mgr, expectedResult));
		FactKeyActor<?> ika = new FactKeyActor<>(fk);
		CDOMWrapperInfoFacet wiFacet =
				FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
		wiFacet.set(dsid, d.getClass(), "cost", ika);
		processThroughFreeMarker("${deity.cost}", expectedResult.toString());
	}

	@Test
	public void testListKeyActorMissingSafe()
	{
		NumberManager mgr = new NumberManager();
		FactKey<Number> fk = FactKey.getConstant("cost", mgr);
		OutputActor<?> ika = new FactKeyActor<>(fk);
		CDOMWrapperInfoFacet wiFacet =
				FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
		wiFacet.set(dsid, Deity.class, "cost", ika);
		processThroughFreeMarker("${deity.cost!}", "");
	}
}
