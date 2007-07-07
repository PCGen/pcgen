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
import java.util.Collections;

import pcgen.persistence.lst.prereq.PreParserFactory;
import java.util.Iterator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.core.prereq.PrereqHandler;

/**
 * This class stores an association between an object and a set of prereqs.
 * Refactored from ChoiceInfo originally written by
 * Andrew Wilson <nuance@sourceforge.net>
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @param <T> 
 */
public class QualifiedObject<T>
{

	private T theObject = null;
	private List<Prerequisite> thePrereqs = null;

	private static final String angleSplit = "[<>\\|]";
	private static final String squareSplit = "[\\[\\]\\|]";

	/** Default Constructor */
	public QualifiedObject()
	{
	    // Do Nothing
	}

    /**
     * Constructor
     * @param anObj
     */
	public QualifiedObject(final T anObj)
	{
		theObject = anObj;
	}

	/**
     * Constructor 
     * @param anObj
     * @param aPrereqList
	 */
    public QualifiedObject( final T anObj, final List<Prerequisite> aPrereqList )
	{
		theObject = anObj;
		thePrereqs = new ArrayList<Prerequisite>( aPrereqList );
	}

	/**
     * Get the qualifiying object. Will always return the object 
     * if no character is passed in.
     * 
     * @param aPC Character to be checked or null
     * @return qualifying object
	 */
    public T getObject( final PlayerCharacter aPC )
	{
		if (aPC == null || qualifies( aPC ) )
		{
			return theObject;
		}
		return null;
	}

    /**
     * Get an unmodifiable copy of the list of prereqs.
     * @return The prereqs.
     */
    public List<Prerequisite> getPrereqs()
    {
		if ( thePrereqs == null )
		{
			return Collections.emptyList();
		}
    	return Collections.unmodifiableList(thePrereqs);
    }
    
	/**
     * Set qualifying object 
     * @param anObject
	 */
    public void setObject( final T anObject )
	{
		theObject = anObject;
	}

	/**
     * Add PreReqs 
     * @param prereqs
	 */
    public void addPrerequisites( final List<Prerequisite> prereqs )
	{
		if ( thePrereqs == null )
		{
			thePrereqs = new ArrayList<Prerequisite>(prereqs.size());
		}
		thePrereqs.addAll( prereqs );
	}

	/**
     * Return true if the object qualifies for all of the Pre Reqs 
     * @param aPC
     * @return true if the object qualifies for all of the Pre Reqs
	 */
    public boolean qualifies( final PlayerCharacter aPC)
	{
		if (thePrereqs == null)
		{
			return true;
		}

		return PrereqHandler.passesAll(thePrereqs, aPC, null);
	}

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("Object:");
		result.append(theObject.toString());
		result.append(", Prereq:");
		result.append(thePrereqs.toString());
		// TODO Auto-generated method stub
		return result.toString();
	}
    
    

	/**
     * Create the qualified object 
     * @param unparsed
     * @param aDelim
     * @return qualified object
	 */
    public static QualifiedObject<String> createQualifiedObject( final String unparsed, final char aDelim )
	{

		int start = unparsed.indexOf(aDelim);

		if ((start < 0))
		{
			// no Prereqs, assign directly to key field
			return new QualifiedObject<String>(unparsed);
		}
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
    
    public static class AutoQualifiedObject<AT> extends QualifiedObject<AT>
    {
    	//Just an identifier for 5.14
    	
		public AutoQualifiedObject()
		{
			super();
		}

		public AutoQualifiedObject(AT anObj, List<Prerequisite> aPrereqList)
		{
			super(anObj, aPrereqList);
		}

		public AutoQualifiedObject(AT anObj)
		{
			super(anObj);
		}
    }
}
