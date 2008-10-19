package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PrePCLevelParser;
import plugin.pretokens.writer.PrePCLevelWriter;

public class PrePCLevelRoundRobin extends AbstractMinMaxRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PrePCLevelRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PrePCLevelRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PrePCLevelParser());
		TokenRegistration.register(new PrePCLevelWriter());
	}

	@Override
	public String getBaseString()
	{
		return "PCLEVEL";
	}

}
