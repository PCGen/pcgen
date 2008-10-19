package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreVisionParser;
import plugin.pretokens.writer.PreVisionWriter;

public class PreVisionRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreVisionRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreVisionRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreVisionParser());
		TokenRegistration.register(new PreVisionWriter());
	}

	@Override
	public String getBaseString()
	{
		return "VISION";
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
