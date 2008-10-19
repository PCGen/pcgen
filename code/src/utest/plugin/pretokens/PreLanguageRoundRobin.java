package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreLanguageParser;
import plugin.pretokens.writer.PreLanguageWriter;

public class PreLanguageRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreLanguageRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreLanguageRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreLanguageParser());
		TokenRegistration.register(new PreLanguageWriter());
	}

	@Override
	public String getBaseString()
	{
		return "LANG";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

}
