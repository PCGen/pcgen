/*
 * PrereqObject.java
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
 * Current Version: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * Copyright 2006 Aaron Divinsky <boomer70@yahoo.com>
 */
package pcgen.core;

import pcgen.core.prereq.Prerequisite;
import java.util.List;
import java.util.ArrayList;
import pcgen.core.prereq.PrereqHandler;
import java.util.Arrays;
import pcgen.persistence.lst.prereq.PreParserFactory;
import java.util.Iterator;
import pcgen.persistence.PersistenceLayerException;
import java.util.Collections;
import pcgen.core.prereq.PrerequisiteUtilities;
import java.util.Collection;

public class PrereqObject implements Cloneable
{
	private List<Prerequisite> thePrereqs = null;

	private static final String angleSplit = "[<>\\|]";
	private static final String squareSplit = "[\\[\\]\\|]";

	public void addPrerequisites( final List<Prerequisite> prereqs )
	{
		if ( thePrereqs == null )
		{
			thePrereqs = new ArrayList<Prerequisite>(prereqs.size());
		}
		thePrereqs.addAll( prereqs );
	}

	public void addPrerequisites( final String unparsed, final char aDelim )
	{
		int start = unparsed.indexOf(aDelim);

		String obj = "";

		final List<String> tokens = Arrays.asList(unparsed.split(aDelim == '<'
															? angleSplit
															: squareSplit));
		final Iterator<String> tokIt  = tokens.iterator();

		// extract and assign the choice from the unparsed string
		obj = tokIt.next();

		try
		{
			final PreParserFactory factory = PreParserFactory.getInstance();

			for (; tokIt.hasNext();)
			{
				final Prerequisite prereq = factory.parse(tokIt.next());

				if (prereq != null)
				{
					thePrereqs.add(prereq);
				}
			}
		}
		catch (PersistenceLayerException e)
		{
			e.printStackTrace();
		}
	}

	final void setPreReq(final int index, final Prerequisite aPreReq)
	{
		thePrereqs.set(index, aPreReq);
	}

	public boolean qualifies( final PlayerCharacter aPC)
	{
		if (thePrereqs == null)
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, aPC, null);
	}

	/** TODO This is rather foobar'd */
	final boolean passesPreReqToGain(final PObject p, PlayerCharacter currentPC)
	{
		if (getPreReqCount() == 0)
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, (Equipment) p, currentPC);
	}


	/**
	 * Get the pre requesite at an index
	 * @param i
	 * @return the pre requesite at an index
	 */
	public final Prerequisite getPreReq(final int i)
	{
		return thePrereqs.get(i);
	}

	/**
	 * Get the list of pre-requesites
	 * @return the list of pre-requesites
	 */
	public List<Prerequisite> getPreReqList()
	{
		if ( thePrereqs != null )
		{
			return Collections.unmodifiableList(thePrereqs);
		}
		return null;
	}

	/**
	 * Clear the pre requestite list
	 */
	public final void clearPreReq()
	{
		thePrereqs = null;
	}

	/**
	 * Add a Pre requesite to the prereq list with no level qualifier
	 * @param preReq
	 */
	public final void addPreReq(final Prerequisite preReq)
	{
		addPreReq(preReq, -1);
	}

	/**
	 * Add a Pre requesite to the prereq list with a level qualifier
	 * @param preReq
	 * @param levelQualifier
	 */
	public final void addPreReq(final Prerequisite preReq, final int levelQualifier)
	{
		if ("clear".equals(preReq.getKind()))
		{
			thePrereqs = null;
		}
		else
		{
			if (thePrereqs == null)
			{
				thePrereqs = new ArrayList<Prerequisite>();
			}
			if (levelQualifier > 0)
			{
				preReq.setLevelQualifier(levelQualifier);
			}
			thePrereqs.add(preReq);
		}
	}

	/**
	 * Returns true if this object has a pre requestie of the type that
	 * is passed in.
	 *
	 * @param matchType
	 * @return true if this object has a pre requestie of the type that
	 * is passed in
	 */
	public final boolean hasPreReqTypeOf(final String matchType)
	{
		if (getPreReqCount() == 0)
		{
			return false;
		}

		for (int i = 0; i < getPreReqCount(); ++i)
		{
			final Prerequisite prereq = getPreReq(i);

			if (prereq != null)
			{
				if (matchType == null && prereq.getKind() == null)
				{
					return true;
				}
				if (matchType.equalsIgnoreCase(prereq.getKind()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get the number of pre requesites
	 * @return the number of pre requesites
	 */
	public final int getPreReqCount()
	{
		if (thePrereqs == null)
		{
			return 0;
		}

		return thePrereqs.size();
	}

	/**
	 * Returns the pre requesites as an HTML String
	 * @param aPC
	 * @return the pre requesites as an HTML String
	 */
	public final String preReqHTMLStrings(final PlayerCharacter aPC)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null, thePrereqs, true);
	}

	/**
	 * Returns the pre requesites as an HTML String with a header
	 * @param aPC
	 * @param includeHeader
	 * @return the pre requesites as an HTML String
	 */
	public String preReqHTMLStrings(final PlayerCharacter aPC, final boolean includeHeader)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null, thePrereqs, includeHeader);
	}

	/**
	 * Returns the pre requesites as an HTML String given an object
	 * @param aPC
	 * @param p
	 * @return the pre requesites as an HTML String given an object
	 */
	public final String preReqHTMLStrings(final PlayerCharacter aPC, final PObject p)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, p, thePrereqs, true);
	}

	/**
	 * Creates the requirement string for printing.
	 * @return the requirement string for printing
	 */
	public final String preReqStrings()
	{
		return PrereqHandler.toHtmlString(thePrereqs);
	}

	/**
	 * Add the pre-reqs to this collection
	 * @param collection
	 */
	final void addPreReqTo(final Collection<Prerequisite> collection)
	{
		if (thePrereqs != null)
		{
			collection.addAll(thePrereqs);
		}
	}

	public Object clone()
		throws CloneNotSupportedException
	{
		final PrereqObject obj = (PrereqObject)super.clone();
		if ( thePrereqs != null )
		{
			obj.thePrereqs = new ArrayList<Prerequisite> (thePrereqs);
		}
		return obj;
	}
}
