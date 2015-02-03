/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.modifier;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.Modifier;
import pcgen.util.StringPClassUtil;

/**
 * A ContextModifier is a Modifier that has the ability to wrap another Modifier
 * in order to conditionally apply the underlying Modifier only in a given
 * context.
 * 
 * @param <T>
 *            The class of object this ContextModifier acts upon.
 * @param <R>
 *            The class of objects which provide the context in which this
 *            ContextModifier acts
 */
public class ContextModifier<T, R extends PrereqObject> implements Modifier<T>
{
	/**
	 * The underlying Modifier that this ContextModifer will apply when the
	 * given context is matched.
	 */
	private final Modifier<T> modifier;

	/**
	 * A Reference which contains the objects in the context in which the
	 * underlying Modifier should be applied.
	 */
	private final CDOMReference<R> contextItems;

	/**
	 * Constructs a new ContextModifier that will conditionally apply the given
	 * Modifier only when objects in the given CDOMReference are provided as the
	 * context of the modification.
	 * 
	 * @param mod
	 *            The underlying Modifier that this ContextModifer will apply
	 *            when the given context is matched.
	 * @param contextRef
	 *            The CDOMReference which contains the objects for which the
	 *            underlying Modifier should be applied.
	 * @throws IllegalArgumentException
	 *             if the given Modifier or the given CDOMReference is null
	 */
	public ContextModifier(Modifier<T> mod, CDOMReference<R> contextRef)
	{
		if (mod == null)
		{
			throw new IllegalArgumentException(
					"Modifier in ContextModifier cannot be null");
		}
		if (contextRef == null)
		{
			throw new IllegalArgumentException(
					"Context in ContextModifier cannot be null");
		}
		modifier = mod;
		contextItems = contextRef;
	}

	/**
	 * Conditionally applies the underlying Modifier to the given input. Will
	 * only be applied if the object given as the context object is contained
	 * within the CDOMReference provided during construction of this
	 * ContextModifier.
	 * 
	 * Note this method may return the object passed in as the input object. The
	 * behavior of ContextModifier will depend on the behavior of the underlying
	 * Modifier. Therefore, if the input object is mutable, the caller of the
	 * applyModifier method should be aware of that behavior, and should treat
	 * the returned object appropriately.
	 * 
	 * @param obj
	 *            The input object this ContextModifier will act upon
	 * @param context
	 *            The context of this ContextModifier, to establish whether this
	 *            Modifier should act upon the input object
	 * @return The modified object, of the same class as the input object.
	 */
	@Override
	public T applyModifier(T obj, Object context)
	{
		return (context instanceof PrereqObject && contextItems
				.contains((R) context)) ? modifier.applyModifier(obj, context)
				: obj;
	}

	/**
	 * Returns a representation of this ContextModifier, suitable for storing in
	 * an LST file.
	 * 
	 * @return A representation of this ContextModifier, suitable for storing in
	 *         an LST file.
	 */
	@Override
	public String getLSTformat()
	{
		String contextString = contextItems.getLSTformat(false);
		StringBuilder sb = new StringBuilder();
		sb.append(modifier.getLSTformat()).append('|');
		sb.append(StringPClassUtil.getStringFor(contextItems
				.getReferenceClass()));
		sb.append(contextString.indexOf('=') == -1 ? '=' : '.');
		sb.append(contextString);
		return sb.toString();
	}

	/**
	 * The class of object this ContextModifier acts upon (matches the modified
	 * class of the underlying Modifier).
	 * 
	 * @return The class of object this ContextModifier acts upon
	 */
	@Override
	public Class<T> getModifiedClass()
	{
		return modifier.getModifiedClass();
	}

	/**
	 * Returns true if this ContextModifier is equal to the given Object.
	 * Equality is defined as being another ContextModifier object underlying
	 * Modifier and context items
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ContextModifier)
		{
			ContextModifier<?, ?> other = (ContextModifier<?, ?>) obj;
			return modifier.equals(other.modifier)
					&& contextItems.equals(other.contextItems);
		}
		return false;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this ContextModifier
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return modifier.hashCode() * 31 - contextItems.hashCode();
	}

}
