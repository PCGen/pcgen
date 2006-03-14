/*
 *  Variable.java
 *  Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author Scott Ellsworth
 */
package pcgen.core;


/**
 * A Variable is something that alters a computed variable within pcgen
 * Examples include stats, saves, and base attack bonuses.
 * @author Scott Ellsworth
 * @version $Revision$
 */
public class Variable
{
	/** The name of the variable. */
	private String name;
	/** The formula which should be used to calculate the value of the variable. */
	private String value;
	/** The level at which the variable should be applied, 0 if always.*/
	private int level;

	/**
	 * Create a new variable definition.
	 * @param level The level at which the variable should be applied, 0 if always.
	 * @param variableName The name of the variable.
	 * @param defaultFormula The formula which should be used to calculate its value.
	 */
	public Variable(final int level, final String variableName, final String defaultFormula)
	{
		this.level = level;
		name = variableName;
		value = defaultFormula;
	}

	/**
	 * @return The definition of the variable (level, name and value).
	 */
	public String getDefinition()
	{
		return level + "|" + name + "|" + value;
	}

	/**
	 * @return The variable's level.
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @return The variable's name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return The variable's name in upper case.
	 */
	public String getUpperName()
	{
		return name.toUpperCase();
	}
	
	/**
	 * @return Returns the variable's value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(final String value)
	{
		this.value = value;
	}

}
