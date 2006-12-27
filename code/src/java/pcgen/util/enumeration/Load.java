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
}
