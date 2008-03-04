package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreAttackParser;
import plugin.pretokens.writer.PreAttackWriter;

public class PreAttackRoundRobin extends AbstractIntegerRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreAttackRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreAttackRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreAttackParser());
		TokenRegistration.register(new PreAttackWriter());
	}

	@Override
	public String getBaseString()
	{
		return "ATT";
	}

}
