/*
 * PrereqObject.java
 * Copyright 2006 Aaron Divinsky <boomer70@yahoo.com>
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
 */
package pcgen.core;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * This class implements support for prerequisites for an object.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class PrereqObject implements Cloneable
{
	/** The list of prerequisites */
	private List<Prerequisite> thePrereqs = null;

	/** Used to split an unparsed prereq string that uses angle brackets. */
	private static final String angleSplit = "[<>\\|]"; //$NON-NLS-1$
	/** Used to split an uparsed prereq string that uses square brackets. */
	private static final String squareSplit = "[\\[\\]\\|]"; //$NON-NLS-1$

	/**
	 * Adds a <tt>Collection</tt> of <tt>Prerequisite</tt> objects.
	 * 
	 * @param prereqs A <tt>Collection</tt> of <tt>Prerequisite</tt> objects.
	 */
	public void addPrerequisites( final Collection<Prerequisite> prereqs )
	{
		if ( prereqs == null )
		{
			return;
		}
		if ( thePrereqs == null )
		{
			thePrereqs = new ArrayList<Prerequisite>(prereqs.size());
		}
		for ( final Prerequisite pre : prereqs )
		{
			addPreReq( pre );
		}
	}

	/**
	 * Takes a string containing valid PRExxx tags separated by a delimiter
	 * and parses and adds them to the prerequisite list.
	 * 
	 * <p>For example, the string <br />
	 * <code>[PREVARGTEQ:Foo,2][PRECLASS:1,Fighter=1]</code><br />
	 * would be parsed and and added with the call <br />
	 * <code>addPrereqisite(string, '[');
	 * 
	 * @param unparsed The unparsed prerequisite string.
	 * @param aDelim The delimiter that separates multiple prerequisites.
	 */
	public void addPrerequisites( final String unparsed, final char aDelim )
	{
		final String[] tokens = unparsed.split(aDelim == '<' ? angleSplit 
															 : squareSplit);
		try
		{
			final PreParserFactory factory = PreParserFactory.getInstance();

			for ( final String pre : tokens )
			{
				final Prerequisite prereq = factory.parse(pre);

				if (prereq != null)
				{
					addPreReq(prereq);
				}
			}
		}
		catch (PersistenceLayerException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Tests if the specified PlayerCharacter passes all the prerequisites.
	 * 
	 * @param aPC The <tt>PlayerCharacter</tt> to test.
	 * 
	 * @return <tt>true</tt> if the PC passes all the prerequisites.
	 */
	public boolean qualifies( final PlayerCharacter aPC )
	{
		if (thePrereqs == null)
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, aPC, null);
	}

	/** TODO This is rather foobar'd */
	public final boolean passesPreReqToGain(final Equipment p, final PlayerCharacter aPC)
	{
		if (!hasPreReqs())
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, p, aPC);
	}

	/**
	 * Get the list of <tt>Prerequesite</tt>s.
	 * 
	 * @return An unmodifiable <tt>List</tt> of <tt>Prerequesite</tt>s or <tt>
	 * null</tt> if no prerequisites have been set.
	 */
	public List<Prerequisite> getPreReqList()
	{
		if ( thePrereqs != null )
		{
			return Collections.unmodifiableList(thePrereqs);
		}
		return Collections.emptyList();
	}

	/**
	 * Clear the prerequisite list.
	 */
	public final void clearPreReq()
	{
		thePrereqs = null;
	}

	/**
	 * Add a <tt>Prerequesite</tt> to the prerequisite list.
	 * 
	 * @param preReq The prerequisite to add.
	 */
	public final void addPreReq(final Prerequisite preReq)
	{
		addPreReq(preReq, -1);
	}

	/**
	 * Tests to see if this object has any prerequisites associated with it.
	 * 
	 * @return <tt>true</tt> if it has prereqs
	 */
	public boolean hasPreReqs()
	{
		return thePrereqs != null;
	}

	/**
	 * Add a <tt>Prerequesite</tt> to the prereq list with a level qualifier.
	 * 
	 * <p>If the Prerequisite kind is &quot;clear&quot; all the prerequisites
	 * will be cleared from the list.
	 * 
	 * @param preReq The <tt>Prerequisite</tt> to add.
	 * @param levelQualifier A level qualifier.
	 * 
	 * @see pcgen.core.prereq.Prerequisite#setLevelQualifier(int)
	 */
	public final void addPreReq(final Prerequisite preReq, final int levelQualifier)
	{
		if ( preReq == null )
		{
			return;
		}
		if (Prerequisite.CLEAR_KIND.equals(preReq.getKind())) //$NON-NLS-1$
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
	 * Returns true if this object has any prerequisites of the kind that
	 * is passed in.
	 *
	 * @param matchType The kind of Prerequisite to test for.
	 * 
	 * @return <tt>true</tt> if this object has a prerequisite of the kind that
	 * is passed in
	 * 
	 * @see pcgen.core.prereq.Prerequisite#getKind()
	 */
	public final boolean hasPreReqTypeOf(final String matchType)
	{
		if (!hasPreReqs())
		{
			return false;
		}

		for (Prerequisite prereq : getPreReqList())
		{
			if (PrerequisiteUtilities.hasPreReqKindOf(prereq, matchType))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the number of prerequisites currently associated.
	 * 
	 * @return the number of prerequesites
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
	 * Adds the prerequisites to the <tt>Collection</tt> passed in.
	 * 
	 * @param A <tt>Collection</tt> to add to.
	 */
	final void addPreReqTo(final Collection<Prerequisite> collection)
	{
		if (thePrereqs != null)
		{
			collection.addAll(thePrereqs);
		}
	}

	/**
	 * Returns the prerequisites in &quot;PCC&quot; format.
	 * 
	 * @return A string in &quot;PCC&quot; format or an empty string.
	 */
	public String getPCCText()
	{
		if ( thePrereqs == null )
		{
			return Constants.EMPTY_STRING;
		}
		
		final StringWriter writer = new StringWriter();
		for ( final Prerequisite prereq : thePrereqs )
		{
			final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			writer.write(Constants.PIPE);
			try
			{
				prereqWriter.write(writer, prereq);
			}
			catch (PersistenceLayerException e)
			{
				e.printStackTrace();
			}
		}
		return writer.toString();
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
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
