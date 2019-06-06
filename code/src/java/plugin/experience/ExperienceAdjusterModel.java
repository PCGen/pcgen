/*
 * Copyright 2003 (C) Devon Jones
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
package plugin.experience;

import java.io.File;
import java.util.Observable;
import java.util.stream.IntStream;

import gmgen.io.ReadXML;
import gmgen.plugin.Combatant;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.State;
import gmgen.util.LogUtilities;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;
import plugin.experience.gui3.PreferencesExperiencesPanelController;

/**
 * This {@code class} holds all the necessary data in order to have
 * functionality for the experience adjuster.<br>
 */
public class ExperienceAdjusterModel extends Observable
{
	private ReadXML experienceTable = null;
	private final ExperienceList enemies = new ExperienceList();
	protected ExperienceList party = new ExperienceList();
	protected InitHolderList combat;
	protected File dir;
	protected double multiplier = 1.0;

	/** The value of experience gotten from a group. */
	private int partyExperience;

	/**
	 * Class constructor for ExperienceAdjusterView taking a
	 * parent directory.  This will call the
	 * function {@code initComponents} to initialise all the GUI
	 * components on the {@code JPanel}.
	 * @param parentDir the directory this is running out of
	 */
	ExperienceAdjusterModel(File parentDir)
	{
		dir = parentDir;
	}

	/**
	 * Set combat
	 * @param combat
	 */
	public void setCombat(InitHolderList combat)
	{
		this.combat = combat;
	}

	/**
	 * Get enemies
	 * @return enemies
	 */
	public ExperienceList getEnemies()
	{
		return enemies;
	}

	/**
	 * Set multiplier
	 * @param mult
	 */
	public void setMultiplier(double mult)
	{
		multiplier = mult;
	}

	/**
	 * Set party
	 * @param party
	 */
	public void setParty(ExperienceList party)
	{
		this.party = party;
	}

	/**
	 * Get party
	 * @return party
	 */
	public ExperienceList getParty()
	{
		return party;
	}

	/**
	 * Add enemy
	 * @param enemy
	 */
	public void addEnemy(ExperienceListItem enemy)
	{
		combat.add(enemy.getCombatant());
		enemies.addElement(enemy);
	}

	/**
	 * Adds experience to a certain character.
	 * @param item
	 * @param experience the value to add to the character.
	 */
	void addExperienceToCharacter(ExperienceListItem item, int experience)
	{
		Combatant cbt = item.getCombatant();
		cbt.setXP(cbt.getXP() + experience);
		LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME,
			cbt.getName() + " Awarded " + experience + " Experience");
	}

	/**
	 * Adds experience to a group of combatants.
	 */
	void addExperienceToParty()
	{
		int expType = SettingsHandler.getGMGenOption(ExperienceAdjusterPlugin.LOG_NAME + ".ExperienceType",
				PreferencesExperiencesPanelController.EXPERIENCE_35);
		LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME,
			"Party Awarded " + getPartyExperience() + " Total Experience Split as:");

		for (int i = 0; i < party.size(); i++)
		{
			Combatant cbt = party.get(i).getCombatant();
			if (expType == PreferencesExperiencesPanelController.EXPERIENCE_3)
			{
				cbt.setXP(cbt.getXP() + (getPartyTotalExperience() / party.size()));
				LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME,
					cbt.getName() + ": " + (getPartyTotalExperience() / party.size()));
			}
			else
			{
				cbt.setXP(cbt.getXP() + getCombatantExperience(cbt));
				LogUtilities.inst().logMessage(ExperienceAdjusterPlugin.LOG_NAME,
					cbt.getName() + ": " + getCombatantExperience(cbt));
			}
		}
	}

	/**
	 * Clear enemies
	 */
	void clearEnemies()
	{
		IntStream.range(0, enemies.size())
		         .mapToObj(enemies::get)
		         .forEach(item -> combat.remove(item.getCombatant()));

		enemies.removeAllElements();
	}

	/**
	 * Populate lists
	 */
	void populateLists()
	{
		if (combat != null)
		{
			party.removeAllElements();
			enemies.removeAllElements();

			for (InitHolder iH : combat)
			{
				if (iH instanceof Combatant)
				{
					Combatant cbt = (Combatant) iH;

					if (cbt.getCombatantType().equals("PC"))
					{
						party.addElement(new ExperienceListItem(cbt));
					}
					else if (cbt.getCombatantType().equals("Enemy"))
					{
						if (cbt.getStatus() == State.Dead || cbt.getStatus() == State.Defeated)
						{
							enemies.addElement(new ExperienceListItem(cbt));
						}
					}
				}
			}
		}
	}

	/**
	 * Remove enemy
	 * @param enemy
	 */
	void removeEnemy(ExperienceListItem enemy)
	{
		combat.remove(enemy.getCombatant());
		enemies.removeElement(enemy);
	}

	/**
	 * Get combatant experience
	 * @param cbt
	 * @return combatant experience
	 */
	private int getCombatantExperience(Combatant cbt)
	{
		float enemyCR;
		int tableCR;
		int experience = 0;
		File experienceFolder = new File(dir, "experience_tables");
		File experienceFile = new File(experienceFolder, "7_1.xml");
		// Lets not load the massive XML file more than we have to
		if (experienceTable == null)
		{
			experienceTable = new ReadXML(experienceFile);
		}

		for (int i = 0; i < enemies.size(); i++)
		{
			ExperienceListItem item = enemies.get(i);
			enemyCR = item.getCombatant().getCR();

			if (enemyCR < 1)
			{
				tableCR = 1;
			}
			else
			{
				tableCR = (int) enemyCR;
			}

			String xp = (String) experienceTable.getTable().crossReference(Integer.toString((int) cbt.getCR()),
				Integer.toString(tableCR));

			try
			{
				if (enemyCR < 1)
				{
					experience += (int) (Float.parseFloat(xp) * enemyCR);
				}
				else
				{
					experience += Integer.parseInt(xp);
				}
			}
			catch (Exception e)
			{
				Logging.errorPrint("Experience Value: '" + xp + "' Not a number");
				Logging.errorPrint(e.getMessage(), e);
			}
		}

		return (int)((experience * multiplier) / party.size());
	}

	/**
	 * Get party total experience
	 * @return party total experience
	 */
	private int getPartyTotalExperience()
	{
		float enemyCR;
		int tableCR;
		int experience = 0;
		File experienceFolder = new File(dir, "experience_tables");
		File experienceFile = new File(experienceFolder, "7_1.xml");
		// Lets not load the massive XML file more than we have to
		if (experienceTable == null)
		{
			experienceTable = new ReadXML(experienceFile);
		}

		for (int i = 0; i < enemies.size(); i++)
		{
			ExperienceListItem item = enemies.get(i);
			enemyCR = item.getCombatant().getCR();

			if (enemyCR < 1)
			{
				tableCR = 1;
			}
			else
			{
				tableCR = (int) enemyCR;
			}

			String xp = (String) experienceTable.getTable().crossReference(Integer.toString(party.averageCR()),
				Integer.toString(tableCR));

			try
			{
				if (enemyCR < 1)
				{
					experience += (int) (Float.parseFloat(xp) * enemyCR);
				}
				else
				{
					experience += Integer.parseInt(xp);
				}
			}
			catch (Exception e)
			{
				Logging.errorPrint("Experience Value: '" + xp + "' Not a number");
				Logging.errorPrint(e.getMessage(), e);
			}
		}

		return (int)(experience * multiplier);
	}

	/**
	 * Gets the group experience,
	 * @return the experience for the group.
	 */
	int getPartyExperience()
	{
		return partyExperience;
	}

	/**
	 * Updates the value displayed on the GUI for group experience.
	 */
	void updatePartyExperience()
	{
		int expType = SettingsHandler.getGMGenOption(ExperienceAdjusterPlugin.LOG_NAME + ".ExperienceType",
				PreferencesExperiencesPanelController.EXPERIENCE_35);
		if (expType == PreferencesExperiencesPanelController.EXPERIENCE_3)
		{
			partyExperience = getPartyTotalExperience();
		}
		else
		{
			partyExperience =  IntStream.range(0, party.size())
			         .mapToObj(party::get)
			         .map(ExperienceListItem::getCombatant)
			         .mapToInt(this::getCombatantExperience)
			         .sum();
		}
	}
}
