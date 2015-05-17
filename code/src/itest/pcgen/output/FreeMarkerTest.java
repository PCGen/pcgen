/*
 * Copyright (c) 2015 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output;

import pcgen.base.util.BasicIndirect;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.facet.model.CheckFacet;
import pcgen.core.PCCheck;
import pcgen.output.actor.FactKeyActor;
import pcgen.output.publish.OutputDB;
import pcgen.output.testsupport.AbstractOutputTestCase;
import pcgen.output.wrapper.CDOMObjectWrapper;
import plugin.format.StringManager;

public class FreeMarkerTest extends AbstractOutputTestCase
{

	private static final CheckFacet cf = new CheckFacet();

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
		cf.init();
	}

	public void testBasic()
	{
		createChecks();
		processThroughFreeMarker("<#list checks as obj>" + "${obj.shortname}"
			+ "</#list>", "WillRefFort");
	}

	public void testNested()
	{
		createChecks();
		String macro = "<#macro getKeyed objlist key>" + "<#list objlist as obj>"
				+ "<#if obj.key == key><#nested obj></#if>" + "</#list>"
				+ "</#macro>";

		processThroughFreeMarker(macro
			+ "<@getKeyed checks \"Willpower\" ; ck>${ck.shortname}</@getKeyed>",
			"Will");
	}

	private void createChecks()
	{
		StringManager sm = new StringManager();
		FactKey<String> sn = FactKey.getConstant("ShortName", sm);

		PCCheck pcc = new PCCheck();
		pcc.setName("Willpower");
		pcc.put(sn, new BasicIndirect<>(sm, "Will"));
		cf.add(id, pcc);
		pcc = new PCCheck();
		pcc.setName("Reflex");
		pcc.put(sn, new BasicIndirect<>(sm, "Ref"));
		cf.add(id, pcc);
		pcc = new PCCheck();
		pcc.setName("Fortitude");
		pcc.put(sn, new BasicIndirect<>(sm, "Fort"));
		cf.add(id, pcc);

		FactKeyActor<?> fka = new FactKeyActor<>(sn);
		CDOMObjectWrapper.getInstance().load(pcc.getClass(), "shortname", fka);
	}

}
