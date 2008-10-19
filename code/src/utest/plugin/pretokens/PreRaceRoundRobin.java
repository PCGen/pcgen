package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class PreRaceRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreRaceRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRaceRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreRaceParser());
		TokenRegistration.register(new PreRaceWriter());
	}

	@Override
	public String getBaseString()
	{
		return "RACE";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	public void testRaceType()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,RACETYPE=Foo");
	}

	public void testRaceTypeCount()
	{
		runRoundRobin("PRE" + getBaseString() + ":2,RACETYPE=Foo");
	}

	public void testMultipleRaceType()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,RACETYPE=Bar,RACETYPE=Foo");
	}

	public void testRaceTypeComplex()
	{
		runRoundRobin("PRE" + getBaseString() + ":3,Foo,RACETYPE=.Bar");
	}

	public void testRaceSubType()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,RACESUBTYPE=Foo");
	}

	public void testRaceSubTypeCount()
	{
		runRoundRobin("PRE" + getBaseString() + ":2,RACESUBTYPE=Foo");
	}

	public void testMultipleRaceSubType()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,RACESUBTYPE=Bar,RACESUBTYPE=Foo");
	}

	public void testRaceSubTypeComplex()
	{
		runRoundRobin("PRE" + getBaseString() + ":3,Foo,RACESUBTYPE=Bar");
	}

}
