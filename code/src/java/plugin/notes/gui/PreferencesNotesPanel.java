/*
 *  Copyright (C) 2002 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.notes.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import gmgen.util.LogUtilities;
import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;

/**
 * Panel that tracks the misc preferences
 */
public class PreferencesNotesPanel extends gmgen.gui.PreferencesPanel
{

	private static final String OPTION_NAME_NOTES_DATA = "Notes.DataDir"; //$NON-NLS-1$
	private static final String OPTION_NAME_LOG = "Logging.On"; //$NON-NLS-1$

	private JPanel dirPanel;
	private JPanel loggingPanel;
	private JTextField dataDirField;
	private JCheckBox logging;
	private JButton browseButton;

	/** Creates new form PreferencesNotesPanel */
	public PreferencesNotesPanel()
	{
		initComponents();
		initPreferences();
	}

	@Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(OPTION_NAME_NOTES_DATA, getDataDir());
		SettingsHandler.setGMGenOption(OPTION_NAME_LOG, isLogging());
		LogUtilities.inst().setLogging(isLogging());
	}

	@Override
	public void initPreferences()
	{
		// XXX change to another default?
		setDataDir(SettingsHandler.getGMGenOption(OPTION_NAME_NOTES_DATA,
			SettingsHandler.getGmgenPluginDir().toString() + File.separator + "Notes")); //$NON-NLS-1$
		setLogging(SettingsHandler.getGMGenOption(OPTION_NAME_LOG, false));
	}

	/**
	 * <p>
	 * Sets the current data directory setting
	 * </p>
	 *
	 * @param dir
	 */
	private void setDataDir(String dir)
	{
		dataDirField.setText(dir);
	}

	/**
	 * <p>
	 * Gets the current data directory setting
	 * </p>
	 * @return data directory
	 */
	private String getDataDir()
	{
		return dataDirField.getText();
	}

	private boolean isLogging()
	{
		return logging.isSelected();
	}

	private void setLogging(boolean isLogging)
	{
		logging.setSelected(isLogging);
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_notes_general"); //$NON-NLS-1$
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		dirPanel = new JPanel();
		loggingPanel = new JPanel();
		dataDirField = new JTextField();
		logging = new JCheckBox();
		browseButton = new JButton(LanguageBundle.getString("...")); //$NON-NLS-1$

		browseButton.addActionListener(this::browseButtonActionPerformed);

		JPanel borderPanel = new JPanel();
		borderPanel.setLayout(new GridBagLayout());

		dirPanel = new JPanel(new GridBagLayout());
		dirPanel.setBorder(new TitledBorder(LanguageBundle.getString("in_plugin_notes_sourceDir"))); //$NON-NLS-1$

		JLabel locationLabel = new JLabel(LanguageBundle.getString("in_plugin_notes_dataLocation")); //$NON-NLS-1$

		GridBagConstraints c = new GridBagConstraints();
		dirPanel.add(locationLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		dirPanel.add(dataDirField, c);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		dirPanel.add(browseButton, c);

		loggingPanel = new JPanel();
		loggingPanel.setLayout(new BorderLayout());
		loggingPanel.setBorder(new TitledBorder(LanguageBundle.getString("in_plugin_notes_client"))); //$NON-NLS-1$

		logging.setText(LanguageBundle.getString("in_plugin_notes_logGameData")); //$NON-NLS-1$

		loggingPanel.add(logging, BorderLayout.CENTER);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = GridBagConstraints.REMAINDER;
		borderPanel.add(dirPanel, c);
		borderPanel.add(loggingPanel, c);
		c.weighty = 1.0;
		c.fill = GridBagConstraints.VERTICAL;
		borderPanel.add(new JPanel(), c);
		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(borderPanel);
		add(jScrollPane1, BorderLayout.CENTER);
	}

	/**
	 * <p>
	 * Handles browsing for a directory.
	 * </p>
	 *
	 * @param e
	 */
	protected void browseButtonActionPerformed(ActionEvent e)
	{
		JFileChooser dlg = new JFileChooser(getDataDir());
		dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (dlg.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			setDataDir(dlg.getSelectedFile().getAbsolutePath());
		}
	}
}
