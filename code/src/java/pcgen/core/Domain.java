/*
 * Domain.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>Domain</code>.
 *
 * @author   Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Domain extends PObject
{
	@Override
	public String getSpellKey(PlayerCharacter pc)
	{
		return "DOMAIN|" + keyName;
	}

	@Override
	public Domain clone()
	{
		Domain aObj = null;

		try
		{
			aObj                = (Domain) super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(
				exc.getMessage(),
				Constants.s_APPNAME,
				MessageType.ERROR);
		}

		return aObj;
	}

	/**
	 * (non-Javadoc)
	 * Only compares the name.
	 * @param obj
	 * @return TRUE if equals, else FALSE
	 * @see Object#equals
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj != null)
		{
			if (obj.getClass() == this.getClass())
			{
				return ((Domain) obj).getKeyName().equals(this.getKeyName());
			}
		}

		return false;
	}

	/**
	 * Only uses the name for hashCode.
	 *
	 * @return a hashcode for this Domain object
	 */
	@Override
	public int hashCode()
	{
		final int result;
		result = ((getKeyName() != null) ? getKeyName().hashCode() : 0);

		return result;
	}
}
