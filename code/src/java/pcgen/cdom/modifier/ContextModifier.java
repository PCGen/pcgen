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

public class ContextModifier<T, R extends PrereqObject> implements Modifier<T>
{
	private final Modifier<T> modifier;
	private final CDOMReference<R> contextItems;

	public ContextModifier(Modifier<T> mod, CDOMReference<R> context)
	{
		if (mod == null)
		{
			throw new IllegalArgumentException(
					"Modifier in ContextModifier cannot be null");
		}
		if (context == null)
		{
			throw new IllegalArgumentException(
					"Context in ContextModifier cannot be null");
		}
		modifier = mod;
		contextItems = context;
	}

	public T applyModifier(T obj, Object context)
	{
		return (context instanceof PrereqObject && contextItems
				.contains((R) context)) ? modifier.applyModifier(obj, context)
				: obj;
	}

	public String getLSTformat()
	{
		String cf = contextItems.getLSTformat();
		StringBuilder sb = new StringBuilder();
		sb.append(modifier.getLSTformat()).append('|');
		sb.append(StringPClassUtil.getStringFor(contextItems
				.getReferenceClass()));
		sb.append(cf.indexOf('=') == -1 ? '=' : '.');
		sb.append(cf);
		return sb.toString();
	}

	public Class<T> getModifiedClass()
	{
		return modifier.getModifiedClass();
	}

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

	@Override
	public int hashCode()
	{
		return modifier.hashCode() * 31 - contextItems.hashCode();
	}

}
