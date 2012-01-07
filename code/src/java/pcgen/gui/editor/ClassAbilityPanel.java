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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.MapCollection;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.SpellProhibitor;
import pcgen.core.spell.Spell;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.rules.context.LoadContext;

/**
 * <code>ClassAbilityPanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */
public class ClassAbilityPanel extends JPanel implements PObjectUpdater<PCClass>
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

	@Override
	public void updateData(PCClass obj)
	{
		String a = attackCycle.getText().trim();

		LoadContext context = Globals.getContext();
		if (a.length() > 0)
		{
			context.unconditionallyProcess(obj, "ATTACKCYCLE", a);
		}

		a = hitDice.getText().trim();

		if (a.length() > 0)
		{
			obj.put(ObjectKey.LEVEL_HITDIE, new HitDie(Integer.parseInt(a)));
		}

		a = deity.getText().trim();

		if (a.length() > 0)
		{
			context.unconditionallyProcess(obj, "DEITY", a);
		}

		a = itemCreate.getText().trim();
		if (a.length() > 0)
		{
			obj.put(StringKey.ITEMCREATE, a);
		}

		a = extraFeats.getText().trim();

		if (a.length() > 0)
		{
			obj.put(IntegerKey.START_FEATS, Integer.valueOf(a));
		}

		a = levelsPerFeat.getText().trim();

		if (a.length() > 0)
		{
			obj.put(IntegerKey.LEVELS_PER_FEAT, Integer.valueOf(a));
		}

		a = knownSpells.getText().trim();

		if (a.length() > 0)
		{
			obj.removeListFor(ListKey.KNOWN_SPELLS);
			context.unconditionallyProcess(obj, "KNOWNSPELLS", a);
		}

		obj.put(ObjectKey.MEMORIZE_SPELLS, memorize.getSelectedObjects() != null);
		a = prohibited.getText().trim();

		if (a.length() > 0)
		{
			context.unconditionallyProcess(obj, "PROHIBITED", a);
		}

		obj.put(ObjectKey.SPELLBOOK, spellBook.getSelectedObjects() != null);
		context.unconditionallyProcess(obj, "SPELLLIST", spellList.getText().trim());

		a = (String) spellStat.getSelectedItem();

		if ((a != null) && (a.length() > 0) && !Constants.NONE.equals(a))
		{
			context.unconditionallyProcess(obj, "SPELLSTAT", a);
		}

		/*
		 * CONSIDER I find this interesting that one can unset the Spell Type,
		 * but this does not ensure consistency with KNOWN, et al. to ensure the
		 * class is valid. Isn't SpellType required if there are spells known or
		 * cast? - thpr 11/9/06
		 * 
		 * Ditto SpellBaseStat above...
		 */
		obj.remove(StringKey.SPELLTYPE);
		a = (String) spellType.getSelectedItem();

		if ((a != null) && (a.length() > 0) && !a.equalsIgnoreCase(Constants.LST_NONE))
		{
			obj.put(StringKey.SPELLTYPE, a);
		}

		a = maxLevel.getText().trim();

		if (a.length() > 0)
		{
			obj.put(IntegerKey.LEVEL_LIMIT, Integer.parseInt(a));
		}
	}

	@Override
	public void updateView(PCClass obj)
	{
		attackCycle.setText(StringUtil.join(new MapCollection(obj
				.getMapFor(MapKey.ATTACK_CYCLE)), Constants.PIPE));
		hitDice.setText(String.valueOf(obj.getSafe(ObjectKey.LEVEL_HITDIE).getDie()));
		deity.setText(StringUtil.join(obj.getSafeListFor(ListKey.DEITY), Constants.PIPE));
		itemCreate.setText(obj.get(StringKey.ITEMCREATE));
		Integer sf = obj.get(IntegerKey.START_FEATS);
		extraFeats.setText(sf == null ? "" : sf.toString());
		Integer lpf = obj.get(IntegerKey.LEVELS_PER_FEAT);
		if (lpf != null)
		{
			levelsPerFeat.setText(lpf.toString());
		}

		String[] known = Globals.getContext().unparseSubtoken(obj, "KNOWNSPELLS");
		if (known != null && known.length > 0)
		{
			knownSpells.setText(known[0]);
		}
		memorize.setSelected(obj.getSafe(ObjectKey.MEMORIZE_SPELLS));
		Set<String> set = new TreeSet<String>();
		for (SpellProhibitor sp : obj
			.getSafeListFor(ListKey.PROHIBITED_SPELLS))
		{
			set.addAll(sp.getValueList());
		}
		prohibited.setText(StringUtil.join(set, ","));

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
		Boolean sb = obj.get(ObjectKey.SPELLBOOK);
		spellBook.setSelected(sb != null && sb);
		TransitionChoice<CDOMListObject<Spell>> choices = obj.get(ObjectKey.SPELLLIST_CHOICE);
		if (choices != null) {
			StringBuilder csb = new StringBuilder();
			csb.append(choices.getCount());
			csb.append(Constants.PIPE);
			csb.append(choices.getChoices().getLSTformat());
			spellList.setText(csb.toString());
		}

		//spellStat.setText(obj.getSpellBaseStat());
		//spellType.setText(obj.getSpellType());
		spellStat.setSelectedItem(obj.getSpellBaseStat());
		spellType.setSelectedItem(obj.getSpellType());
		maxLevel.setText(String.valueOf(obj.getSafe(IntegerKey.LEVEL_LIMIT)));
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
		String aString;
		List<String> aList = new ArrayList<String>();

		//
		// Make list of stats
		//
		for (PCStat pcs : Globals.getContext().ref.getOrderSortedCDOMObjects(PCStat.class))
		{
			aList.add(pcs.getAbb());
		}

		aList.remove(Constants.NONE);
		Collections.sort(aList);
		aList.add(0, Constants.NONE);
		spellStat.setModel(new DefaultComboBoxModel(aList.toArray()));

		//
		// Make list of used spell types
		//
		aList.clear();
		
		for (PCClass cl : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
		{
			aString = cl.getSpellType();

			if (!aList.contains(aString))
			{
				aList.add(aString);
			}
		}

		aList.remove(Constants.NONE);
		Collections.sort(aList);
		aList.add(0, Constants.NONE);
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
