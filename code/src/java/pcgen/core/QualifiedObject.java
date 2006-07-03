/*
 * QualifiedObject.java
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

import java.util.List;
import pcgen.core.prereq.Prerequisite;
import java.util.ArrayList;
import java.util.Arrays;
import pcgen.persistence.lst.prereq.PreParserFactory;
import java.util.Iterator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.core.prereq.PrereqHandler;

/**
 * This class stores an association between an object and a set of prereqs.
 * Refactored from ChoiceInfo originally written by
 * Andrew Wilson <nuance@sourceforge.net>
 * @author Aaron Divinsky <boomer70@yahoo.com>
 */
public class QualifiedObject<T>
{
	private T theObject = null;
	private List<Prerequisite> thePrereqs = null;

	private static final String angleSplit = "[<>\\|]";
	private static final String squareSplit = "[\\[\\]\\|]";

	public QualifiedObject()
	{
	}

	public QualifiedObject(final T anObj)
	{
		theObject = anObj;
	}

	public QualifiedObject( final T anObj, final List<Prerequisite> aPrereqList )
	{
		theObject = anObj;
		thePrereqs = new ArrayList<Prerequisite>( aPrereqList );
	}

	public T getObject( final PlayerCharacter aPC )
	{
		if ( qualifies( aPC ) )
		{
			return theObject;
		}
		return null;
	}

	public void setObject( final T anObject )
	{
		theObject = anObject;
	}

	public void addPrerequisites( final List<Prerequisite> prereqs )
	{
		if ( thePrereqs == null )
		{
			thePrereqs = new ArrayList<Prerequisite>(prereqs.size());
		}
		thePrereqs.addAll( prereqs );
	}

	public boolean qualifies( final PlayerCharacter aPC)
	{
		if (thePrereqs == null)
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, aPC, null);
	}

	public static QualifiedObject<String> createQualifiedObject( final String unparsed, final char aDelim )
	{

		int start = unparsed.indexOf(aDelim);

		if ((start < 0))
		{
			// no Prereqs, assign directly to key field
			return new QualifiedObject<String>(unparsed);
		}
		else
		{
			List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
			String obj = "";

			List<String> tokens = Arrays.asList(unparsed.split(aDelim == '<'
																? angleSplit
																: squareSplit));
			Iterator<String> tokIt  = tokens.iterator();

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
						prereqs.add(prereq);
					}
				}
			}
			catch (PersistenceLayerException e)
			{
				e.printStackTrace();
			}
			return new QualifiedObject<String>( obj, prereqs );
		}
	}

}
