/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2004 Ross M. Lodge
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
 *
 * OpposedSkillModel.java
 *
 * Created on May 4, 2004, 1:49:47 PM
 */

package plugin.initiative;

import gmgen.plugin.PcgCombatant;

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;

import pcgen.core.RollingMethods;

/**
 * <p>
 * Overrides OpposedSkillBasicModel to provide basic skill rolling abilities.
 * </p>
 *
 * <p>
 * Current Ver: $Revision$
 * </p>
 * <p>
 * </p>
 * <p>
 * </p>
 *
 * @author LodgeR
 */
public class OpposedSkillModel extends OpposedSkillBasicModel
{

	/**
	 * <p>
	 * "Wrapper" class that extends {@code InitWrapper} to provide
	 * skill check facilities.
	 * </p>
	 */
	protected class SkillInitWrapper extends InitWrapper
	{
		/** Integer representing a fudge factor */
		private Integer fudge = null;
		/** Integer representing the result of the roll */
		private Integer result = null;
		/** Integer cacheing the roll value itself */
		private Integer roll = null;

		/**
		 * <p>
		 * Basic constructor
		 * </p>
		 *
		 * @param init
		 */
		public SkillInitWrapper(PcgCombatant init)
		{
			super(init);
			if (skillName != null)
			{
				roll();
			}
		}

		/**
		 * <p>
		 * Gets the skill bonus for the specified skill name
		 * </p>
		 *
		 * @param aSkillKey
		 * @return skillBonus
		 */
		public Integer getSkillBonus(String aSkillKey)
		{
			Integer returnValue = null;
			if (initiative != null && aSkillKey != null)
			{
				PlayerCharacter pc = initiative.getPC();
				Skill skill = Globals.getContext().getReferenceContext()
						.silentlyGetConstructedCDOMObject(Skill.class,
								aSkillKey);
				if (skill != null && pc.getDisplay().hasSkill(skill))
				{
					returnValue =
							SkillModifier.modifier(skill, pc).intValue()
									+ SkillRankControl.getTotalRank(pc, skill).intValue();
				}
				else if (skill != null
						&& skill.getSafe(ObjectKey.USE_UNTRAINED)
						&& skill.get(ObjectKey.KEY_STAT) != null)
				{
					returnValue = SkillModifier.modifier(skill,
							pc).intValue();
				}
			}
			return returnValue;
		}

		/**
		 * <p>
		 * Rolls the skill check.
		 * </p>
		 */
		public void roll()
		{
			roll = RollingMethods.roll("1d20");
			calc();
		}

		/**
		 * <p>
		 * Calculates the final result of the skill check.
		 * </p>
		 */
		public void calc()
		{
			Integer i = getSkillBonus(skillName);
			if (i != null && roll != null)
			{
				int r = roll.intValue();
				r += i.intValue();
				i = fudge;
				if (i != null)
				{
					r += i.intValue();
				}
				result = r;
			}
			else
			{
				result = null;
			}
		}

		/**
		 * <p>
		 * Gets the value of fudge
		 * </p>
		 * @return Returns the fudge.
		 */
		public Integer getFudge()
		{
			return fudge;
		}

		/**
		 * <p>
		 * Sets the value of fudge
		 * </p>
		 *
		 * @param fudge The fudge to set.
		 */
		public void setFudge(Integer fudge)
		{
			this.fudge = fudge;
			calc();
		}

		/**
		 * <p>
		 * Gets the value of result
		 * </p>
		 * @return Returns the result.
		 */
		public Integer getResult()
		{
			return result;
		}

		/**
		 * <p>
		 * Sets the value of result
		 * </p>
		 *
		 * @param result The result to set.
		 */
		public void setResult(Integer result)
		{
			this.result = result;
		}
	}

	/** Name of the skill being currently used for rolls */
	protected String skillName;

	/**
	 * <p>
	 * Constructor -- adds columns
	 * </p>
	 */
	public OpposedSkillModel()
	{
		super();
		columns.addColumn("BONUS", Integer.class, 0, false,
			"Bonus");
		columns.addColumn("FUDGE", Integer.class, null, true, "Fudge");
		columns.addColumn("RESULT", Integer.class, null, false, "Result");
	}

	/**
	 * <p>
	 * Constructor -- adds columns and initializes the combatant list
	 * </p>
	 *
	 * @param combatantList
	 */
	public OpposedSkillModel(List combatantList)
	{
		super(combatantList);
		columns.addColumn("BONUS", Integer.class, 0, false,
			"Bonus");
		columns.addColumn("FUDGE", Integer.class, null, true, "Fudge");
		columns.addColumn("RESULT", Integer.class, null, false, "Result");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
    @Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object returnValue = null;
		if (rowIndex < combatants.size())
		{
			SkillInitWrapper entry = (SkillInitWrapper) getRowEntry(rowIndex);
			switch (columnIndex)
			{
				case 0:
					returnValue = super.getValueAt(rowIndex, columnIndex);
					break;
				case 1:
					returnValue = entry.getSkillBonus(skillName);
					break;
				case 2:
					returnValue = entry.getFudge();
					break;
				case 3:
					returnValue = entry.getResult();
					break;
			}
		}
		return returnValue;
	}

	/**
	 * <p>
	 * Rolls the check for the specified roll
	 * </p>
	 *
	 * @param rowIndex
	 */
	public void roll(int rowIndex)
	{
		if (rowIndex < combatants.size())
		{
			((SkillInitWrapper) getRowEntry(rowIndex)).roll();
			fireTableCellUpdated(rowIndex, 3);
		}
	}

	/**
	 * <p>
	 * Rolls the check for all rows
	 * </p>
	 */
	public void rollAll()
	{
		for (int i = 0; i < combatants.size(); i++)
		{
			roll(i);
		}
	}

	/**
	 * <p>
	 * Sets the skill name and rolls dice for all rows
	 * </p>
	 *
	 * @param name
	 */
	public void setSkill(String name)
	{
		skillName = name;
		rollAll();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
    @Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (rowIndex < combatants.size() && columnIndex == 2
			&& aValue instanceof Integer)
		{
			SkillInitWrapper entry = (SkillInitWrapper) getRowEntry(rowIndex);
			entry.setFudge((Integer) aValue);
		}
		else
		{
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
	}

	/* (non-Javadoc)
	 * @see plugin.initiative.OpposedSkillBasicModel#addCombatant(gmgen.plugin.PcgCombatant)
	 */
    @Override
	public void addCombatant(PcgCombatant combatant)
	{
		combatants.put(combatant.getName(), new SkillInitWrapper(combatant));
		int rowIndex = getIndexOf(combatant.getName());
		fireTableRowsInserted(rowIndex, rowIndex);
	}
}
