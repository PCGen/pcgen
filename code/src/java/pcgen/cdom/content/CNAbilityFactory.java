package pcgen.cdom.content;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;

public class CNAbilityFactory
{

	public static CNAbility getCNAbility(Category<Ability> cat, Nature nature,
		Ability a)
	{
		return new CNAbility(cat, a, nature);
	}

}
