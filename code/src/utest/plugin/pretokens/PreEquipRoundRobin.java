package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreEquipParser;
import plugin.pretokens.writer.PreEquipWriter;

public class PreEquipRoundRobin extends AbstractEquipmentRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreEquipRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreEquipRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreEquipParser());
		TokenRegistration.register(new PreEquipWriter());
	}

	@Override
	public String getBaseString()
	{
		return "EQUIP";
	}
}
