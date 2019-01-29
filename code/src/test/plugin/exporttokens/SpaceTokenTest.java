/*
 * Copyright 2008 (C) PCGen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.display.CharacterDisplay;
import plugin.exporttokens.deprecated.SpaceToken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code SpaceTokenTest} - Unit test for the SPACE output token
 */
public class SpaceTokenTest extends AbstractCharacterTestCase
{
	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		// Do Nothing
	}

	@AfterEach
	@Override
	public void tearDown() throws Exception
	{
		// Do Nothing
	}

	/**
	 * Test the SPACE token.
	 */
	@Test
	public void testSpaceToken()
	{
		SpaceToken token = new SpaceToken();
		assertEquals(" ", token.getToken("SPACE", (CharacterDisplay) null, null));
	}

}
