/*
 * JComboBoxEx.java
 * Copyright 2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
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
 */
package pcgen.gui2.util;

import pcgen.util.StringIgnoreCaseComparator;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 * Sorted {@code JComboBox}.
 *
 */
public class JComboBoxEx extends JComboBox
{
	/**
	 * The {@code Comparator}.  The default is
	 * {@code StringIgnoreCaseComparator} (since combo boxes
	 * display string items to the user).
	 */
	private Comparator<Object> comparator = new StringIgnoreCaseComparator();

	/**
	 * Should we sort anytime the items are changed?
	 */
	private boolean autoSort = false;

	/**
	 * Creates a {@code JComboBoxEx} with a default data
	 * model.
	 */
	public JComboBoxEx()
	{
		super();
	}

	/**
	 * Creates a {@code JComboBoxEx} that takes it's items
	 * from an existing {@code ComboBoxModel}.
	 *
	 * @param aModel the {@code ComboBoxModel} that provides
	 * the displayed list of items
	 */
	public JComboBoxEx(ComboBoxModel aModel)
	{
		super(aModel);
	}

	/**
	 * Creates a {@code JComboBoxEx} that contains the
	 * elements in the specified array. By default the first item
	 * in the array (and therefore the data model) becomes
	 * selected.
	 *
	 * @param items an array of objects to insert into the combo
	 * box
	 */
	public JComboBoxEx(Object[] items)
	{
		super(items);
	}

	/**
	 * Creates a {@code JComboBoxEx} that contains the
	 * elements in the specified {@code Vector}. By default
	 * the first item in the vector and therefore the data model)
	 * becomes selected.
	 *
	 * @param items an array of vectors to insert into the combo
	 * box
	 */
	public JComboBoxEx(Vector<?> items)
	{
		super(items);
	}

	/**
	 * Sets all the items.
	 *
	 * @param items an array of objects to insert into the combo
	 * box
	 */
	public void setAllItems(Object[] items)
	{
		// setModel(getModel().getClass().getDeclaredConstructor(new Class[] {Object[].class}).newInstance(new Object[] {items}));
		removeAllItems();

		for (int i = 0; i < items.length; ++i)
		{
			super.addItem(items[i]);
		}
	}

	/**
	 * Gets all the items.
	 *
	 * @return an array of objects in the combo box
	 */
	public Object[] getAllItems()
	{
		int count = getItemCount();
		Object[] items = new Object[count];

		for (int i = 0; i < count; ++i)
		{
			items[i] = getItemAt(i);
		}

		return items;
	}

	/**
	 * Set {@code true} if the combo box automatically should
	 * sort when items change.  If {@code false}, then
	 * require a call to {@link #sortItems()} to sort items.  This
	 * is an optimization choice.  The default is
	 * {@code false}.
	 *
	 * <strong>This only affects combo box methods.</strong> If
	 * you modify what an item returns for {@code toString()}
	 * by manipulating the item, you need to call
	 * {@code sortItems()} manually.
	 *
	 * @param autoSort automatically sort when items change?
	 */
	public void setAutoSort(boolean autoSort)
	{
		this.autoSort = autoSort;
	}

	/**
	 * Returns {@code true} if the combo box automatically
	 * sorts when items change.  If {@code false}, then call
	 * {@link #sortItems()} to sort items.  This is an
	 * optimization choice.  The default is {@code false}.
	 *
	 * <strong>This only affects combo box methods.</strong> If
	 * you modify what an item returns for {@code toString()}
	 * by manipulating the item, you need to call
	 * {@code sortItems()} manually.
	 *
	 * @return {@code true} if the combo box automatically
	 * sorts when items change.
	 */
	public boolean getAutoSort()
	{
		return autoSort;
	}

	/**
	 * Sets the {@code Comparator} used to sort items.  The
	 * default is {@code StringIgnoreCaseComparator} (since
	 * combo boxes display string items to the user).
	 *
	 * @param comparator the {@code Comparator} used to sort
	 * items
	 */
	public void setComparator(Comparator<Object> comparator)
	{
		this.comparator = comparator;
	}

	/**
	 * Returns the {@code Comparator} used to sort items.
	 * The default is {@code StringIgnoreCaseComparator}
	 * (since combo boxes display string items to the user).
	 *
	 * @return the {@code Comparator} used to sort items
	 */
	public Comparator<Object> getComparator()
	{
		return comparator;
	}

	@Override
	public void addItem(Object item)
	{
		super.addItem(item);

		if (autoSort)
		{
			sortItems();
		}
	}

	/**
	 * Sorts the combo box items using the comparator for this
	 * combo box.
	 *
	 * @see #setComparator(Comparator)
	 */
	public void sortItems()
	{
		sortItems(comparator);
	}

	/**
	 * Sorts the combo box items using <var>comparator</var>.
	 *
	 * @param aComparator the {@code Comparator} used to sort
	 * items
	 */
	public void sortItems(Comparator<Object> aComparator)
	{
		// Keep the same item selected after sorting
		Object selected = getSelectedItem();
		Object[] items = getAllItems();

		Arrays.sort(items, aComparator);
		setAllItems(items);
		setSelectedItem(selected);
	}
}
