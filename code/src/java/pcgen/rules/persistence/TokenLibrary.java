package pcgen.rules.persistence;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.PreCompatibilityToken;
import pcgen.rules.persistence.util.TokenFamily;

public class TokenLibrary
{
//	private static final Class<CDOMPCClass> PCCLASS_CLASS = CDOMPCClass.class;

	static final Class<CDOMObject> CDOMOBJECT_CLASS = CDOMObject.class;

//	private final static List<DeferredToken<? extends CDOMObject>> deferredTokens = new ArrayList<DeferredToken<? extends CDOMObject>>();
//
//	private final static DoubleKeyMap<Class<?>, String, Class<ChooseLstQualifierToken<?>>> qualifierMap = new DoubleKeyMap<Class<?>, String, Class<ChooseLstQualifierToken<?>>>();
//
//	private final static DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>> primitiveMap = new DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>>();

	private final static Set<TokenFamily> tokenSources = new TreeSet<TokenFamily>();

	static
	{
		tokenSources.add(TokenFamily.CURRENT);
		tokenSources.add(TokenFamily.REV514);
	}

//	public static <T> PrimitiveToken<T> getPrimitive(Class<T> name,
//			String tokKey)
//	{
//		Class<PrimitiveToken<?>> cptc = primitiveMap.get(name, tokKey);
//		if (cptc == null)
//		{
//			return null;
//		}
//		try
//		{
//			return (PrimitiveToken<T>) cptc.newInstance();
//		}
//		catch (InstantiationException e)
//		{
//			throw new UnreachableError("new Instance on " + cptc
//					+ " should not fail in getPrimitive", e);
//		}
//		catch (IllegalAccessException e)
//		{
//			throw new UnreachableError("new Instance on " + cptc
//					+ " should not fail due to access", e);
//		}
//	}
//
//	public static <T extends CDOMObject> ChooseLstQualifierToken<T> getChooseQualifier(
//			Class<T> domain_class, String key)
//	{
//		Class<ChooseLstQualifierToken<?>> clqtc = qualifierMap.get(
//				domain_class, key);
//		if (clqtc == null)
//		{
//			return null;
//		}
//		try
//		{
//			return (ChooseLstQualifierToken<T>) clqtc.newInstance();
//		}
//		catch (InstantiationException e)
//		{
//			throw new UnreachableError("new Instance on " + clqtc
//					+ " should not fail in getChooseQualifier", e);
//		}
//		catch (IllegalAccessException e)
//		{
//			throw new UnreachableError("new Instance on " + clqtc
//					+ " should not fail due to access", e);
//		}
//	}
//
//	public static List<DeferredToken<? extends CDOMObject>> getDeferredTokens()
//	{
//		return new ArrayList<DeferredToken<? extends CDOMObject>>(
//				deferredTokens);
//	}
//
//	public static void addToPrimitiveMap(PrimitiveToken<?> p)
//	{
//		Class<? extends PrimitiveToken> newTokClass = p.getClass();
//		if (PrimitiveToken.class.isAssignableFrom(newTokClass))
//		{
//			String name = p.getTokenName();
//			Class cl = ((PrimitiveToken) p).getReferenceClass();
//			Class<PrimitiveToken<?>> prev = primitiveMap.put(cl, name,
//					(Class<PrimitiveToken<?>>) newTokClass);
//			if (prev != null)
//			{
//				Logging.errorPrint("Found a second " + name + " Primitive for "
//						+ cl);
//			}
//		}
//	}
//
//	public static void addToQualifierMap(QualifierToken<?> p)
//	{
//		Class<? extends QualifierToken> newTokClass = p.getClass();
//		if (ChooseLstQualifierToken.class.isAssignableFrom(newTokClass))
//		{
//			Class<?> cl = ((ChooseLstQualifierToken<?>) p).getChoiceClass();
//			String name = p.getTokenName();
//			Class<ChooseLstQualifierToken<?>> prev = qualifierMap.put(cl, name,
//					(Class<ChooseLstQualifierToken<?>>) newTokClass);
//			if (prev != null)
//			{
//				Logging.errorPrint("Found a second " + name + " Primitive for "
//						+ cl);
//			}
//		}
//	}

	public static void addToTokenMap(Object newToken)
	{
//		if (newToken instanceof DeferredToken)
//		{
//			deferredTokens.add((DeferredToken<?>) newToken);
//		}
		if (newToken instanceof CDOMPrimaryToken)
		{
			CDOMPrimaryToken<?> tok = (CDOMPrimaryToken<?>) newToken;
			TokenFamily.CURRENT.putToken(tok);
//			if (PCCLASS_CLASS.equals(tok.getTokenClass()))
//			{
//				addToTokenMap(new ClassWrappedToken(
//						(CDOMPrimaryToken<CDOMPCClass>) tok));
//			}
		}
		if (newToken instanceof CDOMSecondaryToken)
		{
			TokenFamily.CURRENT.putSubToken((CDOMSecondaryToken<?>) newToken);
		}
//		if (newToken instanceof ChoiceSetToken)
//		{
//			TokenFamily.CURRENT.putChooseToken((ChoiceSetToken<?>) newToken);
//		}
		if (newToken instanceof PrerequisiteParserInterface)
		{
			PrerequisiteParserInterface prereqToken = (PrerequisiteParserInterface) newToken;
			TokenFamily.CURRENT.putPrerequisiteToken(prereqToken);
			for (String s : prereqToken.kindsHandled())
			{
				/*
				 * TODO Theoretically these belong in REV514, but put into
				 * current for unparse testing
				 */
				PreCompatibilityToken pos = new PreCompatibilityToken(s,
						prereqToken, false);
				TokenFamily.CURRENT.putToken(pos);
				TokenFamily.CURRENT.putSubToken(pos);
				PreCompatibilityToken neg = new PreCompatibilityToken(s,
						prereqToken, true);
				TokenFamily.CURRENT.putToken(neg);
				TokenFamily.CURRENT.putSubToken(neg);
			}
		}
//		if (newToken instanceof CDOMCompatibilityToken)
//		{
//			CDOMCompatibilityToken<?> tok = (CDOMCompatibilityToken<?>) newToken;
//			TokenFamily fam = TokenFamily.getConstant(tok.compatibilityLevel(),
//					tok.compatibilitySubLevel(), tok.compatibilityPriority());
//			fam.putToken(tok);
//			tokenSources.add(fam);
//			if (fam.compareTo(TokenFamily.REV514) <= 0
//					&& PCCLASS_CLASS.equals(tok.getTokenClass()))
//			{
//				addToTokenMap(new ClassWrappedToken(
//						(CDOMCompatibilityToken<CDOMPCClass>) tok));
//			}
//		}
//		if (newToken instanceof CDOMCompatibilitySubToken)
//		{
//			CDOMCompatibilitySubToken<?> tok = (CDOMCompatibilitySubToken<?>) newToken;
//			TokenFamily fam = TokenFamily.getConstant(tok.compatibilityLevel(),
//					tok.compatibilitySubLevel(), tok.compatibilityPriority());
//			fam.putSubToken(tok);
//			tokenSources.add(fam);
//		}
//		if (newToken instanceof ChoiceSetCompatibilityToken)
//		{
//			ChoiceSetCompatibilityToken tok = (ChoiceSetCompatibilityToken) newToken;
//			TokenFamily fam = TokenFamily.getConstant(tok.compatibilityLevel(),
//					tok.compatibilitySubLevel(), tok.compatibilityPriority());
//			fam.putChooseToken(tok);
//			tokenSources.add(fam);
//		}
	}

	abstract static class AbstractTokenIterator<C, T> implements Iterator<T>
	{
//		private static final Class<Object> OBJECT_CLASS = Object.class;
		private final Class<C> rootClass;
		private final String tokenKey;
		private T nextToken = null;
		private boolean needNewToken = true;
		private Class<?> stopClass;
		private final Iterator<TokenFamily> subIterator;

		public AbstractTokenIterator(Class<C> cl, String key)
		{
			rootClass = cl;
			subIterator = tokenSources.iterator();
			tokenKey = key;
		}

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
				while (nextToken == null && actingClass != null
						&& !actingClass.equals(stopClass))
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

		public void remove()
		{
			throw new UnsupportedOperationException(
					"Iterator does not support remove");
		}
	}

	static class TokenIterator<C extends CDOMObject, T extends CDOMToken<? super C>>
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

//	static class ChooseTokenIterator<C extends CDOMObject> extends
//			TokenLibrary.AbstractTokenIterator<C, ChoiceSetToken<? super C>>
//	{
//		public ChooseTokenIterator(Class<C> cl, String key)
//		{
//			super(cl, key);
//		}
//
//		@Override
//		protected ChoiceSetToken<? super C> grabToken(TokenFamily family,
//				Class<?> cl, String key)
//		{
//			return (ChoiceSetToken<? super C>) family.getChooseToken(cl, key);
//		}
//	}

//	static class QualifierTokenIterator<C extends CDOMObject, T extends ChooseLstQualifierToken<? super C>>
//			extends TokenLibrary.AbstractTokenIterator<C, T>
//	{
//		public QualifierTokenIterator(Class<C> cl, String key)
//		{
//			super(cl, key);
//		}
//
//		@Override
//		protected T grabToken(TokenFamily family, Class<?> cl, String key)
//		{
//			if (!TokenFamily.CURRENT.equals(family))
//			{
//				return null;
//			}
//			Class<ChooseLstQualifierToken<?>> cl1 = qualifierMap.get(cl, key);
//			if (cl1 == null)
//			{
//				return null;
//			}
//			try
//			{
//				return (T) cl1.newInstance();
//			}
//			catch (InstantiationException e)
//			{
//				throw new UnreachableError("new Instance on " + cl1
//						+ " should not fail", e);
//			}
//			catch (IllegalAccessException e)
//			{
//				throw new UnreachableError("new Instance on " + cl1
//						+ " should not fail due to access", e);
//			}
//		}
//	}

	static class PreTokenIterator
			extends
			TokenLibrary.AbstractTokenIterator<CDOMObject, PrerequisiteParserInterface>
	{
		public PreTokenIterator(String key)
		{
			super(CDOMOBJECT_CLASS, key);
		}

		@Override
		protected PrerequisiteParserInterface grabToken(TokenFamily family,
				Class<?> cl, String key)
		{
			return family.getPrerequisiteToken(key);
		}
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

}
