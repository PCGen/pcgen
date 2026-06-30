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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.BonusSkillRankChangeFacet.SkillRankChangeEvent;
import pcgen.cdom.facet.BonusSkillRankChangeFacet.SkillRankChangeListener;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins the Double.compare semantics in BonusSkillRankChangeFacet.reset()
 * after the SpotBugs FE_FLOATING_POINT_EQUALITY fix (PR #7628).
 */
class BonusSkillRankChangeFacetTest
{
	private static LoadContext context;
	private static Skill skill;

	private CharID id;
	private final BonusSkillRankChangeFacet facet = new BonusSkillRankChangeFacet();
	private final SRListener listener = new SRListener();
	/** Backing values returned by the in-test BonusCheckingFacet. */
	private Map<String, Double> bonusValues;

	private static final class SRListener implements SkillRankChangeListener
	{
		int eventCount;
		SkillRankChangeEvent lastEvent;

		@Override
		public void bonusChange(SkillRankChangeEvent event)
		{
			eventCount++;
			lastEvent = event;
		}
	}

	@BeforeAll
	static void staticSetUp()
	{
		SettingsHandler.getGameAsProperty().get().clearLoadContext();
		context = Globals.getContext();
		AbstractReferenceContext ref = context.getReferenceContext();
		FacetLibrary.getFacet(LoadContextFacet.class).set(context.getDataSetID(),
			new WeakReference<>(context));
		skill = new Skill();
		skill.setName("TestSkill");
		ref.importObject(skill);
	}

	@BeforeEach
	void setUp()
	{
		DataSetID cid = context.getDataSetID();
		id = CharID.getID(cid);
		bonusValues = new HashMap<>();
		facet.addSkillRankChangeListener(listener);
		facet.setBonusCheckingFacet(new BonusCheckingFacet()
		{
			@Override
			public double getBonus(CharID charID, String bonusType, String bonusName)
			{
				return bonusValues.getOrDefault(bonusType + "." + bonusName, 0.0d);
			}
		});
	}

	/** First reset fires once; second reset with same value does not refire. */
	@Test
	void sameValueDoesNotRefire()
	{
		bonusValues.put("SKILLRANK." + skill.getKeyName(), 3.0d);

		facet.reset(id);
		assertEquals(1, listener.eventCount, "first reset should fire one event");
		assertEquals(3.0d, listener.lastEvent.getNewVal().doubleValue(), 0.0001);

		facet.reset(id);
		assertEquals(1, listener.eventCount, "same-value reset should not refire");
	}

	/** Reset with a changed value fires a new event with old/new values. */
	@Test
	void changedValueFiresAgain()
	{
		bonusValues.put("SKILLRANK." + skill.getKeyName(), 3.0d);
		facet.reset(id);
		assertEquals(1, listener.eventCount);

		bonusValues.put("SKILLRANK." + skill.getKeyName(), 5.0d);
		facet.reset(id);
		assertEquals(2, listener.eventCount, "changed value should fire a new event");
		assertEquals(3.0d, listener.lastEvent.getOldVal().doubleValue(), 0.0001);
		assertEquals(5.0d, listener.lastEvent.getNewVal().doubleValue(), 0.0001);
	}
}
