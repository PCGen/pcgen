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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import pcgen.cdom.grouping.GroupingDefinition;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.prereq.PreMultParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.CDOMInterfaceToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ClassWrappedToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ModifierFactory;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.rules.persistence.token.PreCompatibilityToken;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.rules.persistence.util.TokenFamily;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;

public final class TokenLibrary implements PluginLoader
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<CDOMObject> CDOMOBJECT_CLASS = CDOMObject.class;
	private static final TreeMapToList<Integer, PostDeferredToken<? extends Loadable>> POST_DEFERRED_TOKENS =
			new TreeMapToList<>();
	private static final TreeMapToList<Integer, PostValidationToken<? extends Loadable>> POST_VALIDATION_TOKENS =
			new TreeMapToList<>();
	private static final DoubleKeyMap<Class<?>, String, Class<? extends QualifierToken<?>>> QUALIFIER_MAP =
			new DoubleKeyMap<>();
	private static final DoubleKeyMap<Class<?>, String, Class<? extends PrimitiveToken<?>>> PRIMITIVE_MAP =
			new DoubleKeyMap<>();

	/**
	 * Contains legal GroupingDefinition objects loaded from plugins
	 */
	private static final DoubleKeyMap<Class<?>, String, GroupingDefinition<?>> GROUPING_MAP = new DoubleKeyMap<>();
	private static final DoubleKeyMap<Class<?>, String, ModifierFactory<?>> MODIFIER_MAP = new DoubleKeyMap<>();

	/**
	 * Contains the interface tokens mapped by the token name.
	 */
	private static final Map<String, CDOMInterfaceToken<?, ?>> IF_TOKEN_MAP = new HashMap<>();
	private static final Set<TokenFamily> TOKEN_FAMILIES = new TreeSet<>();
	private static final CaseInsensitiveMap<Class<? extends BonusObj>> BONUS_TAG_MAP = new CaseInsensitiveMap<>();

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
		GROUPING_MAP.clear();
		MODIFIER_MAP.clear();
		IF_TOKEN_MAP.clear();
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
		Iterator<PrimitiveToken<T>> it = new PrimitiveTokenIterator<>(cl, tokKey);
		if (it.hasNext())
		{
			return it.next();
		}
		return null;
	}

	/**
	 * Returns the GroupingDefinition available with the given Format and grouping key.
	 * 
	 * @return The GroupingDefinition available with the given Format and grouping key.
	 */
	public static <T> GroupingDefinition<T> getGrouping(Class<T> inputClass,
		String tokenKey)
	{
		boolean isDirect = true;
		Class<? super T> actingClass = inputClass;
		while (actingClass != null)
		{
			GroupingDefinition token = GROUPING_MAP.get(actingClass, tokenKey);
			if ((token != null) && (token.requiresDirect() == isDirect))
			{
				return token;
			}
			actingClass = actingClass.getSuperclass();
			isDirect = false;
		}
		return null;
	}

	public static Collection<PostDeferredToken<? extends Loadable>> getPostDeferredTokens()
	{
		List<PostDeferredToken<? extends Loadable>> list = new ArrayList<>();
		for (Integer key : POST_DEFERRED_TOKENS.getKeySet())
		{
			list.addAll(POST_DEFERRED_TOKENS.getListFor(key));
		}
		return list;
	}

	public static void addToModifierMap(ModifierFactory<?> m)
	{
        ModifierFactory.class.isAssignableFrom(m.getClass());
        String name = m.getIdentification();
        Class<?> cl = m.getVariableFormat();
        ModifierFactory<?> prev = MODIFIER_MAP.put(cl, name, m);
        if (prev != null)
        {
            Logging.errorPrint("Found a second " + name + " Modifier for " + cl);
        }
    }

	public static <T> ModifierFactory<T> getModifier(Class<T> cl, String tokKey)
	{
		Iterator<ModifierFactory<T>> it = new ModifierIterator<>(cl, tokKey);
		if (it.hasNext())
		{
			return it.next();
		}
		else
		{
			return null;
		}
	}

	public static Collection<PostValidationToken<? extends Loadable>> getPostValidationTokens()
	{
		List<PostValidationToken<? extends Loadable>> list = new ArrayList<>();
		for (Integer key : POST_VALIDATION_TOKENS.getKeySet())
		{
			list.addAll(POST_VALIDATION_TOKENS.getListFor(key));
		}
		return list;
	}

	public static void addToPrimitiveMap(PrimitiveToken<?> p)
	{
		Class newTokClass = p.getClass();
		if (PrimitiveToken.class.isAssignableFrom(newTokClass))
		{
			String name = p.getTokenName();
			Class cl = p.getReferenceClass();
			Class<? extends PrimitiveToken<?>> prev = PRIMITIVE_MAP.put(cl, name, newTokClass);
			if (prev != null)
			{
				Logging.errorPrint("Found a second " + name + " Primitive for " + cl);
			}
		}
	}

	/**
	 * Adds a new GroupingDefinition to this TokenLibrary.
	 * 
	 * @param definition
	 *            The GroupingDefinition to be added to this TokenLibrary
	 */
	public static void addToGroupingMap(GroupingDefinition<?> definition)
	{
		String name = definition.getIdentification();
		Class<?> classScope = definition.getUsableLocation();
		GroupingDefinition<?> prev = GROUPING_MAP.put(classScope, name, definition);
		if (prev != null)
		{
			Logging.errorPrint("Found a second " + name + " Grouping for " + classScope.getSimpleName());
		}
	}

	/**
	 * Adds a new QualifierToken to this TokenLibrary.
	 * 
	 * @param token
	 *            The QualifierToken to be added to this TokenLibrary
	 */
	public static void addToQualifierMap(QualifierToken<?> token)
	{
		Class newTokClass = token.getClass();
		Class<?> cl = token.getReferenceClass();
		String name = token.getTokenName();
		Class<? extends QualifierToken> prev = QUALIFIER_MAP.put(cl, name, newTokClass);
		if (prev != null)
		{
			Logging.errorPrint("Found a second " + name + " Qualifier for " + cl);
		}
	}

	public static void addToTokenMap(Object newToken)
	{
		if (newToken instanceof PostDeferredToken<?> pdt)
		{
			POST_DEFERRED_TOKENS.addToListFor(pdt.getPriority(), pdt);
		}
		if (newToken instanceof PostValidationToken<?> pdt)
		{
			POST_VALIDATION_TOKENS.addToListFor(pdt.getPriority(), pdt);
		}
		if (newToken instanceof CDOMCompatibilityToken<?> tok)
		{
			TokenFamily fam = TokenFamily.getConstant(tok.compatibilityLevel(), tok.compatibilitySubLevel(),
				tok.compatibilityPriority());
			if (fam.putToken(tok) != null)
			{
				Logging.errorPrint("Duplicate " + tok.getTokenClass().getSimpleName()
					+ " Compatibility Token found for token " + tok.getTokenName() + " at compatibility level "
					+ tok.compatibilityLevel() + '.' + tok.compatibilitySubLevel() + '.' + tok.compatibilityPriority());
			}
			TOKEN_FAMILIES.add(fam);
			if (fam.compareTo(TokenFamily.REV514) <= 0 && PCCLASS_CLASS.equals(tok.getTokenClass()))
			{
				@SuppressWarnings("unchecked")
				CDOMCompatibilityToken<PCClass> clTok = (CDOMCompatibilityToken<PCClass>) tok;
				addToTokenMap(new ClassWrappedToken(clTok));
			}
		}
		if (newToken instanceof CDOMInterfaceToken<?, ?> tok)
		{
			CDOMInterfaceToken<?, ?> existingToken = IF_TOKEN_MAP.put(tok.getTokenName(), tok);
			if (existingToken != null)
			{
				Logging.errorPrint("Duplicate " + tok.getTokenClass().getSimpleName()
					+ " Token found for interface token " + tok.getTokenName() + ". Classes were "
					+ existingToken.getClass().getName() + " and " + newToken.getClass().getName());
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
		if (newToken instanceof CDOMPrimaryToken<?> tok)
		{
			CDOMToken<?> existingToken = family.putToken(tok);
			if (existingToken != null)
			{
				Logging.errorPrint("Duplicate " + tok.getTokenClass().getSimpleName() + " Token found for token "
					+ tok.getTokenName() + ". Classes were " + existingToken.getClass().getName() + " and "
					+ newToken.getClass().getName());
			}
			if (PCCLASS_CLASS.equals(tok.getTokenClass()))
			{
				@SuppressWarnings("unchecked")
				CDOMPrimaryToken<PCClass> clTok = (CDOMPrimaryToken<PCClass>) tok;
				addToTokenMap(new ClassWrappedToken(clTok));
			}
		}
		if (newToken instanceof CDOMSecondaryToken<?> tok)
		{
			CDOMSubToken<?> existingToken = family.putSubToken(tok);
			if (existingToken != null)
			{
				Logging.errorPrint("Duplicate " + tok.getTokenClass().getSimpleName() + " Token found for token "
					+ tok.getParentToken() + ':' + tok.getTokenName() + ". Classes were "
					+ existingToken.getClass().getName() + " and " + newToken.getClass().getName());
			}
		}
		if (newToken instanceof PrerequisiteParserInterface prereqToken)
		{
			family.putPrerequisiteToken(prereqToken);
			for (String s : prereqToken.kindsHandled())
			{
				/*
				 * TODO Theoretically these belong in REV514, but put into
				 * current for unparse testing
				 */
				PreCompatibilityToken pos = new PreCompatibilityToken(s, prereqToken, false);
				if (family.putToken(pos) != null)
				{
					Logging.errorPrint("Duplicate " + pos.getTokenClass().getSimpleName() + " Token found for token "
						+ pos.getTokenName());
				}
				if (family.putSubToken(pos) != null)
				{
					Logging.errorPrint("Duplicate " + pos.getTokenClass().getSimpleName() + " Token found for token "
						+ pos.getParentToken() + ':' + pos.getTokenName());
				}
				family.putSubToken(pos);
				PreCompatibilityToken neg = new PreCompatibilityToken(s, prereqToken, true);
				if (family.putToken(neg) != null)
				{
					Logging.errorPrint("Duplicate " + neg.getTokenClass().getSimpleName() + " Token found for token "
						+ neg.getTokenName());
				}
				if (family.putSubToken(neg) != null)
				{
					Logging.errorPrint("Duplicate " + neg.getTokenClass().getSimpleName() + " Token found for token "
						+ neg.getParentToken() + ':' + neg.getTokenName());
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
		if (LstToken.class.isAssignableFrom(clazz) || PrerequisiteParserInterface.class.isAssignableFrom(clazz))
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
		if (GroupingDefinition.class.isAssignableFrom(clazz))
		{
			addToGroupingMap((GroupingDefinition<?>) token);
		}
		if (ModifierFactory.class.isAssignableFrom(clazz))
		{
			addToModifierMap((ModifierFactory<?>) token);
		}
	}

	@Override
	public Class[] getPluginClasses()
	{
		return new Class[]{LstToken.class, BonusObj.class, PrerequisiteParserInterface.class, ModifierFactory.class,
			GroupingDefinition.class};
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
					actingClass = getSuperClass(actingClass);
					nextToken = grabToken(family, actingClass, tokenKey);
				}
				if (stopClass == null)
				{
					stopClass = actingClass;
				}
				needNewToken = nextToken == null;
			}
		}

		protected Class<?> getSuperClass(Class<?> actingClass)
		{
			return actingClass.getSuperclass();
		}

		protected abstract T grabToken(TokenFamily family, Class<?> cl, String key);

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
			throw new UnsupportedOperationException("Iterator does not support remove");
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

	static class SubTokenIterator<C, T extends CDOMSubToken<? super C>> extends TokenLibrary.AbstractTokenIterator<C, T>
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
				throw new UnreachableError("new Instance on " + cl1 + " should not fail due to access", e);
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
				throw new UnreachableError("new Instance on " + cl1 + " should not fail due to access", e);
			}
		}

	}

	static class ModifierIterator<C, T extends ModifierFactory<? super C>>
			extends TokenLibrary.AbstractTokenIterator<C, T>
	{

		public ModifierIterator(Class<C> cl, String key)
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
			return (T) MODIFIER_MAP.get(cl, key);
		}

		@Override
		protected Class<?> getSuperClass(Class<?> actingClass)
		{
			if (actingClass.isArray())
			{
				Class<?> component = actingClass.getComponentType();
				Class<?> parentComponent = getSuperClass(component);
				return Array.newInstance(parentComponent, 0).getClass();
			}
			return super.getSuperClass(actingClass);
		}
	}

	static class PreTokenIterator extends TokenLibrary.AbstractTokenIterator<CDOMObject, PrerequisiteParserInterface>
	{

		public PreTokenIterator(String key)
		{
			super(CDOMOBJECT_CLASS, key);
		}

		@Override
		protected PrerequisiteParserInterface grabToken(TokenFamily family, Class<?> cl, String key)
		{
			return family.getPrerequisiteToken(key);
		}

	}

	/**
	 * Add a CLASS via a BONUS.
	 *
	 * @param bonusClass the bonus class
	 * @return true if successful
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static boolean addBonusClass(Class bonusClass) throws InstantiationException, IllegalAccessException
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

	/**
	 * Returns the CDOMInterfaceToken of the given name. null is returned if there is no
	 * CDOMInterfaceToken of the given name.
	 * 
	 * @param name
	 *            The name of the CDOMInterfaceToken to be returned
	 * @return The CDOMInterfaceToken of the given name
	 */
	public static CDOMInterfaceToken<?, ?> getInterfaceToken(String name)
	{
		return IF_TOKEN_MAP.get(name);
	}

	/**
	 * Returns a Collection of the CDOMInterfaceToken objects in this TokenLibrary.
	 * 
	 * @return A Collection of the CDOMInterfaceToken objects in this TokenLibrary
	 */
	public static Collection<CDOMInterfaceToken<?, ?>> getInterfaceTokens()
	{
		return Collections.unmodifiableCollection(IF_TOKEN_MAP.values());
	}
}
