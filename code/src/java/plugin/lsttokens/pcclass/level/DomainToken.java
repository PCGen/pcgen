/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.pcclass.level;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Domain;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DOMAIN Token
 */
public class DomainToken extends AbstractToken implements
		CDOMPrimaryToken<PCClassLevel>
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	@Override
	public String getTokenName()
	{
		return "DOMAIN";
	}

	public boolean parse(LoadContext context, PCClassLevel level, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;
		while (pipeTok.hasMoreTokens())
		{
			String tok = pipeTok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tok))
			{
				if (!first)
				{
					Logging.log(Logging.LST_ERROR, "  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item");
					return false;
				}
				context.getObjectContext().removeList(level, ListKey.DOMAIN);
				continue;
			}
			// Note: May contain PRExxx
			String domainKey;
			Prerequisite prereq = null;

			int openBracketLoc = tok.indexOf('[');
			if (openBracketLoc == -1)
			{
				if (tok.indexOf(']') != -1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " must have '[' if it contains a PREREQ tag");
					return false;
				}
				domainKey = tok;
			}
			else
			{
				if (tok.indexOf(']') != tok.length() - 1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " must end with ']' if it contains a PREREQ tag");
					return false;
				}
				domainKey = tok.substring(0, openBracketLoc);
				String prereqString = tok.substring(openBracketLoc + 1, tok
						.length() - 1);
				if (prereqString.length() == 0)
				{
					Logging.errorPrint(getTokenName()
							+ " cannot have empty prerequisite : " + value);
					return false;
				}
				prereq = getPrerequisite(prereqString);
				if (prereq == null)
				{
					Logging.errorPrint(getTokenName()
							+ " had invalid prerequisite : " + prereqString);
					return false;
				}
			}
			CDOMSingleRef<Domain> domain = context.ref.getCDOMReference(
					DOMAIN_CLASS, domainKey);

			QualifiedObject<CDOMSingleRef<Domain>> qo = new QualifiedObject<CDOMSingleRef<Domain>>(
					domain);
			if (prereq != null)
			{
				qo.addPrerequisite(prereq);
			}
			context.getObjectContext().addToList(level, ListKey.DOMAIN, qo);
			first = false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClassLevel level)
	{
		Changes<QualifiedObject<CDOMSingleRef<Domain>>> changes = context.getObjectContext().getListChanges(level, ListKey.DOMAIN);
		List<String> list = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		Collection<QualifiedObject<CDOMSingleRef<Domain>>> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR.");
			return null;
		}
		Collection<QualifiedObject<CDOMSingleRef<Domain>>> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			for (QualifiedObject<CDOMSingleRef<Domain>> qo : added)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(qo.getObject(null).getLSTformat());
				if (qo.hasPrerequisites())
				{
					List<Prerequisite> prereqs = qo.getPrerequisiteList();
					if (prereqs.size() > 1)
					{
						context.addWriteMessage("Incoming Edge to "
								+ level.getKeyName() + " had more than one "
								+ "Prerequisite: " + prereqs.size());
						return null;
					}
					sb.append('[');
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, prereqs.get(0));
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
								+ e);
						return null;
					}
					sb.append(swriter.toString());
					sb.append(']');
				}
				list.add(sb.toString());
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return new String[] { StringUtil.join(list, Constants.PIPE) };
	}

	public Class<PCClassLevel> getTokenClass()
	{
		return PCClassLevel.class;
	}
}