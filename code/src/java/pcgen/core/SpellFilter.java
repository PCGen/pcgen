/*
 * SpellProhibitor.java
 * Copyright 2005 (c) Tom Parker <thpr@sourceforge.net>
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
 * Created on November 3, 2006
 *
 * Current Ver: $Revision: 1522 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2006-10-24 18:40:09 -0400 (Tue, 24 Oct 2006) $
 *
 */
package pcgen.core;

public class SpellFilter {
	
	private String spellName;
	
	private String spellType;
	
	private int spellLevel = -1;

	public void setSpellLevel(int spellLevel) {
		this.spellLevel = spellLevel;
	}

	public void setSpellName(String spellName) {
		this.spellName = spellName;
	}

	public void setSpellType(String spellType) {
		this.spellType = spellType;
	}
	
	public boolean isEmpty() {
		return spellLevel < 0 && spellType == null && spellName == null;
	}
	
	public boolean matchesFilter(String spellKey, int testSpellLevel) {
		if (spellLevel >= 0 && testSpellLevel != spellLevel) {
			return false;
		}
		if (spellType != null && !Globals.getSpellKeyed(spellKey).isType(spellType)) {
			return false;
		}
		if (spellName != null && !spellName.equals(spellKey)) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (spellLevel >= 0) {
			sb.append("LEVEL=").append(spellLevel);
		}
		if (spellType != null) {
			if (sb.length() != 0) {
				sb.append(",");
			}
			sb.append("TYPE=").append(spellType);
		}
		if (spellName != null) {
			if (sb.length() != 0) {
				sb.append(",");
			}
			sb.append(spellName);
		}
		return sb.toString();
	}

}
