/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An EnhancedWeakReference is a WeakReference enhanced to be used in Streams.
 * 
 * It is intended to be usable much like Optional, and thus also behaves in equality much
 * like Optional. Meaning, if the objects underlying the WeakReference are equal, then two
 * EnhancedWeakReference objects will be equal.
 * 
 * Inside an EnhancedWeakReference contains a WeakReference (recognizable by the Garbage
 * Collection system as such).
 * 
 * As a caution, similar to keys in HashMap, objects placed with an EnhancedWeakReference
 * are intended to behave in a static fashion with respect to hashCode. If they do not
 * (meaning their hash changes over the lifecycle of an object), then
 * EnhancedWeakReference may have unintended behavior.
 * 
 * Warning: Do NOT check isPresent() or isEmpty() and then call get(), assuming you will
 * not get a NoSuchElementException. Writing code as such will expose you to a timing
 * attack from the Garbage Collector (which just might pause your code in between those
 * calls). Rather use safeGet and check the returned Optional.
 *
 * @param <T>
 *            The format of object underlying the EnhancedWeakReference
 */
public class EnhancedWeakReference<T>
{

	/**
	 * The underlying WeakReference that this EnhancedWeakReference wraps
	 */
	private final WeakReference<T> underlyingReference;

	/**
	 * The hashCode for this EnhancedWeakReference. This is generated at construction,
	 * since the underlying object may be lost.
	 */
	private final int hash;

	/**
	 * Constructs a new EnhancedWeakReference with a WeakReference to the given underlying
	 * object
	 * 
	 * @param underlying
	 *            The object to be (weakly) contained within this EnhancedWeakReference
	 */
	public EnhancedWeakReference(T underlying)
	{
		hash = underlying.hashCode();
		underlyingReference =
				new WeakReference<>(Objects.requireNonNull(underlying));
	}

	/**
	 * Returns the underlying object of this EnhancedWeakReference.
	 * 
	 * @return The underlying object of this EnhancedWeakReference
	 * @throws NoSuchElementException
	 *             if the underlying object is no longer available
	 */
	public T get()
	{
		T underlying = underlyingReference.get();
		if (underlying == null)
		{
			throw new NoSuchElementException("No value present");
		}
		return underlying;
	}

	/**
	 * Returns an Optional possibly containing the underlying object of this
	 * EnhancedWeakReference. Optional.empty() will be returned if the underlying
	 * WeakReference returns null.
	 * 
	 * @return An Optional possibly containing the underlying object of this
	 *         EnhancedWeakReference
	 */
	public Optional<T> safeGet()
	{
		return Optional.ofNullable(underlyingReference.get());
	}

	/**
	 * Returns true if the underlying WeakReference still contains a reference to the
	 * object.
	 * 
	 * @return true if the underlying WeakReference still contains a reference to the
	 *         object; false otherwise
	 */
	public boolean isPresent()
	{
		return underlyingReference.get() != null;
	}

	/**
	 * Returns true if the underlying WeakReference does not contain a reference to the
	 * object (meaning it has been garbage collected)
	 * 
	 * @return true if the underlying WeakReference does not contain a reference to the
	 *         object (meaning it has been garbage collected); false otherwise
	 */
	public boolean isEmpty()
	{
		return underlyingReference.get() == null;
	}

	/**
	 * Runs the given Runnable and returns false if the underlying WeakReference is empty;
	 * otherwise returns true without running the Runnable.
	 * 
	 * This is designed to be used in a Stream. If this EnhancedWeakReference is empty,
	 * then the Runnable can be used to remove it from a parent list, etc. Beware of
	 * ConcurrentModificationException if you use this method - you may need to copy a
	 * Collection before calling .stream()
	 * 
	 * @param emptyAction
	 *            The action to be run if the underlying WeakReference is empty
	 * @return true if the underlying WeakReference is Present; false if Empty
	 */
	@SuppressWarnings("PMD.DoNotUseThreads")
	public boolean consumeIfEmpty(Runnable emptyAction)
	{
		T underlying = underlyingReference.get();
		if (underlying == null)
		{
			emptyAction.run();
		}
		return (underlying != null);
	}

	/**
	 * Runs the provided action if the element underlying this EnhancedWeakReference
	 * matches the given object
	 * 
	 * @param object
	 *            The object to be compared to the object underlying this
	 *            EnhancedWeakReference
	 * @param action
	 *            The action to be taken if the object underlying this
	 *            EnhancedWeakReference matches the given object
	 */
	public void ifEquals(T object, Consumer<? super T> action)
	{
		T underlying = underlyingReference.get();
		if ((underlying != null) && (underlying.equals(object)))
		{
			action.accept(underlying);
		}
	}

	/**
	 * Returns the underlying object if present, or returns the provided element if the
	 * underlying WeakReference is empty.
	 * 
	 * @param other
	 *            The alternative object to be returned if the underlying WeakReference is
	 *            empty
	 * @return The underlying object if present, or returns the provided element if the
	 *         underlying WeakReference is empty
	 */
	public T orElse(T other)
	{
		T underlying = underlyingReference.get();
		return (underlying == null) ? other : underlying;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj instanceof EnhancedWeakReference)
		{
			EnhancedWeakReference<?> other = (EnhancedWeakReference<?>) obj;
			return Objects.equals(underlyingReference.get(),
				other.underlyingReference.get());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return hash;
	}
}
