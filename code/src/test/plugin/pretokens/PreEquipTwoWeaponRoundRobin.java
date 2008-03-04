package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreEquipTwoWeaponParser;
import plugin.pretokens.writer.PreEquipTwoWeaponWriter;

public class PreEquipTwoWeaponRoundRobin extends AbstractEquipmentRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreEquipTwoWeaponRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreEquipTwoWeaponRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreEquipTwoWeaponParser());
		TokenRegistration.register(new PreEquipTwoWeaponWriter());
	}

	@Override
	public String getBaseString()
	{
		return "EQUIPTWOWEAPON";
	}
}
