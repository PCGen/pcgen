/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Current Ver: $Revision: 1.5 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 00:08:08 $
 */
package pcgen.core.utils;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a Typesafe enumeration of legal List Characteristics of an object.
 */
public final class ListKey {
    
	public static final ListKey AUTO_ARRAY = new ListKey();
	public static final ListKey AUTO_LANGUAGES = new ListKey();
	public static final ListKey DOMAIN = new ListKey();
	public static final ListKey FILE_BIO_SET = new ListKey();
	public static final ListKey FILE_CLASS = new ListKey();
	public static final ListKey FILE_CLASS_SKILL = new ListKey();
	public static final ListKey FILE_CLASS_SPELL = new ListKey();
	public static final ListKey FILE_COIN = new ListKey();
	public static final ListKey FILE_COMPANION_MOD = new ListKey();
	public static final ListKey FILE_DEITY = new ListKey();
	public static final ListKey FILE_DOMAIN = new ListKey();
	public static final ListKey FILE_EQUIP = new ListKey();
	public static final ListKey FILE_EQUIP_MOD = new ListKey();
	public static final ListKey FILE_ABILITY = new ListKey();
	public static final ListKey FILE_FEAT = new ListKey();
	public static final ListKey FILE_KIT = new ListKey();
	public static final ListKey FILE_LANGUAGE = new ListKey();
	public static final ListKey FILE_LST_EXCLUDE = new ListKey();
	public static final ListKey FILE_PCC = new ListKey();
	public static final ListKey FILE_RACE = new ListKey();
	public static final ListKey FILE_REQ_SKILL = new ListKey();
	public static final ListKey FILE_SKILL = new ListKey();
	public static final ListKey FILE_SPELL = new ListKey();
	public static final ListKey FILE_TEMPLATE = new ListKey();
	public static final ListKey FILE_WEAPON_PROF = new ListKey();
	public static final ListKey GAME_MODE = new ListKey();
	public static final ListKey KITS = new ListKey();
	public static final ListKey LICENSE = new ListKey();
	public static final ListKey LICENSE_FILE = new ListKey();
	public static final ListKey LINE = new ListKey();
	public static final ListKey NATURAL_WEAPONS = new ListKey();
	public static final ListKey PANTHEON = new ListKey();
	public static final ListKey RACEPANTHEON = new ListKey();
	public static final ListKey REMOVE_STRING_LIST = new ListKey();
	public static final ListKey SAVE = new ListKey();
	public static final ListKey SECTION_15 = new ListKey();
	public static final ListKey SELECTED_ARMOR_PROF = new ListKey();
	public static final ListKey SELECTED_SHIELD_PROFS = new ListKey();
	public static final ListKey SELECTED_WEAPON_PROF_BONUS = new ListKey();
	public static final ListKey SPECIAL_ABILITY = new ListKey();
	public static final ListKey TEMPLATES = new ListKey();
	public static final ListKey TEMPLATES_ADDED = new ListKey();
	public static final ListKey TEMP_BONUS = new ListKey();
	public static final ListKey TYPE = new ListKey();
	public static final ListKey UDAM = new ListKey();
	public static final ListKey UMULT = new ListKey();
	/** Key for a list of virtual feats (feats granted regardless of the prereqs) */
	public static final ListKey VIRTUAL_FEATS = new ListKey();
	/** Key for a list of weapon proficiencies */
	public static final ListKey WEAPON_PROF = new ListKey();
    
    /**
     * Private constructor to prevent instantiation of this class. 
     */
    private ListKey() {
        //Only allow instantation here
    }
}
