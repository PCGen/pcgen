/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.formatmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.testsupport.TestSupport;

/**
 * Test the FormatUtilites class
 */
public class FormatUtilitiesTest
{

	@Test
	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(FormatUtilities.class);
	}

	@Test
	public void testIsValid()
	{
		NumberManager numberManager = new NumberManager();
		assertEquals(numberManager, FormatUtilities.ifValid(numberManager));
		assertThrows(NullPointerException.class, () -> FormatUtilities.ifValid(new BadManagerNoIdentifierType()));
		assertThrows(NullPointerException.class, () -> FormatUtilities.ifValid(new BadManagerNoManagedClass()));
	}

	private class BadManagerNoIdentifierType implements FormatManager<String>
	{

		@Override
		public String convert(String inputStr)
		{
			return inputStr;
		}

		@Override
		public Indirect<String> convertIndirect(String inputStr)
		{
			return null;
		}

		@Override
		public boolean isDirect()
		{
			return true;
		}

		@Override
		public String unconvert(String obj)
		{
			return obj;
		}

		@Override
		public Class<String> getManagedClass()
		{
			return String.class;
		}

		@Override
		public String getIdentifierType()
		{
			return null;
		}

		@Override
		public Optional<FormatManager<?>> getComponentManager()
		{
			return Optional.empty();
		}

	}

	private class BadManagerNoManagedClass implements FormatManager<String>
	{

		@Override
		public String convert(String inputStr)
		{
			return inputStr;
		}

		@Override
		public Indirect<String> convertIndirect(String inputStr)
		{
			return null;
		}

		@Override
		public boolean isDirect()
		{
			return true;
		}

		@Override
		public String unconvert(String obj)
		{
			return obj;
		}

		@Override
		public Class<String> getManagedClass()
		{
			return null;
		}

		@Override
		public String getIdentifierType()
		{
			return "String";
		}

		@Override
		public Optional<FormatManager<?>> getComponentManager()
		{
			return Optional.empty();
		}

	}
}
