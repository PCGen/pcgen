package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreTemplateParser;
import plugin.pretokens.writer.PreTemplateWriter;

public class PreTemplateRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreTemplateRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreTemplateRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreTemplateParser());
		TokenRegistration.register(new PreTemplateWriter());
	}

	@Override
	public String getBaseString()
	{
		return "TEMPLATE";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

}
