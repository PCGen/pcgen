/*
 * FilterParser.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on February 20, 2002, 5:30 PM
 */
package pcgen.gui.filter;

import pcgen.cdom.base.Constants;
import pcgen.system.LanguageBundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>FilterParser</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
final class FilterParser
{
	private static final String TOKEN_AND = "AND";
	private static final String TOKEN_NOT = "NOT";
	private static final String TOKEN_OR = "OR";
	private static final String TOKEN_STARTGROUP = "(";
	private static final String TOKEN_ENDGROUP = ")";
	private static final char CHAR_TOKEN_STARTGROUP = '(';
	private static final char CHAR_TOKEN_ENDGROUP = ')';
	private static final char TOKEN_STARTFILTER = '[';
	private static final String TOKEN_ENDFILTER = "]";
	private List[] filterList;

	/**
	 * Constructor
	 * @param filterList
	 */
	FilterParser(List[] filterList)
	{
		this.filterList = filterList;
	}

	/**
	 * parse a filter definition String to create a PObjectFilter
	 * @param filterDefinition
	 *
	 * @return the created PObjectFilter
	 * author: Thomas Behr 20-02-02
	 * @throws FilterParseException
	 */
	public PObjectFilter parse(String filterDefinition)
		throws FilterParseException
	{
		List<String> tokenList = createTokenList(normalize(filterDefinition));
		checkTokens(tokenList);

		return parseTokenList(enforceStrongAssociationForNOT(tokenList));
	}

	/**
	 * author: Thomas Behr 20-02-02
	 * @param token
	 * @return true if it is a legal token
	 */
	static boolean isLegalToken(String token)
	{
		String s = token.toUpperCase();

		return s.equals(TOKEN_STARTGROUP) || s.equals(TOKEN_ENDGROUP) || s.equals(TOKEN_AND) || s.equals(TOKEN_NOT)
		|| s.equals(TOKEN_OR)
		|| ((s.length() > 0) && (s.charAt(0) == TOKEN_STARTFILTER) && s.endsWith(TOKEN_ENDFILTER) && (s.length() > 2));
	}

	/**
	 * @param token1
	 * @param token2
	 * @return  0 if legal sequence
	 *         -1 if empty statement
	 *         -2 if missing operand
	 *         -3 if illegal sequence
	 *         -4 if illegal first token
	 *         -5 if illegal last token
	 * author: Thomas Behr 21-02-02
	 */
	private static int isLegalTokenSequence(String token1, String token2)
	{
		String s1 = token1.toUpperCase();
		String s2 = token2.toUpperCase();

		// empty statement
		if (s1.endsWith(TOKEN_STARTGROUP) && (s2.length() > 0) && (s2.charAt(0) == CHAR_TOKEN_ENDGROUP))
		{
			return -1;
		}

		// missing operand
		if (s1.endsWith(TOKEN_ENDGROUP)
		    && (s2.equals(TOKEN_NOT) || ((s2.length() > 0) && (s2.charAt(0) == CHAR_TOKEN_STARTGROUP))
		    || ((s2.length() > 0) && (s2.charAt(0) == TOKEN_STARTFILTER))))
		{
			return -2;
		}

		// missing operand
		if (s1.endsWith(TOKEN_ENDFILTER)
		    && (s2.equals(TOKEN_NOT) || ((s2.length() > 0) && (s2.charAt(0) == CHAR_TOKEN_STARTGROUP))
		    || ((s2.length() > 0) && (s2.charAt(0) == TOKEN_STARTFILTER))))
		{
			return -2;
		}

		// illegal sequence
		if (s1.equals(TOKEN_AND)
		    && !(s2.equals(TOKEN_NOT) || ((s2.length() > 0) && (s2.charAt(0) == CHAR_TOKEN_STARTGROUP))
		    || ((s2.length() > 0) && (s2.charAt(0) == TOKEN_STARTFILTER))))
		{
			return -3;
		}

		// illegal sequence
		if (s1.equals(TOKEN_NOT)
		    && !(((s2.length() > 0) && (s2.charAt(0) == CHAR_TOKEN_STARTGROUP))
		    || ((s2.length() > 0) && (s2.charAt(0) == TOKEN_STARTFILTER))))
		{
			return -3;
		}

		// illegal sequence
		if (s1.equals(TOKEN_OR)
		    && !(s2.equals(TOKEN_NOT) || ((s2.length() > 0) && (s2.charAt(0) == CHAR_TOKEN_STARTGROUP))
		    || ((s2.length() > 0) && (s2.charAt(0) == TOKEN_STARTFILTER))))
		{
			return -3;
		}

		// illegal first token
		if ("".equals(s1)
		    && !(s2.equals(TOKEN_NOT) || ((s2.length() > 0) && (s2.charAt(0) == CHAR_TOKEN_STARTGROUP))
		    || ((s2.length() > 0) && (s2.charAt(0) == TOKEN_STARTFILTER))))
		{
			return -4;
		}

		// illegal last token
		if ("".equals(s2) && !(s1.endsWith(TOKEN_ENDGROUP) || s1.endsWith(TOKEN_ENDFILTER)))
		{
			return -5;
		}

		return 0;
	}

	/**
	 * author: Thomas Behr 20-02-02
	 * @param tokenList
	 * @throws FilterParseException
	 */
	private static void checkTokens(List<String> tokenList) throws FilterParseException
	{
		String token;
		String lastToken = "";

		for (Iterator<String> it = tokenList.iterator(); it.hasNext();)
		{
			token = it.next();

			if (!isLegalToken(token))
			{
				throw new FilterParseException(LanguageBundle.getFormattedString("in_malformed",token));
			}

			final int sequenceError = isLegalTokenSequence(lastToken, token);

			if (sequenceError == -1)
			{
				throw new FilterParseException(LanguageBundle.getString("in_emptyState"));
			}
			else if (sequenceError == -2)
			{
				throw new FilterParseException(LanguageBundle.getString("in_missOper"));
			}
			else if (sequenceError == -3)
			{
				throw new FilterParseException(LanguageBundle.getFormattedString("in_illegSeq",lastToken,
				    token));
			}
			else if (sequenceError == -4)
			{
				throw new FilterParseException(LanguageBundle.getFormattedString("in_illegTok",token));
			}

			lastToken = token;
		}

		if (isLegalTokenSequence(lastToken, "") != 0)
		{
			throw new FilterParseException(LanguageBundle.getFormattedString("in_illegLastTok",lastToken));
		}
	}

	/**
	 * break String down to usable tokens
	 *
	 * author: Thomas Behr 20-02-02
	 * @param parseString
	 * @return token list
	 * @throws FilterParseException
	 */
	private static List<String> createTokenList(String parseString)
		throws FilterParseException
	{
		int braceCount = 0;
		int bracketCount = 0;

		List<String> list = new ArrayList<String>();

		boolean filterName = false;

		String token;
		StringBuffer name = new StringBuffer();

		StringTokenizer tokens = new StringTokenizer(parseString,
			    " " + TOKEN_STARTFILTER + TOKEN_ENDFILTER + TOKEN_STARTGROUP + TOKEN_ENDGROUP, true);

		while (tokens.hasMoreTokens())
		{
			token = tokens.nextToken();

			if (token.equals(TOKEN_STARTGROUP))
			{
				braceCount++;
			}
			else if (token.equals(TOKEN_ENDGROUP))
			{
				braceCount--;
			}
			else if (token.equals(new Character(TOKEN_STARTFILTER).toString()))
			{
				name.delete(0, name.length());
				name.append(token);
				token = "";
				filterName = true;
				bracketCount++;
			}
			else if (token.equals(TOKEN_ENDFILTER))
			{
				name.append(token);
				token = name.toString();
				filterName = false;
				bracketCount--;
			}
			else if (!filterName)
			{
				token = token.toUpperCase();
			}

			if (!filterName)
			{
				token = token.trim();

				if (token.length() > 0)
				{
					list.add(token);
				}
			}
			else
			{
				name.append(token);
			}
		}
		 // end while (tokens.hasMoreTokens())

		if (braceCount > 0)
		{
			throw new FilterParseException(LanguageBundle.getFormattedString("in_missing",TOKEN_ENDGROUP));
		}
		else if (braceCount < 0)
		{
			throw new FilterParseException(LanguageBundle.getFormattedString("in_missing",TOKEN_STARTGROUP));
		}

		if (bracketCount > 0)
		{
			throw new FilterParseException(LanguageBundle.getFormattedString("in_missing",TOKEN_ENDFILTER));
		}
		else if (bracketCount < 0)
		{
			throw new FilterParseException(LanguageBundle.getFormattedString("in_missing",TOKEN_STARTFILTER));
		}

		return list;
	}

	/**
	 * author: Thomas Behr 20-02-02
	 * @param tokenList
	 * @return List
	 */
	private List<String> enforceStrongAssociationForNOT(List<String> tokenList)
	{
		List<String> newTokenList = new ArrayList<String>();

		final int index = tokenList.indexOf(TOKEN_NOT);

		if (index == -1)
		{
			return tokenList;
		}

		newTokenList.addAll(tokenList.subList(0, index));
		newTokenList.add(TOKEN_STARTGROUP);
		newTokenList.add(TOKEN_NOT);

		List<String> restList = new ArrayList<String>(tokenList.subList(index + 1, tokenList.size()));

		String firstRestToken = restList.get(0);

		if (firstRestToken.equals(TOKEN_STARTGROUP))
		{
			int i = 0;
			int braceCount = 1;
			String token;

			restList.remove(i);

			for (Iterator<String> it = restList.iterator(); it.hasNext(); i++)
			{
				token = it.next();

				if (token.equals(TOKEN_STARTGROUP))
				{
					braceCount++;
				}
				else if (token.equals(TOKEN_ENDGROUP))
				{
					braceCount--;
				}

				if (braceCount == 0)
				{
					break;
				}
			}

			restList.remove(i);

			if (i == restList.size())
			{
				newTokenList.add(TOKEN_STARTGROUP);
				newTokenList.addAll(enforceStrongAssociationForNOT(restList));
				newTokenList.add(TOKEN_ENDGROUP);
				newTokenList.add(TOKEN_ENDGROUP);
			}
			else
			{
				newTokenList.add(TOKEN_STARTGROUP);
				newTokenList.addAll(enforceStrongAssociationForNOT(restList.subList(0, i)));
				newTokenList.add(TOKEN_ENDGROUP);
				newTokenList.add(TOKEN_ENDGROUP);
				newTokenList.addAll(enforceStrongAssociationForNOT(restList.subList(i, restList.size())));
			}
		}
		else if ((firstRestToken.length() > 0) && (firstRestToken.charAt(0) == TOKEN_STARTFILTER))
		{
			restList.remove(0);
			newTokenList.add(firstRestToken);
			newTokenList.add(TOKEN_ENDGROUP);
			newTokenList.addAll(enforceStrongAssociationForNOT(restList));
		}

		return newTokenList;
	}

	/**
	 * replace all whitespace characters with " "
	 *
	 * author: Thomas Behr 20-02-02
	 * @param s
	 * @return String
	 */
	private static String normalize(String s)
	{
		StringBuffer buffer = new StringBuffer();
		String tokenizeOnThis = "\t" + Constants.LINE_SEPARATOR + "\f";
		StringTokenizer tokens = new StringTokenizer(s, tokenizeOnThis);

		while (tokens.hasMoreTokens())
		{
			buffer.append(tokens.nextToken()).append(" ");
		}

		return buffer.toString().trim();
	}

	/**
	 * Recursively build compound filter
	 *
	 * author: Thomas Behr 20-02-02
	 * @param tokenList
	 * @return PObjectFilter
	 * @throws FilterParseException
	 */
	private PObjectFilter parseTokenList(List<String> tokenList)
		throws FilterParseException
	{
		PObjectFilter filter;

		String firstToken;
		firstToken = tokenList.get(0);

		if (firstToken.equals(TOKEN_STARTGROUP))
		{
			int i = 0;
			int braceCount = 1;
			String token;

			tokenList.remove(i);

			for (Iterator<String> it = tokenList.iterator(); it.hasNext(); i++)
			{
				token = it.next();

				if (token.equals(TOKEN_STARTGROUP))
				{
					braceCount++;
				}
				else if (token.equals(TOKEN_ENDGROUP))
				{
					braceCount--;
				}

				if (braceCount == 0)
				{
					break;
				}
			}

			tokenList.remove(i);

			if (i == tokenList.size())
			{
				filter = parseTokenList(tokenList);
			}
			else
			{
				List<String> tokenList1 = new ArrayList<String>(tokenList.subList(0, i));
				List<String> tokenList2 = new ArrayList<String>(tokenList.subList(i + 1, tokenList.size()));
				filter = FilterFactory.createCompoundFilter(parseTokenList(tokenList1), parseTokenList(tokenList2),
					    tokenList.get(i));
			}
		}
		else if (firstToken.equals(TOKEN_NOT))
		{
			/*
			 * this means 'NOT' is weak associative
			 */
			tokenList.remove(0);
			filter = FilterFactory.createInverseFilter(parseTokenList(tokenList));
		}
		else if ((firstToken.length() > 0) && (firstToken.charAt(0) == TOKEN_STARTFILTER))
		{
			String filterName = tokenList.remove(0);

			if (tokenList.size() > 0)
			{
				String operand = tokenList.remove(0);
				filter = FilterFactory.createCompoundFilter(retrieveFilter(filterName), parseTokenList(tokenList),
					    operand);
			}
			else
			{
				filter = retrieveFilter(filterName);
			}
		}
		else
		{
			throw new FilterParseException(LanguageBundle.getFormattedString("in_malformed",firstToken));
		}

		return filter;
	}

	/**
	 * retrieve filter according to category and name
	 *
	 * author: Thomas Behr 20-02-02
	 * @param filterName
	 * @return PObjectFilter
	 * @throws FilterParseException
	 */
	private PObjectFilter retrieveFilter(String filterName)
		throws FilterParseException
	{
		PObjectFilter filter;

		for (int i = 0; i < filterList.length; i++)
		{
			for (Iterator it = filterList[i].iterator(); it.hasNext();)
			{
				filter = (PObjectFilter) it.next();

				if (filterName.equals(TOKEN_STARTFILTER + filter.getCategory() + PObjectFilter.SEPARATOR
				        + filter.getName() + TOKEN_ENDFILTER))
				{
					return filter;
				}
			}
		}

		throw new FilterParseException(LanguageBundle.getFormattedString("in_notFindFil",filterName));
	}
}
