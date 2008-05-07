/**
 * 
 */
package pcgen.rules.persistence.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMToken;

public class TokenFamilyIterator<C> implements
		Iterator<CDOMPrimaryToken<? super C>>
{
	private static final Class<Object> OBJECT_CLASS = Object.class;
	private CDOMPrimaryToken<? super C> nextToken = null;
	private boolean needNewToken = true;
	private Class<?> actingClass;
	private Iterator<CDOMToken<?>> subIterator;
	private Set<String> used = new HashSet<String>();

	public TokenFamilyIterator(Class<C> cl)
	{
		actingClass = cl;
		subIterator = TokenFamily.CURRENT.getTokens(cl).iterator();
	}

	public CDOMPrimaryToken<? super C> next()
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

	private CDOMPrimaryToken<? super C> getNext()
	{
		needNewToken = false;
		while (subIterator.hasNext())
		{
			CDOMToken<?> tok = subIterator.next();
			if (tok instanceof CDOMPrimaryToken)
			{
				return (CDOMPrimaryToken<? super C>) tok;
			}
		}
		if (OBJECT_CLASS.equals(actingClass))
		{
			return null;
		}
		actingClass = actingClass.getSuperclass();
		subIterator = TokenFamily.CURRENT.getTokens(actingClass).iterator();
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