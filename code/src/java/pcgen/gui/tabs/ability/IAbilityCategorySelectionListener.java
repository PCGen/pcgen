/*
 * IAbilityCategorySelectionListener.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 3/12/2007
 *
 * $Id$
 */
package pcgen.gui.tabs.ability;

import pcgen.core.AbilityCategory;


/**
 * <code>IAbilityCategorySelectionListener</code> used to advise 
 * interested parties about Ability Category Selection events.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 * 
 * @since 5.13.6
 */
public interface IAbilityCategorySelectionListener
{
	/**
	 * This method is called when the user highlights an Ability Category 
	 * node in the list. 
	 * 
	 * @param anAbilityCat The highlighted ability category
	 */
	void abilityCategorySelected(final AbilityCategory anAbilityCat);
}
