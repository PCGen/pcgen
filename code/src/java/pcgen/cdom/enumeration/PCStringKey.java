/*
 * Copyright 2005-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.enumeration;

/**
 * This is a Typesafe enumeration of legal String Characteristics of a PC.
 * 
 * These are distinct from StringKey items, in order to separate two different
 * types of function. These are related to the PlayerCharacter, and thus are
 * usually capturing items from the UI and then enabling them in FreeMarker
 * output. StringKey should be used when items are being attached to a
 * CDOMObject (although a FACT is generally preferred at this point).
 */
public enum PCStringKey
{
	/*
	 * *WARNING* Renaming these items will impact FreeMarker output, rename with care!
	 */
	ASSETS, BIO, BIRTHPLACE, BIRTHDAY, CATCHPHRASE, CITY, COMPANIONS, DESCRIPTION, EYECOLOR, GMNOTES,
	HAIRCOLOR, HANDED, INTERESTS, LOCATION, MAGIC, NAME, PERSONALITY1, PERSONALITY2,
	PHOBIAS, PLAYERSNAME, RESIDENCE, SPEECHTENDENCY, TABNAME,
	/*
	 * These are undocumented as far as output - no need to worry about names
	 */
	FILE_NAME, PORTRAIT_PATH, SPELLBOOK_AUTO_ADD_KNOWN, CURRENT_EQUIP_SET_NAME;

	public static PCStringKey getStringKey(String s)
	{
		return valueOf(s.toUpperCase());
	}
}
