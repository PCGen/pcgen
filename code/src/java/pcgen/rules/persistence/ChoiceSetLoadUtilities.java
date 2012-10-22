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
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.primitive.CompoundAndPrimitive;
import pcgen.cdom.primitive.CompoundOrPrimitive;
import pcgen.cdom.primitive.NegatingPrimitive;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.PatternMatchingReference;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.utils.ParsingSeparator;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary.QualifierTokenIterator;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public final class ChoiceSetLoadUtilities
{
	
	private ChoiceSetLoadUtilities()
	{
		//Don't instantiate utility class
	}

	public static <T extends CDOMObject> PrimitiveCollection<T> getChoiceSet(
			LoadContext context, SelectionCreator<T> sc, String joinedOr)
	{
		/*
		 * TODO Need to check why this was in CDOM branch - does it work without
		 * it? - it should!
		 */
		// if (joinedOr.equals(Constants.LST_ANY) ||
		// joinedOr.equals(Constants.LST_ALL))
		// {
		// /*
		// * TODO Categorized items break here :(
		// */
		// return new AnyChoiceSet<T>(poClass);
		// }
		List<PrimitiveCollection<T>> orList = new ArrayList<PrimitiveCollection<T>>();
		for (ParsingSeparator pipe = new ParsingSeparator(joinedOr, '|'); pipe
				.hasNext();)
		{
			String joinedAnd = pipe.next();
			if (hasIllegalSeparator(',', joinedAnd))
			{
				return null;
			}
			List<PrimitiveCollection<T>> andList = new ArrayList<PrimitiveCollection<T>>();
			for (ParsingSeparator comma = new ParsingSeparator(joinedAnd, ','); comma
					.hasNext();)
			{
				String primitive = comma.next();
				if (primitive == null || primitive.length() == 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Choice argument was null or empty: " + primitive);
					return null;
				}
				QualifierToken<T> qual = getQualifier(context, sc,
						primitive);
				if (qual == null)
				{
					PrimitiveCollection<T> pcf = getSimplePrimitive(context,
							sc, primitive);
					if (pcf == null)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
								"Choice argument was not valid: " + primitive);
						return null;
					}
					else
					{
						andList.add(pcf);
					}
				}
				else
				{
					andList.add(qual);
				}
			}
			if (!andList.isEmpty())
			{
				if (andList.size() == 1)
				{
					orList.add(andList.get(0));
				}
				else
				{
					orList.add(new CompoundAndPrimitive<T>(andList));
				}
			}
		}
		if (orList.isEmpty())
		{
			return null;
		}
		else if (orList.size() == 1)
		{
			return orList.get(0);
		}
		else
		{
			return new CompoundOrPrimitive<T>(orList);
		}
	}

	protected static boolean hasIllegalSeparator(char separator, String value)
	{
		if (value.charAt(0) == separator)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Choice arguments may not start with " + separator + " : "
							+ value);
			return true;
		}
		if (value.charAt(value.length() - 1) == separator)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Choice arguments may not end with " + separator + " : "
							+ value);
			return true;
		}
		if (value.indexOf(String.valueOf(new char[] { separator, separator })) != -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Choice arguments uses double separator " + separator
							+ separator + " : " + value);
			return true;
		}
		return false;
	}

	public static <T extends CDOMObject> PrimitiveCollection<T> getPrimitive(
			LoadContext context, SelectionCreator<T> sc, String joinedOr)
	{
		if (joinedOr.length() == 0 || hasIllegalSeparator('|', joinedOr))
		{
			return null;
		}
		List<PrimitiveCollection<T>> pcfOrList = new ArrayList<PrimitiveCollection<T>>();
		for (ParsingSeparator pipe = new ParsingSeparator(joinedOr, '|'); pipe
				.hasNext();)
		{
			String joinedAnd = pipe.next();
			if (joinedAnd.length() == 0 || hasIllegalSeparator(',', joinedAnd))
			{
				return null;
			}
			List<PrimitiveCollection<T>> pcfAndList = new ArrayList<PrimitiveCollection<T>>();
			for (ParsingSeparator comma = new ParsingSeparator(joinedAnd, ','); comma
					.hasNext();)
			{
				String primitive = comma.next();
				if (primitive == null || primitive.length() == 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Choice argument was null or empty: " + primitive);
					return null;
				}
				PrimitiveCollection<T> pcf = getSimplePrimitive(context,
						sc, primitive);
				if (pcf == null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Choice argument was not valid: " + primitive);
					return null;
				}
				else
				{
					pcfAndList.add(pcf);
				}
			}
			if (pcfAndList.size() == 1)
			{
				pcfOrList.add(pcfAndList.get(0));
			}
			else
			{
				pcfOrList.add(new CompoundAndPrimitive<T>(pcfAndList));
			}
		}
		if (pcfOrList.size() == 1)
		{
			return pcfOrList.get(0);
		}
		else
		{
			return new CompoundOrPrimitive<T>(pcfOrList);
		}
	}

	public static PrimitiveInfo getPrimitiveInfo(String key)
	{
		int openBracketLoc = key.indexOf('[');
		int closeBracketLoc = key.indexOf(']');
		int equalLoc = key.indexOf('=');
		PrimitiveInfo pi = new PrimitiveInfo();
		pi.key = key;
		if (openBracketLoc == -1)
		{
			if (closeBracketLoc != -1)
			{
				Logging.errorPrint("Found error in Primitive Choice: " + key
						+ " has a close bracket but no open bracket");
				return null;
			}
			if (equalLoc == -1)
			{
				pi.tokKey = key;
				pi.tokValue = null;
			}
			else
			{
				pi.tokKey = key.substring(0, equalLoc);
				pi.tokValue = key.substring(equalLoc + 1);
				if (pi.tokValue.length() == 0)
				{
					Logging.errorPrint("Found error in Primitive Choice: " + key
							+ " has equals but no target value");
					return null;
				}
			}
			pi.tokRestriction = null;
		}
		else
		{
			if (closeBracketLoc == -1)
			{
				Logging.errorPrint("Found error in Primitive Choice: " + key
						+ " has an open bracket but no close bracket");
				return null;
			}
			if (closeBracketLoc != key.length() - 1)
			{
				Logging.errorPrint("Found error in Primitive Choice: " + key
						+ " had close bracket, but had characters "
						+ "following the close bracket");
				return null;
			}
			if (equalLoc == -1 || equalLoc > openBracketLoc)
			{
				pi.tokKey = key.substring(0, openBracketLoc);
				pi.tokValue = null;
				pi.tokRestriction = key.substring(openBracketLoc + 1,
						closeBracketLoc);
			}
			else
			{
				pi.tokKey = key.substring(0, equalLoc);
				pi.tokValue = key.substring(equalLoc + 1, openBracketLoc);
				pi.tokRestriction = key.substring(openBracketLoc + 1,
						closeBracketLoc);
			}
		}
		return pi;
	}

	public static <T extends Loadable> PrimitiveCollection<T> getSimplePrimitive(
			LoadContext context, SelectionCreator<T> sc, String key)
	{
		PrimitiveInfo pi = getPrimitiveInfo(key);
		if (pi == null)
		{
			return null;
		}
		PrimitiveCollection<T> prim = getTokenPrimitive(context, sc
				.getReferenceClass(), pi);
		if (prim == null)
		{
			return getTraditionalPrimitive(sc, pi);
		}
		return prim;
	}

	public static <T> PrimitiveCollection<T> getTokenPrimitive(
			LoadContext context, Class<T> cl, PrimitiveInfo pi)
	{
		PrimitiveToken<T> prim = TokenLibrary.getPrimitive(cl, pi.tokKey);
		if (prim != null)
		{
			if (!prim.initialize(context, cl, pi.tokValue, pi.tokRestriction))
			{
				return null;
			}
		}
		return prim;
	}

	public static <T extends Loadable> PrimitiveCollection<T> getTraditionalPrimitive(
			SelectionCreator<T> sc, PrimitiveInfo pi)
	{
		String tokKey = pi.tokKey;
		String tokValue = pi.tokValue;
		if (pi.tokRestriction != null)
		{
			Logging.errorPrint("Didn't expect tokRestriction on " + tokKey
				+ " here: " + pi.tokRestriction);
			return null;
		}
		if ("TYPE".equals(tokKey))
		{
			return TokenUtilities.getTypeReference(sc, tokValue);
		}
		if ("!TYPE".equals(tokKey))
		{
			CDOMGroupRef<T> typeReference = TokenUtilities
					.getTypeReference(sc, tokValue);
			if (typeReference == null)
			{
				return null;
			}
			return new NegatingPrimitive<T>(typeReference, sc.getAllReference());
		}
		if (tokValue != null)
		{
			Logging.errorPrint("Didn't expect Arguments here: " + tokValue
					+ " was found in " + pi.key);
		}
		if ("ALL".equals(tokKey))
		{
			return sc.getAllReference();
		}
		String key = pi.key;
		if (key.startsWith(Constants.LST_TYPE_DOT))
		{
			return TokenUtilities.getTypeReference(sc, key
					.substring(5));
		}
		if (key.startsWith(Constants.LST_NOT_TYPE_DOT))
		{
			return new NegatingPrimitive<T>(TokenUtilities.getTypeReference(sc,
				key.substring(6)), sc.getAllReference());
		}
		if (key.indexOf('%') != -1)
		{
			return new PatternMatchingReference<T>(sc.getReferenceClass(),
					sc.getAllReference(), key);
		}
		else
		{
			return sc.getReference(key);
		}
	}
	
	public static class PrimitiveInfo
	{
		public String key;
		public String tokKey;
		public String tokValue;
		public String tokRestriction;
	}

	public static <T extends CDOMObject> QualifierToken<T> getQualifier(
			LoadContext loadContext, SelectionCreator<T> sc, String key)
	{
		if (key == null || key.length() == 0)
		{
			Logging.errorPrint("Found error in Primitive Choice: "
					+ "item was null or empty");
			return null;
		}
		int openBracketLoc = key.indexOf('[');
		int closeBracketLoc = key.indexOf(']');
		int equalLoc = key.indexOf('=');
		boolean startsNot = key.charAt(0) == '!';
		String tokKey;
		String tokValue;
		String tokRestriction;
		if (openBracketLoc == -1)
		{
			if (closeBracketLoc != -1)
			{
				Logging.errorPrint("Found error in Qualifier Choice: " + key
						+ " has a close bracket but no open bracket");
				return null;
			}
			if (equalLoc == -1)
			{
				tokKey = key;
				tokValue = null;
			}
			else
			{
				tokKey = key.substring(0, equalLoc);
				tokValue = key.substring(equalLoc + 1);
			}
			tokRestriction = null;
		}
		else
		{
			if (closeBracketLoc == -1)
			{
				Logging.errorPrint("Found error in Qualifier Choice: " + key
						+ " has an open bracket but no close bracket");
				return null;
			}
			if (closeBracketLoc != key.length() - 1)
			{
				Logging.errorPrint("Found error in Qualifier Choice: " + key
						+ " had close bracket, but had characters "
						+ "following the close bracket");
				return null;
			}
			if (closeBracketLoc - openBracketLoc == 1)
			{
				Logging.errorPrint("Found error in Qualifier Choice: " + key
						+ " has an open bracket "
						+ "immediately followed by close bracket");
				return null;
			}
			if (equalLoc == -1 || equalLoc > openBracketLoc)
			{
				tokKey = key.substring(0, openBracketLoc);
				tokValue = null;
			}
			else
			{
				tokKey = key.substring(0, equalLoc);
				tokValue = key.substring(equalLoc + 1, openBracketLoc);
			}
			tokRestriction = key.substring(openBracketLoc + 1, closeBracketLoc);
		}
		if (startsNot)
		{
			tokKey = tokKey.substring(1);
		}
		for (Iterator<QualifierToken<T>> it = new QualifierTokenIterator<T, QualifierToken<T>>(
				sc.getReferenceClass(), tokKey); it.hasNext();)
		{
			QualifierToken<T> token = it.next();
			if (token.initialize(loadContext, sc, tokValue, tokRestriction,
					startsNot))
			{
				return token;
			}
			Logging.addParseMessage(Logging.LST_ERROR,
					"Failed in parsing typeStr: " + key);
		}
		return null;
	}

}
