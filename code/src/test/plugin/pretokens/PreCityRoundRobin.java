package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreCityParser;
import plugin.pretokens.writer.PreCityWriter;

public class PreCityRoundRobin extends AbstractStringRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreCityRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCityRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreCityParser());
		TokenRegistration.register(new PreCityWriter());
	}

	@Override
	public String getBaseString()
	{
		return "CITY";
	}

}
