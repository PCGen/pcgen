package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.writer.PreClassWriter;

public class PreClassRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreClassRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreClassRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreClassParser());
		TokenRegistration.register(new PreClassWriter());
	}

	@Override
	public String getBaseString()
	{
		return "CLASS";
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

	public void testSpellcaster()
	{
		runRoundRobin("PRECLASS:1,SPELLCASTER=2");
	}

	public void testSpellcasterTyped()
	{
		runRoundRobin("PRECLASS:1,SPELLCASTER.Arcane=2");
	}
}
