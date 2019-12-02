/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;

public final class CNAbilitySelectionUtilities
{

	private CNAbilitySelectionUtilities()
	{
		//Do not instantiate utility class
	}

	public static boolean canCoExist(CNAbilitySelection cnas1, CNAbilitySelection cnas2)
	{
		CNAbility cna = cnas1.getCNAbility();
		Ability a = cna.getAbility();
		CNAbility ocna = cnas2.getCNAbility();
		if (!ocna.getAbilityCategory().getParentCategory().equals(cna.getAbilityCategory().getParentCategory()))
		{
			//Different (parent) categories, so doesn't matter...
			//This test is only required because Ability only checks key :/
			return true;
		}
		if (!ocna.getAbility().equals(a))
		{
			//Different abilities, so doesn't matter...
			return true;
		}
		//Same ability here
		if (!a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			//If MULT:NO, then can't coexist...
			return false;
		}
		//MULT:YES here
		if (a.getSafe(ObjectKey.STACKS))
		{
			//Allows stacking, so always true (give or take NUMCHOICES?)
			return true;
		}
		//STACK:NO here
        //enforce STACK:NO
        return !cnas1.getSelection().equals(cnas2.getSelection());
    }
}
