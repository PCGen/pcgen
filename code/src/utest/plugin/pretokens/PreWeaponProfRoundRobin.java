package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
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
		this.runRoundRobin("PRE" + getBaseString() + ":1,DEITYWEAPON");
	}

	public void testNegateItem()
	{
		this.runSimpleRoundRobin("PRE" + getBaseString() + ":1,Foo,[TYPE=Bar]",
				"PREMULT:1,[PRE" + getBaseString() + ":1,Foo],[!PRE"
						+ getBaseString() + ":1,TYPE=Bar]");
	}

}
