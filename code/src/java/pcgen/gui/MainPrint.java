/*
 * MainPrint.java
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
import pcgen.util.FOPHandler;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.*;

/**
 * Title:        MainPrint.java
 * Description:  New GUI implementation for printing PCs and Parties via templates
 * Basically, this is a copy of MainExport.  This class handles the "PDF" option
 *               on the export menu.
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author Thomas Behr
 * @version $Revision$
 */
final class MainPrint extends JPanel
{
	/** constant representing exporting (as opposed to printing mode) */
	public static final int EXPORT_MODE = 0;
	/** constant representing printing (as opposed to exporting mode) */
	public static final int PRINT_MODE = 1;
	static final long serialVersionUID = 2322087772130893998L;

	/** TODO internationalise */
	private static final String MSG = "Please standby while rendering ...";
	private static final CsheetFilter csheetFilter = new CsheetFilter();
	private static final CsheetFilter psheetFilter = new CsheetFilter(1);
	private ButtonListener bl = new ButtonListener();
	private FOPHandler fh = new FOPHandler();
	private JButton closeButton = new JButton();
	private JButton exportButton = new JButton();
	private JButton printButton = new JButton();
	private JButton templatePathButton = new JButton();
	private JCheckBox cboxParty = new JCheckBox();
	private JFrame parentFrame;
	private JLabel lblPCs = new JLabel();
	private JLabel lblTemplates = new JLabel();
	private JList pcList;
	private JList templateList;
	private JPanel buttonPanel = new JPanel();
	private JProgressBar progressBar = new JProgressBar();
	private JScrollPane pcScroll;
	private JScrollPane templateScroll;
	private JTextField progressField = new JTextField();
	private TemplateListModel templateModel;
	private Timer timer;
	private boolean partyMode = false;
	private int mode;

	/**
	 * Constructor
	 * @param pf
	 * @param mode
	 */
	public MainPrint(JFrame pf, int mode)
	{
		this.mode = mode;

		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
		}

		this.parentFrame = pf;
		this.timer = new Timer(5,
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						int value = progressBar.getValue();

						if (value == progressBar.getMaximum())
						{
							value = -1;
						}

						progressBar.setValue(value + 1);
					}
				});
	}

	/**
	 * Set the current PC selection
	 * @param curSel
	 */
	public void setCurrentPCSelection(int curSel)
	{
		pcList.updateUI();

		if (((curSel > 0) && ((curSel - 1) < pcList.getModel().getSize())) || pcList.getModel().getSize() == 1) //an individual PC is selected
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

	private void setDefaultTemplateSelection()
	{
		String tempSel;

		if (partyMode)
		{
			tempSel = SettingsHandler.getSelectedPartyPDFOutputSheet();
		}
		else
		{
			tempSel = SettingsHandler.getSelectedCharacterPDFOutputSheet(null);
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

		final int index = Math.max(0, templateModel.indexOf(tempSel));
		templateList.setSelectedIndex(index);
		templateList.ensureIndexIsVisible(index);
	}

	private void setPartyMode(boolean party)
	{
		if (partyMode != party)
		{
			partyMode = party;
			final TemplateListModel tlModel = (TemplateListModel) templateList.getModel();
			tlModel.setPartyMode(party);
			tlModel.updateTemplateList(); //reload template names
			templateList.updateUI();
			cboxParty.setSelected(party);

		}
	}

	private void getTemplatePath()
	{
		PFileChooser fcTemplates = new PFileChooser();
		fcTemplates.setCurrentDirectory(new File(SettingsHandler.getPDFOutputSheetPath()));

		if (fcTemplates.showOpenDialog(MainPrint.this) == JFileChooser.APPROVE_OPTION)
		{
			File newTemplatePath = fcTemplates.getSelectedFile();

			if (!newTemplatePath.isDirectory())
			{
				newTemplatePath = newTemplatePath.getParentFile().getParentFile();
			}

			final TemplateListModel tlModel = (TemplateListModel) templateList.getModel();
			tlModel.updateTemplateList(); //reload template names
			templateList.updateUI(); //refresh the list
			setDefaultTemplateSelection(); //just in case we've moved back to where the default is
		}
	}

	/**
	 * Thomas Behr
	 * 23-12-01
	 */
	private void block()
	{
		closeButton.setEnabled(false);
		closeButton.update(closeButton.getGraphics());
		exportButton.setEnabled(false);
		exportButton.update(exportButton.getGraphics());
		printButton.setEnabled(false);
		printButton.update(printButton.getGraphics());
		progressField.setText(MSG);
		progressField.update(progressField.getGraphics());
		progressBar.setValue(0);
	}

	/**
	 * Thomas Behr
	 * 18-12-01
	 * <p/>
	 * this is just a quick and dirty hack
	 */
	private void export()
	{
		int[] pcExports;

		final String extension = ".pdf";

		PFileChooser fcExport = new PFileChooser();
		fcExport.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fcExport.addChoosableFileFilter(null, "All Files (*.*)");
		fcExport.addChoosableFileFilter("pdf", "PDF Documents (*.pdf)");

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
			String path = partyMode ? null : Globals.getPCList().get(pcExports[loop]).getFileName();
			if (path != null && path.length() > 0)
			{
				path = new File(path).getParent().toString();
			}
			else
			{
				path = SettingsHandler.getLastUsedPcgPath().toString();
			}
			fcExport.setCurrentDirectory(new File(path));
			fcExport.setSelectedFile(new File(path + File.separator + pcName
				+ extension));
			fcExport.setDialogTitle("Export " + pcName);

			try
			{
				if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
				{
					continue;
				}
			}
			catch (Exception ex)
			{
				Logging.errorPrint("Could not show Save Dialog for " + pcName);

				continue;
			}

			final String characterFileName = fcExport.getSelectedFile().getAbsolutePath();

			if (characterFileName.length() < 1)
			{
				ShowMessageDelegate.showMessageDialog("You must set a filename.", "PCGen", MessageType.ERROR);

				continue;
			}

			try
			{
				final File outFile = new File(characterFileName);

				if (outFile.isDirectory())
				{
					ShowMessageDelegate.showMessageDialog("You cannot overwrite a directory with a file.", "PCGen", MessageType.ERROR);

					continue;
				}

				if (outFile.exists() && SettingsHandler.getAlwaysOverwrite() == false)
				{
					int reallyClose = JOptionPane.showConfirmDialog(this,
							"The file " + outFile.getName() + " already exists, " + "are you sure you want "
							+ "to overwrite it?", "Confirm overwriting " + outFile.getName(), JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION)
					{
						continue;
					}
				}

				// can NOT block until after all
				// possible user input is done
				block();

				/**
				 * Dekker500
				 * Feb 1, 2003
				 *
				 * If user selected an XSLT template, perform export to base XML file
				 */
				File tmpFile;
				String selectedTemplate = (String) templateList.getSelectedValue();

				if (selectedTemplate.endsWith(".xslt") || selectedTemplate.endsWith(".xsl"))
				{
					tmpFile = File.createTempFile("currentPC_", ".xml");
					printToXMLFile(tmpFile, pcExports[loop]);

					File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator
							+ (String) templateList.getSelectedValue());
					fh.setInputFile(tmpFile, template);
					SettingsHandler.setSelectedCharacterPDFOutputSheet(template
						.getAbsolutePath(), Globals
						.getPCList().get(pcExports[loop]));
				}
				else
				{
					tmpFile = File.createTempFile("currentPC_", ".fo");
					printToFile(tmpFile, pcExports[loop]);
					fh.setInputFile(tmpFile);
				}

				// setting up pdf renderer
				fh.setMode(FOPHandler.PDF_MODE);
				fh.setOutputFile(outFile);

				// render to pdf
				Throwable throwable = null;
				timer.start();

				try
				{
					Logging.memoryReport();
					Runtime.getRuntime().gc();
					Logging.memoryReport();
					new org.apache.fop.apps.Options(); // this must be instantiated for getVersion() to work.
					System.out.println("Fop Version: " + org.apache.fop.apps.Version.getVersion());
					fh.run();

					Logging.memoryReport();
					Runtime.getRuntime().gc();
					Logging.memoryReport();
				}
				catch (Throwable t)
				{
					Logging.memoryReport();
					throwable = t;
				}

				timer.stop();

				// must unblock before can do user dialogs
				unblock();

				tmpFile.deleteOnExit();

				if (throwable != null)
				{
					throwable.printStackTrace();
					if (throwable instanceof OutOfMemoryError)
					{
						StringBuffer errMsg = new StringBuffer(
							"Your character could not be exported as there was not\n"
								+ "enough memory available.\n\n");
						if (Globals.getPCList().size() > 1)
						{
							errMsg
								.append("To export out your character, please try closing and \n"
									+ "reopening PCGen and then only loading the required PC.");
						}
						else
						{
							errMsg
								.append("To export out your character, please try running PCGen\n"
									+ "using the pcgenhighmem.bat file (or an equivalent).");
						}
						ShowMessageDelegate.showMessageDialog(
							errMsg.toString(), "PCGen", MessageType.ERROR);
						Runtime.getRuntime().gc();
						Logging.memoryReport();
					}
					else
					{
						ShowMessageDelegate.showMessageDialog(throwable.getClass().getName() + ": " + throwable.getMessage(), "PCGen",
						MessageType.ERROR);
					}
				}

				String errMessage = fh.getErrorMessage();

				if (errMessage.length() > 0)
				{
					ShowMessageDelegate.showMessageDialog(errMessage, "PCGen", MessageType.ERROR);
				}

				Globals.executePostExportCommandPDF(characterFileName);
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not export " + pcName, ex);
				ShowMessageDelegate.showMessageDialog("Could not export " + pcName + ". Try another filename.", "PCGen", MessageType.ERROR);
			}
			catch (Throwable ex)
			{
				unblock();
				Logging.errorPrint("Could not export " + pcName, ex);
				ShowMessageDelegate.showMessageDialog("Could not export " + pcName + ". See console for details.", "PCGen", MessageType.ERROR);
			}
		}
	}

	private void jbInit() throws Exception
	{
		lblPCs.setText("Select a Character:");
		pcList = new JList(new PCListModel());
		pcList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pcScroll = new JScrollPane();
		pcScroll.getViewport().setView(pcList);
		lblTemplates.setText("Select a Template:");
		templateList = new JList(templateModel = new TemplateListModel(csheetFilter, psheetFilter, partyMode, "pdf"));
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateScroll = new JScrollPane();
		templateScroll.getViewport().setView(templateList);
		templatePathButton.setText("Find Templates...");
		templatePathButton.setMnemonic(KeyEvent.VK_F);
		templatePathButton.addActionListener(bl);
		exportButton.setText("Export");
		exportButton.setMnemonic(KeyEvent.VK_E);
		exportButton.addActionListener(bl);

		/**
		 * Thomas Behr
		 * 18-12-01
		 *
		 * this is just a quick and dirty hack
		 */
		printButton.setText("Print");
		printButton.setMnemonic(KeyEvent.VK_P);
		printButton.addActionListener(bl);

		closeButton.setText("Close");
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(bl);

		/**
		 * Thomas Behr
		 * 18-12-01
		 *
		 * this is just a quick and dirty hack
		 */
		if (mode == EXPORT_MODE)
		{
			buttonPanel.add(exportButton);
		}
		else if (mode == PRINT_MODE)
		{
			buttonPanel.add(printButton);
		}

		buttonPanel.add(closeButton);

		cboxParty.setText("Entire Party");
		cboxParty.addActionListener(bl);

		progressField.setBackground(UIManager.getColor("Panel.background"));
		progressField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		progressField.setEditable(false);
		progressField.setText("");
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(0);

		JPanel progressPanel = new JPanel(new GridLayout(2, 1));
		progressPanel.add(progressField);
		progressPanel.add(progressBar);

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

		final JPanel centerPane = new JPanel(new BorderLayout());
		centerPane.add(contentPane, BorderLayout.CENTER);
		centerPane.add(buttonPane, BorderLayout.SOUTH);

		this.add(centerPane, BorderLayout.CENTER);
			this.add(progressPanel, BorderLayout.SOUTH);
			this.setSize(new Dimension(500, 400));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Thomas Behr
	 * 16-12-01
	 * <p/>
	 * this is just a quick and dirty hack
	 */
	private void print()
	{
		int[] pcExports;

		//final String templateName = (String)templateList.getSelectedValue();
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

			block();

			try
			{
				/*
				 * Dekker500
				 * Feb 1, 2003
				 *
				 * If user selected an XSLT template, perform export to base XML file
				 */
				File tmpFile;
				Logging.debugPrint((String) templateList.getSelectedValue());

				if (((String) templateList.getSelectedValue()).endsWith(".xslt"))
				{
					Logging.debugPrint("Printing using XML/XSLT");
					tmpFile = File.createTempFile("currentPC_", ".xml");
					printToXMLFile(tmpFile, pcExports[loop]);

					File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator
							+ (String) templateList.getSelectedValue());
					fh.setInputFile(tmpFile, template);
				}
				else
				{
					Logging.debugPrint("Printing using FO sheets");
					tmpFile = File.createTempFile("currentPC_", ".fo");
					printToFile(tmpFile, pcExports[loop]);
					fh.setInputFile(tmpFile);
				}

				// setting up pdf renderer
				fh.setMode(FOPHandler.AWT_MODE);

//				fh.setInputFile(tmpFile);
				// render to awt
//                                  block();
				Throwable throwable = null;
				timer.start();

				try
				{
					Logging.memoryReport();
					Runtime.getRuntime().gc();
					Logging.memoryReport();
					fh.run();

					Logging.memoryReport();
					Runtime.getRuntime().gc();
					Logging.memoryReport();
				}
				catch (Throwable t)
				{
					Logging.memoryReport();
					throwable = t;
				}

				timer.stop();

//                                  unblock();
				tmpFile.deleteOnExit();

				if (throwable != null)
				{
					Logging.errorPrint("Could not print " + pcName, throwable);
					if (throwable instanceof OutOfMemoryError)
					{
						StringBuffer errMsg = new StringBuffer(
							"Your character could not be printed as there was not\n"
								+ "enough memory available.\n\n");
						if (Globals.getPCList().size() > 1)
						{
							errMsg
								.append("To print out your character, please try closing and \n"
									+ "reopening PCGen and then only loading the required PC.");
						}
						else
						{
							errMsg
								.append("To print out your character, please try running PCGen\n"
									+ "using the pcgenhighmem.bat file (or an equivalent).");
						}
						ShowMessageDelegate.showMessageDialog(
							errMsg.toString(), "PCGen", MessageType.ERROR);
						Runtime.getRuntime().gc();
						Logging.memoryReport();
					}
					else
					{
						ShowMessageDelegate.showMessageDialog(throwable.getClass().getName() + ": " + throwable.getMessage(), "PCGen",
						MessageType.ERROR);
					}
				}

				String errMessage = fh.getErrorMessage();

				if (errMessage.length() > 0)
				{
					ShowMessageDelegate.showMessageDialog(errMessage, "PCGen", MessageType.ERROR);
				}
				else if (throwable == null)
				{
					// standard print stuff
					org.apache.fop.render.awt.AWTRenderer awtRenderer = (org.apache.fop.render.awt.AWTRenderer) fh
						.getRenderer();

					PrinterJob printerJob = PrinterJob.getPrinterJob();
					printerJob.setPrintable(awtRenderer);
					printerJob.setPageable(awtRenderer);

					if (printerJob.printDialog())
					{
						printerJob.print();
					}
				}

//  				unblock();
			}
			catch (Throwable ex)
			{
				Logging.errorPrint("Could not print " + pcName, ex);
				ShowMessageDelegate.showMessageDialog("Could not print " + pcName + ". Try again.", "PCGen", MessageType.ERROR);
			}

			unblock();
		}
	}

	private void printToFile(File outFile, int pcIndex)
		throws IOException
	{
		//final BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
		final File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator
				+ (String) templateList.getSelectedValue());

		if (partyMode)
		{
			SettingsHandler.setSelectedPartyPDFOutputSheet(template.getAbsolutePath());
			(new ExportHandler(template)).write(Globals.getPCList(), bw);

//			Party.print(template, bw);
		}
		else
		{
			final PlayerCharacter aPC = Globals.getPCList().get(pcIndex);
			SettingsHandler.setSelectedCharacterPDFOutputSheet(template.getAbsolutePath(), aPC);

			(new ExportHandler(template)).write(aPC, bw);
		}

		bw.close();
	}

	/**
	 * Dekker500
	 * Feb 1, 2003
	 * <p/>
	 * If user selected an XSLT template, perform initial export to base XML file.
	 * <p/>
	 * In XML file mode, there is no such thing as party mode.
	 * Party mode simply means that the output file will contain
	 * each character, one after the other.  The XSLT sheet will extract the
	 * individual characters as required.
	 * @param outFile
	 * @param pcIndex
	 * @throws IOException
	 */
	private void printToXMLFile(File outFile, int pcIndex)
		throws IOException
	{
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
		
		File template = new File(SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator + SettingsHandler.getGame().getName() + File.separator + "base.xml");
		if(!template.exists()) {
			template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + "base.xml");
		}
		PlayerCharacter aPC;

		if (partyMode)
		{
			for (int i = 0; i < Globals.getPCList().size(); i++)
			{
				aPC = Globals.getPCList().get(i);
				(new ExportHandler(template)).write(aPC, bw);
			}
		}
		else
		{
			aPC = Globals.getPCList().get(pcIndex);
			(new ExportHandler(template)).write(aPC, bw);
		}

		bw.close();
	}

	/**
	 * Thomas Behr
	 * 23-12-01
	 */
	private void unblock()
	{
		progressBar.setValue(0);
		progressField.setText("");
		progressField.update(progressField.getGraphics());
		printButton.setEnabled(true);
		printButton.update(printButton.getGraphics());
		exportButton.setEnabled(true);
		exportButton.update(exportButton.getGraphics());
		closeButton.setEnabled(true);
		closeButton.update(closeButton.getGraphics());
	}

	/**
	 * Thomas Behr
	 * 23-12-01
	 */
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();

			if (src.equals(exportButton))
			{
				if (templateList.getSelectedValue() == null)
				{
					ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_exportNoTemplate"), "PCGen", MessageType.ERROR);
				}
				else
				{
					(new Thread(new Runnable()
						{
							public void run()
							{
								export();
							}
						})).start();
				}
			}
			else if (src.equals(printButton))
			{
				if (templateList.getSelectedValue() == null)
				{
					ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_printNoTemplate"), "PCGen", MessageType.ERROR);
				}
				else
				{
					(new Thread(new Runnable()
						{
							public void run()
							{
								print();
							}
						})).start();
				}
			}
			else if (src.equals(closeButton))
			{
				parentFrame.dispose();
			}
			else if (src.equals(cboxParty))
			{
				setPartyMode(cboxParty.isSelected());
				pcList.setEnabled(!cboxParty.isSelected());
				templateList.updateUI();
				boolean enable = ((TemplateListModel)templateList.getModel()).getNumFiles() > 0;
				exportButton.setEnabled(enable);
				printButton.setEnabled(enable);
				setDefaultTemplateSelection();
			}
			else if (src.equals(templatePathButton))
			{
				getTemplatePath();
			}
		}
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
