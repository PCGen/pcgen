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

import pcgen.cdom.base.CDOMObject;

public class SourcedSelection<BT extends CDOMObject, SEL, SRC> extends
		Selection<BT, SEL>
{

	private final SRC source;

	public SourcedSelection(BT obj, SEL sel, SRC src)
	{
		super(obj, sel);
		if (src == null)
		{
			throw new IllegalArgumentException("Source cannot be null");
		}
		source = src;
	}

	public SRC getSource()
	{
		return source;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SourcedSelection)
		{
			SourcedSelection<?, ?, ?> other = (SourcedSelection<?, ?, ?>) obj;
			boolean selectionEqual =
					(getSelection() == other.getSelection())
						|| (getSelection() != null && getSelection().equals(
							other.getSelection()));
			return selectionEqual && getObject().equals(other.getObject())
				&& source.equals(other.source);
		}
		return false;
	}

	/*
	 * Hashcode from Selection is sufficient
	 */

	public static <BT extends CDOMObject, SEL, SRC> SourcedSelection<BT, SEL, SRC> getSelection(
		BT obj, SEL sel, SRC src)
	{
		return new SourcedSelection<BT, SEL, SRC>(obj, sel, src);
	}

}
