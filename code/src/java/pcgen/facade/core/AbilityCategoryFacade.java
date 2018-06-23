/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.facade.core;

/**
 * AbilityCategoryFacade defines the interface to be used b the UI when 
 * interacting with ability categories.
 * 
 * 
 */
public interface AbilityCategoryFacade
{

	/**
	 * 
	 * @return "Feats", "Class Abilities", or "Salient Divine Ability"
	 */
	public String getType();

	/**
	 * 
	 * @return the singular name of the Category
	 */
	public String getName();

	/**
	 * 
	 * @return the plural name of the Category
	 */
	@Override
	public String toString();

	/**
	 * @return Can the selections in this pool be edited.
	 */
	boolean isEditable();

	/**
	 * @return Can the pool for this category be edited.
	 */
	boolean allowPoolMod();

}
