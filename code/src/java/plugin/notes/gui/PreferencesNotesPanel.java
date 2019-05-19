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
import java.io.File;

import gmgen.util.LogUtilities;
import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

/**
 * Panel that tracks the misc preferences
 */
public final class PreferencesNotesPanel extends gmgen.gui.PreferencesPanel
{

	private static final String OPTION_NAME_NOTES_DATA = "Notes.DataDir"; //$NON-NLS-1$
	private static final String OPTION_NAME_LOG = "Logging.On"; //$NON-NLS-1$

	private Text dataDirField;
	private CheckBox logging;
	private final JFXPanel jfxPanel = new JFXPanel();

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
	 * Sets the current data directory setting
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
		VBox vbox = new VBox();

		dataDirField = new Text();
		logging = new CheckBox();
		ButtonBase browseButton = new Button(LanguageBundle.getString("...")); //$NON-NLS-1$

		browseButton.setOnAction(this::browseButtonActionPerformed);

		Node locationLabel = new Label(LanguageBundle.getString("in_plugin_notes_dataLocation")); //$NON-NLS-1$

		vbox.getChildren().add(locationLabel);
		vbox.getChildren().add(dataDirField);
		vbox.getChildren().add(browseButton);

		logging.setText(LanguageBundle.getString("in_plugin_notes_logGameData")); //$NON-NLS-1$
		vbox.getChildren().add(logging);

		Platform.runLater(() -> {
			Scene scene = new Scene(vbox);
			jfxPanel.setScene(scene);
		});

		setLayout(new BorderLayout());
		add(jfxPanel, BorderLayout.CENTER);
	}

	/**
	 * Handles browsing for a directory.
	 */
	private void browseButtonActionPerformed(ActionEvent ignored)
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(getDataDir()));
		File directory = directoryChooser.showDialog(jfxPanel.getScene().getWindow());

		if (directory != null)
		{
			setDataDir(directory.getAbsolutePath());
		}
	}
}
