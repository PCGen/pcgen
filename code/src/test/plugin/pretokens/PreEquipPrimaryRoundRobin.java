package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreEquipPrimaryParser;
import plugin.pretokens.writer.PreEquipPrimaryWriter;

public class PreEquipPrimaryRoundRobin extends AbstractEquipmentRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreEquipPrimaryRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreEquipPrimaryRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreEquipPrimaryParser());
		TokenRegistration.register(new PreEquipPrimaryWriter());
	}

	@Override
	public String getBaseString()
	{
		return "EQUIPPRIMARY";
	}
}
