/*
 * CompanionSupportFacade.java
 * Copyright 2012 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Mar 4, 2012, 6:08:03 PM
 */
package pcgen.core.facade;

import pcgen.core.facade.util.ListFacade;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface CompanionSupportFacade
{
	/**
	 * This adds a companion to this character.
	 * A CharacterFacade is used instead of a CompanionFacade to make
	 * sure that the added companion is an existing character. This enforces
	 * that this method doesn't try to create a new character behind the scenes.
	 * <br>
	 * To implement this method, the added companion would need to be wrapped in
	 * another CompanionFacade such that the backing character can be garbage
	 * collected if the character is closed.
	 * @param companion the companion to add
	 */
	public void addCompanion(CharacterFacade companion);
	/**
	 * Removes a companion from this character.
	 * The companion to removed will be one retrieved from the <code>getCompanions</code>
	 * list.
	 * @param companion the companion to remove
	 */
	public void removeCompanion(CompanionFacade companion);
	
	/**
	 * Returns a list of companions that the character can create.
	 * Elements of the list are expected to be bare-bones implementations
	 * of CompanionStubFacade 
	 * @return a list of companion stubs
	 */
	public ListFacade<CompanionStubFacade> getAvailableCompanions();
	public int getRemainingCompanions(String type);
	public int getMaxCompanions(String type);
	public ListFacade<CompanionFacade> getCompanions();
}
