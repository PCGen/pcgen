package plugin.pretokens;

public abstract class AbstractIntegerRoundRobin extends
		AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public void testBasic()
	{
		runRoundRobin("PRE" + getBaseString() + ":1");
	}

	public void testHigher()
	{
		runRoundRobin("PRE" + getBaseString() + ":3");
	}

}
