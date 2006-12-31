/*
 * Campaign.java
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import pcgen.core.utils.*;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.util.*;

/**
 * <code>Campaign</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Campaign extends PObject
{
	private Map<String, String> publisherMap = new HashMap<String, String>();
	private Properties options = new Properties();
	private boolean isD20;
	private boolean isLicensed;
	private boolean isLoaded;
	private boolean isOGL;
	private boolean showInMenu;

	/**
	 * Constructor
	 */
	public Campaign() {
		super();
		listChar.initializeListFor(ListKey.GAME_MODE);
		listChar.initializeListFor(ListKey.LICENSE);
		listChar.initializeListFor(ListKey.LINE);
		listChar.initializeListFor(ListKey.SECTION_15);
		listChar.initializeListFor(ListKey.FILE_BIO_SET);
		listChar.initializeListFor(ListKey.FILE_CLASS);
		listChar.initializeListFor(ListKey.FILE_CLASS_SKILL);
		listChar.initializeListFor(ListKey.FILE_CLASS_SPELL);
		listChar.initializeListFor(ListKey.FILE_COMPANION_MOD);
		listChar.initializeListFor(ListKey.FILE_COVER);
		listChar.initializeListFor(ListKey.FILE_DEITY);
		listChar.initializeListFor(ListKey.FILE_DOMAIN);
		listChar.initializeListFor(ListKey.FILE_EQUIP);
		listChar.initializeListFor(ListKey.FILE_EQUIP_MOD);
		listChar.initializeListFor(ListKey.FILE_ABILITY);
		listChar.initializeListFor(ListKey.FILE_FEAT);
		listChar.initializeListFor(ListKey.FILE_KIT);
		listChar.initializeListFor(ListKey.FILE_LANGUAGE);
		listChar.initializeListFor(ListKey.FILE_LST_EXCLUDE);
		listChar.initializeListFor(ListKey.FILE_PCC);
		listChar.initializeListFor(ListKey.FILE_RACE);
		listChar.initializeListFor(ListKey.FILE_REQ_SKILL);
		listChar.initializeListFor(ListKey.FILE_SKILL);
		listChar.initializeListFor(ListKey.FILE_SPELL);
		listChar.initializeListFor(ListKey.FILE_TEMPLATE);
		listChar.initializeListFor(ListKey.FILE_WEAPON_PROF);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllAbilityFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_ABILITY, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllBioSetFiles(final List<String> files)
	{
		listChar.addAllToListFor(ListKey.FILE_BIO_SET, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllClassFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_CLASS, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllClassSkillFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_CLASS_SKILL, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllClassSpellFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_CLASS_SPELL, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllCompanionModFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_COMPANION_MOD, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllCoverFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_COVER, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllDeityFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_DEITY, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllDomainFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_DOMAIN, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllEquipFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_EQUIP, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllEquipModFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_EQUIP_MOD, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllFeatFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_FEAT, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllKitFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_KIT, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllLanguageFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_LANGUAGE, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllLstExcludeFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_LST_EXCLUDE, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllPccFiles(final List<String> files)
	{
		listChar.addAllToListFor(ListKey.FILE_PCC, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllRaceFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_RACE, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllReqSkillFiles(final List<String> files)
	{
		listChar.addAllToListFor(ListKey.FILE_REQ_SKILL, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllSkillFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_SKILL, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllSpellFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_SPELL, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllTemplateFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_TEMPLATE, files);
	}

	/**
	 *
	 * @param files
	 */
	public void addAllWeaponProfFiles(final List<CampaignSourceEntry> files)
	{
		listChar.addAllToListFor(ListKey.FILE_WEAPON_PROF, files);
	}

	/**
	 *
	 * @param file
	 */
	public void addAbilityFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_ABILITY, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addBioSetFile(final String file)
	{
		listChar.addToListFor(ListKey.FILE_BIO_SET, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addClassFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_CLASS, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addClassSkillFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_CLASS_SKILL, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addClassSpellFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_CLASS_SPELL, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addCompanionModFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_COMPANION_MOD, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addCoverFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_COVER, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addDeityFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_DEITY, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addDomainFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_DOMAIN, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addEquipFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_EQUIP, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addEquipModFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_EQUIP_MOD, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addFeatFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_FEAT, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addKitFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_KIT, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addLanguageFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_LANGUAGE, file);
	}

	/**
	 * This method is used to addText a brief segment of license text to the
	 * OGL license information for the campaign.
	 * @param license String piece of information to addText to the OGL license.
	 */
	public void addLicense(final String license)
	{
		if (license.equals(".CLEAR"))
		{
			listChar.removeListFor(ListKey.LICENSE);
			listChar.initializeListFor(ListKey.LICENSE);
		}
		else
		{
			listChar.addToListFor(ListKey.LICENSE, license);
		}
	}

	/**
	 * This method is used to addText an external file to the
	 * license information required for the source.  The added
	 * file may be either relative to the default directory or
	 * URL syntax; at present, however, the only URL syntax that
	 * is honored by the GUI are those refering to the file system
	 * i.e. file:/etc/etc/etc URLs.
	 * @param licenseFile String location of a license file
	 */
	public void addLicenseFile(final String licenseFile)
	{
		listChar.addToListFor(ListKey.LICENSE_FILE, licenseFile);
	}

	/**
	 * Add a line
	 * @param line
	 */
	public void addLine(final String line)
	{
		if (line.equals(".CLEAR"))
		{
			listChar.removeListFor(ListKey.LINE);
			listChar.initializeListFor(ListKey.LINE);
		}
		else
		{
			listChar.addToListFor(ListKey.LINE, line);
		}
	}

	/**
	 *
	 * @param file
	 */
	public void addLstExcludeFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_LST_EXCLUDE, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addPccFile(final String file)
	{
		listChar.addToListFor(ListKey.FILE_PCC, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addRaceFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_RACE, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addReqSkillFile(final String file)
	{
		listChar.addToListFor(ListKey.FILE_REQ_SKILL, file);
	}

	/**
	 * Add section 15 info
	 * @param section15
	 */
	public void addSection15(final String section15)
	{
		if (section15.equals(".CLEAR"))
		{
			listChar.removeListFor(ListKey.SECTION_15);
			listChar.initializeListFor(ListKey.SECTION_15);
		}
		else
		{
			listChar.addToListFor(ListKey.SECTION_15, section15);
		}
	}

	/**
	 *
	 * @param file
	 */
	public void addSkillFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_SKILL, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addSpellFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_SPELL, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addTemplateFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_TEMPLATE, file);
	}

	/**
	 *
	 * @param file
	 */
	public void addWeaponProfFile(final CampaignSourceEntry file)
	{
		listChar.addToListFor(ListKey.FILE_WEAPON_PROF, file);
	}

	/**
	 * Returns the abilityFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getAbilityFiles()
	{
		return getListFor(ListKey.FILE_ABILITY);
	}

	/**
	 * Returns the bioSetFileList.
	 * @return List
	 */
	public List<String> getBioSetFiles()
	{
		return getListFor(ListKey.FILE_BIO_SET);
	}

	/**
	 * Get the book type
	 * @return bookType
	 */
	public String getBookType()
	{
		String characteristic = stringChar.get(StringKey.BOOK_TYPE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Returns the classFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getClassFiles()
	{
		return getListFor(ListKey.FILE_CLASS);
	}

	/**
	 * Returns the classSkillFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getClassSkillFiles()
	{
		return getListFor(ListKey.FILE_CLASS_SKILL);
	}

	/**
	 * Returns the classSpellFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getClassSpellFiles()
	{
		return getListFor(ListKey.FILE_CLASS_SPELL);
	}
	/**
	 * Returns the companionmodFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getCompanionModFiles()
	{
		return getListFor(ListKey.FILE_COMPANION_MOD);
	}

	/**
	 * Returns the coverFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getCoverFiles()
	{
		return getListFor(ListKey.FILE_COVER);
	}

	/**
	 * Returns the deityFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getDeityFiles()
	{
		return getListFor(ListKey.FILE_DEITY);
	}

	/**
	 * Get the destination
	 * @return destination
	 */
	public String getDestination()
	{
		String characteristic = stringChar.get(StringKey.DESTINATION);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Returns the domainFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getDomainFiles()
	{
		return getListFor(ListKey.FILE_DOMAIN);
	}

	/**
	 * Returns the equipmentFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getEquipFiles()
	{
		return getListFor(ListKey.FILE_EQUIP);
	}

	/**
	 * Returns the equipmodFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getEquipModFiles()
	{
		return getListFor(ListKey.FILE_EQUIP_MOD);
	}

	/**
	 * Returns the featFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getFeatFiles()
	{
		return getListFor(ListKey.FILE_FEAT);
	}

	/**
	 * Returns the name of the game this campaign is intended for.
	 * @return the name of the game
	 */
	public List<String> getGameModes()
	{
		return getListFor(ListKey.GAME_MODE);
	}

	/**
	 * Returns the game modes in a Human readable format
	 *
	 * @return game mode as a String
	 **/
	public String getGameModeString()
	{
		final StringBuffer sb = new StringBuffer();
		List<String> gameModeList = getListFor(ListKey.GAME_MODE);

		for (Iterator<String> i = gameModeList.iterator(); i.hasNext();)
		{
			final String gameMode = i.next();
			sb.append(gameMode);

			if (i.hasNext())
			{
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	/**
	 * Get the genre
	 * @return genre
	 */
	public String getGenre()
	{
		String characteristic = stringChar.get(StringKey.GENRE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the help
	 * @return help
	 */
	public String getHelp()
	{
		String characteristic = stringChar.get(StringKey.HELP);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the info on this campaign
	 */
	public String getInfoText()
	{
		String characteristic = stringChar.get(StringKey.INFO_TEXT);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Returns the kitFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getKitFiles()
	{
		return getListFor(ListKey.FILE_KIT);
	}

	/**
	 * Returns the languageFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getLanguageFiles()
	{
		return getListFor(ListKey.FILE_LANGUAGE);
	}

	/**
	 * Get the licenses list
	 * @return license
	 */
	public List<String> getLicenses()
	{
		return getListFor(ListKey.LICENSE);
	}

	/**
	 * Returns the license info for this campaign's source(book).
	 * @return the license
	 */
	public String getLicenseString()
	{
		StringBuffer sb = new StringBuffer();

		for ( String license : getListFor(ListKey.LICENSE) )
		{
			sb.append(license).append("<br>");
		}

		return sb.toString();
	}

	/**
	 * Get the license files
	 * @return license files
	 */
	public List<String> getLicenseFiles()
	{
		return getListFor(ListKey.LICENSE_FILE);
	}

	/**
	 * Get the lines
	 * @return lines
	 */
	public List<String> getLines()
	{
		return getListFor(ListKey.LINE);
	}

	/**
	 * Returns the lstExcludeFiles.
	 * @return List
	 */
	public List<CampaignSourceEntry> getLstExcludeFiles()
	{
		return getListFor(ListKey.FILE_LST_EXCLUDE);
	}

	/**
	 * Returns the pccFileList.
	 * @return List
	 */
	public List<String> getPccFiles()
	{
		return getListFor(ListKey.FILE_PCC);
	}

	/**
	 * Get the publisher longname
	 * @return publisher long name
	 */
	public String getPubNameLong()
	{
		String characteristic = stringChar.get(StringKey.PUB_NAME_LONG);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * get the publisher short name
	 * @return publisher short name
	 */
	public String getPubNameShort()
	{
		String characteristic = stringChar.get(StringKey.PUB_NAME_SHORT);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the publisher web name
	 * @return publisher web name
	 */
	public String getPubNameWeb()
	{
		String characteristic = stringChar.get(StringKey.PUB_NAME_WEB);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Returns the raceFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getRaceFiles()
	{
		return getListFor(ListKey.FILE_RACE);
	}

	/**
	 * @return the 'load rank' of the campaign.
	 */
	public int getRank()
	{
		Integer characteristic = integerChar.get(IntegerKey.RANK);
		return characteristic == null ? 9 : characteristic.intValue();
	}

	/**
	 * Returns the reqSkillList.
	 * @return List
	 */
	public List<String> getReqSkillFiles()
	{
		return getListFor(ListKey.FILE_REQ_SKILL);
	}

	/**
	 * Get section 15 as a List
	 * @return section 15
	 */
	public List<String> getSection15s()
	{
		return getListFor(ListKey.SECTION_15);
	}

	/**
	 * Returns the section 15 info for this campaign's source(book).
	 * @return the section 15 info
	 */
	public String getSection15String()
	{
		StringBuffer sb = new StringBuffer();

		for ( String license : getListFor(ListKey.SECTION_15) )
		{
			sb.append(license).append("<br>");
		}

		return sb.toString();
	}

	/**
	 * Get the setting
	 * @return setting
	 */
	public String getSetting()
	{
		String characteristic = stringChar.get(StringKey.SETTING);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Returns the skillFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getSkillFiles()
	{
		return getListFor(ListKey.FILE_SKILL);
	}

	/**
	 * Returns the spellFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getSpellFiles()
	{
		return getListFor(ListKey.FILE_SPELL);
	}

	/**
	 * Returns the templateFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getTemplateFiles()
	{
		return getListFor(ListKey.FILE_TEMPLATE);
	}

	/**
	 * Returns the weaponProfFileList.
	 * @return List
	 */
	public List<CampaignSourceEntry> getWeaponProfFiles()
	{
		return getListFor(ListKey.FILE_WEAPON_PROF);
	}

	/**
	 * Queries to see if this campaign is of a gameMode
	 * @param gameMode    name of gameMode to test for
	 * @return        boolean if present
	 **/
	public boolean isGameMode(final String gameMode)
	{
		return listChar.containsInList(ListKey.GAME_MODE, gameMode);
	}

	/**
	 * Queries to see if this campaign is of a gameMode
	 * @param gameModeList    list of gameModes to test for
	 * @return        boolean if present
	 **/
	public boolean isGameMode(final List<String> gameModeList)
	{
		for ( String gameMode : gameModeList )
		{
			if (listChar.containsInList(ListKey.GAME_MODE, gameMode))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Set the book type
	 * @param bookType
	 */
	public void setBookType(final String bookType)
	{
		stringChar.put(StringKey.BOOK_TYPE, bookType);
	}

	/**
	 * Set the destination
	 * @param destination
	 */
	public void setDestination(final String destination)
	{
		stringChar.put(StringKey.DESTINATION, destination);
	}

	/**
	 * Sets the name of the game this campaign is intended for.
	 * @param gameMode name or '|' delimited list of names
	 */
	public void setGameMode(final String gameMode)
	{
		final StringTokenizer aTok = new StringTokenizer(gameMode, "|");
		listChar.removeListFor(ListKey.GAME_MODE);

		while (aTok.hasMoreTokens())
		{
			final String tok = aTok.nextToken();
			if (!(isGameMode(tok)))
			{
				listChar.addToListFor(ListKey.GAME_MODE, tok);
			}
		}
	}

	/**
	 * Set the genre
	 * @param genre
	 */
	public void setGenre(final String genre)
	{
		stringChar.put(StringKey.GENRE, genre);
	}

	/**
	 * Set the help
	 * @param help
	 */
	public void setHelp(final String help)
	{
		stringChar.put(StringKey.HELP, help);
	}

	/**
	 * Set the infotext
	 * @param infoText
	 */
	public void setInfoText(final String infoText)
	{
		stringChar.put(StringKey.INFO_TEXT, infoText);
	}

	/**
	 * Set the publisher long name
	 * @param pubNameLong
	 */
	public void setPubNameLong(final String pubNameLong)
	{
		addPublisher("LONG:" + pubNameLong);
		stringChar.put(StringKey.PUB_NAME_LONG, pubNameLong);
	}

	/**
	 * Set the publisher short name
	 * @param pubNameShort
	 */
	public void setPubNameShort(final String pubNameShort)
	{
		addPublisher("SHORT:" + pubNameShort);
		stringChar.put(StringKey.PUB_NAME_SHORT, pubNameShort);
	}

	/**
	 * Set the publisher web name
	 * @param pubNameWeb
	 */
	public void setPubNameWeb(final String pubNameWeb)
	{
		addPublisher("WEB:" + pubNameWeb);
		stringChar.put(StringKey.PUB_NAME_WEB, pubNameWeb);
	}

	/**
	 * Sets the 'load rank' of the campaign.
	 * @param rank the wanted load rank
	 */
	public void setRank(final int rank)
	{
		integerChar.put(IntegerKey.RANK, rank);
	}

	/**
	 * Set the setting
	 * @param setting
	 */
	public void setSetting(final String setting)
	{
		stringChar.put(StringKey.SETTING, setting);
	}

	/**
	 * @return whether or not the d20 info will pop up when this campaign is loaded
	 */
	public boolean isD20()
	{
		return isD20;
	}

	/**
	 * Set the isd20 flag
	 * @param isD20
	 */
	public void setIsD20(final boolean isD20)
	{
		this.isD20 = isD20;
	}

	/**
	 * Sets whether this campaign is licensed.
	 * @param isLicensed
	 */
	public void setIsLicensed(final boolean isLicensed)
	{
		this.isLicensed = isLicensed;
	}

	/**
	 * Sets whether the campaign is loaded.
	 * @param isLoaded
	 */
	public void setIsLoaded(final boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}

	/**
	 * Set the isOGL flag
	 * @param isOGL
	 */
	public void setIsOGL(final boolean isOGL)
	{
		this.isOGL = isOGL;
	}

	/**
	 * Returns whether this campaign is licensed
	 * @return true if this campaign is licensed
	 */
	public boolean isLicensed()
	{
		return isLicensed;
	}

	/**
	 * @return true if the campaign (source file set) is loaded.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * @return whether or not the OGL will pop up when this campaign is loaded
	 */
	public boolean isOGL()
	{
		return isOGL;
	}

	/**
	 * Set the campaign options
	 * @param options
	 */
	public void setOptions(final Properties options)
	{
		this.options = options;
	}

	/**
	 * @return the options which are to apply to this campaign
	 */
	public Properties getOptions()
	{
		return options;
	}

	/**
	 * Get the campaign options as a List
	 * @return campaign options
	 */
	public List<String> getOptionsList()
	{
		final List<String> aList = new ArrayList<String>();

		if (options != null)
		{
			for (Enumeration<?> e = options.propertyNames(); e.hasMoreElements();)
			{
				aList.add(e.nextElement().toString());
			}
		}

		return aList;
	}

	/**
	 * Returns the publisherMap.
	 * @return Map
	 */
	public Map<String, String> getPublisherMap()
	{
		return publisherMap;
	}

	/**
	 * Get the publisher with key
	 * @param key
	 * @return publisher
	 */
	public String getPublisherWithKey(final String key)
	{
		final String val = publisherMap.get(key);

		return (val != null) ? val : "";
	}

	/**
	 * Sets whether this campaign should be listed in the campaigns menu.
	 * @param showInMenu
	 */
	public void setShowInMenu(final boolean showInMenu)
	{
		this.showInMenu = showInMenu;
	}

	/**
	 * This method returns a reference to the Campaign that this object
	 * originated from.  In this case, it will return (this).
	 * @return Campaign instance referencing the file containing the
	 *         source for this object
	 */
	public Campaign getSourceCampaign()
	{
		return this;
	}

	/**
	 * Add a publisher
	 * @param argPublisher
	 */
	public void addPublisher(final String argPublisher)
	{
		final String publisher;

		if (argPublisher.startsWith("PUBNAME"))
		{
			publisher = argPublisher.substring(7);
		}
		else
		{
			publisher = argPublisher;
		}

		final String key = publisher.substring(0, publisher.indexOf(":"));
		publisherMap.put(key, publisher.substring(publisher.indexOf(":") + 1));
	}

	/**
	 * Returns whether this campaign should be listed in the campaigns menu
	 * @return true if this campaign should be listed in the campaigns menu
	 */
	public boolean canShowInMenu()
	{
		return showInMenu;
	}

	/**
	 * Returns a list of the Campaign objects that were loaded by this Campaign.
	 * 
	 * @return A list of <tt>Campaign</tt>s loaded by this Campaign.
	 */
	public List<Campaign> getSubCampaigns()
	{
		final List<String> pccFiles = getPccFiles();

		final List<Campaign> ret = new ArrayList<Campaign>(pccFiles.size());
		
		for ( final String fileName : pccFiles )
		{
			final Campaign campaign = Globals.getCampaignByFilename(fileName, true);
			ret.add(campaign);
		}
		return ret;
	}

	public Object clone()
	{
		Campaign newCampaign = null;

		try
		{
			newCampaign = (Campaign) super.clone();
			newCampaign.isLoaded = isLoaded;
			newCampaign.showInMenu = showInMenu;
			newCampaign.isD20 = isD20;
			newCampaign.isOGL = isOGL;
			newCampaign.isLicensed = isLicensed;
			newCampaign.options = (Properties) options.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return newCampaign;
	}
}
