/*
 * Copyright James Dempsey, 2013
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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FontManipulation;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code RadioChooserDialog} provides a dialog with a variable
 * number of radio buttons to allow the user to make a single choice from a 
 * list. A ChooserFacade instance must be supplied, this defines the choices 
 * available, the text to be displayed on screen and the actions to be taken 
 * when the user confirms their choices. The chooser is generally displayed 
 * via a call to UIDelgate.showGeneralChooser.
 */
@SuppressWarnings("serial")
public class RadioChooserDialog extends JDialog implements ActionListener
{

	private final ChooserFacade chooser;
	private boolean committed;
	private ButtonGroup avaGroup = null;
	private JRadioButton[] avaRadioButton = null;
	private JPanel buttonPanel;
	private JRadioButton selectedButton;

	/**
	 * Create a new instance of RadioChooserDialog for selecting from the data 
	 * supplied in the chooserFacade. 
	 * @param frame The window we are opening relative to.
	 * @param chooser The definition of what should be displayed.
	 */
	public RadioChooserDialog(Frame frame, ChooserFacade chooser)
	{
		super(frame, true);
		this.chooser = chooser;

		initComponents();
		pack();
	}

	private void initComponents()
	{
		setTitle(LanguageBundle.getString("in_chooserSelectOne")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel(chooser.getName());
		FontManipulation.title(titleLabel);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		pane.add(titleLabel, BorderLayout.NORTH);

		buildButtonPanel();

		pane.add(buttonPanel, BorderLayout.CENTER);

		JPanel bottomPane = new JPanel(new FlowLayout());
		JButton button = new JButton(LanguageBundle.getString("in_ok")); //$NON-NLS-1$
		button.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok")); //$NON-NLS-1$
		button.setActionCommand("OK");
		button.addActionListener(this);
		bottomPane.add(button);
		button = new JButton(LanguageBundle.getString("in_cancel")); //$NON-NLS-1$
		button.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel")); //$NON-NLS-1$
		button.setActionCommand("CANCEL");
		button.addActionListener(this);
		bottomPane.add(button);
		pane.add(bottomPane, BorderLayout.SOUTH);
	}

	/**
	 * Create the panel of radio buttons.
	 */
	private void buildButtonPanel()
	{
		ListFacade<InfoFacade> availableList = chooser.getAvailableList();
		int row = 0;
		avaRadioButton = new JRadioButton[availableList.getSize()];
		avaGroup = new ButtonGroup();

		// Create the buttons
		for (InfoFacade infoFacade : availableList)
		{
			avaRadioButton[row] = new JRadioButton(infoFacade.toString(), false);
			avaGroup.add(avaRadioButton[row]);
			avaRadioButton[row].addActionListener(this);
			++row;
		}

		int numRows = row;
		if (numRows > 0)
		{
			avaRadioButton[0].setSelected(true);
			selectedButton = avaRadioButton[0];
		}

		// Layout the buttons
		GridBagLayout gridbag = new GridBagLayout();
		buttonPanel = new JPanel();
		TitledBorder title = BorderFactory.createTitledBorder(null, "");
		buttonPanel.setBorder(title);
		buttonPanel.setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		if (numRows > 11)
		{
			buildTwoColLayout(numRows, c, gridbag);
		}
		else
		{
			for (int i = 0; i < numRows; ++i)
			{
				int cr = i;
				c.anchor = GridBagConstraints.WEST;
				Utility.buildConstraints(c, 0, cr, 2, 1, 1, 0);
				gridbag.setConstraints(avaRadioButton[i], c);
				buttonPanel.add(avaRadioButton[i]);
			}
		}
	}

	/**
	 * Build up tow columns of buttons arranged in column order e.g. 1-6 then 7-12 
	 * @param numButtons The number of buttons to be placed.
	 * @param c The GridBagConstraints for the panel.
	 * @param gridbag The layout of the panel.
	 */
	private void buildTwoColLayout(int numButtons, GridBagConstraints c, GridBagLayout gridbag)
	{
		int numRows = numButtons - numButtons / 2;
		for (int i = 0; i < numRows; ++i)
		{
			int cr = i;
			c.anchor = GridBagConstraints.WEST;
			Utility.buildConstraints(c, 0, cr, 2, 1, 1, 0);
			gridbag.setConstraints(avaRadioButton[i], c);
			buttonPanel.add(avaRadioButton[i]);

			if (i + numRows < numButtons)
			{
				c.anchor = GridBagConstraints.EAST;
				Utility.buildConstraints(c, 3, cr, 2, 1, 1, 0);
				gridbag.setConstraints(avaRadioButton[i + numRows], c);
				buttonPanel.add(avaRadioButton[i + numRows]);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof JRadioButton)
		{
			// A new radio button was selected - remember it
			selectedButton = (JRadioButton) e.getSource();
			return;
		}
		if (e.getActionCommand().equals("OK"))
		{
			for (int i = 0; i < avaRadioButton.length; i++)
			{
				if (selectedButton == avaRadioButton[i])
				{
					chooser.addSelected(chooser.getAvailableList().getElementAt(i));
					break;
				}
			}
			if (chooser.isRequireCompleteSelection() && chooser.getRemainingSelections().get() > 0)
			{
				JOptionPane.showMessageDialog(
					this, LanguageBundle.getFormattedString("in_chooserRequireComplete", //$NON-NLS-1$
					chooser.getRemainingSelections().get()), chooser.getName(), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			else
			{
				chooser.commit();
			}
		}
		else
		{
			chooser.rollback();
		}
		committed = e.getActionCommand().equals("OK");
		dispose();
	}

	/**
	 * Returns the means by which the dialog was closed.   
	 * @return the committed status, false for cancelled, true for OKed. 
	 */
	public boolean isCommitted()
	{
		return committed;
	}

}
