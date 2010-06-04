package pcgen.cdom.identifier;

import pcgen.cdom.base.Identified;

public class SpellSchool implements Identified
{

	private String name;

	public String getDisplayName()
	{
		return name;
	}

	public String getKeyName()
	{
		return name;
	}

	public String getLSTformat()
	{
		return name;
	}

	public boolean isInternal()
	{
		return false;
	}

	public boolean isType(String type)
	{
		return false;
	}

	public void setName(String s)
	{
		name = s;
	}

	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof SpellSchool
			&& name.equals(((SpellSchool) other).name);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

}
