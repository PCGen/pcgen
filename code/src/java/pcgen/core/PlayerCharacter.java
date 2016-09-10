/*
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
package pcgen.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.NumericPCAttribute;
import pcgen.cdom.enumeration.StringPCAttribute;
import pcgen.cdom.list.CompanionList;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;

public interface PlayerCharacter extends Cloneable, VariableContainer
{
	@Override
	String toString();

	void setPCAttribute(NumericPCAttribute attr, int value);

	/**
	 * Sets player character information
	 *
	 * @param attr which attribute to set
	 * @param value the value to set it to
	 */
	void setPCAttribute(StringPCAttribute attr, String value);

	/**
	 * Set the current EquipSet that is used to Bonus/Equip calculations.
	 *
	 * @param eqSetId The equipSet to be used for Bonus Calculations and output
	 */
	void setCalcEquipSetId(String eqSetId);

	/**
	 * Get the id for the equipment set being used for calculation.
	 *
	 * @return id
	 */
	String getCalcEquipSetId();

	/**
	 * Set's current equipmentList to selected output EquipSet then loops
	 * through all the equipment and sets the correct status of each (equipped,
	 * carried, etc).
	 */
	void setCalcEquipmentList();

	/**
	 * Set's current equipmentList to selected output EquipSet then loops
	 * through all the equipment and sets the correct status of each (equipped,
	 * carried, etc). Boolean parameter useTempBonuses controls whether or
	 * not the temporary bonuses associated with equipment are applied.
	 *
	 * @param useTempBonuses whether to apply Temporary bonuses from equipment.
	 */
	void setCalcEquipmentList(boolean useTempBonuses);

	/**
	 * Apply the bonus from a follower to the master pc.
	 */
	void setCalcFollowerBonus();

	/**
	 * Get a class, represented by a given key, from among those possessed by this pc.
	 *
	 * @param key the class's key
	 * @return PCClass
	 */
	PCClass getClassKeyed(String key);

	/**
	 * Get the class list.
	 *
	 * @return classList
	 */
	ArrayList<PCClass> getClassList();

	/**
	 * Gets the Set of PCClass objects for this Character.
	 * @return a set of PCClass objects
	 */
	Set<PCClass> getClassSet();

	/**
	 * Set the cost pool, which is the number of points the character has spent.
	 *
	 * @param i the number of points spent
	 */
	void setCostPool(int i);

	/**
	 * Get the cost pool, which is the number of points the character has spent.
	 *
	 * @return costPool
	 */
	int getCostPool();

	/**
	 * Set the current equipment set name.
	 *
	 * @param aName the name of the new current equipment set
	 */
	void setCurrentEquipSetName(String aName);

	/**
	 * Get the deity.
	 *
	 * @return deity
	 */
	Deity getDeity();

	/**
	 * Selector.
	 *
	 * @return description lst
	 */
	String getDescriptionLst();

	/**
	 * Sets the character changed since last save.
	 * NB: This is not a 'safe' call - its use should be considered carefully and in
	 * particular it should not be called from a method used as part of PlayerCharacter
	 * cloning as this can mean conditional abilities get dropped when they are actually
	 * qualified for, just not at that point in the clone.
	 *
	 * @param dirtyState the new "dirty" value (may be false to indicate no change)
	 */
	void setDirty(boolean dirtyState);

	/**
	 * Gets whether the character has been changed since last saved.
	 *
	 * @return true if dirty
	 */
	boolean isDirty();

	/**
	 * Returns the serial for the instance - every time something changes the
	 * serial is incremented. Use to detect change in PlayerCharacter.
	 *
	 * @return serial
	 */
	int getSerial();

	/**
	 * Get the equipment set indexed by path.
	 *
	 * @param path the "path" of the equipSet to return
	 * @return EquipSet
	 */
	EquipSet getEquipSetByIdPath(String path);

	/**
	 * Get the current equipment set number.
	 *
	 * @return equipSet number
	 */
	int getEquipSetNumber();

	/**
	 * Get the character's "equipped" equipment.
	 * @return a set of the "equipped" equipment
	 */
	Set<Equipment> getEquippedEquipmentSet();

	/**
	 * Retrieves a list of the character's equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 *
	 * @return An ArrayList of the equipment objects in output order.
	 */
	List<Equipment> getEquipmentListInOutputOrder();

	/**
	 * Retrieves a list of the character's equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 *
	 * Deals with merge as well.  See the Constants package for acceptable values
	 * of merge .
	 *
	 * @param merge controls how much merging is done.
	 *
	 * @return An ArrayList of the equipment objects in output order.
	 */
	List<Equipment> getEquipmentListInOutputOrder(int merge);

	/**
	 * Get the master list of equipment.
	 *
	 * @return equipment master list
	 */
	List<Equipment> getEquipmentMasterList();

	/**
	 * Search among the PCs equipment for a named piece of equipment.
	 * @param name The name of the piece of equipment.
	 * @return null or the equipment named.
	 */
	Equipment getEquipmentNamed(String name);

	/**
	 * Set the characters eye colour.
	 *
	 * @param aString
	 *            the colour of their eyes
	 */
	void setEyeColor(String aString);

	/**
	 * Checks whether a PC is allowed to level up. A PC is not allowed to level
	 * up if the "Enforce Spending" option is set and he still has unallocated
	 * skill points and/or feat slots remaining. This can be used to enforce
	 * correct spending of these resources when creating high-level multiclass
	 * characters.
	 *
	 * @return true if the PC can level up
	 */
	boolean canLevelUp();

	/**
	 * Sets the filename of the character.
	 *
	 * @param newFileName the name of the file this character will be saved in
	 */
	void setFileName(String newFileName);

	/**
	 * Gets the filename of the character.
	 *
	 * @return file name of character
	 */
	String getFileName();

	/**
	 * Returns the followers associated with this character.
	 *
	 * @return A <tt>Set</tt> of <tt>Follower</tt> objects.
	 */
	Collection<Follower> getFollowerList();

	/**
	 * Sets the character's gender.
	 *
	 * <p>
	 * The gender will only be changed if the character does not have a template
	 * that locks the character's gender.
	 *
	 * @param g
	 *            A gender to try and set.
	 */
	void setGender(Gender g);

	/**
	 * Sets the character's wealth.
	 *
	 * <p>
	 * Gold here is used as a character's total purchase power not actual gold
	 * pieces.
	 *
	 * @param aString
	 *            A String gold amount. TODO - Do this parsing elsewhere.
	 */
	void setGold(String aString);

	/**
	 * Sets the character's wealth.
	 *
	 * <p>
	 * Gold here is used as a character's total purchase power not actual gold
	 * pieces.
	 *
	 * @param amt
	 *            A gold amount.
	 */
	void setGold(BigDecimal amt);

	/**
	 * Returns the character's total wealth.
	 *
	 * @see PlayerCharacter#setGold(String)
	 *
	 * @return A <tt>BigDecimal</tt> value for the character's wealth.
	 */
	BigDecimal getGold();

	/**
	 * Sets the character's handedness.
	 *
	 * @param h A handedness to try and set.
	 */
	void setHanded(Handed h);

	/**
	 * Sets the character's height in inches.
	 *
	 * @param i
	 *            A height in inches.
	 *
	 * TODO - This should be a double value stored in CM
	 */
	void setHeight(int i);

	/**
	 * Marks the character as being in the process of being loaded.
	 *
	 * <p>
	 * This information is used to prevent the system from trying to calculate
	 * values on partial information or values that should be set from the saved
	 * character.
	 *
	 * <p>
	 * TODO - This is pretty dangerous.
	 *
	 * @param newIsImporting
	 *            <tt>true</tt> to mark the character as being imported.
	 */
	void setImporting(boolean newIsImporting);

	/**
	 * Gets the character's list of languages.
	 *
	 * @return An unmodifiable language set.
	 */
	Set<Language> getLanguageSet();

	/**
	 * This method returns the effective level of this character for purposes of
	 * applying companion mods to a companion of the specified type.
	 * <p>
	 * <b>Note</b>: This whole structure is kind of messed up since nothing
	 * enforces that a companion mod of a given type always looks at the same
	 * variable (either Class or Variable).  Note it seems that this used to
	 * be driven off types but now it's driven from a list of companion mods
	 * but the java doc has not been updated.
	 *
	 * @param compList
	 *            A list of companionMods to get level for
	 * @return The effective level for this companion type
	 */
	int getEffectiveCompanionLevel(CompanionList compList);

	/**
	 * Set the master for this object also set the level dependent stats based
	 * on the masters level and info contained in the companionModList Array
	 * such as HitDie, SR, BONUS, SA, etc.
	 *
	 * @param aM
	 *            The master to be set.
	 */
	void setMaster(Follower aM);

	/**
	 * Returns the maximum number of followers this character can have from
	 * the given companion list. This method does not adjust for any followers
	 * already selected by the character.
	 *
	 * @param cList
	 *            A list of potential follower races
	 * @return The max number of followers -1 for any number
	 */
	int getMaxFollowers(CompanionList cList);

	/**
	 * Get the PlayerCharacter that is the "master" for this object.
	 *
	 * @return master PC
	 */
	PlayerCharacter getMasterPC();

	/**
	 * Sets the character's name.
	 *
	 * @param aString
	 *            A name to set.
	 */
	void setName(String aString);

	/**
	 * Gets the character's name.
	 *
	 * @return The name
	 */
	String getName();

	/**
	 * Takes all the Temporary Bonuses and Merges them into just the unique
	 * named bonuses.
	 *
	 * @return List of Strings
	 */
	List<String> getNamedTempBonusList();

	public static final class CasterLevelSpellBonus {
		private int bonus;
		private String type;

		/**
		 * Constructor
		 *
		 * @param b
		 * @param t
		 */
		CasterLevelSpellBonus(final int b, final String t) {
			bonus = b;
			type = t;
		}

		/**
		 * Get bonus
		 *
		 * @return bonus
		 */
		public int getBonus()
		{
			return bonus;
		}

		/**
		 * Get type
		 *
		 * @return type
		 */
		public String getType()
		{
			return type;
		}

		/**
		 * Set bonus
		 *
		 * @param newBonus
		 */
		public void setBonus(final int newBonus)
		{
			bonus = newBonus;
		}

		@Override
		public String toString()
		{
			return ("bonus: " + bonus + "    type: " + type);
		}

	}
}
