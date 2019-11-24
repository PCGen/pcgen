/*
 * Copyright 2007 (C) James Dempsey
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import pcgen.cdom.enumeration.Destination;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.InstallableCampaign;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.tools.CommonMenuText;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.SimpleHtmlPanelController;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.InstallLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

/**
 * {@code DataInstaller} is responsible for managing the installation of
 * a data set including the selection of the set and the install options.
 */
public final class DataInstaller extends JFrame
{
	/**
	 * The listener for receiving and processing action events from installer 
	 * buttons. 
	 */
	private final class InstallerButtonListener implements ActionListener
	{

		/**
		 * Gets the currently selected destination.
		 *
		 * @return the selected destination
		 */
		private Destination getSelectedDestination()
		{
			if (locDataButton.isSelected())
			{
				return Destination.DATA;
			}
			if (locVendorDataButton.isSelected())
			{
				return Destination.VENDORDATA;
			}
			if (locHomebrewDataButton.isSelected())
			{
				return Destination.HOMEBREWDATA;
			}
			return null;
		}

		/**
		 * Install a data set (campaign) into the current PCGen install.
		 *
		 * @param dataSet the data set (campaign) to be installed.
		 * @param dest The location the data is to be installed to.
		 *
		 * @return true, if install data source
		 */
		private boolean installDataSource(File dataSet, Destination dest)
		{
			// Get the directory the data is to be stored in
			if (dataSet == null)
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_diDataSetNotSelected"),
					TITLE, MessageType.ERROR);
				return false;
			}
			if (dest == null)
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_diDataFolderNotSelected"),
					TITLE, MessageType.ERROR);
				return false;
			}
			File destDir;
			switch (dest)
			{
				case VENDORDATA:
					destDir = new File(PCGenSettings.getVendorDataDir());
					break;

				case HOMEBREWDATA:
					destDir = new File(PCGenSettings.getHomebrewDataDir());
					break;

				case DATA:
				default:
					destDir = new File(ConfigurationSettings.getPccFilesDir());
					break;
			}

			// Check chosen dir exists
			if (!destDir.exists())
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getFormattedString("in_diDataFolderNotExist", destDir.getAbsoluteFile()), TITLE,
					MessageType.ERROR);
				return false;
			}

			// Scan for non standard files and files that would be overwritten
			List<String> directories = new ArrayList<>();
			List<String> files = new ArrayList<>();
			if (!populateFileAndDirLists(dataSet, directories, files))
			{
				return false;
			}
			if (!checkNonStandardOK(files))
			{
				return false;
			}
			if (!checkOverwriteOK(files, destDir))
			{
				return false;
			}

			if (!createDirectories(directories, destDir))
			{
				return false;
			}

			// Navigate through the zip file, processing each file
			return createFiles(dataSet, destDir, files);
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			JButton source = (JButton) actionEvent.getSource();

			if (source == null)
			{
				// Do nothing
			}
			else if (source.equals(closeButton))
			{
				setVisible(false);
				dispose();
			}
			else if (source.equals(selectButton))
			{
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(currFolder);
				fileChooser.setTitle(LanguageBundle.getString("in_diChooserTitle"));
				FileChooser.ExtensionFilter dataSetFilter = new FileChooser.ExtensionFilter(
						"Data Sets", "*.pcz", "*.zip"
				);
				fileChooser.getExtensionFilters().add(dataSetFilter);
				fileChooser.setSelectedExtensionFilter(dataSetFilter);
				File dataset = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));
				if (dataset == null)
				{
					return;
				}
				currFolder = dataset.getParentFile();
				readDataSet(dataset);
			}
			else if (source.equals(installButton))
			{
				if (installDataSource(currDataSet, getSelectedDestination()))
				{
					//PCGen_Frame1.getInst().getMainSource().refreshCampaigns();
					//TODO: Refresh the data cleanly.
					//					PersistenceManager.getInstance().refreshCampaigns();
					//					FacadeFactory.refresh();
					ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getFormattedString("in_diInstalled", campaign //$NON-NLS-1$
						.getDisplayName()), TITLE, MessageType.INFORMATION);
				}
			}
		}

		/**
		 * Read data set.
		 *
		 * @param dataSet the data set
		 *
		 */
		private void readDataSet(File dataSet)
		{
			// Open the ZIP file
			try (ZipFile in = new ZipFile(dataSet))
			{
				// Get the install file in a case insensitive manner
				ZipEntry installEntry = null;
				@SuppressWarnings("rawtypes")
				Enumeration entries = in.entries();
				while (entries.hasMoreElements())
				{
					ZipEntry entry = (ZipEntry) entries.nextElement();
					if (entry.getName().equalsIgnoreCase("install.lst"))
					{
						installEntry = entry;
						break;
					}
				}
				if (installEntry == null)
				{
					// Report that it isn't a valid data set
					Logging.errorPrint("File " + dataSet + " is not a valid datsset - no Install.lst file");
					ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getFormattedString("in_diNoInstallFile", dataSet.getName()), TITLE,
						MessageType.WARNING);
					return;
				}

				// Parse the install file
				String installInfo;
				try (InputStream inStream = in.getInputStream(installEntry);
				     BufferedReader reader =
						     new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8)))
				{
					installInfo = reader.lines()
					                    .collect(Collectors.joining("\n"));
				}

				final InstallLoader loader = new InstallLoader();
				loader.loadLstString(null, dataSet.toURI(), installInfo);
				campaign = loader.getCampaign();
			}
			catch (IOException e)
			{
				// Report the error
				Logging.errorPrint("Failed to read data set " + dataSet + " due to ", e);
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_diBadDataSet", dataSet),
					TITLE, MessageType.ERROR);
				return;
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Failed to parse data set " + dataSet + " due to ", e);
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_diBadDataSet", dataSet),
					TITLE, MessageType.ERROR);
				return;
			}

			// Validate that the campaign is compatible with our version
			if (campaign.getSafe(StringKey.MINDEVVER) != null
				&& !CoreUtility.isPriorToCurrent(campaign.getSafe(StringKey.MINDEVVER)))
			{
				if (CoreUtility.isCurrMinorVer(campaign.getSafe(StringKey.MINDEVVER)))
				{
					Logging.errorPrint("Dataset " + campaign.getDisplayName() + " needs at least PCGen version "
						+ campaign.getSafe(StringKey.MINDEVVER) + " to run. It could not be installed.");
					ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getFormattedString("in_diVersionTooOldDev",
							campaign.getSafe(StringKey.MINDEVVER), campaign.getSafe(StringKey.MINVER)),
						TITLE, MessageType.WARNING);
					return;
				}
			}
			if (campaign.getSafe(StringKey.MINVER) != null
				&& !CoreUtility.isPriorToCurrent(campaign.getSafe(StringKey.MINVER)))
			{
				Logging.errorPrint("Dataset " + campaign.getDisplayName() + " needs at least PCGen version "
					+ campaign.getSafe(StringKey.MINVER) + " to run. It could not be installed.");
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getFormattedString("in_diVersionTooOld", campaign.getSafe(StringKey.MINVER)), TITLE,
					MessageType.WARNING);
				return;
			}

			// Display the info
			dataSetSel.setText(dataSet.getAbsolutePath());
			dataSetDetails.getController().setHtml(FacadeFactory.getCampaignInfoFactory().getHTMLInfo(campaign));
			if (campaign.get(ObjectKey.DESTINATION) == null)
			{
				locDataButton.setSelected(false);
				locVendorDataButton.setSelected(false);
				locHomebrewDataButton.setSelected(false);
			}
			else
			{
				switch (campaign.get(ObjectKey.DESTINATION))
				{
					case DATA:
						locDataButton.setSelected(true);
						break;
					case VENDORDATA:
						locVendorDataButton.setSelected(true);
						break;
					case HOMEBREWDATA:
						locHomebrewDataButton.setSelected(true);
						break;
					default:
						//Case not caught, should this cause an error?
						break;
				}
			}
			currDataSet = dataSet;

			toFront();
		}
	}

	/** The name of the OUTPUTSHEETS folder. */
	private static final String OUTPUTSHEETS_FOLDER = "outputsheets/";

	/** The name of the DATA folder. */
	private static final String DATA_FOLDER = "data/";

	/** The standard window title. */
	private static final String TITLE = LanguageBundle.getString("in_dataInstaller");

	/** The component to display the path of the selected data set. */
	private JTextField dataSetSel;

	/** The select button. */
	private JButton selectButton;

	/** The data set detail display component. */
	private JFXPanelFromResource<SimpleHtmlPanelController> dataSetDetails;

	/** The button for the data location. */
	private JRadioButton locDataButton;

	/** The button for the vendor data location. */
	private JRadioButton locVendorDataButton;

	/** The button for the homebrew data location. */
	private JRadioButton locHomebrewDataButton;

	/** The install button. */
	private JButton installButton;

	/** The close button. */
	private JButton closeButton;

	/** The listener. */
	private final ActionListener listener = new InstallerButtonListener();

	/** The campaign. */
	private InstallableCampaign campaign;

	/** The current data set. */
	private File currDataSet;

	/** The current folder */
	private File currFolder;

	/**
	 * Instantiates a new data installer.
	 */
	public DataInstaller()
	{
		currFolder = new File(System.getProperty("user.dir"));
		initComponents();

		setIconImage(Icons.PCGenApp.getImageIcon().getImage());
		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * Check for any non standard files being installed and check with the
	 * user if there are. Note if the user says no to installing the non
	 * standard files they will be removed from the file list
	 * 
	 * @param files the names of the files being installed.
	 * 
	 * @return Should the install process continue
	 */
	private boolean checkNonStandardOK(Collection<String> files)
	{
		Collection<String> nonStandardFiles = new ArrayList<>();
		for (String filename : files)
		{
			if (!filename.toLowerCase().startsWith(DATA_FOLDER)
				&& !filename.toLowerCase().startsWith(OUTPUTSHEETS_FOLDER))
			{
				nonStandardFiles.add(filename);
			}
		}

		if (!nonStandardFiles.isEmpty())
		{
			StringBuilder msg = new StringBuilder();
			for (String filename : nonStandardFiles)
			{
				msg.append(' ').append(filename).append('\n');
			}

			Alert diWarningDialog = GuiUtility.runOnJavaFXThreadNow(() -> new Alert(Alert.AlertType.CONFIRMATION));
			ButtonType noButton = new ButtonType(LanguageBundle.getString("in_no"), ButtonBar.ButtonData.NO);
			// default for confirm is yes/cancel
			diWarningDialog.getButtonTypes().add(noButton);
			diWarningDialog.setTitle(LanguageBundle.getString("in_dataInstaller"));
			diWarningDialog.setHeaderText(LanguageBundle.getString("in_diNonStandardFiles"));
			diWarningDialog.setContentText(msg.toString());
			Optional<ButtonType> warningResult = GuiUtility.runOnJavaFXThreadNow(diWarningDialog::showAndWait);
			if (warningResult.isPresent())
			{
				ButtonType buttonType = warningResult.get();
				if (buttonType.equals(ButtonType.CANCEL))
				{
					return false;
				}
				if (buttonType.equals(ButtonType.NO))
				{
					files.removeAll(nonStandardFiles);
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Check for any files that would be overwritten and confirm it is ok
	 * with the user.
	 * 
	 * @param files the names of the files being installed.
	 * @param destDir the destination data directory
	 * 
	 * @return true, if successful
	 */
	private static boolean checkOverwriteOK(Collection<String> files, File destDir)
	{
		Collection<String> existingFiles = new ArrayList<>();
		Collection<String> existingFilesCorr = new ArrayList<>();
		for (String filename : files)
		{
			String correctedFilename = correctFileName(destDir, filename);
			if (new File(correctedFilename).exists())
			{
				existingFiles.add(filename);
				existingFilesCorr.add(correctedFilename);
			}
		}

		if (!existingFiles.isEmpty())
		{
			StringBuilder msg = new StringBuilder();
			for (String filename : existingFilesCorr)
			{
				msg.append(' ').append(filename).append("\n");
			}

			Alert diWarningDialog = new Alert(Alert.AlertType.CONFIRMATION);
			ButtonType noButton = new ButtonType(LanguageBundle.getString("in_no"), ButtonBar.ButtonData.NO);
			// default for confirm is yes/cancel
			diWarningDialog.getButtonTypes().add(noButton);
			diWarningDialog.setTitle(LanguageBundle.getString("in_dataInstaller"));
			diWarningDialog.setHeaderText(LanguageBundle.getString("in_diOverwriteFiles"));
			diWarningDialog.setContentText(msg.toString());
			Optional<ButtonType> warningResult = diWarningDialog.showAndWait();
			if (warningResult.isPresent())
			{
				ButtonType buttonType = warningResult.get();
				if (buttonType.equals(ButtonType.CANCEL))
				{
					return false;
				}
				if (buttonType.equals(ButtonType.NO))
				{
					files.removeAll(existingFiles);
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Copy the contents of the input stream to the output stream. Used
	 * here to write the zipped file to the install location.
	 * 
	 * @param in the input stream
	 * @param out the output stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void copyInputStream(InputStream in, OutputStream out) throws IOException
	{
		in.transferTo(out);

		in.close();
		out.close();
	}

	/**
	 * Correct the file name to account for the selected data directory and
	 * preference based folder locations such as output sheets.
	 * 
	 * @param destDir the destination data directory
	 * @param fileName the file name to be corrected.
	 * 
	 * @return the corrected file name.
	 */
	private static String correctFileName(File destDir, String fileName)
	{
		if (fileName.toLowerCase().startsWith(DATA_FOLDER))
		{
			fileName = destDir.getAbsolutePath() + fileName.substring(4);
		}
		else if (fileName.toLowerCase().startsWith(OUTPUTSHEETS_FOLDER))
		{
			fileName = new File(ConfigurationSettings.getOutputSheetsDir()).getAbsolutePath() + fileName.substring(12);
		}
		return fileName;
	}

	/**
	 * Creates the directories needed by the installer, where they
	 * do not already exist.
	 * 
	 * @param directories the directories
	 * @param destDir the destination data directory
	 * 
	 * @return true, if successful
	 */
	private static boolean createDirectories(Iterable<String> directories, File destDir)
	{
		for (String dirname : directories)
		{
			String corrDirname = correctFileName(destDir, dirname);
			File dir = new File(corrDirname);
			if (!dir.exists())
			{
				Logging.log(Logging.INFO, "Creating directory: " + dir);
				if (!dir.mkdirs())
				{
					ShowMessageDelegate.showMessageDialog(
						LanguageBundle.getFormattedString("in_diDirNotCreated", dir.getAbsoluteFile()), TITLE,
						MessageType.ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Creates the files in the archive, as filtered by previous user actions.
	 * 
	 * @param dataSet the data set (campaign) to be installed.
	 * @param destDir the destination data directory
	 * @param files the list of file names
	 * 
	 * @return true, if all files created ok
	 */
	private static boolean createFiles(File dataSet, File destDir, Iterable<String> files)
	{
		String corrFilename = "";
		try (ZipFile in = new ZipFile(dataSet))
		{
			for (String filename : files)
			{
				ZipEntry entry = in.getEntry(filename);
				corrFilename = correctFileName(destDir, filename);
				if (Logging.isDebugMode())
				{
					Logging.debugPrint("Extracting file: " + filename + " to " + corrFilename);
				}
				copyInputStream(in.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(corrFilename)));
			}
			return true;
		}
		catch (IOException e)
		{
			// Report the error
			Logging.errorPrint("Failed to read data set " + dataSet + " or write file " + corrFilename + " due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_diWriteFail", corrFilename),
				TITLE, MessageType.ERROR);
			return false;
		}
	}

	/**
	 * Build the user interface ready for display.
	 */
	private void initComponents()
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		GridBagLayout gridbag = new GridBagLayout();
		setTitle(TITLE);
		setLayout(gridbag);

		// Data set selection row
		Utility.buildConstraints(gbc, 0, 0, 1, 1, 0.0, 0.0);
		JLabel dataSetLabel = new JLabel(LanguageBundle.getString("in_diDataSet"), SwingConstants.RIGHT);
		gridbag.setConstraints(dataSetLabel, gbc);
		add(dataSetLabel, gbc);

		Utility.buildConstraints(gbc, 1, 0, 2, 1, 1.0, 0.0);
		dataSetSel = new JTextField("", SwingConstants.WEST);
		dataSetSel.setEditable(false);
		gridbag.setConstraints(dataSetSel, gbc);
		add(dataSetSel, gbc);

		Utility.buildConstraints(gbc, 3, 0, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.NONE;
		selectButton = new JButton();
		CommonMenuText.name(selectButton, "select"); //$NON-NLS-1$
		gridbag.setConstraints(selectButton, gbc);
		add(selectButton, gbc);
		selectButton.addActionListener(listener);

		// Data set details row
		Utility.buildConstraints(gbc, 0, 1, 4, 1, 1.0, 1.0);
		dataSetDetails = new JFXPanelFromResource<>(
				SimpleHtmlPanelController.class,
				"SimpleHtmlPanel.fxml"
		);
		dataSetDetails.setPreferredSize(new Dimension(400, 200));
		dataSetDetails.setBackground(getBackground());
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(dataSetDetails);
		gridbag.setConstraints(jScrollPane, gbc);
		add(jScrollPane, gbc);

		// Location row
		Utility.buildConstraints(gbc, 0, 2, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel locLabel = new JLabel(LanguageBundle.getString("in_diLocation"), SwingConstants.RIGHT);
		gridbag.setConstraints(locLabel, gbc);
		add(locLabel, gbc);

		ButtonGroup exclusiveGroup = new ButtonGroup();
		locDataButton = new JRadioButton(LanguageBundle.getString("in_diData"));
		locDataButton.setToolTipText(LanguageBundle.getString("in_diData_tip"));
		exclusiveGroup.add(locDataButton);
		locVendorDataButton = new JRadioButton(LanguageBundle.getString("in_diVendorData"));
		locVendorDataButton.setToolTipText(LanguageBundle.getString("in_diVendorData_tip"));
		exclusiveGroup.add(locVendorDataButton);
		locHomebrewDataButton = new JRadioButton(LanguageBundle.getString("in_diHomebrewData"));
		locHomebrewDataButton.setToolTipText(LanguageBundle.getString("in_diHomebrewData_tip"));
		exclusiveGroup.add(locHomebrewDataButton);
		JPanel optionsPanel = new JPanel();
		optionsPanel.add(locDataButton);
		optionsPanel.add(locVendorDataButton);
		optionsPanel.add(locHomebrewDataButton);
		Utility.buildConstraints(gbc, 1, 2, 3, 1, 0.0, 0.0);
		gridbag.setConstraints(optionsPanel, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		add(optionsPanel, gbc);

		// Buttons row
		installButton = new JButton();
		CommonMenuText.name(installButton, "diInstall"); //$NON-NLS-1$
		installButton.addActionListener(listener);
		closeButton = new JButton();
		CommonMenuText.name(closeButton, "close"); //$NON-NLS-1$
		closeButton.addActionListener(listener);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(installButton);
		buttonsPanel.add(closeButton);
		Utility.buildConstraints(gbc, 2, 3, 2, 1, 0.0, 0.0);
		gridbag.setConstraints(buttonsPanel, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		add(buttonsPanel, gbc);

		pack();
	}

	/**
	* Populate the lists of files and directories to be installed.
	* 
	* @param dataSet the data set (campaign) to be installed.
	* @param directories the list of directory names
	* @param files the list of file names
	* 
	* @return true, if populate file and dir lists
	*/
	@SuppressWarnings("rawtypes")
	private static boolean populateFileAndDirLists(File dataSet,
	                                               Collection<String> directories,
	                                               Collection<String> files)
	{
		// Navigate through the zip file, processing each file
		// Open the ZIP file
		try (ZipFile in = new ZipFile(dataSet))
		{

			Enumeration entries = in.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory())
				{
					directories.add(entry.getName());
				}
				else if (!entry.getName().equalsIgnoreCase("install.lst"))
				{
					files.add(entry.getName());
				}
			}
		}
		catch (IOException e)
		{
			// Report the error
			Logging.errorPrint("Failed to read data set " + dataSet + " due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_diBadDataSet", dataSet), TITLE,
				MessageType.ERROR);
			return false;
		}
		return true;
	}
}
