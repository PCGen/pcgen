package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreCSkillParser;
import plugin.pretokens.writer.PreCSkillWriter;

public class PreCSkillRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreCSkillRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCSkillRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreCSkillParser());
		TokenRegistration.register(new PreCSkillWriter());
	}

	@Override
	public String getBaseString()
	{
		return "CSKILL";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

}
