/*
 * SortedListModel.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Apr 25, 2010, 4:37:10 PM
 */
package pcgen.gui2.util;

import java.util.Arrays;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import org.apache.commons.lang.ArrayUtils;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SortedListModel<E> extends AbstractListModel implements ListListener<E>
{

	private ListFacade<E> model;
	private Comparator<? super E> comparator;
	private Comparator<Integer> indexComparator = new Comparator<Integer>()
	{

		@Override
		public int compare(Integer o1, Integer o2)
		{
			E e1 = model.getElementAt(o1);
			E e2 = model.getElementAt(o2);
			return comparator.compare(e1, e2);
		}

	};

	public SortedListModel(ListFacade<E> model, Comparator<? super E> comparator)
	{
		this.model = model;
		this.comparator = comparator;
		sortModel();
		model.addListListener(this);
	}

	private Integer[] transform = null;

	@Override
	public E getElementAt(int index)
	{
		if (index < 0 || index >= transform.length)
		{
			return null;
		}
		return model.getElementAt(transform[index]);
	}

	@Override
	public int getSize()
	{
		return model.getSize();
	}

	private void sortModel()
	{
		transform = new Integer[model.getSize()];
		for (int i = 0; i < transform.length; i++)
		{
			transform[i] = i;
		}
		Arrays.sort(transform, indexComparator);
	}

	@Override
	public void elementAdded(ListEvent<E> e)
	{
		transform = (Integer[]) ArrayUtils.add(transform, transform.length);
		Arrays.sort(transform, indexComparator);
		int index = Arrays.binarySearch(transform, e.getIndex(), indexComparator);
		fireIntervalAdded(this, index, index);
	}

	@Override
	public void elementRemoved(ListEvent<E> e)
	{
		int index = ArrayUtils.indexOf(transform, e.getIndex());
		transform = (Integer[]) ArrayUtils.removeElement(transform, transform.length-1);
		Arrays.sort(transform, indexComparator);
		fireIntervalRemoved(this, index, index);
	}

	@Override
	public void elementsChanged(ListEvent<E> e)
	{
		sortModel();
		fireContentsChanged(this, 0, transform.length - 1);
	}

}
