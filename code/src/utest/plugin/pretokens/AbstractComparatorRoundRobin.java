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
			super.runSimpleRoundRobin("PRE" + getBaseString() + ":" + s, "PRE"
					+ getBaseString() + "GTEQ:" + s);
		}
	}

	@Override
	public void runNegativeRoundRobin(String s)
	{
		super.runSimpleRoundRobin("!PRE" + getBaseString() + "GT:" + s, "PRE"
				+ getBaseString() + "LTEQ:" + s);
		super.runSimpleRoundRobin("!PRE" + getBaseString() + "GTEQ:" + s, "PRE"
				+ getBaseString() + "LT:" + s);
		super.runSimpleRoundRobin("!PRE" + getBaseString() + "LT:" + s, "PRE"
				+ getBaseString() + "GTEQ:" + s);
		super.runSimpleRoundRobin("!PRE" + getBaseString() + "LTEQ:" + s, "PRE"
				+ getBaseString() + "GT:" + s);
		super.runSimpleRoundRobin("!PRE" + getBaseString() + "NEQ:" + s, "PRE"
				+ getBaseString() + "EQ:" + s);
		super.runSimpleRoundRobin("!PRE" + getBaseString() + "EQ:" + s, "PRE"
				+ getBaseString() + "NEQ:" + s);
		if (isBaseAllowed())
		{
			super.runSimpleRoundRobin("!PRE" + getBaseString() + ":" + s, "PRE"
					+ getBaseString() + "LT:" + s);
		}
	}

}
