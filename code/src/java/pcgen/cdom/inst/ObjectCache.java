/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.inst;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.Vision;
import pcgen.core.analysis.SkillCostCalc;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.enumeration.VisionType;

public class ObjectCache extends CDOMObject
{

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	public void initializeListFor(ListKey<?> lk)
	{
		listChar.initializeListFor(lk);
	}

	public void initializeVisionCache(PlayerCharacter pc)
	{
		initializeListFor(ListKey.VISION_CACHE);
		Map<VisionType, Integer> map = new HashMap<VisionType, Integer>();
		for (CDOMObject cdo : pc.getCDOMObjectList())
		{
			Collection<CDOMReference<Vision>> mods = cdo
					.getListMods(Vision.VISIONLIST);
			if (mods == null)
			{
				continue;
			}
			for (CDOMReference<Vision> ref : mods)
			{
				Collection<AssociatedPrereqObject> assoc = cdo
						.getListAssociations(Vision.VISIONLIST, ref);
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), pc,
							null))
					{
						for (Vision v : ref.getContainedObjects())
						{
							VisionType visType = v.getType();
							int a = pc.getVariableValue(v.getDistance(), "")
									.intValue();
							Integer current = map.get(visType);
							if (current == null || current < a)
							{
								map.put(visType, a);
							}
						}
					}
				}
			}
		}

		/*
		 * parse through the global list of vision tags and see if this PC has
		 * any BONUS:VISION tags which will create a new visionMap entry, and
		 * add any BONUS to existing entries in the map
		 */
		for (VisionType vType : VisionType.getAllVisionTypes())
		{
			final int aVal = (int) pc.getTotalBonusTo("VISION", vType
					.toString());

			if (aVal > 0)
			{
				Integer current = map.get(vType);
				map.put(vType, aVal + (current == null ? 0 : current));
			}
		}
		TreeSet<Vision> set = new TreeSet<Vision>();
		for (Map.Entry<VisionType, Integer> me : map.entrySet())
		{
			set.add(new Vision(me.getKey(), me.getValue().toString()));
		}
		addAllToListFor(ListKey.VISION_CACHE, set);
	}
	
	private final DoubleKeyMap<String, PCClass, SkillCost> skillCostMap = new DoubleKeyMap<String, PCClass, SkillCost>();

	public SkillCost getSkillCost(PlayerCharacter pc, Skill skill, PCClass pcc)
	{
		String sk = skill.getKeyName();
		SkillCost cost = skillCostMap.get(sk, pcc);
		if (cost == null)
		{
			cost = SkillCostCalc.skillCostForPCClass(skill, pcc, pc);
			skillCostMap.put(sk, pcc, cost);
		}
		return cost;
	}

}
