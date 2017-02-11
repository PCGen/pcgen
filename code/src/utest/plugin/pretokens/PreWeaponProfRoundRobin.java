/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
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
		AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString() + ":1,Foo,[TYPE=Bar]",
				"PREMULT:1,[PRE" + getBaseString() + ":1,Foo],[!PRE"
						+ getBaseString() + ":1,TYPE=Bar]");
	}

}
