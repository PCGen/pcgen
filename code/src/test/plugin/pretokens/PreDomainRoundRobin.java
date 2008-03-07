package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreDomainParser;
import plugin.pretokens.writer.PreDomainWriter;

public class PreDomainRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreDomainRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDomainRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreDomainParser());
		TokenRegistration.register(new PreDomainWriter());
	}

	@Override
	public String getBaseString()
	{
		return "DOMAIN";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

	public void testAny()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Any");
	}
}
