/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import pcgen.system.ConfigurationSettings;
import pcgen.system.ConfigurationSettings.SettingsFilesPath;

import org.apache.commons.lang3.SystemUtils;

public final class OptionsPathDialog extends JDialog
{

	private final JTextField dirField;
	private final JButton dirButton;
	private String selectedDir;

	private OptionsPathDialog(Frame frame)
	{
		super(frame, true);
		this.dirField = new JTextField();
		this.dirButton = new JButton();
		this.selectedDir = ConfigurationSettings.getDefaultSettingsFilesPath();
		initComponents();
	}

	public static String promptSettingsPath()
	{
		JFrame tempFrame = new JFrame("Select Settings Path");
		tempFrame.setLocationRelativeTo(null);
		OptionsPathDialog dialog = new OptionsPathDialog(tempFrame);

		tempFrame.setVisible(true);
		dialog.setVisible(true);
		tempFrame.setVisible(false);
		tempFrame.dispose();
		return dialog.selectedDir;
	}

	private void initComponents()
	{
		setResizable(false);
		setTitle("Directory for options.ini location");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		JLabel label = new JLabel("Select a directory to store PCGen options in:");
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(4, 4, 0, 4);
		getContentPane().add(label, gridBagConstraints);

		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		getContentPane().add(new JSeparator(), gridBagConstraints);

		label = new JLabel(
			"If you have an existing options.ini file," + "then select the directory containing that file");
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		getContentPane().add(label, gridBagConstraints);

		ActionListener handler = new ActionHandler();
		ButtonGroup group = new ButtonGroup();

		gridBagConstraints.insets = new Insets(0, 4, 0, 4);
		addRadioButton("<html><b>PCGen Dir</b>: This is the directory that PCGen is installed into",
			SettingsFilesPath.pcgen.name(), group, handler, gridBagConstraints);
		// Remark: do mac user really need to be able to put the file either in a specific mac dir or home?
		if (SystemUtils.IS_OS_MAC_OSX)
		{
			addRadioButton("<html><b>Mac User Dir</b>", SettingsFilesPath.mac_user.name(), group, handler,
				gridBagConstraints);
		}
		else if (SystemUtils.IS_OS_UNIX)
		{
			// putting it the same way as mac. merging all and using a system config dir instead would be better IMHO.
			addRadioButton("<html><b>Freedesktop configuration sub-directory</b> Use for most Linux/BSD",
				SettingsFilesPath.FD_USER.name(), group, handler, gridBagConstraints);
		}
		addRadioButton("<html><b>Home Dir</b>: This is your home directory", SettingsFilesPath.user.name(), group,
			handler, gridBagConstraints);
		addRadioButton("Select a directory to use", "select", group, handler, gridBagConstraints);

		dirField.setText(ConfigurationSettings.getSettingsDirFromFilePath(selectedDir));
		dirField.setEditable(false);

		gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.insets = new Insets(0, 4, 0, 0);
		getContentPane().add(dirField, gridBagConstraints);

		dirButton.setText("...");
		dirButton.setEnabled(false);
		dirButton.addActionListener(handler);
		dirButton.setActionCommand("custom");
		dirButton.setMargin(new Insets(2, 2, 2, 2));

		GridBagConstraints bagConstraints = new GridBagConstraints();
		bagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		bagConstraints.insets = new Insets(0, 0, 0, 4);
		getContentPane().add(dirButton, bagConstraints);

		JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(75, 23));
		okButton.setActionCommand("ok");
		okButton.addActionListener(handler);

		bagConstraints.insets = new Insets(4, 0, 4, 0);
		getContentPane().add(okButton, bagConstraints);
		getRootPane().setDefaultButton(okButton);

		pack();
		setLocationRelativeTo(null);
	}

	private void addRadioButton(String text, String command, ButtonGroup group, ActionListener listener,
		GridBagConstraints gbc)
	{
		boolean selected = command.equals(selectedDir);
		if (selected)
		{
			text += " (default)"; //for i18n this will need to be handled differently
		}
		AbstractButton rButton = new JRadioButton(text);
		rButton.setActionCommand(command);
		rButton.setSelected(selected);
		rButton.addActionListener(listener);
		group.add(rButton);
		getContentPane().add(rButton, gbc);
	}

	private class ActionHandler implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String command = e.getActionCommand();
			if (command.equals("custom"))
			{
				JFileChooser fc = new JFileChooser(dirField.getText());

				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				final int rVal = fc.showOpenDialog(null);

				if (rVal == JFileChooser.APPROVE_OPTION)
				{
					File dir = fc.getSelectedFile();
					if (dir.listFiles().length > 0)
					{
						int confirm = JOptionPane.showConfirmDialog(rootPane,
							"The folder " + dir.getAbsolutePath() + " is not empty.\n"
								+ "All ini files in this directory may be overwritten. " + "Are you sure?");
						if (confirm != JOptionPane.YES_OPTION)
						{
							return;
						}
					}
					selectedDir = String.valueOf(fc.getSelectedFile());
					dirField.setText(selectedDir);
				}
			}
			else if (command.equals("select"))
			{
				dirButton.setEnabled(true);
			}
			else if (command.equals("ok"))
			{
				dispose();
			}
			else
			{
				dirButton.setEnabled(false);
				selectedDir = command;
				dirField.setText(ConfigurationSettings.getSettingsDirFromFilePath(selectedDir));
			}
		}

	}

}
