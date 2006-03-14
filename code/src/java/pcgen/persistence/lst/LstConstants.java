/*
 * LstConstants.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on March 2, 2002, 3:23 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.persistence.lst;

/**
 * A list of constants that define types of Objects to be 
 * loaded by the LST System Loader
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public interface LstConstants
{
	/** BIO2_TYPE = 32 */
	int BIO2_TYPE = 32;
	/** BIO_TYPE = 31 */
	int BIO_TYPE = 31;
	/** BONUS_TYPE = 18 */
	int BONUS_TYPE = 18;
	/** CAMPAIGN_TYPE = 13 */
	int CAMPAIGN_TYPE = 13;
	/** CLASSSKILL_TYPE = 14 */
	int CLASSSKILL_TYPE = 14;
	/** CLASSSPELL_TYPE = 15 */
	int CLASSSPELL_TYPE = 15;
	/** CLASS_TYPE = 1 */
	int CLASS_TYPE = 1;
	/** COINS_TYPE = 23 */
	int COINS_TYPE = 23;
	/** COMPANIONMOD_TYPE = 25 */
	int COMPANIONMOD_TYPE = 25;
	/** DEITY_TYPE = 5 */
	int DEITY_TYPE = 5;
	/** DOMAIN_TYPE = 4 */
	int DOMAIN_TYPE = 4;
	/** EQMODIFIER_TYPE = 19 */
	int EQMODIFIER_TYPE = 19;
	/** EQUIPMENT_TYPE = 9 */
	int EQUIPMENT_TYPE = 9;
	/** EQUIPSLOT_TYPE = 22 */
	int EQUIPSLOT_TYPE = 22;
	/** FEAT_TYPE = 3 */
	int FEAT_TYPE = 3;
	/** HELPCONTEXT_TYPE = 26 */
	int HELPCONTEXT_TYPE = 26;
	/** KIT_TYPE = 30 */
	int KIT_TYPE = 30;
	/** LANGUAGE_TYPE = 10 */
	int LANGUAGE_TYPE = 10;
	/** LEVEL_TYPE = 29 */
	int LEVEL_TYPE = 29;
	/** LOAD_TYPE = 11 */
	int LOAD_TYPE = 11;
	/** LOCATIONS_TYPE = 34 */
	int LOCATIONS_TYPE = 34;
	/** MISCGAMEINFO_TYPE = 28 */
	int MISCGAMEINFO_TYPE = 28;
	/** PAPERINFO_TYPE = 24 */
	int PAPERINFO_TYPE = 24;
	/** POINTBUY_TYPE = 27 */
	int POINTBUY_TYPE = 27;

	// The following are used by loadOrder in Globals.
	
	/** RACE_TYPE = 0 */
	int RACE_TYPE = 0;
	/** REQSKILL_TYPE = 16 */
	int REQSKILL_TYPE = 16;
	/** SCHOOLS_TYPE = 8 */
	int SCHOOLS_TYPE = 8;
	/** SIZEADJUSTMENT_TYPE = 20 */
	int SIZEADJUSTMENT_TYPE = 20;
	/** SKILL_TYPE = 2 */
	int SKILL_TYPE = 2;
	/** SPECIAL_TYPE = 12 */
	int SPECIAL_TYPE = 12;
	/** SPELL_TYPE = 6 */
	int SPELL_TYPE = 6;
	/** STATNAME_TYPE = 21 */
	int STATNAME_TYPE = 21;
	/** TEMPLATE_TYPE = 17 */
	int TEMPLATE_TYPE = 17;
	/** TRAITS_TYPE = 33 */
	int TRAITS_TYPE = 33;
	/** WEAPONPROF_TYPE = 7 */
	int WEAPONPROF_TYPE = 7;

	//int XP_TYPE = 22;
	//int COLOR_TYPE = 9;
	//int TRAIT_TYPE = 10;
	//int PHOBIA_TYPE = 28;
	//int INTERESTS_TYPE = 30;
	//int PHRASE_TYPE = 31;
	//int HAIRSTYLE_TYPE = 32;
	//int SPEECH_TYPE = 33;
}
