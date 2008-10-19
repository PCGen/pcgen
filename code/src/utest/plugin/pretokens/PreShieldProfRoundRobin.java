package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreShieldProfParser;
import plugin.pretokens.writer.PreShieldProfWriter;

public class PreShieldProfRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreShieldProfRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreShieldProfRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreShieldProfParser());
		TokenRegistration.register(new PreShieldProfWriter());
	}

	@Override
	public String getBaseString()
	{
		return "SHIELDPROF";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}
}
