/*
 * ClassAbilityPanel
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com >
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

import pcgen.base.util.MapCollection;
import pcgen.core.*;
import pcgen.core.utils.ChoiceList;
import pcgen.core.utils.CoreUtility;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.enumeration.AttackType;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <code>ClassAbilityPanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */
public class ClassAbilityPanel extends JPanel implements PObjectUpdater
{
	private JCheckBox memorize = new JCheckBox();
	private JCheckBox spellBook = new JCheckBox();

	//private JTextField spellStat = new JTextField();
	//private JTextField spellType = new JTextField();
	private JComboBoxEx spellStat = new JComboBoxEx();
	private JComboBoxEx spellType = new JComboBoxEx();
	private JTextField attackCycle = new JTextField();
	private JTextField deity = new JTextField();
	private JTextField extraFeats = new JTextField();
	private JTextField hitDice = new JTextField();
	private JTextField itemCreate = new JTextField();
	private JTextField knownSpells = new JTextField();
	private JTextField levelsPerFeat = new JTextField();
	private JTextField maxLevel = new JTextField();
	private JTextField prohibited = new JTextField();
	private JTextField spellList = new JTextField();

	/** Creates new form ClassAbilityPanel */
	public ClassAbilityPanel()
	{
		initComponents();
		initComponentContents();
	}

	public void updateData(PObject po)
	{
		if (!(po instanceof PCClass))
		{
			return;
		}

		PCClass obj = (PCClass) po;
		String a = attackCycle.getText().trim();

		if (a.length() > 0)
		{
			PCClassLstToken token = (PCClassLstToken) TokenStore.inst()
					.getTokenMap(PCClassLstToken.class).get("ATTACKCYCLE");
			token.parse(obj, a, -9);
		}

		a = hitDice.getText().trim();

		if (a.length() > 0)
		{
			obj.setHitDie(Integer.parseInt(a));
		}

		a = deity.getText().trim();

		if (a.length() > 0)
		{
			PCClassLstToken token = (PCClassLstToken) TokenStore.inst()
					.getTokenMap(PCClassLstToken.class).get("DEITY");
			token.parse(obj, a, -9);
		}

		a = itemCreate.getText().trim();
		obj.setItemCreationMultiplier(a);

		a = extraFeats.getText().trim();

		if (a.length() > 0)
		{
			obj.setInitialFeats(Integer.parseInt(a));
		}

		a = levelsPerFeat.getText().trim();

		if (a.length() > 0)
		{
			obj.setLevelsPerFeat(Integer.valueOf(a));
		}

		a = knownSpells.getText().trim();

		if (a.length() > 0)
		{
			obj.clearKnownSpellsList();
			PCClassLstToken token = (PCClassLstToken) TokenStore.inst()
					.getTokenMap(PCClassLstToken.class).get("KNOWNSPELLS");
			token.parse(obj, a, -9);
		}

		obj.setMemorizeSpells(memorize.getSelectedObjects() != null);
		a = prohibited.getText().trim();

		if (a.length() > 0)
		{
			PCClassLstToken token = (PCClassLstToken) TokenStore.inst()
					.getTokenMap(PCClassLstToken.class).get("PROHIBITED");
			token.parse(obj, a, -9);
		}

		obj.setSpellBookUsed(spellBook.getSelectedObjects() != null);
		
		PCClassLstToken token = (PCClassLstToken) TokenStore.inst()
				.getTokenMap(PCClassLstToken.class).get("SPELLLIST");
		token.parse(obj, spellList.getText().trim(), -9);

		//a = spellStat.getText().trim();
		//if (a.length() > 0)
		//{
		//	obj.setSpellBaseStat(a);
		//}
		//a = spellType.getText().trim();
		//if (a.length() > 0)
		//{
		//	obj.setSpellType(a);
		//}
		if (!Constants.s_NONE.equals(obj.getSpellBaseStat())) {
			//This IF gate exists to prevent useless instantiation of SpellProgressionInfo
			obj.setSpellBaseStat(Constants.s_NONE);
		}
		a = (String) spellStat.getSelectedItem();

		if ((a != null) && (a.length() > 0) && !Constants.s_NONE.equals(a))
		{
			obj.setSpellBaseStat(a);
		}

		/*
		 * CONSIDER I find this interesting that one can unset the Spell Type,
		 * but this does not ensure consistency with KNOWN, et al. to ensure the
		 * class is valid. Isn't SpellType required if there are spells known or
		 * cast? - thpr 11/9/06
		 * 
		 * Ditto SpellBaseStat above...
		 */
		obj.setSpellType(Constants.s_NONE);
		a = (String) spellType.getSelectedItem();

		if ((a != null) && (a.length() > 0))
		{
			obj.setSpellType(a);
		}

		a = maxLevel.getText().trim();

		if (a.length() > 0)
		{
			obj.setMaxLevel(Integer.parseInt(a));
		}
	}

	public void updateView(PObject po)
	{
		if (!(po instanceof PCClass))
		{
			return;
		}

		PCClass obj = (PCClass) po;
		Map<AttackType, String> attackCycleMap = obj.getAttackCycle();
		if (attackCycleMap != null) {
			MapCollection mc = new MapCollection(attackCycleMap);
			attackCycle.setText(CoreUtility.join(mc, Constants.PIPE));
		}
		hitDice.setText(String.valueOf(obj.getBaseHitDie()));
		deity.setText(CoreUtility.join(obj.getDeityList(), Constants.PIPE));
		itemCreate.setText(obj.getItemCreationMultiplier());
		extraFeats.setText(String.valueOf(obj.getInitialFeats()));
		if (obj.getLevelsPerFeat()!=null)
		{
			levelsPerFeat.setText(obj.getLevelsPerFeat().toString());
		}

		knownSpells.setText(CoreUtility.join(obj.getKnownSpellsList(), "|"));
		memorize.setSelected(obj.getMemorizeSpells());
		prohibited.setText(CoreUtility.join(obj.getProhibitedSchools(), ","));

//		StringBuffer specKnown = new StringBuffer();
//		for (LevelProperty<String> lp : obj.getSpecialtyKnownList())
//		{
//			if (known.length() > 0)
//			{
//				specKnown.append('|');
//			}
//			specKnown.append(lp.getLevel());
//			specKnown.append('=');
//			specKnown.append(lp.getObject());
//		}
		spellBook.setSelected(obj.getSpellBookUsed());
		ChoiceList<String> classSpellChoices = obj.getClassSpellChoices();
		if (classSpellChoices != null) {
			spellList.setText(classSpellChoices.toString());
		}

		//spellStat.setText(obj.getSpellBaseStat());
		//spellType.setText(obj.getSpellType());
		spellStat.setSelectedItem(obj.getSpellBaseStat());
		spellType.setSelectedItem(obj.getSpellType());
		maxLevel.setText(String.valueOf(obj.getMaxLevel()));
	}

	private static GridBagConstraints buildConstraints(GridBagConstraints gridBagConstraints, int gridx, int gridy,
	    boolean useInsets)
	{
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;

		if (useInsets)
		{
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		}

		return gridBagConstraints;
	}

	private void initComponentContents()
	{
		Iterator e;
		String aString;
		List<String> aList = new ArrayList<String>();

		//
		// Make list of stats
		//
		for (e = SettingsHandler.getGame().getUnmodifiableStatList().iterator(); e.hasNext();)
		{
			aList.add(((PCStat) e.next()).getAbb());
		}

		aList.remove(Constants.s_NONE);
		Collections.sort(aList);
		aList.add(0, Constants.s_NONE);
		spellStat.setModel(new DefaultComboBoxModel(aList.toArray()));

		//
		// Make list of used spell types
		//
		aList.clear();

		for (e = Globals.getClassList().iterator(); e.hasNext();)
		{
			aString = ((PCClass) e.next()).getSpellType();

			if (!aList.contains(aString))
			{
				aList.add(aString);
			}
		}

		aList.remove(Constants.s_NONE);
		Collections.sort(aList);
		aList.add(0, Constants.s_NONE);
		spellType.setModel(new DefaultComboBoxModel(aList.toArray()));
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		JLabel tempLabel;

		setLayout(new GridBagLayout());

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0.1;

		tempLabel = new JLabel("Hit Die:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 0, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 0, true);
		gridBagConstraints.weightx = 0.2;
		add(hitDice, gridBagConstraints);

		tempLabel = new JLabel("Max Level:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 0, true);
		gridBagConstraints.weightx = 0.1;
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 0, true);
		gridBagConstraints.weightx = 0.2;
		add(maxLevel, gridBagConstraints);


		tempLabel = new JLabel("Bonus Feats:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 1, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 1, true);
		add(extraFeats, gridBagConstraints);

		tempLabel = new JLabel("Levels Per Feat:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 1, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 1, true);
		add(levelsPerFeat, gridBagConstraints);


		tempLabel = new JLabel("Attack Cycle:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 2, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 2, true);
		add(attackCycle, gridBagConstraints);

		tempLabel = new JLabel("Item Create Mult:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 2, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 2, true);
		add(itemCreate, gridBagConstraints);


		tempLabel = new JLabel("Spell Type:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 3, true);
		add(tempLabel, gridBagConstraints);

		spellType.setEditable(true);
		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 3, true);
		add(spellType, gridBagConstraints);

		tempLabel = new JLabel("Spell Stat:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 3, true);
		add(tempLabel, gridBagConstraints);

		spellStat.setEditable(true);
		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 3, true);
		add(spellStat, gridBagConstraints);


		tempLabel = new JLabel("Uses Spell Book:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 5, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 5, true);
		add(spellBook, gridBagConstraints);

		tempLabel = new JLabel("Memorizes Spells:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 5, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 5, true);
		add(memorize, gridBagConstraints);


		//Removed specialtyKnown because it is level dependent - thpr 10/31/06
		//tempLabel = new JLabel("Specialty Known:");
		//gridBagConstraints = buildConstraints(gridBagConstraints, 0, 6, true);
		//add(tempLabel, gridBagConstraints);

		//gridBagConstraints = buildConstraints(gridBagConstraints, 1, 6, true);
		//add(specialtyKnown, gridBagConstraints);

		tempLabel = new JLabel("Spell List:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 6, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 6, true);
		add(spellList, gridBagConstraints);

		tempLabel = new JLabel("Prohibited:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 6, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 6, true);
		add(prohibited, gridBagConstraints);


		tempLabel = new JLabel("Known Spells:");
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 7, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 7, true);
		gridBagConstraints.gridwidth = 3;
		add(knownSpells, gridBagConstraints);

		
		tempLabel = new JLabel("Deities:");
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 8, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 8, true);
		gridBagConstraints.gridwidth = 3;
		add(deity, gridBagConstraints);
	}
}
