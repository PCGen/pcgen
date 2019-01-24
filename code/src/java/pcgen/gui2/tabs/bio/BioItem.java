/*
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

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.util.ManagedField;
import pcgen.system.LanguageBundle;

abstract class BioItem implements ItemListener
{

	private final JLabel label = new JLabel();
	private final JCheckBox checkbox = new JCheckBox();
	private JComboBox combobox = null;
	private JTextField textField = null;
	private JLabel trailinglabel = null;
	private final BiographyField bioField;
	private final CharacterFacade character;
	
	/**
	 * The ManagedField holding the information for this BioItem.
	 */
	private ManagedField textFieldHandler;
	private ManagedField formattedFieldHandler;

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

	protected void setTextFieldHandler(ManagedField handler)
	{
		if (textField != null)
		{
			throw new IllegalStateException("The TextField has already been set"); //$NON-NLS-1$
		}
		this.textField = handler.getTextField();
		textFieldHandler = handler;
	}

	protected void setFormattedFieldHandler(ManagedField handler)
	{
		if (textField != null)
		{
			throw new IllegalStateException("The TextField has already been set"); //$NON-NLS-1$
		}
		this.textField = handler.getTextField();
		formattedFieldHandler = handler;
	}

	protected void setComboBoxModel(CharacterComboBoxModel<?> model)
	{
		if (combobox != null)
		{
			throw new IllegalStateException("The CharacterComboBoxModel has already been set"); //$NON-NLS-1$
		}
		this.combobox = new JComboBox<>(model);
		combobox.setPreferredSize(new Dimension(10, BiographyInfoPane.TEMPLATE_TEXT_FIELD.getPreferredSize().height));
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
	 */
	public void install()
	{
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
	public void uninstall()
	{
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
	public void itemStateChanged(ItemEvent e)
	{
		boolean selected = e.getStateChange() == ItemEvent.SELECTED;
		character.setExportBioField(bioField, selected);
	}

	public void setExportable(boolean export)
	{
		checkbox.setSelected(export);
	}

}