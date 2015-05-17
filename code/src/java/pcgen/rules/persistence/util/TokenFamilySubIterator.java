/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMToken;

public class TokenFamilySubIterator<C> implements
		Iterator<CDOMSecondaryToken<? super C>>
{
	private static final Class<Object> OBJECT_CLASS = Object.class;
	private CDOMSecondaryToken<? super C> nextToken = null;
	private boolean needNewToken = true;
	private Class<?> actingClass;
	private final String parentToken;
	private Iterator<CDOMSecondaryToken<?>> subIterator;
	private final Set<String> used = new HashSet<String>();

	public TokenFamilySubIterator(Class<C> cl, String parentName)
	{
		actingClass = cl;
		parentToken = parentName;
		subIterator = TokenFamily.CURRENT.getSubTokens(cl, parentToken)
				.iterator();
	}

	@Override
	public CDOMSecondaryToken<? super C> next()
	{
		setNext();
		if (nextToken == null)
		{
			throw new NoSuchElementException();
		}
		needNewToken = true;
		return nextToken;
	}

	private void setNext()
	{
		if (needNewToken)
		{
			nextToken = getNext();
			if (nextToken != null)
			{
				String tokenName = nextToken.getTokenName();
				if (used.contains(tokenName))
				{
					/*
					 * Don't use a super-class token in write
					 */
					setNext();
				}
				else
				{
					used.add(nextToken.getTokenName());
				}
			}
		}
	}

	private CDOMSecondaryToken<? super C> getNext()
	{
		needNewToken = false;
		while (subIterator.hasNext())
		{
			CDOMToken<?> tok = subIterator.next();
			if (tok instanceof CDOMSecondaryToken)
			{
				return (CDOMSecondaryToken<? super C>) tok;
			}
		}
		if (OBJECT_CLASS.equals(actingClass))
		{
			return null;
		}
		actingClass = actingClass.getSuperclass();
		subIterator = TokenFamily.CURRENT
				.getSubTokens(actingClass, parentToken).iterator();
		return getNext();
	}

	@Override
	public boolean hasNext()
	{
		setNext();
		return nextToken != null;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException(
				"Iterator does not support remove");
	}
}
