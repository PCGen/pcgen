/*
 * Copyright 2007 (c) Tom Parker <thpr@users.sourceforge.net>
 *  Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
package pcgen.base.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

/**
 * An implementation of the <tt>Collection</tt> interface that allows objects
 * added to the Collection to have a &quot;weight&quot; associated with them.
 * This weight acts as though <i>weight</i> copies of the item were added to
 * the Collection. The {@code size()} method returns the total weight of
 * all items in the Collection. The {@code get()} method returns the
 * &quot;weight&quot; element in the Collection.
 * <p>
 * As an example, if three items are added to the Collection
 * <ul>
 * <li>Item 1, weight 3</li>
 * <li>Item 2, weight 2</li>
 * <li>Item 3, weight 1</li>
 * </ul>
 * The Collection will have a total weight of 3+2+1=6. The call
 * {@code get(4)} will return Item 2.
 * <p>
 *
 * @param <E> The Class stored in the WeightedCollection
 */
public class WeightedCollection<E> extends AbstractCollection<E>
{

    /**
     * The actual list where the data is stored.
     */
    private final Collection<WeightedItem<E>> theData;

    /**
     * Default constructor. Creates an empty collection.
     */
    public WeightedCollection()
    {
        theData = new ListSet<>();
    }

    /**
     * Constructs an empty collection with the specified initial capacity.
     *
     * @param initialSize the initial capacity of the collection.
     * @throws IllegalArgumentException if the specified initial capacity is negative
     */
    public WeightedCollection(int initialSize)
    {
        theData = new ListSet<>(initialSize);
    }

    /**
     * Creates a <tt>WeightedCollection</tt> from the <tt>Collection</tt>
     * provided. All the elements added will have the default weight equal to
     * the number of times they appear in the given collection.
     * <p>
     * This constructor is both reference-semantic and value-semantic. It will
     * not modify or maintain a reference to the given Collection of objects.
     * However, references to the objects contained in the Collection are
     * maintained by the WeightedCollection, and the WeightedCollection may
     * return references to those objects contained in the Collection.
     *
     * @param collection The <tt>Collection</tt> to copy.
     * @throws NullPointerException if the given Collection is null
     */
    public WeightedCollection(Collection<? extends E> collection)
    {
        this();
        addAll(collection, 1);
    }

    /**
     * Constructs an empty WeightedCollection with the given Comparator used to
     * establish equality and order in the WeightedCollection.
     *
     * @param comp The Comparator this Set will use to determine equality and
     *             order of the WeightedCollection
     */
    public WeightedCollection(Comparator<? super E> comp)
    {
        if (comp == null)
        {
            theData = new ListSet<>();
        } else
        {
            theData = new TreeSet<>(new WeightedItemComparator<>(comp));
        }
    }

    /**
     * Returns the total weight of the WeightedCollection. This is the sum of
     * the weights of all the items in the WeightedCollection.
     *
     * @return The total weight.
     */
    @Override
    public int size()
    {
        return theData.stream()
                .mapToInt(WeightedItem::getWeight)
                .sum();
    }

    /**
     * Adds all the elements from the specified <tt>Collection</tt> to this
     * WeightedCollection with the default weight of 1.
     * <p>
     * This method is both reference-semantic and value-semantic. It will not
     * modify or maintain a reference to the given Collection of objects.
     * However, references to the objects contained in the Collection are
     * maintained by the WeightedCollection, and the WeightedCollection may
     * return references to those objects contained in the Collection.
     *
     * @param collection The <tt>Collection</tt> to add the elements from.
     * @throws NullPointerException if the given Collection is null
     */
    @Override
    public boolean addAll(Collection<? extends E> collection)
    {
        return addAll(collection, 1);
    }

    /**
     * Adds an element to the WeightedCollection with the specified weight. If
     * the element is already present in the WeightedCollection the weight is
     * added to the existing element instead. Note that this is means
     * WeightedCollection does not guarantee order of the collection.
     *
     * @param weight  Weight to add this element with.
     * @param element Element to add.
     * @return true if we added successfully
     * @throws IllegalArgumentException if the given weight is less than zero
     */
    public final boolean add(E element, int weight)
    {
        if (weight < 0)
        {
            throw new IllegalArgumentException("Cannot items with weight < 0");
        } else if (weight == 0)
        {
            return false;
        }
        // Lets see if we can find this element
        for (WeightedItem<E> item : theData)
        {
            E wiElement = item.getElement();
            if (Objects.equals(wiElement, element))
            {
                item.addWeight(weight);
                return true;
            }
        }
        return theData.add(new WeightedItem<>(element, weight));
    }

    /**
     * Adds the specified element with the default weight.
     *
     * @param element The element to add
     * @return true if the element was added.
     * @see WeightedCollection#add(Object, int)
     */
    @Override
    public boolean add(E element)
    {
        return add(element, 1);
    }

    /**
     * Returns a random selection from the WeightedCollection based on weight.
     *
     * @return The random element selected.
     */
    public E getRandomValue()
    {
        int index = RandomUtil.getRandomInt(size());
        int total = 0;
        for (WeightedItem<E> item : theData)
        {
            total += item.getWeight();
            if (total > index)
            {
                /*
                 * NOTE The return statement can't be 100% covered with a Sun
                 * compiler for code coverage stats.
                 */
                return item.getElement();
            }
        }
        /*
         * This can occur if the list is empty.
         */
        throw new IndexOutOfBoundsException(index + " >= " + total);
    }

    /**
     * Returns an <tt>Iterator</tt> that iterates over the elements in the
     * WeightedCollection. This Iterator <i>accounts for the weight of the
     * elements in the WeightedCollection</i>.
     * <p>
     * This method is reference-semantic. While ownership of the Iterator is
     * transferred to the calling object (no reference to the iterator is
     * maintained by the WeightedCollection), actions on the returned Iterator
     * (e.g. remove()) can alter the WeightedCollection on which this method was
     * called.
     *
     * @return An <tt>Iterator</tt> for the WeightedCollection.
     */
    @NotNull
    @Override
    public Iterator<E> iterator()
    {
        return new WeightedIterator();
    }

    /**
     * Returns an <tt>Iterator</tt> that iterates over the elements in the
     * WeightedCollection. This Iterator <i>does NOT account for the weight of
     * the elements in the WeightedCollection</i>. Therefore in a list with
     * three elements of differing weights, this iterator simply returns each
     * element in turn.
     * <p>
     * This method is reference-semantic. While ownership of the Iterator is
     * transferred to the calling object (no reference to the iterator is
     * maintained by the WeightedCollection), actions on the returned Iterator
     * (e.g. remove()) can alter the WeightedCollection on which this method was
     * called.
     *
     * @return An <tt>Iterator</tt> for the WeightedCollection.
     */
    Iterator<E> unweightedIterator()
    {
        return new UnweightedIterator();
    }

    /**
     * Checks if the object specified exists in this WeightedCollection.
     *
     * @param element The object to test for
     * @return <tt>true</tt> if the object is in the WeightedCollection.
     */
    @Override
    public boolean contains(Object element)
    {
        return theData.stream()
                .map(WeightedItem::getElement)
                .anyMatch(Predicate.isEqual(element));
    }

    /**
     * Returns the weight for the given object in this WeightedCollection. If
     * the given object is not in this collection, zero is returned.
     *
     * @param element The object for which the weight in this WeightedCollection
     *                will be returned.
     * @return the weight of the given object in this WeightedCollection, or
     * zero if the object is not in this WeightedCollection
     */
    public int getWeight(Object element)
    {
        return theData.stream()
                .filter(item -> Objects.equals(item.getElement(), element))
                .mapToInt(WeightedItem::getWeight)
                .findFirst()
                .orElse(0);
    }

    /**
     * Removes the object from the WeightedCollection if it is present. This
     * removes the object from this WeightedCollection regardless of the weight
     * of the object in this WeightedCollection. Therefore, if an object was
     * weight 2 in this WeightedCollection and is removed, the size of this
     * WeightedCollection will decrease by two, and NO copies of the given
     * object will remain in this WeightedCollection.
     *
     * @param element The element to remove
     * @return <tt>true</tt> if the element was removed.
     */
    @Override
    public boolean remove(Object element)
    {
        for (Iterator<WeightedItem<E>> it = theData.iterator();it.hasNext();)
        {
            WeightedItem<E> item = it.next();
            E wiElement = item.getElement();
            if (Objects.equals(wiElement, element))
            {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Tests if this WeightedCollection has any elements.
     *
     * @return <tt>true</tt> if the WeightedCollection contains no elements.
     */
    @Override
    public boolean isEmpty()
    {
        return theData.isEmpty();
    }

    /**
     * Removes all the elements from the WeightedCollection.
     */
    @Override
    public void clear()
    {
        theData.clear();
    }

    /**
     * Compares the specified object with this WeightedCollection for equality.
     * Returns <tt>true</tt> if and only if the specified object is also a
     * WeightedCollection, both WeightedCollections have the same size, and all
     * corresponding pairs of elements in the two WeightedCollections are
     * <i>equal</i>. (Two elements <tt>e1</tt> and <tt>e2</tt> are <i>equal</i>
     * if <tt>(e1==null ? e2==null :
     * e1.equals(e2))</tt>.) In other words,
     * two WeightedCollections are defined to be equal if they contain the same
     * elements in the same order.
     * <p>
     *
     * @param obj The object to be compared for equality with this
     *            WeightedCollection.
     * @return <tt>true</tt> if the specified object is equal to this
     * WeightedCollection.
     */
    @Override
    public boolean equals(Object obj)
    {
        /*
         * CONSIDER Currently, this is ORDER SENSITIVE, which is probably bad
         * for a collection? This needs to be seriously thought through to
         * determine how exactly this should work... especially given that there
         * is no solution for sorting a WeightedCollection and thus it is not
         * possible to actually sort before doing the comparison. - thpr 2/5/07
         */
        return obj instanceof WeightedCollection && theData.equals(((WeightedCollection<?>) obj).theData);
    }

    @Override
    public int hashCode()
    {
        return theData.hashCode();
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "WeightedCollection: " + theData;
    }

    /**
     * Adds each element in the specified collection with the indicated weight
     * value.
     * <p>
     * This method is both reference-semantic and value-semantic. It will not
     * modify or maintain a reference to the given Collection of objects.
     * However, references to the objects contained in the Collection are
     * maintained by the WeightedCollection, and the WeightedCollection may
     * return references to those objects contained in the Collection.
     *
     * @param weight     The weight value to use for each element added.
     * @param collection The elements to add to the WeightedCollection
     * @return <tt>true</tt> if the WeightedCollection is changed by this
     * call.
     * @throws NullPointerException if the given Collection is null
     */
    public final boolean addAll(Collection<? extends E> collection, int weight)
    {
        boolean modified = false;
        for (E element : collection)
        {
            modified |= add(element, weight);
        }
        return modified;
    }

    /**
     * This class is a simple wrapper to associate an object from a
     * <tt>WeightedList</tt> and its weight.
     *
     * @param <T>
     */
    static final class WeightedItem<T>
    {
        private final T theElement;

        private int theWeight;

        /**
         * This constructor creates a new <tt>WeightedItem</tt> with the
         * specified weight.
         *
         * @param element The object this Item represents.
         * @param weight  The weight of the item within the list.
         */
        WeightedItem(T element, int weight)
        {
            theElement = element;
            theWeight = weight;
        }

        /**
         * Gets the wrapped object.
         *
         * @return The object this item wraps
         */
        public T getElement()
        {
            return theElement;
        }

        /**
         * Gets the weight of this object.
         *
         * @return The weight of this item
         */
        public int getWeight()
        {
            return theWeight;
        }

        /**
         * Adds the specified amount of weight to the item.
         *
         * @param weight an amount of weight to add.
         */
        void addWeight(int weight)
        {
            theWeight += weight;
        }

        @Override
        public int hashCode()
        {
            return theWeight * 29 + (theElement == null ? 0 : theElement.hashCode());
        }

        /**
         * Equals method. Note this is required in order to have the .equals()
         * at the WeightedCollection level work properly (it is a deep equals)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof WeightedItem)
            {
                WeightedItem<?> item = (WeightedItem<?>) obj;
                return theWeight == item.theWeight && (theElement == null && item.theElement == null
                        || theElement != null && theElement.equals(item.theElement));
            }
            //Arguably unreachable code
            return false;
        }

        @Override
        public String toString()
        {
            return theElement + " (" + theWeight + ')';
        }
    }

    /**
     * A weighted Iterator for a WeightedCollection
     */
    private class WeightedIterator implements Iterator<E>
    {

        private final Iterator<WeightedItem<E>> iter = theData.iterator();

        private WeightedItem<E> currentEntry;

        private int currentReturned = 0;

        @Override
        public boolean hasNext()
        {
            if (currentEntry == null)
            {
                if (!iter.hasNext())
                {
                    return false;
                }
                currentEntry = iter.next();
                currentReturned = 0;
            }
            if (currentReturned < currentEntry.theWeight)
            {
                return true;
            }
            return iter.hasNext();
        }

        @Override
        public E next()
        {
            if ((currentEntry == null) || (currentReturned >= currentEntry.getWeight()))
            {
                currentEntry = iter.next();
                currentReturned = 0;
            }
            currentReturned++;
            return currentEntry.theElement;
        }

        @Override
        public void remove()
        {
            iter.remove();
            currentEntry = null;
        }
    }

    /**
     * A Unweighted Iterator for the WeightedCollection
     */
    private class UnweightedIterator implements Iterator<E>
    {
        /**
         * An iterator that iterates over the raw data elements.
         */
        private final Iterator<WeightedItem<E>> realIterator = theData.iterator();

        /**
         * Checks if there are any more elements in the iteration.
         *
         * @return <tt>true</tt> if there are more elements.
         */
        @Override
        public boolean hasNext()
        {
            return realIterator.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return The next element.
         */
        @Override
        public E next()
        {
            return realIterator.next().getElement();
        }

        /**
         * Removes from the WeightedCollection the last element returned from
         * the iteration.
         */
        @Override
        public void remove()
        {
            realIterator.remove();
        }
    }

    /**
     * Implements a Comparator of WeightedItems. Takes in a Comparator to which
     * it will delegate comparison of the underlying objects.
     *
     * @param <WICT> The type of the object underlying the WeightedItem objects
     *               that this WeightedItemComparator can compare.
     */
    private static final class WeightedItemComparator<WICT> implements Comparator<WeightedItem<WICT>>
    {

        /**
         * The Comparator to which this WeightedItemComparator will delegate
         * comparison of the underlying objects.
         */
        private final Comparator<? super WICT> delegate;

        /**
         * Constructs a new WeightedItemComparator with the given Comparator as
         * the delegate given to provide comparison of the underlying objects.
         *
         * @param comp The delegate Comparator given to provide comparison of the
         *             objects underlying the WeightedItem objects compared by
         *             this WeightedItemComparator
         */
        WeightedItemComparator(Comparator<? super WICT> comp)
        {
            delegate = comp;
        }

        /**
         * Compare two WeightedItem objects
         */
        @Override
        public int compare(WeightedItem<WICT> item1, WeightedItem<WICT> item2)
        {
            return delegate.compare(item1.getElement(), item2.getElement());
        }

    }
}
