/*
 * PCLevelCastingInfo.java
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created: November 8, 2006
 *
 * $Id: PCClass.java 1605 2006-11-08 02:14:21Z thpr $
 */
package pcgen.core;

import pcgen.cdom.base.Constants;

/**
 * SpellProgressionInfo contains information about Spell Progression in support
 * of a PCClass.
 * 
 * @author Tom Parker <thpr@users.sourceforge.net>
 */
public class SpellProgressionInfo implements Cloneable {

	/*
	 * FUTURETYPESAFETY This should NOT be a String, as Spell Types are a
	 * specific set of items... This, however is NON Trivial, since Spell Types
	 * are used WIDELY through the code base. It will be a nice thing to make
	 * type safe, but it is best done by itself in a checkin specifically
	 * focused on making Spell Types type safe.
	 * 
	 * In the future it would be nice to have this in SpellProgressionInfo, but
	 * that is not possible today because this defaults to 'None' and SOOO much
	 * of the code actually depends on this being non-null. This should be one
	 * of the implicit tasks (moving this) which is part of the project to make
	 * Spell Types Type Safe.
	 */
	private String spellType = null;

	/**
	 * Sets the Spell Type for this Spell Progression. The type cannot be null
	 * or an empty String. To "unset" the Spell Type, the Spell Type should be
	 * set to Constants.s_NONE
	 * 
	 * @param type
	 *            The type of Spell in this Spell Progression
	 */
	public void setSpellType(String type) {
		if (type == null) {
			throw new IllegalArgumentException("Spell type cannot be null");
		}
		if (type.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Spell type cannot be empty string");
		}
		spellType = type.trim();
	}

	/**
	 * Returns the type of spell in this Spell Progression. Will not return
	 * null. Constants.s_NONE is used to indicate that no Spell Type exists in
	 * this Spell Progression.
	 * 
	 * @return The type of spell in this Spell Progression
	 */
	public String getSpellType() {
		if (spellType == null) {
			return Constants.s_NONE;
		}
		return spellType;
	}

	/**
	 * Clones this SpellProgressionInfo object. A semi-deep (or semi-shallow,
	 * depending on one's point of view) clone is performed, under the
	 * assumption that the cloned object should be allowed to have any of the
	 * SpellProgressionInfo.set* method called without allowing either the
	 * original or the cloned SpellProgressionInfo object to accidentally modify
	 * the other.
	 * 
	 * There is the assumption, however, that the Lists contained within the
	 * SpellProgressionInfo object are never modified, and violation of that
	 * semantic rule either within SpellProgressionInfo or by other objects
	 * which call the reference-semantic methods of SpellProgressionInfo can
	 * render this clone insufficient.
	 * 
	 * @return A semi-shallow Clone of this SpellProgressionInfo object.
	 * @throws CloneNotSupportedException
	 */
	@Override
	public SpellProgressionInfo clone() throws CloneNotSupportedException {
		return (SpellProgressionInfo) super.clone();
	}
}
