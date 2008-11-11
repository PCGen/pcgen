package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.Ability;

public class AbilityRef
{

	private final CDOMReference<Ability> abilities;
	private String choice = null;

	public AbilityRef(CDOMReference<Ability> ab)
	{
		abilities = ab;
	}

	public void addChoice(String s)
	{
		choice = s;
	}

	public CDOMReference<Ability> getRef()
	{
		return abilities;
	}
	
	public String getChoice()
	{
		return choice;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AbilityRef)
		{
			AbilityRef other = (AbilityRef) obj;
			if (other.abilities.equals(abilities))
			{
				if (choice == null)
				{
					return other.choice == null;
				}
				else
				{
					return choice.equals(other.choice);
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return 3 - abilities.hashCode();
	}

}
