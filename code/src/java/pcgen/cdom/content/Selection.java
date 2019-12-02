/*
 * Copyright (c) Thomas Parker, 2012.
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
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.analysis.ChooseActivation;

public class Selection<BT extends CDOMObject, SEL>
{

	private final BT base;
	private final SEL selection;

	public Selection(BT obj, SEL sel)
	{
		Objects.requireNonNull(obj, "Object cannot be null");
		if (ChooseActivation.hasNewChooseToken(obj))
		{
			if (sel == null)
			{
				throw new IllegalArgumentException("Selection cannot be null in " + obj.getClass().getSimpleName() + " "
					+ obj.getKeyName() + " with CHOOSE");
			}
		}
		else
		{
			if (sel != null)
			{
				throw new IllegalArgumentException("Selection must be null in " + obj.getClass().getSimpleName() + " "
					+ obj.getKeyName() + " without CHOOSE");
			}
		}
		base = obj;
		selection = sel;
	}

	public BT getObject()
	{
		return base;
	}

	public SEL getSelection()
	{
		return selection;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Selection)
		{
			Selection<?, ?> other = (Selection<?, ?>) obj;
			boolean selectionEqual =
                    Objects.equals(selection, other.selection);
			return selectionEqual && base.equals(other.base);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return base.hashCode() ^ (selection == null ? 0 : selection.hashCode());
	}

}
