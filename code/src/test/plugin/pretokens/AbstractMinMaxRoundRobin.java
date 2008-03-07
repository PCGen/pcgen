package plugin.pretokens;

public abstract class AbstractMinMaxRoundRobin extends
		AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public void testMin()
	{
		runRoundRobin("PRE" + getBaseString() + ":MIN=1");
	}

	public void testMax()
	{
		runRoundRobin("PRE" + getBaseString() + ":MAX=3");
	}

	public void testBoth()
	{
		runRoundRobin("PRE" + getBaseString() + ":MIN=1,MAX=4");
	}
}
