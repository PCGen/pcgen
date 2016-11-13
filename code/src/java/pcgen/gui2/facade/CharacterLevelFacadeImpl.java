/**
 * CharacterLevelFacadeImpl.java
 * Copyright James Dempsey, 2010
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
 * Created on 02/07/2010 3:20:17 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.ClassFacade;

/**
 * The Class {@code CharacterLevelFacadeImpl} is an implementation of
 * the CharacterLevelFacade interface for the new user interface. It provides  
 * a container for information about a particular level of the character. 
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public class CharacterLevelFacadeImpl implements CharacterLevelFacade
{

	private int characterLevel;
	private ClassFacade classFacade;
	
	public CharacterLevelFacadeImpl(ClassFacade classFacade, int level)
	{
		this.classFacade = classFacade;
		this.characterLevel = level;
	}

	/**
	 * @return the characterLevel
	 */
	public int getCharacterLevel()
	{
		return characterLevel;
	}

	/**
	 * @return The class taken for this level
	 */
	ClassFacade getSelectedClass()
	{
		return classFacade;
	}

	@Override
	public String toString()
	{
		return characterLevel + " - " + String.valueOf(classFacade);
	}

	
}
