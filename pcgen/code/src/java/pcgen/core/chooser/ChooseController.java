/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */
package pcgen.core.chooser;

import java.util.List;

class ChooseController<T>
{
	public ChooseController()
	{
		// Nothing to build here
	}

	public int getPool()
	{
		return 1;
	}

	public boolean isMultYes()
	{
		return false;
	}

	public boolean isStackYes()
	{
		return false;
	}

	public double getCost()
	{
		return 1.0;
	}

	public int getTotalChoices()
	{
		return 1;
	}

	public void adjustPool(List<? extends T> selected)
	{
		// Ignore
	}
}
