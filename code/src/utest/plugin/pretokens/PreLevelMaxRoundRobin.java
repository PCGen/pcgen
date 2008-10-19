package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreLevelMaxParser;
import plugin.pretokens.writer.PreLevelMaxWriter;

public class PreLevelMaxRoundRobin extends AbstractIntegerRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreLevelMaxRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreLevelMaxRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreLevelMaxParser());
		TokenRegistration.register(new PreLevelMaxWriter());
	}

	@Override
	public String getBaseString()
	{
		return "LEVELMAX";
	}

}
