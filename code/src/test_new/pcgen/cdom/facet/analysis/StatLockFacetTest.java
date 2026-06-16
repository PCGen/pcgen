/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.StatLock;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class StatLockFacetTest extends
		AbstractExtractingFacetTest<CDOMObject, StatLock>
{

	private StatLockFacet facet;
	private StatLock[] target;
	private CDOMObject[] source;

	@BeforeEach
	@Override
	public void setUp()
	{
		facet = new StatLockFacet();
		super.setUp();
		facet.setFormulaResolvingFacet(new FormulaResolvingFacet());
		CDOMObject cdo1 = new PCTemplate();
		cdo1.setName("Templ");
		CDOMObject cdo2 = new Race();
		cdo2.setName("Race");
		PCStat pcs1 = new PCStat();
		pcs1.setName("Stat1");
		PCStat pcs2 = new PCStat();
		pcs2.setName("Stat2");
		StatLock st1 = new StatLock(CDOMDirectSingleRef.getRef(pcs1), FormulaFactory.getFormulaFor(4));
		StatLock st2 = new StatLock(CDOMDirectSingleRef.getRef(pcs2), FormulaFactory.getFormulaFor(2));
		cdo1.addToListFor(ListKey.STAT_LOCKS, st1);
		cdo2.addToListFor(ListKey.STAT_LOCKS, st2);
		source = new CDOMObject[]{cdo1, cdo2};
		target = new StatLock[]{st1, st2};
	}

	@Override
	protected AbstractSourcedListFacet<CharID, StatLock> getFacet()
	{
		return facet;
	}

	public static int n = 0;

	@Override
	protected StatLock getObject()
	{
		PCStat stat = new PCStat();
		stat.setName("Stat" + n++);
		return new StatLock(CDOMDirectSingleRef.getRef(stat), FormulaFactory.getFormulaFor(1));
	}

	@Override
	protected CDOMObject getContainingObject(int i)
	{
		return source[i];
	}

	@Override
	protected DataFacetChangeListener<CharID, CDOMObject> getListener()
	{
		return facet;
	}

	@Override
	protected StatLock getTargetObject(int i)
	{
		return target[i];
	}

	@Test
	public void testGetLock()
	{
		Object source1 = new Object();
		PCStat stat = new PCStat();
		stat.setName("Stat" + n++);
		StatLock t1 = new StatLock(CDOMDirectSingleRef.getRef(stat), FormulaFactory.getFormulaFor(1));
		PCStat stat1 = new PCStat();
		stat1.setName("Stat" + n++);
		StatLock t2 = new StatLock(CDOMDirectSingleRef.getRef(stat1), FormulaFactory.getFormulaFor(4));
		PCStat stat3 = new PCStat();
		stat3.setName("Stat" + n++);
		StatLock t3 = new StatLock(CDOMDirectSingleRef.getRef(stat3), FormulaFactory.getFormulaFor(2));
		assertNull(facet.getLockedStat(id, stat));
		getFacet().add(id, t1, source1);
		assertEquals(1, facet.getLockedStat(id, stat));
		assertNull(facet.getLockedStat(id, stat1));
		getFacet().add(id, t2, source1);
		assertEquals(4, facet.getLockedStat(id, stat1));
		getFacet().add(id, t3, source1);
		assertEquals(2, facet.getLockedStat(id, stat3));
	}
}
