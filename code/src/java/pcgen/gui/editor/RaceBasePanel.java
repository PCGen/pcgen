/*
 * RaceBasePanel
 * Copyright 2003 (C) James Dempsey <jdempsey@users.sourceforge.net >
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
 * Created on January 8, 2003, 8:15 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * <code>RaceBasePanel</code>
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 */
public class RaceBasePanel extends BasePanel<Race>
{
	private static final String NO_MONSTER_CLASS = "(None)";
	private static final String[] sizeTitles =
			new String[]{"Fine", "Diminutive", "Tiny", "Small", "Medium",
				"Large", "Huge", "Gargantuan", "Colossal"};
	private static final String[] sizeAbbrev =
			new String[]{"F", "D", "T", "S", "M", "L", "H", "G", "C"};
	private static final String[] crValues =
			{"1/10", "1/8", "1/6", "1/4", "1/3", "1/2", "0", "1", "2", "3",
				"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
				"15", "16", "17", "18", "19", "20"};
	private JComboBoxEx cmbBonusFeats;
	private JComboBoxEx cmbBonusSkillPoints;

	private JComboBoxEx cmbCR;
	private JComboBoxEx cmbHands;
	private JComboBoxEx cmbLegs;
	private JComboBoxEx cmbMonsterClass;
	private JComboBoxEx cmbMonsterLevel;
	private JComboBoxEx cmbRaceType;
	private JComboBoxEx cmbReach;
	private JComboBoxEx cmbSize;
	private JComboBoxEx cmbSkillMult;
	private JPanel pnlTemplateMisc;
	private JTextField txtDisplayName;
	private JTextField txtHitDiceAdvancement;
	private JTextField txtLevelAdj;

	//	private AvailableSelectedPanel pnlTemplateTypes;
	private TypePanel pnlTemplateTypes;

	/** Creates new form TemplateBasePanel */
	public RaceBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	public void setBonusFeats(final int bonusFeats)
	{
		if ((bonusFeats >= 0) && (bonusFeats < cmbBonusFeats.getItemCount()))
		{
			cmbBonusFeats.setSelectedIndex(bonusFeats);
		}
	}

	public int getBonusFeats()
	{
		return cmbBonusFeats.getSelectedIndex();
	}

	public void setBonusSkillPoints(final int bonusSkillPoints)
	{
		if ((bonusSkillPoints >= 0)
			&& (bonusSkillPoints < cmbBonusSkillPoints.getItemCount()))
		{
			cmbBonusSkillPoints.setSelectedIndex(bonusSkillPoints);
		}
	}

	public int getBonusSkillPoints()
	{
		return cmbBonusSkillPoints.getSelectedIndex();
	}

	public void setCR(ChallengeRating argCR)
	{
		if (argCR != null)
		{
			cmbCR.setSelectedItem(argCR.getRating().toString());
		}
	}

	public void setDisplayName(final String dislayName)
	{
		txtDisplayName.setText(dislayName);
	}

	public String getDisplayName()
	{
		return txtDisplayName.getText();
	}

	public void setHands(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbHands.getItemCount()))
		{
			cmbHands.setSelectedIndex(aNumber);
		}
	}

	public int getHands()
	{
		return cmbHands.getSelectedIndex();
	}

	public void setHitDiceAdvancement(final Race thisRace)
	{
		LoadContext context = Globals.getContext();
		String[] hitdice = context.unparseSubtoken(thisRace, "HITDICE");
		if (hitdice != null)
		{
			if (hitdice.length == 1)
			{
				txtHitDiceAdvancement.setText(hitdice[0]);
			}
			else
			{
				Logging.errorPrint("Found more than one HITDICEADVANCEMENT in "
						+ thisRace.getDisplayName());
			}
		}
	}

	public void setLegs(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbLegs.getItemCount()))
		{
			cmbLegs.setSelectedIndex(aNumber);
		}
	}

	public int getLegs()
	{
		return cmbLegs.getSelectedIndex();
	}

	public void setLevelAdjustment(final String levelAdj)
	{
		txtLevelAdj.setText(levelAdj);
	}

	public Formula getLevelAdjustment()
	{
		return FormulaFactory.getFormulaFor(txtLevelAdj.getText());
	}

	public void setMonsterClass(final String aString)
	{
		if ((aString == null) || (aString.length() == 0))
		{
			cmbMonsterClass.setSelectedItem(NO_MONSTER_CLASS);
		}
		else
		{
			cmbMonsterClass.setSelectedItem(aString);
		}
	}

	public String getMonsterClass()
	{
		return (String) cmbMonsterClass.getSelectedItem();
	}

	public void setMonsterClassList(final List<String> classList)
	{
		cmbMonsterClass.setModel(new DefaultComboBoxModel(classList.toArray()));
	}

	public void setMonsterLevel(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbMonsterLevel.getItemCount()))
		{
			cmbMonsterLevel.setSelectedIndex(aNumber);
		}
	}

	public int getMonsterLevel()
	{
		return cmbMonsterLevel.getSelectedIndex();
	}

	public void setRaceSize(final Formula f)
	{
		cmbSize.setSelectedIndex(0);
		if (f == null)
		{
			return;
		}
		
		String aString = f.toString();
		for (int index = 0; index < sizeAbbrev.length; index++)
		{
			if (sizeAbbrev[index].equals(aString))
			{
				cmbSize.setSelectedIndex(index);

				break;
			}
		}
	}

	public SizeAdjustment getRaceSize()
	{
		int index = cmbSize.getSelectedIndex();

		if ((index >= 0) && (index < sizeAbbrev.length))
		{
			String abb = sizeAbbrev[index];
			return Globals.getContext().ref.getAbbreviatedObject(
					SizeAdjustment.class, abb);
		}
		return null;
	}

	public void setRaceType(final String aString)
	{
		cmbRaceType.setSelectedItem(aString);
	}

	public String getRaceType()
	{
		return (String) cmbRaceType.getSelectedItem();
	}

	
	public void setReach(final int aNumber)
	{
		cmbReach.setSelectedItem(String.valueOf(aNumber));
	}

	public int getReach()
	{
		return Integer.parseInt((String) cmbReach.getSelectedItem());
	}

	public void setSkillMultiplier(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbSkillMult.getItemCount()))
		{
			cmbSkillMult.setSelectedIndex(aNumber);
		}
	}

	public int getSkillMultiplier()
	{
		return cmbSkillMult.getSelectedIndex();
	}

	public void setTypesAvailableList(final List<Type> aList,
		final boolean sort)
	{
		pnlTemplateTypes.setAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final List<Type> aList,
		final boolean sort)
	{
		pnlTemplateTypes.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlTemplateTypes.getSelectedList();
	}

	@Override
	public void updateData(Race thisRace)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("FEAT|POOL|").append(getBonusFeats());
		final BonusObj bon = Bonus.newBonus(Globals.getContext(), sb.toString());
		thisRace.addToListFor(ListKey.BONUS, bon);

		if (getBonusSkillPoints() > 0)
		{
			thisRace.put(IntegerKey.SKILL_POINTS_PER_LEVEL, getBonusSkillPoints());
		}
		else
		{
			thisRace.remove(IntegerKey.SKILL_POINTS_PER_LEVEL);
		}
		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(thisRace, "CR", cmbCR.getSelectedItem()
				.toString());
		if (getDisplayName() != null && getDisplayName().trim().length() > 0)
		{
			thisRace.setName(getDisplayName());
		}
		thisRace.put(IntegerKey.CREATURE_HANDS, getHands());
		if (txtHitDiceAdvancement.getText().trim().length() > 0)
		{
			context.unconditionallyProcess(thisRace, "HITDICEADVANCEMENT", txtHitDiceAdvancement.getText());
		}
		thisRace.put(IntegerKey.LEGS, getLegs());
		thisRace.put(FormulaKey.LEVEL_ADJUSTMENT, getLevelAdjustment());
		String monsterClass = getMonsterClass();
		if (!monsterClass.equals(NO_MONSTER_CLASS))
		{
			context.unconditionallyProcess(thisRace, "MONSTERCLASS",
					monsterClass + ":" + getMonsterLevel());
		}
		thisRace.put(FormulaKey.SIZE, new FixedSizeFormula(getRaceSize()));
		thisRace.put(IntegerKey.REACH, getReach());
		thisRace.put(IntegerKey.INITIAL_SKILL_MULT, getSkillMultiplier());

		//
		// Save types
		//
		thisRace.put(ObjectKey.RACETYPE, RaceType
			.getConstant((String) cmbRaceType.getSelectedItem()));
		thisRace.removeListFor(ListKey.TYPE);

		for (Object o : getTypesSelectedList())
		{
			thisRace.addToListFor(ListKey.TYPE, Type.getConstant(o.toString()));
		}
	}

	@Override
	public void updateView(Race thisRace)
	{
		//
		// Populate the types
		//
		List<Type> availableList = new ArrayList<Type>();
		List<Type> selectedList = new ArrayList<Type>();

		for (final Race race : Globals.getContext().ref.getConstructedCDOMObjects(Race.class))
		{
			for (Type type : race.getTrueTypeList(false))
			{
				if (!type.equals(Type.CUSTOM))
				{
					if (!availableList.contains(type))
					{
						availableList.add(type);
					}
				}
			}
		}

		// remove this race's type from the available list and place into selected list
		for (Type type : thisRace.getTrueTypeList(false))
		{
			if (!type.equals(Type.CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
			}
		}

		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		/// Populate the race type drop-down
		Collection<RaceType> typeCol = RaceType.getAllConstants();
		RaceType[] typeArr = new RaceType[typeCol.size()];
		typeArr = typeCol.toArray(typeArr);
		String[] raceTypeNames = new String[typeArr.length];
		for (int i = 0; i < typeArr.length; i++)
		{
			raceTypeNames[i] = typeArr[i].toString();
		}
		cmbRaceType.setModel(new DefaultComboBoxModel(raceTypeNames));
		RaceType rt = thisRace.get(ObjectKey.RACETYPE);
		if (rt != null)
		{
			cmbRaceType.setSelectedItem(rt.toString());
		}
		
		///
		/// Populate the monster classes
		///
		List<String> classesList = new ArrayList<String>();
		classesList.add(NO_MONSTER_CLASS);

		for (PCClass aClass : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
		{
			if (aClass.isMonster())
			{
				classesList.add(aClass.getKeyName());
			}
		}

		setMonsterClassList(classesList);

		setBonusFeats(0);
		setBonusSkillPoints(thisRace.getSafe(IntegerKey.SKILL_POINTS_PER_LEVEL));
		setCR(thisRace.get(ObjectKey.CHALLENGE_RATING));
		setDisplayName(thisRace.getDisplayName());
		setHands(thisRace.getSafe(IntegerKey.CREATURE_HANDS));
		setHitDiceAdvancement(thisRace);
		setLegs(thisRace.getSafe(IntegerKey.LEGS));
		setLevelAdjustment(thisRace.getSafe(FormulaKey.LEVEL_ADJUSTMENT).toString());
		LevelCommandFactory lcf = thisRace.get(ObjectKey.MONSTER_CLASS);
		if (lcf != null)
		{
			setMonsterClass(lcf.getPCClass().getKeyName());
			setMonsterLevel(lcf.getLevelCount().resolve(null, "").intValue());
		}
		setRaceSize(thisRace.get(FormulaKey.SIZE));
		setReach(thisRace.getSafe(IntegerKey.REACH));
		setSkillMultiplier(thisRace.getSafe(IntegerKey.INITIAL_SKILL_MULT));
	}

	private static GridBagConstraints buildConstraints(int gridx, int gridy)
	{
		GridBagConstraints gridBagConstraints;
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;

		return gridBagConstraints;
	}

	private void initComponentContents()
	{
		cmbSize.setModel(new DefaultComboBoxModel(sizeTitles));

		String[] values = new String[20];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i);
		}

		cmbBonusSkillPoints.setModel(new DefaultComboBoxModel(values));
		cmbBonusFeats.setModel(new DefaultComboBoxModel(values));
		cmbLegs.setModel(new DefaultComboBoxModel(values));
		cmbHands.setModel(new DefaultComboBoxModel(values));
		cmbSkillMult.setModel(new DefaultComboBoxModel(values));

		values = new String[10];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i * 5);
		}

		cmbReach.setModel(new DefaultComboBoxModel(values));

		values = new String[40];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i);
		}

		cmbMonsterLevel.setModel(new DefaultComboBoxModel(values));

		cmbCR.setModel(new DefaultComboBoxModel(crValues));
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;
		JLabel tempLabel;

		cmbBonusSkillPoints = new JComboBoxEx();
		cmbBonusFeats = new JComboBoxEx();

		cmbCR = new JComboBoxEx();
		txtDisplayName = new JTextField();
		cmbHands = new JComboBoxEx();
		txtHitDiceAdvancement = new JTextField();
		cmbLegs = new JComboBoxEx();
		txtLevelAdj = new JTextField();
		cmbMonsterClass = new JComboBoxEx();
		cmbMonsterLevel = new JComboBoxEx();
		cmbRaceType = new JComboBoxEx();
		cmbReach = new JComboBoxEx();
		cmbSize = new JComboBoxEx();
		cmbSkillMult = new JComboBoxEx();

		pnlTemplateMisc = new JPanel();

		//pnlTemplateTypes = new AvailableSelectedPanel();
		pnlTemplateTypes =
				new TypePanel(LanguageBundle.getString("in_demEnterNewType"));

		setLayout(new GridBagLayout());

		pnlTemplateMisc.setLayout(new GridBagLayout());

		tempLabel = new JLabel("Display Name");
		gridBagConstraints = buildConstraints(0, 0);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 0);
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtDisplayName, gridBagConstraints);

		tempLabel = new JLabel("Hands");
		gridBagConstraints = buildConstraints(0, 1);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 1);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbHands, gridBagConstraints);

		tempLabel = new JLabel("Legs");
		gridBagConstraints = buildConstraints(2, 1);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 1);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbLegs, gridBagConstraints);

		tempLabel = new JLabel("Reach");
		gridBagConstraints = buildConstraints(0, 2);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 2);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbReach, gridBagConstraints);

		tempLabel = new JLabel("Skill Multiplier");
		gridBagConstraints = buildConstraints(2, 2);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 2);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbSkillMult, gridBagConstraints);

		tempLabel = new JLabel("Bonus Skill Pts / Level");
		gridBagConstraints = buildConstraints(0, 3);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 3);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbBonusSkillPoints, gridBagConstraints);

		tempLabel = new JLabel("Bonus Starting Feats");
		gridBagConstraints = buildConstraints(2, 3);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 3);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbBonusFeats, gridBagConstraints);

		tempLabel = new JLabel("CR");
		gridBagConstraints = buildConstraints(0, 4);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		cmbCR.setEditable(true);
		gridBagConstraints = buildConstraints(1, 4);
		gridBagConstraints.weightx = 0.4;

		pnlTemplateMisc.add(cmbCR, gridBagConstraints);

		tempLabel = new JLabel("Level Adjustment");
		gridBagConstraints = buildConstraints(2, 4);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 4);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtLevelAdj, gridBagConstraints);

		tempLabel = new JLabel("Size");
		gridBagConstraints = buildConstraints(0, 5);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbSize, gridBagConstraints);

		tempLabel = new JLabel("Hit Dice Advancement");
		gridBagConstraints = buildConstraints(2, 5);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtHitDiceAdvancement, gridBagConstraints);

		tempLabel = new JLabel("Monster Class");
		gridBagConstraints = buildConstraints(0, 6);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 6);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbMonsterClass, gridBagConstraints);

		tempLabel = new JLabel("Monster Level");
		gridBagConstraints = buildConstraints(2, 6);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 6);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbMonsterLevel, gridBagConstraints);

		tempLabel = new JLabel("Race Type");
		gridBagConstraints = buildConstraints(0, 7);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		cmbRaceType.setEditable(true);
		gridBagConstraints = buildConstraints(1, 7);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbRaceType, gridBagConstraints);
//
//		tempLabel = new JLabel("Hit Dice Size");
//		gridBagConstraints = buildConstraints(2, 7);
//		pnlTemplateMisc.add(tempLabel, gridBagConstraints);
//
//		gridBagConstraints = buildConstraints(3, 7);
//		gridBagConstraints.weightx = 0.4;
//		pnlTemplateMisc.add(cmbHitDiceSize, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlTemplateMisc, gridBagConstraints);

		//pnlTemplateTypes.setHeader(LanguageBundle.getString("in_type"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlTemplateTypes, gridBagConstraints);
	}
}
