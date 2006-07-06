/*
 * ClassType.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;



/**
 * <code>Campaign</code>.
 *
 * @author Felipe Diniz <fdiniz@users.sourceforge.net>
 * @version $Revision$
 */
public final class ClassType implements Cloneable
{
	private String theName = "";
	private String crFormula = "";
	private boolean xpPenalty = true;
	private boolean isMonster = false;

	/**
     * Get the name of the class type 
     * @return name of the class type
	 */
    public String getName()
	{
		return theName;
	}

    /**
     * Set the name of the class type
     * @param aName
     */
	public void setName( final String aName )
	{
		theName = aName;
	}

	/**
	 * Set the CR Formula
	 * @param crFormula
	 */
	public void setCRFormula(final String crFormula)
	{
		this.crFormula = crFormula;
	}

	/**
	 * Get the CR formula
	 * @return CR Formula
	 */
	public String getCRFormula()
	{
		return crFormula;
	}

	/**
	 * Set the monster
	 * @param monster
	 */
	public void setMonster(final boolean monster)
	{
		isMonster = monster;
	}

	/**
	 * is monster
	 * @return TRUE if it is a monster
	 */
	public boolean isMonster()
	{
		return isMonster;
	}

	/**
	 * Set the XP penalty
	 * @param xpPenalty
	 */
	public void setXPPenalty(final boolean xpPenalty)
	{
		this.xpPenalty = xpPenalty;
	}

	/**
	 * Get the XP penalty
	 * @return true if there is a penalty
	 */
	public boolean getXPPenalty()
	{
		return xpPenalty;
	}

	public Object clone()
		throws CloneNotSupportedException
	{
		final ClassType newClassType = (ClassType) super.clone();

		newClassType.theName = new String(theName);
		newClassType.isMonster = isMonster;

		newClassType.crFormula = new String(crFormula);
		newClassType.xpPenalty = xpPenalty;

		return newClassType;
	}
}
