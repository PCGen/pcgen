/*
 * HasCost.java
 * Copyright 2001 (C) Jonas Karlson <jujutsunerd@sf.net>
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
 * Created on September 28, 2002, 11:50 PM
 *
 * $Id$
 */

package pcgen.core;

/**
 * This interface will probably refactored away.
 * @author Jonas Karlson <jujutsunerd@sf.net>
 * @version $Revision$
 */
public interface HasCost
{
	/** 
	 * Set the cost
	 * @param argCost
	 */
	void setCost(String argCost);

	/**
	 * Get the cost
	 * @return the cost
	 */
	double getCost();
}
