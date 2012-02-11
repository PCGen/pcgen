/*
 * CharacterFacade.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jun 12, 2008, 8:27:12 PM
 */
package pcgen.core.facade;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;

import javax.swing.undo.UndoManager;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.facade.event.ChangeListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.io.ExportHandler;

/**
 * The CharacterFacade interface provides a key role in separation
 * of the core and the UI layers. The UI can only operate on this
 * interface the core provides the implementation. This class
 * is heavily event driven, ie, any changes that occur to the
 * model will result to an event being fired that a listener
 * can pick up on. To operate like this, all values returned
 * from this class, with a couple of exceptions, are models that can
 * be listenered to. Two of the most commonly used models are
 * the <code>ReferenceFacade</code> and the <code>ListFacade</code>
 * <br>
 * Note: This facade returns references to items of interest.
 * These allow not only the values to be retrieved but also
 * interested parties to register as listeners for changes to the valiues.
 * @see pcgen.core.facade.util.ListFacade
 * @see ReferenceFacade
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface CharacterFacade
{

	public InfoFactory getInfoFactory();

	public UndoManager getUndoManager();

	public ReferenceFacade<GenderFacade> getGenderRef();

	public void setAlignment(AlignmentFacade alignment);

	public ReferenceFacade<AlignmentFacade> getAlignmentRef();

	public void setGender(GenderFacade gender);

	public void setGender(String gender);

	/**
	 * @param stat The stat to retrieve the base for
	 * @return A reference to the base score for the stat
	 */
	public ReferenceFacade<Integer> getScoreBaseRef(StatFacade stat);

	/**
	 * @param stat The stat to retrieve the mod total for
	 * @return The modifier for the stat total
	 */
	public int getModTotal(StatFacade stat);

	/**
	 * @param stat The stat to retrieve the base score of
	 * @return The base (user set) score for the stat
	 */
	public int getScoreBase(StatFacade stat);

	/**
	 * Update the base score of the stat.
	 * @param stat The stat to be set.
	 * @param score The new base score.
	 */
	public void setScoreBase(StatFacade stat, int score);

	/**
	 * Retrieve the display string for the score total. This may be a 
	 * non-number if the stat is a non-ability (e.g. ghost constitution)
	 * or if the game mode has named stat values (see the STATROLLTEXT 
	 * game mode token).
	 *  
	 * @param stat The stat to be retrieved
	 * @return The display string for the score total
	 */
	public String getScoreTotalString(StatFacade stat);

	/**
	 * @param stat The stat to retrieve the racial bonus of.
	 * @return The racial bonus to the stat score.
	 */
	public int getScoreRaceBonus(StatFacade stat);

	/**
	 * Retrieve the bonus to the stat score from sources other than the 
	 * character's race. e.g. templates, abilities. Also called the misc bonus.
	 * @param stat The stat to retrieve the other bonus of.
	 * @return The misc bonus to the stat score
	 */
	public int getScoreOtherBonus(StatFacade stat);

	public void addAbility(AbilityCategoryFacade category,
						   AbilityFacade ability);

	public void removeAbility(AbilityCategoryFacade category,
							  AbilityFacade ability);

	public boolean hasAbility(AbilityCategoryFacade category,
							  AbilityFacade ability);

	/**
	 * Note: This method should never return null. If the character does not possess
	 * any abilities in the parameter category, this method should create a new
	 * DefaultGenericListModel for that category and keep a reference to it for future use.
	 * @param category
	 * @return a List of Abilities the character posseses in the specified category
	 */
	public ListFacade<AbilityFacade> getAbilities(AbilityCategoryFacade category);

	/**
	 * Retrieve a list of the ability categories that are currently relevant to 
	 * the character. That is those ability categories that the character has 
	 * abilities or unspent pool points in. The list will be updated as 
	 * categories become active or inactive.
	 * @return The list of active categories.
	 */
	public ListFacade<AbilityCategoryFacade> getActiveAbilityCategories();

	/**
	 *
	 * @return
	 * @deprecated
	 */
	public ListFacade<CharacterLevelFacade> getLevels();

	public void addCharacterLevels(ClassFacade[] classes);

	public void removeCharacterLevels(int levels);

	/**
	 * This returns the number of times that a given class has been taken by this character.
	 * @param c a ClassFacade
	 * @return the total level of a class
	 */
	public int getClassLevel(ClassFacade c);

	public int getRemainingSelections(AbilityCategoryFacade category);

	public void setRemainingSelection(AbilityCategoryFacade category,
									  int remaining);

	/**
	 *
	 * @return
	 * @deprecated
	 */
	public int getSkillModifier(SkillFacade skill, CharacterLevelFacade level);

	/**
	 *
	 * @return
	 * @deprecated
	 */
	public float getSkillRanks(SkillFacade skill, CharacterLevelFacade level);

	/**
	 *
	 * @return
	 * @deprecated
	 */
	public float getMaxRanks(SkillCost cost, CharacterLevelFacade level);

	/**
	 * Adjust the cash held by the character.
	 * @param modVal The amount to add to the character's funds.
	 */
	public void adjustFunds(BigDecimal modVal);

	/**
	 * Set the cash held by the character.
	 * @param newVal The new amount for the character's funds.
	 */
	public void setFunds(BigDecimal newVal);

	/**
	 * @return A reference to the amount of gold the character owns.
	 */
	public ReferenceFacade<BigDecimal> getFundsRef();

	/**
	 * @return A reference to the total wealth of the character.
	 */
	public ReferenceFacade<BigDecimal> getWealthRef();

	/**
	 * @return A reference to the chosen buy sell rate scheme for the character.
	 */
	public ReferenceFacade<GearBuySellFacade> getGearBuySellRef();

	/**
	 * Set a new buy sell rate scheme for the character.
	 * @param scheme The new buy sell rate scheme.
	 */
	public void setGearBuySellRef(GearBuySellFacade scheme);

	/**
	 * @param allowed Is the character allowed to spend more funds than they have.
	 */
	public void setAllowDebt(boolean allowed);

	/**
	 * @return True if the character is allowed to spend more funds than they have.
	 */
	public boolean isAllowDebt();
	
	public ListFacade<EquipmentSetFacade> getEquipmentSets();

	public ReferenceFacade<EquipmentSetFacade> getEquipmentSetRef();

	public void setEquipmentSet(EquipmentSetFacade set);

	public EquipmentListFacade getPurchasedEquipment();

	public void addPurchasedEquipment(EquipmentFacade equipment, int quantity, boolean customize);

	public void removePurchasedEquipment(EquipmentFacade equipment, int quantity);

	//public int getQuantity(EquipmentFacade equipment);
	public boolean isQualifiedFor(EquipmentFacade equipment);

	/**
	 * Create an equipment item sized for the character. Will return an existing 
	 * item if a suitable one already exists, including the passed in item if it 
	 * is already the correct size ir if the item is not the type of item that 
	 * can be resized.
	 * 
	 * @param equipment The equipment item to be resized.
	 * @return The item at the correct size.
	 */
	public EquipmentFacade getEquipmentSizedForCharacter(EquipmentFacade equipment);

	/**
	 * Whether we should automatically resize all purchased gear to match the 
	 * character's size.
	 * @return true if equipment should be auto resize.
	 */
	public boolean isAutoResize();

	/**
	 * Update whether we should automatically resize all purchased gear to match  
	 * the character's size.
	 * 
	 * @param autoResize The new value for auto resize equipment option.
	 */
	public void setAutoResize(boolean autoResize);

	public EquipmentSetFacade createEquipmentSet(String name);

	public void deleteEquipmentSet(EquipmentSetFacade set);

	public boolean isQualifiedFor(ClassFacade c);

	public boolean isAutomatic(LanguageFacade language);

	public void addTemplate(TemplateFacade template);

	public void removeTemplate(TemplateFacade template);

	public ListFacade<TemplateFacade> getTemplates();
	//public boolean isBonus(LanguageFacade language);

	/**
	 * Note: this returns both the bonuses that the character
	 * has applied as well as the ones that haven't been applied.
	 * @return a list of bonuses than the character can apply
	 */
	public ListFacade<TempBonusFacade> getAvailableTempBonuses();

	/**
	 * adds a temp bonus to the character
	 * @param bonus the bonus to add
	 */
	public void addTempBonus(TempBonusFacade bonus);

	/**
	 * removes a bonus from the character
	 * @param bonus the bonus to remove
	 */
	public void removeTempBonus(TempBonusFacade bonus);

	/**
	 * 
	 * @return a list of bonuses that have been added to the character
	 */
	public ListFacade<TempBonusFacade> getTempBonuses();

	/**
	 * This returns a DataSetFacade that contains all
	 * of the sources that this character was loaded with.
	 * The returned DataSetFacade can be used to browse all
	 * of the other facades available for this character.
	 * @return the DataSetFacade for this character
	 */
	public DataSetFacade getDataSet();

	/**
	 * @return a reference to this character's Race
	 */
	public ReferenceFacade<RaceFacade> getRaceRef();

	/**
	 * @return A reference to a list containing the character's race.
	 */
	public ListFacade<RaceFacade> getRaceAsList();

	/**
	 * Sets this character's race
	 * @param race
	 */
	public void setRace(RaceFacade race);

	/**
	 * @return a reference to this character's tab name
	 */
	public ReferenceFacade<String> getTabNameRef();

	/**
	 * @param name the text to displayed in the character's tab
	 */
	public void setTabName(String name);

	/**
	 * @return a reference to this character's name
	 */
	public ReferenceFacade<String> getNameRef();

	/**
	 * Sets this character's name
	 * @param name the name of the character
	 */
	public void setName(String name);

	/**
	 * @return a reference to this character's player's name
	 */
	public ReferenceFacade<String> getPlayersNameRef();

	/**
	 * @param name The name of the player
	 */
	public void setPlayersName(String name);

	/**
	 * @return a reference to this character's handedness string
	 */
	public ReferenceFacade<SimpleFacade> getHandedRef();

	/**
	 * @param handedness The new handedness string for the character
	 */
	public void setHanded(SimpleFacade handedness);

	/**
	 * @see setFile(File)
	 * @return a reference to the character's file
	 */
	public ReferenceFacade<File> getFileRef();

	/**
	 * Sets the file that this character will be saved to.
	 * @see getFileRef()
	 * @param file the File to associate with this character
	 */
	public void setFile(File file);

	public ReferenceFacade<DeityFacade> getDeityRef();

	public void setDeity(DeityFacade deity);

	/**
	 * @return The domains that the character knows
	 */
	public ListFacade<DomainFacade> getDomains();

	/**
	 * Add a domain to the list of those the character knows.
	 * @param domain The domain to add.
	 */
	public void addDomain(DomainFacade domain);

	/**
	 * Remove a domain from the list of those the character knows.
	 * @param domain The domain to remove.
	 */
	public void removeDomain(DomainFacade domain);

	/**
	 * @return The maximum number of domains the character can know.
	 */
	public ReferenceFacade<Integer> getMaxDomains();

	public ReferenceFacade<Integer> getRemainingDomainSelectionsRef();

	/**
	 * @return The domains which the character has access to.
	 */
	public ListFacade<DomainFacade> getAvailableDomains();

	public ListFacade<LanguageFacade> getLanguages();
//
//	public void setBonusLanguages(List<LanguageFacade> languages);
//
//	public void setSkillLanguages(List<LanguageFacade> language);

	public ListFacade<LanguageChooserFacade> getLanguageChoosers();
//
//	/**
//	 * @return The number of bonus languages the character has remaining to be selected
//	 */
//	public ReferenceFacade<Integer> getNumBonusLanguagesOutstanding();
//
//	/**
//	 * @return The number of languages from skills that the character has remaining to be selected
//	 */
//	public ReferenceFacade<Integer> getNumSkillLanguagesOutstanding();
//
//	/**
//	 * @return The list of valid selections for bonus languages for this character.
//	 */
//	public List<LanguageFacade> getAvailBonusLangages();
//
//	public List<LanguageFacade> getCurrBonusLangages();

	/**
	 * Write the character details, as defined by the export handler to the writer.
	 * 
	 * @param theHandler The ExportHandler that defines how the output will be formatted.
	 * @param buf The writer the character details are to be output to.
	 */
	public void export(ExportHandler theHandler, BufferedWriter buf);

	/**
	 * gets the UIDelegate that this character uses to display messages
	 * and choosers
	 * @return the UIDelegate that this character uses
	 */
	public UIDelegate getUIDelegate();

	/**
	 * @return The facade for character levels for this character.
	 */
	public CharacterLevelsFacade getCharacterLevelsFacade();

	/**
	 * @return The facade for description for this character.
	 */
	public DescriptionFacade getDescriptionFacade();

	/**
	 * Set the character's current experience point value
	 * @param xp The new XP value to be set
	 */
	public void setXP(final int xp);

	/**
	 * @return a reference to this character's current experience point value
	 */
	public ReferenceFacade<Integer> getXPRef();

	/**
	 * Adjust the character's current experience point value
	 * @param xp The value to be added to the character's current experience point value
	 */
	public void adjustXP(final int xp);

	/**
	 * @return A reference to the XP total that will qualify the character for the next level
	 */
	public ReferenceFacade<Integer> getXPForNextLevelRef();

	/**
	 * Set the character's XP table.
	 * *
	 * @param xpTableName The name of the XP table to be set
	 */
	public void setXPTable(final String xpTableName);

	/**
	 * @return A reference to the name of the character's XP table
	 */
	public ReferenceFacade<String> getXPTableNameRef();

	/**
	 * Set the character's age in years.
	 * @param age The new age to be set.
	 */
	public void setAge(final int age);

	/**
	 * @return A reference to the age of the character
	 */
	public ReferenceFacade<Integer> getAgeRef();

	/**
	 * @return A list of the defined age categories.  
	 */
	public ListFacade<SimpleFacade> getAgeCategories();

	/**
	 * Set the character's age category. Will also reset their age if the age category 
	 * has changed.
	 * @param ageCat The new age category to be set
	 */
	public void setAgeCategory(final SimpleFacade ageCat);

	/**
	 * @return A reference to the age category of the character.
	 */
	public ReferenceFacade<SimpleFacade> getAgeCategoryRef();

	/**
	 * @return A reference to the label text for the character's stats total 
	 */
	public ReferenceFacade<String> getStatTotalLabelTextRef();

	/**
	 * @return A reference to the text for the character's stats total 
	 */
	public ReferenceFacade<String> getStatTotalTextRef();

	/**
	 * @return A reference to the label text for the character's modifier total
	 */
	public ReferenceFacade<String> getModTotalLabelTextRef();

	/**
	 * @return A reference to the text for the character's modifier total
	 */
	public ReferenceFacade<String> getModTotalTextRef();

	/**
	 * @return A list of things to be done for the character
	 */
	public ListFacade<TodoFacade> getTodoList();

	/**
	 * Roll a new set of ability scores for the character.
	 */
	public void rollStats();

	/**
	 * @return true If the current stat generation method supports randomly rolling stats. 
	 */
	public boolean isStatRollEnabled();

	public ReferenceFacade<Integer> getTotalHPRef();

	public ReferenceFacade<String> getCarriedWeightRef();

	public ReferenceFacade<String> getLoadRef();

	public ReferenceFacade<String> getWeightLimitRef();

	/**
	 * @return A reference to the stat roll method for the character 
	 */
	public ReferenceFacade<Integer> getRollMethodRef();

	/**
	 * Notify that the roll method may have changed. 
	 */
	public void refreshRollMethod();

	/**
	 * Check if the character meets all requirements to be of the onject.
	 * @param infoFacade The object to be checked.
	 * @return True if the character qualifies for the object, false if not.
	 */
	public boolean isQualifiedFor(InfoFacade infoFacade);

	/**
	 * Check if the character meets all requirements to take the domain.
	 * @param domain The domain to be checked.
	 * @return True if the character can take the domain, false if not.
	 */
	public boolean isQualifiedFor(DomainFacade domain);

	public void addCharacterChangeListener(CharacterChangeListener listener);

	public void removeCharacterChangeListener(CharacterChangeListener listener);

	public static interface CharacterChangeListener
	{

		public void characterChanged();

	}

	public Nature getAbilityNature(AbilityFacade ability);
//
//	/**
//	 * @param category the category of the ability
//	 * @param ability the ability that has choices
//	 * @return a String which represents the choices made for this ability
//	 */
//	public String getAbilityChoiceDisplayString(AbilityCategoryFacade category, AbilityFacade ability);

	public SpellSupportFacade getSpellSupport();

	/**
	 * @return a reference to the character's full portrait image
	 */
	public ReferenceFacade<File> getPortraitRef();

	/**
	 * Sets the file containing the portrait image
	 * @param file a File containing the portrait image
	 */
	public void setPortrait(File file);

	/**
	 * The thumbnail cropping rectangle is used to indicate the area
	 * of the portrait image that is to be used for the character's thumbnail.
	 * Note: the Rectangle returned from the reference is passed by value, not by
	 * reference.
	 * @return a reference to the cropping rectangle for the thumbnail
	 */
	public ReferenceFacade<Rectangle> getThumbnailCropRef();

	public void setThumbnailCrop(Rectangle rect);

	/**
	 * Retrieve the current export state of the BiographyField.
	 * 
	 * @param field The BiographyField to be examined
	 * @return true if the field should be exported, false if it should be suppressed from export.
	 */
	public boolean getExportBioField(BiographyField field);

	/**
	 * Set the export state of the BiographyField.
	 * 
	 * @param field The BiographyField 
	 * @param export if the field should be exported, false if it should be suppressed from export.
	 */
	public void setExportBioField(BiographyField field, boolean export);

	/**
	 * @return a reference to this character's skin color.
	 */
	public ReferenceFacade<String> getSkinColorRef();

	/**
	 * @param color the skin color to set.
	 */
	public void setSkinColor(String color);

	/**
	 * @return a reference to this character's hair color.
	 */
	public ReferenceFacade<String> getHairColorRef();

	/**
	 * @param color the hair color to set.
	 */
	public void setHairColor(String color);

	/**
	 * @return a reference to this character's eye color.
	 */
	public ReferenceFacade<String> getEyeColorRef();

	/**
	 * @param color the eye color to set.
	 */
	public void setEyeColor(String color);

	/**
	 * @return a reference to this character's height.
	 */
	public ReferenceFacade<Integer> getHeightRef();

	/**
	 * @param height the height to set.
	 */
	public void setHeight(int height);

	/**
	 * @return a reference to this character's weight.
	 */
	public ReferenceFacade<Integer> getWeightRef();

	/**
	 * @param weight the weight to set.
	 */
	public void setWeight(int weight);

	/**
	 * Register a listener to be advised of potential changes in the number of 
	 * selections for an ability category. 
	 * @param listener The class to be advised of a change.
	 */
	public void addAbilityCatSelectionListener(ChangeListener listener);

	/**
	 * Deregister a listener that should no longer be advised of potential changes
	 * in the number of selections for an ability category. 
	 * @param listener The class to no longer be advised of a change.
	 */
	public void removeAbilityCatSelectionListener(ChangeListener listener);

	/**
	 * @return true if the character has been changed and needs to be saved.
	 */
	public boolean isDirty();

}
