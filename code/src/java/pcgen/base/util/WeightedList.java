/*
 * WeightedList.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.base.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import pcgen.core.Globals;

/**
 * An implementation of the <tt>List</tt> interface that allows objects added
 * to the list to have a &quot;weight&quot; associated with them.  This weight
 * acts as though <i>weight</i> copies of the item were added to the list.  The
 * <code>size()</code> method returns the total weight of all items in the list.
 * The <code>get()</code> method returns the &quot;weighth&quot; element in the
 * list.
 * <p>As an example, if three items are added to the list
 * <ul>
 * <li>Item 1, weight 3</li>
 * <li>Item 2, weight 2</li>
 * <li>Item 3, weight 1</li>
 * </ul>
 * The list will have a total weight of 3+2+1=6.  The call <code>get(4)</code>
 * will return Item 2.<p>
 * 
 * @author boomer70
 * @param <E> 
 * @see	    java.util.List
 *
 */
public class WeightedList<E> implements List<E>
{
	/** The actual list where the data is stored. */
	protected List<WeightedItem<E>> theData;

	/**
	 * Default constructor.  Creates an empty list.
	 */
	public WeightedList()
	{
		theData = new ArrayList<WeightedItem<E>>();
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param   initialSize   the initial capacity of the list.
	 * 
	 * @exception IllegalArgumentException if the specified initial capacity
	 *            is negative
	 */
	public WeightedList(final int initialSize)
	{
		theData = new ArrayList<WeightedItem<E>>(initialSize);
	}

	/**
	 * Copy constructor.  Creates a new list that is a copy of the list passed
	 * in.
	 * @param wl List to copy.
	 */
	public WeightedList(final WeightedList<E> wl)
	{
		if (wl == null)
		{
			theData = new ArrayList<WeightedItem<E>>();
		}
		else
		{
			theData = new ArrayList<WeightedItem<E>>(wl.theData.size());
			for (final WeightedItem<E> item : wl.theData)
			{
				theData.add(new WeightedItem<E>(item.getElement(), item
					.getWeight()));
			}
		}
	}

	/**
	 * Creates a <tt>WeightedList</tt> from the <tt>Collection</tt> provided.
	 * All the elements added will have the default weight of 1.
	 * 
	 * @param c The <tt>Collection</tt> to copy.
	 */
	public WeightedList(final Collection<? extends E> c)
	{
		theData = new ArrayList<WeightedItem<E>>(c.size());
		for (final E element : c)
		{
			add(element);
		}
	}

	/**
	 * Returns the total weight of the list.  This is the sum of the weights
	 * of all the items in the list.
	 * 
	 * @return The total weight.
	 */
	public int size()
	{
		return totalWeight();
	}

	/**
	 * Adds all the elements from the specified <tt>Collection</tt> to this list
	 * with the default weight of 1.  The elements are added in the order
	 * returned by the <tt>Collection</tt>'s <tt>Iterator</tt>.
	 * 
	 * @param c The <tt>Collection</tt> to add the elements from.
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(final Collection<? extends E> c)
	{
		final int initSize = theData.size();
		for (final E element : c)
		{
			add(element);
		}
		return theData.size() != initSize;
	}

	/**
	 * Adds an element to the list with the specified weight.  If the element
	 * is already present in the list the weight is added to the existing
	 * element instead.
	 * 
	 * @param weight Weight to add this element with.
	 * @param element Element to add.
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(final int weight, final E element)
	{
		// Lets see if we can find this element
		for (final WeightedItem<E> wi : theData)
		{
			if (wi.getElement().equals(element))
			{
				wi.addWeight(weight);
				return;
			}
		}
		theData.add(new WeightedItem<E>(element, weight));
	}

	/**
	 * Adds the specified element with the default weight.
	 * 
	 * @param element The element to add
	 * @return true if the element was added.
	 * 
	 * @see WeightedList#add(int, Object)
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(final E element)
	{
		add(1, element);
		return true;
	}

	/**
	 * Returns the element at the relative (accounting for weight) index 
	 * specified.
	 * 
	 * @param index The relative index.
	 * @return The element at the relative index.
	 * 
	 * @see java.util.List#get(int)
	 */
	public E get(final int index)
	{
		int total = 0;
		for (int i = 0; i < theData.size(); i++)
		{
			WeightedItem<E> wi = theData.get(i);
			total += wi.getWeight();
			if (total > index)
			{
				return wi.getElement();
			}
		}
		return null;
	}

	/**
	 * Returns a random selection from the list based on weight.
	 * 
	 * @return The random element selected.
	 */
	public E getRandomValue()
	{
		return get(Globals.getRandomInt(size()));
	}

	/**
	 * Returns an <tt>Iterator</tt> that iterates over the elements in the list
	 * ignoring weight.  Therefore in a list with three elements of differing
	 * weights, this iterator simply returns each element in turn.
	 * 
	 * @return An <tt>Iterator</tt> for the list.
	 * 
	 * @see java.util.List#iterator()
	 */
	public Iterator<E> iterator()
	{
		return new Itr();
	}

	/**
	 * Checks if the object specified exists in this list.
	 * 
	 * @param o The object to test for
	 * @return <tt>true</tt> if the object is in the list.
	 * 
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(final Object o)
	{
		for (final E element : this)
		{
			if (element instanceof List)
			{
				if (((List) element).contains(o))
				{
					return true;
				}
			}
			else
			{
				if (element.equals(o))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes the object from the list if it is present.
	 * 
	 * @param o The element to remove
	 * @return <tt>true</tt> if the element was removed.
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(final Object o)
	{
		for (Iterator<WeightedItem<E>> i = theData.iterator(); i.hasNext();)
		{
			final WeightedItem<E> wi = i.next();
			if (wi.getElement().equals(o))
			{
				i.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if this list has any elements.
	 * 
	 * @return <tt>true</tt> if the list contains no elements.
	 * 
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty()
	{
		return theData.isEmpty();
	}

	/**
	 * Returns an array representing the elements in this list.  The array is
	 * returned without regard to weight.
	 * 
	 * @return A new array containing the elements in the list.
	 * 
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray()
	{
		final Object[] ret = new Object[theData.size()];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = theData.get(i).getElement();
		}
		return ret;
	}

	/**
	 * Returns an Array of the same type passed in containing all the elements
	 * in the list.  The Array passed in is modified by this call.
	 * @param <T> 
	 * 
	 * @param a The Array to fill with elements
	 * @return The Array containing all the elements.
	 * 
	 * @see java.util.List#toArray(Object[]) 
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a)
	{
		if (a.length < theData.size())
		{
			// Allocate a new array
			a =
					(T[]) Array.newInstance(a.getClass().getComponentType(),
						theData.size());
		}
		for (int i = 0; i < a.length; i++)
		{
			a[i] = (T) theData.get(i).getElement();
		}
		if (a.length > theData.size())
		{
			a[theData.size()] = null;
		}
		return a;
	}

	/**
	 * Returns <tt>true</tt> if this list contains all the elements in the 
	 * specified collection.
	 * 
	 * @param c The collection to test against
	 * @return <tt>true</tt> if the list contains all the elements in the
	 * specified collection.
	 * 
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(final Collection<?> c)
	{
		return theData.containsAll(c);
	}

	/**
	 * Removes from the list all the elements that are contained in the 
	 * specified collection.
	 * 
	 * @param c elements to be removed from this list
	 * @return <tt>true</tt> if the list is changed by the result of this call.
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(final Collection<?> c)
	{
		return theData.removeAll(c);
	}

	/**
	 * Retains only the elements that are contained in the specified collection.
	 * In other words, remove all the elements that are not in the specified
	 * collection.
	 * 
	 * @param c elements to retain in the list
	 * @return <tt>true</tt> if the list is changed by this call.
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(final Collection<?> c)
	{
		return theData.retainAll(c);
	}

	/**
	 * Removes all the elements from the list.
	 * @see java.util.List#clear()
	 */
	public void clear()
	{
		theData.clear();
	}

	/**
	 * Compares the specified object with this list for equality.  Returns
	 * <tt>true</tt> if and only if the specified object is also a list, both
	 * lists have the same size, and all corresponding pairs of elements in
	 * the two lists are <i>equal</i>.  (Two elements <tt>e1</tt> and
	 * <tt>e2</tt> are <i>equal</i> if <tt>(e1==null ? e2==null :
	 * e1.equals(e2))</tt>.)  In other words, two lists are defined to be
	 * equal if they contain the same elements in the same order.<p>
	 * 
	 * @param o The object to be compared for equality with this list.
	 * @return <tt>true</tt> if the specified object is equal to this list.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o)
	{
		return theData.equals(o);
	}

	/**
	 * Returns the hash code value for this list. <p>
	 * 
	 * @return the hash code value for this list.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return theData.hashCode();
	}

	/**
	 * Returns a string representation of the list.
	 * 
	 * @return A string representation of the values in the list.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString()
	{
		final StringBuffer buf = new StringBuffer();
		buf.append("[");

		final Iterator<WeightedItem<E>> i = theData.iterator();
		boolean hasNext = i.hasNext();
		while (hasNext)
		{
			final WeightedItem<E> o = i.next();
			final E data = o.getElement();
			buf.append(data == this ? "(this list)" : String.valueOf(data)); //TODO Is this use of object identity comparison intentional? JK070115
			buf.append(" (").append(o.getWeight()).append(")");
			hasNext = i.hasNext();
			if (hasNext)
				buf.append(", ");
		}

		buf.append("]");
		return buf.toString();
	}

	/**
	 * Adds each element in the specified collection with the indicated weight
	 * value.
	 * 
	 * @param aWeight The weight value to use for each element added.
	 * @param c The elements to add to the list
	 * @return <tt>true</tt> if the list is changed by this call.
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(final int aWeight, final Collection<? extends E> c)
	{
		final int initSize = theData.size();
		for (Iterator<? extends E> i = c.iterator(); i.hasNext();)
		{
			add(aWeight, i.next());
		}
		return theData.size() != initSize;
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element. <p>
	 *
	 * This implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @param index index of element to replace.
	 * @param element element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * 
	 * @throws UnsupportedOperationException if the <tt>set</tt> method is not
	 *		  supported by this List.
	 *
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public E set(@SuppressWarnings("unused")
	final int index, @SuppressWarnings("unused")
	final E element)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes the element at the absolute index specified.<p>
	 * This method does not account for the weight of the elements in the list,
	 * so that if you want to remove the second element in the list you would 
	 * call <code>remove(1)</code> regardless of the weight of that or the
	 * previous element.<p>
	 * The method returns the element that was removed or <tt>null</tt> if the
	 * element could not be removed.
	 * 
	 * @param index The absolute index of the element to remove.
	 * @return The element removed or <tt>null</tt>.
	 * 
	 * @see java.util.List#remove(int)
	 */
	public E remove(final int index)
	{
		final WeightedItem<E> element = theData.remove(index);
		if (element != null)
		{
			return element.getElement();
		}
		return null;
	}

	/**
	 * Returns the absolute index of the element specfied or -1 if the element
	 * is not present in the list.<p>
	 * The implementation does not account for the weight of the elements.
	 * 
	 * @param o The element to find
	 * @return The absolute index or -1.
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(final Object o)
	{
		return theData.indexOf(o);
	}

	/**
	 * Returns the last absolute index of the element specified or -1 if the 
	 * element is not present in the list.<p>
	 * Because duplicate elements are not allowed in this list this method
	 * returns the same result as <code>indexOf(java.lang.Object)</code>.
	 * 
	 * @param o The element to find.
	 * @return The absolute index or -1
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 * @see pcgen.base.util.WeightedList#indexOf(java.lang.Object)
	 */
	public int lastIndexOf(final Object o)
	{
		return theData.lastIndexOf(o);
	}

	/**
	 * Returns a <tt>ListIterator</tt> implementation for this list.<p>
	 * The <tt>Iterator</tt> returned iterates over the elements ignoring the
	 * weights of the elements.
	 * 
	 * @return The <tt>ListIterator</tt>.
	 * 
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator()
	{
		return new ListItr();
	}

	/**
	 * Returns a list iterator starting at the specified absolute index.<p>
	 * 
	 * The <tt>Iterator</tt> returned iterates over the elements ignoring the
	 * weights of the elements.
	 * 
	 * @return The <tt>ListIterator</tt>.
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(final int index)
	{
		return new ListItr(index);
	}

	/**
	 * Returns a portion of the list as a new list.  The indexes specified are
	 * absolute indexes.
	 * 
	 * @param fromIndex The start element count to begin the list at
	 * @param toIndex The last element count to create the list for
	 * @return A new List backed by this list representing the elements between
	 * <tt>fromIndex</tt> and <tt>toIndex</tt>.
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList(final int fromIndex, final int toIndex)
	{

		return new WeightedSubList<E>(this, fromIndex, toIndex);
	}

	private int totalWeight()
	{
		int total = 0;
		for (int i = 0; i < theData.size(); i++)
		{
			WeightedItem<E> wi = theData.get(i);
			total += wi.getWeight();
		}
		return total;
	}

	/**
	 * This class is a simple wrapper to associate an object from a
	 * <tt>WeightedList</tt> and its weight.
	 * 
	 * @author boomer70
	 * 
	 * @param <T> 
	 */
	class WeightedItem<T>
	{
		private T theElement;
		private int theWeight;

		/**
		 * This constructor creates a new <tt>WeightedItem</tt> with the 
		 * specified weight.
		 * 
		 * @param element The object this Item represents.
		 * @param weight The weight of the item within the list.
		 */
		public WeightedItem(final T element, final int weight)
		{
			theElement = element;
			theWeight = weight;
		}

		/**
		 * Gets the wrapped object.
		 * 
		 * @return The object this item wraps
		 */
		public final T getElement()
		{
			return theElement;
		}

		/**
		 * Gets the weight of this object.
		 * 
		 * @return The weight of this item
		 */
		public final int getWeight()
		{
			return theWeight;
		}

		/**
		 * Adds the specified amount of weight to the item.
		 * 
		 * @param aWeight an amount of weight to add.
		 */
		public void addWeight(final int aWeight)
		{
			theWeight += aWeight;
		}

		/**
		 * Returns <tt>true</tt> if the objects this item wraps compare equal 
		 * using the objects <code>equal</code> method.
		 * \n TODO Surely it's not intentional that this class doesn't override hashCode? 
		 * @param o Object to compare.
		 * @return <tt>true</tt> if the element this item contains equals the
		 * object passed in.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object o)
		{
			return theElement.equals(o);
		}
		
		/**
		 * Must be consistent with equals
		 */
		@Override
		public int hashCode() {
			return theElement.hashCode();
		}

		/**
		 * Returns a String representation of this object.<p>
		 * 
		 * The implementation returns <code>toString()</code> of the contained
		 * element.
		 * 
		 * @return A string representation of class.
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return theElement.toString();
		}
	}

	private class Itr implements Iterator<E>
	{
		/** An iterator that iterates over the raw data elements. */
		Iterator<WeightedItem<E>> realIterator = theData.iterator();

		/**
		 * Checks if there are any more elements in the iteration.
		 * 
		 * @return <tt>true</tt> if there are more elements.
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return realIterator.hasNext();
		}

		/**
		 * Returns the next element in the iteration.
		 * 
		 * @return The next element.
		 * 
		 * @see java.util.Iterator#next()
		 */
		public E next()
		{
			WeightedItem<E> next = realIterator.next();

			return next.getElement();
		}

		/**
		 * Removes from the list the last element returned from the iteration.
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			realIterator.remove();
		}
	}

	private class ListItr implements ListIterator<E>
	{
		/** An Iterator that iterates over the raw data elements */
		ListIterator<WeightedItem<E>> realIterator = null;

		/**
		 * Constructs a new <tt>ListIterator</tt> from the underlying list.
		 *
		 */
		public ListItr()
		{
			realIterator = theData.listIterator();
		}

		/**
		 * Constructs a new <tt>ListIterator</tt> starting at the specified 
		 * position.
		 * 
		 * @param index The position to start iterating from.
		 */
		public ListItr(int index)
		{
			realIterator = theData.listIterator(index);
		}

		/**
		 * Checks if there are any more elements in the iteration.
		 * 
		 * @return <tt>true</tt> if there are more elements.
		 * 
		 * @see java.util.ListIterator#hasNext()
		 */
		public boolean hasNext()
		{
			return realIterator.hasNext();
		}

		/**
		 * Returns the next element in the iteration.
		 * 
		 * @return The next element.
		 * 
		 * @see java.util.ListIterator#next()
		 */
		public E next()
		{
			WeightedItem<E> next = realIterator.next();

			return next.getElement();
		}

		/**
		 * Returns <tt>true</tt> if the iterator has more elements when
		 * traversing in reverse direction.
		 * 
		 * @return <tt>true</tt> if there if a call to <tt>previous()</tt> will
		 * succeed.
		 * 
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious()
		{
			return realIterator.hasPrevious();
		}

		/**
		 * Returns the previous element in the iteration.
		 * 
		 * @return The previous element.
		 * 
		 * @see java.util.ListIterator#previous()
		 */
		public E previous()
		{
			WeightedItem<E> next = realIterator.previous();

			return next.getElement();
		}

		/**
		 * Returns the integer index of the next element in the iteration.
		 * 
		 * @return The index of the next element.
		 * 
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex()
		{
			return realIterator.nextIndex();
		}

		/**
		 * Returns the integer index of the previous element in the iteration.
		 * 
		 * @return The index of the previous element.
		 * 
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex()
		{
			return realIterator.previousIndex();
		}

		/**
		 * Removes from the list the last element returned from the iteration.
		 * 
		 * @see java.util.ListIterator#remove()
		 */
		public void remove()
		{
			realIterator.remove();
		}

		/**
		 * Replaces the last element returned by <tt>next</tt> or
		 * <tt>previous</tt> with the specified element (optional operation).
		 * This call can be made only if neither <tt>ListIterator.remove</tt> nor
		 * <tt>ListIterator.add</tt> have been called after the last call to
		 * <tt>next</tt> or <tt>previous</tt>.
		 *
		 * @param o the element with which to replace the last element returned by
		 *          <tt>next</tt> or <tt>previous</tt>.
		 *          
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(final E o)
		{
			WeightedItem<E> wi = new WeightedItem<E>(o, 1);
			realIterator.set(wi);
		}

		/**
		 * Inserts the specified element into the list (optional operation).  The
		 * element is inserted immediately before the next element that would be
		 * returned by <tt>next</tt>, if any, and after the next element that
		 * would be returned by <tt>previous</tt>, if any.  (If the list contains
		 * no elements, the new element becomes the sole element on the list.)
		 * The new element is inserted before the implicit cursor: a subsequent
		 * call to <tt>next</tt> would be unaffected, and a subsequent call to
		 * <tt>previous</tt> would return the new element.  (This call increases
		 * by one the value that would be returned by a call to <tt>nextIndex</tt>
		 * or <tt>previousIndex</tt>.)
		 *
		 * @param o the element to insert.
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add(final E o)
		{
			WeightedItem<E> wi = new WeightedItem<E>(o, 1);
			realIterator.add(wi);
		}
	}
}

/**
 * A class that implements the SubList functionality for a WeightedList.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 *
 * @see WeightedList#subList(int, int)
 * 
 * @param <E>
 */
class WeightedSubList<E> extends WeightedList<E>
{
	/**
	 * Constructs a new List that is a view of a portion of the List passed in.
	 * 
	 * @param list The backing List
	 * @param fromIndex The starting index of the new list
	 * @param toIndex The ending index of the new list.
	 * 
	 * @throws IndexOutOfBoundsException if either index is outside the range
	 * of indexes in the backing list.
	 * @throws IllegalArgumentException if the fromIndex is greater than toIndex
	 */
	@SuppressWarnings("nls")
	WeightedSubList(final WeightedList<E> list, final int fromIndex,
		final int toIndex)
	{
		if (fromIndex < 0)
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		if (toIndex > list.size())
			throw new IndexOutOfBoundsException("toIndex = " + toIndex);
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex
				+ ") > toIndex(" + toIndex + ")");
		theData = list.theData.subList(fromIndex, toIndex);
	}
}
