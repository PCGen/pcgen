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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.TripleKeyMap;
import pcgen.cdom.base.Loadable;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

public final class TokenFamily implements Comparable<TokenFamily>
{

	public static final TokenFamily CURRENT = new TokenFamily(new Revision(
			Integer.MAX_VALUE, 0, 0));

	public static final TokenFamily REV514 = new TokenFamily(new Revision(5,
			14, Integer.MIN_VALUE));

	/**
	 * The Map of Constants
	 */
	private static SortedMap<Revision, TokenFamily> typeMap;

	/**
	 * The name of this Constant
	 */
	private final Revision rev;

	private final DoubleKeyMap<Class<?>, String, CDOMToken<?>> tokenMap =
			new DoubleKeyMap<Class<?>, String, CDOMToken<?>>();

	private final TripleKeyMap<Class<?>, String, String, CDOMSecondaryToken<?>> subTokenMap =
			new TripleKeyMap<Class<?>, String, String, CDOMSecondaryToken<?>>(HashMap.class, CaseInsensitiveMap.class, CaseInsensitiveMap.class);

	private final Map<CaseInsensitiveString, PrerequisiteParserInterface> preTokenMap =
			new HashMap<CaseInsensitiveString, PrerequisiteParserInterface>();

	private final List<DeferredToken<? extends Loadable>> deferredTokenList =
			new ArrayList<DeferredToken<? extends Loadable>>();

	
	public TokenFamily(Revision r)
	{
		rev = r;
	}

	public <T> CDOMToken<T> putToken(CDOMToken<T> tok)
	{
		if (tok.getTokenClass() == null)
		{
			Logging.errorPrint("Cannot load token "
				+ tok.getClass().getSimpleName() + " with no token class");
		}
		return (CDOMToken<T>) tokenMap.put(tok.getTokenClass(), tok
				.getTokenName(), tok);
	}

	public CDOMToken<?> getToken(Class<?> cl, String name)
	{
		return tokenMap.get(cl, name);
	}

	public Set<CDOMToken<?>> getTokens(Class<?> cl)
	{
		return tokenMap.values(cl);
	}

	public <U, T extends CDOMSecondaryToken<U>> CDOMSecondaryToken<U> putSubToken(T tok)
	{
		return (CDOMSecondaryToken<U>) subTokenMap.put(tok.getTokenClass(), tok
				.getParentToken(), tok.getTokenName(), tok);
	}

	public <T> CDOMSubToken<T> getSubToken(Class<? extends T> cl, String token,
			String key)
	{
		return (CDOMSubToken<T>) subTokenMap.get(cl, token, key);
	}

	public Set<CDOMSecondaryToken<?>> getSubTokens(Class<?> cl, String token)
	{
		return subTokenMap.values(cl, token);
	}

	public void putPrerequisiteToken(PrerequisiteParserInterface token)
	{
		for (String s : token.kindsHandled())
		{
			preTokenMap.put(new CaseInsensitiveString(s), token);
		}
	}

	public PrerequisiteParserInterface getPrerequisiteToken(String key)
	{
		return preTokenMap.get(new CaseInsensitiveString(key));
	}

	/**
	 * Constructs a new TokenFamily with the given primary, secondary and
	 * tertiary values as the Sequence characteristics
	 * 
	 * @return The new TokenFamily built with the given primary, secondary and
	 *         tertiary values
	 */
	public static TokenFamily getConstant(int primary, int secondary,
			int tertiary)
	{
		if (typeMap == null)
		{
			buildMap();
		}
		Revision r = new Revision(primary, secondary, tertiary);
		TokenFamily o = typeMap.get(r);
		if (o == null)
		{
			o = new TokenFamily(r);
			typeMap.put(r, o);
		}
		return o;
	}

	/**
	 * Actually build the set of Constants, using any "public static final"
	 * constants within the child (extending) class as initial values in the
	 * Constant pool.
	 */
	private static void buildMap()
	{
		typeMap = new TreeMap<Revision, TokenFamily>();
		Class<TokenFamily> cl = TokenFamily.class;
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (cl.equals(o.getClass()))
					{
						TokenFamily tObj = cl.cast(o);
						if (typeMap.containsKey(tObj.rev))
						{
							throw new UnreachableError(
									"Attempt to redefine constant value "
											+ tObj.rev + " to "
											+ fields[i].getName()
											+ ", value was "
											+ typeMap.get(tObj.rev));
						}
						typeMap.put(tObj.rev, tObj);
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new UnreachableError(
						"Attempt to fetch field failed: " + e.getMessage());
				}
				catch (IllegalAccessException e)
				{
					throw new UnreachableError(
						"Attempt to fetch field failed for access: "
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * Clears all of the Constants defined by this class. Note that this does
	 * not remove any Constants declared in the Constant class (as those are
	 * considered 'permanent' members of the Sequenced Constant collection.
	 * 
	 * Note that this *will not* reset the ordinal count, because that is a
	 * dangerous operation. As there could be outstanding references to
	 * constants that would be removed from the Constant pool, no reuse of
	 * ordinals is driven by this method. As a result, calling this method may
	 * result in a Constant Pool which does not have sequentially numbered
	 * ordinal values.
	 */
	public static void clearConstants()
	{
		buildMap();
	}

	/**
	 * Returns a Collection of all of the Constants for this class. The returned
	 * Collection is unmodifiable.
	 * 
	 * @return an unmodifiable Collection of all of the Constants for this class
	 */
	public static Collection<TokenFamily> getAllConstants()
	{
		return Collections.unmodifiableCollection(typeMap.values());
	}

	@Override
	public int compareTo(TokenFamily tf)
	{
		return rev.compareTo(tf.rev);
	}

	/*
	 * Note there is no reason to do .hashCode or .equals because this is Type
	 * Safe (meaning it can only build one object per Revision)
	 */

	@Override
	public String toString()
	{
		return "Token Family: " + rev.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj == this || obj instanceof TokenFamily
				&& compareTo((TokenFamily) obj) == 0;
	}

	@Override
	public int hashCode()
	{
		return rev.hashCode();
	}

	public void clearTokens()
	{
		tokenMap.clear();
		subTokenMap.clear();
		deferredTokenList.clear();
		preTokenMap.clear();
	}

	public List<DeferredToken<? extends Loadable>> getDeferredTokens()
	{
		return new ArrayList<DeferredToken<? extends Loadable>>(
				deferredTokenList);
	}

	public void addDeferredToken(DeferredToken<?> newToken)
	{
		deferredTokenList.add(newToken);
	}
}
