package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreDeityParser;
import plugin.pretokens.writer.PreDeityWriter;
import plugin.pretokens.writer.PreHasDeityWriter;

public class PreDeityRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreDeityRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDeityRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreDeityParser());
		TokenRegistration.register(new PreDeityWriter());
		TokenRegistration.register(new PreHasDeityWriter());
	}

	@Override
	public String getBaseString()
	{
		return "DEITY";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

	public void testY()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Y");
	}

	public void testN()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,N");
	}

	public void testPantheon()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,PANTHEON.Foo");
	}

	public void testMultiplePantheon()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,PANTHEON.Bar,PANTHEON.Foo");
	}

	public void testPantheonComplex()
	{
		runRoundRobin("PRE" + getBaseString() + ":3,Foo,PANTHEON.Bar");
	}

}
