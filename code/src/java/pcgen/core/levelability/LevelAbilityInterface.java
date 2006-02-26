/*
 * LevelAbilityInterface.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on October 06, 2005, 12:16 AM
 *
 * Current Ver: $Revision: 1.4 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/16 15:10:15 $
 *
 */
package pcgen.core.levelability;

import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;

import java.util.List;

interface LevelAbilityInterface
{
	/**
	 * Process the choice
	 * @param anArrayList
	 * @param selectedList
	 * @param aPC
	 * @param pcLevelInfo
	 * @return true if process is OK
	 */
	public boolean processChoice(
			final List            anArrayList,
			final List            selectedList,
			final PlayerCharacter aPC,
			final PCLevelInfo     pcLevelInfo);
}

