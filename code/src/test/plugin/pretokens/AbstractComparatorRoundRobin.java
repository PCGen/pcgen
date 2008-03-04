package plugin.pretokens;

public abstract class AbstractComparatorRoundRobin extends
		AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public abstract boolean isBaseAllowed();

	@Override
	public void runRoundRobin(String s)
	{
		super.runRoundRobin("PRE" + getBaseString() + "GT:" + s);
		super.runRoundRobin("PRE" + getBaseString() + "GTEQ:" + s);
		super.runRoundRobin("PRE" + getBaseString() + "LT:" + s);
		super.runRoundRobin("PRE" + getBaseString() + "LTEQ:" + s);
		super.runRoundRobin("PRE" + getBaseString() + "NEQ:" + s);
		super.runRoundRobin("PRE" + getBaseString() + "EQ:" + s);
		if (isBaseAllowed())
		{
			//super.runRoundRobin("PRE" + getBaseString() + ":" + s);
		}
	}

}
