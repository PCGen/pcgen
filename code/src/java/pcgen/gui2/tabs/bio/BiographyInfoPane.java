/*
 * BiographyInfoPane.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 26, 2011, 7:55:47 PM
 */
package pcgen.gui2.tabs.bio;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.GenderFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.SimpleFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tabs.models.FormattedFieldHandler;
import pcgen.gui2.tabs.models.TextFieldHandler;

/**
 * The Class <code>BiographyInfoPane</code> is a panel within the Description 
 * tab. It contains biography and physical description fields which may be 
 * updated by the user.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2011-12-29 10:08:51 +1100 (Thu, 29 Dec 2011) $
 * 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 * @version $Revision: 15691 $
 */
@SuppressWarnings("serial")
public class BiographyInfoPane extends JPanel implements CharacterInfoTab
{
	private static final String ALL_COMMAND = "ALL";
	private static final String NONE_COMMAND = "NONE";

	private final TabTitle title = new TabTitle("Biography");
	private final JButton allButton;
	private final JButton noneButton;
	private final JPanel itemsPanel = new JPanel(new GridBagLayout());

	/**
	 * Create a new instance of BiographyInfoPane.
	 */
	public BiographyInfoPane()
	{
		this.allButton = new JButton();
		this.noneButton = new JButton();
		initComponents();
	}

	private void initComponents()
	{
		setLayout(new GridBagLayout());
		Box vbox = Box.createVerticalBox();

		allButton.setText("All");
		allButton.setActionCommand(ALL_COMMAND);
		noneButton.setText("None");
		noneButton.setActionCommand(NONE_COMMAND);

		Box hbox = Box.createHorizontalBox();
		hbox.add(new JLabel("Check an item to include on your Character Sheet"));
		hbox.add(Box.createRigidArea(new Dimension(5, 0)));
		hbox.add(allButton);
		hbox.add(Box.createRigidArea(new Dimension(3, 0)));
		hbox.add(noneButton);
		vbox.add(hbox);

		vbox.add(Box.createVerticalStrut(10));
		vbox.add(itemsPanel);
		vbox.add(Box.createVerticalStrut(10));

		hbox = Box.createHorizontalBox();
		hbox.add(Box.createHorizontalGlue());
		JButton customItemButton = new JButton("Add custom Biography item");
		customItemButton.setEnabled(false);
		hbox.add(customItemButton);
		hbox.add(Box.createHorizontalGlue());
		vbox.add(hbox);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		add(vbox, gbc);
	}

	@Override
	public Hashtable<Object, Object> createModels(final CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(ItemHandler.class, new ItemHandler(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		((ItemHandler) state.get(ItemHandler.class)).install(this);
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		((ItemHandler) state.get(ItemHandler.class)).uninstall(this);
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	private class ItemHandler
	{

		private List<BioItem> bioItems = new ArrayList<BioItem>();
		//private CharacterFacade character;

		public ItemHandler(CharacterFacade character)
		{
			//this.character = character;
			bioItems.add(new NameItem(character));
			bioItems.add(new PlayerNameItem(character));
			bioItems.add(new GenderItem(character));
			bioItems.add(new HandedItem(character));
			bioItems.add(new AlignmentItem(character));
			bioItems.add(new DeityItem(character));
			bioItems.add(new AgeItem(character));
			bioItems.add(new SkinColorItem(character));
			bioItems.add(new HairColorItem(character));
			bioItems.add(new EyeColorItem(character));
			bioItems.add(new HeightItem(character));
			bioItems.add(new WeightItem(character));
		}

		public void install(BiographyInfoPane parent)
		{
			itemsPanel.removeAll();
			for (BioItem bioItem : bioItems)
			{
				bioItem.addComponents(itemsPanel);
				bioItem.install(parent);
			}
		}

		public void uninstall(BiographyInfoPane parent)
		{
			for (BioItem bioItem : bioItems)
			{
				bioItem.uninstall(parent);
			}
		}
	}

	private static class NameItem extends BioItem
	{

		public NameItem(final CharacterFacade character)
		{
			super("Name:", BiographyField.NAME, character);
			setTextFieldHandler(new TextFieldHandler(new JTextField(), character.getNameRef())
			{

				@Override
				protected void textChanged(String text)
				{
					character.setName(text);
				}

			});
		}

	}

	private static class PlayerNameItem extends BioItem
	{

		public PlayerNameItem(final CharacterFacade character)
		{
			super("Player:", BiographyField.PLAYERNAME, character);
			setTextFieldHandler(new TextFieldHandler(new JTextField(), character.getPlayersNameRef())
			{

				@Override
				protected void textChanged(String text)
				{
					character.setPlayersName(text);
				}

			});
		}

	}

	private static class GenderItem extends BioItem implements ReferenceListener<RaceFacade>
	{

		private CharacterComboBoxModel<GenderFacade> genderModel;

		public GenderItem(final CharacterFacade character)
		{
			super("Gender:", BiographyField.GENDER, character);
			genderModel = new CharacterComboBoxModel<GenderFacade>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setGender((GenderFacade) anItem);
				}

			};
			genderModel.setReference(character.getGenderRef());

			ReferenceFacade<RaceFacade> ref = character.getRaceRef();
			if (ref.getReference() != null)
			{
				genderModel.setListFacade(ref.getReference().getGenders());
			}
			else
			{
				genderModel.setListFacade(new DefaultListFacade<GenderFacade>());
			}

			setComboBoxModel(genderModel);

			checkVisible();
			ref.addReferenceListener(this);
		}

		@Override
		public void referenceChanged(ReferenceEvent<RaceFacade> e)
		{
			if (e.getNewReference() != null)
			{
				genderModel.setListFacade(e.getNewReference().getGenders());
			}
			else
			{
				genderModel.setListFacade(new DefaultListFacade<GenderFacade>());
			}
			checkVisible();
		}

		private void checkVisible()
		{
			setVisible(genderModel.getSize() != 0);
		}

	}

	private static class HandedItem extends BioItem implements ReferenceListener<RaceFacade>
	{

		private CharacterComboBoxModel<SimpleFacade> handsModel;

		public HandedItem(final CharacterFacade character)
		{
			super("Handed:", BiographyField.HANDED, character);
			handsModel = new CharacterComboBoxModel<SimpleFacade>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setHanded((SimpleFacade) anItem);
				}

			};
			handsModel.setReference(character.getHandedRef());

			ReferenceFacade<RaceFacade> ref = character.getRaceRef();
			if (ref.getReference() != null)
			{
				handsModel.setListFacade(ref.getReference().getHands());
			}
			else
			{
				handsModel.setListFacade(new DefaultListFacade<SimpleFacade>());
			}

			setComboBoxModel(handsModel);

			checkVisible();
			ref.addReferenceListener(this);
		}

		@Override
		public void referenceChanged(ReferenceEvent<RaceFacade> e)
		{
			if (e.getNewReference() != null)
			{
				handsModel.setListFacade(e.getNewReference().getHands());
			}
			else
			{
				handsModel.setListFacade(new DefaultListFacade<SimpleFacade>());
			}
			checkVisible();
		}

		private void checkVisible()
		{
			setVisible(handsModel.getSize() != 0);
		}

	}

	private static class AlignmentItem extends BioItem
	{

		public AlignmentItem(final CharacterFacade character)
		{
			super("Alignment:", BiographyField.ALIGNMENT, character);
			CharacterComboBoxModel<AlignmentFacade> alignmentModel = new CharacterComboBoxModel<AlignmentFacade>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setAlignment((AlignmentFacade) anItem);
				}

			};
			alignmentModel.setListFacade(character.getDataSet().getAlignments());
			alignmentModel.setReference(character.getAlignmentRef());
			setComboBoxModel(alignmentModel);
		}

	}

	private static class DeityItem extends BioItem
	{

		public DeityItem(final CharacterFacade character)
		{
			super("Deity:", BiographyField.DEITY, character);
			CharacterComboBoxModel<DeityFacade> deityModel = new CharacterComboBoxModel<DeityFacade>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setDeity((DeityFacade) anItem);
				}

			};
			deityModel.setListFacade(character.getDataSet().getDeities());
			deityModel.setReference(character.getDeityRef());
			setComboBoxModel(deityModel);
		}

	}

	private static class AgeItem extends BioItem
	{

		public AgeItem(final CharacterFacade character)
		{
			super("Age:", BiographyField.AGE, character);
			CharacterComboBoxModel<SimpleFacade> ageModel = new CharacterComboBoxModel<SimpleFacade>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setAgeCategory((SimpleFacade) anItem);
				}

			};
			ageModel.setListFacade(character.getAgeCategories());
			ageModel.setReference(character.getAgeCategoryRef());
			setComboBoxModel(ageModel);
			setFormattedFieldHandler(new FormattedFieldHandler(new JFormattedTextField(), character.getAgeRef())
			{

				@Override
				protected void valueChanged(int value)
				{
					character.setAge(value);
				}

			});
		}

	}

	private static class SkinColorItem extends BioItem
	{

		public SkinColorItem(final CharacterFacade character)
		{
			super("Skin Tone:", BiographyField.SKIN_TONE, character);
			setTextFieldHandler(new TextFieldHandler(new JTextField(), character.getSkinColorRef())
			{

				@Override
				protected void textChanged(String text)
				{
					character.setSkinColor(text);
				}

			});
		}

	}

	private static class HairColorItem extends BioItem
	{

		public HairColorItem(final CharacterFacade character)
		{
			super("Hair Color:", BiographyField.HAIR_COLOR, character);
			setTextFieldHandler(new TextFieldHandler(new JTextField(), character.getHairColorRef())
			{

				@Override
				protected void textChanged(String text)
				{
					character.setHairColor(text);
				}

			});
		}

	}

	private static class EyeColorItem extends BioItem
	{

		public EyeColorItem(final CharacterFacade character)
		{
			super("Eye Color:", BiographyField.EYE_COLOR, character);
			setTextFieldHandler(new TextFieldHandler(new JTextField(), character.getEyeColorRef())
			{

				@Override
				protected void textChanged(String text)
				{
					character.setEyeColor(text);
				}

			});
		}

	}

	private static class HeightItem extends BioItem
	{

		public HeightItem(final CharacterFacade character)
		{
			super("Height:", BiographyField.HEIGHT, character);
			setTrailingLabel(character.getDataSet().getGameMode().getHeightUnit());
			setFormattedFieldHandler(new FormattedFieldHandler(new JFormattedTextField(), character.getHeightRef())
			{

				@Override
				protected void valueChanged(int value)
				{
					character.setHeight(value);
				}

			});
		}

	}

	private static class WeightItem extends BioItem
	{

		public WeightItem(final CharacterFacade character)
		{
			super("Weight:", BiographyField.WEIGHT, character);
			setTrailingLabel(character.getDataSet().getGameMode().getWeightUnit());
			setFormattedFieldHandler(new FormattedFieldHandler(new JFormattedTextField(), character.getWeightRef())
			{

				@Override
				protected void valueChanged(int value)
				{
					character.setWeight(value);
				}

			});
		}

	}

	private static abstract class BioItem implements ActionListener, ItemListener 
	{

		private final JLabel label = new JLabel();
		private final JCheckBox checkbox = new JCheckBox();
		private JComboBox combobox = null;
		private JTextField textField = null;
		private JLabel trailinglabel = null; 
		private BiographyField bioField;
		private CharacterFacade character;

		protected BioItem(String text, BiographyField bioField, CharacterFacade character)
		{
			this.bioField = bioField;
			this.character = character;
			label.setText(text);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			checkbox.setSelected(character.getExportBioField(bioField));
		}

		public void addComponents(JPanel panel)
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.BOTH;
			panel.add(checkbox, gbc);
			gbc.insets = new Insets(1, 2, 1, 2);
			panel.add(label, gbc);
			int numComponents = 0;
			numComponents += textField != null ? 1 : 0;
			numComponents += combobox != null ? 1 : 0;
			numComponents += trailinglabel != null ? 1 : 0;
			switch (numComponents)
			{
				case 3:
					gbc.weightx = .3333;
					break;

				case 2:
					gbc.weightx = .5;
					break;

				default:
					gbc.weightx = 1.0;
					break;
			}
			if (combobox != null)
			{
				panel.add(combobox, gbc);
			}
			if (trailinglabel == null)
			{
				gbc.gridwidth = GridBagConstraints.REMAINDER;
			}
			if (textField != null)
			{
				panel.add(textField, gbc);
			}
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			if (trailinglabel != null)
			{
				panel.add(trailinglabel, gbc);
			}
			else if (numComponents < 2)
			{
				//We need a filler component so just use the lightweight Box
				panel.add(Box.createHorizontalGlue(), gbc);
			}
		}

		protected void setTextFieldHandler(TextFieldHandler handler)
		{
			if (textField != null)
			{
				throw new IllegalStateException("The TextField has already been set");
			}
			this.textField = handler.getTextField();
			handler.install();
		}

		protected void setFormattedFieldHandler(FormattedFieldHandler handler)
		{
			if (textField != null)
			{
				throw new IllegalStateException("The TextField has already been set");
			}
			this.textField = handler.getFormattedTextField();
			handler.install();
		}

		protected void setComboBoxModel(CharacterComboBoxModel<?> model)
		{
			if (combobox != null)
			{
				throw new IllegalStateException("The CharacterComboBoxModel has already been set");
			}
			this.combobox = new JComboBox(model);
			combobox.setPreferredSize(new Dimension(10, 20));
		}


		/**
		 * @param text The text to be displayed in a label after the entry fields.
		 */
		protected void setTrailingLabel(String text)
		{
			if (trailinglabel != null)
			{
				throw new IllegalStateException("The trailing label has already been set");
			}
			this.trailinglabel = new JLabel(text);
		}
		
		public void setVisible(boolean visible)
		{
			label.setVisible(visible);
			checkbox.setVisible(visible);
			if (combobox != null)
			{
				combobox.setVisible(visible);
			}
			if (textField != null)
			{
				textField.setVisible(visible);
			}
			if (trailinglabel != null)
			{
				trailinglabel.setVisible(visible);
			}
		}

		/**
		 * Installs this BioItem by attaching itself to the buttons.
		 * @param parent The pane holding this item.
		 */
		public void install(BiographyInfoPane parent)
		{
			parent.allButton.addActionListener(this);
			parent.noneButton.addActionListener(this);
			checkbox.addItemListener(this);
		}

		/**
		 * Uninstalls this BioItem by removing its listeners from the buttons.
		 * @param parent The pane holding this item.
		 */
		public void uninstall(BiographyInfoPane parent)
		{
			parent.allButton.removeActionListener(this);
			parent.noneButton.removeActionListener(this);
			checkbox.removeItemListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (ALL_COMMAND.equals(e.getActionCommand()))
			{
				checkbox.setSelected(true);
				character.setExportBioField(bioField, true);
			}
			else if (NONE_COMMAND.equals(e.getActionCommand()))
			{
				checkbox.setSelected(false);
				character.setExportBioField(bioField, false);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			boolean selected = e.getStateChange() == ItemEvent.SELECTED;
			character.setExportBioField(bioField, selected);
		}

	}

}
