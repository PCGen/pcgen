/*
 * PrerequisiteWriterInterface.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.8 $
 *
 * Last Editor: $Author: binkley $
 *
 * Last Edited: $Date: 2005/10/18 20:23:56 $
 *
 */

package pcgen.persistence.lst.output.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * This class handles writing pre reqs for LST Tokens
 */
public class AbstractPrerequisiteWriter
{

	protected void checkValidOperator( Prerequisite prereq, PrerequisiteOperator[] comparators ) throws PersistenceLayerException
	{
		String comparatorString = "";
		for (int i = 0; i < comparators.length; i++)
		{
			PrerequisiteOperator comparator = comparators[i];
			if (prereq.getOperator().equals(comparators[i]))
			{
				return;
			}
			if (i > 0)
			{
				comparatorString += ", ";
			}
			comparatorString += comparator;
		}

		String kind = prereq.getKind();
		if (kind == null)
		{
			kind = "<NULL>";
		}
		throw new PersistenceLayerException("Cannot write token: LST syntax only supports " + comparatorString + " operators for PRE" + kind.toUpperCase() + ": " + prereq.toString());
	}


	protected boolean checkForPremultOfKind(final Prerequisite prereq, final String kind, final boolean multiplesOnly)
	{
		//
		// PREMULT ?
		//
		if (prereq.getKind() == null)
		{
			//
			// Are all the sub-prereqs the desired kind?
			//
			for (Iterator iter = prereq.getPrerequisites().iterator(); iter.hasNext(); )
			{
				final Prerequisite element = (Prerequisite) iter.next();
				if (!kind.equalsIgnoreCase(element.getKind()))
				{
					return false;
				}
				if (multiplesOnly && !element.isCountMultiples())
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Meant to be over-ridden
	 * TODO  Does this and its overriding methods need to throw IOException?
	 * 
	 * @param writer
	 * @param prereq
	 * @return false if not over ridden
	 * @throws IOException
	 */
	public boolean specialCase(Writer writer, Prerequisite prereq) throws IOException
	{
		try
		{
			return false;
		}
		catch (Exception e)
		{
			throw new IOException();
		}
	}
}
