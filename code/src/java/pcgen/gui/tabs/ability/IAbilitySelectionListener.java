/*
 * IAbilitySelectionListener.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui.tabs.ability;

import pcgen.core.Ability;

/**
 * This interface is used to advise interested parties about Ability Selection
 * events.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public interface IAbilitySelectionListener
{
	/**
	 * This method is called when the user highlights an Ability in the list.
	 * The method can be called with <tt>null</tt> if no Ability is currently
	 * selected.
	 * 
	 * @param anAbility The highlighted ability or <tt>null</tt>
	 */
	void abilitySelected(final Ability anAbility);
	
	/**
	 * This method is called when the user chooses to add an Ability to the
	 * character.
	 * 
	 * @param anAbility The chosen Ability.
	 * 
	 * @return Return <tt>true</tt> to allow the ability to be added.
	 */
	boolean addAbility(final Ability anAbility);
	
	/**
	 * This method is called when the user chooses to remove an Ability from
	 * the character.
	 * 
	 * @param anAbility The Ability to remove.
	 * 
	 * @return Return <tt>true</tt> to allow the Ability to be removed.
	 */
	boolean removeAbility(final Ability anAbility);
}
