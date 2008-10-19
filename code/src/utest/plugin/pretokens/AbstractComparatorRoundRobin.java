package plugin.pretokens;

public abstract class AbstractComparatorRoundRobin extends
		AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public abstract boolean isBaseAllowed();

	@Override
	public void runPositiveRoundRobin(String s)
	{
		super.runPositiveRoundRobin("PRE" + getBaseString() + "GT:" + s);
		super.runPositiveRoundRobin("PRE" + getBaseString() + "GTEQ:" + s);
		super.runPositiveRoundRobin("PRE" + getBaseString() + "LT:" + s);
		super.runPositiveRoundRobin("PRE" + getBaseString() + "LTEQ:" + s);
		super.runPositiveRoundRobin("PRE" + getBaseString() + "NEQ:" + s);
		super.runPositiveRoundRobin("PRE" + getBaseString() + "EQ:" + s);
		if (isBaseAllowed())
		{
			// super.runPositiveRoundRobin("PRE" + getBaseString() + ":" + s);
		}
	}

	@Override
	public void runNegativeRoundRobin(String s)
	{
	}

}
