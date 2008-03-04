package plugin.pretokens;

public abstract class AbstractRankedRoundRobin extends
		AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public abstract boolean isTypeAllowed();

	public abstract boolean isAnyAllowed();

	public void testBasic()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Foo=1");
	}

	public void testMultiple()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Foo=1,Bar=2");
	}

	public void testHigher()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Foo=3");
	}

	public void testBothMultiple()
	{
		runRoundRobin("PRE" + getBaseString() + ":2,Foo=3,Bar=5,Goo=6");
	}

	public void testType()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1,TYPE.Foo=3");
		}
	}

	public void testAny()
	{
		if (isAnyAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1,ANY=3");
		}
	}

	public void testMultipleType()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1,TYPE.Bar=3,TYPE.Foo=2");
		}
	}

	public void testTypeAnd()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1,TYPE.Foo.Bar=3");
		}
	}

	public void testComplex()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":3,Foo,TYPE.Bar=4");
		}
	}

}
