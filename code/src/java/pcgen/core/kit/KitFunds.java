/*
 * KitFunds.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 20, 2005, 9:46 AM
 *
 * $Id: KitFunds.java,v 1.6 2006/02/16 15:10:14 karianna Exp $
 */
package pcgen.core.kit;

import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

import java.io.Serializable;
import java.util.List;

/**
 * <code>KitFunds</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.6 $
 */
public final class KitFunds extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String name = "";
	private String qty = "1";

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient int theQty = 0;

	/**
	 * Constructor
	 * @param fundsName
	 */
	public KitFunds(final String fundsName)
	{
		name = fundsName;
	}

	/**
	 * Set the quantity
	 * @param argQty
	 */
	public void setQty(final String argQty)
	{
		qty = argQty;
	}

	/**
	 * Get the quantity
	 * @return quantity
	 */
	public String getQty()
	{
		return qty;
	}

	public String toString()
	{
		return qty + ' ' + name;
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		theQty = -1;
		if (qty == null)
		{
			return false;
		}
		theQty = aPC.getVariableValue(getQty(), "").intValue();
		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		aPC.adjustGold(theQty);
	}

	public Object clone()
	{
		KitFunds aClone = (KitFunds)super.clone();

		aClone.name = name;
		aClone.qty = qty;

		return aClone;
	}

	public String getObjectName()
	{
		return "Funds";
	}
}
