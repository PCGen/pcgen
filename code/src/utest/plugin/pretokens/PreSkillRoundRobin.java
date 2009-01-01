package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreSkillParser;
import plugin.pretokens.writer.PreSkillWriter;

public class PreSkillRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreSkillRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSkillRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSkillParser());
		TokenRegistration.register(new PreSkillWriter());
	}

	@Override
	public String getBaseString()
	{
		return "SKILL";
	}

	@Override
	public boolean isAnyAllowed()
	{
		return true;
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}
}
