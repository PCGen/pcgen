/*
 * VariableProcessorEq.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 13-Dec-2004
 */
package pcgen.core;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

/**
 * <code>VariableProcessorEq</code> is a processor for variables
 * associated with a character's equipment. This class converts
 * formulas or variables into values and is used extensively
 * both in defintions of objects and for output to output sheets.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Chris Ward <frugal@purplewombat.co.uk>
 * @version $Revision$
 */
public class VariableProcessorEq extends VariableProcessor
{

	private Equipment eq;
	private boolean primaryHead;

	/**
	 * Create a new VariableProcessorEq instance for an equipment item, and pc. It
	 * also allows splitting of the processing of the heads of double weapons.
	 *
	 * @param eq The item of equipment  being processed.
	 * @param pc The player character being processed.
	 * @param primaryHead Is this the primary head of a double weapon?
	 */
	public VariableProcessorEq(Equipment eq, PlayerCharacter pc,
							   boolean primaryHead)
	{
		super(pc);
		this.eq = eq;
		this.primaryHead = primaryHead;
	}

	/**
	 * @see pcgen.core.VariableProcessor#lookupVariable(java.lang.String, java.lang.String, pcgen.core.spell.Spell)
	 */
	Float lookupVariable(String element, String src, Spell spell)
	{
		Float retVal = null;
		if (getPc().hasVariable(element))
		{
			final Float value = getPc().getVariable(element, true, true, src,
				"", decrement);
			Logging.debugPrint(jepIndent + "variable for: '" + element + "' = "
							   + value);
			retVal = new Float(value.doubleValue());
		}

		if (retVal == null)
		{
			final String foo = getInternalVariable(spell, element, src);
			if (foo != null)
			{
				Float d = null;
				try
				{
					d = new Float(foo);
				}
				catch (NumberFormatException nfe)
				{
					// What we got back was not a number
				}
				if (d != null)
				{
					if (!d.isNaN())
					{
						retVal = d;
						Logging.debugPrint(jepIndent
										   + "internal variable for: '"
										   + element + "' = " + d);
					}
				}
				else
				{
					try
					{
						d = new Float(foo.substring(1));
						if (!d.isNaN())
						{
							retVal = d;
							Logging.debugPrint(jepIndent + "internal variable for: '" + element + "' = " + d);
						}
					}
					catch (NumberFormatException nfe)
					{
						// What we got back was not a number
					}
				}
			}
		}

		if (retVal == null)
		{
			final String foo = getExportVariable(element);
			if (foo != null)
			{
				Float d = null;
				try
				{
					d = new Float(foo);
				}
				catch (NumberFormatException nfe)
				{
					// What we got back was not a number
				}
				if (d != null)
				{
					if (!d.isNaN())
					{
						retVal = d;
						Logging.debugPrint(jepIndent + "export variable for: '"
										   + element + "' = " + d);
					}
				}
			}
		}

		return retVal;
	}

	/**
	 * Retrieve a pre-coded variable for a piece of equipment. These are known properties of
	 * all equipment items. If a value is not found for the equipment item, a search will be
	 * made of the character.
	 *
	 * @param aSpell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @param valString The variable to be evaluated
	 * @param src The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	public String getInternalVariable(final Spell aSpell, String valString,
									  final String src)
	{
		String retVal = null;
		if ("SIZE".equals(valString))
		{
			retVal = String.valueOf(eq.sizeInt());
		}
		else if (valString.startsWith("EQUIP.SIZE"))
		{
			if (valString.equals("EQUIP.SIZE"))
			{
				retVal = eq.getSize();
			}
			else if (valString.substring(11).equals("INT"))
			{
				retVal = String.valueOf(eq.sizeInt());
			}
		}
		else if ("WT".equals(valString))
		{
			if (eq.isCalculatingCost() && eq.isWeightAlreadyUsed())
			{
				retVal = "0";
			}
			else
			{
				if (eq.isCalculatingCost() && eq.isAmmunition())
				{
					final Float unitWeight = new Float(eq.getWeightInPounds()
						/ eq.getBaseQty());
					retVal = unitWeight.toString();
				}
				else
				{
					retVal = String.valueOf(eq.getWeightInPounds());
				}

				eq.setWeightAlreadyUsed(true);
			}
		}
		else if ("BASECOST".equals(valString))
		{
			retVal = eq.getBaseCost().toString();
		}
		else if ("DMGDIE".equals(valString))
		{
			final RollInfo aRollInfo = new RollInfo(eq.getDamage(getPc()));
			retVal = Integer.toString(aRollInfo.sides);
		}
		else if ("DMGDICE".equals(valString))
		{
			final RollInfo aRollInfo = new RollInfo(eq.getDamage(getPc()));
			retVal = Integer.toString(aRollInfo.times);
		}
		else if ("EQACCHECK".equals(valString))
		{
			Integer acCheck = eq.get(IntegerKey.AC_CHECK);
			retVal = acCheck == null ? "0" : acCheck.toString();
		}
		else if ("EQHANDS".equals(valString))
		{
			retVal = Integer.toString(eq.getSlots());
		}
		else if ("EQSPELLFAIL".equals(valString))
		{
			retVal = eq.getSpellFailure().toString();
		}
		else if ("RANGE".equals(valString))
		{
			retVal = eq.getRange().toString();
		}
		else if ("CRITMULT".equals(valString))
		{
			if (primaryHead)
			{
				retVal = eq.getCritMult();
			}
			else
			{
				retVal = eq.getAltCritMult();
			}
		}
		else if ("RACEREACH".equals(valString))
		{
			retVal = getPc().getVariableValue("REACH.VAL", src).toString();
		}
		else if ("REACH".equals(valString))
		{
			retVal = Integer.toString(eq.getReach());
		}
		else if ("REACHMULT".equals(valString))
		{
			retVal = Integer.toString(eq.getReachMult());
		}
		else
		{
			for (int j = 0; j < SettingsHandler.getGame().s_ATTRIBSHORT.length;
				 ++j)
			{
				if (valString.equals(SettingsHandler.getGame().s_ATTRIBSHORT[j]))
				{
					retVal = String.valueOf(getPc().getStatList().getStatModFor(
						SettingsHandler.getGame().s_ATTRIBSHORT[j]));

					break;
				}
			}
		}
		if (retVal == null)
		{
			// we have not managed to find an internal variable for the equipment, so try to find
			// one for the character.
			VariableProcessorPC vpc = new VariableProcessorPC(getPc());
			retVal = vpc.getInternalVariable(aSpell, valString, src);
		}
		return retVal;
	}
}
