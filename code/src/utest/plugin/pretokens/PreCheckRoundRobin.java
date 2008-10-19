package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreCheckParser;
import plugin.pretokens.writer.PreCheckWriter;

public class PreCheckRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreCheckRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCheckRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreCheckParser());
		TokenRegistration.register(new PreCheckWriter());
	}

	@Override
	public String getBaseString()
	{
		return "CHECK";
	}

	@Override
	public boolean isAnyAllowed()
	{
		return false;
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}
}
