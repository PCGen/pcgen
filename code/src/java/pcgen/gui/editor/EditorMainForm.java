/*
 * EditorMainForm.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 6, 2002, 9:24 AM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.Movement;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.Vision;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.PCClassKeyChange;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * <code>EditorMainForm</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class EditorMainForm extends JDialog {
	static final long serialVersionUID = 608648521263089459L;

	//
	// tags from PObject:
	// ADD, AUTO
	// BONUS
	// CCSKILL, CSKILL
	// CHOOSE
	// DEFINE, DESC, DESCISPI, DR
	// KEY, KIT
	// LANGAUTO
	// NAME, NAMEISPI
	// OUTPUTNAME
	// PRExxx
	// REGION, RESTRICT
	// SA, SPELL, SR
	// TYPE
	// UDAM, UMULT
	// VISION
	// WEAPONAUTO
	//
	// tags from Class:
	// ABB
	// ADDDOMAINS
	// ATTACKCYCLE
	// BAB
	// CAST
	// CASTAS
	// DEF
	// DEITY
	// DOMAIN
	// EXCHANGELEVEL
	// EXCLASS
	// FEAT
	// FEATAUTO
	// HASSUBCLASS
	// HD
	// ITEMCREATE
	// KNOWN
	// KNOWNSPELLS
	// KNOWNSPELLSFROMSPECIALTY
	// LANGBONUS
	// LEVELSPERFEAT
	// MAXLEVEL
	// MEMORIZE
	// MODTOSKILLS
	// MULTIPREREQS
	// PROHIBITED
	// QUALIFY
	// SKILLLIST
	// SPECIALS
	// SPECIALTYKNOWN
	// SPELLBOOK
	// SPELLLIST
	// SPELLSTAT
	// SPELLTYPE
	// STARTSKILLPTS
	// SUBCLASS
	// TEMPLATE
	// UATT
	// VFEAT
	// VISIBLE
	// WEAPONBONUS
	// XTRAFEATS
	// MONSKILL
	// PRERACETYPE
	//
	// tags from Deity:
	// ALIGN
	// DEITYWEAP
	// DOMAINS
	// FOLLOWERALIGN
	// PANTHEON
	// QUALIFY
	// RACE
	// SYMBOL
	//
	// tags from Domain:
	// FEAT
	// QUALIFY
	//
	// tags from Feat:
	// ADD
	// COST
	// MULT
	// QUALIFY
	// STACK
	// VISIBLE
	//
	// tags from Language:
	// --none--
	//
	// tags from Race:
	// AC
	// BAB
	// CHOOSE:LANGAUTO
	// CR
	// FACE
	// FAVCLASS
	// FEAT
	// HANDS
	// HITDIE
	// HITDICEADVANCEMENT
	// INIT
	// LANGBONUS
	// LANGNUM
	// LEGS
	// LEVELADJUSTMENT
	// MONSTERCLASS
	// MOVE
	// NATURALATTACKS
	// PROF
	// QUALIFY
	// RACENAME
	// REACH
	// SIZE
	// SKILL
	// SKILLMULT
	// STARTFEATS
	// TEMPLATE
	// VFEAT
	// WEAPONBONUS
	// XTRASKILLPTSPERLVL
	//
	// tags from Skill:
	// ACHECK
	// CLASSES
	// EXCLUSIVE
	// KEYSTAT
	// QUALIFY
	// REQ
	// ROOT
	// SYNERGY
	// USEUNTRAINED
	//
	// tags from Spell:
	// CASTTIME
	// CLASSES
	// COMPS
	// COST
	// DOMAINS
	// EFFECTS (deprecated use DESC)
	// EFFECTTYPE (deprecated use TARGETAREA)
	// CT
	// DESCRIPTOR
	// DURATION
	// ITEM
	// LVLRANGE (Wheel of Time)
	// QUALIFY
	// RANGE
	// SAVEINFO
	// SCHOOL
	// SPELLLEVEL (deprecated use CLASSES or DOMAINS)
	// SPELLRES
	// SUBSCHOOL
	// TARGETAREA
	// STAT
	// VARIANTS
	// XPCOST
	//
	//
	private AgePanel pnlAge;
	private AppearancePanel pnlAppearance;
	private AvailableSelectedPanel pnlBonusLang;

	private AvailableSelectedPanel pnlClasses;
	private AvailableSelectedPanel pnlDomains;
	private AvailableSelectedPanel pnlFeats;
	private AvailableSelectedPanel pnlLanguages;
	private AvailableSelectedPanel pnlRaces;
	private AvailableSelectedPanel pnlSkills;
	private AvailableSelectedPanel pnlTemplates;
	private AvailableSelectedPanel pnlVFeats;
	private AvailableSelectedPanel pnlWeapons;
	private ClassAbilityPanel pnlClassAbility;
	private ClassLevelPanel pnlClassLevel;
	private EditorBasePanel pnlMainTab;
	private JButton btnCancel;
	private JButton btnSave;
	private AdvancedPanel pnlAdvanced;
	private JPanel pnlBase2;
	private JPanel pnlButtons;
	private JPanel pnlMainDialog;
	private JPanel pnlTabs;
	private JTabbedPane jTabbedPane1;
	private LevelAbilitiesPanel pnlLevelAbilities;
	private MovementPanel pnlMovement;
	private NaturalAttacksPanel pnlNaturalAttacks;
	private PObject thisPObject = null;
	private AvailableSelectedPanel pnlFollowers;
	private QualifiedAvailableSelectedPanel pnlQClasses;
	private QualifiedAvailableSelectedPanel pnlQDomains;
	private QualifiedAvailableSelectedPanel pnlQSpells;
	private QualifiedAvailableSelectedPanel pnlSynergy;
	private SourceFilesPanel pnlFileTypes;
	private VisionPanel pnlVision;
	private boolean wasCancelled = true;
	private int editType = EditorConstants.EDIT_NONE;

	/** Creates new form EditorMainForm
	 * @param parent
	 * @param argPObject
	 * @param argEditType
	 * @throws Exception*/
	public EditorMainForm(JFrame parent, PObject argPObject, int argEditType) throws Exception {
		super(parent);

		if (argPObject == null)
		{
			throw new NullPointerException();
		}

		setModal(true);
		editType = argEditType;
		thisPObject = argPObject;
		initComponents();
		initComponentContents();
		setLocationRelativeTo(parent); // centre on parent
	}

	/**
	 * return true if it was cancelled
	 * @return true if it was cancelled
	 */
	public boolean wasCancelled()
	{
		return wasCancelled;
	}

	private static void addVariables(List<String> availableList, Collection<? extends CDOMObject> objList)
	{
		for (CDOMObject obj : objList)
		{
			for (VariableKey vk : obj.getVariableKeys())
			{
				String vname = vk.toString();
				if (!availableList.contains(vname))
				{
					availableList.add(vname);
				}
			}
		}
	}

	/** Closes the dialog */
	private void closeDialog()
	{
		setVisible(false);
		dispose();
	}

	///////////////////////
	// Spells--Classes and Domains tabs
	//
	private static String decodeDomainEntry(String entry)
	{
		final int idx = entry.indexOf('=');

		if (idx >= 0)
		{
			entry = entry.substring(0, idx);
		}

		return entry;
	}

	///////////////////////
	// Synergy tab
	//

	/*    private void buildSynergyTab()
	   {
		   pnlSynergy.setExtraLayout(new GridBagLayout());
		   lblQualifier.setText(LanguageBundle.getString("in_demSkillRank"));
		   lblQualifier.setLabelFor(cmbQualifier);
		   GridBagConstraints gbc = new GridBagConstraints();
		   gbc.gridx = 0;
		   gbc.gridy = 0;
		   gbc.fill = GridBagConstraints.HORIZONTAL;
		   gbc.insets = new Insets(2, 5, 2, 5);
		   gbc.anchor = GridBagConstraints.WEST;
		   pnlSynergy.addExtra(lblQualifier, gbc);
		   gbc = new GridBagConstraints();
		   gbc.gridx = 1;
		   gbc.gridy = 0;
		   gbc.fill = GridBagConstraints.HORIZONTAL;
		   gbc.insets = new Insets(2, 5, 2, 5);
		   gbc.anchor = GridBagConstraints.EAST;
	   //        gbc.weightx = 1.0;
			   pnlSynergy.addExtra(cmbQualifier, gbc);
			   lblVariable.setText(LanguageBundle.getString("in_demSynergyBonus"));
			   lblVariable.setLabelFor(cmbVariable);
			   gbc = new GridBagConstraints();
			   gbc.gridx = 0;
			   gbc.gridy = 1;
			   gbc.fill = GridBagConstraints.HORIZONTAL;
			   gbc.insets = new Insets(2, 5, 2, 5);
			   gbc.anchor = GridBagConstraints.WEST;
			   pnlSynergy.addExtra(lblVariable, gbc);
			   gbc = new GridBagConstraints();
			   gbc.gridx = 1;
			   gbc.gridy = 1;
			   gbc.fill = GridBagConstraints.HORIZONTAL;
			   gbc.insets = new Insets(2, 5, 2, 5);
			   gbc.anchor = GridBagConstraints.EAST;
	   //        gbc.weightx = 1.0;
			   pnlSynergy.addExtra(cmbVariable, gbc);
			   pnlSynergy.setAddFilter(new EditorAddFilter()
			   {
				   public Object encode(Object anObj)
				   {
					   return encodeSynergyEntry((String) anObj);
				   }
				   public Object decode(Object anObj)
				   {
					   return decodeSynergyEntry((String) anObj);
				   }
			   });
		   }
	 */
	private String decodeSynergyEntry(String entry)
	{
		int idx = -1;

		if (entry.indexOf('=') >= 0)
		{
			for (int j = 0; j < pnlSynergy.getQualifierItemCount(); ++j)
			{
				final String qualifier = "=" + (String) pnlSynergy.getQualifierItemAt(j) + "=";
				idx = entry.indexOf(qualifier);

				if (idx >= 0)
				{
					break;
				}
			}
		}

		if (idx >= 0)
		{
			entry = entry.substring(0, idx);
		}

		return entry;
	}

	private String encodeDomainEntry(QualifiedAvailableSelectedPanel pnl, String newEntry)
	{
		String qualifier = null;

		if (pnl.getQualifierSelectedIndex() >= 0)
		{
			qualifier = pnl.getQualifierSelectedItem().toString();
		}

		return encodeDomainEntry(newEntry, qualifier);
	}

	private static String encodeDomainEntry(String newEntry, final String qualifier)
	{
		if (qualifier != null)
		{
			newEntry = newEntry + "=" + qualifier;
		}

		return newEntry;
	}

	private String encodeSpellEntry(QualifiedAvailableSelectedPanel pnl, String newEntry)
	{
		String qualifier = null;

		if (pnl.getQualifierSelectedIndex() >= 0)
		{
			qualifier = pnl.getQualifierSelectedItem().toString();
		}

		return encodeSpellEntry(newEntry, qualifier);
	}

	private static String encodeSpellEntry(String newEntry, final String qualifier)
	{
		if (qualifier != null)
		{
			newEntry = "Level " + qualifier + "= " + newEntry;
		}

		return newEntry;
	}

	private static Spell decodeSpellEntry(String entry)
	{
		final int idx = entry.indexOf('=');

		if (idx >= 0)
		{
			entry = entry.substring(idx + 2);
		}

		return Globals.getSpellKeyed(entry);
	}

	private String encodeSynergyEntry(String newEntry)
	{
		String condition = null;
		String qualifier = null;

		if (pnlSynergy.getQualifierSelectedIndex() >= 0)
		{
			qualifier = pnlSynergy.getQualifierSelectedItem().toString();

			if (pnlSynergy.getVariableSelectedIndex() >= 0)
			{
				condition = pnlSynergy.getVariableSelectedItem().toString();
			}
		}

		return encodeSynergyEntry(newEntry, qualifier, condition);
	}

	private static String encodeSynergyEntry(String newEntry, String qualifier, String condition)
	{
		if ((qualifier != null) && (condition != null))
		{
			newEntry = newEntry + "=" + qualifier + "=" + condition;
		}

		return newEntry;
	}

	private void btnCancelActionPerformed()
	{
		wasCancelled = true;
		closeDialog();
	}

	//////////////////////////////////////////
	private void btnSaveActionPerformed()
	{
		String aString = pnlMainTab.getNameText();

		if (aString.length() == 0)
		{
			ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_demMes1"), Constants.APPLICATION_NAME,
					MessageType.ERROR);

			return;
		}

		String originalKey = thisPObject.getKeyName();
		thisPObject.setName(aString);

		try
		{
			copyDataToObject(originalKey);
		} catch (Exception e)
		{
			Logging.errorPrint("Failed to save custom object due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_demSaveFailed"),
					Constants.APPLICATION_NAME, MessageType.ERROR);
			return;
		}

		// TODO: Remove fully qualified package name, once our Type  
		// enum no longer generates a compile error in OpenJDK 1.7
		thisPObject.addToListFor(ListKey.TYPE, pcgen.cdom.enumeration.Type.CUSTOM);

		wasCancelled = false;
		closeDialog();
	}

	/**
	 * @param originalKey 
	 * 
	 */
	private void copyDataToObject(String originalKey)
	{
		String aString;
		Object[] sel;
		//
		// Save source info
		//

		// TODO - Not sure what the desired behaviour is here.
		// I am going to assume that this shouldn't touch the mod entry but I 
		// am not sure that is correct.
		//		thisPObject.setModSourceMap(new HashMap());

		aString = pnlMainTab.getSourceText();

		if (aString.length() != 0)
		{
			thisPObject.put(StringKey.SOURCE_PAGE, aString);
		}
		LoadContext context = Globals.getContext();

		//
		// Save P.I. flag
		//
		thisPObject.put(ObjectKey.NAME_PI, pnlMainTab.getProductIdentity());
		thisPObject.removeListFor(ListKey.TYPE);
		context.commit();

		pnlMainTab.updateData(thisPObject);

		thisPObject.removeListFor(ListKey.BONUS);
		thisPObject.removeAllVariables();
		thisPObject.removeListFor(ListKey.DAMAGE_REDUCTION);
		thisPObject.clearPrerequisiteList();
		thisPObject.removeListFor(ListKey.SAB);
		thisPObject.remove(ObjectKey.SR);
		thisPObject.removeAllFromList(Spell.SPELLS);
		thisPObject.removeListFor(ListKey.EQUIPMENT);
		thisPObject.removeListFor(ListKey.WEAPONPROF);
		thisPObject.remove(ObjectKey.HAS_DEITY_WEAPONPROF);
		thisPObject.removeListFor(ListKey.AUTO_SHIELDPROF);
		thisPObject.removeListFor(ListKey.AUTO_ARMORPROF);

		switch (editType)
		{
		case EditorConstants.EDIT_DEITY:

			//
			// Save granted domains
			//
			CDOMReference<DomainList> list = Deity.DOMAINLIST;
			Deity deity = (Deity) thisPObject;
			deity.removeAllFromList(list);
			if (pnlDomains.getSelectedList().length > 0)
			{
				List<Prerequisite> followerPrereqs = buildFollowerPrereqs(pnlFollowers.getSelectedList());
				sel = pnlDomains.getSelectedList();
				for (Object object : sel)
				{
					Domain d = (Domain) object;
					CDOMDirectSingleRef<Domain> ref = CDOMDirectSingleRef.getRef(d);
					SimpleAssociatedObject sao = new SimpleAssociatedObject();
					sao.addAllPrerequisites(followerPrereqs);
					sao.setAssociation(AssociationKey.TOKEN, "DOMAINS");
					deity.putToList(list, ref, sao);
				}
			}

			//
			// Save racial worshippers
			//
			thisPObject.removeListFor(ListKey.RACEPANTHEON);
			sel = pnlRaces.getSelectedList();
			for (int i = 0; i < sel.length; i++)
			{
				thisPObject.addToListFor(ListKey.RACEPANTHEON, ((Race) sel[i]).getKeyName());
			}

			break;

		case EditorConstants.EDIT_DOMAIN:

			// Clear old data 
			thisPObject.removeAllFromList(Ability.FEATLIST);
			EditorMainForm.clearSpellListInfo(thisPObject);
			context.commit();

			// Save feats
			sel = pnlFeats.getSelectedList();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, '|');
				context.unconditionallyProcess(thisPObject, "FEAT", aString);
			}

			sel = pnlQSpells.getSelectedList();

			context.ref.constructNowIfNecessary(DomainSpellList.class, thisPObject.getKeyName());
			for (int i = 0; i < sel.length; ++i)
			{
				aString = sel[i].toString();
				final int idx = aString.indexOf('=');
				if (idx > 0)
				{
					final String domainKey = thisPObject.getKeyName();
					String spellName = aString.substring(idx + 2);
					String spellLevel = aString.substring(idx - 1, idx);
					context.unconditionallyProcess(thisPObject, "SPELLLEVEL", "DOMAIN|" + domainKey + "=" + spellLevel
							+ "|" + spellName);
				}
			}

			break;

		case EditorConstants.EDIT_FEAT:
			Ability thisAbility = (Ability) thisPObject;
			thisAbility.setCDOMCategory(AbilityCategory.FEAT);
			break;

		case EditorConstants.EDIT_LANGUAGE:
			break;

		case EditorConstants.EDIT_RACE:

			Race thisRace = (Race) thisPObject;

			thisRace.removeListFor(ListKey.MOVEMENT);
			Movement cm = Movement.getMovementFrom(pnlMovement.getMoveValues());
			cm.setMovementTypes(pnlMovement.getMoveTypes());
			thisRace.addToListFor(ListKey.MOVEMENT, cm);

			thisRace.removeAllFromList(Vision.VISIONLIST);
			List<Vision> visionList = pnlVision.getVision();
			for (Vision vis : visionList)
			{
				thisRace.putToList(Vision.VISIONLIST, new CDOMDirectSingleRef<Vision>(vis),
						new SimpleAssociatedObject());
			}

			thisRace.removeListFor(ListKey.NATURAL_WEAPON);
			thisRace.addAllToListFor(ListKey.NATURAL_WEAPON, pnlNaturalAttacks.getNaturalWeapons());
			pnlAppearance.updateData(thisRace);
			pnlAge.updateData(thisRace);

			//
			// Save granted templates
			//
			thisRace.removeListFor(ListKey.TEMPLATE);
			thisRace.removeListFor(ListKey.TEMPLATE_CHOOSE);
			thisRace.removeListFor(ListKey.TEMPLATE_ADDCHOICE);
			sel = pnlTemplates.getSelectedList();
			aString = EditUtil.delimitArray(sel, '|');
			if ((aString != null) && (aString.length() > 0))
			{
				context.unconditionallyProcess(thisRace, "TEMPLATE", aString);
			}

			//
			// Save choice of templates
			//
			sel = pnlTemplates.getSelectedList2();
			aString = EditUtil.delimitArray(sel, '|');

			if ((aString != null) && (aString.length() > 0))
			{
				context.unconditionallyProcess(thisRace, "TEMPLATE", "CHOOSE:" + aString);
			}

			//
			// Save favoured classes
			//
			sel = pnlClasses.getSelectedList();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, '|');
				context.unconditionallyProcess(thisRace, "FAVCLASS", aString);
			}

			//
			// Save choice of auto languages
			//
			sel = pnlLanguages.getSelectedList2();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, '|');
				context.unconditionallyProcess(thisRace, "CHOOSE", "LANGAUTO|" + aString);
			}

			//
			// Save feats
			//
			sel = pnlFeats.getSelectedList();
			for (Object o : sel)
			{
				context.unconditionallyProcess(thisRace, "FEAT", o.toString());
			}

			//
			// Save bonus languages
			//
			thisRace.removeAllFromList(Language.STARTING_LIST);
			sel = pnlBonusLang.getSelectedList();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, ',');
				context.unconditionallyProcess(thisRace, "LANGBONUS", aString);
			}

			break;

		case EditorConstants.EDIT_SKILL:

			sel = pnlSynergy.getSelectedList();

			// BONUS:SKILL|Ride|2|PRESKILL:1,Handle Animal=5|TYPE=Synergy.STACK
			StringTokenizer aTok;

			for (int i = 0; i < sel.length; ++i)
			{
				//((Skill) thisPObject).addSynergyList(sel[i].toString());
				aTok = new StringTokenizer(sel[i].toString(), "=");

				if (aTok.countTokens() == 3)
				{
					final String skillName = aTok.nextToken();
					final String skillRank = aTok.nextToken();
					StringBuffer sb = new StringBuffer(50);
					sb.append("SKILL|").append(thisPObject.getKeyName());
					sb.append('|').append(aTok.nextToken());
					sb.append("|PRESKILL:1,").append(skillName).append('=').append(skillRank);
					sb.append("|TYPE=Synergy.STACK");
					final BonusObj aBonus = Bonus.newBonus(context, sb.toString());

					if (aBonus != null)
					{
						thisPObject.addToListFor(ListKey.BONUS, aBonus);
					}
				} else
				{
					Logging.errorPrint("Synergy has invalid format: " + sel[i].toString());
				}
			}

			Globals.getContext().getListContext().clearAllMasterLists("CLASSES", thisPObject);
			context.commit();
			sel = pnlClasses.getSelectedList2();
			if (sel.length > 0)
			{
				context.unconditionallyProcess(thisPObject, "CLASSES",
						"ALL|!" + StringUtil.join(Arrays.asList(sel), "|!"));
			}

			for (Object o : pnlClasses.getSelectedList())
			{
				context.unconditionallyProcess(thisPObject, "CLASSES", o.toString());
			}

			break;

		case EditorConstants.EDIT_SPELL:
			Spell sp = (Spell) thisPObject;
			((SpellBasePanel2) pnlBase2).updateData(sp);
			context.getListContext().clearAllMasterLists("CLASSES", sp);
			context.getListContext().clearAllMasterLists("DOMAINS", sp);
			context.commit();
			for (Object o : pnlQClasses.getSelectedList())
			{
				context.unconditionallyProcess(sp, "CLASSES", o.toString());
			}
			for (Object o : pnlQDomains.getSelectedList())
			{
				context.unconditionallyProcess(sp, "DOMAINS", o.toString());
			}

			break;

		case EditorConstants.EDIT_TEMPLATE:

			PCTemplate thisPCTemplate = (PCTemplate) thisPObject;
			thisPCTemplate.removeListFor(ListKey.MOVEMENT);
			if (pnlMovement.getMoveValues().length() > 0)
			{
				Movement cmv = Movement.getMovementFrom(pnlMovement.getMoveValues());
				cmv.setMoveRatesFlag(pnlMovement.getMoveRateType());
				thisPCTemplate.addToListFor(ListKey.MOVEMENT, cmv);
			}

			thisPCTemplate.removeAllFromList(Vision.VISIONLIST);
			List<Vision> tplVisionList = pnlVision.getVision();
			for (Vision vis : tplVisionList)
			{
				thisPCTemplate.putToList(Vision.VISIONLIST, new CDOMDirectSingleRef<Vision>(vis),
						new SimpleAssociatedObject());
			}

			//
			// Save granted templates
			//
			thisPCTemplate.removeListFor(ListKey.TEMPLATE);
			thisPCTemplate.removeListFor(ListKey.TEMPLATE_CHOOSE);
			thisPCTemplate.removeListFor(ListKey.TEMPLATE_ADDCHOICE);
			sel = pnlTemplates.getSelectedList();
			aString = EditUtil.delimitArray(sel, '|');
			if ((aString != null) && (aString.length() > 0))
			{
				context.unconditionallyProcess(thisPCTemplate, "TEMPLATE", aString);
			}

			//
			// Save choice of templates
			//
			sel = pnlTemplates.getSelectedList2();
			aString = EditUtil.delimitArray(sel, '|');

			if ((aString != null) && (aString.length() > 0))
			{
				context.unconditionallyProcess(thisPCTemplate, "TEMPLATE", "CHOOSE:" + aString);
			}

			//
			// Save favoured classes
			//
			sel = pnlClasses.getSelectedList();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, '|');
				context.unconditionallyProcess(thisPCTemplate, "FAVOREDCLASS", aString);
			}

			//
			// Save choice of auto languages
			//
			sel = pnlLanguages.getSelectedList2();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, '|');
				context.unconditionallyProcess(thisPCTemplate, "CHOOSE", "LANGAUTO|" + aString);
			}

			//
			// Save feats
			//
			thisPCTemplate.removeListFor(ListKey.FEAT_TOKEN_LIST);
			sel = pnlFeats.getSelectedList();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, '|');
				context.unconditionallyProcess(thisPCTemplate, "FEAT", aString);
			}

			//
			// Save bonus languages
			//
			thisPCTemplate.removeAllFromList(Language.STARTING_LIST);
			sel = pnlBonusLang.getSelectedList();
			if (sel.length > 0)
			{
				aString = EditUtil.delimitArray(sel, ',');
				context.unconditionallyProcess(thisPCTemplate, "LANGBONUS", aString);
			}

			//
			// Save level and hit dice abilities
			//
			thisPCTemplate.removeListFor(ListKey.LEVEL_TEMPLATES);
			thisPCTemplate.removeListFor(ListKey.HD_TEMPLATES);
			sel = pnlLevelAbilities.getSelectedList();

			for (int index = 0; index < sel.length; index++)
			{
				aString = (String) sel[index];

				if (aString.startsWith("HD:"))
				{
					context.unconditionallyProcess(thisPCTemplate, "HD", aString.substring(3));
				} else if (aString.startsWith("LEVEL:"))
				{
					context.unconditionallyProcess(thisPCTemplate, "LEVEL", aString.substring(6));
				}
			}

			context.performDeferredProcessing(thisPCTemplate);
			break;

		case EditorConstants.EDIT_CAMPAIGN:
			pnlFileTypes.updateData((Campaign) thisPObject);

			break;

		case EditorConstants.EDIT_CLASS:
			PCClass thisPCClass = (PCClass) thisPObject;
			thisPCClass.removeListFor(ListKey.TEMPLATE);
			thisPCClass.removeListFor(ListKey.TEMPLATE_CHOOSE);
			thisPCClass.removeListFor(ListKey.TEMPLATE_ADDCHOICE);
			thisPCClass.remove(IntegerKey.UMULT);
			thisPCClass.removeListFor(ListKey.UNARMED_DAMAGE);
			for (PCClassLevel pcl : thisPCClass.getOriginalClassLevelCollection())
			{
				pcl.remove(IntegerKey.UMULT);
				pcl.removeListFor(ListKey.UNARMED_DAMAGE);
			}
			break;

		default:
			break;
		}

		//
		// Save granted languages
		//
		if (pnlLanguages != null)
		{
			thisPObject.removeListFor(ListKey.AUTO_LANGUAGES);
			sel = pnlLanguages.getSelectedList();
			for (int i = 0; i < sel.length; i++)
			{
				final Language lang = Globals.getContext().ref.silentlyGetConstructedCDOMObject(Language.class,
						sel[i].toString());

				if (lang != null)
				{
					thisPObject.addToListFor(ListKey.AUTO_LANGUAGES, new QualifiedObject(
							new CDOMDirectSingleRef<Language>(lang)));
				}
			}
		}

		//
		// Save auto weapon proficiencies
		//
		if (pnlWeapons != null)
		{
			thisPObject.removeListFor(ListKey.WEAPONPROF);
			sel = pnlWeapons.getSelectedList();
			StringBuffer selList = new StringBuffer();
			for (int i = 0; i < sel.length; i++)
			{
				if (i > 0)
				{
					selList.append('|');
				}
				selList.append((String) sel[i]);
			}
			if (selList.length() > 0)
			{
				context.unconditionallyProcess(thisPObject, "AUTO", "WEAPONPROF|" + selList.toString());
			}

			sel = pnlWeapons.getSelectedList2();

			if (sel != null)
			{
				if (editType == EditorConstants.EDIT_CLASS || editType == EditorConstants.EDIT_RACE)
				{
					thisPObject.removeAllFromList(WeaponProf.STARTING_LIST);
					if (sel.length > 0)
					{
						context.unconditionallyProcess(thisPObject, "WEAPONBONUS", EditUtil.delimitArray(sel, ','));
					}
				}
			}
		}

		// TODO: check if all skills of one type are selected...maybe change to TYPE.blah?
		if (pnlSkills != null)
		{
			//
			// Save granted class skills
			//
			thisPObject.removeListFor(ListKey.CSKILL);
			sel = pnlSkills.getSelectedList();
			for (int i = 0; i < sel.length; i++)
			{
				Skill sk = context.ref.silentlyGetConstructedCDOMObject(Skill.class, sel[i].toString());
				if (sk != null)
				{
					thisPObject.addToListFor(ListKey.CSKILL, CDOMDirectSingleRef.getRef(sk));
				}
			}

			//
			// Save granted cross class skills
			//
			thisPObject.removeListFor(ListKey.CCSKILL);
			sel = pnlSkills.getSelectedList2();
			for (int i = 0; i < sel.length; i++)
			{
				Skill sk = context.ref.silentlyGetConstructedCDOMObject(Skill.class, sel[i].toString());
				if (sk != null)
				{
					thisPObject.addToListFor(ListKey.CCSKILL, CDOMDirectSingleRef.getRef(sk));
				}
			}
		}

		//
		// Save advanced tab info
		//
		//
		// Make sure the lists are all empty to start
		//
		switch (editType)
		{
		case EditorConstants.EDIT_CLASS:
			((PCClass) thisPObject).removeListFor(ListKey.ADD);
			break;

		case EditorConstants.EDIT_DEITY:
			((Deity) thisPObject).removeListFor(ListKey.PANTHEON);
			break;

		case EditorConstants.EDIT_DOMAIN:
			break;

		case EditorConstants.EDIT_FEAT:
			break;

		case EditorConstants.EDIT_LANGUAGE:
			break;

		case EditorConstants.EDIT_RACE:
			break;

		case EditorConstants.EDIT_SKILL:
			((Skill) thisPObject).put(StringKey.CHOICE_STRING, null);

			break;

		case EditorConstants.EDIT_SPELL:
			break;

		case EditorConstants.EDIT_TEMPLATE:
			break;

		default:
			break;
		}
		context.getListContext().removeAllFromList("VISION", thisPObject, Vision.VISIONLIST);
		context.commit();

		if (editType == EditorConstants.EDIT_SKILL)
		{
			((Skill) thisPObject).put(StringKey.CHOICE_STRING, null);
		} else if ((editType == EditorConstants.EDIT_DOMAIN) || (editType == EditorConstants.EDIT_FEAT))
		{
			thisPObject.put(StringKey.CHOICE_STRING, "");
		}

		if (editType != EditorConstants.EDIT_DOMAIN)
		{
			EditorMainForm.clearSpellListInfo(thisPObject);
		}

		sel = pnlAdvanced.getSelectedList();

		if (editType == EditorConstants.EDIT_CLASS)
		{
			pnlClassAbility.updateData((PCClass) thisPObject);
			pnlClassLevel.updateData((PCClass) thisPObject);
		}

		for (int i = 0; i < sel.length; ++i)
		{
			aString = (String) sel[i];
			try
			{
				final String token = aString.trim();
				final int colonLoc = token.indexOf(':');
				if (colonLoc == -1)
				{
					Logging.errorPrint("Invalid Token - does not contain a colon: " + token);
				} else if (colonLoc == 0)
				{
					Logging.errorPrint("Invalid Token - starts with a colon: " + token);
				} else
				{
					String key = token.substring(0, colonLoc);
					String value = (colonLoc == token.length() - 1) ? null : token.substring(colonLoc + 1);
					if (context.processToken(thisPObject, key, value))
					{
						context.commit();
					} else
					{
						context.rollback();
						Logging.replayParsedMessages();
					}
					Logging.clearParseMessages();
				}
			} catch (PersistenceLayerException ple)
			{
				Logging.errorPrint(ple.getMessage() + " while parsing " + aString, ple);
			}
		}

		if (editType == EditorConstants.EDIT_CLASS)
		{
			PCClassKeyChange.changeReferences(originalKey, (PCClass) thisPObject);
		}
	}

	/**
	 * Builds the follower prereqs based on the list of selected alignments.
	 * 
	 * @param selectedList the selected alignments
	 * 
	 * @return the list of Prerequisites
	 */
	private List<Prerequisite> buildFollowerPrereqs(Object[] selectedList)
	{
		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
		StringBuffer tbuf = new StringBuffer(100);
		for (Object selItem : selectedList)
		{
			String alignName = (String) selItem;
			boolean found = false;

			for (PCAlignment align : Globals.getContext().ref.getOrderSortedCDOMObjects(PCAlignment.class))
			{
				if (alignName.equals(align.getDisplayName()))
				{
					if (tbuf.length() > 0)
					{
						tbuf.append(',');
					}
					tbuf.append(align.getAbb());
					found = true;
					break;
				}
			}

			if (!found)
			{
				Logging.errorPrint("Alignment " + alignName + " could not be found. Ignoring.");
			}
		}

		if (tbuf.length() > 0)
		{
			try
			{
				final PrerequisiteParserInterface parser = PreParserFactory.getInstance().getParser("ALIGN");
				Prerequisite prereq = parser.parse("align", tbuf.toString(), false, false);
				prereqs.add(prereq);
			} catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Unable to create PREALIGN for " + tbuf.toString() + ". Ignoring.", e);
			}
		}

		return prereqs;
	}

	//TODO: I'm in the process of breaking this method up (and removing a lot of duplicated code.) 1122 lines is just TOO LONG. Heck, it's long for a class, never mind a method... JK070110
	private void initComponentContents()
	{
		String aString;
		StringTokenizer aTok;
		//		List selectedList = new ArrayList();
		//		List selectedList2 = new ArrayList();
		List<String> aList;
		List<String> movementValues;
		List<String> visionValues;
		List<Vision> vision;
		List<Equipment> naturalAttacks;

		pnlMainTab.setNameText(thisPObject.getKeyName());
		pnlMainTab.setProductIdentity(thisPObject.getSafe(ObjectKey.NAME_PI));
		pnlMainTab.setSourceText(thisPObject.get(StringKey.SOURCE_PAGE));

		pnlMainTab.updateView(thisPObject);

		switch (editType)
		{
		case EditorConstants.EDIT_CLASS:

			//pnlMainTab.setDescriptionText(thisPObject.getDescription());	// don't want PI here
			//pnlMainTab.setDescIsPI(thisPObject.getDescIsPI());
			break;

		case EditorConstants.EDIT_DEITY:

			//
			// Initialize the lists of available and selected follower alignments
			//
			List<String> availableFollowerAlignmentList = new ArrayList<String>();
			List<String> selectedFollowerAlignmentList = new ArrayList<String>();

			for (PCAlignment anAlignment : Globals.getContext().ref.getOrderSortedCDOMObjects(PCAlignment.class))
			{
				if (anAlignment.getSafe(ObjectKey.VALID_FOR_FOLLOWER))
				{
					availableFollowerAlignmentList.add(anAlignment.getDisplayName());
				}
			}

			Prerequisite prereq = getFirstPrereqOfKind(thisPObject, "align");
			if (prereq != null)
			{
				parseAlignAbbrev(availableFollowerAlignmentList, selectedFollowerAlignmentList, prereq);
			}

			pnlFollowers.setAvailableList(availableFollowerAlignmentList, true);
			pnlFollowers.setSelectedList(selectedFollowerAlignmentList, true);

			//
			// Initialize the contents of the available and selected domains lists
			//
			List<Domain> selectedDomainList = new ArrayList<Domain>();
			List<Domain> availableDomainList = new ArrayList<Domain>();

			for (Domain aDomain : Globals.getContext().ref.getConstructedCDOMObjects(Domain.class))
			{
				if (((Deity) thisPObject).hasObjectOnList(Deity.DOMAINLIST, aDomain))
				{
					selectedDomainList.add(aDomain);
				} else
				{
					availableDomainList.add(aDomain);
				}
			}

			pnlDomains.setAvailableList(availableDomainList, true);
			pnlDomains.setSelectedList(selectedDomainList, true);

			//
			// Initialize the contents of the available and selected races list
			//
			List<Race> selectedRaceList = new ArrayList<Race>();
			List<Race> availableRaceList = new ArrayList<Race>();

			final List<String> raceList = ((Deity) thisPObject).getSafeListFor(ListKey.RACEPANTHEON);

			for (final Race race : Globals.getContext().ref.getConstructedCDOMObjects(Race.class))
			{
				final String raceName = race.getKeyName();

				if (!raceName.equals(Constants.NONESELECTED))
				{
					if (raceList.contains(raceName))
					{
						selectedRaceList.add(race);
					} else
					{
						availableRaceList.add(race);
					}
				}
			}

			pnlRaces.setAvailableList(availableRaceList, true);
			pnlRaces.setSelectedList(selectedRaceList, true);

			break;

		case EditorConstants.EDIT_DOMAIN:

			//
			// Populate the feats available list and selected lists
			//
			List<String> availableFeatList = new ArrayList<String>();
			List<String> selecetdFeatList = new ArrayList<String>();

			for (Ability anAbility : Globals.getContext().ref.getManufacturer(Ability.class, AbilityCategory.FEAT)
					.getAllObjects())
			{
				availableFeatList.add(anAbility.getKeyName());
			}

			for (CDOMReference<Ability> ref : thisPObject.getSafeListMods(Ability.FEATLIST))
			{
				String lst = ref.getLSTformat(false);
				if (!selecetdFeatList.contains(lst))
				{
					availableFeatList.remove(lst);
					selecetdFeatList.add(lst);
				}
			}

			pnlFeats.setAvailableList(availableFeatList, true);
			pnlFeats.setSelectedList(selecetdFeatList, true);

			//TODO Remember to change here when spellMap is changed JK070101
			List<Spell> availableSpellList = new ArrayList<Spell>();
			List<String> selectedSpellList = new ArrayList<String>();

			availableSpellList = new ArrayList<Spell>(Globals.getSpellMap().values().size());

			for (Iterator<?> e = Globals.getSpellMap().values().iterator(); e.hasNext();)
			{
				final Object obj = e.next();

				if (obj instanceof Spell)
				{
					HashMapToList<CDOMList<Spell>, Integer> hml = SpellLevel.getMasterLevelInfo(null, (Spell) obj);
					List<Integer> levelList = hml.getListFor(((Domain) thisPObject).get(ObjectKey.DOMAIN_SPELLLIST));
					if (levelList == null || levelList.isEmpty())
					{
						availableSpellList.add((Spell) obj);
					} else
					{
						int lvl = levelList.get(0);
						if (lvl == -1)
						{
							availableSpellList.add((Spell) obj);
						} else
						{
							selectedSpellList.add(encodeSpellEntry(obj.toString(), Integer.toString(lvl)));
						}
					}
				}
			}
			// TODO: Fix this hack. Copied domains will show the original spell list when subsequently edited. Close and reload will show the correct one
			if (selectedSpellList.isEmpty())
			{
				addSelectedDomainSpells(availableSpellList, selectedSpellList);
			}
			Globals.sortPObjectList(availableSpellList);

			pnlQSpells.setAvailableList(availableSpellList, true);
			pnlQSpells.setSelectedList(selectedSpellList, true);

			break;

		case EditorConstants.EDIT_FEAT:
			break;

		case EditorConstants.EDIT_LANGUAGE:
			break;

		case EditorConstants.EDIT_RACE:

			//
			// Populate the templates available list and selected lists
			//
			List<String> availableRaceTemplateList = new ArrayList<String>();
			List<String> selectedRaceTemplateList = new ArrayList<String>();
			List<String> selectedRaceTemplateList2 = new ArrayList<String>();

			for (PCTemplate aTemplate : Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class))
			{
				aString = aTemplate.getKeyName();

				if (!availableRaceTemplateList.contains(aString))
				{
					availableRaceTemplateList.add(aString);
				}
			}

			//
			// remove this race's granted templates from the available list and place into selected list
			//
			moveGrantedTemplatesFromAvailableToSelected(thisPObject, selectedRaceTemplateList,
					selectedRaceTemplateList2, availableRaceTemplateList);

			pnlTemplates.setAvailableList(availableRaceTemplateList, true);
			pnlTemplates.setSelectedList(selectedRaceTemplateList, true);
			pnlTemplates.setSelectedList2(selectedRaceTemplateList2, true);

			//
			// Populate the favoured classes available list and selected lists
			//
			List<String> availableFavouredClassList = new ArrayList<String>();
			List<String> selectedFavouredClassList = new ArrayList<String>();

			for (PCClass aClass : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
			{
				if (!(aClass instanceof SubClass) && !(aClass instanceof SubstitutionClass))
				{
					availableFavouredClassList.add(aClass.getKeyName());
				}
				if (aClass.containsListFor(ListKey.SUB_CLASS))
				{
					for (SubClass subClass : aClass.getListFor(ListKey.SUB_CLASS))
					{
						availableFavouredClassList.add(aClass.getKeyName() + "." + subClass.getKeyName());
					}
				}
				if (aClass.containsListFor(ListKey.SUBSTITUTION_CLASS))
				{
					for (SubstitutionClass subClass : aClass.getListFor(ListKey.SUBSTITUTION_CLASS))
					{
						availableFavouredClassList.add(aClass.getKeyName() + "." + subClass.getKeyName());
					}
				}
			}

			availableFavouredClassList.add("Any");
			List<CDOMReference<? extends PCClass>> favClass = thisPObject.getListFor(ListKey.FAVORED_CLASS);
			if (favClass != null)
			{
				for (CDOMReference<? extends PCClass> ref : favClass)
				{
					String cl = ref.getLSTformat(false);
					availableFavouredClassList.remove(cl);
					selectedFavouredClassList.add(cl);
				}
			}

			pnlClasses.setAvailableList(availableFavouredClassList, true);
			pnlClasses.setSelectedList(selectedFavouredClassList, true);

			//
			// Populate the feats available list and selected lists
			//
			List<String> availableRaceFeatList = new ArrayList<String>();
			List<String> selectedRaceFeatList = new ArrayList<String>();

			for (Ability anAbility : Globals.getContext().ref.getManufacturer(Ability.class, AbilityCategory.FEAT)
					.getAllObjects())
			{
				availableRaceFeatList.add(anAbility.getKeyName());
			}

			for (CDOMReference<Ability> ref : thisPObject.getSafeListFor(ListKey.FEAT_TOKEN_LIST))
			{
				String lst = ref.getLSTformat(false);
				if (!selectedRaceFeatList.contains(lst))
				{
					availableRaceFeatList.remove(lst);
					selectedRaceFeatList.add(lst);
				}
			}

			pnlFeats.setAvailableList(availableRaceFeatList, true);
			pnlFeats.setSelectedList(selectedRaceFeatList, true);

			//
			// Populate the virtual feats available list and selected list
			//
			List<String> availableRaceVirtualFeatList = new ArrayList<String>();
			List<String> selectedRaceVirtualFeatList = new ArrayList<String>();

			for (Ability anAbility : Globals.getContext().ref.getManufacturer(Ability.class, AbilityCategory.FEAT)
					.getAllObjects())
			{
				availableRaceVirtualFeatList.add(anAbility.getKeyName());
			}

			Collection<CDOMReference<Ability>> mods = thisPObject.getListMods(AbilityList.getAbilityListReference(
					AbilityCategory.FEAT, Nature.VIRTUAL));
			if (mods != null)
			{
				for (CDOMReference<Ability> ref : mods)
				{
					String featName = ref.getLSTformat(false);
					if (!selectedRaceVirtualFeatList.contains(featName))
					{
						availableRaceVirtualFeatList.remove(featName);
						selectedRaceVirtualFeatList.add(featName);
					}
				}
			}

			pnlVFeats.setAvailableList(availableRaceVirtualFeatList, true);
			pnlVFeats.setSelectedList(selectedRaceVirtualFeatList, true);

			//
			// Populate the bonus languages available list and selected lists
			//
			List<Language> availableRaceLangList = new ArrayList<Language>();
			List<Language> selectedRaceLangList = new ArrayList<Language>();
			Collection<CDOMReference<Language>> langCollection = thisPObject.getListMods(Language.STARTING_LIST);
			if (langCollection != null)
			{
				for (CDOMReference<Language> ref : langCollection)
				{
					selectedRaceLangList.addAll(ref.getContainedObjects());
				}
			}
			availableRaceLangList.addAll(Globals.getContext().ref.getConstructedCDOMObjects(Language.class));
			availableRaceLangList.removeAll(selectedRaceLangList);

			pnlBonusLang.setAvailableList(availableRaceLangList, true);
			pnlBonusLang.setSelectedList(selectedRaceLangList, true);

			//
			// Populate the movement panel
			//
			movementValues = new ArrayList<String>();

			List<Movement> mms = thisPObject.getListFor(ListKey.MOVEMENT);
			if (mms != null && !mms.isEmpty())
			{
				Movement cm = mms.get(0);
				if (cm != null && cm.getNumberOfMovementTypes() > 0)
				{
					for (int index = 0; index < cm.getNumberOfMovementTypes(); index++)
					{
						final String aMove = MovementPanel.makeMoveString(cm.getMovementType(index),
								cm.getMovement(index), null, null);
						movementValues.add(aMove);
					}
				}
			}

			pnlMovement.setSelectedList(movementValues);

			//
			// Populate the vision panel
			//
			vision = new ArrayList<Vision>();
			for (CDOMReference<Vision> ref : thisPObject.getSafeListMods(Vision.VISIONLIST))
			{
				vision.addAll(ref.getContainedObjects());
			}
			visionValues = buildVisionValues(vision);
			pnlVision.setSelectedList(visionValues);

			//
			// Populate the natural attacks panel
			//
			naturalAttacks = thisPObject.getSafeListFor(ListKey.NATURAL_WEAPON);
			pnlNaturalAttacks.setSelectedList(naturalAttacks);

			//
			// Populate the appearance panel
			//
			List<String> eyeColorList = new ArrayList<String>();
			List<String> hairColorList = new ArrayList<String>();
			List<String> skinToneList = new ArrayList<String>();

			for (final Race race : Globals.getContext().ref.getConstructedCDOMObjects(Race.class))
			{
				//					final String raceName = (String) e.next();
				String[] unp = Globals.getContext().unparseSubtoken(race, "REGION");

				if (unp == null)
				{
					aString = Constants.NONE;
				} else
				{
					aString = unp[0];
				}

				aList = Globals.getBioSet().getTagForRace(aString, race.getKeyName(), "HAIR");

				if (aList != null)
				{
					for (Iterator<String> ai = aList.iterator(); ai.hasNext();)
					{
						String as = ai.next();
						StringTokenizer at = new StringTokenizer(as, "|", false);

						while (at.hasMoreTokens())
						{
							String ast = at.nextToken();

							if (!hairColorList.contains(ast))
							{
								hairColorList.add(ast);
							}
						}
					}
				}

				aList = Globals.getBioSet().getTagForRace(aString, race.getKeyName(), "EYES");

				if (aList != null)
				{
					for (Iterator<String> ai = aList.iterator(); ai.hasNext();)
					{
						String as = ai.next();
						StringTokenizer at = new StringTokenizer(as, "|", false);

						while (at.hasMoreTokens())
						{
							String ast = at.nextToken();

							if (!eyeColorList.contains(ast))
							{
								eyeColorList.add(ast);
							}
						}
					}
				}

				aList = Globals.getBioSet().getTagForRace(aString, race.getKeyName(), "SKINTONE");

				if (aList != null)
				{
					for (Iterator ai = aList.iterator(); ai.hasNext();)
					{
						String as = (String) ai.next();
						StringTokenizer at = new StringTokenizer(as, "|", false);

						while (at.hasMoreTokens())
						{
							String ast = at.nextToken();

							if (!skinToneList.contains(ast))
							{
								skinToneList.add(ast);
							}
						}
					}
				}
			}

			pnlAppearance.setEyeColorAvailableList(eyeColorList, true);
			pnlAppearance.setHairColorAvailableList(hairColorList, true);
			pnlAppearance.setSkinToneAvailableList(skinToneList, true);
			pnlAppearance.updateView((Race) thisPObject);

			//
			// Populate the age panel
			//
			pnlAge.updateView((Race) thisPObject);

			break;

		case EditorConstants.EDIT_SKILL:
			List<String> availableSkillList = new ArrayList<String>();
			List<String> selectedSkillList = new ArrayList<String>();
			List<String> selectedSkillList2 = new ArrayList<String>();

			for (Iterator<PCClass> e = Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class).iterator(); e
					.hasNext();)
			{
				final PCClass aClass = e.next();
				availableSkillList.add(aClass.getKeyName());
			}

			Changes<CDOMReference> masterChanges = Globals.getContext().getListContext()
					.getMasterListChanges("CLASSES", thisPObject, ClassSkillList.class);
			Collection<CDOMReference> add = masterChanges.getAdded();
			if (add != null)
			{
				for (CDOMReference<ClassSkillList> ref : add)
				{
					String className = ref.getLSTformat(false);
					if (className.startsWith("ALL|!"))
					{
						for (String prev : className.substring(5).split("\\|\\!"))
						{
							selectedSkillList2.add(prev);
							availableSkillList.remove(prev);
						}
					} else
					{
						selectedSkillList.add(className);
						availableSkillList.remove(className);
					}
				}
			}

			pnlClasses.setAvailableList(availableSkillList, true);
			pnlClasses.setSelectedList(selectedSkillList, true);
			pnlClasses.setSelectedList2(selectedSkillList2, true);
			pnlClasses.setLblSelectedText("Class Skill");
			pnlClasses.setLblSelected2Text("Not allowed");

			break;

		case EditorConstants.EDIT_SPELL:
			((SpellBasePanel2) pnlBase2).updateView((Spell) thisPObject);

			//
			// Initialize the contents of the available and selected domains lists
			//
			int iCount = 0;
			List<String> availableDomainsList = new ArrayList<String>();
			List<String> selectedDomainsList = new ArrayList<String>();

			HashMapToList<CDOMList<Spell>, Integer> hml = SpellLevel.getMasterLevelInfo(null, (Spell) thisPObject);
			for (Domain aDomain : Globals.getContext().ref.getConstructedCDOMObjects(Domain.class))
			{
				List<Integer> levelList = hml.getListFor(aDomain.get(ObjectKey.DOMAIN_SPELLLIST));
				if (levelList == null || levelList.isEmpty())
				{
					availableDomainsList.add(aDomain.getKeyName());
				} else
				{
					int lvl = levelList.get(0);
					if (lvl == -1)
					{
						availableDomainsList.add(aDomain.getKeyName());
					} else
					{
						selectedDomainsList.add(encodeDomainEntry(aDomain.getKeyName(), Integer.toString(lvl)));
						++iCount;
					}
				}
			}

			pnlQDomains.setAvailableList(availableDomainsList, true);
			pnlQDomains.setSelectedList(selectedDomainsList, true);

			//
			// Initialize the contents of the available and selected classes lists
			//
			List<String> availableClassesList = new ArrayList<String>();
			List<String> selectedClassesList = new ArrayList<String>();

			for (Iterator<PCClass> e = Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class).iterator(); e
					.hasNext();)
			{
				final PCClass aClass = e.next();
				List<Integer> levelList = hml.getListFor(aClass.get(ObjectKey.CLASS_SPELLLIST));
				if (levelList == null || levelList.isEmpty())
				{
					availableClassesList.add(aClass.getKeyName());
				} else
				{
					int lvl = levelList.get(0);
					if (lvl == -1)
					{
						availableClassesList.add(aClass.getKeyName());
					} else
					{
						selectedClassesList.add(encodeDomainEntry(aClass.getKeyName(), Integer.toString(lvl)));
						++iCount;
					}
				}
			}

			pnlQClasses.setAvailableList(availableClassesList, true);
			pnlQClasses.setSelectedList(selectedClassesList, true);

			//
			// Inform the user if there is a domain/class defined for the spell that was not found
			//
			HashMapToList<CDOMList<Spell>, Integer> lvlInfo = SpellLevel.getMasterLevelInfo(null, (Spell) thisPObject);
			if ((lvlInfo != null) && (lvlInfo.size() != iCount))
			{
				Logging.errorPrint(Integer.toString(iCount) + " classes and domains found. Should have been "
						+ Integer.toString(lvlInfo.size()) + "\n" + lvlInfo);
			}

			break;

		case EditorConstants.EDIT_TEMPLATE:

			//
			// Populate the templates available list and selected lists
			//
			List<String> availableTemplateList = new ArrayList<String>();
			List<String> selectedTemplateList = new ArrayList<String>();
			List<String> selectedTemplateList2 = new ArrayList<String>();

			for (PCTemplate aTemplate : Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class))
			{
				aString = aTemplate.getKeyName();

				if (!availableTemplateList.contains(aString))
				{
					availableTemplateList.add(aString);
				}
			}

			//
			// remove this template's granted templates from the available list and place into selected list
			//
			moveGrantedTemplatesFromAvailableToSelected(thisPObject, selectedTemplateList, selectedTemplateList2,
					availableTemplateList);

			pnlTemplates.setAvailableList(availableTemplateList, true);
			pnlTemplates.setSelectedList(selectedTemplateList, true);
			pnlTemplates.setSelectedList2(selectedTemplateList2, true);

			//
			// Populate the favoured classes available list and selected lists
			//
			List<String> availableFavouredClassesList = new ArrayList<String>();
			List<String> selectedFavouredClassesList = new ArrayList<String>();

			for (PCClass aClass : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
			{
				if (!(aClass instanceof SubClass) && !(aClass instanceof SubstitutionClass))
				{
					availableFavouredClassesList.add(aClass.getKeyName());
				}
				if (aClass.containsListFor(ListKey.SUB_CLASS))
				{
					for (SubClass subClass : aClass.getListFor(ListKey.SUB_CLASS))
					{
						availableFavouredClassesList.add(aClass.getKeyName() + "." + subClass.getKeyName());
					}
				}
				if (aClass.containsListFor(ListKey.SUBSTITUTION_CLASS))
				{
					for (SubstitutionClass subClass : aClass.getListFor(ListKey.SUBSTITUTION_CLASS))
					{
						availableFavouredClassesList.add(aClass.getKeyName() + "." + subClass.getKeyName());
					}
				}
			}

			availableFavouredClassesList.add("Any");
			favClass = thisPObject.getListFor(ListKey.FAVORED_CLASS);
			if (favClass != null)
			{
				for (CDOMReference<? extends PCClass> ref : favClass)
				{
					String cl = ref.getLSTformat(false);
					availableFavouredClassesList.remove(cl);
					selectedFavouredClassesList.add(cl);
				}
			}
			pnlClasses.setAvailableList(availableFavouredClassesList, true);
			pnlClasses.setSelectedList(selectedFavouredClassesList, true);

			//
			// Populate the feats available list and selected lists
			//
			List<String> availableTemplateFeatsList = new ArrayList<String>();
			List<String> selectedTemplateFeatsList = new ArrayList<String>();

			for (Ability anAbility : Globals.getContext().ref.getManufacturer(Ability.class, AbilityCategory.FEAT)
					.getAllObjects())
			{
				availableTemplateFeatsList.add(anAbility.getKeyName());
			}

			PersistentTransitionChoice<?> ptc = thisPObject.get(ObjectKey.TEMPLATE_FEAT);
			if (ptc != null)
			{
				for (String str : ptc.getChoices().getLSTformat().split(","))
				{
					if (!selectedTemplateFeatsList.contains(str))
					{
						availableTemplateFeatsList.remove(str);
						selectedTemplateFeatsList.add(str);
					}
				}
			}

			pnlFeats.setAvailableList(availableTemplateFeatsList, true);
			pnlFeats.setSelectedList(selectedTemplateFeatsList, true);

			//
			// Populate the movement panel
			//
			movementValues = new ArrayList<String>();

			List<Movement> mmsl = thisPObject.getListFor(ListKey.MOVEMENT);
			if (mmsl != null && !mmsl.isEmpty())
			{
				Movement cmv = mmsl.get(0);
				if (cmv != null && cmv.getNumberOfMovementTypes() > 0)
				{
					for (int index = 0; index < cmv.getNumberOfMovementTypes(); index++)
					{
						final String aMove = MovementPanel.makeMoveString(cmv.getMovementType(index),
								cmv.getMovement(index), cmv.getMovementMult(index), cmv.getMovementMultOp(index));
						movementValues.add(aMove);
					}
					pnlMovement.setMoveRateType(cmv.getMoveRatesFlag());
				}
			}
			pnlMovement.setSelectedList(movementValues);

			//
			// Populate the vision panel
			//
			vision = new ArrayList<Vision>();
			for (CDOMReference<Vision> ref : thisPObject.getSafeListMods(Vision.VISIONLIST))
			{
				vision.addAll(ref.getContainedObjects());
			}
			visionValues = buildVisionValues(vision);
			pnlVision.setSelectedList(visionValues);

			//
			// Populate the specialabilities panel
			//
			List<String> selectedSAList = new ArrayList<String>();

			LoadContext context = Globals.getContext();
			String[] level = context.unparseSubtoken(thisPObject, "LEVEL");
			if (level != null)
			{
				for (String s : level)
				{
					selectedSAList.add("LEVEL:" + s);
				}
			}
			level = context.unparseSubtoken(thisPObject, "HD");
			if (level != null)
			{
				for (String s : level)
				{
					selectedSAList.add("HD:" + s);
				}
			}

			pnlLevelAbilities.setSelectedList(selectedSAList);

			//
			// Populate the bonus languages available list and selected lists
			//
			List<Language> availableBonusLangList = new ArrayList<Language>();
			List<Language> selectedBonusLangList = new ArrayList<Language>();

			Collection<CDOMReference<Language>> langColl = thisPObject.getListMods(Language.STARTING_LIST);
			if (langColl != null)
			{
				for (CDOMReference<Language> ref : langColl)
				{
					selectedBonusLangList.addAll(ref.getContainedObjects());
				}
			}
			availableBonusLangList.addAll(Globals.getContext().ref.getConstructedCDOMObjects(Language.class));
			availableBonusLangList.removeAll(selectedBonusLangList);

			pnlBonusLang.setAvailableList(availableBonusLangList, true);
			pnlBonusLang.setSelectedList(selectedBonusLangList, true);

			break;

		case EditorConstants.EDIT_CAMPAIGN:
			pnlFileTypes.updateView((Campaign) thisPObject);

			break;

		default:
			break;
		}

		//
		// Initialize the contents of the available and selected languages lists
		//
		if (pnlLanguages != null)
		{
			List<Language> availableLanguageList = new ArrayList<Language>();
			List<Language> selectedLanguageList = new ArrayList<Language>();
			List<Language> selectedLanguageList2 = new ArrayList<Language>();

			List<QualifiedObject<CDOMReference<Language>>> aSet = thisPObject.getSafeListFor(ListKey.AUTO_LANGUAGES);

			for (Language aLang : Globals.getContext().ref.getConstructedCDOMObjects(Language.class))
			{
				boolean found = false;
				for (QualifiedObject<CDOMReference<Language>> qual : aSet)
				{
					CDOMReference<Language> ref = qual.getRawObject();
					List<Prerequisite> prereqs = qual.getPrerequisiteList();
					Prerequisite p = null;
					if (prereqs != null && !prereqs.isEmpty())
					{
						p = prereqs.get(0);
					}
					if (ref.contains(aLang))
					{
						selectedLanguageList.add(aLang);
						found = true;
						break;
					}
				}
				if (!found)
				{
					availableLanguageList.add(aLang);
				}
			}

			if ((editType == EditorConstants.EDIT_TEMPLATE) || (editType == EditorConstants.EDIT_RACE))
			{
				aString = "";
				String[] unparsed = Globals.getContext().unparseSubtoken(thisPObject, "CHOOSE");
				if (unparsed != null)
				{
					for (String s : unparsed)
					{
						if (s.startsWith("LANGAUTO|"))
						{
							aString = s;
							break;
						}
					}
				}

				aTok = new StringTokenizer(aString, "|", false);

				while (aTok.hasMoreTokens())
				{
					final Language aLang = Globals.getContext().ref.silentlyGetConstructedCDOMObject(Language.class,
							aTok.nextToken());

					if (aLang != null)
					{
						selectedLanguageList2.add(aLang);
						availableLanguageList.remove(aLang);
					}
				}

				pnlLanguages.setSelectedList2(selectedLanguageList2, true);
			}

			pnlLanguages.setAvailableList(availableLanguageList, true);
			pnlLanguages.setSelectedList(selectedLanguageList, true);
		}

		//
		// Initialize the contents of the available and selected weapon prof lists
		//
		if (pnlWeapons != null)
		{
			List<String> selectedWPList = new ArrayList<String>();
			List<String> availableWeaponProfList = new ArrayList<String>();
			Set<String> wpnProfTypes = new HashSet<String>();
			for (WeaponProf wp : Globals.getContext().ref.getConstructedCDOMObjects(WeaponProf.class))
			{
				availableWeaponProfList.add(wp.getDisplayName());
				// TODO: Remove fully qualified package name, once our Type  
				// enum no longer generates a compile error in OpenJDK 1.7
				for (pcgen.cdom.enumeration.Type t : wp.getTrueTypeList(false))
				{
					wpnProfTypes.add(t.toString());
				}
			}
			for (String typeName : wpnProfTypes)
			{
				availableWeaponProfList.add("TYPE." + typeName.toUpperCase());
			}

			// We don't load the WeaponProfAuto list as that is composed of
			// generated things, such as natural weapon proficiencies
			final List<String> autoWeap = new ArrayList<String>();
			String[] autoTokens = Globals.getContext().unparseSubtoken(thisPObject, "AUTO");
			if (autoTokens != null)
			{
				for (String s : autoTokens)
				{
					if (s.startsWith("WEAPONPROF|"))
					{
						autoWeap.add(s.substring(11));
					}
				}
			}

			for (Iterator<String> e = autoWeap.iterator(); e.hasNext();)
			{
				moveProfToSelectedList(availableWeaponProfList, selectedWPList, e.next());
			}
			Collection<CDOMReference<WeaponProf>> wplist = thisPObject.getListMods(WeaponProf.STARTING_LIST);
			if (wplist != null)
			{
				List<String> selectedWPList2 = new ArrayList<String>();
				for (CDOMReference<WeaponProf> ref : wplist)
				{
					moveProfToSelectedList(availableWeaponProfList, selectedWPList2, ref.getLSTformat(false));
				}

				pnlWeapons.setSelectedList2(selectedWPList2, true);
				pnlWeapons.setLblSelectedText(LanguageBundle.getString("in_demAllGranted"));
				pnlWeapons.setLblSelected2Text(LanguageBundle.getString("in_demChoiceGranted"));
			}

			pnlWeapons.setAvailableList(availableWeaponProfList, true);
			pnlWeapons.setSelectedList(selectedWPList, true);
		}

		String[] values;

		switch (editType)
		{
		case EditorConstants.EDIT_DEITY:

			//
			// Initialize the Variable combo with all the variable names we can find
			//
			List<String> availableVariableList = new ArrayList<String>();
			addVariables(availableVariableList, Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class));
			addVariables(availableVariableList,
					Globals.getContext().ref.getManufacturer(Ability.class, AbilityCategory.FEAT).getAllObjects());
			addVariables(availableVariableList, Globals.getContext().ref.getConstructedCDOMObjects(Race.class));
			addVariables(availableVariableList, Globals.getContext().ref.getConstructedCDOMObjects(Skill.class));
			addVariables(availableVariableList,
					Globals.getContext().ref.getConstructedCDOMObjects(EquipmentModifier.class));
			addVariables(availableVariableList, Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class));
			addVariables(availableVariableList, Globals.getAllCompanionMods());
			Collections.sort(availableVariableList);

			break;

		case EditorConstants.EDIT_DOMAIN:

			//
			// Domain Spells allow levels 1 to 9
			//
			List<String> availableDomainList = new ArrayList<String>();

			for (int i = 1; i <= 9; ++i)
			{
				availableDomainList.add(String.valueOf(i));
			}

			pnlQSpells.setQualifierModel(new DefaultComboBoxModel(availableDomainList.toArray()));
			pnlQSpells.setQualifierSelectedIndex(0);

			break;

		case EditorConstants.EDIT_FEAT:
			break;

		case EditorConstants.EDIT_LANGUAGE:
			break;

		case EditorConstants.EDIT_RACE:
			break;

		case EditorConstants.EDIT_SKILL:
			List<String> availableSkillList = new ArrayList<String>();
			List<String> selectedSkillList = new ArrayList<String>();

			for (Skill aSkill : Globals.getContext().ref.getConstructedCDOMObjects(Skill.class))
			{
				if (!aSkill.getKeyName().equals(thisPObject.getKeyName()))
				{
					availableSkillList.add(aSkill.getKeyName());
				}
			}

			//
			// BONUS:SKILL|Ride|2|PRESKILL:1,Handle Animal=5|TYPE=Synergy.STACK
			//
			for (Iterator<BonusObj> e = thisPObject.getSafeListFor(ListKey.BONUS).iterator(); e.hasNext();)
			{
				parseSynergyBonus(e.next(), availableSkillList, selectedSkillList);
			}

			pnlSynergy.setAvailableList(availableSkillList, true);
			pnlSynergy.setSelectedList(selectedSkillList, true);

			//
			// initialize the Qualifier and Variables combos on the Synergy tab
			//
			values = new String[30];

			for (int i = 0; i < values.length; ++i)
			{
				values[i] = String.valueOf(i + 1);
			}

			pnlSynergy.setQualifierModel(new DefaultComboBoxModel(values));
			pnlSynergy.setVariableModel(new DefaultComboBoxModel(values));
			pnlSynergy.setQualifierSelectedIndex(4); // should be 5
			pnlSynergy.setVariableSelectedIndex(1); // should be 2

			break;

		case EditorConstants.EDIT_SPELL:

			//
			// Domains allow levels 1 to 9
			//
			List<String> availableDomainsList = new ArrayList<String>();

			for (int i = 1; i <= 9; ++i)
			{
				availableDomainsList.add(String.valueOf(i));
			}

			pnlQDomains.setQualifierModel(new DefaultComboBoxModel(availableDomainsList.toArray()));
			pnlQDomains.setQualifierSelectedIndex(0);

			//
			// Classes allow levels 0-9
			//
			availableDomainsList.add(0, "0");
			pnlQClasses.setQualifierModel(new DefaultComboBoxModel(availableDomainsList.toArray()));
			pnlQClasses.setQualifierSelectedIndex(0);

			break;

		case EditorConstants.EDIT_TEMPLATE:
			break;

		default:
			break;
		}

		//
		// Initialize the contents of the available and selected class/cross-class lists
		//
		if (pnlSkills != null)
		{
			List<String> availableClassCrossClassList = new ArrayList<String>();
			List<String> selectedClassCrossClassList = new ArrayList<String>();

			for (Skill aSkill : Globals.getContext().ref.getConstructedCDOMObjects(Skill.class))
			{
				aString = aSkill.getKeyName();

				if (!availableClassCrossClassList.contains(aString))
				{
					availableClassCrossClassList.add(aString);
				}

				// TODO: Remove fully qualified package name, once our Type  
				// enum no longer generates a compile error in OpenJDK 1.7
				for (pcgen.cdom.enumeration.Type type : aSkill.getTrueTypeList(false))
				{
					aString = "TYPE." + type;

					if (!availableClassCrossClassList.contains(aString))
					{
						availableClassCrossClassList.add(aString);
					}
				}
			}

			List<CDOMReference<Skill>> csk = thisPObject.getListFor(ListKey.CSKILL);

			if (csk != null)
			{
				for (CDOMReference<Skill> ref : csk)
				{
					for (Skill sk : ref.getContainedObjects())
					{
						String key = sk.getKeyName();
						selectedClassCrossClassList.add(key);

						if (availableClassCrossClassList.contains(key))
						{
							availableClassCrossClassList.remove(key);
						}
					}
				}
			}

			pnlSkills.setSelectedList(selectedClassCrossClassList, true);

			List<String> selectedCCSkillList = new ArrayList<String>();
			List<CDOMReference<Skill>> ccsk = thisPObject.getListFor(ListKey.CCSKILL);

			if (ccsk != null)
			{
				for (CDOMReference<Skill> ref : ccsk)
				{
					for (Skill sk : ref.getContainedObjects())
					{
						String key = sk.getKeyName();
						selectedClassCrossClassList.add(key);

						if (availableClassCrossClassList.contains(key))
						{
							availableClassCrossClassList.remove(key);
						}
					}
				}
			}

			pnlSkills.setSelectedList2(selectedCCSkillList, true);

			pnlSkills.setAvailableList(availableClassCrossClassList, true);
		}

		pnlAdvanced.setAvailableTagList(editType);
		List<String> selectedAdvancedList = buildAdvancedSelectedList(editType);
		pnlAdvanced.setSelected(selectedAdvancedList);
	}

	/**
	 * Add the domain spells for the current object to the selected spell list, 
	 * removing them from the available list.
	 * 
	 * @param availableSpellList The list of available spells
	 * @param selectedSpellList The list of domain spells strings 
	 */
	@SuppressWarnings("unchecked")
	private void addSelectedDomainSpells(List<Spell> availableSpellList, List<String> selectedSpellList)
	{
		final LoadContext context = Globals.getContext();
		final String tokenName = "SPELLLEVEL";

		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> changedDomainLists = context
				.getListContext().getChangedLists(thisPObject, DomainSpellList.class);
		for (CDOMReference listRef : changedDomainLists)
		{
			AssociatedChanges changes = context.getListContext().getChangesInList(tokenName, thisPObject, listRef);
			Collection<Spell> removedItems = changes.getRemoved();
			if (removedItems != null && !removedItems.isEmpty() || changes.includesGlobalClear())
			{
				return;
			}
			MapToList<CDOMReference<Spell>, AssociatedPrereqObject> mtl = changes.getAddedAssociations();
			if (mtl == null || mtl.isEmpty())
			{
				// Zero indicates no Token
				continue;
			}
			for (CDOMReference<Spell> added : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(added))
				{
					Integer lvl = assoc.getAssociation(AssociationKey.SPELL_LEVEL);
					Boolean known = assoc.getAssociation(AssociationKey.KNOWN);
					boolean isKnown = known != null && known;
					if (!isKnown)
					{
						for (Spell spell : added.getContainedObjects())
						{
							String entry = encodeSpellEntry(spell.getKeyName(), String.valueOf(lvl));
							selectedSpellList.add(entry);
							availableSpellList.remove(spell);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the first prereq of the required kind, or no kind 
	 * (a PREMULT), associated with a linked Domain object. 
	 * 
	 * @param obj the object to be searched, normally a Deity object
	 * @param kind the kind of prereq to be retrieved.
	 * 
	 * @return the first prereq of type, null if there are none.
	 */
	private Prerequisite getFirstPrereqOfKind(CDOMObject obj, String kind)
	{
		if (obj == null)
		{
			return null;
		}

		Collection<CDOMReference<Domain>> keys = obj.getListMods(Deity.DOMAINLIST);
		if (keys == null || keys.isEmpty())
		{
			return null;
		}

		CDOMReference<Domain> firstKey = keys.iterator().next();
		Collection<AssociatedPrereqObject> prereqObjCol = obj.getListAssociations(Deity.DOMAINLIST, firstKey);
		AssociatedPrereqObject assoc = prereqObjCol.iterator().next();

		List<Prerequisite> prereqList = assoc.getPrerequisiteList();
		if (prereqList == null || prereqList.isEmpty())
		{
			return null;
		}

		return getPrereqOfKind(kind, prereqList);

	}

	/**
	 * Retrieve the first prerequisite of either the specified kind, or no kind 
	 * (a PREMULT).
	 *  
	 * @param kind The kind of prerequisite to be found
	 * @param prereqList The list to be searched.
	 * @return The matching prerequisite, or null if none.
	 */
	private Prerequisite getPrereqOfKind(String kind, List<Prerequisite> prereqList)
	{
		for (Prerequisite prerequisite : prereqList)
		{
			if (kind.equals(prerequisite.getKind()) || prerequisite.getKind() == null)
			{
				return prerequisite;
			}
			Prerequisite found = getPrereqOfKind(kind, prerequisite.getPrerequisites());
			if (found != null)
			{
				return found;
			}
		}
		return null;
	}

	/**
	 * Build up a list of the vision strings based on a list of 
	 * vision objects. 
	 * @param vision The vision objects
	 * @return List of vision strings
	 */
	private List<String> buildVisionValues(List<Vision> vision)
	{
		List<String> visionValues;
		visionValues = new ArrayList<String>();

		if (vision != null)
		{
			for (Vision vis : vision)
			{
				final StringBuffer visionString = new StringBuffer(25);
				visionString.append(vis.getType());
				visionString.append(',').append(vis.getDistance());
				visionValues.add(visionString.toString());
			}
		}
		return visionValues;
	}

	/**
	 * Move the named Weapon Proficiency from the available list to the
	 * selected list.
	 * @param availableList The list of available weapon prof names
	 * @param selectedList The list of selected weapon prof names.
	 * @param profName The prof name to be moved.
	 */
	private void moveProfToSelectedList(List<String> availableList, List<String> selectedList, String profName)
	{
		if (profName.startsWith("TYPE"))
		{
			selectedList.add(profName);
			availableList.remove(profName.toUpperCase());
		} else
		{
			final WeaponProf wp = Globals.getContext().ref.silentlyGetConstructedCDOMObject(WeaponProf.class, profName);

			if (wp != null)
			{
				selectedList.add(wp.toString());
				availableList.remove(wp.toString());
			}
		}
	}

	/**
	 * Move this template's granted templates from the available list and place into selected list.
	 * @param templateList
	 * @param selectedList
	 * @param selectedList2
	 * @param availableList
	 */
	private static void moveGrantedTemplatesFromAvailableToSelected(PObject cdo, List<String> selectedList,
			List<String> selectedList2, List<String> availableList)
	{
		for (CDOMReference<PCTemplate> ref : cdo.getSafeListFor(ListKey.TEMPLATE))
		{
			for (PCTemplate pct : ref.getContainedObjects())
			{
				String aString = pct.getKeyName();
				selectedList.add(aString);
				availableList.remove(aString);
			}
		}

		for (CDOMReference<PCTemplate> ref : cdo.getSafeListFor(ListKey.TEMPLATE_CHOOSE))
		{
			for (PCTemplate pct : ref.getContainedObjects())
			{
				String aString = pct.getKeyName();
				if (!selectedList.contains(aString))
				{
					selectedList2.add(aString);
				}
			}
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		btnCancel = new JButton();
		btnSave = new JButton();
		jTabbedPane1 = new JTabbedPane();
		pnlAdvanced = new AdvancedPanel(thisPObject);
		pnlButtons = new JPanel();
		pnlMainDialog = new JPanel();
		pnlTabs = new JPanel();

		switch (editType)
		{
		case EditorConstants.EDIT_CLASS:
			pnlLanguages = new AvailableSelectedPanel();
			pnlSkills = new AvailableSelectedPanel(true);
			pnlWeapons = new AvailableSelectedPanel(true);
			pnlClassAbility = new ClassAbilityPanel();
			pnlClassAbility.updateView((PCClass) thisPObject);
			pnlClassLevel = new ClassLevelPanel();
			pnlClassLevel.updateView((PCClass) thisPObject);

			break;

		case EditorConstants.EDIT_DEITY:
			pnlLanguages = new AvailableSelectedPanel();
			pnlSkills = new AvailableSelectedPanel(true);
			pnlWeapons = new AvailableSelectedPanel();

			//cmbQualifier = new JComboBoxEx();
			//cmbVariable = new JComboBoxEx();
			//lblQualifier = new JLabel();
			//lblVariable = new JLabel();
			pnlDomains = new AvailableSelectedPanel();
			pnlFollowers = new AvailableSelectedPanel();
			pnlRaces = new AvailableSelectedPanel();

			break;

		case EditorConstants.EDIT_DOMAIN:
			pnlLanguages = new AvailableSelectedPanel();
			pnlFeats = new AvailableSelectedPanel();
			pnlSkills = new AvailableSelectedPanel(true);
			pnlWeapons = new AvailableSelectedPanel();
			pnlQSpells = new QualifiedAvailableSelectedPanel("in_demLevel", null, new EditorAddFilter() {
				@Override
				public Object encode(Object anObj)
				{
					return encodeSpellEntry(pnlQSpells, anObj.toString());
				}

				@Override
				public Object decode(Object anObj)
				{
					return decodeSpellEntry(anObj.toString());
				}
			}, null);

			break;

		case EditorConstants.EDIT_FEAT:
			pnlLanguages = new AvailableSelectedPanel();
			pnlSkills = new AvailableSelectedPanel(true);
			pnlWeapons = new AvailableSelectedPanel();

			break;

		case EditorConstants.EDIT_LANGUAGE:
			break;

		case EditorConstants.EDIT_RACE:
			pnlMovement = new MovementPanel(true);
			pnlVision = new VisionPanel();
			pnlNaturalAttacks = new NaturalAttacksPanel();
			pnlLanguages = new AvailableSelectedPanel(true);
			pnlSkills = new AvailableSelectedPanel(true);
			pnlTemplates = new AvailableSelectedPanel(true);
			pnlWeapons = new AvailableSelectedPanel(true);
			pnlClasses = new AvailableSelectedPanel();
			pnlFeats = new AvailableSelectedPanel();
			pnlVFeats = new AvailableSelectedPanel();
			pnlBonusLang = new AvailableSelectedPanel();
			pnlAppearance = new AppearancePanel();
			pnlAge = new AgePanel();

			break;

		case EditorConstants.EDIT_SKILL:
			pnlLanguages = new AvailableSelectedPanel();
			pnlSkills = new AvailableSelectedPanel(true);
			pnlWeapons = new AvailableSelectedPanel();

			//cmbQualifier = new JComboBoxEx();
			//cmbVariable = new JComboBoxEx();
			//lblQualifier = new JLabel();
			//lblVariable = new JLabel();
			pnlClasses = new AvailableSelectedPanel(true);
			pnlSynergy = new QualifiedAvailableSelectedPanel("in_demSkillRank", "in_demSynergyBonus",
					new EditorAddFilter() {
						@Override
						public Object encode(Object anObj)
						{
							return encodeSynergyEntry((String) anObj);
						}

						@Override
						public Object decode(Object anObj)
						{
							return decodeSynergyEntry((String) anObj);
						}
					}, null);

			break;

		case EditorConstants.EDIT_SPELL:
			pnlBase2 = new SpellBasePanel2();
			pnlQClasses = new QualifiedAvailableSelectedPanel("in_demLevel", null, new EditorAddFilter() {
				@Override
				public Object encode(Object anObj)
				{
					return encodeDomainEntry(pnlQClasses, (String) anObj);
				}

				@Override
				public Object decode(Object anObj)
				{
					return decodeDomainEntry((String) anObj);
				}
			}, null);
			pnlQDomains = new QualifiedAvailableSelectedPanel("in_demLevel", null, new EditorAddFilter() {
				@Override
				public Object encode(Object anObj)
				{
					return encodeDomainEntry(pnlQDomains, (String) anObj);
				}

				@Override
				public Object decode(Object anObj)
				{
					return decodeDomainEntry((String) anObj);
				}
			}, null);

			break;

		case EditorConstants.EDIT_TEMPLATE:
			pnlVision = new VisionPanel();
			pnlMovement = new MovementPanel(false);
			pnlLevelAbilities = new LevelAbilitiesPanel();
			pnlLanguages = new AvailableSelectedPanel(true);
			pnlSkills = new AvailableSelectedPanel(true);
			pnlTemplates = new AvailableSelectedPanel(true);
			pnlWeapons = new AvailableSelectedPanel();
			pnlClasses = new AvailableSelectedPanel();
			pnlFeats = new AvailableSelectedPanel();
			pnlBonusLang = new AvailableSelectedPanel();

			break;

		case EditorConstants.EDIT_CAMPAIGN:
			pnlFileTypes = new SourceFilesPanel();

			break;

		default:
			break;
		}

		getContentPane().setLayout(new GridBagLayout());

		String ttl = "";

		switch (editType)
		{
		case EditorConstants.EDIT_CLASS:
			ttl = "Class";

			break;

		case EditorConstants.EDIT_DEITY:
			ttl = "Deity";

			break;

		case EditorConstants.EDIT_DOMAIN:
			ttl = "Domain";

			break;

		case EditorConstants.EDIT_FEAT:
			ttl = "Feat";

			break;

		case EditorConstants.EDIT_LANGUAGE:
			ttl = "Language";

			break;

		case EditorConstants.EDIT_RACE:
			ttl = "Race";

			break;

		case EditorConstants.EDIT_SKILL:
			ttl = "Skill";

			break;

		case EditorConstants.EDIT_SPELL:
			ttl = "Spell";

			break;

		case EditorConstants.EDIT_TEMPLATE:
			ttl = "Template";

			break;

		default:
			break;
		}

		setTitle(LanguageBundle.getString("in_demTitle" + ttl));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt)
			{
				wasCancelled = true;
				closeDialog();
			}
		});

		pnlMainDialog.setLayout(new GridBagLayout());

		pnlMainDialog.setPreferredSize(new Dimension(640, 460));
		pnlTabs.setLayout(new BorderLayout());

		pnlTabs.setMinimumSize(new Dimension(128, 88));
		pnlTabs.setPreferredSize(new Dimension(640, 440));
		jTabbedPane1.setName(LanguageBundle.getString("in_demDeitytab"));
		pnlMainTab = new EditorBasePanel(editType);

		jTabbedPane1.addTab(LanguageBundle.getString("in_demBase"), pnlMainTab);

		switch (editType)
		{
		case EditorConstants.EDIT_CLASS:
			jTabbedPane1.addTab(LanguageBundle.getString("in_classability"), pnlClassAbility);
			jTabbedPane1.addTab(LanguageBundle.getString("in_classlevel"), pnlClassLevel);

			break;

		case EditorConstants.EDIT_DEITY:
			pnlDomains.setHeader(LanguageBundle.getString("in_demGrantDom"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_domains"), pnlDomains);

			jTabbedPane1.addTab(LanguageBundle.getString("in_demFollowers"), pnlFollowers);

			pnlRaces.setHeader(LanguageBundle.getString("in_demRacWors"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_race"), pnlRaces);

			break;

		case EditorConstants.EDIT_DOMAIN:
			jTabbedPane1.addTab("Spell Lists", pnlQSpells);
			break;

		case EditorConstants.EDIT_FEAT:
			break;

		case EditorConstants.EDIT_LANGUAGE:
			break;

		case EditorConstants.EDIT_RACE:
			pnlClasses.setHeader(LanguageBundle.getString("in_demFavoredClasses"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_demClasses"), pnlClasses);
			pnlTemplates.setLblSelectedText(LanguageBundle.getString("in_demAllGranted"));
			pnlTemplates.setLblSelected2Text(LanguageBundle.getString("in_demChoiceGranted"));
			pnlLanguages.setLblSelectedText(LanguageBundle.getString("in_demAllGranted"));
			pnlLanguages.setLblSelected2Text(LanguageBundle.getString("in_demChoiceGranted"));
			pnlBonusLang.setHeader(LanguageBundle.getString("in_demBonusLang"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_demBonusLangAbbrev"), pnlBonusLang);
			pnlFeats.setLblSelectedText(LanguageBundle.getString("in_demSelFeats"));

			break;

		case EditorConstants.EDIT_SKILL:

			//buildSynergyTab();
			pnlSynergy.setHeader(" ");
			jTabbedPane1.addTab(LanguageBundle.getString("in_demSynergy"), pnlSynergy);

			pnlClasses.setHeader(" ");
			jTabbedPane1.addTab(LanguageBundle.getString("in_demClasses"), pnlClasses);

			break;

		case EditorConstants.EDIT_SPELL:
			jTabbedPane1.addTab(LanguageBundle.getString("in_demBase2"), pnlBase2);
			jTabbedPane1.addTab(LanguageBundle.getString("in_demClasses"), pnlQClasses);
			jTabbedPane1.addTab(LanguageBundle.getString("in_domains"), pnlQDomains);

			break;

		case EditorConstants.EDIT_TEMPLATE:
			pnlClasses.setHeader(LanguageBundle.getString("in_demFavoredClasses"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_demClasses"), pnlClasses);
			pnlTemplates.setLblSelectedText(LanguageBundle.getString("in_demAllGranted"));
			pnlTemplates.setLblSelected2Text(LanguageBundle.getString("in_demChoiceGranted"));
			pnlLanguages.setLblSelectedText(LanguageBundle.getString("in_demAllGranted"));
			pnlLanguages.setLblSelected2Text(LanguageBundle.getString("in_demChoiceGranted"));
			pnlBonusLang.setHeader(LanguageBundle.getString("in_demBonusLang"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_demBonusLangAbbrev"), pnlBonusLang);

			break;

		case EditorConstants.EDIT_CAMPAIGN:
			jTabbedPane1.addTab(LanguageBundle.getString("in_fileTypes"), pnlFileTypes);

			break;

		default:
			break;
		}

		if (pnlLanguages != null)
		{
			pnlLanguages.setHeader(LanguageBundle.getString("in_demGrantLang"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_languages"), pnlLanguages);
		}

		if (pnlWeapons != null)
		{
			pnlWeapons.setHeader(LanguageBundle.getString("in_demGraWeaPro"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_weapon"), pnlWeapons);
		}

		if (pnlSkills != null)
		{
			pnlSkills.setHeader(LanguageBundle.getString("in_demGraSkil"));
			pnlSkills.setLblSelectedText(LanguageBundle.getString("in_demSelClaSkil"));
			pnlSkills.setLblSelected2Text(LanguageBundle.getString("in_demSelCroCla"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_skills"), pnlSkills);
		}

		if (pnlLevelAbilities != null)
		{
			jTabbedPane1.addTab(LanguageBundle.getString("in_specialabilities"), pnlLevelAbilities);
		}

		if (pnlMovement != null)
		{
			jTabbedPane1.addTab(LanguageBundle.getString("in_movement"), pnlMovement);
		}

		if (pnlTemplates != null)
		{
			pnlTemplates.setHeader(LanguageBundle.getString("in_demGraTemp"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_templates"), pnlTemplates);
		}

		if (pnlVision != null)
		{
			jTabbedPane1.addTab(LanguageBundle.getString("in_demVision"), pnlVision);
		}

		if (pnlAge != null)
		{
			jTabbedPane1.addTab(LanguageBundle.getString("in_demAge"), pnlAge);
		}

		if (pnlAppearance != null)
		{
			jTabbedPane1.addTab(LanguageBundle.getString("in_demAppearance"), pnlAppearance);
		}

		if (pnlNaturalAttacks != null)
		{
			jTabbedPane1.addTab("Natural Weapons", pnlNaturalAttacks);
		}

		if (pnlFeats != null)
		{
			pnlFeats.setHeader(LanguageBundle.getString("in_demGraFeat"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_feats"), pnlFeats);
		}

		if (pnlVFeats != null)
		{
			pnlVFeats.setHeader(LanguageBundle.getString("in_demGraVFeat"));
			jTabbedPane1.addTab(LanguageBundle.getString("in_demVFeats"), pnlVFeats);
		}

		//The Advanced Tab has no meaning in the Source File Editor and therefore
		// should only be shown when needed.
		if (editType != EditorConstants.EDIT_CAMPAIGN)
		{
			jTabbedPane1.addTab(LanguageBundle.getString("in_demAdv"), pnlAdvanced);
		}

		pnlTabs.add(jTabbedPane1, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 6.0;
		pnlMainDialog.add(pnlTabs, gridBagConstraints);

		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));

		btnCancel.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel"));
		btnCancel.setText(LanguageBundle.getString("in_cancel"));
		btnCancel.setPreferredSize(new Dimension(80, 26));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				btnCancelActionPerformed();
			}
		});

		pnlButtons.add(btnCancel);

		btnSave.setMnemonic(LanguageBundle.getMnemonic("in_mn_save"));
		btnSave.setText(LanguageBundle.getString("in_save"));
		btnSave.setPreferredSize(new Dimension(80, 26));
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				btnSaveActionPerformed();
			}
		});

		pnlButtons.add(btnSave);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		pnlMainDialog.add(pnlButtons, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(pnlMainDialog, gridBagConstraints);

		pack();
	}

	/**
	 * Parses the prerequisite to obtain the selected alignments.
	 * 
	 * @param availableList the available list
	 * @param selectedList the selected list
	 * @param prereq the prereq
	 */
	private void parseAlignAbbrev(List<String> availableList, List<String> selectedList, Prerequisite prereq)
	{
		if (prereq == null)
		{
			return;
		}
		for (Prerequisite childPrereq : prereq.getPrerequisites())
		{
			parseAlignAbbrev(availableList, selectedList, childPrereq);
		}

		String key = prereq.getKey();
		PCAlignment align = Globals.getContext().ref.getAbbreviatedObject(PCAlignment.class, key);
		if (align != null)
		{
			selectedList.add(align.getDisplayName());
		}
	}

	private static boolean parseSynergyBonus(final BonusObj aBonus, List<String> availableList,
			List<String> selectedList)
	{
		String aString = aBonus.toString();

		if (aString.startsWith("SKILL|"))
		{
			final List bonusParts = CoreUtility.split(aString, '|');

			//
			// Should probably check if bonusParts(1) == <skill name>, but
			// if the user has just copied a skill with a synergy bonus, the
			// skill name will be incorrect. If we don't check the name, then
			// we can correct the naming when we save
			//
			if ((bonusParts.size() == 5) && ((String) bonusParts.get(3)).startsWith("PRESKILL:1,")
					&& ((String) bonusParts.get(4)).equalsIgnoreCase("TYPE=Synergy.STACK"))
			{
				final String bonus = (String) bonusParts.get(2);
				String skill = ((String) bonusParts.get(3)).substring(11);
				final int idx = skill.indexOf('=');

				if (idx > 0)
				{
					final String ranks = skill.substring(idx + 1);
					skill = skill.substring(0, idx);

					if (availableList != null)
					{
						availableList.remove(skill);
					}

					if (selectedList != null)
					{
						selectedList.add(encodeSynergyEntry(skill, ranks, bonus));
					}

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Build the list of items that should be pre-poulated as 'selected' on the
	 * advanced tab.
	 * @param anEditType The type of object ebing edited.
	 * @return A list of selected items, each a String object.
	 */
	List<String> buildAdvancedSelectedList(int anEditType)
	{
		List<String> selectedList = new ArrayList<String>();

		//
		// Initialize the list of advanced items in the selected list
		//
		selectedList.clear();

		switch (anEditType)
		{
		case EditorConstants.EDIT_DEITY:
			for (Iterator e = ((Deity) thisPObject).getSafeListFor(ListKey.PANTHEON).iterator(); e.hasNext();)
			{
				selectedList.add("PANTHEON:" + e.next());
			}

			break;

		case EditorConstants.EDIT_DOMAIN:
		case EditorConstants.EDIT_FEAT:
			if (thisPObject.getSafe(StringKey.CHOICE_STRING).length() != 0)
			{
				selectedList.add("CHOOSE:" + thisPObject.getSafe(StringKey.CHOICE_STRING));
			}

			break;

		case EditorConstants.EDIT_SKILL:
			final String choiceString = thisPObject.getSafe(StringKey.CHOICE_STRING);

			if ((choiceString != null) && (choiceString.length() > 0))
			{
				selectedList.add("CHOOSE:" + choiceString);
			}

			break;

		default:
			break;
		}

		String[] auto = Globals.getContext().unparseSubtoken(thisPObject, "AUTO");

		if (auto != null)
		{
			for (String key : auto)
			{
				if (!key.startsWith("WEAPONPROF"))
				{
					selectedList.add("AUTO:" + key);
				}
			}
		}

		if (anEditType != EditorConstants.EDIT_CLASS)
		{
			for (Iterator<BonusObj> e = thisPObject.getSafeListFor(ListKey.BONUS).iterator(); e.hasNext();)
			{
				// updated 18 Jul 2003 -- sage_sam and 9 apr 2005 -- hunterc
				final BonusObj bonus = e.next();

				if (!parseSynergyBonus(bonus, null, null))
				{
					selectedList.add("BONUS:" + bonus.getPCCText()); //Formats the items in the proper .lst format
				}
			}
		}

		if (thisPObject.hasPrerequisites())
		{
			for (Prerequisite p : thisPObject.getPrerequisiteList())
			{
				// TODO: This hack is here because there is currently no
				// specific Prerequisite editor. The code curently relies
				// on the assumption that input format==in memory format== output format
				// This assumption is WRONG
				StringWriter writer = new StringWriter();
				PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
				try
				{
					prereqWriter.write(writer, p);
				} catch (PersistenceLayerException e1)
				{
					e1.printStackTrace();
				}

				selectedList.add(writer.toString());
			}
		}

		if (anEditType != EditorConstants.EDIT_CLASS)
		{
			for (VariableKey vk : thisPObject.getVariableKeys())
			{
				selectedList.add("DEFINE:" + vk.toString() + "|" + thisPObject.get(vk));
			}
		}

		List<SpecialAbility> saList = thisPObject.getListFor(ListKey.SAB);

		if (saList != null && (saList.size() != 0) && (anEditType != EditorConstants.EDIT_CLASS))
		{
			for (Iterator<SpecialAbility> e = saList.iterator(); e.hasNext();)
			{
				selectedList.add("SAB:" + e.next().toString());
			}
		}

		String[] drs = Globals.getContext().unparseSubtoken(thisPObject, "DR");

		if (drs != null)
		{
			for (String dr : drs)
			{
				selectedList.add("DR:" + dr);
			}
		}

		if (anEditType != EditorConstants.EDIT_CLASS)
		{
			String[] unparse = Globals.getContext().unparseSubtoken(thisPObject, "SPELLS");
			if (unparse != null)
			{
				for (String s : unparse)
				{
					selectedList.add("SPELLS:" + s);
				}
			}
		}

		SpellResistance sr = thisPObject.get(ObjectKey.SR);

		if (sr != null)
		{
			selectedList.add("SR:" + sr.getLSTformat());
		}

		if (anEditType != EditorConstants.EDIT_DOMAIN)
		{
			String[] spellSupportObj = Globals.getContext().unparseSubtoken(thisPObject, "SPELLLEVEL");
			if (spellSupportObj != null)
			{
				selectedList.addAll(Arrays.asList(spellSupportObj));
			}
		}

		if (anEditType == EditorConstants.EDIT_CLASS)
		{
			String[] unparse = Globals.getContext().unparseSubtoken(thisPObject, "ADD");
			if (unparse != null)
			{
				for (String s : unparse)
				{
					selectedList.add("ADD:" + s);
				}
			}
		}

		//KEY
		//QUALIFY
		//UDAM
		//UMULT
		// VISION
		String vision[] = Globals.getContext().unparseToken(thisPObject, "VISION");
		if (vision != null)
		{
			for (String tag : vision)
			{
				selectedList.add(tag);
			}
		}

		return selectedList;
	}

	public static void clearSpellListInfo(PObject po)
	{
		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> modLists = po.getModifiedLists();
		for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : modLists)
		{
			if (ref.getReferenceClass().equals(ClassSpellList.class)
					|| ref.getReferenceClass().equals(DomainSpellList.class))
			{
				po.removeAllFromList(ref);
			}
		}
	}

}
