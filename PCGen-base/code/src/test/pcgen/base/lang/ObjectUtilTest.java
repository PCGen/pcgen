/*
 * Copyright (c) 2015 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.lang;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

/**
 * Test the ObjectUtil class
 */
public class ObjectUtilTest
{

	@Test
	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(ObjectUtil.class);
	}

	@Test
	public void testNulls()
	{
		assertTrue(ObjectUtil.identityEquals(null).test(null));
	}

	@Test
	public void testNullObject()
	{
		assertFalse(ObjectUtil.identityEquals(null).test(new Object()));
	}

	@Test
	public void testObjectNull()
	{
		assertFalse(ObjectUtil.identityEquals(new Object()).test(null));
	}

	@Test
	public void testDifferent()
	{
		assertFalse(ObjectUtil.identityEquals(new Object()).test(new Object()));
	}

	@Test
	public void testSameInstance()
	{
		Object obj = new Object();
		assertTrue(ObjectUtil.identityEquals(obj).test(obj));
	}

	@Test
	public void testEqualsMethodIsIdentity()
	{
		assertFalse(ObjectUtil.identityEquals(new Integer(1)).test(new Integer(1)));
	}

}
