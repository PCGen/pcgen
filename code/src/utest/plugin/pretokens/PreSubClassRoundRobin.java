package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreSubClassParser;
import plugin.pretokens.writer.PreSubClassWriter;

public class PreSubClassRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreSubClassRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSubClassRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSubClassParser());
		TokenRegistration.register(new PreSubClassWriter());
	}

	@Override
	public String getBaseString()
	{
		return "SUBCLASS";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

}
