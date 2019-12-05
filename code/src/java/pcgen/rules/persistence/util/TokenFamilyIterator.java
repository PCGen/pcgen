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

import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMToken;

/**
 * An Iterator that increments across the CDOMPrimaryTokens in a given
 * TokenFamily. This is primarily used when items are "unparsed" (converted to
 * LST), since it is necessary to increment across ALL tokens in order to be
 * able to ensure that the object is fully written to a persistent format.
 * <p>
 * Note that this Iterator "corrects" for the fact that parent classes can also
 * be used, meaning if an LST Token is registered against CDOMObject.class it
 * can be used on Race.class or Ability.class, etc.
 *
 * @param <C> The Class of object for which this TokenFamilyIterator is
 *            iterating over tokens (will be the class of the object being
 *            loaded, such as Race or Ability)
 */
public class TokenFamilyIterator<C> implements Iterator<CDOMPrimaryToken<? super C>>
{

    /**
     * The Object class, "cached" due to common use
     */
    private static final Class<Object> OBJECT_CLASS = Object.class;

    /**
     * The next token to be returned by this TokenFamilyIterator (Note: is only
     * validly containing the next token when needNewToken is false)
     */
    private CDOMPrimaryToken<? super C> nextToken = null;

    /**
     * True if nextToken has been used and needs to be refreshed
     */
    private boolean needNewToken = true;

    /**
     * The "acting" class for which tokens are being retrieved. This can be
     * either the class being loaded (e.g. Race) or a parent Class (e.g.
     * CDOMObject).
     */
    private Class<?> actingClass;

    /**
     * The underlying Iterator that is incrementing across the CDOMTokens in the
     * actingClass.
     */
    private Iterator<CDOMToken<?>> subIterator;

    /**
     * The token names that have been used. This is necessary since a specific
     * class (e.g. Race) can have a "local" version of a token. If that local
     * version exists, then we MUST skip the version that is "more global" when
     * we do this Iterator, otherwise this Iterator will provide tokens that are
     * designed to be unreachable (And thus will produce errors)
     */
    private final Set<String> used = new HashSet<>();

    /**
     * Constructs a new TokenFamilyIterator for the given Class
     *
     * @param cl The Class for which this TokenFamilyIterator will return
     *           CDOMTokens.
     */
    public TokenFamilyIterator(Class<C> cl)
    {
        actingClass = cl;
        subIterator = TokenFamily.CURRENT.getTokens(cl).iterator();
    }

    @Override
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

    /**
     * Sets nextToken to be "valid", in the sense of determining the next token
     * that this TokenFamilyIterator needs to return
     */
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
                    needNewToken = true;
                    setNext();
                } else
                {
                    used.add(nextToken.getTokenName());
                }
            }
        }
    }

    /**
     * Returns the next eligible CDOMPrimaryToken.
     * <p>
     * Note the eligible token may not be valid for use if it is a token for a
     * parent class and there was a more specific token already defined.
     *
     * @return The next eligible CDOMPrimaryToken
     */
    private CDOMPrimaryToken<? super C> getNext()
    {
        needNewToken = false;
        while (subIterator.hasNext())
        {
            CDOMToken<?> tok = subIterator.next();
            if (tok instanceof CDOMPrimaryToken)
            {
                @SuppressWarnings("unchecked")
                CDOMPrimaryToken<? super C> pt = (CDOMPrimaryToken<? super C>) tok;
                return pt;
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

    @Override
    public boolean hasNext()
    {
        setNext();
        return nextToken != null;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Iterator does not support remove");
    }
}
