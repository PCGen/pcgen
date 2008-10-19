package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreMoveParser;
import plugin.pretokens.writer.PreMoveWriter;

public class PreMoveRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreMoveRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreMoveRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreMoveParser());
		TokenRegistration.register(new PreMoveWriter());
	}

	@Override
	public String getBaseString()
	{
		return "MOVE";
	}

	@Override
	public boolean isAnyAllowed()
	{
		return false;
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}
}
