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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.utils;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a Typesafe enumeration of legal List Characteristics of an object.
 */
public final class ListKey {
    
	/** AUTO_ARRAY - a ListKey */
	public static final ListKey AUTO_ARRAY = new ListKey();
	/** AUTO_LANGUAGES - a ListKey */
	public static final ListKey AUTO_LANGUAGES = new ListKey();
	/** CLASS_SKILLS - a ListKey */
	public static final ListKey CLASS_SKILLS = new ListKey();
	/** CROSS_CLASS_SKILLS - a ListKey */
	public static final ListKey CROSS_CLASS_SKILLS = new ListKey();
	/** DOMAIN - a ListKey */
	public static final ListKey DOMAIN = new ListKey();
	/** FILE_BIO_SET - a ListKey */
	public static final ListKey FILE_BIO_SET = new ListKey();
	/** FILE_CLASS - a ListKey */
	public static final ListKey FILE_CLASS = new ListKey();
	/** FILE_CLASS_SKILL - a ListKey */
	public static final ListKey FILE_CLASS_SKILL = new ListKey();
	/** FILE_CLASS_SPELL - a ListKey */
	public static final ListKey FILE_CLASS_SPELL = new ListKey();
	/** FILE_COIN - a ListKey */
	public static final ListKey FILE_COIN = new ListKey();
	/** FILE_COMPANION_MOD - a ListKey */
	public static final ListKey FILE_COMPANION_MOD = new ListKey();
	/** FILE_COVER - a ListKey */
	public static final ListKey FILE_COVER = new ListKey();
	/** FILE_DEITY - a ListKey */
	public static final ListKey FILE_DEITY = new ListKey();
	/** FILE_DOMAIN - a ListKey */
	public static final ListKey FILE_DOMAIN = new ListKey();
	/** FILE_EQUIP - a ListKey */
	public static final ListKey FILE_EQUIP = new ListKey();
	/** FILE_EQUIP_MOD - a ListKey */
	public static final ListKey FILE_EQUIP_MOD = new ListKey();
	/** FILE_ABILITY - a ListKey */
	public static final ListKey FILE_ABILITY = new ListKey();
	/** FILE_FEAT - a ListKey */
	public static final ListKey FILE_FEAT = new ListKey();
	/** FILE_KIT - a ListKey */
	public static final ListKey FILE_KIT = new ListKey();
	/** FILE_LANGUAGE - a ListKey */
	public static final ListKey FILE_LANGUAGE = new ListKey();
	/** FILE_LST_EXCLUDE - a ListKey */
	public static final ListKey FILE_LST_EXCLUDE = new ListKey();
	/** FILE_PCC - a ListKey */
	public static final ListKey FILE_PCC = new ListKey();
	/** FILE_RACE - a ListKey */
	public static final ListKey FILE_RACE = new ListKey();
	/** FILE_REQ_SKILL - a ListKey */
	public static final ListKey FILE_REQ_SKILL = new ListKey();
	/** FILE_SKILL - a ListKey */
	public static final ListKey FILE_SKILL = new ListKey();
	/** FILE_SPELL - a ListKey */
	public static final ListKey FILE_SPELL = new ListKey();
	/** FILE_TEMPLATE - a ListKey */
	public static final ListKey FILE_TEMPLATE = new ListKey();
	/** FILE_WEAPON_PROF - a ListKey */
	public static final ListKey FILE_WEAPON_PROF = new ListKey();
	/** GAME_MODE - a ListKey */
	public static final ListKey GAME_MODE = new ListKey();
	/** KITS - a ListKey */
	public static final ListKey KITS = new ListKey();
	/** LICENSE - a ListKey */
	public static final ListKey LICENSE = new ListKey();
	/** LICENSE_FILE - a ListKey */
	public static final ListKey LICENSE_FILE = new ListKey();
	/** LINE - a ListKey */
	public static final ListKey LINE = new ListKey();
	/** NATURAL_WEAPONS - a ListKey */
	public static final ListKey NATURAL_WEAPONS = new ListKey();
	/** PANTHEON - a ListKey */
	public static final ListKey PANTHEON = new ListKey();
	/** RACE_PANTHEON - a ListKey */
	public static final ListKey RACEPANTHEON = new ListKey();
	/** REMOVE_STRING_LIST - a ListKey */
	public static final ListKey REMOVE_STRING_LIST = new ListKey();
	/** SAVE - a ListKey */
	public static final ListKey SAVE = new ListKey();
	/** SECTION 15 - a ListKey */
	public static final ListKey SECTION_15 = new ListKey();
	/** SELECTED_ARMOR_PROFS - a ListKey */
	public static final ListKey SELECTED_ARMOR_PROF = new ListKey();
	/** SELECTED_SHIELD_PROFS - a ListKey */
	public static final ListKey SELECTED_SHIELD_PROFS = new ListKey();
	/** SELECTED_WEAPON_PROF_BONUS - a ListKey */
	public static final ListKey SELECTED_WEAPON_PROF_BONUS = new ListKey();
	/** SPECIAL_ABILITY - a ListKey */
	public static final ListKey SPECIAL_ABILITY = new ListKey();
	/** TEMPLATES - a ListKey */
	public static final ListKey TEMPLATES = new ListKey();
	/** TEMPLATES_ADDED - a ListKey */
	public static final ListKey TEMPLATES_ADDED = new ListKey();
	/** TEMP_BONUS - a ListKey */
	public static final ListKey TEMP_BONUS = new ListKey();
	/** TYPE - a ListKey */
	public static final ListKey TYPE = new ListKey();
	/** UDAM - a ListKey */
	public static final ListKey UDAM = new ListKey();
	/** UMULT - a ListKey */
	public static final ListKey UMULT = new ListKey();
	/** Key for a list of virtual feats (feats granted regardless of the prereqs) */
	public static final ListKey VIRTUAL_FEATS = new ListKey();
	/** Key for a list of weapon proficiencies */
	public static final ListKey WEAPON_PROF = new ListKey();
    
    /** Private constructor to prevent instantiation of this class */
    private ListKey() {
        //Only allow instantation here
    }
}
