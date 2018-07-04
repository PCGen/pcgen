/*
 * Copyright 2008 (C) James Dempsey
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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code DIWarningDialog} is responsible for
 * displaying warnings for the data installer. The list of 
 * files will be displayed in a scrollable area.
 * 
 * 
 */
@SuppressWarnings("serial")
public class DIWarningDialog extends JDialog implements ActionListener
{

	private final String fileText;
	private final String introText;

	/** The result selected by the user. */
	private int result = JOptionPane.CANCEL_OPTION;

	private static final String ACTION_YES = "yes";
	private static final String ACTION_NO = "no";
	private static final String ACTION_CANCEL = "cancel";

	/**
	 * Instantiates a new warning dialog for the data installer.
	 * 
	 * @param parent the parent frame
	 * @param fileList the file list as a text string, one file per line
	 * @param introText the intro text to explain the dialogs purpose to the user.
	 */
	public DIWarningDialog(Frame parent, String fileList, String introText)
	{
		super(parent, LanguageBundle.getString("in_dataInstaller"), true);

		fileText = fileList;
		this.introText = introText;

		initComponents();
		Utility.setComponentRelativeLocation(parent, this);
	}

	/**
	 * Gets the response.
	 * 
	 * @return the response
	 */
	public int getResponse()
	{
		return result;
	}

	/**
	 * Initialises the user interface.
	 */
	private void initComponents()
	{
		setLayout(new GridBagLayout());

		JLabel introLabel = new JLabel(introText);
		GridBagConstraints gbc = new GridBagConstraints();
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 5, 10);
		add(introLabel, gbc);

		JTextArea messageArea = new JTextArea();
		messageArea.setName("errorMessageBox");
		messageArea.setEditable(false);
		messageArea.setTabSize(8);
		messageArea.setText(fileText);
		JScrollPane messageAreaContainer = new JScrollPane(messageArea);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 1.0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 10, 5, 10);
		add(messageAreaContainer, gbc);

		JLabel dummy = new JLabel(" ");
		Utility.buildRelativeConstraints(gbc, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		add(dummy, gbc);

		JButton yesButton = new JButton(LanguageBundle.getString("in_yes"));
		yesButton.setActionCommand(ACTION_YES);
		yesButton.addActionListener(this);
		Utility.buildRelativeConstraints(gbc, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.EAST);
		gbc.insets = new Insets(5, 5, 10, 5);
		add(yesButton, gbc);

		JButton noButton = new JButton(LanguageBundle.getString("in_no"));
		noButton.setActionCommand(ACTION_NO);
		noButton.addActionListener(this);
		Utility.buildRelativeConstraints(gbc, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.EAST);
		add(noButton, gbc);

		JButton cancelButton = new JButton(LanguageBundle.getString("in_cancel"));
		cancelButton.setActionCommand(ACTION_CANCEL);
		cancelButton.addActionListener(this);
		getRootPane().setDefaultButton(cancelButton);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.EAST);
		gbc.insets = new Insets(5, 5, 10, 10);
		add(cancelButton, gbc);

		pack();

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				result = JOptionPane.CANCEL_OPTION;
				setVisible(false);
			}
		});

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (ACTION_YES.equals(e.getActionCommand()))
		{
			result = JOptionPane.YES_OPTION;
		}
		else if (ACTION_NO.equals(e.getActionCommand()))
		{
			result = JOptionPane.NO_OPTION;
		}
		else
		{
			result = JOptionPane.CANCEL_OPTION;
		}
		setVisible(false);
	}
}
