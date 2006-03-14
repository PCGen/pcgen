/*
 * KitWrapper.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on September 23, 2002, 8:55 PM
 */
package pcgen.core.kit;

import pcgen.core.PObject;

import java.math.BigDecimal;

/**
 * <code>KitWrapper</code>.
 *
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision$
 */
public final class KitWrapper
{
	private BigDecimal cost = null;
	private Object obj = null;
	private PObject aPObject = null;
	private boolean free = false;
	private int qty = 1;

	/**
	 * Constructor
	 * @param argObj
	 * @param argFree
	 */
	public KitWrapper(final Object argObj, final boolean argFree)
	{
		obj = argObj;
		free = argFree;
	}

	/**
	 * Get object
	 * @return object
	 */
	public Object getObject()
	{
		return obj;
	}

	/**
	 * Set the PObject
	 * @param argPObject
	 */
	public void setPObject(final PObject argPObject)
	{
		aPObject = argPObject;
	}

	/**
	 * Return the PObject
	 * @return PObject
	 */
	public PObject getPObject()
	{
		return aPObject;
	}
}
