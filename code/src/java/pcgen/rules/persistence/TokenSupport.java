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

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.Loadable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary.SubTokenIterator;
import pcgen.rules.persistence.TokenLibrary.TokenIterator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.util.TokenFamily;
import pcgen.rules.persistence.util.TokenFamilyIterator;
import pcgen.rules.persistence.util.TokenFamilySubIterator;
import pcgen.util.Logging;

public class TokenSupport
{
	public DoubleKeyMapToList<Class<?>, String, CDOMToken<?>> tokenCache =
		new DoubleKeyMapToList<Class<?>, String, CDOMToken<?>>();

	public TripleKeyMapToList<Class<?>, String, String, CDOMToken<?>> subTokenCache =
		new TripleKeyMapToList<Class<?>, String, String, CDOMToken<?>>();

	public <T extends Loadable> boolean processToken(LoadContext context,
		T derivative, String typeStr, String argument)
		throws PersistenceLayerException
	{
		Class<T> cl = (Class<T>) derivative.getClass();
		List<? extends CDOMToken<T>> tokenList = getTokens(cl, typeStr);
		if (tokenList != null)
		{
			for (CDOMToken<T> token : tokenList)
			{
				ParseResult parse = token.parseToken(context, derivative, argument);
				// Need to add messages as there may be warnings.
				parse.addMessagesToLog();
				if (parse.passed())
				{
					return true;
				}
				Logging.addParseMessage(Logging.LST_INFO,
					"Failed in parsing typeStr: " + typeStr + " " + argument);
			}
		}
		Logging.addParseMessage(Logging.LST_ERROR, "Illegal Token '" + typeStr
			+ "' '" + argument + "' for " + cl.getName() + " "
			+ derivative.getDisplayName() + " in " + context.getObjectContext().getSourceURI());
		return false;
	}

	public <T extends Loadable> List<? extends CDOMToken<T>> getTokens(Class<T> cl, String name)
	{
		List list = tokenCache.getListFor(cl, name);
		if (list == null)
		{
			for (Iterator<? extends CDOMToken<T>> it =
				new TokenIterator<T, CDOMToken<T>>(cl, name); it.hasNext();)
			{
				CDOMToken<T> token = it.next();
				tokenCache.addToListFor(cl, name, token);
			}
			list = tokenCache.getListFor(cl, name);
		}
		return list;
	}

	public <T> List<? extends CDOMToken<T>> getTokens(
		Class<T> cl, String name, String subtoken)
	{
		List list = subTokenCache.getListFor(cl, name, subtoken);
		if (list == null)
		{
			for (Iterator<CDOMSubToken<T>> it =
					new SubTokenIterator<T, CDOMSubToken<T>>(cl, name, subtoken); it
				.hasNext();)
			{
				CDOMToken<T> token = it.next();
				subTokenCache.addToListFor(cl, name, subtoken, token);
			}
			list = subTokenCache.getListFor(cl, name, subtoken);
		}
		return list;
	}

	public <T> ParseResult processSubToken(LoadContext context, T cdo,
		String tokenName, String key, String value)
	{
		ComplexParseResult cpr = new ComplexParseResult();
		List<? extends CDOMToken<T>> tokenList = getTokens((Class<T>) cdo.getClass(), tokenName, key);
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
		 * CONSIDER Better option than toString, given that T != CDOMObject
		 */
		cpr.addErrorMessage("Illegal " + tokenName
			+ " subtoken '" + key + "' '" + value + "' for " + cdo.toString());
		return cpr;
	}

	public <T> String[] unparseSubtoken(LoadContext context, T cdo, String tokenName)
	{
		char separator = tokenName.charAt(0) == '*' ? ':' : '|';
		Collection<String> set = new WeightedCollection<String>(
				String.CASE_INSENSITIVE_ORDER);
		Class<T> cl = (Class<T>) cdo.getClass();
		TokenFamilySubIterator<T> it =
				new TokenFamilySubIterator<T>(cl, tokenName);
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
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public <T> Collection<String> unparse(LoadContext context, T cdo)
	{
		Collection<String> set = new WeightedCollection<String>(
				String.CASE_INSENSITIVE_ORDER);
		Class<T> cl = (Class<T>) cdo.getClass();
		TokenFamilyIterator<T> it = new TokenFamilyIterator<T>(cl);
		while (it.hasNext())
		{
			CDOMPrimaryToken<? super T> token = it.next();
			String[] s = token.unparse(context, cdo);
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

	/**
	 * Produce the LST code for any occurrences of the token. An attempt to 
	 * unparse an invalid or non-existent token will result in an 
	 * IllegalArgumentError.
	 *  
	 * @param loadContext The load context to be used
	 * @param cdo The object to be partially unparsed
	 * @param tokenName The name of the token to be extracted, must be a primary token.
	 * @param <T> The type of object to be processed, generally a CDOMObject.
	 * @return An array of LST code 'fields' being each occurrence of the token for the target object.
	 */
	public <T> String[] unparseToken(LoadContext loadContext, T cdo, String tokenName)
	{
		List<String> result = new ArrayList<String>();
		Class<?> cl = cdo.getClass();
		CDOMToken<?> token = null;
		while (token == null)
		{
			token = TokenFamily.CURRENT.getToken(cl, tokenName);
			if (token == null)
			{
				if (Object.class.equals(cl))
				{
					return null;
				}
				cl = cl.getSuperclass();
			}

		}
		if (token != null && CDOMPrimaryToken.class.isAssignableFrom(token.getClass()))
		{
			CDOMPrimaryToken<? super T> primaryToken = (CDOMPrimaryToken<? super T>) token;
			String[] s = primaryToken.unparse(loadContext, cdo);
			if (s != null)
			{
				for (String aString : s)
				{
					result.add(token.getTokenName() + ':' + aString);
				}
			}
		}
		else if (token == null)
		{
			throw new IllegalArgumentException("The token " + tokenName
				+ " could not be found.");
		}
		else
		{
			throw new IllegalArgumentException(
				"Expected a primary token in unparseToken, but " + tokenName
					+ " - " + token.getClass().getName() + " is not a CDOMPrimaryToken.");
		}
		if (result.isEmpty())
		{
			return null;
		}
		return result.toArray(new String[]{});
	}
}
