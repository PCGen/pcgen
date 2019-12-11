/*
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
 *
 * Copyright 2006 Aaron Divinsky <boomer70@yahoo.com>
 */
package pcgen.core;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.core.prereq.Prerequisite;

/**
 * This class stores an association between an object and a set of prereqs.
 * Refactored from ChoiceInfo originally written by
 * Andrew Wilson &lt;nuance@sourceforge.net&gt;
 * @param <T> 
 */
public class QualifiedObject<T> extends ConcretePrereqObject implements QualifyingObject
{

	private T theObject;

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
	public QualifiedObject(final T anObj, final List<Prerequisite> aPrereqList)
	{
		theObject = anObj;
		addAllPrerequisites(aPrereqList);
	}

	/**
	 * Constructor 
	 * @param anObj
	 * @param prereq
	 */
	public QualifiedObject(final T anObj, Prerequisite prereq)
	{
		theObject = anObj;
		addPrerequisite(prereq);
	}

	/**
	 * Get the qualifiying object. Will always return the object 
	 * if no character is passed in.
	 * 
	 * @param aPC Character to be checked or null
	 * @param owner TODO
	 * @return qualifying object
	 */
	public T getObject(final PlayerCharacter aPC, CDOMObject owner)
	{
		if (aPC == null || qualifies(aPC, owner))
		{
			return theObject;
		}
		return null;
	}

	/**
	 * Get the qualifiying object. Will always return the object
	 * 
	 * @return qualifying object
	 */
	public T getRawObject()
	{
		return theObject;
	}

	/**
	 * Set qualifying object 
	 * @param anObject
	 */
	public void setObject(final T anObject)
	{
		theObject = anObject;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return "Object:"
				+ theObject.toString()
				+ ", Prereq:"
				+ getPrerequisiteList().toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof QualifiedObject)
		{
			QualifiedObject<?> other = (QualifiedObject<?>) obj;
			if (!equalsPrereqObject(other))
			{
				return false;
			}
			if (other.theObject == null)
			{
				return theObject == null;
			}
			return other.theObject.equals(theObject);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getPrerequisiteCount() * 23 + (theObject == null ? -1 : theObject.hashCode());
	}

}
