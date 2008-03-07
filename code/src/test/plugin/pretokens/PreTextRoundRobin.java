package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreTextParser;
import plugin.pretokens.writer.PreTextWriter;

public class PreTextRoundRobin extends AbstractStringRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreTextRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreTextRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreTextParser());
		TokenRegistration.register(new PreTextWriter());
	}

	@Override
	public String getBaseString()
	{
		return "TEXT";
	}

}
