/*
 * Copyright 2012 (C) Vincent Lhote
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.enumeration;

import pcgen.system.LanguageBundle;

/**
 * Represents the handedness available in PCGen.
 * 
 * It is designed to hold Handedness in a type-safe fashion, so that they can be
 * quickly compared and use less memory when identical Handedness exist in two
 * CDOMObjects.
 */
public enum Handed
{
	Right
	{
		@Override
		public String toString()
		{
			return LanguageBundle.getString("in_handRight");
		}
	},

	Left
	{
		@Override
		public String toString()
		{
			return LanguageBundle.getString("in_handLeft");
		}
	},

	Ambidextrous
	{
		@Override
		public String toString()
		{
			return LanguageBundle.getString("in_handBoth");
		}
	},

	None
	{
		@Override
		public String toString()
		{
			return LanguageBundle.getString("in_comboNone");
		}
	},

	Other
	{
		@Override
		public String toString()
		{
			return LanguageBundle.getString("in_comboOther");
		}
	}
}
