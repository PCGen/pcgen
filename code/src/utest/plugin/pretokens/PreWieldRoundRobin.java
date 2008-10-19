package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreWieldParser;
import plugin.pretokens.writer.PreWieldWriter;

public class PreWieldRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreWieldRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreWieldRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreWieldParser());
		TokenRegistration.register(new PreWieldWriter());
	}

	@Override
	public String getBaseString()
	{
		return "WIELD";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}
}
