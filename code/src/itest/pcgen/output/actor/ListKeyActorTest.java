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

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.core.Deity;
import pcgen.output.publish.OutputDB;
import pcgen.output.testsupport.AbstractOutputTestCase;
import pcgen.output.wrapper.CDOMObjectWrapper;

public class ListKeyActorTest extends AbstractOutputTestCase
{

	private static final DeityFacet DF = new DeityFacet();

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
	}

	private void classSetUp()
	{
		OutputDB.reset();
		DF.init();
	}

	public void testListKeyActor()
	{
		Deity d = new Deity();
		d.setName("Bob");
		String expectedResult1 = "Magical";
		String expectedResult2 = "Long";
		DF.set(id, d);
		d.addToListFor(ListKey.BOOK_TYPE, expectedResult1);
		d.addToListFor(ListKey.BOOK_TYPE, expectedResult2);
		ListKeyActor lka = new ListKeyActor(ListKey.BOOK_TYPE);
		CDOMObjectWrapper.load(dsid, d.getClass(), "booktype", lka);
		processThroughFreeMarker("${deity.booktype[0]}", expectedResult1);
		processThroughFreeMarker("${deity.booktype[1]}", expectedResult2);
	}

	public void testListKeyActorMissingSafe()
	{
		ListKeyActor lka = new ListKeyActor(ListKey.BOOK_TYPE);
		CDOMObjectWrapper.load(dsid, Deity.class, "booktype", lka);
		processThroughFreeMarker("${(deity.booktype[0])!}", "");
		processThroughFreeMarker("${(deity.booktype.join(\", \"))!}", "");
	}

}
