/*
 * AspectToken.java
 * Copyright 2008 (C) James Dempsey
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
 */
package plugin.lsttokens.ability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.helper.Aspect;
import pcgen.core.Ability;
import pcgen.core.prereq.Prerequisite;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * The Class {@code AspectToken} parses a generic detail field for
 * abilities. It is a name/value characteristic allowing substitution of values.
 * 
 * <p>
 * Variable substitution is performed by replacing a placeholder indicated by %#
 * with the #th variable in the variable list. For example, the string <br>
 * {@code "This is %1 variable %3 %2"} <br>
 * would be replaced with the string &quot;This is a variable substitution
 * string&quot; if the variable list was &quot;a&quot;,&quot;string&quot;,
 * &quot;substitution&quot;.
 * 
 * 
 */
public class AspectToken extends AbstractNonEmptyToken<Ability> implements CDOMPrimaryToken<Ability>
{
	@Override
	public String getTokenName()
	{
		return "ASPECT"; //$NON-NLS-1$
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Ability ability, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail(
				getTokenName() + " expecting '|', format is: " + "AspectName|Aspect value|Variable|... was: " + value);
		}
		String key = value.substring(0, pipeLoc);
		if (key.isEmpty())
		{
			return new ParseResult.Fail(getTokenName() + " expecting non-empty type, "
				+ "format is: AspectName|Aspect value|Variable|... was: " + value);
		}
		String val = value.substring(pipeLoc + 1);
		if (val.isEmpty())
		{
			return new ParseResult.Fail(getTokenName() + " expecting non-empty value, "
				+ "format is: AspectName|Aspect value|Variable|... was: " + value);
		}
		if (val.startsWith(Constants.PIPE))
		{
			return new ParseResult.Fail(getTokenName() + " expecting non-empty value, "
				+ "format is: AspectName|Aspect value|Variable|... was: " + value);
		}
		Aspect a = parseAspect(key, val);
		MapChanges<AspectName, List<Aspect>> mc = context.getObjectContext().getMapChanges(ability, MapKey.ASPECT);
		Map<AspectName, List<Aspect>> fullMap = mc.getAdded();
		List<Aspect> aspects = fullMap.get(a.getKey());
		if (aspects == null)
		{
			aspects = new ArrayList<>();
		}
		aspects.add(a);
		context.getObjectContext().put(ability, MapKey.ASPECT, a.getKey(), aspects);
		return ParseResult.SUCCESS;
	}

	/**
	 * Parses the ASPECT tag into a Aspect object.
	 * 
	 * @param aspectDef
	 *            The LST tag
	 * @return A <tt>Aspect</tt> object
	 */
	public Aspect parseAspect(final String name, final String aspectDef)
	{
		final StringTokenizer tok = new StringTokenizer(aspectDef, Constants.PIPE);

		String firstToken = tok.nextToken();
		/*if (PreParserFactory.isPreReqString(firstToken))
		{
			Logging.errorPrint("Invalid " + getTokenName() + ": " + name);
			Logging.errorPrint("  PRExxx can not be only value");
			return null;
		}*/
		final Aspect aspect = new Aspect(name, EntityEncoder.decode(firstToken));

		boolean isPre = false;
		while (tok.hasMoreTokens())
		{
			final String token = tok.nextToken();
			if (PreParserFactory.isPreReqString(token))
			{
				Prerequisite prereq = getPrerequisite(token);
				if (prereq == null)
				{
					Logging.errorPrint(getTokenName() + " had invalid prerequisite : " + token);
					return null;
				}
				aspect.addPrerequisite(prereq);
				isPre = true;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": " + name);
					Logging.errorPrint("  PRExxx must be at the END of the Token");
					return null;
				}
				aspect.addVariable(token);
			}
		}

		return aspect;
	}

	@Override
	public String[] unparse(LoadContext context, Ability ability)
	{
		MapChanges<AspectName, List<Aspect>> changes = context.getObjectContext().getMapChanges(ability, MapKey.ASPECT);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<>();
		Set<AspectName> keys = changes.getAdded().keySet();
		for (AspectName an : keys)
		{
			List<Aspect> aspects = changes.getAdded().get(an);
            for (Aspect q : aspects) {
                set.add(
                        q.getName() + Constants.PIPE + q.getPCCText());
            }
		}
		return set.toArray(new String[0]);
	}

	@Override
	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

}
