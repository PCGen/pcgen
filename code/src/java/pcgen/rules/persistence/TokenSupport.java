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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pcgen.base.proxy.DeferredMethodController;
import pcgen.base.proxy.StagingInfo;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.ProxyUtilities;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.GroupDefinition;
import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary.SubTokenIterator;
import pcgen.rules.persistence.TokenLibrary.TokenIterator;
import pcgen.rules.persistence.token.CDOMInterfaceToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.util.Revision;
import pcgen.rules.persistence.util.TokenFamily;
import pcgen.rules.persistence.util.TokenFamilyIterator;
import pcgen.rules.persistence.util.TokenFamilySubIterator;
import pcgen.util.Logging;

public class TokenSupport
{
	private final TokenFamily localTokens = new TokenFamily(new Revision(0, 0, 0));

	private final DoubleKeyMapToList<Class<?>, String, CDOMToken<?>> tokenCache = new DoubleKeyMapToList<>();

	private final TripleKeyMapToList<Class<?>, String, String, CDOMToken<?>> subTokenCache =
			new TripleKeyMapToList<>(HashMap.class, CaseInsensitiveMap.class, CaseInsensitiveMap.class);

	/**
	 * Processes the given token information in the scope of the given LoadContext and
	 * object.
	 * 
	 * @param context
	 *            The LoadContext to support how the token is processed
	 * @param target
	 *            The object on which the token will be processed
	 * @param tokenName
	 *            The name of the token to be processed
	 * @param tokenValue
	 *            The value of the token to be processed
	 * @return true if the parsing was successful; false otherwise
	 */
	public <T extends Loadable> boolean processToken(LoadContext context, T target, String tokenName, String tokenValue)
	{
		//Interface tokens override everything else... even if NOT VALID!
		CDOMInterfaceToken<?, ?> interfaceToken = TokenLibrary.getInterfaceToken(tokenName);
		if (interfaceToken != null)
		{
			Class<? extends Loadable> targetClass = target.getClass();
			if (interfaceToken.getTokenClass().isAssignableFrom(targetClass)
				&& interfaceToken.getReadInterface().isAssignableFrom(targetClass))
			{
				return processInterfaceToken(context, target, tokenName, tokenValue, interfaceToken);
			}
			//We want to fall through to class tokens if the interface token isn't usable
		}
		return processClassTokens(context, target, tokenName, tokenValue);
	}

	private <T extends Loadable> boolean processClassTokens(LoadContext context, T target, String tokenName,
		String tokenValue)
	{
		//Must be true
		@SuppressWarnings("unchecked")
		Class<T> cl = (Class<T>) target.getClass();
		List<? extends CDOMToken<T>> tokenList = getTokens(cl, tokenName);
		if (tokenList != null)
		{
			for (CDOMToken<T> token : tokenList)
			{
				ParseResult parse;
				try
				{
					parse = token.parseToken(context, target, tokenValue);
				}
				catch (IllegalArgumentException e)
				{
					Logging.errorPrint("IllegalArgumentException", e);
					Logging.addParseMessage(Logging.LST_ERROR,
						"Token generated an IllegalArgumentException: " + e.getLocalizedMessage());
					parse = new ParseResult.Fail("Token processing failed");
				}
				// Need to add messages as there may be warnings.
				parse.addMessagesToLog(context.getSourceURI());
				if (parse.passed())
				{
					return true;
				}
				if (Logging.isLoggable(Logging.LST_ERROR))
				{
					Logging.addParseMessage(Logging.LST_ERROR, "Failed in parsing typeStr: " + tokenName + ' '
						+ tokenValue + " for " + cl.getName() + ' ' + target.getDisplayName());
				}
			}
		}
		if (tokenName.startsWith(" "))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"Illegal whitespace at start of token '" + tokenName + "' '" + tokenValue + "' for " + cl.getName()
					+ ' ' + target.getDisplayName() + " in " + context.getSourceURI());
		}
		else
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Illegal Token '" + tokenName + "' '" + tokenValue + "' for "
				+ cl.getName() + ' ' + target.getDisplayName() + " in " + context.getSourceURI());
		}
		return false;
	}

	private <R, W> boolean processInterfaceToken(LoadContext context, Object target,
		String tokenName, String tokenValue, CDOMInterfaceToken<R, W> interfaceToken)
	{
		//Suppressed as we checked this before this method is called
		@SuppressWarnings("unchecked")
		StagingInfo<R, W> info =
				ProxyUtilities.getStagingFactory().produceStaging(
					interfaceToken.getReadInterface(), interfaceToken.getTokenClass(), (R) target);
		ParseResult parse;
		try
		{
			parse = interfaceToken.parseToken(context, info.getWriteProxy(), tokenValue);
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(e.getLocalizedMessage(), e);
			Logging.addParseMessage(Logging.LST_ERROR,
				"Token generated an IllegalArgumentException: " + e.getLocalizedMessage());
			parse = new ParseResult.Fail("Token processing failed");
		}
		// Need to add messages as there may be warnings.
		parse.addMessagesToLog(context.getSourceURI());
		if (parse.passed())
		{
			//Suppressed as we checked this before this method is called
			@SuppressWarnings("unchecked")
			DeferredMethodController<W> controller =
					new DeferredMethodController<>(info.getStagingObject(), (W) target);
			context.addDeferredMethodController(controller);
			return true;
		}
		if (Logging.isLoggable(Logging.LST_INFO))
		{
			Logging.addParseMessage(Logging.LST_INFO, "Failed in parsing token: " + tokenName + ' ' + tokenValue
				+ " for " + target.getClass().getName() + ' ' + target);
		}
		return false;
	}

	private <T extends Loadable> List<? extends CDOMToken<T>> getTokens(Class<T> cl, String name)
	{
		List list = tokenCache.getListFor(cl, name);
		if (list == null)
		{
			CDOMToken<?> local = localTokens.getToken(cl, name);
			if (local != null)
			{
				tokenCache.addToListFor(cl, name, local);
			}
			for (Iterator<? extends CDOMToken<T>> it = new TokenIterator<>(cl, name); it.hasNext();)
			{
				CDOMToken<T> token = it.next();
				tokenCache.addToListFor(cl, name, token);
			}
			list = tokenCache.getListFor(cl, name);
		}
		return list;
	}

	private <T> List<? extends CDOMToken<T>> getTokens(Class<T> cl, String name, String subtoken)
	{
		List list = subTokenCache.getListFor(cl, name, subtoken);
		if (list == null)
		{
			CDOMToken<?> local = localTokens.getSubToken(cl, name, subtoken);
			if (local != null)
			{
				subTokenCache.addToListFor(cl, name, subtoken, local);
			}
			for (Iterator<CDOMSubToken<T>> it = new SubTokenIterator<>(cl, name, subtoken); it.hasNext();)
			{
				CDOMToken<T> token = it.next();
				subTokenCache.addToListFor(cl, name, subtoken, token);
			}
			list = subTokenCache.getListFor(cl, name, subtoken);
		}
		return list;
	}

	public <T> ParseResult processSubToken(LoadContext context, T cdo, String tokenName, String key, String value)
	{
		ComplexParseResult cpr = new ComplexParseResult();
		Class<T> cl = (Class<T>) cdo.getClass();
		List<? extends CDOMToken<T>> tokenList = getTokens(cl, tokenName, key);
		if (tokenList != null)
		{
			for (CDOMToken<T> token : tokenList)
			{
				ParseResult pr = token.parseToken(context, cdo, value);
				if (pr.passed())
				{
					return pr;
				}
				cpr.copyMessages(pr);
				cpr.addErrorMessage("Failed in parsing subtoken: " + key + " of " + value);
			}
		}
		/*
		 * CONSIDER a better option than toString, given that T != CDOMObject
		 */
		cpr.addErrorMessage(
			"Illegal " + tokenName + " subtoken '" + key + "' '" + value + "' for " + cl.getName() + ' ' + cdo);
		return cpr;
	}

	public <T> String[] unparseSubtoken(LoadContext context, T cdo, String tokenName)
	{
		char separator = tokenName.charAt(0) == '*' ? ':' : '|';
		Collection<String> set = new WeightedCollection<>(String.CASE_INSENSITIVE_ORDER);
		Class<T> cl = (Class<T>) cdo.getClass();
		TokenFamilySubIterator<T> it = new TokenFamilySubIterator<>(cl, tokenName);
		while (it.hasNext())
		{
			CDOMSecondaryToken<? super T> token = it.next();
			String[] s = token.unparse(context, cdo);
			if (s != null)
			{
				for (String aString : s)
				{
					set.add(token.getTokenName() + separator + aString);
				}
			}
		}
		Set<CDOMSecondaryToken<? super T>> local = localTokens.getSubTokens(cl, tokenName);
		for (CDOMSecondaryToken<? super T> token : local)
		{
			String[] s = token.unparse(context, cdo);
			if (s != null)
			{
				for (String aString : s)
				{
					set.add(token.getTokenName() + separator + aString);
				}
			}
		}

		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[0]);
	}

	/**
	 * Unparses an object into the LST tokens that would build the object.
	 * 
	 * @param context
	 *            The LoadContext used to interpret the contents of the object
	 * @param loadable
	 *            The Loadable object to be unparsed
	 * @return A Collection of Strings indicating the tokens that would build the object
	 */
	public <T extends Loadable, R extends Loadable> Collection<String> unparse(LoadContext context, T loadable)
	{
		Collection<String> set = new WeightedCollection<>(String.CASE_INSENSITIVE_ORDER);
		@SuppressWarnings("unchecked")
		Class<T> cl = (Class<T>) loadable.getClass();
		for (CDOMInterfaceToken<?, ?> interfaceToken : TokenLibrary.getInterfaceTokens())
		{
			if (interfaceToken.getReadInterface().isAssignableFrom(cl))
			{
				@SuppressWarnings("unchecked")
				CDOMInterfaceToken<R, T> token = (CDOMInterfaceToken<R, T>) interfaceToken;
				@SuppressWarnings("unchecked")
				String[] s = token.unparse(context, (R) loadable);
				if (s != null)
				{
					for (String aString : s)
					{
						set.add(token.getTokenName() + ':' + aString);
					}
				}
			}
		}
		TokenFamilyIterator<T> it = new TokenFamilyIterator<>(cl);
		while (it.hasNext())
		{
			CDOMPrimaryToken<? super T> token = it.next();
			String[] s = token.unparse(context, loadable);
			if (s != null)
			{
				for (String aString : s)
				{
					set.add(token.getTokenName() + ':' + aString);
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set;
	}

	public Collection<DeferredToken<? extends Loadable>> getDeferredTokens()
	{
		List<DeferredToken<? extends Loadable>> c = new ArrayList<>();
		c.addAll(localTokens.getDeferredTokens());
		c.addAll(TokenFamily.CURRENT.getDeferredTokens());
		return c;
	}

	public void loadLocalToken(Object token)
	{
		TokenLibrary.loadFamily(localTokens, token);
	}

	public <T> GroupDefinition<T> getGroup(Class<T> cl, String s)
	{
		return localTokens.getGroup(cl, s);
	}
}
