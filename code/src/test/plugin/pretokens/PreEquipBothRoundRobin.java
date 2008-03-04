package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreEquipBothParser;
import plugin.pretokens.writer.PreEquipBothWriter;

public class PreEquipBothRoundRobin extends AbstractEquipmentRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreEquipBothRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreEquipBothRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreEquipBothParser());
		TokenRegistration.register(new PreEquipBothWriter());
	}

	@Override
	public String getBaseString()
	{
		return "EQUIPBOTH";
	}
}
