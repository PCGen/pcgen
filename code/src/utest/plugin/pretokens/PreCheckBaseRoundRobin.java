package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreCheckParser;
import plugin.pretokens.writer.PreCheckBaseWriter;

public class PreCheckBaseRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreCheckBaseRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCheckBaseRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreCheckParser());
		TokenRegistration.register(new PreCheckBaseWriter());
	}

	@Override
	public String getBaseString()
	{
		return "CHECKBASE";
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
