package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreSkillTotalParser;
import plugin.pretokens.writer.PreSkillWriter;

public class PreSkillTotRoundRobin extends AbstractPreRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreSkillTotRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSkillTotRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSkillTotalParser());
		TokenRegistration.register(new PreSkillWriter());
	}

	public void testBasic()
	{
		runRoundRobin("PRESKILLTOT:Hide,Seek=20");
	}
}
