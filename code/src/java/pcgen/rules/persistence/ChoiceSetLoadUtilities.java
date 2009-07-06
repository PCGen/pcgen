package pcgen.rules.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.choiceset.CompoundAndChoiceSet;
import pcgen.cdom.choiceset.CompoundOrChoiceSet;
import pcgen.cdom.choiceset.RetainingChooser;
import pcgen.cdom.filter.CompoundAndFilter;
import pcgen.cdom.filter.CompoundOrFilter;
import pcgen.cdom.filter.NegatingFilter;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.PatternMatchingReference;
import pcgen.core.utils.ParsingSeparator;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary.QualifierTokenIterator;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public final class ChoiceSetLoadUtilities
{
	public static <T extends CDOMObject> PrimitiveChoiceSet<T> getChoiceSet(
			LoadContext context, Class<T> poClass, String joinedOr)
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
		List<PrimitiveChoiceSet<T>> orList = new ArrayList<PrimitiveChoiceSet<T>>();
		List<PrimitiveChoiceFilter<T>> pcfOrList = new ArrayList<PrimitiveChoiceFilter<T>>();
		for (ParsingSeparator pipe = new ParsingSeparator(joinedOr, '|'); pipe
				.hasNext();)
		{
			String joinedAnd = pipe.next();
			if (hasIllegalSeparator(',', joinedAnd))
			{
				return null;
			}
			List<PrimitiveChoiceSet<T>> andList = new ArrayList<PrimitiveChoiceSet<T>>();
			List<PrimitiveChoiceFilter<T>> pcfAndList = new ArrayList<PrimitiveChoiceFilter<T>>();
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
				QualifierToken<T> qual = getQualifier(context, poClass,
						primitive);
				if (qual == null)
				{
					PrimitiveChoiceFilter<T> pcf = getSimplePrimitive(context,
							poClass, primitive);
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
				else
				{
					andList.add(qual);
				}
			}
			if (!pcfAndList.isEmpty())
			{
				if (pcfAndList.size() == 1 && andList.isEmpty())
				{
					pcfOrList.add(pcfAndList.get(0));
				}
				else
				{
					RetainingChooser<T> ret = new RetainingChooser<T>(poClass,
							context.ref.getCDOMAllReference(poClass));
					ret.addRetainingChoiceFilter(new CompoundAndFilter<T>(
							pcfAndList));
					andList.add(ret);
				}
			}
			if (andList.size() == 1)
			{
				orList.add(andList.get(0));
			}
			else if (!andList.isEmpty())
			{
				orList.add(new CompoundAndChoiceSet<T>(andList));
			}
		}
		if (!pcfOrList.isEmpty())
		{
			RetainingChooser<T> ret = new RetainingChooser<T>(poClass,
					context.ref.getCDOMAllReference(poClass));
			ret.addAllRetainingChoiceFilters(pcfOrList);
			orList.add(ret);
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
			return new CompoundOrChoiceSet<T>(orList);
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

	public static <T extends CDOMObject> PrimitiveChoiceFilter<T> getPrimitive(
			LoadContext context, Class<T> poClass, String joinedOr)
	{
		if (joinedOr.length() == 0 || hasIllegalSeparator('|', joinedOr))
		{
			return null;
		}
		List<PrimitiveChoiceFilter<T>> pcfOrList = new ArrayList<PrimitiveChoiceFilter<T>>();
		for (ParsingSeparator pipe = new ParsingSeparator(joinedOr, '|'); pipe
				.hasNext();)
		{
			String joinedAnd = pipe.next();
			if (joinedAnd.length() == 0 || hasIllegalSeparator(',', joinedAnd))
			{
				return null;
			}
			List<PrimitiveChoiceFilter<T>> pcfAndList = new ArrayList<PrimitiveChoiceFilter<T>>();
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
				PrimitiveChoiceFilter<T> pcf = getSimplePrimitive(context,
						poClass, primitive);
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
				pcfOrList.add(new CompoundAndFilter<T>(pcfAndList));
			}
		}
		if (pcfOrList.size() == 1)
		{
			return pcfOrList.get(0);
		}
		else
		{
			return new CompoundOrFilter<T>(pcfOrList);
		}
	}

	public static <T extends CDOMObject> PrimitiveChoiceFilter<T> getSimplePrimitive(
			LoadContext context, Class<T> cl, String key)
	{
		int openBracketLoc = key.indexOf('[');
		int closeBracketLoc = key.indexOf(']');
		int equalLoc = key.indexOf('=');
		String tokKey;
		String tokValue;
		String tokRestriction;
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
				tokKey = key.substring(0, openBracketLoc);
				tokValue = null;
				tokRestriction = key.substring(openBracketLoc + 1,
						closeBracketLoc);
			}
			else
			{
				tokKey = key.substring(0, equalLoc);
				tokValue = key.substring(equalLoc + 1, openBracketLoc);
				tokRestriction = key.substring(openBracketLoc + 1,
						closeBracketLoc);
			}
		}
		PrimitiveToken<T> prim = TokenLibrary.getPrimitive(cl, tokKey);
		if (prim == null)
		{
			if (tokRestriction != null)
			{
				Logging.errorPrint("Didn't expect tokRestriction on " + tokKey
					+ " here: " + tokRestriction);
				return null;
			}
			if ("TYPE".equals(tokKey))
			{
				return TokenUtilities.getTypeReference(context, cl, tokValue);
			}
			if ("!TYPE".equals(tokKey))
		{
				CDOMGroupRef<T> typeReference = TokenUtilities
						.getTypeReference(context, cl, tokValue);
				if (typeReference == null)
				{
					return null;
				}
				return new NegatingFilter<T>(typeReference);
			}
			if (tokValue != null)
			{
				Logging.errorPrint("Didn't expect Arguments here: " + tokValue);
			}
			if ("ALL".equals(tokKey))
			{
				return context.ref.getCDOMAllReference(cl);
			}
			if (key.startsWith(Constants.LST_TYPE_OLD))
			{
				return TokenUtilities.getTypeReference(context, cl, key
						.substring(5));
			}
			if (key.startsWith(Constants.LST_NOT_TYPE_OLD))
			{
				return new NegatingFilter<T>(TokenUtilities.getTypeReference(
						context, cl, key.substring(6)));
			}
			if (key.indexOf('%') != -1)
			{
				return new PatternMatchingReference<T>(cl, context.ref
						.getCDOMAllReference(cl), key);
			}
			else
			{
				return context.ref.getCDOMReference(cl, key);
			}
		}
		else
		{
			if (!prim.initialize(context, tokValue, tokRestriction))
			{
				return null;
			}
		}
		return prim;
	}

	public static <T extends CDOMObject> QualifierToken<T> getQualifier(
			LoadContext loadContext, Class<T> cl, String key)
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
				cl, tokKey); it.hasNext();)
		{
			QualifierToken<T> token = it.next();
			if (token.initialize(loadContext, cl, tokValue, tokRestriction,
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
