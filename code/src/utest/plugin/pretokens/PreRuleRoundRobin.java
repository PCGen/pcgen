package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreRuleParser;
import plugin.pretokens.writer.PreRuleWriter;

public class PreRuleRoundRobin extends AbstractBasicRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreRuleRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRuleRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreRuleParser());
		TokenRegistration.register(new PreRuleWriter());
	}

	@Override
	public String getBaseString()
	{
		return "RULE";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

}
