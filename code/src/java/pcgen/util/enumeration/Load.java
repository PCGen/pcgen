package pcgen.util.enumeration;

public enum Load
{

	LIGHT("LIGHT"), MEDIUM("MEDIUM"), HEAVY("HEAVY"), OVERLOAD("OVERLOAD");

	private final String text;

	Load(String s)
	{
		text = s;
	}

	@Override
	public String toString()
	{
		return text;
	}

	public boolean checkLtEq(Load x)
	{
		return ordinal() <= x.ordinal();
	}

	public Load max(Load x)
	{
		return checkLtEq(x) ? x : this;
	}
	
	/**
	 * @param val should be a string value to be checked for equality (case-insensitive) with
	 * 				one of the enum values for this enumeration
	 * @return the enumeration that matches the given string, or null if none match
	 */
	public static Load getLoadType(String val)
	{
		Load r = null;
		
		if (LIGHT.toString().equalsIgnoreCase(val))
		{
			r = LIGHT;
		}
		if (MEDIUM.toString().equalsIgnoreCase(val))
		{
			r = MEDIUM;
		}
		if (HEAVY.toString().equalsIgnoreCase(val))
		{
			r = HEAVY;
		}
		if (OVERLOAD.toString().equalsIgnoreCase(val))
		{
			r = OVERLOAD;
		}
		return r;
	}
}
