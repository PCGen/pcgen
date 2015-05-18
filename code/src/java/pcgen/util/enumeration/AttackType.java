package pcgen.util.enumeration;

public enum AttackType
{

	MELEE("BAB"),

	RANGED("RAB"),

	UNARMED("UAB"),

	GRAPPLE("GAB");

	private final String identifier;

	private AttackType(String ident)
	{
		identifier = ident;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public static AttackType getInstance(String ident)
	{
		for (AttackType at : AttackType.values())
		{
			if (at.identifier.equals(ident))
			{
				return at;
			}
		}
		throw new IllegalArgumentException("Illegal AttackType identifier: "
			+ ident);
	}
}
