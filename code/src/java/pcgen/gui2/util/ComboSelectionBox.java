/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.util;

import java.awt.BorderLayout;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;

public class ComboSelectionBox extends JPanel implements ItemSelectable
{

	private static final long serialVersionUID = 4240590146578106112L;
	private ComboSelectionDialog dialog;
	private MutableComboBoxModel model;
	private JComboBox comboBox;
	private JButton button;

	public ComboSelectionBox()
	{
		super(new BorderLayout());
		initComponents();
	}

	private void initComponents()
	{
		comboBox = new JComboBox();

		setBorder(comboBox.getBorder());
		comboBox.setBorder(BorderFactory.createEmptyBorder());

		button = new JButton(new ButtonAction());
		button.setEnabled(false);
		button.setMargin(new java.awt.Insets(0, 0, 0, 0));

		add(comboBox, BorderLayout.CENTER);
		add(button, BorderLayout.LINE_END);
	}

	private void checkButton()
	{
		if (dialog != null && model != null)
		{
			dialog.setModel(model);
			button.setEnabled(true);
		}
		else
		{
			button.setEnabled(false);
		}
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		comboBox.setEnabled(enabled);
		if (enabled)
		{
			checkButton();
		}
		else
		{
			button.setEnabled(false);
		}
	}

	public void setModel(MutableComboBoxModel model)
	{
		this.model = model;
		comboBox.setModel(model);
		checkButton();
	}

	public void setDialog(ComboSelectionDialog dialog)
	{
		this.dialog = dialog;
		checkButton();
	}

	public Object getSelectedItem()
	{
		return comboBox.getSelectedItem();
	}

	@Override
	public Object[] getSelectedObjects()
	{
		return comboBox.getSelectedObjects();
	}

	@Override
	public void addItemListener(ItemListener l)
	{
		comboBox.addItemListener(l);
	}

	@Override
	public void removeItemListener(ItemListener l)
	{
		comboBox.removeItemListener(l);
	}

	private class ButtonAction extends AbstractAction
	{

		public ButtonAction()
		{
			super("...");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{

			SwingUtilities.invokeLater(dialog::display);
		}

	}
}
