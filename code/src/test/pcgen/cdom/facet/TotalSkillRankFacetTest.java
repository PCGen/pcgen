/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.event.AssociationChangeEvent;
import pcgen.cdom.facet.event.AssociationChangeListener;
import pcgen.core.Skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins the Double.compare semantics in TotalSkillRankFacet.set()
 * after the SpotBugs FE_FLOATING_POINT_EQUALITY fix (PR #7628).
 */
class TotalSkillRankFacetTest
{
	private CharID id;
	private final TotalSkillRankFacet facet = new TotalSkillRankFacet();
	private final ACListener listener = new ACListener();
	private Skill skill;

	private static final class ACListener implements AssociationChangeListener
	{
		int eventCount;
		AssociationChangeEvent lastEvent;

		@Override
		public void bonusChange(AssociationChangeEvent event)
		{
			eventCount++;
			lastEvent = event;
		}
	}

	@BeforeEach
	void setUp()
	{
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		facet.addAssociationChangeListener(listener);
		skill = new Skill();
		skill.setName("TestSkill");
	}

	/** Setting the same Double value twice stores once and fires once. */
	@Test
	void sameValueIsNotReapplied()
	{
		facet.set(id, skill, 3.0d);
		assertEquals(1, listener.eventCount, "first set should fire");
		assertEquals(3.0d, facet.get(id, skill), 0.0001);

		facet.set(id, skill, 3.0d);
		assertEquals(1, listener.eventCount, "same-value set should not refire");
	}

	/** Setting a different Double value fires again with old/new values. */
	@Test
	void changedValueIsReapplied()
	{
		facet.set(id, skill, 3.0d);
		assertEquals(1, listener.eventCount);

		facet.set(id, skill, 5.0d);
		assertEquals(2, listener.eventCount, "changed value should fire a new event");
		assertEquals(5.0d, facet.get(id, skill), 0.0001);
		assertEquals(3.0d, listener.lastEvent.getOldVal().doubleValue(), 0.0001);
		assertEquals(5.0d, listener.lastEvent.getNewVal().doubleValue(), 0.0001);
	}
}
