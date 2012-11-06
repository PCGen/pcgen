/*
 * pcgen - DESCRIPTION OF PACKAGE Copyright (C) 2004 Ross M. Lodge
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 675 Mass
 * Ave, Cambridge, MA 02139, USA.
 *
 * The author of this program grants you the ability to use this code in
 * conjunction with code that is covered under the Open Gaming License
 *
 * CheckDialog.java
 *
 * Created on Jan 31, 2004, 1:07:58 PM
 */
package plugin.initiative.gui;

import plugin.initiative.DiceRollModel;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.NumberFormatter;
import java.awt.Component;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * <p>
 * A generic check dialog; handles an expression with a DC
 * </p>
 *
 * @author Ross M. Lodge
 *
 */
public class CheckDialog extends DiceRollDialog
{
	/** A default dc so we can carry over to next use of dialog. */
	private static int m_defaultDC = 15;

	/** Formatted text field for the dc */
	private JFormattedTextField m_dc;

	/**
	 * <p>
	 * Constructs the dialog; initializes components
	 * </p>
	 *
	 * @param model
	 *            A DiceRollModel
	 * @throws HeadlessException
	 */
	public CheckDialog(DiceRollModel model) throws HeadlessException
	{
		super(model);
	}

	/**
	 * <p>
	 * Initializes the DC value
	 * </p>
	 *
	 * @param labelText
	 */
	protected void initDC(String labelText)
	{
		NumberFormatter formatter =
				new NumberFormatter(new DecimalFormat("##"));
		formatter.setValueClass(Integer.class);
		m_dc = new JFormattedTextField(formatter);
		m_dc.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		m_dc.setValue(Integer.valueOf(m_defaultDC));
		JLabel label = new JLabel("DC:");
		label.setAlignmentX(Component.RIGHT_ALIGNMENT);
		addComponent(m_dc, label);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see plugin.initiative.gui.DiceRollDialog#initComponents()
	 */
    @Override
	protected void initComponents()
	{
		/*
		 * Dialog will consist of Roll: [ ] DC: [ ] Result: [ ] [ Roll ] [ Ok ]
		 */

		//Set basic properties
		initDC("DC:");
		super.initComponents();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see plugin.initiative.gui.DiceRollDialog#setResult(int)
	 */
    @Override
	protected void setResult(int result)
	{
		m_result.setText("<html><body><b>"
			+ result
			+ ((result >= ((Integer) m_dc.getValue()).intValue()) ? " (passed)"
				: "") + "</b></body></html>");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see plugin.initiative.gui.DiceRollDialog#initListeners()
	 */
    @Override
	protected void initListeners()
	{
		super.initListeners();
		m_dc.addPropertyChangeListener(new PropertyChangeListener()
		{
            @Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if ("value".equals(evt.getPropertyName()))
				{
					m_defaultDC = ((Integer) m_dc.getValue()).intValue();
				}
			}
		});
	}

}
