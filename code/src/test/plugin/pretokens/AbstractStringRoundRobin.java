package plugin.pretokens;

public abstract class AbstractStringRoundRobin extends
		AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public void testBasic()
	{
		runRoundRobin("PRE" + getBaseString() + ":Hello");
	}

	public void testHigher()
	{
		runRoundRobin("PRE" + getBaseString() + ":Goodbye");
	}

}
