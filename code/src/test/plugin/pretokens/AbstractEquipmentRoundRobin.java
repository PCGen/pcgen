package plugin.pretokens;

public abstract class AbstractEquipmentRoundRobin extends
		AbstractBasicRoundRobin
{

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	public void testWield()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,WIELDCATEGORY=Light");
	}

	public void testPattern()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Foo%");
	}

	public void testMultipleWield()
	{
		runRoundRobin("PRE" + getBaseString()
				+ ":1,WIELDCATEGORY=Light,WIELDCATEGORY=Medium");
	}

	public void testComplexWield()
	{
		runRoundRobin("PRE" + getBaseString()
				+ ":3,Foo,TYPE=Foo,WIELDCATEGORY=Light");
	}

}
