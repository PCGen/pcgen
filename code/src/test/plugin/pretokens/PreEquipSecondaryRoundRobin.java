package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreEquipSecondaryParser;
import plugin.pretokens.writer.PreEquipSecondaryWriter;

public class PreEquipSecondaryRoundRobin extends AbstractEquipmentRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreEquipSecondaryRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreEquipSecondaryRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreEquipSecondaryParser());
		TokenRegistration.register(new PreEquipSecondaryWriter());
	}

	@Override
	public String getBaseString()
	{
		return "EQUIPSECONDARY";
	}
}
