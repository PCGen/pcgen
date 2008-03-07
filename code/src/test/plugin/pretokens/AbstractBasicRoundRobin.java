package plugin.pretokens;

public abstract class AbstractBasicRoundRobin extends AbstractPreRoundRobin
{

	public abstract String getBaseString();

	public abstract boolean isTypeAllowed();

	public void testBasic()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Foo");
	}

	public void testMultiple()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Spot,Listen");
	}

	public void testNoCombineSub()
	{
		runRoundRobin("PREMULT:1,[PRE" + getBaseString() + ":1,Foo,Bar],[PRE"
				+ getBaseString() + ":2,Spot,Listen]");
	}

	public void testNoCombineSubNegative()
	{
		runRoundRobin("PREMULT:1,[!PRE" + getBaseString() + ":1,Foo],[!PRE"
				+ getBaseString() + ":1,Spot]");
	}

	public void testCombineSub()
	{
		runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString()
				+ ":1,Foo],[!PRE" + getBaseString() + ":1,Spot]", "!PRE"
				+ getBaseString() + ":1,Foo,Spot");
	}

	public void testCombineSubNegative()
	{
		runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString()
				+ ":1,Foo],[!PRE" + getBaseString() + ":1,Spot]", "PRE"
				+ getBaseString() + ":1,Foo,Spot");
	}

	public void testNoCombineMult()
	{
		runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":1,Foo,Bar],[PRE"
				+ getBaseString() + ":1,Spot,Listen]");
	}

	public void testMultipleCount()
	{
		runRoundRobin("PRE" + getBaseString() + ":2,Foo,Bar");
	}

	public void testType()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1,TYPE=Foo");
		}
	}

	public void testTypeMultipleCount()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":2,TYPE=Foo");
		}
	}

	public void testMultipleType()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1,TYPE=Bar,TYPE=Foo");
		}
	}

	public void testTypeAnd()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1,TYPE=Foo.Bar");
		}
	}

	public void testComplex()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":3,Foo,TYPE=Bar");
		}
	}
}
