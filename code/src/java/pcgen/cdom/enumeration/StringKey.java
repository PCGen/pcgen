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
 */
package pcgen.cdom.enumeration;

/**
 * This is a Typesafe enumeration of legal String Characteristics of an object.
 * It is designed to act as an index to a specific String (set by various tokens
 * in LST terms). This is legacy support for tokens that PCGen does not process
 * beyond simple input/output (e.g. holy symbol of a deity). If possible, it is
 * preferred to use a FACT: token rather than a StringKey based token.
 * 
 * These are distinct from PCStringKey items, in order to separate two different
 * types of function. StringKey should be used when items are being attached to
 * a CDOMObject (although a FACT is generally preferred at this point),
 * PCStringKey should be used when a piece of information is attached to a
 * PlayerCharacter (typically by the UI).
 */
public enum StringKey
{
	NAME, KEY_NAME, MASTER_HP_FORMULA, MASTER_CHECK_FORMULA, MASTER_BAB_FORMULA, CHOICE_STRING, DESTINATION,
	PAGE_USAGE, MINDEVVER, MINVER, BIO, DESCRIPTION, GENRE, HELP, OUTPUT_NAME, PUB_NAME_LONG, COST, PUB_NAME_SHORT,
	PUB_NAME_WEB, SETTING, TEMP_DESCRIPTION, FILE_NAME, ABB_KR, TARGET_AREA, NAME_TEXT, DAMAGE, DAMAGE_OVERRIDE,
	FUMBLE_RANGE, LISTTYPE, ITEMCREATE, LEVEL_TYPE, DATA_PRODUCER, DATA_FORMAT, CAMPAIGN_SETTING, SOURCE_WEB,
	SOURCE_SHORT, SOURCE_LONG, SOURCE_PAGE, SOURCE_LINK, CONVERT_NAME, QUALIFIED_KEY, SORT_KEY, ICON, ICON_URI,
	DISPLAY_NAME, TEMPVALUE, SIZEFORMULA, SUBREGION
}
