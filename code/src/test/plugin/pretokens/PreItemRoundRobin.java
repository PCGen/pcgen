package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreItemParser;
import plugin.pretokens.writer.PreItemWriter;

public class PreItemRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreItemRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreItemRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreItemParser());
		TokenRegistration.register(new PreItemWriter());
	}

	@Override
	public String getBaseString()
	{
		return "ITEM";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

}
