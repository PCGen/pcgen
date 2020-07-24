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
package pcgen.base.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.MessageFormat;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

/**
 * Test the MessageFormatUtilities class
 */
public class MessageFormatUtilitiesTest
{

	@Test
	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(MessageFormatUtilities.class);
	}

	@Test
	public void testNulls()
	{
		MessageFormat mf = new MessageFormat("Hello");
		assertEquals(0, MessageFormatUtilities.getRequriedArgumentCount(mf));
		mf.applyPattern("Hello {0}");
		assertEquals(1, MessageFormatUtilities.getRequriedArgumentCount(mf));
		mf.applyPattern("Hello {3}");
		assertEquals(4, MessageFormatUtilities.getRequriedArgumentCount(mf));
		mf.applyPattern("Hello {2} {1}, {0,number}");
		assertEquals(3, MessageFormatUtilities.getRequriedArgumentCount(mf));
		mf.applyPattern("Hello {0,choice,0#{1}|0<{2}}");
		assertEquals(3, MessageFormatUtilities.getRequriedArgumentCount(mf));
	}

}
