/*
 * GameModeRollMethod.java
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
 * Created on September 2, 2002, 2:25 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.system;


/**
 * <code>GameModeRollMethod</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public class GameModeRollMethod
{
	private String methodName;
	private String methodRoll;
	
	/**
	 * Constructor
	 * @param argName
	 * @param argRoll
	 */
	public GameModeRollMethod(final String argName, final String argRoll)
	{
		methodName = argName;
		methodRoll = argRoll;
	}

	/**
	 * Get the roll method name for this game mode
	 * @return roll method name for this game mode
	 */
	public String getMethodName()
	{
		return methodName;
	}
	
	/**
	 * Get the roll method for this game mode
	 * @return the roll method for this game mode
	 */
	public String getMethodRoll()
	{
		return methodRoll;
	}
}
