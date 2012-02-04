/*
 * MainExport.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Title:        MainExport.java
 * Description:  New GUI implementation for exporting PCs and Parties
 *               via templates. This class handles the "standard" option
 *               on the export menu.
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author Jason Buchanan
 * @version $Revision$
 */
final class MainExport extends JPanel
{
	static final long serialVersionUID = 6401354046356862511L;
	private static final CsheetFilter csheetFilter = new CsheetFilter();
	private static final CsheetFilter psheetFilter = new CsheetFilter(1);
	private JButton closeButton = new JButton();
	private JButton exportButton = new JButton();
	private JButton templatePathButton = new JButton();
	private JCheckBox cboxParty = new JCheckBox();
	private JLabel lblPCs = new JLabel();
	private JLabel lblTemplates = new JLabel();
	private JList pcList;
	private JList templateList;
	private JPanel buttonPanel = new JPanel();
	private JScrollPane pcScroll;
	private JScrollPane templateScroll;
	private TemplateListModel templateModel;
	private boolean partyMode = false;

	/**
	 * Constructor
	 * @param exportType
	 */
	public MainExport(String exportType)
	{
		try
		{
			jbInit(exportType);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
		}
	}

	/**
	 * Set the curently selected PC to either the entire party or a specific
	 * PC.
	 *
	 * @param curSel The index of the PC to be selected.
	 */
	public void setCurrentPCSelection(int curSel)
	{
		pcList.updateUI();

		if ((curSel > 0) && ((curSel - 1) < pcList.getModel().getSize())) //an individual PC is selected
		{
			setPartyMode(false);
			pcList.setSelectedIndex(curSel - 1);
		}
		else
		{
			setPartyMode(true);
			pcList.setSelectedIndex(pcList.getModel().getSize() - 1); //select "Entire Party" if the user's on DM Tools or Campaign tab
		}

		setDefaultTemplateSelection();
	}

	void refreshTemplates()
	{
		((TemplateListModel) templateList.getModel()).updateTemplateList();
	}

	private void setDefaultTemplateSelection()
	{
		String tempSel;

		if (partyMode)
		{
			tempSel = SettingsHandler.getSelectedPartyHTMLOutputSheet();
		}
		else
		{
			tempSel = SettingsHandler.getSelectedCharacterHTMLOutputSheet(null);
		}

		// Need to make sure to pick a safe item!
		// Bug #714808 sage_sam 04 April 2003
		final File templateDir = SettingsHandler.getPcgenOutputSheetDir();

		if (templateDir != null)
		{
			final int templatePathEnd = templateDir.getAbsolutePath().length() + 1;

			if ((templatePathEnd > 1) && (templatePathEnd < tempSel.length()))
			{
				tempSel = tempSel.substring(templatePathEnd);
			}
		}

		templateList.setSelectedIndex(Math.max(0, templateModel.indexOf(tempSel)));
	}

	private void setPartyMode(boolean party)
	{
		if (partyMode != party)
		{
			partyMode = party;
			final TemplateListModel tlModel = (TemplateListModel) templateList.getModel();
			tlModel.setPartyMode(party);
			tlModel.updateTemplateList(); //reload template names
			templateList.revalidate();
			templateList.updateUI();
			cboxParty.setSelected(party);
		}
	}

	private void getTemplatePath()
	{
		JFileChooser fcTemplates = new JFileChooser();
		fcTemplates.setCurrentDirectory(new File(SettingsHandler.getHTMLOutputSheetPath()));

		if (fcTemplates.showOpenDialog(MainExport.this) == JFileChooser.APPROVE_OPTION)
		{
			File newTemplatePath = fcTemplates.getSelectedFile();

			if (!newTemplatePath.isDirectory())
			{
				newTemplatePath = newTemplatePath.getParentFile();
			}

			SettingsHandler.setPcgenOutputSheetDir(newTemplatePath.getParentFile());

			final TemplateListModel tlModel = (TemplateListModel) templateList.getModel();
			tlModel.updateTemplateList(); //reload template names
			templateList.revalidate(); //refresh the list
			setDefaultTemplateSelection(); //just in case we've moved back to where the default is
		}
	}

	private void export()
	{
		int[] pcExports;

		final String templateName = (String) templateList.getSelectedValue();
		final int idx = templateName.lastIndexOf('.');
		String extension = "";

		if (idx >= 0)
		{
			extension = templateName.substring(idx + 1);
		}

		final PFileChooser fcExport = new PFileChooser();
		fcExport.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fcExport.setCurrentDirectory(new File(SettingsHandler.getHTMLOutputSheetPath()));
		fcExport.addChoosableFileFilter(null, "All Files (*.*)");

		String desc;

		if ("htm".equalsIgnoreCase(extension) || "html".equalsIgnoreCase(extension))
		{
            fcExport.addChoosableFileFilter(null, "HTML Documents (*.htm, *.html)");
		}
		else if ("xml".equalsIgnoreCase(extension))
		{
            fcExport.addChoosableFileFilter(null, "XML Documents (*.xml)");
		}
		else
		{
            desc = extension + " Files";
            fcExport.addChoosableFileFilter(extension, desc + " (*." + extension + ")");
		}

		if (!partyMode)
		{
			pcExports = pcList.getSelectedIndices();
		}
		else
		{
			pcExports = new int[]{ -2 }; //this value should never happen with getSelectedIndices()
		}

		for (int loop = 0; loop < pcExports.length; loop++)
		{
			final String pcName = partyMode ? "Entire Party" : (String) pcList.getModel().getElementAt(pcExports[loop]);
			String path = partyMode ? null :
					Globals.getPCList().get(pcExports[loop]).getFileName();
			if (path != null && path.length() > 0)
			{
				path = new File(path).getParent().toString();
			}
			else
			{
				path = SettingsHandler.getLastUsedPcgPath().toString();
			}
			fcExport.setSelectedFile(new File(path + File.separator + pcName
				+ "." + extension));
			fcExport.setDialogTitle("Export " + pcName);

			if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			{
				continue;
			}

			final String aFileName = fcExport.getSelectedFile().getAbsolutePath();

			if (aFileName.length() < 1)
			{
				ShowMessageDelegate.showMessageDialog("You must set a filename.", "PCGen", MessageType.ERROR);
				continue;
			}

			try
			{
				final File outFile = new File(aFileName);

				if (outFile.isDirectory())
				{
					ShowMessageDelegate.showMessageDialog("You cannot overwrite a directory with a file.", "PCGen", MessageType.ERROR);

					continue;
				}

				if (outFile.exists() && SettingsHandler.getAlwaysOverwrite() == false)
				{
					int reallyClose = JOptionPane.showConfirmDialog(this,
							"The file " + outFile.getName() + " already exists, are you sure you want to overwrite it?",
							"Confirm overwriting " + outFile.getName(), JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION)
					{
						continue;
					}
				}

				printToFile(outFile, pcExports[loop]);
				Globals.executePostExportCommandStandard(aFileName);
			}
			catch (IOException ex)
			{
				ShowMessageDelegate.showMessageDialog("Could not export " + pcName + ". Try another filename.", "PCGen", MessageType.ERROR);
				Logging.errorPrint("Could not export " + pcName, ex);
			}
		}
	}

	private void jbInit(String exportType) throws Exception
	{
		lblPCs.setText("Select a Character:");
		pcList = new JList(new PCListModel());
		pcList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pcScroll = new JScrollPane();
		pcScroll.getViewport().setView(pcList);
		lblTemplates.setText("Select a Template:");
		// If it is not text then go down the html/xml path, else go down the text path
		if (exportType.equals(GuiConstants.EXPORT_AS_HTML_XML)) {
			templateList = new JList(templateModel = new pcgen.gui.TemplateListModel(csheetFilter, psheetFilter, partyMode, "htmlxml"));
		} else {
			templateList = new JList(templateModel = new pcgen.gui.TemplateListModel(csheetFilter, psheetFilter, partyMode, "text"));
		}
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateScroll = new JScrollPane();
		templateScroll.getViewport().setView(templateList);
		templatePathButton.setText("Find Templates...");
		templatePathButton.setMnemonic(KeyEvent.VK_F);
		templatePathButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					getTemplatePath();
				}
			});
		exportButton.setText("Export");
		exportButton.setMnemonic(KeyEvent.VK_E);
		exportButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (templateList.getSelectedValue() == null)
					{
						ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_exportNoTemplate"), "PCGen", MessageType.ERROR);
					}
					else
					{
						export();
					}
				}
			});

		closeButton.setText("Close");
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JFrame parentFrame = (JFrame) getParent().getParent().getParent().getParent(); //ugly, but effective...
					parentFrame.dispose();
				}
			});
		buttonPanel.add(exportButton);
		buttonPanel.add(closeButton);
		cboxParty.setText("Entire Party");
		cboxParty.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setPartyMode(cboxParty.isSelected());
					pcList.setEnabled(!cboxParty.isSelected());
					templateList.updateUI();
					boolean enable = ((TemplateListModel)templateList.getModel()).getNumFiles() > 0;
					exportButton.setEnabled(enable);

					//templateList.revalidate();
					setDefaultTemplateSelection();
				}
			});
		this.setLayout(new BorderLayout());

		final JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(buttonPanel);

		final JPanel contentPane = new JPanel(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		contentPane.add(lblPCs, gbc);
		gbc.gridx++;
		gbc.insets = new Insets(0, 0, 0, 0);
		contentPane.add(lblTemplates, gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.2;
		gbc.weighty = 1.0;
		contentPane.add(pcScroll, gbc);
		gbc.gridx++;
		gbc.weightx = 0.8;
		contentPane.add(templateScroll, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		contentPane.add(cboxParty, gbc);

		this.add(contentPane, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.SOUTH);

			this.setSize(new Dimension(500, 400));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private void printToFile(File outFile, int pcIndex)
		throws IOException
	{
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
		final File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator
				+ (String) templateList.getSelectedValue());

		if (partyMode)
		{
			SettingsHandler.setSelectedPartyHTMLOutputSheet(template.getAbsolutePath());
			(new ExportHandler(template)).write(Globals.getPCList(), bw);
			bw.close();
		}
		else
		{

			final PlayerCharacter aPC = Globals.getPCList()
				.get(pcIndex);
			SettingsHandler.setSelectedCharacterHTMLOutputSheet(template
				.getAbsolutePath(), aPC);
			(new ExportHandler(template)).write(aPC, bw);
		}

		bw.close();
	}

	private static class PCListModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			if (index < Globals.getPCList().size())
			{
				final PlayerCharacter aPC = Globals.getPCList().get(index);

				return aPC.getDisplayName();
			}
			return null;
		}

		public int getSize()
		{
			return Globals.getPCList().size();
		}
	}
}
