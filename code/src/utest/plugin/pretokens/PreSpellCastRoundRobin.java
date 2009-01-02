package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreSpellCastParser;
import plugin.pretokens.writer.PreSpellCastMemorizeWriter;
import plugin.pretokens.writer.PreSpellCastWriter;

public class PreSpellCastRoundRobin extends AbstractPreRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreSpellCastRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSpellCastRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSpellCastParser());
		TokenRegistration.register(new PreSpellCastWriter());
		TokenRegistration.register(new PreSpellCastMemorizeWriter());
	}

	public void testYes()
	{
		runRoundRobin("PRESPELLCAST:MEMORIZE=Y");
	}

	public void testNo()
	{
		runRoundRobin("PRESPELLCAST:MEMORIZE=N");
	}

	public void testType()
	{
		runRoundRobin("PRESPELLCAST:TYPE=TypeText");
	}
}
