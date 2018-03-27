/*
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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.core.PCAlignment;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DeityFacade;
import pcgen.facade.core.GenderFacade;
import pcgen.facade.core.HandedFacade;
import pcgen.facade.core.SimpleFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tabs.models.FormattedFieldHandler;
import pcgen.gui2.tabs.models.TextFieldHandler;
import pcgen.gui2.util.ScrollablePanel;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code BiographyInfoPane} is a panel within the Description
 * tab. It contains biography and physical description fields which may be 
 * updated by the user.
 *
 * 
 */
@SuppressWarnings("serial")
public class BiographyInfoPane extends JPanel implements CharacterInfoTab
{
	private static final String ALL_COMMAND = "ALL"; //$NON-NLS-1$
	private static final String NONE_COMMAND = "NONE"; //$NON-NLS-1$
	private static final JTextField templateTextField = new JTextField(
		"PrototypeDisplayText"); //$NON-NLS-1$;
	/** The fields that we always display */
	private static final EnumSet<BiographyField> defaultBioFieds = EnumSet
		.range(BiographyField.NAME, BiographyField.WEIGHT);

	private final TabTitle title = new TabTitle(
		LanguageBundle.getString("in_descBiography"), null); //$NON-NLS-1$
	private final JButton allButton;
	private final JButton noneButton;
	private final JPanel itemsPanel;
	private JButton addCustomItemButton;
	private JScrollPane detailsScroll;

	/**
	 * Create a new instance of BiographyInfoPane.
	 */
	public BiographyInfoPane()
	{
		this.allButton = new JButton();
		this.noneButton = new JButton();
		this.itemsPanel = new ScrollablePanel(20);
		initComponents();
	}

	private void initComponents()
	{
		setLayout(new GridBagLayout());
		Box vbox = Box.createVerticalBox();

		allButton.setText(LanguageBundle.getString("in_all")); //$NON-NLS-1$
		allButton.setActionCommand(ALL_COMMAND);
		noneButton.setText(LanguageBundle.getString("in_none")); //$NON-NLS-1$
		noneButton.setActionCommand(NONE_COMMAND);

		Box hbox = Box.createHorizontalBox();
		hbox.add(new JLabel(LanguageBundle.getString("in_descCheckItem"))); //$NON-NLS-1$
		hbox.add(Box.createRigidArea(new Dimension(5, 0)));
		hbox.add(allButton);
		hbox.add(Box.createRigidArea(new Dimension(3, 0)));
		hbox.add(noneButton);
		vbox.add(hbox);

		itemsPanel.setLayout(new GridBagLayout());
		itemsPanel.setBorder(new EmptyBorder(8, 5, 8, 5) );

		vbox.add(Box.createVerticalStrut(10));
		detailsScroll = new JScrollPane(itemsPanel);
		detailsScroll.setPreferredSize(detailsScroll.getMaximumSize());
		detailsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		detailsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		detailsScroll.setMinimumSize(new Dimension(600, 0));
		vbox.add(detailsScroll);
		vbox.add(Box.createVerticalStrut(10));

		hbox = Box.createHorizontalBox();
		hbox.add(Box.createHorizontalGlue());
		addCustomItemButton = new JButton();
		hbox.add(addCustomItemButton);
		hbox.add(Box.createHorizontalGlue());
		vbox.add(hbox);
		vbox.add(Box.createVerticalGlue());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		add(vbox, gbc);
	}

	@Override
	public ModelMap createModels(final CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(ItemHandler.class, new ItemHandler(character));
		models.put(AddCustomAction.class, new AddCustomAction(character));
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(ItemHandler.class).install(this);
		addCustomItemButton.setAction(models.get(AddCustomAction.class));
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(ItemHandler.class).uninstall(this);
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	private class ItemHandler implements ListListener<BiographyField>
	{

		private final ListFacade<BiographyField> customFields;
		private final List<BioItem> bioItems = new ArrayList<>();
		private final Map<BiographyField, BioItem> customFieldMap =
                new EnumMap<>(
                        BiographyField.class);
		private final CharacterFacade character;
		private BiographyInfoPane detailsPane;

		public ItemHandler(CharacterFacade character2)
		{
			this.character = character2;
			bioItems.add(new NameItem(character));
			bioItems.add(new PlayerNameItem(character));
			bioItems.add(new GenderItem(character));
			bioItems.add(new HandedItem(character));
			if (!character.getDataSet().getAlignments().isEmpty())
			{
				bioItems.add(new AlignmentItem(character));
			}
			bioItems.add(new DeityItem(character));
			bioItems.add(new AgeItem(character));
			bioItems.add(new SkinColorItem(character));
			bioItems.add(new HairColorItem(character));
			bioItems.add(new HairStyleItem(character));
			bioItems.add(new EyeColorItem(character));
			bioItems.add(new HeightItem(character));
			bioItems.add(new WeightItem(character));
			
			customFields = character.getDescriptionFacade().getCustomBiographyFields();
			
			for (BiographyField field : customFields)
			{
				BioItem item;
				if (field == BiographyField.REGION)
				{
					item = new RegionItem(character);
				}
				else
				{
					item = new BiographyFieldBioItem(field, character);
				}
				customFieldMap.put(field, item);
			}
		}

		public void install(BiographyInfoPane parent)
		{
			detailsPane = parent;
			itemsPanel.removeAll();
			// 
			for (BioItem bioItem : bioItems)
			{
				bioItem.addComponents(itemsPanel);
				bioItem.install(parent);
			}
			for (BioItem bioItem : customFieldMap.values())
			{
				bioItem.addComponents(itemsPanel);
				bioItem.install(parent);
			}

			customFields.addListListener(this);
			detailsScroll.setPreferredSize(itemsPanel.getPreferredSize());
			detailsScroll.invalidate();
		}

		public void uninstall(BiographyInfoPane parent)
		{
			for (BioItem bioItem : bioItems)
			{
				bioItem.uninstall(parent);
			}
			for (BioItem bioItem : customFieldMap.values())
			{
				bioItem.uninstall(parent);
			}
			detailsPane = null;
			customFields.removeListListener(this);
		}

		@Override
		public void elementAdded(ListEvent<BiographyField> e)
		{
			BiographyField field = e.getElement();
			BioItem bioItem = new BiographyFieldBioItem(field, character);
			customFieldMap.put(field, bioItem);
			bioItem.addComponents(itemsPanel);
			bioItem.install(detailsPane);
			detailsPane.validate();
			detailsScroll.setPreferredSize(itemsPanel.getPreferredSize());
			detailsScroll.repaint();
		}

		@Override
		public void elementRemoved(ListEvent<BiographyField> e)
		{
			BiographyField field = e.getElement();
			BioItem bioItem = new BiographyFieldBioItem(field, character);
			customFieldMap.put(field, bioItem);
			bioItem.uninstall(detailsPane);
			detailsPane.invalidate();
		}

		@Override
		public void elementsChanged(ListEvent<BiographyField> e)
		{
			BiographyInfoPane parent = detailsPane;
			uninstall(parent);
			install(parent);
			detailsPane.invalidate();
		}

		@Override
		public void elementModified(ListEvent<BiographyField> e)
		{
			// Ignored.
		}
	}

	private static class NameItem extends BioItem
	{

		public NameItem(final CharacterFacade character)
		{
			super("in_nameLabel", BiographyField.NAME, character); //$NON-NLS-1$
			setTextFieldHandler(new TextFieldHandler(new JTextField(30), character.getNameRef())
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
			super("in_player", BiographyField.PLAYERNAME, character); //$NON-NLS-1$
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

	private static class GenderItem extends BioItem
	{

		private final CharacterComboBoxModel<GenderFacade> genderModel;

		public GenderItem(final CharacterFacade character)
		{
			super("in_gender", BiographyField.GENDER, character); //$NON-NLS-1$
			genderModel = new CharacterComboBoxModel<GenderFacade>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setGender((GenderFacade) anItem);
				}

			};
			genderModel.setReference(character.getGenderRef());
            genderModel.setListFacade(character.getAvailableGenders());
			setComboBoxModel(genderModel);

			checkVisible();
		}

		private void checkVisible()
		{
			setVisible(genderModel.getSize() != 0);
		}

	}

	private static class HandedItem extends BioItem
	{

		private final CharacterComboBoxModel<HandedFacade> handsModel;

		public HandedItem(final CharacterFacade character)
		{
			super("in_handString", BiographyField.HANDED, character); //$NON-NLS-1$
			handsModel = new CharacterComboBoxModel<HandedFacade>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setHanded((HandedFacade) anItem);
				}

			};
			handsModel.setReference(character.getHandedRef());
            handsModel.setListFacade(character.getAvailableHands());
			setComboBoxModel(handsModel);

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
			super("in_alignString", BiographyField.ALIGNMENT, character); //$NON-NLS-1$
			CharacterComboBoxModel<PCAlignment> alignmentModel = new CharacterComboBoxModel<PCAlignment>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setAlignment((PCAlignment) anItem);
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
			super("in_deity", BiographyField.DEITY, character); //$NON-NLS-1$
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
			super("in_age", BiographyField.AGE, character); //$NON-NLS-1$
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
			super("in_appSkintoneColor", BiographyField.SKIN_TONE, character); //$NON-NLS-1$
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
			super("in_appHairColor", BiographyField.HAIR_COLOR, character); //$NON-NLS-1$
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
			super("in_appEyeColor", BiographyField.EYE_COLOR, character); //$NON-NLS-1$
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
			super("in_height", BiographyField.HEIGHT, character); //$NON-NLS-1$
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
			super("in_weight", BiographyField.WEIGHT, character); //$NON-NLS-1$
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

	private static class RegionItem extends BioItem
	{

		public RegionItem(final CharacterFacade character)
		{
			super("in_region", BiographyField.REGION, character); //$NON-NLS-1$
			final JTextField regionField = new JTextField();
			regionField.setEditable(false);
			setTextFieldHandler(new TextFieldHandler(regionField, character
				.getDescriptionFacade()
				.getBiographyField(BiographyField.REGION))
			{
				@Override
				protected void textChanged(String text)
				{
					// Ignored for a non-editable field.
				}

			});
		}

	}

	private static class HairStyleItem extends BiographyFieldBioItem
	{
		public HairStyleItem(final CharacterFacade character)
		{
			super("in_style", BiographyField.HAIR_STYLE, character); //$NON-NLS-1$
		}
	}

	/**
	 * The Class {@code BiographyFieldBioItem} manages a row displaying a
	 * textual biography item and allowing it to be edited and suppressed 
	 * from output.
	 */
	private static class BiographyFieldBioItem extends BioItem
	{

		public BiographyFieldBioItem(final String titleKey, final BiographyField field, final CharacterFacade character)
		{
			super(titleKey, field, character);
			setTextFieldHandler(new TextFieldHandler(new JTextField(), character.getDescriptionFacade().getBiographyField(field))
			{
				@Override
				protected void textChanged(String text)
				{
					character.getDescriptionFacade().setBiographyField(field, text);
				}

			});
		}

		public BiographyFieldBioItem(final BiographyField field, final CharacterFacade character)
		{
			super(field.getIl8nKey(), field, character);
			setTextFieldHandler(new TextFieldHandler(new JTextField(), character.getDescriptionFacade().getBiographyField(field))
			{
				@Override
				protected void textChanged(String text)
				{
					character.getDescriptionFacade().setBiographyField(field, text);
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
		private final BiographyField bioField;
		private final CharacterFacade character;
		private TextFieldHandler textFieldHandler;
		private FormattedFieldHandler formattedFieldHandler;

		protected BioItem(String text, BiographyField bioField, CharacterFacade character)
		{
			this.bioField = bioField;
			this.character = character;
			if (text.startsWith("in_")) //$NON-NLS-1$
			{
				label.setText(LanguageBundle.getString(text) + ":"); //$NON-NLS-1$
			}
			else
			{
				label.setText(text);
			}
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			if (character != null)
			{
				checkbox.setSelected(character.getExportBioField(bioField));
			}
		}

		public void addComponents(JPanel panel)
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.PAGE_START;
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
					gbc.weightx = 0.3333;
					break;

				case 2:
					gbc.weightx = 0.5;
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
				throw new IllegalStateException("The TextField has already been set"); //$NON-NLS-1$
			}
			this.textField = handler.getTextField();
			textFieldHandler = handler;
		}

		protected void setFormattedFieldHandler(FormattedFieldHandler handler)
		{
			if (textField != null)
			{
				throw new IllegalStateException("The TextField has already been set"); //$NON-NLS-1$
			}
			this.textField = handler.getFormattedTextField();
			formattedFieldHandler = handler;
		}

		protected void setComboBoxModel(CharacterComboBoxModel<?> model)
		{
			if (combobox != null)
			{
				throw new IllegalStateException("The CharacterComboBoxModel has already been set"); //$NON-NLS-1$
			}
			this.combobox = new JComboBox<>(model);
			combobox.setPreferredSize(new Dimension(10, templateTextField.getPreferredSize().height));
		}


		/**
		 * @param text The text to be displayed in a label after the entry fields.
		 */
		protected void setTrailingLabel(String text)
		{
			if (trailinglabel != null)
			{
				throw new IllegalStateException("The trailing label has already been set"); //$NON-NLS-1$
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
			if (textFieldHandler != null)
			{
				textFieldHandler.install();
			}
			if (formattedFieldHandler != null)
			{
				formattedFieldHandler.install();
			}
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
			if (textFieldHandler != null)
			{
				textFieldHandler.uninstall();
			}
			if (formattedFieldHandler != null)
			{
				formattedFieldHandler.uninstall();
			}
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

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			boolean selected = e.getStateChange() == ItemEvent.SELECTED;
			character.setExportBioField(bioField, selected);
		}

	}


	/**
	 * The Class {@code AddAction} acts on a user pressing the Add Custom
	 * Details button.
	 */
	private class AddCustomAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddCustomAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_descAddDetail")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Build list of choices
			List<BiographyField> availFields =
                    new ArrayList<>(Arrays.asList(BiographyField.values()));
			availFields.removeAll(defaultBioFieds);
			for (BiographyField field : character.getDescriptionFacade()
				.getCustomBiographyFields())
			{
				availFields.remove(field);
			}
			if (availFields.isEmpty())
			{
				JOptionPane
					.showMessageDialog(
						JOptionPane.getFrameForComponent(addCustomItemButton),
						LanguageBundle.getString("in_descNoMoreDetails"), //$NON-NLS-1$
						Constants.APPLICATION_NAME,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			String fieldNames[] = new String[availFields.size()];
			int i = 0;		
			for (BiographyField biographyField : availFields)
			{
				fieldNames[i++] = LanguageBundle.getString(biographyField.getIl8nKey());
			}

			// Show dialog to choose fields
			String s =
					(String) JOptionPane.showInputDialog(
						JOptionPane.getFrameForComponent(addCustomItemButton),
			                    LanguageBundle.getString("in_descAddFieldMsg"), //$NON-NLS-1$
			                    LanguageBundle.getString("in_descAddFieldTitle"), //$NON-NLS-1$
			                    JOptionPane.QUESTION_MESSAGE,
			                    null,
			                    fieldNames,
			                    fieldNames[0]);

			// Check if a selection was made
			if (StringUtils.isEmpty(s))
			{
				return;
			}
			
			// Add the chosen field to the character
			for (BiographyField field : availFields)
			{
				if (s.equals( LanguageBundle.getString(field.getIl8nKey())))
				{
					character.getDescriptionFacade().addCustomBiographyField(field);
					break;
				}
			}
		}

	}
	
}
