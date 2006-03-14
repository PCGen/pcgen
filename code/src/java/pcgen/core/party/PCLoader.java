/*
 * Copyright (c) 2005 Tom Parker thpr@sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on Jun 10, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.party;

import pcgen.core.PlayerCharacter;

import java.io.File;

/**
 * Interface to identify classes which can load a PC from a file.
 *
 * @author Tom Parker <thpr@sourceforge.net>
 */
public interface PCLoader
{
	/**
	 * Loads a player character from the given <var>file</var>.
	 *
	 * @param file the file storing the PC
	 *
	 * @return the PC
	 */
	public PlayerCharacter loadPCFromFile(File file);
}