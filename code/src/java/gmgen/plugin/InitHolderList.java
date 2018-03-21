/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import pcgen.core.SettingsHandler;
import plugin.initiative.InitiativePlugin;

import gmgen.plugin.dice.Dice;
import gmgen.plugin.dice.Die;

/**
 */
public class InitHolderList extends ArrayList<InitHolder> {

	/**
	 * Gets the Max Init of the InitHolderList object, minimum 20
	 *
	 * @return the highest initiative in the list (minimum 20)
	 */
	public int getMaxInit() {
		return this.stream()
			.mapToInt(holder -> holder.getInitiative().getCurrentInitiative())
			.max().orElse(20);
	}

	/**
	 * Returns a vector intended for use as a row for a table.
	 *
	 * @param i
	 *          the index of the InitHolder to return the vector for
	 * @param columnOrder
	 *          a List containing the order of columns, retrieved from the
	 *          table object.
	 * @return The Vector that contains a table row.
	 */
	public Vector<Object> getRowVector(int i, List<String> columnOrder) {
		return this.get(i).getRowVector(columnOrder);
	}

	/**
	 * Determines if a string passed in exists within the InitHolderList And if it
	 * does, it appends a space and a number that is unique in the list
	 *
	 * @param name
	 *          String to compare
	 * @return Unique Name
	 */
	public String getUniqueName(String name) {
		int i = 1;
		String workingName = name;
		while (!isUniqueName(workingName)) {
			workingName = workingName.replaceAll(" \\(\\d.*\\)", "") + " (" + i + ")";
			i++;
		}
		return workingName;
	}

	/**
	 * Determines if a string passed in exists within the InitHolderList
	 *
	 * @param name
	 *          String to compare
	 * @return if the string is unique or not
	 */
	public boolean isUniqueName(String name) {
		return this.stream().noneMatch(c -> c.getName().equals(name));
	}

	/**
	 * Method for adding a combatant to the list
	 *
	 * @param user
	 *          The Combatant to be added
	 * @return if the add is successful.
	 */
    @Override
	public boolean add(InitHolder user) {
		boolean result = super.add(user);

		if (result) {
			this.sort();
		}

		return result;
	}

	/**
	 * Calculate the initiative
	 */
	public void calculateNumberField() {
		int j = 1;

		for (InitHolder c : this) {
			if (c instanceof Combatant) {
				Combatant cbt = (Combatant) c;
				cbt.setNumber(j);
				j++;
			}
		}
	}

	/** Rolls an initiative check for the whole list */
	public void check() {
		Die d20 = new Dice(1, 20);
		boolean pcroll = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".rollPCInitiatives", true);

		for (InitHolder c : this) {
			int roll = d20.roll();
			boolean doroll = true;
			if (!pcroll && c instanceof Combatant) {
				Combatant com = (Combatant) c;
				if (com.getCombatantType().equals("PC")) {
					doroll = false;
				}
			}

			if (doroll) {
				c.getInitiative().checkExtRoll(roll);
			} else {
				c.getInitiative().resetCurrentInitiative();
			}
		}

		this.sort();

		calculateNumberField();
	}

	/**
	 * Checks to see if a particular initiative is Active (a combatant has that
	 * initiative)
	 *
	 * @param init
	 *          Initiative value to check
	 * @return if it is active
	 */
	public boolean initValid(int init) {

		return this.stream()
				   .filter(c -> c.getStatus() != State.Dead)
				   .mapToInt(c -> c.getInitiative().getCurrentInitiative())
				   .anyMatch(cInit -> cInit == init);
	}

	/** sorts the list based on initiative */
	public void sort() {
		this.sort(new InitHolderComperator());
	}
}
