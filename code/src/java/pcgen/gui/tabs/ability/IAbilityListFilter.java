/*
 * IAbilityListFilter.java
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
 * This interface is used to allow interested parties to filter the list of 
 * Abilities displayed by the <tt>AbilityModel</tt> class.
 * 
 * 
 * @author boomer70 <boomer70@yahoo.com
 * 
 * @since 5.11.1
 */
public interface IAbilityListFilter
{
	/**
	 * This method is called by the <tt>AbilityModel</tt> before an Ability is
	 * added to the model for the registered filter.
	 * 
	 * <p>The filter should return <tt>true</tt> to allow the specified Ability
	 * to be added to the model or <tt>false</tt> if not.
	 * 
	 * @param aMode The view mode the <tt>AbilityModel</tt> is building.
	 * @param anAbility The ability to be checked.
	 * 
	 * @return <tt>true</tt> to allow the Ability to be added.
	 */
	boolean accept(final AbilitySelectionPanel.ViewMode aMode,
		final Ability anAbility);
}
