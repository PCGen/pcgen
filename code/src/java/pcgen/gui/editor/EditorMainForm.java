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

import pcgen.core.*;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.StringWriter;
import java.util.*;
import java.util.List;

/**
 * <code>EditorMainForm</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class EditorMainForm extends JDialog
{
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
// HITDICE
// HITDIE
// HITDICEADVANCEMENT
// INIT
// LANGBONUS
// LANGNUM
// LEGS
// LEVELADJUSTMENT
// MFEAT
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
	private static final String[] qualifiers = new String[]{ "(None)", "VARDEFINED" };

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
	private QualifiedAvailableSelectedPanel pnlFollowers;
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
	public EditorMainForm(JFrame parent, PObject argPObject, int argEditType)
		throws Exception
	{
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

	private static void addVariables(List availableList, Collection objList)
	{
		for (Iterator e = objList.iterator(); e.hasNext();)
		{
			final Object obj = e.next();

			if (obj instanceof PObject)
			{
				PObject pobj = (PObject) obj;

				for (Iterator i = pobj.getVariableIterator(); i.hasNext();)
				{
					Variable var = (Variable) i.next();

					if (!var.getUpperName().startsWith("LOCK.") && !availableList.contains(var.getName()))
					{
						availableList.add(var.getName());
					}
				}
			}
			else
			{
				Logging.errorPrint(PropertyFactory.getString("in_demEr1") + ": " + obj.getClass().getName());
			}
		}
	}

	/** Closes the dialog */
	private void closeDialog()
	{
		setVisible(false);
		dispose();
	}

	//
	// User has changed the selection in the qualifier combo. If they've selected "(None)" then disable choosing from the variable name
	// combo. Otherwise enable it
	//
	private void cmbQualifierItemStateChanged()
	{
		final String qualifier = (String) pnlFollowers.getQualifierSelectedItem();

		if ((qualifier != null) && !"(None)".equalsIgnoreCase(qualifier))
		{
			pnlFollowers.setVariableEnabled(true);
		}
		else
		{
			pnlFollowers.setVariableEnabled(false);
		}
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
	// Followers tab
	//
	private String decodeFollowerEntry(String entry)
	{
		int idx = -1;

		if (entry.indexOf('[') >= 0)
		{
			for (int j = 0; j < pnlFollowers.getQualifierItemCount(); ++j)
			{
				final String qualifier = " [" + (String) pnlFollowers.getQualifierItemAt(j) + ":";
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

	///////////////////////
	// Synergy tab
	//

/*    private void buildSynergyTab()
   {
	   pnlSynergy.setExtraLayout(new GridBagLayout());
	   lblQualifier.setText(PropertyFactory.getString("in_demSkillRank"));
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
		   lblVariable.setText(PropertyFactory.getString("in_demSynergyBonus"));
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
			entry = entry.substring(idx+2);
		}

		return Globals.getSpellKeyed(entry);
	}

	private String encodeFollowerEntry(String newEntry)
	{
		String condition = null;
		final String qualifier = (String) pnlFollowers.getQualifierSelectedItem();

		if ((qualifier != null) && !"(None)".equalsIgnoreCase(qualifier))
		{
			condition = " [" + qualifier + ":" + (String) pnlFollowers.getVariableSelectedItem() + ']';
		}

		if (condition != null)
		{
			newEntry += condition;
		}

		return newEntry;
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
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_demMes1"), Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		Object[] sel;

		thisPObject.setName(aString);

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
			final SourceEntry source = thisPObject.getSourceEntry();
			source.setPageNumber(aString);
		}

		//
		// Save P.I. flag
		//
		thisPObject.setNameIsPI(pnlMainTab.getProductIdentity());

		pnlMainTab.updateData(thisPObject);

		thisPObject.getBonusList().clear();
		thisPObject.clearVariableList();
//		thisPObject.setDR(".CLEAR");
		thisPObject.clearDR();
		thisPObject.clearPreReq();
		thisPObject.clearSpecialAbilityList();
		thisPObject.clearSRList();
		thisPObject.getSpellSupport().clearSpellList();
		thisPObject.clearAutoList();

		SpellSupport spellSupport = thisPObject.getSpellSupport();
		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:

				//
				// Save granted domains
				//
				Deity deity = (Deity) thisPObject;
				if (pnlDomains.getAvailableList().length == 0)
				{
					deity.setDomainList(null);
				}
				else
				{
					sel = pnlDomains.getSelectedList();
					Domain[] domains = (Domain[])sel;
					deity.setDomainList( CoreUtility.arrayToList(domains) );
				}

				//
				// Save follower alignments
				//
				sel = pnlFollowers.getSelectedList();

				StringBuffer tbuf = new StringBuffer(100);

				for (int i = 0; i < sel.length; ++i)
				{
					String qualifier = null;
					aString = (String) sel[i];

					int idx = aString.indexOf(" [VARDEFINED:");

					if (idx >= 0)
					{
						qualifier = aString.substring(idx + 1);

						if (qualifier.endsWith("]"))
						{
							qualifier = qualifier.substring(0, qualifier.length() - 1);
						}

						qualifier = qualifier.replace(':', '=');
						aString = aString.substring(0, idx);
					}

					for (int align = 0; align < SettingsHandler.getGame().getUnmodifiableAlignmentList().size(); ++align)
					{
						if (aString.equals(SettingsHandler.getGame().getLongAlignmentAtIndex(align)))
						{
							if (qualifier != null)
							{
								tbuf.append(qualifier).append('=');
							}

							tbuf.append(align);

							if (qualifier != null)
							{
								tbuf.append(']');
							}
						}
					}
				}

				((Deity) thisPObject).setFollowerAlignments(tbuf.toString());

				//
				// Save racial worshippers (no need to explicitly clear)
				//
				sel = pnlRaces.getSelectedList();
				List raceArray = new ArrayList(sel.length);
				for (int i = 0; i < sel.length; i++)
				{
					raceArray.add( ((Race)sel[i]).getKeyName() );
				}
				((Deity) thisPObject).setRacePantheonList(raceArray);

				break;

			case EditorConstants.EDIT_DOMAIN:

				//
				// Save feats
				//
				((Domain)thisPObject).addFeat(".CLEAR");
				sel = pnlFeats.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				((Domain)thisPObject).addFeat(aString);

				sel = pnlQSpells.getSelectedList();
				if (thisPObject.isNewItem())
					thisPObject.setNewItem(false);
				spellSupport.clearSpellLevelMap();
				thisPObject.getSpellSupport().clearSpellInfoMap();

				for (int i = 0; i < sel.length; ++i)
				{
					aString = sel[i].toString();
					final int idx = aString.indexOf('=');
					final String domainKey = thisPObject.getKeyName(), spellName, spellLevel;
					if (idx > 0)
					{
						spellName = aString.substring(idx+2);
						spellLevel = aString.substring(idx-1,idx);
						spellSupport.putLevel("DOMAIN", domainKey, spellName, spellLevel);
						spellSupport.putInfo("DOMAIN", spellName, domainKey, spellLevel);
					}
				}

				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:

				Race thisRace = (Race) thisPObject;

				//thisRace.setMovements(pnlMovement.getMoveRates());
				Movement cm = Movement.getMovementFrom(pnlMovement.getMoveValues());
				cm.setMovementTypes(pnlMovement.getMoveTypes());
				thisRace.setMovement(cm);
				
				thisRace.clearVisionList();
				List<Vision> visionList = pnlVision.getVision();
				for (Vision vis : visionList) 
				{
					thisRace.addVision(vis);
				}
				
				thisRace.setNaturalWeapons(pnlNaturalAttacks.getNaturalWeapons());
				pnlAppearance.updateData(thisPObject);
				pnlAge.updateData(thisPObject);

				//
				// Save granted templates
				//
				thisRace.addTemplate(".CLEAR");
				sel = pnlTemplates.getSelectedList();

				for (int index = 0; index < sel.length; index++)
				{
					thisRace.addTemplate((String) sel[index]);
				}

				//
				// Save choice of templates
				//
				sel = pnlTemplates.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');

				if ((aString != null) && (aString.length() > 0))
				{
					thisRace.addTemplate("CHOOSE:" + aString);
				}

				//
				// Save favoured classes
				//
				sel = pnlClasses.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setFavoredClass(aString);

				//
				// Save choice of auto languages
				//
				sel = pnlLanguages.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setChooseLanguageAutos(aString);

				//
				// Save feats
				//
				sel = pnlFeats.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setFeatList(aString);
				sel = pnlFeats.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				thisRace.setMFeatList(aString);

				//
				// Save virtual feats
				//
				/*
				   sel = pnlVFeats.getSelectedList();
				   aString = EditUtil.delimitArray(sel, '|');
				   thisRace.setVFeatList(aString);
				 */
				//
				// Save bonus languages
				//
				thisRace.setLanguageBonus(".CLEAR");
				sel = pnlBonusLang.getSelectedList();
				aString = EditUtil.delimitArray(sel, ',');
				thisRace.setLanguageBonus(aString);

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
						thisPObject.addBonusList(sb.toString());
					}
					else
					{
						Logging.errorPrint("Synergy has invalid format: " + sel[i].toString());
					}
				}

				((Skill) thisPObject).getClassList().clear();
				sel = pnlClasses.getSelectedList2();

				for (int i = 0; i < sel.length; ++i)
				{
					((Skill) thisPObject).addClassList('!' + sel[i].toString());
				}

				sel = pnlClasses.getSelectedList();

				for (int i = 0; i < sel.length; ++i)
				{
					((Skill) thisPObject).addClassList(sel[i].toString());
				}

				break;

			case EditorConstants.EDIT_SPELL:
				((SpellBasePanel2) pnlBase2).updateData(thisPObject);
				((Spell) thisPObject).setLevelInfo(".CLEAR", 0);
				sel = pnlQClasses.getSelectedList();

				for (int i = 0; i < sel.length; ++i)
				{
					aString = sel[i].toString();

					final int idx = aString.indexOf('=');

					if (idx > 0)
					{
						((Spell) thisPObject).setLevelInfo("CLASS|" + aString.substring(0, idx),
							aString.substring(idx + 1));
					}
				}

				sel = pnlQDomains.getSelectedList();

				for (int i = 0; i < sel.length; ++i)
				{
					aString = sel[i].toString();

					final int idx = aString.indexOf('=');

					if (idx > 0)
					{
						((Spell) thisPObject).setLevelInfo("DOMAIN|" + aString.substring(0, idx),
							aString.substring(idx + 1));
					}
				}

				break;

			case EditorConstants.EDIT_TEMPLATE:

				PCTemplate thisPCTemplate = (PCTemplate) thisPObject;
				Movement cmv = Movement.getMovementFrom(pnlMovement.getMoveValues());
				cmv.setMoveRatesFlag(pnlMovement.getMoveRateType());
				thisPCTemplate.setMovement(cmv);

				thisPCTemplate.clearVisionList();
				List<Vision> tplVisionList = pnlVision.getVision();
				for (Vision vis : tplVisionList) 
				{
					thisPCTemplate.addVision(vis);
				}

				//
				// Save granted templates
				//
				thisPCTemplate.addTemplate(".CLEAR");
				sel = pnlTemplates.getSelectedList();

				for (int index = 0; index < sel.length; index++)
				{
					thisPCTemplate.addTemplate((String) sel[index]);
				}

				//
				// Save choice of templates
				//
				sel = pnlTemplates.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');

				if ((aString != null) && (aString.length() > 0))
				{
					thisPCTemplate.addTemplate("CHOOSE:" + aString);
				}

				//
				// Save favoured classes
				//
				sel = pnlClasses.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisPCTemplate.setFavoredClass(aString);

				//
				// Save choice of auto languages
				//
				sel = pnlLanguages.getSelectedList2();
				aString = EditUtil.delimitArray(sel, '|');
				thisPCTemplate.setChooseLanguageAutos(aString);

				//
				// Save feats
				//
				thisPCTemplate.addFeatString(".CLEAR");
				sel = pnlFeats.getSelectedList();
				aString = EditUtil.delimitArray(sel, '|');
				thisPCTemplate.addFeatString(aString);

				//
				// Save bonus languages
				//
				thisPCTemplate.setLanguageBonus(".CLEAR");
				sel = pnlBonusLang.getSelectedList();
				aString = EditUtil.delimitArray(sel, ',');
				thisPCTemplate.setLanguageBonus(aString);

				//
				// Save level and hit dice abilities
				//
				thisPCTemplate.addHitDiceString(".CLEAR");
				thisPCTemplate.clearLevelAbilities();
				sel = pnlLevelAbilities.getSelectedList();

				for (int index = 0; index < sel.length; index++)
				{
					aString = (String) sel[index];

					if (aString.startsWith("HD:"))
					{
						thisPCTemplate.addHitDiceString(aString.substring(3));
					}
					else if (aString.startsWith("LEVEL:"))
					{
						final StringTokenizer tok = new StringTokenizer(aString, ":");
						tok.nextToken(); // Lose the LEVEL:
						final String levelStr = tok.nextToken();
						final int level;
						try
						{
							level = Integer.parseInt(levelStr);
						}
						catch (NumberFormatException ex)
						{
							// TODO - Add error message.
							continue;
						}
						final String typeStr = tok.nextToken();

						thisPCTemplate.addLevelAbility(level, typeStr, tok.nextToken());
					}
				}

				break;

			case EditorConstants.EDIT_CAMPAIGN:
				pnlFileTypes.updateData(thisPObject);

				break;

			case EditorConstants.EDIT_CLASS:
				PCClass thisPCClass = (PCClass) thisPObject;
				thisPCClass.clearTemplates();
				thisPCClass.addUmult(".CLEAR");
				thisPCClass.clearFeatAutos();
				thisPCClass.addUdamList(".CLEAR");
				break;

			default:
				break;
		}

		//
		// Save granted languages
		//
		if (pnlLanguages != null)
		{
			thisPObject.clearLanguageAuto();
			sel = pnlLanguages.getSelectedList();
			for (int i = 0; i < sel.length; i++)
			{
				thisPObject.addLanguageAuto(sel[i].toString());
			}
		}

		//
		// Save auto weapon proficiencies
		//
		if (pnlWeapons != null)
		{
			thisPObject.clearAutoListForTag("WEAPONPROF");
			sel = pnlWeapons.getSelectedList();
			for (int i = 0; i < sel.length; i++)
			{
				thisPObject.addAutoArray("WEAPONPROF|" + sel[i]);
			}

			sel = pnlWeapons.getSelectedList2();

			if (sel != null)
			{
				if (editType == EditorConstants.EDIT_CLASS
				  || editType == EditorConstants.EDIT_RACE)
				{
					for ( int i = 0; i < sel.length; i++ )
					{
						thisPObject.addWeaponProfBonus( (String)sel[i] );
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
			thisPObject.clearCSkills();
			sel = pnlSkills.getSelectedList();
			for(int i = 0; i < sel.length; i++) {
				thisPObject.addCSkill(sel[i].toString());
			}

			//
			// Save granted cross class skills
			//
			thisPObject.clearCcSkills();
			sel = pnlSkills.getSelectedList2();
			for(int i = 0; i < sel.length; i++) {
				thisPObject.addCcSkill(aString);
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
			case EditorConstants.EDIT_DEITY:
				((Deity) thisPObject).getPantheonList().clear();

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
				((Skill) thisPObject).setChoiceString(null);

				break;

			case EditorConstants.EDIT_SPELL:
				break;

			case EditorConstants.EDIT_TEMPLATE:
				break;

			default:
				break;
		}

		if (editType == EditorConstants.EDIT_SKILL)
		{
			((Skill) thisPObject).setChoiceString(null);
		}
		else if ((editType == EditorConstants.EDIT_DOMAIN) || (editType == EditorConstants.EDIT_FEAT))
		{
			thisPObject.setChoiceString("");
		}

		sel = pnlAdvanced.getSelectedList();

		if (editType == EditorConstants.EDIT_CLASS)
		{
			pnlClassAbility.updateData(thisPObject);
			pnlClassLevel.updateData(thisPObject);
		}

		for (int i = 0; i < sel.length; ++i)
		{
			aString = (String) sel[i];
			try
			{
				PObjectLoader.parseTag(thisPObject, aString);
			}
			catch (PersistenceLayerException ple)
			{
				Logging.errorPrint(ple.getMessage(), ple);
			}

			if ((editType == EditorConstants.EDIT_DEITY) && (aString.startsWith("PANTHEON:")))
			{
				((Deity) thisPObject).addPantheon(aString.substring(9));
			}
		}

		thisPObject.setTypeInfo(Constants.s_CUSTOM);

		wasCancelled = false;
		closeDialog();
	}

	private void initComponentContents()
	{
		Iterator e;
		String aString;
		StringTokenizer aTok;
		List availableList = new ArrayList();
		List selectedList = new ArrayList();
		List selectedList2 = new ArrayList();
		List aList;
		List movementValues;
		List<String> visionValues;
		List<Vision> vision;
		List naturalAttacks;

		pnlMainTab.setNameText(thisPObject.getKeyName());
		pnlMainTab.setProductIdentity(thisPObject.getNameIsPI());
		pnlMainTab.setSourceText(thisPObject.getSourceEntry().getPageNumber());

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
				availableList.clear();
				selectedList.clear();

				for (e = SettingsHandler.getGame().getUnmodifiableAlignmentList().iterator(); e.hasNext();)
				{
					final PCAlignment anAlignment = (PCAlignment) e.next();

					if (anAlignment.isValidForFollower())
					{
						availableList.add(anAlignment.getKeyName());
					}
				}

				final String followerAlignments = ((Deity) thisPObject).getFollowerAlignments();
				parseAlignment(availableList, selectedList, followerAlignments, null);

				pnlFollowers.setAvailableList(availableList, true);
				pnlFollowers.setSelectedList(selectedList, true);

				//
				// Initialize the contents of the available and selected domains lists
				//
				selectedList.clear();
				availableList.clear();

				for (e = Globals.getDomainList().iterator(); e.hasNext();)
				{
					final Domain aDomain = (Domain) e.next();

					if (((Deity) thisPObject).hasDomain(aDomain))
					{
						selectedList.add(aDomain);
					}
					else
					{
						availableList.add(aDomain);
					}
				}

				pnlDomains.setAvailableList(availableList, true);
				pnlDomains.setSelectedList(selectedList, true);

				//
				// Initialize the contents of the available and selected races list
				//
				selectedList.clear();
				availableList.clear();

				final List raceList = ((Deity) thisPObject).getRacePantheonList();

				for ( final Race race : Globals.getAllRaces() )
				{
					final String raceName = race.getKeyName();

					if (!raceName.equals(Constants.s_NONESELECTED))
					{
						if (raceList.contains(raceName))
						{
							selectedList.add(race);
						}
						else
						{
							availableList.add(race);
						}
					}
				}

				pnlRaces.setAvailableList(availableList, true);
				pnlRaces.setSelectedList(selectedList, true);

				break;

			case EditorConstants.EDIT_DOMAIN:

				//
				// Populate the feats available list and selected lists
				//
				availableList.clear();
				selectedList.clear();

				for (e = Globals.getAbilityKeyIterator("FEAT"); e.hasNext();)
				{
					final Ability anAbility = (Ability) e.next();
					availableList.add(anAbility.getKeyName());
				}

				for (Iterator<Categorisable> iter = ((Domain) thisPObject).getFeatIterator(); iter.hasNext();)
				{
					Ability ability = (Ability)iter.next();
					aString = ability.getKeyName();

					if (!selectedList.contains(aString))
					{
						availableList.remove(aString);
						selectedList.add(aString);
					}
				}

				pnlFeats.setAvailableList(availableList, true);
				pnlFeats.setSelectedList(selectedList, true);

				availableList.clear();
				selectedList.clear();

				SpellSupport spellSupt = thisPObject.getSpellSupport();
				if (thisPObject.isNewItem())
				{
					spellSupt.clearSpellInfoMap();
					spellSupt.clearSpellLevelMap();
					for (e = Globals.getSpellMap().values().iterator(); e.hasNext();)
					{
						final Object obj = e.next();

						if (obj instanceof Spell)
						{
							availableList.add(obj);
						}
					}
				}
				else
				{
					availableList = new ArrayList(Globals.getSpellMap().values().size());

					for (e = Globals.getSpellMap().values().iterator(); e.hasNext();)
					{
						final Object obj = e.next();

						if (obj instanceof Spell)
						{
							String spellName = obj.toString();

							if (spellSupt.containsInfoFor("DOMAIN", spellName))
							{
								int i = spellSupt.getInfo("DOMAIN", spellName).level;
								selectedList.add(encodeSpellEntry(obj.toString(), Integer.toString(i)));
							}
							else
							{
								availableList.add(obj);
							}
						}
					}

				}
				Globals.sortPObjectList(availableList);

				pnlQSpells.setAvailableList(availableList, true);
				pnlQSpells.setSelectedList(selectedList, true);

				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:

				//
				// Populate the templates available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();

				for (e = Globals.getTemplateList().iterator(); e.hasNext();)
				{
					final PCTemplate aTemplate = (PCTemplate) e.next();
					aString = aTemplate.getKeyName();

					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}

				//
				// remove this race's granted templates from the available list and place into selected list
				//
				moveGrantedTemplatesFromAvailableToSelected(((Race) thisPObject).getTemplateList(), selectedList,
					selectedList2, availableList);

				pnlTemplates.setAvailableList(availableList, true);
				pnlTemplates.setSelectedList(selectedList, true);
				pnlTemplates.setSelectedList2(selectedList2, true);

				//
				// Populate the favoured classes available list and selected lists
				//
				availableList.clear();
				selectedList.clear();

				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					availableList.add(aClass.getKeyName());
				}

				availableList.add("Any");
				aString = ((Race) thisPObject).getFavoredClass();
				aTok = new StringTokenizer(aString, "|", false);

				while (aTok.hasMoreTokens())
				{
					String favouredClass = aTok.nextToken();

					if (!selectedList.contains(favouredClass))
					{
						final int idx = availableList.indexOf(favouredClass);

						if (idx < 0)
						{
							Logging.errorPrint("Unknown class: " + favouredClass);

							continue;
						}

						availableList.remove(idx);
						selectedList.add(favouredClass);
					}
				}

				pnlClasses.setAvailableList(availableList, true);
				pnlClasses.setSelectedList(selectedList, true);

				//
				// Populate the feats available list and selected lists
				//
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();

				for (e = Globals.getAbilityKeyIterator("FEAT"); e.hasNext();)
				{
					final Ability anAbility = (Ability) e.next();
					availableList.add(anAbility.getKeyName());
				}

				aString = ((Race) thisPObject).getFeatList(null, false);
				aTok = new StringTokenizer(aString, "|", false);

				while (aTok.hasMoreTokens())
				{
					String featName = aTok.nextToken();

					if (!selectedList.contains(featName))
					{
						availableList.remove(featName);
						selectedList.add(featName);
					}
				}

				aString = ((Race) thisPObject).getMFeatList();
				aTok = new StringTokenizer(aString, "|", false);

				while (aTok.hasMoreTokens())
				{
					String featName = aTok.nextToken();

					if (!selectedList2.contains(featName))
					{
						availableList.remove(featName);
						selectedList2.add(featName);
					}
				}

				pnlFeats.setAvailableList(availableList, true);
				pnlFeats.setSelectedList(selectedList, true);
				pnlFeats.setSelectedList2(selectedList2, true);

				//
				// Populate the virtual feats available list and selected list
				//
				availableList.clear();
				selectedList.clear();

				for (e = Globals.getAbilityKeyIterator("FEAT"); e.hasNext();)
				{
					final Ability anAbility = (Ability) e.next();
					availableList.add(anAbility.getKeyName());
				}

				for (e = ((Race) thisPObject).getVirtualFeatList().iterator(); e.hasNext();)
				{
					final Ability aFeat = (Ability) e.next();
					String featName = aFeat.getKeyName();

					if (!selectedList.contains(featName))
					{
						availableList.remove(featName);
						selectedList.add(featName);
					}
				}

				pnlVFeats.setAvailableList(availableList, true);
				pnlVFeats.setSelectedList(selectedList, true);

				//
				// Populate the bonus languages available list and selected lists
				//
				selectedList.clear();
				availableList.clear();
				final Set<Language> langs = thisPObject.getLanguageBonus();

				for (e = Globals.getLanguageList().iterator(); e.hasNext();)
				{
					final Language aLang = (Language) e.next();

					if (langs.contains(aLang))
					{
						selectedList.add(aLang);
					}
					else
					{
						availableList.add(aLang);
					}
				}

				pnlBonusLang.setAvailableList(availableList, true);
				pnlBonusLang.setSelectedList(selectedList, true);

				//
				// Populate the movement panel
				//
				movementValues = new ArrayList();

				Movement cm = thisPObject.getMovement();

				if (cm != null && cm.getNumberOfMovementTypes() > 0)
				{
					for (int index = 0; index < cm.getNumberOfMovementTypes(); index++)
					{
						final String aMove = MovementPanel.makeMoveString(cm.getMovementType(index),
								cm.getMovement(index), null, null);
						movementValues.add(aMove);
					}
				}

				pnlMovement.setSelectedList(movementValues);

				//
				// Populate the vision panel
				//
				vision = thisPObject.getVision();
				visionValues = buildVisionValues(vision);
				pnlVision.setSelectedList(visionValues);

				//
				// Populate the natural attacks panel
				//
				naturalAttacks = thisPObject.getNaturalWeapons();
				pnlNaturalAttacks.setSelectedList(naturalAttacks);

				//
				// Populate the appearance panel
				//
				List eyeColorList = new ArrayList();
				List hairColorList = new ArrayList();
				List skinToneList = new ArrayList();

				for ( final Race race : Globals.getAllRaces() )
				{
//					final String raceName = (String) e.next();
					aString = race.getRegionString();

					if (aString == null)
					{
						aString = Constants.s_NONE;
					}

					aList = Globals.getBioSet().getTagForRace(aString, race.getKeyName(), "HAIR");

					if (aList != null)
					{
						for (Iterator ai = aList.iterator(); ai.hasNext();)
						{
							String as = (String) ai.next();
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
						for (Iterator ai = aList.iterator(); ai.hasNext();)
						{
							String as = (String) ai.next();
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
				pnlAppearance.updateView(thisPObject);

				//
				// Populate the age panel
				//
				pnlAge.updateView(thisPObject);

				break;

			case EditorConstants.EDIT_SKILL:
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();

				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					availableList.add(aClass.getKeyName());
				}

				boolean negate;

				for (e = ((Skill) thisPObject).getClassList().iterator(); e.hasNext();)
				{
					aString = (String) e.next();

					if (aString.length() > 0)
					{
						if (aString.charAt(0) == '!')
						{
							aString = aString.substring(1);
							negate = true;
						}
						else
						{
							negate = false;
						}

						final int idx = availableList.indexOf(aString);

						if (idx < 0)
						{
							Logging.errorPrint("Unknown class: " + aString);

							continue;
						}

						availableList.remove(idx);

						if (negate)
						{
							selectedList2.add(aString);
						}
						else
						{
							selectedList.add(aString);
						}
					}
				}

				pnlClasses.setAvailableList(availableList, true);
				pnlClasses.setSelectedList(selectedList, true);
				pnlClasses.setSelectedList2(selectedList2, true);
				pnlClasses.setLblSelectedText("Class Skill");
				pnlClasses.setLblSelected2Text("Not allowed");

				break;

			case EditorConstants.EDIT_SPELL:
				((SpellBasePanel2) pnlBase2).updateView(thisPObject);

				Map lvlInfo = ((Spell) thisPObject).getLevelInfo(null);

				//
				// Initialize the contents of the available and selected domains lists
				//
				int iCount = 0;
				selectedList.clear();
				availableList.clear();

				for (e = Globals.getDomainList().iterator(); e.hasNext();)
				{
					final Domain aDomain = (Domain) e.next();
					Integer lvl = null;

					if (lvlInfo != null)
					{
						lvl = (Integer) lvlInfo.get("DOMAIN|" + aDomain.getKeyName());
					}

					if (lvl != null)
					{
						selectedList.add(encodeDomainEntry(aDomain.getKeyName(), lvl.toString()));
						++iCount;
					}
					else
					{
						availableList.add(aDomain.getKeyName());
					}
				}

				pnlQDomains.setAvailableList(availableList, true);
				pnlQDomains.setSelectedList(selectedList, true);

				//
				// Initialize the contents of the available and selected classes lists
				//
				selectedList.clear();
				availableList.clear();

				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					Integer lvl = null;

					if (lvlInfo != null)
					{
						lvl = (Integer) lvlInfo.get("CLASS|" + aClass.getKeyName());
					}

					if (lvl != null)
					{
						selectedList.add(encodeDomainEntry(aClass.getKeyName(), lvl.toString()));
						++iCount;
					}
					else
					{
						availableList.add(aClass.getKeyName());
					}
				}

				pnlQClasses.setAvailableList(availableList, true);
				pnlQClasses.setSelectedList(selectedList, true);

				//
				// Inform the user if there is a domain/class defined for the spell that was not found
				//
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
				availableList.clear();
				selectedList.clear();
				selectedList2.clear();

				for (e = Globals.getTemplateList().iterator(); e.hasNext();)
				{
					final PCTemplate aTemplate = (PCTemplate) e.next();
					aString = aTemplate.getKeyName();

					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}

				//
				// remove this template's granted templates from the available list and place into selected list
				//
				moveGrantedTemplatesFromAvailableToSelected(((PCTemplate) thisPObject).getTemplateList(), selectedList,
					selectedList2, availableList);

				pnlTemplates.setAvailableList(availableList, true);
				pnlTemplates.setSelectedList(selectedList, true);
				pnlTemplates.setSelectedList2(selectedList2, true);

				//
				// Populate the favoured classes available list and selected lists
				//
				availableList.clear();
				selectedList.clear();

				for (e = Globals.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();
					availableList.add(aClass.getKeyName());
				}

				availableList.add("Any");
				aString = ((PCTemplate) thisPObject).getFavoredClass();
				aTok = new StringTokenizer(aString, "|", false);

				while (aTok.hasMoreTokens())
				{
					String favouredClass = aTok.nextToken();

					if (!selectedList.contains(favouredClass))
					{
						final int idx = availableList.indexOf(favouredClass);

						if (idx < 0)
						{
							Logging.errorPrint("Unknown class: " + favouredClass);

							continue;
						}

						availableList.remove(idx);
						selectedList.add(favouredClass);
					}
				}

				pnlClasses.setAvailableList(availableList, true);
				pnlClasses.setSelectedList(selectedList, true);

				//
				// Populate the feats available list and selected lists
				//
				availableList.clear();
				selectedList.clear();

				for (e = Globals.getAbilityKeyIterator("FEAT"); e.hasNext();)
				{
					final Ability anAbility = (Ability) e.next();
					availableList.add(anAbility.getKeyName());
				}

				List featList = ((PCTemplate) thisPObject).feats(-1, -1, null, false);

				for (e = featList.iterator(); e.hasNext();)
				{
					aString = (String) e.next();

					if (!selectedList.contains(aString))
					{
						availableList.remove(aString);
						selectedList.add(aString);
					}
				}

				pnlFeats.setAvailableList(availableList, true);
				pnlFeats.setSelectedList(selectedList, true);

				//
				// Populate the movement panel
				//
				movementValues = new ArrayList();

				Movement cmv = thisPObject.getMovement();

				if (cmv != null)
				{
					if (cmv.getNumberOfMovementTypes() > 0)
					{
						for (int index = 0; index < cmv.getNumberOfMovementTypes(); index++)
						{
							final String aMove = MovementPanel.makeMoveString(
								cmv.getMovementType(index),
								cmv.getMovement(index),
								cmv.getMovementMult(index),
								cmv.getMovementMultOp(index));
							movementValues.add(aMove);
						}
					}
					pnlMovement.setMoveRateType(cmv.getMoveRatesFlag());
				}

				pnlMovement.setSelectedList(movementValues);

				//
				// Populate the vision panel
				//
				vision = thisPObject.getVision();
				visionValues = buildVisionValues(vision);
				pnlVision.setSelectedList(visionValues);

				//
				// Populate the specialabilities panel
				//
				selectedList.clear();

				selectedList.addAll(((PCTemplate)thisPObject).getLevelAbilities());

				final List<String >specialabilitiesList = ((PCTemplate) thisPObject).getHitDiceStrings();

				if (specialabilitiesList != null)
				{
					for (e = specialabilitiesList.iterator(); e.hasNext();)
					{
						aString = (String) e.next();
						selectedList.add("HD:" + aString);
					}
				}

				pnlLevelAbilities.setSelectedList(selectedList);

				//
				// Populate the bonus languages available list and selected lists
				//
				selectedList.clear();
				availableList.clear();

				final Set aSet = ((PCTemplate) thisPObject).getLanguageBonus();

				for (e = Globals.getLanguageList().iterator(); e.hasNext();)
				{
					final Language aLang = (Language) e.next();

					if (aSet.contains(aLang))
					{
						selectedList.add(aLang);
					}
					else
					{
						availableList.add(aLang);
					}
				}

				pnlBonusLang.setAvailableList(availableList, true);
				pnlBonusLang.setSelectedList(selectedList, true);

				break;

			case EditorConstants.EDIT_CAMPAIGN:
				pnlFileTypes.updateView(thisPObject);

				break;

			default:
				break;
		}

		//
		// Initialize the contents of the available and selected languages lists
		//
		if (pnlLanguages != null)
		{
			selectedList.clear();
			selectedList2.clear();
			availableList.clear();

			final Collection aSet = thisPObject.getSafeListFor(ListKey.AUTO_LANGUAGES);

			for (e = Globals.getLanguageList().iterator(); e.hasNext();)
			{
				final Language aLang = (Language) e.next();

				if (aSet.contains(aLang))
				{
					selectedList.add(aLang);
				}
				else
				{
					availableList.add(aLang);
				}
			}

			if ((editType == EditorConstants.EDIT_TEMPLATE) || (editType == EditorConstants.EDIT_RACE))
			{
				if (editType == EditorConstants.EDIT_TEMPLATE)
				{
					aString = ((PCTemplate) thisPObject).getChooseLanguageAutos();
				}
				else
				{
					aString = ((Race) thisPObject).getChooseLanguageAutos();
				}

				aTok = new StringTokenizer(aString, "|", false);

				while (aTok.hasMoreTokens())
				{
					final Language aLang = Globals.getLanguageKeyed(aTok.nextToken());

					if (aLang != null)
					{
						selectedList2.add(aLang);
						availableList.remove(aLang);
					}
				}

				pnlLanguages.setSelectedList2(selectedList2, true);
			}

			pnlLanguages.setAvailableList(availableList, true);
			pnlLanguages.setSelectedList(selectedList, true);
		}

		//
		// Initialize the contents of the available and selected weapon prof lists
		//
		if (pnlWeapons != null)
		{
			selectedList.clear();
			String allProfNames = Globals.getWeaponProfNames("|", false);
			String[] profNames = allProfNames.split("\\|");
			availableList.clear();
			for (int i = 0; i < profNames.length; i++)
			{
				availableList.add(profNames[i]);
			}
			final Set wpnProfTypes = Globals.getWeaponProfTypes();
			for (Iterator iter = wpnProfTypes.iterator(); iter.hasNext();)
			{
				String typeName = (String) iter.next();
				availableList.add("TYPE." + typeName.toUpperCase());
			}

			// We don't load the WeaponProfAuto list as that is composed of
			// generated things, such as natural weapon proficiencies
			final List autoWeap = new ArrayList();
			thisPObject.addAutoTagsToList("WEAPONPROF",
				autoWeap, null, false);

			for (e = autoWeap.iterator(); e.hasNext();)
			{
				moveProfToSelectedList(availableList, selectedList, (String) e.next());
			}

			if (editType == EditorConstants.EDIT_CLASS)
			{
				e = ((PCClass) thisPObject).getWeaponProfBonus().iterator();
			}
			else if (editType == EditorConstants.EDIT_RACE)
			{
				e = ((Race) thisPObject).getWeaponProfBonus().iterator();
			}
			else
			{
				e = null;
			}

			if (e != null)
			{
				selectedList2.clear();

				while (e.hasNext())
				{
					moveProfToSelectedList(availableList, selectedList2,
						(String) e.next());
				}

				pnlWeapons.setSelectedList2(selectedList2, true);
				pnlWeapons.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlWeapons.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
			}

			pnlWeapons.setAvailableList(availableList, true);
			pnlWeapons.setSelectedList(selectedList, true);
		}

		String[] values;

		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:

				//
				// initialize the Qualifier combo on the Followers tab
				//
				pnlFollowers.setQualifierModel(new DefaultComboBoxModel(qualifiers));

				//
				// Initialize the Variable combo with all the variable names we can find
				//
				availableList.clear();
				addVariables(availableList, Globals.getClassList());
				addVariables(availableList, Globals.getUnmodifiableAbilityList("FEAT"));
				addVariables(availableList, Globals.getAllRaces());
				addVariables(availableList, Globals.getSkillList());
				addVariables(availableList, EquipmentList.getModifierList());
				addVariables(availableList, Globals.getTemplateList());
				addVariables(availableList, Globals.getAllCompanionMods());
				Collections.sort(availableList);
				pnlFollowers.setVariableModel(new DefaultComboBoxModel(availableList.toArray()));

				break;

			case EditorConstants.EDIT_DOMAIN:

				//
				// Domain Spells allow levels 1 to 9
				//
				availableList.clear();

				for (int i = 1; i <= 9; ++i)
				{
					availableList.add(String.valueOf(i));
				}

				pnlQSpells.setQualifierModel(new DefaultComboBoxModel(availableList.toArray()));
				pnlQSpells.setQualifierSelectedIndex(0);

				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				break;

			case EditorConstants.EDIT_SKILL:
				availableList.clear();
				selectedList.clear();

				for (e = Globals.getSkillList().iterator(); e.hasNext();)
				{
					final Skill aSkill = (Skill) e.next();

					if (!aSkill.getKeyName().equals(thisPObject.getKeyName()))
					{
						availableList.add(aSkill.getKeyName());
					}
				}

				//
				// BONUS:SKILL|Ride|2|PRESKILL:1,Handle Animal=5|TYPE=Synergy.STACK
				//
				for (e = thisPObject.getBonusList().iterator(); e.hasNext();)
				{
					parseSynergyBonus((BonusObj) e.next(), availableList, selectedList);
				}

				pnlSynergy.setAvailableList(availableList, true);
				pnlSynergy.setSelectedList(selectedList, true);

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
				availableList.clear();

				for (int i = 1; i <= 9; ++i)
				{
					availableList.add(String.valueOf(i));
				}

				pnlQDomains.setQualifierModel(new DefaultComboBoxModel(availableList.toArray()));
				pnlQDomains.setQualifierSelectedIndex(0);

				//
				// Classes allow levels 0-9
				//
				availableList.add(0, "0");
				pnlQClasses.setQualifierModel(new DefaultComboBoxModel(availableList.toArray()));
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
			selectedList.clear();
			availableList.clear();

			for (e = Globals.getSkillList().iterator(); e.hasNext();)
			{
				final Skill aSkill = (Skill) e.next();
				aString = aSkill.getKeyName();

				if (!availableList.contains(aString))
				{
					availableList.add(aString);
				}

				for (int i = 0, x = aSkill.getMyTypeCount(); i < x; ++i)
				{
					aString = "TYPE." + aSkill.getMyType(i);

					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}

			List skills = thisPObject.getCSkillList();

			if (skills != null)
			{
				for (e = skills.iterator(); e.hasNext();)
				{
					aString = (String) e.next();
					selectedList.add(aString);

					if (availableList.contains(aString))
					{
						availableList.remove(aString);
					}
				}
			}

			pnlSkills.setSelectedList(selectedList, true);

			selectedList.clear();
			skills = thisPObject.getCcSkillList();

			if (skills != null)
			{
				for (e = skills.iterator(); e.hasNext();)
				{
					aString = (String) e.next();
					selectedList.add(aString);

					if (availableList.contains(aString))
					{
						availableList.remove(aString);
					}
				}
			}

			pnlSkills.setSelectedList2(selectedList, true);

			pnlSkills.setAvailableList(availableList, true);
		}

		pnlAdvanced.setAvailableTagList(editType);
		selectedList.clear();
		selectedList = buildAdvancedSelectedList(editType);
		pnlAdvanced.setSelected(selectedList);
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
	private void moveProfToSelectedList(List availableList, List selectedList, String profName)
	{
		if (profName.startsWith("TYPE"))
		{
			selectedList.add(profName);
			availableList.remove(profName.toUpperCase());
		}
		else
		{
			final WeaponProf wp = Globals.getWeaponProfKeyed(profName);

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
	private static void moveGrantedTemplatesFromAvailableToSelected(List templateList, List selectedList,
		List selectedList2, List availableList)
	{
		Iterator e;
		String aString;
		StringTokenizer aTok;

		for (e = templateList.iterator(); e.hasNext();)
		{
			aString = (String) e.next();

			if (aString.startsWith("CHOOSE:"))
			{
				aTok = new StringTokenizer(aString.substring(7), "|", false);

				while (aTok.hasMoreTokens())
				{
					String chooseTemplate = aTok.nextToken();

					if (!selectedList.contains(chooseTemplate))
					{
						selectedList2.add(chooseTemplate);
					}
				}
			}
			else
			{
				selectedList.add(aString);
				availableList.remove(aString);
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
				pnlClassAbility.updateView(thisPObject);
				pnlClassLevel = new ClassLevelPanel();
				pnlClassLevel.updateView(thisPObject);

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
				pnlFollowers = new QualifiedAvailableSelectedPanel("in_demQualifier", "in_demVariable",
						new EditorAddFilter()
						{
							public Object encode(Object anObj)
							{
								return encodeFollowerEntry((String) anObj);
							}

							public Object decode(Object anObj)
							{
								return decodeFollowerEntry((String) anObj);
							}
						},
						new ItemListener()
						{
							public void itemStateChanged(ItemEvent evt)
							{
								cmbQualifierItemStateChanged();
							}
						});
				pnlRaces = new AvailableSelectedPanel();

				break;

			case EditorConstants.EDIT_DOMAIN:
				pnlLanguages = new AvailableSelectedPanel();
				pnlFeats = new AvailableSelectedPanel();
				pnlSkills = new AvailableSelectedPanel(true);
				pnlWeapons = new AvailableSelectedPanel();
				pnlQSpells = new QualifiedAvailableSelectedPanel("in_demLevel", null,
						new EditorAddFilter()
						{
							public Object encode(Object anObj)
							{
								return encodeSpellEntry(pnlQSpells, anObj.toString());
							}

							public Object decode(Object anObj)
							{
								return decodeSpellEntry( anObj.toString());
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
				pnlFeats = new AvailableSelectedPanel(true);
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
						new EditorAddFilter()
						{
							public Object encode(Object anObj)
							{
								return encodeSynergyEntry((String) anObj);
							}

							public Object decode(Object anObj)
							{
								return decodeSynergyEntry((String) anObj);
							}
						}, null);

				break;

			case EditorConstants.EDIT_SPELL:
				pnlBase2 = new SpellBasePanel2();
				pnlQClasses = new QualifiedAvailableSelectedPanel("in_demLevel", null,
						new EditorAddFilter()
						{
							public Object encode(Object anObj)
							{
								return encodeDomainEntry(pnlQClasses, (String) anObj);
							}

							public Object decode(Object anObj)
							{
								return decodeDomainEntry((String) anObj);
							}
						}, null);
				pnlQDomains = new QualifiedAvailableSelectedPanel("in_demLevel", null,
						new EditorAddFilter()
						{
							public Object encode(Object anObj)
							{
								return encodeDomainEntry(pnlQDomains, (String) anObj);
							}

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

		setTitle(PropertyFactory.getString("in_demTitle" + ttl));

		addWindowListener(new WindowAdapter()
			{
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
		jTabbedPane1.setName(PropertyFactory.getString("in_demDeitytab"));
		pnlMainTab = new EditorBasePanel(editType);

		jTabbedPane1.addTab(PropertyFactory.getString("in_demBase"), pnlMainTab);

		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				jTabbedPane1.addTab(PropertyFactory.getString("in_classability"), pnlClassAbility);
				jTabbedPane1.addTab(PropertyFactory.getString("in_classlevel"), pnlClassLevel);

				break;

			case EditorConstants.EDIT_DEITY:
				pnlDomains.setHeader(PropertyFactory.getString("in_demGrantDom"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_domains"), pnlDomains);

				//buildFollowersTab();
				jTabbedPane1.addTab(PropertyFactory.getString("in_demFollowers"), pnlFollowers);

				pnlRaces.setHeader(PropertyFactory.getString("in_demRacWors"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_race"), pnlRaces);

				break;

			case EditorConstants.EDIT_DOMAIN:
				jTabbedPane1.addTab("Spell Lists", pnlQSpells);
				break;

			case EditorConstants.EDIT_FEAT:
				break;

			case EditorConstants.EDIT_LANGUAGE:
				break;

			case EditorConstants.EDIT_RACE:
				pnlClasses.setHeader(PropertyFactory.getString("in_demFavoredClasses"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlClasses);
				pnlTemplates.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlTemplates.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlLanguages.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlLanguages.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlBonusLang.setHeader(PropertyFactory.getString("in_demBonusLang"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demBonusLangAbbrev"), pnlBonusLang);
				pnlFeats.setLblSelectedText(PropertyFactory.getString("in_demSelFeats"));
				pnlFeats.setLblSelected2Text(PropertyFactory.getString("in_demSelMFeats"));

				break;

			case EditorConstants.EDIT_SKILL:

				//buildSynergyTab();
				pnlSynergy.setHeader(" ");
				jTabbedPane1.addTab(PropertyFactory.getString("in_demSynergy"), pnlSynergy);

				pnlClasses.setHeader(" ");
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlClasses);

				break;

			case EditorConstants.EDIT_SPELL:
				jTabbedPane1.addTab(PropertyFactory.getString("in_demBase2"), pnlBase2);
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlQClasses);
				jTabbedPane1.addTab(PropertyFactory.getString("in_domains"), pnlQDomains);

				break;

			case EditorConstants.EDIT_TEMPLATE:
				pnlClasses.setHeader(PropertyFactory.getString("in_demFavoredClasses"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demClasses"), pnlClasses);
				pnlTemplates.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlTemplates.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlLanguages.setLblSelectedText(PropertyFactory.getString("in_demAllGranted"));
				pnlLanguages.setLblSelected2Text(PropertyFactory.getString("in_demChoiceGranted"));
				pnlBonusLang.setHeader(PropertyFactory.getString("in_demBonusLang"));
				jTabbedPane1.addTab(PropertyFactory.getString("in_demBonusLangAbbrev"), pnlBonusLang);

				break;

			case EditorConstants.EDIT_CAMPAIGN:
				jTabbedPane1.addTab(PropertyFactory.getString("in_fileTypes"), pnlFileTypes);

				break;

			default:
				break;
		}

		if (pnlLanguages != null)
		{
			pnlLanguages.setHeader(PropertyFactory.getString("in_demGrantLang"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_languages"), pnlLanguages);
		}

		if (pnlWeapons != null)
		{
			pnlWeapons.setHeader(PropertyFactory.getString("in_demGraWeaPro"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_weapon"), pnlWeapons);
		}

		if (pnlSkills != null)
		{
			pnlSkills.setHeader(PropertyFactory.getString("in_demGraSkil"));
			pnlSkills.setLblSelectedText(PropertyFactory.getString("in_demSelClaSkil"));
			pnlSkills.setLblSelected2Text(PropertyFactory.getString("in_demSelCroCla"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_skills"), pnlSkills);
		}

		if (pnlLevelAbilities != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_specialabilities"), pnlLevelAbilities);
		}

		if (pnlMovement != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_movement"), pnlMovement);
		}

		if (pnlTemplates != null)
		{
			pnlTemplates.setHeader(PropertyFactory.getString("in_demGraTemp"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_templates"), pnlTemplates);
		}

		if (pnlVision != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_demVision"), pnlVision);
		}

		if (pnlAge != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_demAge"), pnlAge);
		}

		if (pnlAppearance != null)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_demAppearance"), pnlAppearance);
		}

		if (pnlNaturalAttacks != null)
		{
			jTabbedPane1.addTab("Natural Weapons", pnlNaturalAttacks);
		}

		if (pnlFeats != null)
		{
			pnlFeats.setHeader(PropertyFactory.getString("in_demGraFeat"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_feats"), pnlFeats);
		}

		if (pnlVFeats != null)
		{
			pnlVFeats.setHeader(PropertyFactory.getString("in_demGraVFeat"));
			jTabbedPane1.addTab(PropertyFactory.getString("in_demVFeats"), pnlVFeats);
		}

		//The Advanced Tab has no meaning in the Source File Editor and therefore
		// should only be shown when needed.
		if (editType != EditorConstants.EDIT_CAMPAIGN)
		{
			jTabbedPane1.addTab(PropertyFactory.getString("in_demAdv"), pnlAdvanced);
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

		btnCancel.setMnemonic(PropertyFactory.getMnemonic("in_mn_cancel"));
		btnCancel.setText(PropertyFactory.getString("in_cancel"));
		btnCancel.setPreferredSize(new Dimension(80, 26));
		btnCancel.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnCancelActionPerformed();
				}
			});

		pnlButtons.add(btnCancel);

		btnSave.setMnemonic(PropertyFactory.getMnemonic("in_mn_save"));
		btnSave.setText(PropertyFactory.getString("in_save"));
		btnSave.setPreferredSize(new Dimension(80, 26));
		btnSave.addActionListener(new ActionListener()
			{
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

	private void parseAlignment(List availableList, List selectedList, String alignmentString, String qualifier)
	{
		for (int i = 0; i < alignmentString.length(); ++i)
		{
			final char alignmentChar = alignmentString.charAt(i);

			if (alignmentChar == '[')
			{
				int idx = alignmentString.indexOf(']', i);

				if (idx >= 0)
				{
					final StringTokenizer aTok = new StringTokenizer(alignmentString.substring(i + 1, idx), "=", false);

					if (aTok.countTokens() == 3)
					{
						final String qualifierType = aTok.nextToken();
						final String variableName = aTok.nextToken();
						parseAlignment(availableList, selectedList, aTok.nextToken(),
							" [" + qualifierType + ":" + variableName + ']');
					}

					i = idx;
				}
			}
			else if ((alignmentChar >= '0') && (alignmentChar <= '9'))
			{
				final int idx = alignmentChar - '0';
				availableList.remove(SettingsHandler.getGame().getLongAlignmentAtIndex(idx));
				selectedList.add(SettingsHandler.getGame().getLongAlignmentAtIndex(idx) + ((qualifier == null) ? "" : qualifier));
			}
		}
	}

	private static boolean parseSynergyBonus(final BonusObj aBonus, List availableList, List selectedList)
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
	List buildAdvancedSelectedList(int anEditType)
	{
		List<String> selectedList = new ArrayList<String>();

		//
		// Initialize the list of advanced items in the selected list
		//
		selectedList.clear();

		switch (anEditType)
		{
			case EditorConstants.EDIT_DEITY:
				for (Iterator e = ((Deity) thisPObject).getPantheonList().iterator(); e.hasNext();)
				{
					selectedList.add("PANTHEON:" + e.next());
				}

				break;

			case EditorConstants.EDIT_DOMAIN:
			case EditorConstants.EDIT_FEAT:
				if (thisPObject.getChoiceString().length() != 0)
				{
					selectedList.add("CHOOSE:" + thisPObject.getChoiceString());
				}

				break;

			case EditorConstants.EDIT_SKILL:
				final Skill mySkill = (Skill) thisPObject;
				final String choiceString = mySkill.getChoiceString();

				if ((choiceString != null) && (choiceString.length() > 0))
				{
					for (Iterator e = ((Skill) thisPObject).getChoiceStringList().iterator(); e.hasNext();)
					{
						selectedList.add("CHOOSE:" + e.next());
					}
				}

				break;

			default:
				break;
		}

		final List autoList = thisPObject.getSafeListFor(ListKey.AUTO_ARRAY);

		if (autoList != null)
		{
			for (Iterator e = autoList.iterator(); e.hasNext();)
			{
				String tagContent = e.next().toString();
				// We need to exclude WEAPONPROFs as they appear on the weapon tab
				if (tagContent != null && !tagContent.startsWith("WEAPONPROF"))
				{
					selectedList.add("AUTO:" + tagContent);
				}
			}
		}

		if (anEditType != EditorConstants.EDIT_CLASS)
		{
			for (Iterator e = thisPObject.getBonusList().iterator(); e.hasNext();)
			{
				// updated 18 Jul 2003 -- sage_sam and 9 apr 2005 -- hunterc
				final BonusObj bonus = (BonusObj) e.next();

				if (!parseSynergyBonus(bonus, null, null))
				{
					selectedList.add("BONUS:" + bonus.getPCCText()); //Formats the items in the proper .lst format
				}
			}
		}

		for (int i = 0, x = thisPObject.getPreReqCount(); i < x; ++i)
		{
			// TODO: This hack is here because there is currently no
			// specific Prerequisite editor. The code curently relies
			// on the assumption that input format==in memory format== output format
			// This assumption is WRONG
			StringWriter writer = new StringWriter();
			Prerequisite prereq = thisPObject.getPreReq(i);
			PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			try
			{
				prereqWriter.write(writer, prereq);
			}
			catch (PersistenceLayerException e1)
			{
				e1.printStackTrace();
			}

			selectedList.add(writer.toString());
		}

		if (anEditType != EditorConstants.EDIT_CLASS)
		{
			for (int i = 0, x = thisPObject.getVariableCount(); i < x; ++i)
			{
				String aString = thisPObject.getVariableDefinition(i);

				if (aString.startsWith("-9|"))
				{
					aString = aString.substring(3);
				}

				selectedList.add("DEFINE:" + aString);
			}
		}

		final List saList = thisPObject.getListFor(ListKey.SPECIAL_ABILITY);

		if ((saList != null) && (saList.size() != 0) && (anEditType != EditorConstants.EDIT_CLASS))
		{
			for (Iterator e = saList.iterator(); e.hasNext();)
			{
				Object specialAbility = e.next();
//				String saSource = ((SpecialAbility) specialAbility).getSource();
				String saSource = ((SpecialAbility) specialAbility).getSASource();
				String saLevel = saSource.substring(saSource.indexOf("|") + 1);
				String saTxt = specialAbility.toString();

				if (saLevel.length() > 0)
				{
					saLevel += "|";
				}

				selectedList.add("SA:" + saLevel + saTxt);
			}

			saList.clear(); // or else it will get doubled on a save!
		}


		// Add only those DR entries that are not level based.
		List drList = thisPObject.getDRList();
		for (Iterator i = drList.iterator(); i.hasNext();)
		{
			DamageReduction dr = (DamageReduction) i.next();
			boolean levelBased = false;
			if (anEditType == EditorConstants.EDIT_CLASS)
			{
				levelBased = dr.isForClassLevel(thisPObject.getKeyName());
			}
			if (!levelBased)
			{
				selectedList.add(dr.getPCCText(true));
			}
		}

		if (anEditType != EditorConstants.EDIT_CLASS)
		{
			List spellList = thisPObject.getSpellList();
			if (spellList != null) {
				for (Iterator it = spellList.iterator(); it.hasNext();)
				{
					selectedList.add("SPELLS:" + ((PCSpell) it.next()).getPCCText());
				}
			}
		}

		String srString = thisPObject.getSRFormula();

		if (srString != null)
		{
			selectedList.add("SR:" + srString);
		}
		return selectedList;
	}

}
