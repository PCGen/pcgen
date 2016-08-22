/*
 * EquipModFacade.java
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 2013-09-14
 *
 * $Id$
 */
package pcgen.facade.core;


/**
 * EquipModFacade defines the methods used by the user interface to 
 * interact with Equipment Modifier objects.  
 * 
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 * @version $Revision$
 */
public interface EquipModFacade extends InfoFacade
{

	public String getDisplayType();

}
