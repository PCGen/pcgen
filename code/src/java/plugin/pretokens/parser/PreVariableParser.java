/*
 * PreVariableParser.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 19-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.utils.ParsingSeparator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreVariableParser extends AbstractPrerequisiteParser implements
		PrerequisiteParserInterface
{
	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{"VAR", "VAREQ", "VARLTEQ", "VARLT", "VARNEQ",
			"VARGT", "VARGTEQ"};
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#parse(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setKind("var");

		// Get the comparator type SIZEGTEQ, BSIZE, SIZENEQ etc.
		String compType = kind.substring(3);
		if (compType.length() == 0)
		{
			compType = "gteq";
		}

		ParsingSeparator ps = new ParsingSeparator(formula, ',');
		try
		{
			int count = 0;
			while (ps.hasNext())
			{
				String first = ps.next();
				if (!ps.hasNext())
				{
					return parseOld(kind, formula, invertResult, overrideQualify);
//					throw new PersistenceLayerException(
//							"Unable to parse prrequisite 'PRE" + kind + ":" + formula
//								+ "'. Incorrect parameter count (must be even)");
				}
				String second = ps.next();
				Prerequisite subreq;
				if (!ps.hasNext() && count == 0)
				{
					subreq = prereq;
				}
				else
				{
					prereq.setKind(null); // PREMULT
					subreq = new Prerequisite();
					subreq.setKind("var");
					prereq.addPrerequisite(subreq);
					count++;
				}
				subreq.setOperator(compType);
				subreq.setKey(first);
				subreq.setOperand(second);
			}
			if (count > 0)
			{
				prereq.setOperand(Integer.toString(count));
			}
		}
		catch (PrerequisiteException pe)
		{
			return parseOld(kind, formula, invertResult, overrideQualify);
//			throw new PersistenceLayerException(
//				"Unable to parse prrequisite 'PRE" + kind + ":" + formula
//					+ "'. " + pe.getLocalizedMessage());
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		prereq.setOverrideQualify(overrideQualify);

		return prereq;
	}
	
	public Prerequisite parseOld(String kind, String formula,
			boolean invertResult, boolean overrideQualify)
			throws PersistenceLayerException
	{
		Logging.deprecationPrint("Warning: You are using a deprecated "
				+ "(or broken) form of PREVAR: " + formula);
		Prerequisite prereq = super.parse(kind, formula, invertResult,
				overrideQualify);
		prereq.setKind("var");

		// Get the comparator type SIZEGTEQ, BSIZE, SIZENEQ etc.
		String compType = kind.substring(3);
		if (compType.length() == 0)
		{
			compType = "gteq";
		}

		String[] tokens = formula.split(",|\\|");

		//
		// There needs to be an even number of tokens
		//
		if ((tokens.length & 0x01) == 0x01)
		{
			throw new PersistenceLayerException(
					"Unable to parse prrequisite 'PRE" + kind + ":" + formula
							+ "'. Incorrect parameter count (must be even)");
		}

		try
		{
			prereq.setOperand(Integer.toString(tokens.length >> 1));
			for (int i = 0; i < tokens.length; ++i)
			{
				String andKey = tokens[i];
				String andOp = tokens[++i];

				Prerequisite andPrereq;
				if (tokens.length > 2)
				{
					prereq.setKind(null); // PREMULT
					andPrereq = new Prerequisite();
					prereq.addPrerequisite(andPrereq);
					andPrereq.setKind("var");
				}
				else
				{
					andPrereq = prereq;
				}
				andPrereq.setOperator(compType);
				andPrereq.setKey(andKey);
				andPrereq.setOperand(andOp);
			}
		}
		catch (PrerequisiteException pe)
		{
			throw new PersistenceLayerException(
					"Unable to parse prrequisite 'PRE" + kind + ":" + formula
							+ "'. " + pe.getLocalizedMessage());
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		prereq.setOverrideQualify(overrideQualify);

		return prereq;
	}

}
