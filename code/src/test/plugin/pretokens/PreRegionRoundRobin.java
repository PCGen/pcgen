package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreRegionParser;
import plugin.pretokens.writer.PreRegionWriter;

public class PreRegionRoundRobin extends AbstractStringRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreRegionRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRegionRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreRegionParser());
		TokenRegistration.register(new PreRegionWriter());
	}

	@Override
	public String getBaseString()
	{
		return "REGION";
	}

}
