package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreTypeParser;
import plugin.pretokens.writer.PreTypeWriter;

public class PreTypeRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreTypeRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreTypeRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreTypeParser());
		TokenRegistration.register(new PreTypeWriter());
	}

	@Override
	public String getBaseString()
	{
		return "TYPE";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}
}
