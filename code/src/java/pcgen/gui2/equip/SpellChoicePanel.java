/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.gui2.equip;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.SpellBuilderFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.util.FacadeListModel;
import pcgen.system.LanguageBundle;

/**
 * SpellChoicePanel provides the user interface for the user to select a spell.
 * 
 */
@SuppressWarnings("serial")
public class SpellChoicePanel extends JPanel
{
	private final JComboBox<InfoFacade> classComboBox;
	private final JComboBox<Integer> spellLevelComboBox;
	private final JComboBox spellComboBox;
	private final JComboBox<String> variantComboBox;
	private final JComboBox<Integer> casterLevelComboBox;
	private final JComboBox<String> spellTypeComboBox;
	private final JList<AbilityFacade> metamagicList;

	private CharacterComboBoxModel<InfoFacade> classModel;
	private CharacterComboBoxModel<Integer> spellLevelModel;
	private CharacterComboBoxModel<InfoFacade> spellModel;
	private CharacterComboBoxModel<String> variantModel;
	private CharacterComboBoxModel<Integer> casterLevelModel;
	private CharacterComboBoxModel<String> spellTypeModel;
	private final SpellBuilderFacade spellBuilderFacade;
	private MetamagicFeatListModel metamgicModel;

	/**
	 * Create a new spell choice panel instance.
	 * @param spellBuilderFacade The facade that manages the selection.
	 */
	public SpellChoicePanel(SpellBuilderFacade spellBuilderFacade)
	{
		this.spellBuilderFacade = spellBuilderFacade;

		this.classComboBox = new JComboBox<>();
		this.spellLevelComboBox = new JComboBox<>();
		this.spellComboBox = new JComboBox<>();
		this.variantComboBox = new JComboBox<>();
		this.casterLevelComboBox = new JComboBox<>();
		this.spellTypeComboBox = new JComboBox<>();
		this.metamagicList = new JList<>();

		initModels();
		initComponents();
	}

	/**
	 * Create the various box models which will drive the choices. 
	 */
	private void initModels()
	{
		classModel = new CharacterComboBoxModel<>()
		{
			@Override
			public void setSelectedItem(Object anItem)
			{
				spellBuilderFacade.setClass((InfoFacade) anItem);
			}
		};
		classModel.setListFacade(spellBuilderFacade.getClasses());
		classModel.setReference(spellBuilderFacade.getClassRef());
		classComboBox.setModel(classModel);

		spellLevelModel = new CharacterComboBoxModel<>()
		{
			@Override
			public void setSelectedItem(Object anItem)
			{
				spellBuilderFacade.setSpellLevel((Integer) anItem);
			}
		};
		spellLevelModel.setListFacade(spellBuilderFacade.getLevels());
		spellLevelModel.setReference(spellBuilderFacade.getSpellLevelRef());
		spellLevelComboBox.setModel(spellLevelModel);

		spellModel = new CharacterComboBoxModel<>()
		{
			@Override
			public void setSelectedItem(Object anItem)
			{
				spellBuilderFacade.setSpell((InfoFacade) anItem);
			}
		};
		spellModel.setListFacade(spellBuilderFacade.getSpells());
		spellModel.setReference(spellBuilderFacade.getSpellRef());
		spellComboBox.setModel(spellModel);

		variantModel = new DisablingCharacterComboBoxModel(variantComboBox)
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				spellBuilderFacade.setVariant((String) anItem);
			}
		};
		variantModel.setListFacade(spellBuilderFacade.getVariants());
		variantModel.setReference(spellBuilderFacade.getVariantRef());
		variantComboBox.setModel(variantModel);

		casterLevelModel = new CharacterComboBoxModel<>()
		{
			@Override
			public void setSelectedItem(Object anItem)
			{
				spellBuilderFacade.setCasterLevel((Integer) anItem);
			}
		};
		casterLevelModel.setListFacade(spellBuilderFacade.getCasterLevels());
		casterLevelModel.setReference(spellBuilderFacade.getCasterLevelRef());
		casterLevelComboBox.setModel(casterLevelModel);

		spellTypeModel = new CharacterComboBoxModel<>()
		{
			@Override
			public void setSelectedItem(Object anItem)
			{
				spellBuilderFacade.setSpellType((String) anItem);
			}
		};
		spellTypeModel.setListFacade(spellBuilderFacade.getSpellTypes());
		spellTypeModel.setReference(spellBuilderFacade.getSpellTypeRef());
		spellTypeComboBox.setModel(spellTypeModel);

		metamgicModel = new MetamagicFeatListModel();
		metamgicModel.setListFacade(spellBuilderFacade.getAvailMetamagicFeats());
		metamagicList.setModel(metamgicModel);
		metamagicList.addListSelectionListener(metamgicModel);
	}

	/**
	 * Initialise the on screen components.
	 */
	private void initComponents()
	{
		setLayout(new GridBagLayout());

		addGridBagLayer(this, "in_sumClass", classComboBox); //$NON-NLS-1$
		addGridBagLayer(this, "in_csdSpLvl", spellLevelComboBox); //$NON-NLS-1$
		spellComboBox.setPrototypeDisplayValue("PrototypeDisplayValueForAVeryLongSpellName"); //$NON-NLS-1$
		addGridBagLayer(this, "in_spellName", spellComboBox); //$NON-NLS-1$
		addGridBagLayer(this, "in_csdVariant", variantComboBox); //$NON-NLS-1$
		if (metamgicModel.getSize() > 0)
		{
			metamagicList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			metamagicList.setVisibleRowCount(4);
			JScrollPane listScroller = new JScrollPane(metamagicList);
			listScroller.setPreferredSize(new Dimension(250, 80));
			addGridBagLayer(this, "in_metaFeat", listScroller); //$NON-NLS-1$
		}
		addGridBagLayer(this, "in_casterLvl", casterLevelComboBox); //$NON-NLS-1$
		addGridBagLayer(this, "in_csdSpellType", spellTypeComboBox); //$NON-NLS-1$

		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	}

	private void addGridBagLayer(JPanel panel, String text, JComponent comp)
	{
		Insets insets = new Insets(0, 0, 3, 2);
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel label = new JLabel(LanguageBundle.getString(text));
		gbc.anchor = java.awt.GridBagConstraints.WEST;
		gbc.gridwidth = 2;
		panel.add(label, gbc);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = insets;
		panel.add(comp, gbc);
	}

	/**
	 * DisablingCharacterComboBoxModel is a model that disables its combo box
	 * when there are no possible selections.
	 */
	private abstract static class DisablingCharacterComboBoxModel extends CharacterComboBoxModel<String>
	{

		private final JComboBox<String> box;

		DisablingCharacterComboBoxModel(JComboBox<String> box)
		{
			this.box = box;
		}

		@Override
		public void elementsChanged(ListEvent<String> e)
		{
			super.elementsChanged(e);
			box.setEnabled(getSize() > 0);
		}

	}

	/**
	 * MetamagicFeatListModel holds the data for a list of metamagic feats and 
	 * commits the choices mode into the facade. 
	 */
	private class MetamagicFeatListModel extends FacadeListModel<AbilityFacade> implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}

			List<AbilityFacade> selectedValues = metamagicList.getSelectedValuesList();
			spellBuilderFacade.setSelectedMetamagicFeats(selectedValues.toArray());
		}

	}
}
