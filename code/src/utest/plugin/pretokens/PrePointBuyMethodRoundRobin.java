package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PrePointBuyMethodParser;
import plugin.pretokens.writer.PrePointBuyMethodWriter;

public class PrePointBuyMethodRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PrePointBuyMethodRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PrePointBuyMethodRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PrePointBuyMethodParser());
		TokenRegistration.register(new PrePointBuyMethodWriter());
	}

	@Override
	public String getBaseString()
	{
		return "POINTBUYMETHOD";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

}
