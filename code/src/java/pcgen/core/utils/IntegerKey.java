/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.utils;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a Typesafe enumeration of legal Integer Characteristics of an object.
 */
public final class IntegerKey 
{
	    
	/** Key to a characteristic defining the number of levels to be added to a spell. */
	public static final IntegerKey ADD_SPELL_LEVEL = new IntegerKey();
	/** Key to a characteristic defining the type of ability. */
	public static final IntegerKey ABILITY_TYPE = new IntegerKey();
	/** Key to a characteristic defining the number of hit dice the object has. */
	public static final IntegerKey HIT_DIE = new IntegerKey();
	/** Key to a characteristic defining the level of the object. */
	public static final IntegerKey LEVEL = new IntegerKey();
	/** Key to a characteristic defining the number of pages of the object. */
	public static final IntegerKey NUM_PAGES = new IntegerKey();
	/** Key to a characteristic defining the loading rank of the object. */
	public static final IntegerKey RANK = new IntegerKey();
	/** Key to a characteristic defining how visible the object is. */
	public static final IntegerKey VISIBLE = new IntegerKey();

    /**
     * Private constructor to stop instantiation of this class. 
     */
    private IntegerKey() 
    {
        //Only allow instantation here
    }
}
