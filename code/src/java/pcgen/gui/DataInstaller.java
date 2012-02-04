/*
 * DataInstaller.java
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
 *
 * Created on 22/12/2007
 *
 * $Id$
 */

package pcgen.gui;

import java.awt.Component;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import pcgen.cdom.enumeration.Destination;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.InstallableCampaign;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.sources.MainSource;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.InstallLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * <code>DataInstaller</code> is responsible for managing the installation of
 * a data set including the selection of the set and the install options.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class DataInstaller extends JFrame
{

	/** Version for serialisation */
	private static final long serialVersionUID = -7429544164441235718L;

	/**
	 * Filter class to only display potential zip format data sets.
	 */
	private final class DataPackFilter extends FileFilter
	{
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File f)
		{
			if (f.isDirectory())
			{
				return true;
			}
			String nameLc = f.getName().toLowerCase();
			if (nameLc.endsWith(".zip") || nameLc.endsWith(".pcz"))
			{
				return true;
			}

			return false;
		}

		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription()
		{
			return "Data Sets (*.pcz,*.zip)";
		}
	}
	
	/**
	 * The listener for receiving and processing action events from installer 
	 * buttons. 
	 */
	private final class InstallerButtonListener implements ActionListener
	{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			JButton source = (JButton) actionEvent.getSource();

			if (source == null)
			{
				// Do nothing
			}
			else if (source == cancel)
			{
				setVisible(false);
				DataInstaller.this.dispose();
			}
			else if (source == selectButton)
			{
				JFileChooser chooser =
						new JFileChooser(currFolder);
				chooser.setDialogTitle(LanguageBundle
					.getString("in_diChooserTitle"));
				chooser.setFileFilter(new DataPackFilter());
				int result = chooser.showOpenDialog(DataInstaller.this);
				if (result != JFileChooser.APPROVE_OPTION)
				{
					return;
				}
				File dataset = chooser.getSelectedFile();
				currFolder = dataset.getParentFile();
				readDataSet(dataset);
			}
			else if (source == installButton)
			{
				if (installDataSource(currDataSet, getSelectedDestination()))
				{
					//PCGen_Frame1.getInst().getMainSource().refreshCampaigns();
					PersistenceManager.getInstance().refreshCampaigns();
					FacadeFactory.refresh();
					ShowMessageDelegate.showMessageDialog(LanguageBundle
						.getFormattedString("in_diInstalled", campaign
							.getDisplayName()), TITLE, MessageType.INFORMATION);
				}
			}
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
	private JLabelPane dataSetDetails;
	
	/** The button for the data location. */
	private JRadioButton locDataButton;
	
	/** The button for the vendor data location. */
	private JRadioButton locVendorDataButton;
	
	/** The install button. */
	private JButton installButton;
	
	/** The cancel. */
	private JButton cancel;
	
	/** The listener. */
	private InstallerButtonListener listener = new InstallerButtonListener();
	
	/** The campaign. */
	private InstallableCampaign campaign;
	
	/** The current data set. */
	private File currDataSet;
	
	/** The current folder */
	private File currFolder;

	/**
	 * Instantiates a new data installer.
	 * 
	 * @param owner the parent window of the dialog.
	 */
	public DataInstaller(Component owner)
	{
		currFolder = new File(System.getProperty("user.dir"));
		initComponents();

		IconUtilitities.maybeSetIcon(this, IconUtilitities.RESOURCE_APP_ICON);
		pcgen.gui.utils.Utility.centerFrame(this, false);
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
	private boolean checkNonStandardOK(List<String> files)
	{
		List<String> nonStandardFiles = new ArrayList<String>();
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
			StringBuffer msg = new StringBuffer();
			for (String filename : nonStandardFiles)
			{
				msg.append(' ').append(filename).append("\n");
			}
			DIWarningDialog dialog =
					new DIWarningDialog(this, msg.toString(), LanguageBundle
						.getFormattedString("in_diNonStandardFiles"));
			dialog.setVisible(true);
			int result = dialog.getResponse();
			if (result == JOptionPane.CANCEL_OPTION)
			{
				return false;
			}
			if (result == JOptionPane.NO_OPTION)
			{
				for (String filename : nonStandardFiles)
				{
					files.remove(filename);
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
	private boolean checkOverwriteOK(List<String> files, File destDir)
	{
		List<String> existingFiles = new ArrayList<String>();
		List<String> existingFilesCorr = new ArrayList<String>();
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
			StringBuffer msg = new StringBuffer();
			for (String filename : existingFilesCorr)
			{
				msg.append(' ').append(filename).append("\n");
			}
			DIWarningDialog dialog =
					new DIWarningDialog(this, msg.toString(), LanguageBundle
						.getFormattedString("in_diOverwriteFiles"));
			dialog.setVisible(true);
			int result = dialog.getResponse();
			if (result == JOptionPane.CANCEL_OPTION)
			{
				return false;
			}
			if (result == JOptionPane.NO_OPTION)
			{
				for (String filename : existingFiles)
				{
					files.remove(filename);
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
	private final void copyInputStream(InputStream in, OutputStream out)
		throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
		{
			out.write(buffer, 0, len);
		}

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
	private String correctFileName(File destDir, String fileName)
	{
		if (fileName.toLowerCase().startsWith(DATA_FOLDER))
		{
			fileName = destDir.getAbsolutePath() + fileName.substring(4);
		}
		else if (fileName.toLowerCase().startsWith(OUTPUTSHEETS_FOLDER))
		{
			fileName =
					new File(ConfigurationSettings.getOutputSheetsDir())
						.getAbsolutePath() + fileName.substring(12);
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
	private boolean createDirectories(List<String> directories, File destDir)
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
					ShowMessageDelegate.showMessageDialog(LanguageBundle
						.getFormattedString("in_diDirNotCreated", dir
							.getAbsoluteFile()), TITLE, MessageType.ERROR);
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
	private boolean createFiles(File dataSet, File destDir, List<String> files)
	{
		String corrFilename = "";
		ZipFile in;
		// Open the ZIP file
		try
		{
			in = new ZipFile(dataSet);
		}
		catch (IOException e)
		{
			Logging.errorPrint("Failed to read data set " + dataSet
				+ " due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diBadDataSet", dataSet), TITLE,
				MessageType.ERROR);
			return false;
		}

		try
		{
			for (String filename : files)
			{
				ZipEntry entry = in.getEntry(filename);
				corrFilename = correctFileName(destDir, filename);
				Logging.debugPrint("Extracting file: " + filename + " to "
					+ corrFilename);
				copyInputStream(
					in.getInputStream(entry),
					new BufferedOutputStream(new FileOutputStream(corrFilename)));
			}
			in.close();
		}
		catch (IOException e)
		{
			// Report the error
			Logging.errorPrint("Failed to read data set " + dataSet
				+ " or write file " + corrFilename + " due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diWriteFail", corrFilename), TITLE,
				MessageType.ERROR);
			return false;
		}
		return true;
	}

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
		return null;
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
		selectButton = new JButton(LanguageBundle.getString("in_select"));
		gridbag.setConstraints(selectButton, gbc);
		add(selectButton, gbc);
		selectButton.addActionListener(listener);
		
		// Data set details row
		Utility.buildConstraints(gbc, 0, 1, 4, 1, 1.0, 1.0);
		dataSetDetails = new JLabelPane();
		dataSetDetails.setPreferredSize(new Dimension(400, 200));
		dataSetDetails.setEditable(false);
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
		locDataButton =
				new JRadioButton(LanguageBundle
					.getString("in_diData"));
		Utility.setDescription(locDataButton, LanguageBundle
					.getString("in_diData_tip"));
		exclusiveGroup.add(locDataButton);
		locVendorDataButton =
				new JRadioButton(LanguageBundle
					.getString("in_diVendorData"));
		Utility.setDescription(locVendorDataButton, LanguageBundle
			.getString("in_diVendorData_tip"));
		exclusiveGroup.add(locVendorDataButton);
		JPanel optionsPanel = new JPanel();
		optionsPanel.add(locDataButton);
		optionsPanel.add(locVendorDataButton);
		Utility.buildConstraints(gbc, 1, 2, 3, 1, 0.0, 0.0);
		gridbag.setConstraints(optionsPanel, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		add(optionsPanel, gbc);

		// Buttons row
		installButton = new JButton(LanguageBundle.getString("in_diInstall"));
		installButton.addActionListener(listener);
		cancel = new JButton(LanguageBundle.getString("in_close"));
		cancel.addActionListener(listener);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(installButton);
		buttonsPanel.add(cancel);
		Utility.buildConstraints(gbc, 2, 3, 2, 1, 0.0, 0.0);
		gridbag.setConstraints(buttonsPanel, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		add(buttonsPanel, gbc);

		pack();
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
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diDataSetNotSelected"), TITLE,
				MessageType.ERROR);
			return false;
		}
		if (dest == null)
		{
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diDataFolderNotSelected"), TITLE,
				MessageType.ERROR);
			return false;
		}
		File destDir;
		switch (dest)
		{
			case VENDORDATA:
				destDir = new File(ConfigurationSettings.getVendorDataDir());
				break;

			case DATA:
			default:
				destDir = new File(ConfigurationSettings.getPccFilesDir());
				break;
		}

		// Check chosen dir exists
		if (!destDir.exists())
		{
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diDataFolderNotExist", destDir
					.getAbsoluteFile()), TITLE, MessageType.ERROR);
			return false;
		}
		
		// Scan for non standard files and files that would be overwritten
		List<String> directories = new ArrayList<String>();
		List<String> files = new ArrayList<String>();
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
	private boolean populateFileAndDirLists(File dataSet,
		List<String> directories, List<String> files)
	{
		// Navigate through the zip file, processing each file
		try
		{
			// Open the ZIP file
			ZipFile in = new ZipFile(dataSet);

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
			in.close();
		}
		catch (IOException e)
		{
			// Report the error
			Logging.errorPrint("Failed to read data set " + dataSet
				+ " due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diBadDataSet", dataSet), TITLE,
				MessageType.ERROR);
			return false;
		}
		return true;
	}

	/**
	 * Read data set.
	 * 
	 * @param dataSet the data set
	 * 
	 * @return true, if successful
	 */
	private boolean readDataSet(File dataSet)
	{
		try
		{
			// Open the ZIP file
			ZipFile in = new ZipFile(dataSet);

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
				Logging.errorPrint("File " + dataSet
					+ " is not a valid datsset - no Install.lst file");
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getFormattedString("in_diNoInstallFile",
						dataSet.getName()), TITLE, MessageType.WARNING);
				in.close();
				return false;
			}
			
			// Parse the install file
			InputStream inStream = in.getInputStream(installEntry);
			BufferedReader reader
			   = new BufferedReader(new InputStreamReader(inStream, "UTF-8")); //$NON-NLS-1$
			
			StringBuffer installInfo = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null)
			{
				installInfo.append(line);
				installInfo.append("\n");
			}

			final InstallLoader loader = new InstallLoader();
			loader.loadLstString(null, dataSet.toURI(), installInfo.toString());
			campaign = loader.getCampaign();
			in.close();
		}
		catch (IOException e)
		{
			// Report the error
			Logging.errorPrint("Failed to read data set " + dataSet
				+ " due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diBadDataSet", dataSet), TITLE,
				MessageType.ERROR);
			return false;
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Failed to parse data set " + dataSet
				+ " due to ", e);
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diBadDataSet", dataSet), TITLE,
				MessageType.ERROR);
			return false;
		}

		// Validate that the campaign is compatible with our version
		if (campaign.getSafe(StringKey.MINDEVVER) != null
			&& !CoreUtility.isPriorToCurrent(campaign.getSafe(StringKey.MINDEVVER)))
		{
			if (CoreUtility.isCurrMinorVer(campaign.getSafe(StringKey.MINDEVVER)))
			{
				Logging.errorPrint("Dataset " + campaign.getDisplayName()
					+ " needs at least PCGen version "
					+ campaign.getSafe(StringKey.MINDEVVER)
					+ " to run. It could not be installed.");
				ShowMessageDelegate.showMessageDialog(LanguageBundle
					.getFormattedString("in_diVersionTooOldDev", campaign.getSafe(StringKey.MINDEVVER), campaign.getSafe(StringKey.MINVER)), TITLE,
					MessageType.WARNING);
				return false;
			}
		}
		if (campaign.getSafe(StringKey.MINVER) != null
			&& !CoreUtility.isPriorToCurrent(campaign.getSafe(StringKey.MINVER)))
		{
			Logging.errorPrint("Dataset " + campaign.getDisplayName()
				+ " needs at least PCGen version " + campaign.getSafe(StringKey.MINVER)
				+ " to run. It could not be installed.");
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_diVersionTooOld", campaign.getSafe(StringKey.MINVER)),
				TITLE, MessageType.WARNING);
			return false;
		}
		
		// Display the info
		dataSetSel.setText(dataSet.getAbsolutePath());
		dataSetDetails.setText(MainSource.buildInfoLabel(campaign));
		if (campaign.get(ObjectKey.DESTINATION) == null)
		{
			locDataButton.setSelected(false);
			locVendorDataButton.setSelected(false);
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
			}
		}
		currDataSet = dataSet;
		
		toFront();
		return true;
	}
}