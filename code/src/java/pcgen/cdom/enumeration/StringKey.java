/*
 * Copyright 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.cdom.enumeration;

/**
 * @author Tom Parker <thpr@users.sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal String Characteristics of an object.
 * It is designed to act as an index to a specific String (set by various tokens
 * in LST terms). Generally this should be used for tokens that PCGen does not
 * process beyond simple input/output (e.g. holy symbol of a deity).
 */
public enum StringKey
{
	NAME, KEY_NAME, WORSHIPPERS, TITLE, HOLY_ITEM, MASTER_HP_FORMULA, MASTER_CHECK_FORMULA, 
	MASTER_BAB_FORMULA, BIRTHPLACE, BIRTHDAY, BOOK_TYPE, CHOICE_STRING, DESTINATION, 
	PAGE_USAGE, MINDEVVER, MINVER, BIO, DESCRIPTION, GENRE, HELP, APPEARANCE, CATCH_PHRASE, 
	OUTPUT_NAME, PUB_NAME_LONG, COST, PUB_NAME_SHORT, 
	CURRENT_EQUIP_SET_NAME, PUB_NAME_WEB, SETTING, TEMP_DESCRIPTION, TRAIT2, 
	TRAIT1, SKIN_COLOR, HAIR_COLOR, SPEECH_TENDENCY, PHOBIAS, INTERESTS, RESIDENCE, 
	SPELLBOOK_AUTO_ADD_KNOWN, PLAYERS_NAME, HANDED, HAIR_STYLE, PORTRAIT_PATH, EYE_COLOR, LOCATION, 
	FILE_NAME, ABB, TARGET_AREA, NAME_TEXT, DAMAGE, DAMAGE_OVERRIDE, RATE_OF_FIRE, FUMBLE_RANGE, 
	LISTTYPE, SPELLTYPE, ITEMCREATE, LEVEL_TYPE, DATA_PRODUCER, DATA_FORMAT, CAMPAIGN_SETTING,
	SOURCE_WEB, SOURCE_SHORT, SOURCE_LONG, SOURCE_PAGE, CONVERT_NAME, QUALIFIED_KEY, SORT_KEY,
	MISC_COMPANIONS, MISC_ASSETS, MISC_MAGIC, MISC_DM, ICON, ICON_URI, TAB_NAME
}
