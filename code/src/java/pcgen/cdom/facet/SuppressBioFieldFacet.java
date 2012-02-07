/*
 * SuppressBioFieldFacet.java
 * Copyright James Dempsey, 2012
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
 * Created on 02/01/2012 3:15:12 PM
 *
 * $Id$
 */
package pcgen.cdom.facet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;

/**
 * The Class <code>SuppressBioFieldFacet</code> tracks the biography fields that 
 * should be hidden from output. 
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class SuppressBioFieldFacet extends AbstractStorageFacet
{

	private final Class<?> thisClass = getClass();

	/**
	 * Set whether the field should be hidden from output. 
	 * 
	 * @param id The CharID representing the target Player Character
	 * @param field The BiographyField to set export suppression rules for.
	 * @param suppress Should the field be hidden from output.
	 */
	public void setSuppressField(CharID id, BiographyField field, boolean suppress)
	{
		@SuppressWarnings("unchecked")
		Set<BiographyField> suppressedFields =
				(Set<BiographyField>) getCache(id, thisClass);
		if (suppressedFields == null)
		{
			suppressedFields =
					Collections.synchronizedSet(new HashSet<BiographyField>());
			setCache(id, thisClass, suppressedFields);
		}

		if (suppress)
		{
			suppressedFields.add(field);
		}
		else
		{
			suppressedFields.remove(field);
		}
	}

	/**
	 * Check whether the field should be hidden from output for the character.
	 *  
	 * @param id The CharID of the Player Character being queried.
	 * @param field The BiographyField to set export suppression rules for.
	 * @return true if the field should not be output, false if it may be.
	 */
	public boolean getSuppressField(CharID id, BiographyField field)
	{
		@SuppressWarnings("unchecked")
		Set<BiographyField> suppressedFields = (Set<BiographyField>) getCache(id, thisClass);
		return suppressedFields != null && suppressedFields.contains(field);
	}

	@Override
	public void copyContents(CharID source, CharID copy)
	{
		Set<BiographyField> set =
				(Set<BiographyField>) getCache(source, thisClass);
		if (set != null)
		{
			Set<BiographyField> copyset =
					Collections.synchronizedSet(new HashSet<BiographyField>());
			copyset.addAll(set);
			setCache(copy, thisClass, copyset);
		}
	}

}
