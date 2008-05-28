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

import pcgen.cdom.enumeration.SkillCost;
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
		return node.getItem().equals(
				skill.getSkillCostType(tab.getSelectedPCClass(), tab.getPc()));
	}

	private static boolean keystatsMatch(PObjectNode node, Skill skill)
	{
		if (Globals.isSkillTypeHidden(skill.getMyType(0)))
		{
			return false;
		}

		return node.toString().equals(skill.getMyType(0));
	}

	/**
	 * Abstract sorter class to assist in sorting skills
	 */
	public static abstract class AbstractSorter implements InfoSkillsSorter
	{
		InfoSkills tab;

		/**
		 * Constructor
		 * @param tab
		 */
		public AbstractSorter(InfoSkills tab)
		{
			this.tab = tab;
		}

		public PObjectNode finalPass(PObjectNode node)
		{
			return node;
		}
	}

	/**
	 * Abstract helper class for sorting skills by sub type then name
	 */
	public static abstract class AbstractSubtypeName_Penultimate extends AbstractSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
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

	/**
	 * Abstract class to assist sorting skills by cost
	 */
	public static abstract class CostSorter extends AbstractSorter
	{
		private int n = 0;

		/**
		 * Constructor
		 * @param tab
		 */
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
			final SkillCost[] costs = { SkillCost.CLASS, SkillCost.CROSS_CLASS, SkillCost.EXCLUSIVE};

			return costs[n++];
		}
	}

	/**
	 * Abstract class to assist sorting skills
	 */
	public static abstract class FinalSorter extends AbstractSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
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

	/**
	 * Concrete class to assist sorting skills by primary cost
	 */
	public static class CostName_Primary extends CostSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public CostName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new CostName_Secondary(tab);
		}
	}

	/**
	 * Concrete class to assist sorting skills by secondary cost
	 */
	public static class CostName_Secondary extends FinalSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public CostName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return costsMatch(node, skill, tab);
		}
	}

	/**
	 * Concrete class to assist sorting skills by cost, subtype, name
	 */
	public static class CostSubtypeName_Primary extends CostSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public CostSubtypeName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new CostSubtypeName_Secondary(tab);
		}
	}

	/**
	 * Concrete class to assist sorting skills by subtype, name, secondary cost
	 */
	public static class CostSubtypeName_Secondary extends AbstractSubtypeName_Penultimate
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public CostSubtypeName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return costsMatch(node, skill, tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new CostSubtypeName_Final(tab);
		}
	}

	/**
	 * Concrete class to assist sorting skills by cost, subtype, name
	 */
	public static class CostSubtypeName_Final extends FinalSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public CostSubtypeName_Final(InfoSkills tab)
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
					ListIterator<PObjectNode> gcIt = ((PObjectNode) jt.next());

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
					if (costsMatch(node.getParent(), skill, tab) && node.toString().equals(skillType))
					{
						return true;
					}
				}
			}

			return false;
		}
	}

	/**
	 * Concrete class to assist sorting skills by subtype, name
	 */
	public static class GenericSubtypeName_Final extends FinalSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
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
					ListIterator<PObjectNode> gcIt = ((PObjectNode) jt.next());

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

	/**
	 * Concrete class to assist sorting skills by key stat
	 */
	public static class KeystatName_Primary extends KeystatSubtypeName_Primary
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public KeystatName_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public InfoSkillsSorter nextSorter()
		{
			return new KeystatName_Secondary(tab);
		}
	}

	/**
	 * Concrete class to assist sorting skills by secondary key stat
	 */
	public static class KeystatName_Secondary extends FinalSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public KeystatName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return keystatsMatch(node, skill);
		}
	}

	/**
	 * Concrete class to assist sorting skills by keystat, primary
	 */
	public static class KeystatSubtypeName_Primary extends AbstractSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
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

	/**
	 * Concrete class to assist sorting skills by keystat, subtype, name
	 */
	public static class KeystatSubtypeName_Secondary extends AbstractSubtypeName_Penultimate
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public KeystatSubtypeName_Secondary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return keystatsMatch(node, skill);
		}
	}

	/**
	 * Concrete class to assist sorting skills by name, primary
	 */
	public static class Name_Primary extends FinalSorter
	{
		/**
		 * Constructor
		 * @param tab
		 */
		public Name_Primary(InfoSkills tab)
		{
			super(tab);
		}

		public boolean nodeGoHere(PObjectNode node, Skill skill)
		{
			return true;
		}
	}

	/**
	 * Concrete class to assist sorting skills by subtype, name, primary
	 */
	public static class SubtypeName_Primary extends AbstractSubtypeName_Penultimate
	{
		/**
		 * Constructor
		 * @param tab
		 */
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
