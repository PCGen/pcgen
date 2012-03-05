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
	public void addCompanion(CharacterFacade companion);
	public void removeCompanion(CompanionFacade companion);
	
	public ListFacade<CompanionStubFacade> getAvailableCompanions();
	public int getRemainingCompanions(String type);
	public int getMaxCompanions(String type);
	public ListFacade<CompanionFacade> getCompanions();
}
