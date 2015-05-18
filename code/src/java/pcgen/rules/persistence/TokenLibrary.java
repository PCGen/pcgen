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
package pcgen.rules.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.GroupDefinition;
import pcgen.cdom.base.Loadable;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.prereq.PreMultParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ClassWrappedToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.rules.persistence.token.PreCompatibilityToken;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.rules.persistence.util.TokenFamily;
import pcgen.rules.types.FormatManager;
import pcgen.rules.types.FormatManagerLibrary;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;

public final class TokenLibrary implements PluginLoader
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<CDOMObject> CDOMOBJECT_CLASS = CDOMObject.class;
	private static final TreeMapToList<Integer, PostDeferredToken<? extends Loadable>> POST_DEFERRED_TOKENS =
			new TreeMapToList<Integer, PostDeferredToken<? extends Loadable>>();
	private static final DoubleKeyMap<Class<?>, String, Class<? extends QualifierToken>> QUALIFIER_MAP =
			new DoubleKeyMap<Class<?>, String, Class<? extends QualifierToken>>();
	private static final DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>> PRIMITIVE_MAP =
			new DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>>();
	private static final Set<TokenFamily> TOKEN_FAMILIES = new TreeSet<TokenFamily>();
	private static final CaseInsensitiveMap<Class<? extends BonusObj>> BONUS_TAG_MAP =
			new CaseInsensitiveMap<Class<? extends BonusObj>>();

	private static TokenLibrary instance = null;

	static
	{
		reset();
	}
	
	public static void reset()
	{
		POST_DEFERRED_TOKENS.clear();
		QUALIFIER_MAP.clear();
		PRIMITIVE_MAP.clear();
		BONUS_TAG_MAP.clear();
		TOKEN_FAMILIES.clear();
		TokenFamily.CURRENT.clearTokens();
		TOKEN_FAMILIES.add(TokenFamily.CURRENT);
		TokenFamily.REV514.clearTokens();
		TOKEN_FAMILIES.add(TokenFamily.REV514);
		addToTokenMap(new PreMultParser());
	}

	private TokenLibrary()
	{
		// Don't instantiate utility class
	}

	public static <T> PrimitiveToken<T> getPrimitive(Class<T> cl, String tokKey)
	{
		Iterator<PrimitiveToken<T>> it =
				new PrimitiveTokenIterator<T, PrimitiveToken<T>>(cl, tokKey);
		if (it.hasNext())
		{
			return it.next();
		}
		return null;
	}

	public static Collection<PostDeferredToken<? extends Loadable>> getPostDeferredTokens()
	{
		List<PostDeferredToken<? extends Loadable>> list =
				new ArrayList<PostDeferredToken<? extends Loadable>>();
		for (Integer key : POST_DEFERRED_TOKENS.getKeySet())
		{
			list.addAll(POST_DEFERRED_TOKENS.getListFor(key));
		}
		return list;
	}

	public static void addToPrimitiveMap(PrimitiveToken<?> p)
	{
		Class<? extends PrimitiveToken> newTokClass = p.getClass();
		if (PrimitiveToken.class.isAssignableFrom(newTokClass))
		{
			String name = p.getTokenName();
			Class cl = ((PrimitiveToken) p).getReferenceClass();
			Class<PrimitiveToken<?>> prev =
					PRIMITIVE_MAP.put(cl, name, (Class<PrimitiveToken<?>>) newTokClass);
			if (prev != null)
			{
				Logging.errorPrint("Found a second " + name + " Primitive for " + cl);
			}
		}
	}

	public static void addToQualifierMap(QualifierToken<?> p)
	{
		Class<? extends QualifierToken> newTokClass = p.getClass();
		Class<?> cl = p.getReferenceClass();
		String name = p.getTokenName();
		Class<? extends QualifierToken> prev = QUALIFIER_MAP.put(cl, name, newTokClass);
		if (prev != null)
		{
			Logging.errorPrint("Found a second " + name + " Qualifier for " + cl);
		}
	}

	public static void addToTokenMap(Object newToken)
	{
		if (newToken instanceof PostDeferredToken)
		{
			PostDeferredToken<?> pdt = (PostDeferredToken<?>) newToken;
			POST_DEFERRED_TOKENS.addToListFor(pdt.getPriority(), pdt);
		}
		if (newToken instanceof CDOMCompatibilityToken)
		{
			CDOMCompatibilityToken<?> tok = (CDOMCompatibilityToken<?>) newToken;
			TokenFamily fam =
					TokenFamily.getConstant(tok.compatibilityLevel(),
						tok.compatibilitySubLevel(), tok.compatibilityPriority());
			if (fam.putToken(tok) != null)
			{
				Logging.errorPrint("Duplicate " + tok.getTokenClass().getSimpleName()
					+ " Compatibility Token found for token " + tok.getTokenName()
					+ " at compatibility level " + tok.compatibilityLevel() + "."
					+ tok.compatibilitySubLevel() + "." + tok.compatibilityPriority());
			}
			TOKEN_FAMILIES.add(fam);
			if (fam.compareTo(TokenFamily.REV514) <= 0 && PCCLASS_CLASS.equals(tok.getTokenClass()))
			{
				addToTokenMap(new ClassWrappedToken(
						(CDOMCompatibilityToken<PCClass>) tok));
			}
		}
		loadFamily(TokenFamily.CURRENT, newToken);
	}

	public static void loadFamily(TokenFamily family, Object newToken)
	{
		if (newToken instanceof DeferredToken)
		{
			family.addDeferredToken((DeferredToken<?>) newToken);
		}
		if (newToken instanceof CDOMPrimaryToken)
		{
			CDOMPrimaryToken<?> tok = (CDOMPrimaryToken<?>) newToken;
			CDOMToken<?> existingToken = family.putToken(tok);
			if (existingToken != null)
			{
				Logging.errorPrint("Duplicate "
					+ tok.getTokenClass().getSimpleName()
					+ " Token found for token " + tok.getTokenName()
					+ ". Classes were " + existingToken.getClass().getName()
					+ " and " + newToken.getClass().getName());
			}
			if (PCCLASS_CLASS.equals(tok.getTokenClass()))
			{
				addToTokenMap(new ClassWrappedToken(
						(CDOMPrimaryToken<PCClass>) tok));
			}
		}
		if (newToken instanceof CDOMSecondaryToken)
		{
			CDOMSecondaryToken<?> tok = (CDOMSecondaryToken<?>) newToken;
			CDOMSubToken<?> existingToken = family.putSubToken(tok);
			if (existingToken != null)
			{
				Logging.errorPrint("Duplicate "
					+ tok.getTokenClass().getSimpleName()
					+ " Token found for token " + tok.getParentToken() + ":"
					+ tok.getTokenName() + ". Classes were "
					+ existingToken.getClass().getName() + " and "
					+ newToken.getClass().getName());
			}
		}
		if (newToken instanceof PrerequisiteParserInterface)
		{
			PrerequisiteParserInterface prereqToken = (PrerequisiteParserInterface) newToken;
			family.putPrerequisiteToken(prereqToken);
			for (String s : prereqToken.kindsHandled())
			{
				/*
				 * TODO Theoretically these belong in REV514, but put into
				 * current for unparse testing
				 */
				PreCompatibilityToken pos =
						new PreCompatibilityToken(s, prereqToken, false);
				if (family.putToken(pos) != null)
				{
					Logging.errorPrint("Duplicate " + pos.getTokenClass().getSimpleName()
						+ " Token found for token " + pos.getTokenName());
				}
				if (family.putSubToken(pos) != null)
				{
					Logging.errorPrint("Duplicate " + pos.getTokenClass().getSimpleName()
						+ " Token found for token " + pos.getParentToken() + ":"
						+ pos.getTokenName());
				}
				family.putSubToken(pos);
				PreCompatibilityToken neg =
						new PreCompatibilityToken(s, prereqToken, true);
				if (family.putToken(neg) != null)
				{
					Logging.errorPrint("Duplicate " + neg.getTokenClass().getSimpleName()
						+ " Token found for token " + neg.getTokenName());
				}
				if (family.putSubToken(neg) != null)
				{
					Logging.errorPrint("Duplicate " + neg.getTokenClass().getSimpleName()
						+ " Token found for token " + neg.getParentToken() + ":"
						+ neg.getTokenName());
				}
			}
		}
		if (newToken instanceof GroupDefinition)
		{
			family.addGroupDefinition((GroupDefinition<?>) newToken);
		}
	}

	public static TokenLibrary getInstance()
	{
		if (instance == null)
		{
			instance = new TokenLibrary();
		}
		return instance;
	}

	@Override
	public void loadPlugin(Class<?> clazz) throws Exception
	{
		if (BonusObj.class.isAssignableFrom(clazz))
		{
			addBonusClass(clazz);
		}

		Object token = clazz.newInstance();
		if (LstToken.class.isAssignableFrom(clazz)
			|| PrerequisiteParserInterface.class.isAssignableFrom(clazz))
		{
			addToTokenMap(token);
		}
		if (QualifierToken.class.isAssignableFrom(clazz))
		{
			addToQualifierMap((QualifierToken<?>) token);
		}
		if (PrimitiveToken.class.isAssignableFrom(clazz))
		{
			addToPrimitiveMap((PrimitiveToken<?>) token);
		}
		if (FormatManager.class.isAssignableFrom(clazz))
		{
			FormatManagerLibrary.addFormatManager((FormatManager<?>) token);
		}
	}

	@Override
	public Class[] getPluginClasses()
	{
		return new Class[]
				{
					LstToken.class,
					BonusObj.class,
					FormatManager.class,
					PrerequisiteParserInterface.class
				};
	}

	abstract static class AbstractTokenIterator<C, T> implements Iterator<T>
	{
		// private static final Class<Object> OBJECT_CLASS = Object.class;

		private final Class<C> rootClass;
		private final String tokenKey;
		private T nextToken = null;
		private boolean needNewToken = true;
		private Class<?> stopClass;
		private final Iterator<TokenFamily> subIterator;

		public AbstractTokenIterator(Class<C> cl, String key)
		{
			rootClass = cl;
			subIterator = TOKEN_FAMILIES.iterator();
			tokenKey = key;
		}

		@Override
		public boolean hasNext()
		{
			setNextToken();
			return !needNewToken;
		}

		protected void setNextToken()
		{
			while (needNewToken && subIterator.hasNext())
			{
				TokenFamily family = subIterator.next();
				Class<?> actingClass = rootClass;
				nextToken = grabToken(family, actingClass, tokenKey);
				while (nextToken == null && actingClass != null && !actingClass.equals(stopClass))
				{
					actingClass = actingClass.getSuperclass();
					nextToken = grabToken(family, actingClass, tokenKey);
				}
				if (stopClass == null)
				{
					stopClass = actingClass;
				}
				needNewToken = nextToken == null;
			}
		}

		protected abstract T grabToken(TokenFamily family, Class<?> cl,
									   String key);

		@Override
		public T next()
		{
			setNextToken();
			if (needNewToken)
			{
				throw new NoSuchElementException();
			}
			needNewToken = true;
			return nextToken;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException(
					"Iterator does not support remove");
		}

	}

	static class TokenIterator<C extends Loadable, T extends CDOMToken<? super C>>
			extends TokenLibrary.AbstractTokenIterator<C, T>
	{

		public TokenIterator(Class<C> cl, String key)
		{
			super(cl, key);
		}

		@Override
		protected T grabToken(TokenFamily family, Class<?> cl, String key)
		{
			return (T) family.getToken(cl, key);
		}

	}

	static class SubTokenIterator<C, T extends CDOMSubToken<? super C>> extends
			TokenLibrary.AbstractTokenIterator<C, T>
	{

		private final String subTokenKey;

		public SubTokenIterator(Class<C> cl, String key, String subKey)
		{
			super(cl, key);
			subTokenKey = subKey;
		}

		@Override
		protected T grabToken(TokenFamily family, Class<?> cl, String key)
		{
			return (T) family.getSubToken(cl, key, subTokenKey);
		}

	}

	static class QualifierTokenIterator<C extends CDOMObject, T extends QualifierToken<? super C>>
			extends TokenLibrary.AbstractTokenIterator<C, T>
	{

		public QualifierTokenIterator(Class<C> cl, String key)
		{
			super(cl, key);
		}

		@Override
		protected T grabToken(TokenFamily family, Class<?> cl, String key)
		{
			if (!TokenFamily.CURRENT.equals(family))
			{
				return null;
			}
			Class<? extends QualifierToken> cl1 = QUALIFIER_MAP.get(cl, key);
			if (cl1 == null)
			{
				return null;
			}
			try
			{
				return (T) cl1.newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnreachableError("new Instance on " + cl1 + " should not fail", e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnreachableError("new Instance on " + cl1
					+ " should not fail due to access", e);
			}
		}

	}

	static class PrimitiveTokenIterator<C, T extends PrimitiveToken<? super C>>
			extends TokenLibrary.AbstractTokenIterator<C, T>
	{

		public PrimitiveTokenIterator(Class<C> cl, String key)
		{
			super(cl, key);
		}

		@Override
		protected T grabToken(TokenFamily family, Class<?> cl, String key)
		{
			if (!TokenFamily.CURRENT.equals(family))
			{
				return null;
			}
			Class<? extends PrimitiveToken> cl1 = PRIMITIVE_MAP.get(cl, key);
			if (cl1 == null)
			{
				return null;
			}
			try
			{
				return (T) cl1.newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnreachableError("new Instance on " + cl1 + " should not fail", e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnreachableError("new Instance on " + cl1
					+ " should not fail due to access", e);
			}
		}

	}

	static class PreTokenIterator
			extends TokenLibrary.AbstractTokenIterator<CDOMObject, PrerequisiteParserInterface>
	{

		public PreTokenIterator(String key)
		{
			super(CDOMOBJECT_CLASS, key);
		}

		@Override
		protected PrerequisiteParserInterface grabToken(TokenFamily family, Class<?> cl,
			String key)
		{
			return family.getPrerequisiteToken(key);
		}

	}

	/**
	 * Add a CLASS via a BONUS
	 * 
	 * @return true if successful
	 */
	public static boolean addBonusClass(Class bonusClass) throws InstantiationException,
		IllegalAccessException
	{
		if (BonusObj.class.isAssignableFrom(bonusClass))
		{
			final BonusObj bonusObj = (BonusObj) bonusClass.newInstance();
			BONUS_TAG_MAP.put(bonusObj.getBonusHandled(), bonusClass);
			return true;
		}
		return false;
	}

	public static Class<? extends BonusObj> getBonus(String bonusName)
	{
		return BONUS_TAG_MAP.get(bonusName);
	}

}
