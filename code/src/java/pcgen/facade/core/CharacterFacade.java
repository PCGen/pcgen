/*
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
 */
package pcgen.facade.core;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.cdom.meta.CorePerspective;
import pcgen.core.AbilityCategory;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.EquipmentModifier;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.VariableProcessor;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ChangeListener;
import pcgen.io.ExportException;
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
 * the {@code ReferenceFacade} and the {@code ListFacade}
 *
 * Note: This facade returns references to items of interest.
 * These allow not only the values to be retrieved but also
 * interested parties to register as listeners for changes to the valiues.
 * @see pcgen.facade.util.ListFacade
 * @see ReferenceFacade
 */
public interface CharacterFacade extends CompanionFacade
{

	InfoFactory getInfoFactory();

	ReferenceFacade<Gender> getGenderRef();

	void setAlignment(PCAlignment alignment);

	ReferenceFacade<PCAlignment> getAlignmentRef();

	void setGender(Gender gender);

	void setGender(String gender);

	/**
	 * @param stat The stat to retrieve the base for
	 * @return A reference to the base score for the stat
	 */
    ReferenceFacade<Number> getScoreBaseRef(PCStat stat);

	/**
	 * @param stat The stat to retrieve the mod total for
	 * @return The modifier for the stat total
	 */
    int getModTotal(PCStat stat);

	/**
	 * @param stat The stat to retrieve the base score of
	 * @return The base (user set) score for the stat
	 */
    int getScoreBase(PCStat stat);

	/**
	 * Update the base score of the stat.
	 * @param stat The stat to be set.
	 * @param score The new base score.
	 */
    void setScoreBase(PCStat stat, int score);

	/**
	 * Retrieve the display string for the score total. This may be a
	 * non-number if the stat is a non-ability (e.g. ghost constitution)
	 * or if the game mode has named stat values (see the STATROLLTEXT
	 * game mode token).
	 *
	 * @param stat The stat to be retrieved
	 * @return The display string for the score total
	 */
    String getScoreTotalString(PCStat stat);

	/**
	 * @param stat The stat to retrieve the racial bonus of.
	 * @return The racial bonus to the stat score.
	 */
    int getScoreRaceBonus(PCStat stat);

	/**
	 * Retrieve the bonus to the stat score from sources other than the
	 * character's race. e.g. templates, abilities. Also called the misc bonus.
	 * @param stat The stat to retrieve the other bonus of.
	 * @return The misc bonus to the stat score
	 */
    int getScoreOtherBonus(PCStat stat);

	void addAbility(AbilityCategory category, AbilityFacade ability);

	void removeAbility(AbilityCategory category, AbilityFacade ability);

	/**
	 * Note: This method should never return null. If the character does not possess
	 * any abilities in the parameter category, this method should create a new
	 * DefaultGenericListModel for that category and keep a reference to it for future use.
	 * @param category
	 * @return a List of Abilities the character posseses in the specified category
	 */
    ListFacade<AbilityFacade> getAbilities(AbilityCategory category);

	/**
	 * Retrieve a list of the ability categories that are currently relevant to
	 * the character. That is those ability categories that the character has
	 * abilities or unspent pool points in. The list will be updated as
	 * categories become active or inactive.
	 * @return The list of active categories.
	 */
    ListFacade<AbilityCategory> getActiveAbilityCategories();

	void addCharacterLevels(PCClass[] classes);

	void removeCharacterLevels(int levels);

	/**
	 * This returns the number of times that a given class has been taken by this character.
	 * @param c a ClassFacade
	 * @return the total level of a class
	 */
    int getClassLevel(PCClass c);

	int getTotalSelections(AbilityCategory category);

	int getRemainingSelections(AbilityCategory category);

	void setRemainingSelection(AbilityCategory category, int remaining);

	/**
	 * Adjust the cash held by the character.
	 * @param modVal The amount to add to the character's funds.
	 */
    void adjustFunds(BigDecimal modVal);

	/**
	 * Set the cash held by the character.
	 * @param newVal The new amount for the character's funds.
	 */
    void setFunds(BigDecimal newVal);

	/**
	 * @return A reference to the amount of gold the character owns.
	 */
    ReferenceFacade<BigDecimal> getFundsRef();

	/**
	 * @return A reference to the total wealth of the character.
	 */
    ReferenceFacade<BigDecimal> getWealthRef();

	/**
	 * @return A reference to the chosen buy sell rate scheme for the character.
	 */
    ReferenceFacade<GearBuySellFacade> getGearBuySellRef();

	/**
	 * Set a new buy sell rate scheme for the character.
	 * @param scheme The new buy sell rate scheme.
	 */
    void setGearBuySellRef(GearBuySellFacade scheme);

	/**
	 * @param allowed Is the character allowed to spend more funds than they have.
	 */
    void setAllowDebt(boolean allowed);

	/**
	 * @return True if the character is allowed to spend more funds than they have.
	 */
    boolean isAllowDebt();

	ListFacade<EquipmentSetFacade> getEquipmentSets();

	ReferenceFacade<EquipmentSetFacade> getEquipmentSetRef();

	void setEquipmentSet(EquipmentSetFacade set);

	EquipmentListFacade getPurchasedEquipment();

	void addPurchasedEquipment(EquipmentFacade equipment, int quantity, boolean customize, boolean free);

	void removePurchasedEquipment(EquipmentFacade equipment, int quantity, boolean free);

	//public int getQuantity(EquipmentFacade equipment);
    boolean isQualifiedFor(EquipmentFacade equipment);

	/**
	 * Create an equipment item sized for the character. Will return an existing
	 * item if a suitable one already exists, including the passed in item if it
	 * is already the correct size ir if the item is not the type of item that
	 * can be resized.
	 *
	 * @param equipment The equipment item to be resized.
	 * @return The item at the correct size.
	 */
    EquipmentFacade getEquipmentSizedForCharacter(EquipmentFacade equipment);

	/**
	 * Whether we should automatically resize all purchased gear to match the
	 * character's size.
	 * @return true if equipment should be auto resize.
	 */
    boolean isAutoResize();

	/**
	 * Update whether we should automatically resize all purchased gear to match
	 * the character's size.
	 *
	 * @param autoResize The new value for auto resize equipment option.
	 */
    void setAutoResize(boolean autoResize);

	EquipmentSetFacade createEquipmentSet(String name);

	void deleteEquipmentSet(EquipmentSetFacade set);

	boolean isQualifiedFor(PCClass c);

	boolean isAutomatic(Language language);

	/**
	 * Is the user allowed to remove this language currently?
	 * e.g Automatic languages may not be removed.
	 * @param language The language to be checked.
	 * @return true if the language can be removed.
	 */
    boolean isRemovable(Language language);

	void addTemplate(PCTemplate template);

	void removeTemplate(PCTemplate template);

	ListFacade<PCTemplate> getTemplates();

	/**
	 * Note: this returns both the bonuses that the character
	 * has applied as well as the ones that haven't been applied.
	 * @return a list of bonuses than the character can apply
	 */
    ListFacade<TempBonusFacade> getAvailableTempBonuses();

	/**
	 * adds a temp bonus to the character
	 * @param bonus the bonus to add
	 */
    void addTempBonus(TempBonusFacade bonus);

	/**
	 * removes a bonus from the character
	 * @param bonus the bonus to remove
	 */
    void removeTempBonus(TempBonusFacade bonus);

	/**
	 * Enables or disables a temporary bonus to the character.
	 * @param bonusFacade the bonus to change
	 * @param active True: Make the bonus active, False: Make the bonus inactive
	 */
    void setTempBonusActive(TempBonusFacade bonusFacade, boolean active);

	/**
	 *
	 * @return a list of bonuses that have been added to the character
	 */
    ListFacade<TempBonusFacade> getTempBonuses();

	/**
	 * This returns a DataSetFacade that contains all
	 * of the sources that this character was loaded with.
	 * The returned DataSetFacade can be used to browse all
	 * of the other facades available for this character.
	 * @return the DataSetFacade for this character
	 */
    DataSetFacade getDataSet();

	/**
	 * @return a reference to this character's Race
	 */
	@Override
    ReferenceFacade<Race> getRaceRef();

	/**
	 * Sets this character's race
	 * @param race
	 */
    void setRace(Race race);

	/**
	 * @return a reference to this character's tab name
	 */
    ReferenceFacade<String> getTabNameRef();

	/**
	 * @param name the text to displayed in the character's tab
	 */
    void setTabName(String name);

	/**
	 * @return a reference to this character's name
	 */
	@Override
    ReferenceFacade<String> getNameRef();

	/**
	 * Sets this character's name
	 * @param name the name of the character
	 */
    void setName(String name);

	/**
	 * @return a reference to this character's player's name
	 */
    ReferenceFacade<String> getPlayersNameRef();

	/**
	 * @param name The name of the player
	 */
    void setPlayersName(String name);

	/**
	 * @return a reference to this character's handedness string
	 */
    ReferenceFacade<Handed> getHandedRef();

	/**
	 * @param handedness The new handedness string for the character
	 */
    void setHanded(Handed handedness);

	/**
	 * @see CharacterFacade#setFile(File)
	 * @return a reference to the character's file
	 */
	@Override
    ReferenceFacade<File> getFileRef();

	/**
	 * Sets the file that this character will be saved to.
	 * @see CharacterFacade#getFileRef()
	 * @param file the File to associate with this character
	 */
    void setFile(File file);

	ReferenceFacade<Deity> getDeityRef();

	void setDeity(Deity deity);

	/**
	 * @return The domains that the character knows
	 */
    ListFacade<QualifiedObject<Domain>> getDomains();

	/**
	 * Add a domain to the list of those the character knows.
	 * @param domain The domain to add.
	 */
    void addDomain(QualifiedObject<Domain> domain);

	/**
	 * Remove a domain from the list of those the character knows.
	 * @param domain The domain to remove.
	 */
    void removeDomain(QualifiedObject<Domain> domain);

	ReferenceFacade<Integer> getRemainingDomainSelectionsRef();

	ListFacade<Handed> getAvailableHands();

	ListFacade<Gender> getAvailableGenders();

	/**
	 * @return The domains which the character has access to.
	 */
    ListFacade<QualifiedObject<Domain>> getAvailableDomains();

	ListFacade<Language> getLanguages();

	ListFacade<LanguageChooserFacade> getLanguageChoosers();

	/**
	 * Remove a bonus language from the character.
	 * @param lang The language to be removed
	 */
    void removeLanguage(Language lang);

	/**
	 * Write the character details, as defined by the export handler to the writer.
	 *
	 * @param theHandler The ExportHandler that defines how the output will be formatted.
	 * @param buf The writer the character details are to be output to.
	 * @throws ExportException If the export fails.
	 */
    void export(ExportHandler theHandler, BufferedWriter buf) throws ExportException;

	/**
	 * gets the UIDelegate that this character uses to display messages
	 * and choosers
	 * @return the UIDelegate that this character uses
	 */
    UIDelegate getUIDelegate();

	/**
	 * @return The facade for character levels for this character.
	 */
    CharacterLevelsFacade getCharacterLevelsFacade();

	/**
	 * @return The facade for description for this character.
	 */
    DescriptionFacade getDescriptionFacade();

	/**
	 * Set the character's current experience point value
	 * @param xp The new XP value to be set
	 */
    void setXP(int xp);

	/**
	 * @return a reference to this character's current experience point value
	 */
    ReferenceFacade<Integer> getXPRef();

	/**
	 * Adjust the character's current experience point value
	 * @param xp The value to be added to the character's current experience point value
	 */
    void adjustXP(int xp);

	/**
	 * @return A reference to the XP total that will qualify the character for the next level
	 */
    ReferenceFacade<Integer> getXPForNextLevelRef();

	/**
	 * Set the character's XP table.
	 * *
	 * @param xpTableName The name of the XP table to be set
	 */
    void setXPTable(String xpTableName);

	/**
	 * Set the character's character type.
	 * *
	 * @param characterType The character type to be set
	 */
    void setCharacterType(String characterType);

	/**
	 * Set the character's associated preview sheet
	 * *
	 * @param previewSheet The preview sheet to be set
	 */
    void setPreviewSheet(String previewSheet);

	/**
	 * Set the character's display filter for skills
	 * *
	 * @param filter The skill filter to be set
	 */
    void setSkillFilter(SkillFilter filter);

	/**
	 * @return A reference to the name of the character's XP table
	 */
    ReferenceFacade<String> getXPTableNameRef();

	/**
	 * @return A reference to the name of the character's type
	 */
    ReferenceFacade<String> getCharacterTypeRef();

	/**
	 * @return A reference to the name of the character's
	 * associated preview sheet
	 */
    ReferenceFacade<String> getPreviewSheetRef();

	/**
	 * @return A reference to the character's display filter
	 * for skills
	 */
    ReferenceFacade<SkillFilter> getSkillFilterRef();

	/**
	 * Set the character's age in years.
	 * @param age The new age to be set.
	 */
    void setAge(int age);

	/**
	 * @return A reference to the age of the character
	 */
    ReferenceFacade<Integer> getAgeRef();

	/**
	 * @return A list of the defined age categories.
	 */
    ListFacade<String> getAgeCategories();

	/**
	 * Set the character's age category. Will also reset their age if the age category
	 * has changed.
	 * @param ageCat The new age category to be set
	 */
    void setAgeCategory(String ageCat);

	/**
	 * @return A reference to the age category of the character.
	 */
    ReferenceFacade<String> getAgeCategoryRef();

	/**
	 * @return A reference to the label text for the character's stats total
	 */
    ReferenceFacade<String> getStatTotalLabelTextRef();

	/**
	 * @return A reference to the text for the character's stats total
	 */
    ReferenceFacade<String> getStatTotalTextRef();

	/**
	 * @return A reference to the label text for the character's modifier total
	 */
    ReferenceFacade<String> getModTotalLabelTextRef();

	/**
	 * @return A reference to the text for the character's modifier total
	 */
    ReferenceFacade<String> getModTotalTextRef();

	/**
	 * @return A list of things to be done for the character
	 */
    ListFacade<TodoFacade> getTodoList();

	/**
	 * Roll a new set of ability scores for the character.
	 */
    void rollStats();

	/**
	 * @return true If the current stat generation method supports randomly rolling stats.
	 */
    boolean isStatRollEnabled();

	ReferenceFacade<Integer> getTotalHPRef();

	ReferenceFacade<String> getCarriedWeightRef();

	ReferenceFacade<String> getLoadRef();

	ReferenceFacade<String> getWeightLimitRef();

	/**
	 * @return A reference to the stat roll method for the character
	 */
    ReferenceFacade<Integer> getRollMethodRef();

	/**
	 * Notify that the roll method may have changed.
	 */
    void refreshRollMethod();

	/**
	 * Check if the character meets all requirements to be of the object.
	 * @param infoFacade The object to be checked.
	 * @return True if the character qualifies for the object, false if not.
	 */
    boolean isQualifiedFor(InfoFacade infoFacade);

	/**
	 * Check if the character meets all requirements to be of the race.
	 * @param race The race to be checked.
	 * @return True if the character qualifies for the race, false if not.
	 */
    boolean isQualifiedFor(Race race);

	/**
	 * Check if the character meets all requirements to take the domain.
	 * @param domain The domain to be checked.
	 * @return True if the character can take the domain, false if not.
	 */
    boolean isQualifiedFor(QualifiedObject<Domain> domain);

	/**
	 * Check if the character meets all requirements to take the deity.
	 * @param deity The deity to be checked.
	 * @return True if the character can take the deity, false if not.
	 */
    boolean isQualifiedFor(Deity deity);

	/**
	 * Check if the character meets all requirements to take the temporary bonus.
	 * @param tempBonusFacade The temporary bonus to be checked.
	 * @return True if the character can take the bonus, false if not.
	 */
    boolean isQualifiedFor(TempBonusFacade tempBonusFacade);

	/**
	 * Check if the character meets all requirements to know the spell.
	 * @param spell The spell to be checked.
	 * @param pcClass The class the spell would be added within.
	 * @return True if the character can know the spell, false if not.
	 */
    boolean isQualifiedFor(SpellFacade spell, PCClass pcClass);

	/**
	 * Is the modifier able to be added to the item of equipment?
	 * @param equipFacade The equipment item being modified.
	 * @param eqMod The equipment modifier to be checked.
	 * @return True if it can be added, false if not.
	 */
    boolean isQualifiedFor(EquipmentFacade equipFacade, EquipmentModifier eqMod);

	Nature getAbilityNature(AbilityFacade ability);
	//
	//	/**
	//	 * @param category the category of the ability
	//	 * @param ability the ability that has choices
	//	 * @return a String which represents the choices made for this ability
	//	 */
	//	public String getAbilityChoiceDisplayString(AbilityCategoryFacade category, AbilityFacade ability);

	SpellSupportFacade getSpellSupport();

	/**
	 * @return a reference to the character's full portrait image
	 */
    ReferenceFacade<File> getPortraitRef();

	/**
	 * Sets the file containing the portrait image
	 * @param file a File containing the portrait image
	 */
    void setPortrait(File file);

	/**
	 * The thumbnail cropping rectangle is used to indicate the area
	 * of the portrait image that is to be used for the character's thumbnail.
	 * Note: the Rectangle returned from the reference is passed by value, not by
	 * reference.
	 * @return a reference to the cropping rectangle for the thumbnail
	 */
    ReferenceFacade<Rectangle> getThumbnailCropRef();

	void setThumbnailCrop(Rectangle rect);

	/**
	 * @return a reference to this character's skin color.
	 */
    ReferenceFacade<String> getSkinColorRef();

	/**
	 * @param color the skin color to set.
	 */
    void setSkinColor(String color);

	/**
	 * @return a reference to this character's hair color.
	 */
    ReferenceFacade<String> getHairColorRef();

	/**
	 * @param color the hair color to set.
	 */
    void setHairColor(String color);

	/**
	 * @return a reference to this character's eye color.
	 */
    ReferenceFacade<String> getEyeColorRef();

	/**
	 * @param color the eye color to set.
	 */
    void setEyeColor(String color);

	/**
	 * @return a reference to this character's weight.
	 */
    ReferenceFacade<Integer> getWeightRef();

	/**
	 * @param weight the weight to set.
	 */
    void setWeight(int weight);

	/**
	 * Register a listener to be advised of potential changes in the number of
	 * selections for an ability category.
	 * @param listener The class to be advised of a change.
	 */
    void addAbilityCatSelectionListener(ChangeListener listener);

	/**
	 * Deregister a listener that should no longer be advised of potential changes
	 * in the number of selections for an ability category.
	 * @param listener The class to no longer be advised of a change.
	 */
    void removeAbilityCatSelectionListener(ChangeListener listener);

	/**
	 * @return true if the character has been changed and needs to be saved.
	 */
    boolean isDirty();

	/**
	 * @return The kits that have been applied to the character
	 */
    DefaultListFacade<Kit> getKits();

	/**
	 * Add a kit to the character. This will test the kit is valid and warn the
	 * user if there are potential errors before applying the kit.
	 * @param object The kit to be added
	 */
    void addKit(Kit object);

	/**
	 * @return The list of kits currently available to the character.
	 */
    List<Kit> getAvailableKits();

	/**
	 * Record the default output sheet for this character.
	 * @param pdf Is this the PDF sheet?
	 * @param outputSheet The new default.
	 */
    void setDefaultOutputSheet(boolean pdf, File outputSheet);

	/**
	 * Return the default output sheet for this character.
	 * @param pdf Is this the PDF sheet?
	 * @return The default output sheet.
	 */
    String getDefaultOutputSheet(boolean pdf);

	CompanionSupportFacade getCompanionSupport();

	/**
	 * @return a character stub representing this character's master
	 */
    CharacterStubFacade getMaster();

	/**
	 * @return the type of companion the current character is, or null if not a companion
	 */
	@Override
    String getCompanionType();

	/**
	 * @return the variable processor for the current character
	 */
    VariableProcessor getVariableProcessor();

	/**
	 * @return calculate a variable for the current character
	 */
    Float getVariable(String variableString, boolean isMax);

	/**
	 * Advise the character facade that it is being closed.
	 */
    void closeCharacter();

	/**
	 * Identify if this character facade is a facade for the supplied character.
	 * @param pc The character to check for.
	 * @return True if this is a facade for the supplied character, false otherwise.
	 */
    boolean matchesCharacter(PlayerCharacter pc);

	/**
	 * Modify the number of charges of the items of equipment.
	 * @param targets The equipment to be updated.
	 */
    void modifyCharges(List<EquipmentFacade> targets);

	/**
	 * Delete the custom equipment item, ignored if the equip is not custom.
	 * @param equip The equipment item to be deleted.
	 */
    void deleteCustomEquipment(EquipmentFacade equip);

	/**
	 * Modify the user-defined notes for the items of equipment.
	 * @param targets The equipment to be updated.
	 */
    void addNote(List<EquipmentFacade> targets);

	List<CoreViewNodeFacade> getCoreViewTree(CorePerspective pers);

	CharID getCharID();

	boolean isQualifiedFor(PCTemplate element);

	boolean isQualifiedFor(Kit element);

	/**
	 * Return true if the feature with the given name is enabled for this PC; false
	 * otherwise.
	 */
    boolean isFeatureEnabled(String feature);

	String getPreviewSheetVar(String key);

	void addPreviewSheetVar(String key, String value);
}
