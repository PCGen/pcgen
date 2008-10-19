package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreArmorProfParser;
import plugin.pretokens.writer.PreArmorProfWriter;

public class PreArmorProfRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreArmorProfRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreArmorProfRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreArmorProfParser());
		TokenRegistration.register(new PreArmorProfWriter());
	}

	@Override
	public String getBaseString()
	{
		return "ARMORPROF";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

}
