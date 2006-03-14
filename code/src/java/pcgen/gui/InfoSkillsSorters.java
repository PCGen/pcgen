/*
 * InfoSkillsSorters.java
 * Copyright 2002 (C) Bryan McRoberts
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
 *
 * Created on Jan 13, 2003, 9:26 PM CST
 */
package pcgen.gui;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.gui.tabs.InfoSkills;
import pcgen.gui.utils.PObjectNode;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * @author  B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 * @version $Revision$
 */
public final class InfoSkillsSorters
{
	private static boolean costsMatch(PObjectNode node, Skill skill, InfoSkills tab)
	{
		return node.toString().equals(skill.getSkillCostType(tab.getSelectedPCClass(), tab.getPc()));
	}

	private static boolean keystatsMatch(PObjectNode node, Skill skill)
	{
		if (Globals.isSkillTypeHidden(skill.getMyType(0)))
		{
			return false;
		}

		return node.toString().equals(skill.getMyType(0));
	}

	public static abstract class AbstractSorter implements InfoSkillsSorter
	{
		InfoSkills tab;

		public AbstractSorter(InfoSkills tab)
		{
			this.tab = tab;
		}

		public PObjectNode finalPass(PObjectNode node)
		{
			return node;
		}
	}

	public static abstract class AbstractSubtypeName_Penultimate extends AbstractSorter
	{
		public AbstractSubtypeName_Penultimate(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new GenericSubtypeName_Final(tab);
		}

		public boolean nodeHaveNext()
		{
			return true;
		}

		public Object whatPart(boolean available, Skill skill, PlayerCharacter pc)
		{
			if (skill.getSubtypeCount() > 0)
			{
				return skill.getSubtypeIterator();
			}

			return InfoSkills.createSkillWrapper(available, skill, pc);
		}
	}

	public static abstract class CostSorter extends AbstractSorter
	{
		private int n = 0;

		public CostSorter(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return n < InfoSkills.nCosts;
		}

		public boolean nodeHaveNext()
		{
			return true;
		}

		public Object whatPart(boolean available, Skill skill, PlayerCharacter pc)
		{
			final String[] costs = { Skill.COST_CLASS, Skill.COST_XCLASS, Skill.COST_EXCL };

			return costs[n++];
		}
	}

	public static abstract class FinalSorter extends AbstractSorter
	{
		public FinalSorter(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			throw new UnsupportedOperationException();
		}

		public boolean nodeHaveNext()
		{
			return false;
		}

		public Object whatPart(boolean available, Skill skill, PlayerCharacter pc)
		{
			return InfoSkills.createSkillWrapper(available, skill, pc);
		}
	}

	public static class CostName_Primary extends CostSorter
	{
		public CostName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new CostName_Secondary(tab);
		}
	}

	public static class CostName_Secondary extends FinalSorter
	{
		public CostName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return costsMatch(node, skill, tab);
		}
	}

	public static class CostSubtypeName_Primary extends CostSorter
	{
		public CostSubtypeName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new CostSubtypeName_Secondary(tab);
		}
	}

	public static class CostSubtypeName_Secondary extends AbstractSubtypeName_Penultimate
	{
		public CostSubtypeName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return costsMatch(node, skill, tab);
		}
	}

	public static class GenericSubtypeName_Final extends FinalSorter
	{
		public GenericSubtypeName_Final(InfoSkills tab)
		{
			super(tab);
		}

		/**
		 * Pass up singletons so that subtypes with only one
		 * member get promoted to the secondary level.
		 *
		 * @param node the root node
		 *
		 * @return the root node, usually as <code>node</code>
		 */
		public PObjectNode finalPass(PObjectNode node)
		{
			// children
			for (ListIterator it = node; it.hasNext();)
			{
				PObjectNode child = (PObjectNode) it.next();

				// grandchildren
				for (ListIterator jt = child; jt.hasNext();)
				{
					ListIterator gcIt = ((PObjectNode) jt.next());

					// subtype level; use arrays
					// instead of iterator to make
					// replacement simple.  XXX
					while (gcIt.hasNext())
					{
						gcIt.next();

						if (node.getChildCount() == 1)
						{
							gcIt.set(node.getChild(0));
						}
					}
				}
			}

			return node;
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			String skillType;
			for (Iterator it = skill.getSubtypeIterator(); it.hasNext();)
			{
				skillType = it.next().toString();
				if (!Globals.isSkillTypeHidden(skillType))
				{
					if (node.toString().equals(skillType))
					{
						return true;
					}
				}
			}

			return false;
		}
	}

	public static class KeystatName_Primary extends KeystatSubtypeName_Primary
	{
		public KeystatName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new KeystatName_Secondary(tab);
		}
	}

	public static class KeystatName_Secondary extends FinalSorter
	{
		public KeystatName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return keystatsMatch(node, skill);
		}
	}

	public static class KeystatSubtypeName_Primary extends AbstractSorter
	{
		public KeystatSubtypeName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new KeystatSubtypeName_Secondary(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return true;
		}

		public boolean nodeHaveNext()
		{
			return true;
		}

		public Object whatPart(boolean available, Skill skill, PlayerCharacter pc)
		{
			if (Globals.isSkillTypeHidden(skill.getMyType(0)))
			{
				return "";
			}
			return skill.getMyType(0);
		}
	}

	public static class KeystatSubtypeName_Secondary extends AbstractSubtypeName_Penultimate
	{
		public KeystatSubtypeName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return keystatsMatch(node, skill);
		}
	}

	public static class Name_Primary extends FinalSorter
	{
		public Name_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return true;
		}
	}

	public static class SubtypeName_Primary extends AbstractSubtypeName_Penultimate
	{
		public SubtypeName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return true;
		}
	}
}
