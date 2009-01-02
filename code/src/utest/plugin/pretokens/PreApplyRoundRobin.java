package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreApplyParser;
import plugin.pretokens.writer.PreApplyWriter;

public class PreApplyRoundRobin extends AbstractPreRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreApplyRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreApplyRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreApplyParser());
		TokenRegistration.register(new PreApplyWriter());
	}

	public void testYes()
	{
		runRoundRobin("PREAPPLY:PC");
	}

	public void testNo()
	{
		runRoundRobin("PREAPPLY:ANYPC");
	}

	public void testSimple()
	{
		runRoundRobin("PREAPPLY:Ranged");
	}

	public void testAnd()
	{
		runRoundRobin("PREAPPLY:Ranged,Silver");
	}

	public void testOr()
	{
		runRoundRobin("PREAPPLY:Ranged;Melee");
	}
}
