package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreGenderParser;
import plugin.pretokens.writer.PreGenderWriter;

public class PreGenderRoundRobin extends AbstractStringRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreGenderRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreGenderRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreGenderParser());
		TokenRegistration.register(new PreGenderWriter());
	}

	@Override
	public String getBaseString()
	{
		return "GENDER";
	}

}
