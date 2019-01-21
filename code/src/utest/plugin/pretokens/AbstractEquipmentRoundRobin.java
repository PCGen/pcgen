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

import org.junit.jupiter.api.Test;

public abstract class AbstractEquipmentRoundRobin extends
		AbstractBasicRoundRobin
{

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	@Test
	public void testWield()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,WIELDCATEGORY=Light");
	}

	@Test
	public void testPattern()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,Foo%");
	}

	@Test
	public void testMultipleWield()
	{
		runRoundRobin("PRE" + getBaseString()
				+ ":1,WIELDCATEGORY=Light,WIELDCATEGORY=Medium");
	}

	@Test
	public void testComplexWield()
	{
		runRoundRobin("PRE" + getBaseString()
				+ ":3,Foo,TYPE=Foo,WIELDCATEGORY=Light");
	}

}
