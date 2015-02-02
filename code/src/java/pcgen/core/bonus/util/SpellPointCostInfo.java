/*
 * SpellPointCostInfo.java
 *
 * Copyright 2008
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
 * Created on Mar 21, 2008
 *
 * Current Ver: $Revision: 
 * Last Editor: $Author:
 * Last Edited: $Date: 
 *
 */
package pcgen.core.bonus.util;

/**
 * @author Joe.Frazier
 *
 */
public class SpellPointCostInfo 
{
	private String spellPointPart; 
	private SpellPointFilterType spellPointPartFilter;
	private String spellPointPartFilterValue;
	private boolean isVirtual = false;
	
	public enum SpellPointFilterType
	{
		SPELL,
		SCHOOL,
		SUBSCHOOL
	}
	
	
	/**
	 * @param spellPointPartFilter
	 * @param spellPointPartFilterValue
	 * @param spellPointPart
	 */
	public SpellPointCostInfo(SpellPointFilterType spellPointPartFilter,
			String spellPointPartFilterValue, String spellPointPart, boolean isVirtual) 
	{
		super();
		this.spellPointPartFilter = spellPointPartFilter;
		this.spellPointPartFilterValue = spellPointPartFilterValue;
		this.spellPointPart = spellPointPart;
		this.isVirtual = isVirtual;
	}
	/**
	 * @return the spellPointPart
	 */
	public String getSpellPointPart() {
		return spellPointPart;
	}
	/**
	 * @param spellPointPart the spellPointPart to set
	 */
	public void setSpellPointPart(String spellPointPart) {
		this.spellPointPart = spellPointPart;
	}
	/**
	 * @return the spellPointPartFilter
	 */
	public SpellPointFilterType getSpellPointPartFilter() {
		return spellPointPartFilter;
	}
 
	/**
	 * @return the spellPointPartFilterValue
	 */
	public String getSpellPointPartFilterValue() {
		return spellPointPartFilterValue;
	}
	/**
	 * @param spellPointPartFilterValue the spellPointPartFilterValue to set
	 */
	public void setSpellPointPartFilterValue(String spellPointPartFilterValue) {
		this.spellPointPartFilterValue = spellPointPartFilterValue;
	}
	/**
	 * @param spellPointPartFilter the spellPointPartFilter to set
	 */
	public void setSpellPointPartFilter(SpellPointFilterType spellPointPartFilter) {
		this.spellPointPartFilter = spellPointPartFilter;
	}
	/**
	 * @return isVirtual
	 */
	public boolean isVirtual() {
		return isVirtual;
	}
	/**
	 * @param virt the isVirutal to set
	 */
	public void setIsVirtual(boolean virt) {
		this.isVirtual = virt; 
	}
}
