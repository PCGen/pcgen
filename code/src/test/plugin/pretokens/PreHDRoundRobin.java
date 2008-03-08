package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreHDParser;
import plugin.pretokens.writer.PreHDWriter;

public class PreHDRoundRobin extends AbstractMinMaxRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreHDRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreHDRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreHDParser());
		TokenRegistration.register(new PreHDWriter());
	}

	@Override
	public String getBaseString()
	{
		return "HD";
	}

}
