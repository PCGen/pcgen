package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreSpellBookParser;
import plugin.pretokens.writer.PreSpellBookWriter;

public class PreSpellBookRoundRobin extends AbstractPreRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreSpellBookRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSpellBookRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSpellBookParser());
		TokenRegistration.register(new PreSpellBookWriter());
	}

	public void testYes()
	{
		runRoundRobin("PRESPELLBOOK:YES");
	}

	public void testNo()
	{
		runRoundRobin("PRESPELLBOOK:NO");
	}

}
