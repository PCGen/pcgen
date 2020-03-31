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
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tabs.models.FormattedFieldHandler;
import pcgen.gui2.tabs.models.TextFieldHandler;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code BiographyInfoPane} is a panel within the Description
 * tab. It contains biography and physical description fields which may be 
 * updated by the user.
 *
 * 
 */
@SuppressWarnings("serial")
public final class BiographyInfoPane extends JPanel implements CharacterInfoTab
{
	static final JTextField TEMPLATE_TEXT_FIELD = new JTextField("PrototypeDisplayText"); //$NON-NLS-1$;

	private final TabTitle title = new TabTitle(LanguageBundle.getString("in_descBiography"), null); //$NON-NLS-1$
	private final JPanel itemsPanel;
	private final JScrollPane detailsScroll;

	/**
	 * Create a new instance of BiographyInfoPane.
	 */
	public BiographyInfoPane()
	{
		this.itemsPanel = new JPanel();
		setLayout(new GridBagLayout());
		Box vbox = Box.createVerticalBox();

		itemsPanel.setLayout(new GridBagLayout());
		itemsPanel.setBorder(new EmptyBorder(8, 5, 8, 5));

		vbox.add(Box.createVerticalStrut(10));
		detailsScroll = new JScrollPane(itemsPanel);
		detailsScroll.setPreferredSize(detailsScroll.getMaximumSize());
		detailsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		detailsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		detailsScroll.setMinimumSize(new Dimension(600, 0));
		vbox.add(detailsScroll);
		vbox.add(Box.createVerticalStrut(10));

		Box hbox = Box.createHorizontalBox();
		hbox.add(Box.createHorizontalGlue());
		JButton addCustomItemButton = new JButton();
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
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(ItemHandler.class).install();
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(ItemHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	private class ItemHandler
	{

		private final List<BioItem> bioItems = new ArrayList<>();

		public ItemHandler(CharacterFacade character)
		{
			bioItems.add(new NameItem(character));
			bioItems.add(new PlayerNameItem(character));
			bioItems.add(new GenderItem(character));
			bioItems.add(new HandedItem(character));
			if (!character.getDataSet().getAlignments().isEmpty())
			{
				bioItems.add(new AlignmentItem(character));
			}
			if (character.isFeatureEnabled(CControl.DOMAINFEATURE))
			{
				bioItems.add(new DeityItem(character));
			}

			bioItems.add(new AgeItem(character));
			bioItems.add(new SkinColorItem(character));
			bioItems.add(new HairColorItem(character));
			bioItems.add(new EyeColorItem(character));
			bioItems.add(new HeightItem(character));
			bioItems.add(new WeightItem(character));

			bioItems.add(new BiographyFieldBioItem(BiographyField.SPEECH_PATTERN, PCStringKey.SPEECHTENDENCY,
					character
			));
			bioItems.add(new BiographyFieldBioItem(BiographyField.BIRTHDAY, PCStringKey.BIRTHDAY, character));
			bioItems.add(new BiographyFieldBioItem(BiographyField.LOCATION, PCStringKey.LOCATION, character));
			bioItems.add(new BiographyFieldBioItem(BiographyField.CITY, PCStringKey.CITY, character));
			bioItems.add(new RegionItem(character));
			bioItems.add(new BiographyFieldBioItem(BiographyField.BIRTHPLACE, PCStringKey.BIRTHPLACE, character));
			bioItems.add(new BiographyFieldBioItem(BiographyField.PERSONALITY_TRAIT_1, PCStringKey.PERSONALITY1,
					character
			));
			bioItems.add(new BiographyFieldBioItem(BiographyField.PERSONALITY_TRAIT_2, PCStringKey.PERSONALITY2,
					character
			));
			bioItems.add(new BiographyFieldBioItem(BiographyField.PHOBIAS, PCStringKey.PHOBIAS, character));
			bioItems.add(new BiographyFieldBioItem(BiographyField.INTERESTS, PCStringKey.INTERESTS, character));
			bioItems.add(new BiographyFieldBioItem(BiographyField.CATCH_PHRASE, PCStringKey.CATCHPHRASE, character));
		}

		public void install()
		{
			itemsPanel.removeAll();

			for (BioItem bioItem : bioItems)
			{
				bioItem.addComponents(itemsPanel);
				bioItem.install();
			}

			detailsScroll.setPreferredSize(itemsPanel.getPreferredSize());
			detailsScroll.invalidate();
		}

		public void uninstall()
		{
			bioItems.forEach(BioItem::uninstall);
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

		private final CharacterComboBoxModel<Gender> genderModel;

		public GenderItem(final CharacterFacade character)
		{
			super("in_gender", BiographyField.GENDER, character); //$NON-NLS-1$
			genderModel = new CharacterComboBoxModel<>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setGender((Gender) anItem);
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

		private final CharacterComboBoxModel<Handed> handsModel;

		public HandedItem(final CharacterFacade character)
		{
			super("in_handString", BiographyField.HANDED, character); //$NON-NLS-1$
			handsModel = new CharacterComboBoxModel<>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setHanded((Handed) anItem);
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
			CharacterComboBoxModel<PCAlignment> alignmentModel = new CharacterComboBoxModel<>()
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
			CharacterComboBoxModel<Deity> deityModel = new CharacterComboBoxModel<>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setDeity((Deity) anItem);
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
			CharacterComboBoxModel<String> ageModel = new CharacterComboBoxModel<>()
			{

				@Override
				public void setSelectedItem(Object anItem)
				{
					character.setAgeCategory((String) anItem);
				}

			};
			ageModel.setListFacade(character.getAgeCategories());
			ageModel.setReference(character.getAgeCategoryRef());
			setComboBoxModel(ageModel);
			setTextFieldHandler(new FormattedFieldHandler(new JFormattedTextField(), character.getAgeRef())
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
			setTextFieldHandler(new FormattedFieldHandler(new JFormattedTextField(), character.getHeightRef())
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
			setTextFieldHandler(new FormattedFieldHandler(new JFormattedTextField(), character.getWeightRef())
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
			setTextFieldHandler(new TextFieldHandler(regionField,
				character.getDescriptionFacade().getBiographyField(BiographyField.REGION))
			{
				@Override
				protected void textChanged(String text)
				{
					// Ignored for a non-editable field.
				}

			});
		}

	}

	/**
	 * The Class {@code BiographyFieldBioItem} manages a row displaying a
	 * textual biography item and allowing it to be edited and suppressed 
	 * from output.
	 */
	private static class BiographyFieldBioItem extends BioItem
	{

		public BiographyFieldBioItem(BiographyField field, PCStringKey attribute, CharacterFacade character)
		{
			super(field.getIl8nKey(), field, character);
			setTextFieldHandler(
				new TextFieldHandler(new JTextField(), character.getDescriptionFacade().getBiographyField(field))
				{
					@Override
					protected void textChanged(String text)
					{
						character.getDescriptionFacade().setBiographyField(field, attribute, text);
					}

				});
		}

	}
}
