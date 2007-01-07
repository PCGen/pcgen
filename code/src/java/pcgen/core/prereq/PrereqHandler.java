/*
 * PrereqHandler.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 28, 2003
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */

package pcgen.core.prereq;


import pcgen.core.*;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import java.util.List;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PrereqHandler {

	/**
	 * Test if the character passes the prerequisites for the caller. The caller
	 * is used to check if prereqs can be bypassed by either preferences or via
	 * Qualifies statements in templates or other objects applied to the
	 * character.
	 *
	 * @param prereqList The list of prerequisites to be tested.
	 * @param character The character to be checked.
	 * @param caller The object that we are testing qualification for.
	 * @return True if the character passes all prereqs.
	 */
	public static boolean passesAll(final List<Prerequisite> prereqList, final PlayerCharacter character, final PObject caller)
	{
		if (prereqList == null || prereqList.isEmpty())
		{
			return true;
		}

		if ((caller instanceof PCClass) && Globals.checkRule(RuleConstants.CLASSPRE)) //$NON-NLS-1$
		{
			return true;
		}
		if ((caller instanceof Ability) && Globals.checkRule(RuleConstants.FEATPRE)) //$NON-NLS-1$
		{
			return true;
		}

		if ((character != null) && (caller != null))
		{
			// Check for QUALIFY:
			if (character.checkQualifyList(caller.getKeyName()))
			{
				return true;
			}
		}

		for (Object object : prereqList)
		{
			Prerequisite prereq;

			if (object instanceof String)
			{
				final String oString = (String)object;
				Logging.debugPrintLocalised("PrereqHandler.Why_not_already_parsed", object, "PrereqHandler.passesAll()"); //$NON-NLS-1$ //$NON-NLS-2$
				try
				{
					final PreParserFactory factory = PreParserFactory.getInstance();
					prereq = factory.parse( oString );
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple); //The message is now produced at a lower level, and thus has to be localised there.
					//Logging.errorPrintLocalised(PropertyFactory.getString("PrereqHandler.Unable_to_parse"), object); //$NON-NLS-1$
					return false;
				}
			}
			else
			{
				prereq = (Prerequisite) object;
			}


			if (!passes(prereq, character, caller))
			{
				return false;
			}
		}
		return true;
	}

	public static boolean passesAll(final List<?> prereqList, final Equipment equip, PlayerCharacter currentPC)
	{
		if (prereqList == null)
		{
			return true;
		}
		for (Object object : prereqList)
		{
			Prerequisite prereq;

			if (object instanceof String)
			{
				Logging.debugPrintLocalised("PrereqHandler.Why_not_already_parsed", object, "PrereqHandler.passesAll()"); //$NON-NLS-1$ //$NON-NLS-2$
				try
				{
					final PreParserFactory factory = PreParserFactory.getInstance();
					prereq = factory.parse( (String) object );
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple); //The message is now produced at a lower level, and thus has to be localised there.
					//Logging.errorPrintLocalised("PrereqHandler.Unable_to_parse", object); //$NON-NLS-1$
					return false;
				}
			}
			else
			{
				prereq = (Prerequisite) object;
			}

			if (!passes(prereq, equip, currentPC))
			{
				return false;
			}
		}
		return true;
	}

	public static boolean passesAtLeastOne(final List<Prerequisite> prereqList, final PlayerCharacter character, final PObject caller)
	{
		if (prereqList == null)
		{
			return true;
		}
		for ( Prerequisite element : prereqList )
		{
			if (passes(element, character, caller))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean passesAtLeastOne(final List<Prerequisite> prereqList, final Equipment equip, PlayerCharacter currentPC)
	{
		if (prereqList == null)
		{
			return true;
		}
		for ( Prerequisite element : prereqList )
		{
			if (passes(element, equip, currentPC))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean passes(final Prerequisite prereq, final PlayerCharacter character, final PObject caller)
	{
		if (character == null)
		{
			return true;
		}
		final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
		final PrerequisiteTest test = factory.getTest(prereq.getKind());
		if (prereq.getLevelQualifier() >= 0 && (caller instanceof PCClass) && ((PCClass)caller).getLevel()!=prereq.getLevelQualifier())
			return true;

		if (test == null)
		{
			Logging.errorPrintLocalised("PrereqHandler.Unable_to_find_implementation", prereq.toString()); //$NON-NLS-1$
			return false;
		}

		final boolean overrideQualify = prereq.isOverrideQualify();
		boolean autoQualifies = false;
		int total = 0;

		if ((caller != null) && character.checkQualifyList(caller.getKeyName()) && (!overrideQualify))
		{
			autoQualifies = true;
		}
		if (autoQualifies)
		{
			return true;
		}
		try
		{
			total = test.passes(prereq, character);
		}
		catch (PrerequisiteException pe)
		{
			Logging.errorPrintLocalised("PrereqHandler.Exception_in_test", pe); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			Logging.errorPrint(
				"Problem encountered when testing PREREQ "
					+ String.valueOf(prereq) +
					( caller != null ? (" for "
					+ String.valueOf(caller)) : "")
					+ ". See following trace for details.", e);
		}
		return total>0;
	}

	public static boolean passes(final Prerequisite prereqList, final Equipment equip, PlayerCharacter currentPC)
	{
		if (equip == null)
		{
			return true;
		}
		final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
		final PrerequisiteTest test = factory.getTest(prereqList.getKind());

		if (test == null)
		{
			Logging.errorPrintLocalised("PrereqHandler.Unable_to_find_implementation", prereqList.toString()); //$NON-NLS-1$
			return false;
		}
		int total=0;
		try
		{
			total = test.passes(prereqList, equip, currentPC);
		}
		catch (PrerequisiteException pe)
		{
			Logging.errorPrintLocalised("PrereqHandler.Exception_in_test", pe); //$NON-NLS-1$
		}
		return total>0;
	}

	public static final String toHtmlString(final List<Prerequisite> anArrayList)
	{
		if (anArrayList==null || anArrayList.isEmpty())
		{
			return ""; //$NON-NLS-1$
		}

		final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();

		final StringBuffer pString = new StringBuffer(anArrayList.size() * 20);

		String delimiter = ""; //$NON-NLS-1$

		for (Prerequisite prereq : anArrayList)
		{
			final PrerequisiteTest preHtml = factory.getTest(prereq.getKind());
			if (preHtml==null)
			{
				Logging.errorPrintLocalised("PrereqHandler.No_known_formatter", prereq.getKind()); //$NON-NLS-1$
			}
			else
			{
				pString.append(delimiter);
				if (prereq.getLevelQualifier() > 0)
					pString.append("at level "+prereq.getLevelQualifier()+":");
				pString.append(preHtml.toHtmlString(prereq) );

					delimiter = PropertyFactory.getString("PrereqHandler.HTML_prerequisite_delimiter"); //$NON-NLS-1$
			}
		}

		return pString.toString();
	}


}
