/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui2.converter.panel;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.gui2.converter.event.TaskStrategyMessage;
import pcgen.gui3.GuiUtility;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.SystemUtils;

public class WriteDirectoryPanel extends ConvertSubPanel
{

	private File path;

	private final SpringLayout layout = new SpringLayout();

	private final JLabel fileLabel;
	private final JLabel warningLabel;

	private List<Campaign> campaignList;

	public WriteDirectoryPanel()
	{
		fileLabel = new JLabel();
		warningLabel = new JLabel();
	}

	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		TaskStrategyMessage.sendStatus(this, "Finding Data Directories");
		campaignList = pc.getListFor(ListKey.CAMPAIGN);
		path = pc.get(ObjectKey.WRITE_DIRECTORY);
		if (path != null)
		{
			fileLabel.setText(path.getAbsolutePath());
		}
		else
		{
			PCGenSettings context = PCGenSettings.getInstance();
			String outputPathName = context.initProperty(PCGenSettings.CONVERT_OUTPUT_SAVE_PATH, SystemUtils.USER_DIR);
			path = new File(outputPathName);
		}
		pc.put(ObjectKey.WRITE_DIRECTORY, path);
		fireProgressEvent(ProgressEvent.ALLOWED);
		return true;
	}

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	@Override
	public boolean returnAllowed()
	{
		return true;
	}

	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		panel.setLayout(layout);
		Component label = new JLabel("Please select the Directory where Converted files should be written: ");
		AbstractButton button = new JButton("Browse...");
		button.setMnemonic('r');
		button.addActionListener(arg0 -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setInitialDirectory(path);

			while (true)
			{
				File fileToOpen = GuiUtility.runOnJavaFXThreadNow(() ->
						directoryChooser.showDialog(null));
				if (fileToOpen != null)
				{
					assert fileToOpen.isDirectory();
					if (fileToOpen.canRead() && fileToOpen.canWrite())
					{
						path = fileToOpen;
						pc.put(ObjectKey.WRITE_DIRECTORY, path);
						fileLabel.setText(path.getAbsolutePath());
						PCGenSettings context = PCGenSettings.getInstance();
						context.setProperty(PCGenSettings.CONVERT_OUTPUT_SAVE_PATH, path.getAbsolutePath());
						showWarning();
						break;
					}
					JOptionPane.showMessageDialog(
							null,
							"Selection must be a valid " + "(readable & writeable) Directory"
					);
					directoryChooser.setInitialDirectory(path.getParentFile());
				}
				else
				{
					break;
				}
			}
		});
		panel.add(label);
		panel.add(fileLabel);
		panel.add(button);
		panel.add(warningLabel);
		showWarning();
		layout.putConstraint(SpringLayout.NORTH, label, 50, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, fileLabel, 75 + label.getPreferredSize().height, SpringLayout.NORTH,
				panel
		);
		layout.putConstraint(SpringLayout.NORTH, button, 75 + label.getPreferredSize().height, SpringLayout.NORTH,
				panel
		);
		layout.putConstraint(SpringLayout.WEST, label, 25, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.WEST, fileLabel, 25, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, button, -50, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, warningLabel, 20, SpringLayout.SOUTH, fileLabel);
		layout.putConstraint(SpringLayout.WEST, warningLabel, 25, SpringLayout.WEST, panel);

		fileLabel.setText(path.getAbsolutePath());
	}

	private void showWarning()
	{
		List<Campaign> existingCampaigns = getExistingPccs();
		StringBuilder warning = new StringBuilder("<html>");
		if (!existingCampaigns.isEmpty())
		{
			warning.append("<b>Warning</b>: Some converted campaigns already exist in this ");
			warning.append("destination folder and will be skipped:\n<UL>");
			final int maxCampaigns = 15;
			int i = 1;
			for (Campaign camp : existingCampaigns)
			{
				if ((i >= maxCampaigns) && (existingCampaigns.size() > maxCampaigns))
				{
					warning.append("<li>").append(existingCampaigns.size() - maxCampaigns + 1).append(" more campaigns.</li>");
					break;
				}
				warning.append("<li>");
				warning.append(camp.getKeyName());
				warning.append("</li>");
				i++;
			}
			warning.append("</ul>");
		}
		warning.append("</html>");
		warningLabel.setText(warning.toString());
	}

	private List<Campaign> getExistingPccs()
	{
		List<Path> existingFiles = findPCCFiles(path);

		List<Campaign> matchingCampaigns = new ArrayList<>();

		for (Campaign camp : campaignList)
		{
			File campFile = new File(camp.getSourceURI());
			if (existingFiles.stream()
			                 .anyMatch(file -> file.getFileName().toString().equals(campFile.getName())))
			{
				matchingCampaigns.add(camp);
			}
		}

		return matchingCampaigns;
	}

	private static List<Path> findPCCFiles(File aDirectory)
	{
		try
		{
			return Files.walk(aDirectory.toPath())
			            .filter(file -> file.getFileName().toString().endsWith("pcc"))
			            .collect(Collectors.toUnmodifiableList());
		}
		catch (IOException e)
		{
			Logging.errorPrint("failed to walk " + aDirectory, e);
			return Collections.emptyList();
		}
	}

}
