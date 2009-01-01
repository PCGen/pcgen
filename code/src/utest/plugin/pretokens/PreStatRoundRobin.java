package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreStatParser;
import plugin.pretokens.writer.PreStatWriter;

public class PreStatRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreStatRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreStatRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreStatParser());
		TokenRegistration.register(new PreStatWriter());
	}

	@Override
	public String getBaseString()
	{
		return "STAT";
	}

	@Override
	public boolean isAnyAllowed()
	{
		return true;
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}
}
