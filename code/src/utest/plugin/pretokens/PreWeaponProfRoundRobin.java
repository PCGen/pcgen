package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreWeaponProfParser;
import plugin.pretokens.writer.PreWeaponProfWriter;

public class PreWeaponProfRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreWeaponProfRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreWeaponProfRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreWeaponProfParser());
		TokenRegistration.register(new PreWeaponProfWriter());
	}

	@Override
	public String getBaseString()
	{
		return "WEAPONPROF";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	public void testDeityWeapon()
	{
		this.runRoundRobin("PREWEAPONPROF:1,DEITYWEAPON");
	}
}
