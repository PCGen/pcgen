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

import pcgen.base.util.BasicIndirect;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.core.Deity;
import pcgen.output.publish.OutputDB;
import pcgen.output.testsupport.AbstractOutputTestCase;
import pcgen.output.wrapper.CDOMObjectWrapper;
import plugin.format.NumberManager;

public class FactKeyActorTest extends AbstractOutputTestCase
{

	private static final DeityFacet df = new DeityFacet();

	private static boolean classSetUpRun = false;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		if (!classSetUpRun)
		{
			classSetUp();
			classSetUpRun = true;
		}
		CDOMObjectWrapper.getInstance().clear();
	}

	private void classSetUp()
	{
		OutputDB.reset();
		df.init();
	}

	public void testFactKeyActor()
	{
		Deity d = new Deity();
		d.setName("Bob");
		Integer expectedResult = 475;
		df.set(id, d);
		NumberManager mgr = new NumberManager();
		FactKey<Number> fk = FactKey.getConstant("cost", mgr);
		d.put(fk, new BasicIndirect<Number>(mgr, expectedResult));
		FactKeyActor<?> ika = new FactKeyActor<>(fk);
		CDOMObjectWrapper.getInstance().load(d.getClass(), "cost", ika);
		processThroughFreeMarker("${deity.cost}", expectedResult.toString());
	}

	public void testListKeyActorMissingSafe()
	{
		NumberManager mgr = new NumberManager();
		FactKey<Number> fk = FactKey.getConstant("cost", mgr);
		FactKeyActor<?> ika = new FactKeyActor<>(fk);
		CDOMObjectWrapper.getInstance().load(Deity.class, "cost", ika);
		processThroughFreeMarker("${deity.cost!}", "");
	}
}
