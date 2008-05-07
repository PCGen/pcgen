/**
 * 
 */
package pcgen.rules.persistence.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;

public class TokenFamilySubIterator<C> implements
		Iterator<CDOMSecondaryToken<? super C>>
{
	private static final Class<Object> OBJECT_CLASS = Object.class;
	private CDOMSecondaryToken<? super C> nextToken = null;
	private boolean needNewToken = true;
	private Class<?> actingClass;
	private final String parentToken;
	private Iterator<CDOMSubToken<?>> subIterator;
	private Set<String> used = new HashSet<String>();

	public TokenFamilySubIterator(Class<C> cl, String parentName)
	{
		actingClass = cl;
		parentToken = parentName;
		subIterator = TokenFamily.CURRENT.getSubTokens(cl, parentToken)
				.iterator();
	}

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

	public boolean hasNext()
	{
		setNext();
		return nextToken != null;
	}

	public void remove()
	{
		throw new UnsupportedOperationException(
				"Iterator does not support remove");
	}
}