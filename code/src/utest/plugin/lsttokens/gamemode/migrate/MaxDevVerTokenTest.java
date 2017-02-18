/*
 * MaxDevVerTokenTest.java
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package plugin.lsttokens.gamemode.migrate;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;

/**
 * MaxDevVerTokenTest checks the function of the MaxDevVerToken class.
 * 
 */
public class MaxDevVerTokenTest
{
	private MigrationRule migrationRule;
	private MaxDevVerToken token;
	private String gameModeName;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		migrationRule = new MigrationRule(ObjectType.SOURCE, "OldKey");
		token = new MaxDevVerToken();
		gameModeName = "Pathfinder";
	}

	/**
	 * Test method for {@link plugin.lsttokens.gamemode.migrate.MaxVerToken#parse(pcgen.core.system.MigrationRule, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testParseValidVer()
	{
		assertTrue("Parse should have been successful", token.parse(migrationRule, "6.01.03", gameModeName));
		assertEquals("MaxDevVer", "6.01.03", migrationRule.getMaxDevVer());
	}
	
	@Test
	public void testParseValidVerNumbers()
	{
		String[] goodVersions = new String[] {"5.17.12", "6.0.0", "6.0.1 RC2", "6.0.1-RC2", "6.01.02", "6.01.02-dev"};
		for (String verString : goodVersions)
		{
			assertTrue("Valid version " + verString
				+ " should have been accepted", token.parse(migrationRule, verString, gameModeName));
			assertEquals("MaxDevVer", verString, migrationRule.getMaxDevVer());
		}
	}

	@Test
	public void testParseInvalidVerEmpty()
	{
		assertFalse("Empty version should not have been accepted", token.parse(migrationRule, "", gameModeName));
		assertNull("MaxDevVer", migrationRule.getMaxDevVer());
	}

	@Test
	public void testParseInvalidVerFormat()
	{
		String[] badVersions = new String[] {"text", "a.b.c", "6.1", "6_0_1", "6.0.1d", "3.rc2", "6.0.1RC2"};
		for (String verString : badVersions)
		{
			assertFalse("Invalid version " + verString
				+ " should not have been accepted",
				token.parse(migrationRule, verString, gameModeName));
			assertNull("MaxDevVer", migrationRule.getMaxDevVer());
		}
	}

}
