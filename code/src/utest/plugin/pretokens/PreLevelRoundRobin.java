package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreLevelWriter;

public class PreLevelRoundRobin extends AbstractMinMaxRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreLevelRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreLevelRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreLevelWriter());
	}

	@Override
	public String getBaseString()
	{
		return "LEVEL";
	}

}
