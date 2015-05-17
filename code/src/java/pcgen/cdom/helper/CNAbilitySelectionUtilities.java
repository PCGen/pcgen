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

	public static boolean canCoExist(CNAbilitySelection cnas1,
		CNAbilitySelection cnas2)
	{
		CNAbility cna = cnas1.getCNAbility();
		Ability a = cna.getAbility();
		CNAbility ocna = cnas2.getCNAbility();
		if (!ocna.getAbilityCategory().getParentCategory()
			.equals(cna.getAbilityCategory().getParentCategory()))
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
		if (cnas1.getSelection().equals(cnas2.getSelection()))
		{
			//enforce STACK:NO
			return false;
		}
		return true;
	}
}
