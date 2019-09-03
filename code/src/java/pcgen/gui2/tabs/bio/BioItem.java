/*
 * Copyright (c) 2019 Tom Parker <thpr@users.sourceforge.net>
 * extracted from BiographyInfoPane.java
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
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Optional;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.util.ManagedField;
import pcgen.system.LanguageBundle;

abstract class BioItem
{

	private final JLabel label = new JLabel();
	private final JCheckBox exportEnabledBox = new JCheckBox();
	private Optional<JComboBox<?>> combobox = Optional.empty();
	private Optional<JTextField> textField = Optional.empty();
	private Optional<JLabel> trailinglabel = Optional.empty();
	private final ItemListener enabledForExportListener;
	
	/**
	 * The ManagedField holding the information for this BioItem.
	 */
	private Optional<ManagedField> textFieldHandler = Optional.empty();

	protected BioItem(String text, BiographyField bioField, CharacterFacade character)
	{
		if (text.startsWith("in_")) //$NON-NLS-1$
		{
			label.setText(LanguageBundle.getString(text) + Constants.COLON);
		}
		else
		{
			label.setText(text);
		}
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		exportEnabledBox.setSelected(character.getExportBioField(bioField));
		enabledForExportListener =
				event -> character.setExportBioField(bioField,
					event.getStateChange() == ItemEvent.SELECTED);
	}

	public void addComponents(JPanel panel)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(exportEnabledBox, gbc);
		gbc.insets = new Insets(1, 2, 1, 2);
		panel.add(label, gbc);
		int numComponents = 0;
		numComponents += textField.isPresent() ? 1 : 0;
		numComponents += combobox.isPresent() ? 1 : 0;
		numComponents += trailinglabel.isPresent() ? 1 : 0;
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
		combobox.ifPresent(box -> panel.add(box, gbc));
		if (trailinglabel.isEmpty())
		{
			gbc.gridwidth = GridBagConstraints.REMAINDER;
		}
		textField.ifPresent(field -> panel.add(field, gbc));
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		if (trailinglabel.isPresent())
		{
			panel.add(trailinglabel.get(), gbc);
		}
		else if (numComponents < 2)
		{
			//We need a filler component so just use the lightweight Box
			panel.add(Box.createHorizontalGlue(), gbc);
		}
	}

	protected void setTextFieldHandler(ManagedField handler)
	{
		textField.ifPresent(tField -> {
			throw new IllegalStateException(
				"The TextField has already been set"); //$NON-NLS-1$
		});
		this.textField = Optional.of(handler.getTextField());
		textFieldHandler = Optional.of(handler);
	}

	protected void setComboBoxModel(CharacterComboBoxModel<?> model)
	{
		combobox.ifPresent(box -> {
			throw new IllegalStateException(
				"The CharacterComboBoxModel has already been set"); //$NON-NLS-1$
		});
		JComboBox<?> newComboBox = new JComboBox<>(model);
		this.combobox = Optional.of(newComboBox);
		newComboBox.setPreferredSize(new Dimension(10,
			BiographyInfoPane.TEMPLATE_TEXT_FIELD.getPreferredSize().height));
	}

	/**
	 * @param text The text to be displayed in a label after the entry fields.
	 */
	protected void setTrailingLabel(String text)
	{
		trailinglabel.ifPresent(box -> {
			throw new IllegalStateException(
				"The trailing label has already been set"); //$NON-NLS-1$
		});
		this.trailinglabel = Optional.of(new JLabel(text));
	}

	public void setVisible(boolean visible)
	{
		label.setVisible(visible);
		exportEnabledBox.setVisible(visible);
		combobox.ifPresent(box -> box.setVisible(visible));
		textField.ifPresent(field -> field.setVisible(visible));
		trailinglabel.ifPresent(endLabel -> endLabel.setVisible(visible));
	}

	/**
	 * Installs this BioItem by attaching itself to the buttons.
	 */
	public void install()
	{
		exportEnabledBox.addItemListener(enabledForExportListener);
		textFieldHandler.ifPresent(ManagedField::install);
	}

	/**
	 * Uninstalls this BioItem by removing its listeners from the buttons.
	 */
	public void uninstall()
	{
		exportEnabledBox.removeItemListener(enabledForExportListener);
		textFieldHandler.ifPresent(ManagedField::uninstall);
	}

	public void setExportable(boolean export)
	{
		exportEnabledBox.setSelected(export);
	}
}
