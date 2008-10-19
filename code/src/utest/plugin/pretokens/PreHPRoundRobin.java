package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreHPParser;
import plugin.pretokens.writer.PreHPWriter;

public class PreHPRoundRobin extends AbstractIntegerRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreHPRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreHPRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreHPParser());
		TokenRegistration.register(new PreHPWriter());
	}

	@Override
	public String getBaseString()
	{
		return "HP";
	}

}
