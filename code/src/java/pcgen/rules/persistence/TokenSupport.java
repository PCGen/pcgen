package pcgen.rules.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary.PreTokenIterator;
import pcgen.rules.persistence.TokenLibrary.SubTokenIterator;
import pcgen.rules.persistence.TokenLibrary.TokenIterator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.util.TokenFamilyIterator;
import pcgen.rules.persistence.util.TokenFamilySubIterator;
import pcgen.util.Logging;

public class TokenSupport
{
	public static final Class<CDOMObject> CDOM_OBJECT_CLASS = CDOMObject.class;

	public <T extends CDOMObject> boolean processToken(LoadContext context,
			T derivative, String typeStr, String argument)
			throws PersistenceLayerException
	{
		Class<T> cl = (Class<T>) derivative.getClass();
		for (Iterator<? extends CDOMToken<T>> it = new TokenIterator<T, CDOMToken<T>>(
				cl, typeStr); it.hasNext();)
		{
			CDOMToken<T> token = it.next();
			if (token.parse(context, derivative, argument))
			{
				return true;
			}
			Logging.addParseMessage(Logging.LST_INFO,
					"Failed in parsing typeStr: " + typeStr + " " + argument);
		}
		Logging.addParseMessage(Logging.LST_ERROR, "Illegal Token '" + typeStr
				+ "' '" + argument + "' for " + cl.getName() + " "
				+ derivative.getDisplayName());
		return false;
	}

	public <T> boolean processSubToken(LoadContext context, T cdo,
			String tokenName, String key, String value)
			throws PersistenceLayerException
	{
		for (Iterator<CDOMSubToken<T>> it = new SubTokenIterator<T, CDOMSubToken<T>>(
				(Class<T>) cdo.getClass(), tokenName, key); it.hasNext();)
		{
			CDOMSubToken<T> token = it.next();
			if (token.parse(context, cdo, value))
			{
				return true;
			}
			Logging.addParseMessage(Logging.LST_ERROR,
					"Failed in parsing typeStr: " + key + " " + value);
		}
		/*
		 * CONSIDER Better option than toString, given that T != CDOMObject
		 */
		Logging.errorPrint("Illegal " + tokenName + " subtoken '" + key + "' '"
				+ value + "' for " + cdo.toString());
		return false;
	}

	public <T> String[] unparse(LoadContext context, T cdo, String tokenName)
	{
		char separator = tokenName.startsWith("*") ? ':' : '|';
		Set<String> set = new TreeSet<String>();
		Class<T> cl = (Class<T>) cdo.getClass();
		TokenFamilySubIterator<T> it = new TokenFamilySubIterator<T>(cl,
				tokenName);
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
		Set<String> set = new TreeSet<String>();
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

	public Prerequisite getPrerequisite(LoadContext context, String key,
			String value) throws PersistenceLayerException
	{
		for (Iterator<PrerequisiteParserInterface> it = new PreTokenIterator(
				key); it.hasNext();)
		{
			PrerequisiteParserInterface token = it.next();
			Prerequisite p = token.parse(key, value, false, false);
			if (p == null)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Failed in parsing Prereq: " + key + " " + value);
			}
			return p;
		}
		Logging.addParseMessage(Logging.LST_ERROR, "Illegal Choice Token '"
				+ key + "' '" + value + "'");
		return null;
	}

//	public <T extends CDOMObject> PrimitiveChoiceSet<T> getChoiceSet(
//			LoadContext context, Class<T> poClass, String joinedOr)
//	{
//		if (joinedOr.equals(Constants.LST_ANY) || joinedOr.equals(Constants.LST_ALL))
//		{
//			/*
//			 * TODO Categorized items break here :(
//			 */
//			return new AnyChoiceSet<T>(poClass);
//		}
//		List<PrimitiveChoiceSet<T>> orList = new ArrayList<PrimitiveChoiceSet<T>>();
//		List<PrimitiveChoiceFilter<T>> pcfOrList = new ArrayList<PrimitiveChoiceFilter<T>>();
//		for (ChooseSeparator pipe = new ChooseSeparator(joinedOr, '|'); pipe
//				.hasNext();)
//		{
//			String joinedAnd = pipe.next();
//			List<PrimitiveChoiceSet<T>> andList = new ArrayList<PrimitiveChoiceSet<T>>();
//			List<PrimitiveChoiceFilter<T>> pcfAndList = new ArrayList<PrimitiveChoiceFilter<T>>();
//			for (ChooseSeparator comma = new ChooseSeparator(joinedAnd, ','); comma
//					.hasNext();)
//			{
//				String primitive = comma.next();
//				ChooseLstQualifierToken<T> qual = getQualifier(context, poClass, primitive);
//				if (qual == null)
//				{
//					PrimitiveChoiceFilter<T> pcf = getPrimitive(context, poClass,
//							primitive);
//					if (pcf == null)
//					{
//						Logging.addParseMessage(Logging.LST_ERROR,
//								"Choice argument was not valid : " + primitive);
//						return null;
//					}
//					else
//					{
//						pcfAndList.add(pcf);
//					}
//				}
//				else
//				{
//					andList.add(qual);
//				}
//			}
//			if (!pcfAndList.isEmpty())
//			{
//				if (pcfAndList.size() == 1 && andList.isEmpty())
//				{
//					pcfOrList.add(pcfAndList.get(0));
//				}
//				else
//				{
//					RetainingChooser<T> ret = new RetainingChooser<T>(poClass);
//					ret.addRetainingChoiceFilter(new CompoundAndFilter<T>(pcfAndList));
//					andList.add(ret);
//				}
//			}
//			if (andList.size() == 1)
//			{
//				orList.add(andList.get(0));
//			}
//			else if (!andList.isEmpty())
//			{
//				orList.add(new CompoundAndChoiceSet<T>(andList));
//			}
//		}
//		if (!pcfOrList.isEmpty())
//		{
//			RetainingChooser<T> ret = new RetainingChooser<T>(poClass);
//			ret.addAllRetainingChoiceFilters(pcfOrList);
//			orList.add(ret);
//		}
//		if (orList.isEmpty())
//		{
//			return null;
//		}
//		else if (orList.size() == 1)
//		{
//			return orList.get(0);
//		}
//		else
//		{
//			return new CompoundOrChoiceSet<T>(orList);
//		}
//	}
//
//	public <T extends CDOMObject> PrimitiveChoiceFilter<T> getPrimitive(
//			LoadContext context, Class<T> cl, String key)
//	{
//		int openBracketLoc = key.indexOf('[');
//		int closeBracketLoc = key.indexOf(']');
//		int equalLoc = key.indexOf('=');
//		String tokKey;
//		String tokValue;
//		String tokRestriction;
//		if (openBracketLoc == -1)
//		{
//			if (closeBracketLoc != -1)
//			{
//				Logging.errorPrint("Found error in Primitive Choice: " + key
//						+ " has a close bracket but no open bracket");
//				return null;
//			}
//			if (equalLoc == -1)
//			{
//				tokKey = key;
//				tokValue = null;
//			}
//			else
//			{
//				tokKey = key.substring(0, equalLoc);
//				tokValue = key.substring(equalLoc + 1);
//			}
//			tokRestriction = null;
//		}
//		else
//		{
//			if (closeBracketLoc == -1)
//			{
//				Logging.errorPrint("Found error in Primitive Choice: " + key
//						+ " has an open bracket but no close bracket");
//				return null;
//			}
//			if (closeBracketLoc != key.length() - 1)
//			{
//				Logging.errorPrint("Found error in Primitive Choice: " + key
//						+ " had close bracket, but had characters "
//						+ "following the close bracket");
//				return null;
//			}
//			if (equalLoc == -1 || equalLoc > openBracketLoc)
//			{
//				tokKey = key.substring(0, openBracketLoc);
//				tokValue = null;
//				tokRestriction = key.substring(openBracketLoc + 1,
//						closeBracketLoc);
//			}
//			else
//			{
//				tokKey = key.substring(0, equalLoc);
//				tokValue = key.substring(equalLoc + 1, openBracketLoc);
//				tokRestriction = key.substring(openBracketLoc + 1,
//						closeBracketLoc);
//			}
//		}
//		PrimitiveToken<T> prim = TokenLibrary.getPrimitive(cl, tokKey);
//		if (prim == null)
//		{
//			if (tokRestriction != null)
//			{
//				Logging.errorPrint("Didn't expect tokRestriction here: "
//						+ tokRestriction);
//			}
//			if ("TYPE".equals(tokKey))
//			{
//				return TokenUtilities.getTypeReference(context, cl, tokValue);
//			}
//			else if ("!TYPE".equals(tokKey))
//			{
//				return new NegatingFilter<T>(TokenUtilities.getTypeReference(
//						context, cl, tokValue));
//			}
//			if (tokValue != null)
//			{
//				Logging.errorPrint("Didn't expect Arguments here: " + tokValue);
//			}
//			if (key.startsWith(Constants.LST_TYPE_OLD))
//			{
//				return TokenUtilities.getTypeReference(context, cl, key
//						.substring(5));
//			}
//			else if (key.startsWith(Constants.LST_NOT_TYPE_OLD))
//			{
//				return new NegatingFilter<T>(TokenUtilities.getTypeReference(
//						context, cl, key.substring(6)));
//			}
//			if (key.indexOf('%') != -1)
//			{
//				return new PatternMatchFilter<T>(cl, key);
//			}
//			else
//			{
//				return context.ref.getCDOMReference(cl, key);
//			}
//		}
//		else
//		{
//			if (!prim.initialize(context, tokValue, tokRestriction))
//			{
//				return null;
//			}
//		}
//		return prim;
//	}
//
//	public <T extends CDOMObject> ChooseLstQualifierToken<T> getQualifier(
//			LoadContext loadContext, Class<T> cl, String key)
//	{
//		int openBracketLoc = key.indexOf('[');
//		int closeBracketLoc = key.indexOf(']');
//		int equalLoc = key.indexOf('=');
//		String tokKey;
//		String tokValue;
//		String tokRestriction;
//		if (openBracketLoc == -1)
//		{
//			if (closeBracketLoc != -1)
//			{
//				Logging.errorPrint("Found error in Primitive Choice: " + key
//						+ " has a close bracket but no open bracket");
//				return null;
//			}
//			if (equalLoc == -1)
//			{
//				tokKey = key;
//				tokValue = null;
//			}
//			else
//			{
//				tokKey = key.substring(0, equalLoc);
//				tokValue = key.substring(equalLoc + 1);
//			}
//			tokRestriction = null;
//		}
//		else
//		{
//			if (closeBracketLoc == -1)
//			{
//				Logging.errorPrint("Found error in Primitive Choice: " + key
//						+ " has an open bracket but no close bracket");
//				return null;
//			}
//			if (closeBracketLoc != key.length() - 1)
//			{
//				Logging.errorPrint("Found error in Primitive Choice: " + key
//						+ " had close bracket, but had characters "
//						+ "following the close bracket");
//				return null;
//			}
//			if (equalLoc == -1 || equalLoc > openBracketLoc)
//			{
//				tokKey = key.substring(0, openBracketLoc);
//				tokValue = null;
//				tokRestriction = key.substring(openBracketLoc + 1,
//						closeBracketLoc);
//			}
//			else
//			{
//				tokKey = key.substring(0, equalLoc);
//				tokValue = key.substring(equalLoc + 1, openBracketLoc);
//				tokRestriction = key.substring(openBracketLoc + 1,
//						closeBracketLoc);
//			}
//		}
//		for (Iterator<ChooseLstQualifierToken<T>> it = new QualifierTokenIterator<T, ChooseLstQualifierToken<T>>(
//				cl, tokKey); it.hasNext();)
//		{
//			ChooseLstQualifierToken<T> token = it.next();
//			if (token.initialize(loadContext, cl, tokValue, tokRestriction))
//			{
//				return token;
//			}
//			Logging.addParseMessage(Logging.LST_ERROR,
//					"Failed in parsing typeStr: " + key);
//		}
//		return null;
//	}
//
//	public <T extends CDOMObject> PrimitiveChoiceSet<?> getChoiceSet(LoadContext loadContext,
//			T cdo, String key, String val) throws PersistenceLayerException
//	{
//		Class<T> cl = (Class<T>) cdo.getClass();
//		
//		for (Iterator<ChoiceSetToken<? super T>> it = new ChooseTokenIterator<T>(
//				cl, key); it.hasNext();)
//		{
//			ChoiceSetToken<? super T> token = it.next();
//			PrimitiveChoiceSet<?> ret = token.parse(loadContext, cdo, val);
//			if (ret != null)
//			{
//				return ret;
//			}
//			Logging.addParseMessage(Logging.LST_ERROR,
//					"Failed in parsing typeStr: " + key + " " + val);
//		}
//		/*
//		 * CONSIDER Better option than toString, given that T != CDOMObject
//		 */
//		Logging.addParseMessage(Logging.LST_ERROR, "Illegal subtoken '" + key
//				+ "' '" + val + "' for " + cdo.toString());
//		return null;
//	}
}
