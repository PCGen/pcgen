/*
 * JListModel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on October 8, 2002, 5:01 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <code>JListModel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class JListModel extends AbstractListModel
{
	private List listData = null;
	private boolean sort = false;

	JListModel(List listdata, boolean argSort)
	{
		initModel(listdata, argSort);
	}

	/**
	 * Set data on the list
	 * @param argListData
	 */
	public void setData(List argListData)
	{
		listData = (List) ((ArrayList) argListData).clone();

		if (sort)
		{
			Collections.sort(listData);
		}

		fireIntervalAdded(this, 0, listData.size());
	}

	@Override
	public Object getElementAt(int i)
	{
		if ((listData != null) && (i < listData.size()))
		{
			return listData.get(i);
		}

		return null;
	}

	@Override
	public int getSize()
	{
		if (listData != null)
		{
			return listData.size();
		}

		return 0;
	}

	Object[] getElements()
	{
		return listData.toArray();
	}

	void setSort(boolean argSort)
	{
		sort = argSort;
	}

//	void addElements(List l, final boolean allowDup)
//	{
//		for (Iterator e = l.iterator(); e.hasNext();)
//		{
//			Object obj = e.next();
//			if (allowDup || !listData.contains(obj))
//			{
//				listData.add(obj);
//			}
//		}
//		if (sort)
//		{
//			Collections.sort(listData);
//		}
//		fireIntervalAdded(this, 0, listData.size());
//	}
	void addElement(Object obj, final boolean allowDup)
	{
		if (allowDup || !listData.contains(obj))
		{
			addElement(obj);
		}
	}

	void addElement(Object obj)
	{
		listData.add(obj);

		if (sort)
		{
			Collections.sort(listData);
		}

		fireIntervalAdded(this, 0, listData.size());
	}

	void addElement(int index, Object obj)
	{
		listData.add(index, obj);

		if (sort)
		{
			Collections.sort(listData);
		}

		fireIntervalAdded(this, 0, listData.size());
	}

	boolean removeElement(Object obj)
	{
		final int idx = listData.indexOf(obj);

		if (idx >= 0)
		{
			listData.remove(idx);
			fireIntervalRemoved(this, idx, idx);
		}

		return idx >= 0;
	}

	private void initModel(List argListData, boolean argSort)
	{
		sort = argSort;
		setData(argListData);
	}
}
