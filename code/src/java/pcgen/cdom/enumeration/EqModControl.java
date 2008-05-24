/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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

public enum EqModControl
{

	YES() {
		@Override
		public boolean getModifiersAllowed()
		{
			return true;
		}

		@Override
		public boolean getModifiersRequired()
		{
			return false;
		}
	},

	NO() {
		@Override
		public boolean getModifiersAllowed()
		{
			return false;
		}

		@Override
		public boolean getModifiersRequired()
		{
			return false;
		}
	},

	REQUIRED() {
		@Override
		public boolean getModifiersAllowed()
		{
			return true;
		}

		@Override
		public boolean getModifiersRequired()
		{
			return true;
		}
	};

	public abstract boolean getModifiersAllowed();

	public abstract boolean getModifiersRequired();

}
