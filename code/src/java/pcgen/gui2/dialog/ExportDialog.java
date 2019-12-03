/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.PartyFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FacadeComboBoxModel;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.io.ExportUtilities;
import pcgen.system.BatchExporter;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import javafx.stage.FileChooser;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * The dialog provides the list of output sheets for a character or party to
 * be exported to.
 */
public final class ExportDialog extends JDialog implements ActionListener, ListSelectionListener
{

	private static final String PDF_EXPORT_DIR_PROP = "pdfExportDir";
	private static final String HTML_EXPORT_DIR_PROP = "htmlExportDir";
	private static final String PARTY_COMMAND = "PARTY";
	private static final String EXPORT_TO_COMMAND = "EXPORT_TO";
	private static final String EXPORT_COMMAND = "EXPORT";
	private static final String CLOSE_COMMAND = "CLOSE";

	public static void showExportDialog(PCGenFrame parent)
	{
		Window dialog = new ExportDialog(parent);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	private final PCGenFrame pcgenFrame;
	private final FacadeComboBoxModel<CharacterFacade> characterBoxModel;
	private final JComboBox<CharacterFacade> characterBox;
	private final JCheckBox partyBox;
	private final JComboBox<SheetFilter> exportBox;
	private final JList<URI> fileList;
	private final JProgressBar progressBar;
	private final JButton exportButton;
	private final JButton closeButton;
	private Collection<File> allTemplates = null;

	private ExportDialog(PCGenFrame parent)
	{
		super(parent, true);
		this.pcgenFrame = parent;
		this.characterBoxModel =
				new FacadeComboBoxModel<>(CharacterManager.getCharacters(), parent.getSelectedCharacterRef());
		this.characterBox = new JComboBox<>(characterBoxModel);
		this.partyBox = new JCheckBox("Entire Party");
		this.exportBox = new JComboBox<>(SheetFilter.values());
		this.fileList = new JList<>();
		this.progressBar = new JProgressBar();
		this.exportButton = new JButton("Export");
		this.closeButton = new JButton("Close");
		initComponents();
		initLayout();
		setWorking(false);
		allTemplates = getAllTemplates();
		refreshFiles();

		Utility.installEscapeCloseOperation(this);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		characterBoxModel.setReference(null);
		characterBoxModel.setListFacade(null);
	}

	private void initComponents()
	{
		characterBox.setRenderer(new DefaultListCellRenderer()
		{

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus)
			{
				CharacterFacade character = (CharacterFacade) value;
				return super.getListCellRendererComponent(list, character.getNameRef().get(), index, isSelected,
					cellHasFocus);
			}

		});

		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.addListSelectionListener(this);

		exportButton.setDefaultCapable(true);
		getRootPane().setDefaultButton(exportButton);

		partyBox.setActionCommand(PARTY_COMMAND);
		exportBox.setActionCommand(EXPORT_TO_COMMAND);
		exportButton.setActionCommand(EXPORT_COMMAND);
		closeButton.setActionCommand(CLOSE_COMMAND);

		exportBox.addActionListener(this);
		partyBox.addActionListener(this);
		exportButton.addActionListener(this);
		closeButton.addActionListener(this);

		exportButton.setEnabled(false);
		progressBar.setStringPainted(true);

		setTitle("Export a PC or Party");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		UIPropertyContext context = UIPropertyContext.createContext("ExportDialog");
		String defaultOSType = context.getProperty(UIPropertyContext.DEFAULT_OS_TYPE);
		if (defaultOSType != null)
		{
			for (SheetFilter filter : SheetFilter.values())
			{
				if (defaultOSType.equals(filter.toString()))
				{
					exportBox.setSelectedItem(filter);
				}
			}
		}
	}

	private void initLayout()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		Box topPanel = Box.createHorizontalBox();
		topPanel.add(new JLabel("Select Character:"));
		topPanel.add(Box.createHorizontalStrut(5));
		topPanel.add(characterBox);
		topPanel.add(Box.createHorizontalStrut(5));
		topPanel.add(partyBox);
		topPanel.add(Box.createHorizontalGlue());
		topPanel.add(Box.createHorizontalStrut(50));
		topPanel.add(new JLabel("Export to:"));
		topPanel.add(Box.createHorizontalStrut(5));
		topPanel.add(exportBox);
		contentPane.add(topPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(fileList);
		scrollPane.setBorder(
			BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Templates"), scrollPane.getBorder()));
		contentPane.add(scrollPane, BorderLayout.CENTER);

		Box bottomPanel = Box.createHorizontalBox();
		bottomPanel.add(progressBar);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(Box.createHorizontalStrut(5));
		bottomPanel.add(exportButton);
		bottomPanel.add(Box.createHorizontalStrut(5));
		bottomPanel.add(closeButton);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pack();
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		exportButton.setEnabled(fileList.getSelectedIndex() != -1);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (PARTY_COMMAND.equals(e.getActionCommand()))
		{
			characterBox.setEnabled(!partyBox.isSelected());
			refreshFiles();
		}
		else if (EXPORT_TO_COMMAND.equals(e.getActionCommand()))
		{
			UIPropertyContext context = UIPropertyContext.createContext("ExportDialog");
			context.setProperty(UIPropertyContext.DEFAULT_OS_TYPE, exportBox.getSelectedItem().toString());
			refreshFiles();
		}
		else if (EXPORT_COMMAND.equals(e.getActionCommand()))
		{
			doExport();
		}
		else if (CLOSE_COMMAND.equals(e.getActionCommand()))
		{
			dispose();
		}
	}

	private void doExport()
	{
		export(exportBox.getSelectedItem() == SheetFilter.PDF);
	}

	private void setWorking(boolean working)
	{
		progressBar.setVisible(working);
		exportButton.setEnabled(!working);
		fileList.setEnabled(!working);
		exportBox.setEnabled(!working);
	}

	private void export(boolean pdf)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		UIPropertyContext context = UIPropertyContext.createContext("ExportDialog");
		FileChooser fileChooser = new FileChooser();

		File baseDir = null;
		{
			String path;
			if (pdf)
			{
				path = context.getProperty(PDF_EXPORT_DIR_PROP);
			}
			else
			{
				path = context.getProperty(HTML_EXPORT_DIR_PROP);
			}
			if (path != null)
			{
				baseDir = new File(path);
			}
		}
		if (baseDir == null || !baseDir.isDirectory())
		{
			baseDir = SystemUtils.getUserHome();
		}
		fileChooser.setInitialDirectory(baseDir);


		URI uri = fileList.getSelectedValue();
		String extension = ExportUtilities.getOutputExtension(uri.toString(), pdf);
		FileChooser.ExtensionFilter fileFilter;
		if (pdf)
		{
			fileFilter = new FileChooser.ExtensionFilter("PDF Documents", "*.pdf");
		}
		else if ("htm".equalsIgnoreCase(extension) || "html".equalsIgnoreCase(extension))
		{
			fileFilter = new FileChooser.ExtensionFilter("HTML Documents", "*.html", "*.htm");
		}
		else if ("xml".equalsIgnoreCase(extension))
		{
			fileFilter = new FileChooser.ExtensionFilter("XML Documents", "*.xml");
		}
		else
		{
			String desc = extension + " Files (*." + extension + ")";
			fileFilter = new FileChooser.ExtensionFilter(desc, "*." + extension);
		}
		fileChooser.getExtensionFilters().add(fileFilter);
		fileChooser.setSelectedExtensionFilter(fileFilter);
		String name;
		File path;
		if (!partyBox.isSelected())
		{
			CharacterFacade character = (CharacterFacade) characterBox.getSelectedItem();
			path = character.getFileRef().get();
			if (path != null)
			{
				path = path.getParentFile();
			}
			else
			{
				path = new File(PCGenSettings.getPcgDir());
			}
			name = character.getTabNameRef().get();
			if (StringUtils.isEmpty(name))
			{
				name = character.getNameRef().get();
			}
		}
		else
		{
			path = new File(PCGenSettings.getPcgDir());
			name = "Entire Party";
		}
		fileChooser.setInitialDirectory(path);
		fileChooser.setTitle("Export " + name);

		final File outFile = GuiUtility.runOnJavaFXThreadNow(() ->
				fileChooser.showSaveDialog(null));
		if (outFile == null)
		{
			return;
		}

		if (pdf)
		{
			context.setProperty(PDF_EXPORT_DIR_PROP, outFile.getParent());
		}
		else
		{
			context.setProperty(HTML_EXPORT_DIR_PROP, outFile.getParent());
		}

		if (pdf)
		{
			new PDFExporter(outFile, name).execute();
		}
		else
		{
			if (!printToFile(outFile))
			{
				String message = "The character export failed. Please see the log for details.";
				pcgenFrame.showErrorMessage(Constants.APPLICATION_NAME, message);
				return;
			}
			maybeOpenFile(outFile);
			Globals.executePostExportCommandStandard(outFile.getAbsolutePath());
		}
	}

	private void maybeOpenFile(File file)
	{
		UIPropertyContext context = UIPropertyContext.getInstance();
		String value = context.getProperty(UIPropertyContext.ALWAYS_OPEN_EXPORT_FILE);
		Boolean openFile = StringUtils.isEmpty(value) ? null : Boolean.valueOf(value);
		if (openFile == null)
		{
			JCheckBox checkbox = new JCheckBox();
			checkbox.setText("Always perform this action");

			JPanel message = PCGenFrame.buildMessageLabelPanel("Do you want to open " + file.getName() + "?", checkbox);
			int ret = JOptionPane.showConfirmDialog(this, message, "Select an Option", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			if (ret == JOptionPane.CLOSED_OPTION)
			{
				return;
			}
			openFile = BooleanUtils.toBoolean(ret, JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
			if (checkbox.isSelected())
			{
				context.setBoolean(UIPropertyContext.ALWAYS_OPEN_EXPORT_FILE, openFile);
			}
		}
		if (!openFile)
		{
			return;
		}

		if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
		{
			pcgenFrame.showErrorMessage("Cannot Open " + file.getName(),
				"Operating System does not support this operation");
			return;
		}
		try
		{
			Desktop.getDesktop().open(file);
		}
		catch (IOException ex)
		{
			String message = "Failed to open " + file.getName();
			pcgenFrame.showErrorMessage(Constants.APPLICATION_NAME, message);
			Logging.errorPrint(message, ex);
		}
	}

	private File getSelectedTemplate()
	{
		File osDir;
		String outputSheetDirectory = SettingsHandler.getGame().getOutputSheetDirectory();
		if (outputSheetDirectory == null)
		{
			osDir = new File(ConfigurationSettings.getOutputSheetsDir());
		}
		else
		{
			osDir = new File(ConfigurationSettings.getOutputSheetsDir(), outputSheetDirectory);
		}
		URI osPath = new File(osDir, ((SheetFilter) exportBox.getSelectedItem()).getPath()).toURI();
		URI uri = fileList.getSelectedValue();
		return new File(osPath.resolve(uri));
	}

	private boolean printToFile(File outFile)
	{
		File template = getSelectedTemplate();

		if (partyBox.isSelected())
		{
			SettingsHandler.setSelectedPartyHTMLOutputSheet(template.getAbsolutePath());
			PartyFacade party = CharacterManager.getCharacters();
			return BatchExporter.exportPartyToNonPDF(party, outFile, template);
		}
		else
		{
			CharacterFacade character = (CharacterFacade) characterBox.getSelectedItem();
			return BatchExporter.exportCharacterToNonPDF(character, outFile, template);
		}
	}

	private void refreshFiles()
	{
		String outputSheetsDir;
		SheetFilter sheetFilter = (SheetFilter) exportBox.getSelectedItem();
		IOFileFilter ioFilter = FileFilterUtils.asFileFilter(sheetFilter);
		IOFileFilter prefixFilter;
		String defaultSheet = null;
		String outputSheetDirectory = SettingsHandler.getGame().getOutputSheetDirectory();
		if (outputSheetDirectory == null)
		{
			outputSheetsDir = ConfigurationSettings.getOutputSheetsDir() + "/" + sheetFilter.getPath();
		}
		else
		{
			outputSheetsDir = ConfigurationSettings.getOutputSheetsDir() + "/" + outputSheetDirectory + "/"
				+ sheetFilter.getPath();
		}

		if (partyBox.isSelected())
		{
			prefixFilter = FileFilterUtils.prefixFileFilter(Constants.PARTY_TEMPLATE_PREFIX);
		}
		else
		{
			CharacterFacade character = (CharacterFacade) characterBox.getSelectedItem();
			prefixFilter = FileFilterUtils.prefixFileFilter(Constants.CHARACTER_TEMPLATE_PREFIX);
			defaultSheet = character.getDefaultOutputSheet(sheetFilter == SheetFilter.PDF);
			if (StringUtils.isEmpty(defaultSheet))
			{
				defaultSheet = outputSheetsDir + "/"
					+ SettingsHandler.getGame().getOutputSheetDefault(sheetFilter.getTag());
			}
		}
		IOFileFilter filter = FileFilterUtils.and(prefixFilter, ioFilter);
		List<File> files = FileFilterUtils.filterList(filter, allTemplates);
		Collections.sort(files);

		URI osPath = new File(outputSheetsDir).toURI();
		URI[] uriList = new URI[files.size()];
		Arrays.setAll(uriList, i -> osPath.relativize(files.get(i).toURI()));
		fileList.setListData(uriList);
		if (StringUtils.isNotEmpty(defaultSheet))
		{
			URI defaultPath = new File(defaultSheet).toURI();
			fileList.setSelectedValue(osPath.relativize(defaultPath), true);
		}
	}

	private final class PDFExporter extends SwingWorker<Boolean, Boolean>
	{

		private final File saveFile;
		private final String name;

		private PDFExporter(File saveFile, String name)
		{
			this.saveFile = saveFile;
			this.name = name;
			progressBar.setString("Exporting to PDF");
			setWorking(true);
		}

		@Override
		protected Boolean doInBackground()
		{
			boolean result;
			if (partyBox.isSelected())
			{
				PartyFacade party = CharacterManager.getCharacters();
				result = BatchExporter.exportPartyToPDF(party, saveFile, getSelectedTemplate());
			}
			else
			{
				CharacterFacade character = (CharacterFacade) characterBox.getSelectedItem();
				result = BatchExporter.exportCharacterToPDF(character, saveFile, getSelectedTemplate());
			}
			return result;
		}

		@Override
		protected void done()
		{
			boolean exception = true;
			try
			{
				if (get())
				{
					exception = false;
				}
				else
				{
					pcgenFrame.showErrorMessage(
							"Could not export " + name,
							"Error occurred while exporting. See log for details."
					);
				}
			}
			catch (InterruptedException ex)
			{
				// Take no action as we are probably being asked to shut down.
			}
			catch (ExecutionException ex)
			{
				Logging.errorPrint("Could not export " + name, ex);
				pcgenFrame.showErrorMessage("Could not export " + name,
					"Error occurred while exporting. See log for details.");
			}
			finally
			{
				if (!exception)
				{
					Globals.executePostExportCommandPDF(saveFile.getAbsolutePath());
				}
				setWorking(false);
				if (!exception)
				{
					maybeOpenFile(saveFile);
				}
			}
		}
	}

	private static Collection<File> getAllTemplates()
	{
		try
		{
			File dir;
			String outputSheetDirectory = SettingsHandler.getGame().getOutputSheetDirectory();
			if (outputSheetDirectory == null)
			{
				Logging.errorPrint("OUTPUTSHEET|DIRECTORY not defined for game mode " + SettingsHandler.getGame());
				dir = new File(ConfigurationSettings.getOutputSheetsDir());
			}
			else
			{
				dir = new File(ConfigurationSettings.getOutputSheetsDir(), outputSheetDirectory);
				if (!dir.isDirectory())
				{
					Logging.errorPrint(
							"Unable to find game mode outputsheets at " + dir.getCanonicalPath() + ". Trying base.");
					dir = new File(ConfigurationSettings.getOutputSheetsDir());
				}
			}
			if (!dir.isDirectory())
			{
				Logging.errorPrint("Unable to find outputsheets folder at " + dir.getCanonicalPath() + ".");
				return Collections.emptyList();
			}
			return Files.walk(dir.toPath())
			            .filter(f -> !f.endsWith(".fo"))
			            .map(Path::toFile)
			            .collect(Collectors.toList());
		}
		catch (IOException e)
		{
			Logging.errorPrint("failed to find templates", e);
			return Collections.emptyList();
		}
	}

	private enum SheetFilter implements FilenameFilter
	{

		HTMLXML("htmlxml", "Standard", "HTM"),
		PDF("pdf", "PDF", "PDF"),
		TEXT("text", "Text", "TXT");
		private final String dirFilter;
		private final String description;
		private final String tag;

		private SheetFilter(String dirFilter, String description, String tag)
		{
			this.dirFilter = dirFilter;
			this.description = description;
			this.tag = tag;
		}

		public String getPath()
		{
			return dirFilter;
		}

		@Override
		public String toString()
		{
			return description;
		}

		public String getTag()
		{
			return tag;
		}

		@Override
		public boolean accept(File dir, String name)
		{
			return dir.getName().equalsIgnoreCase(dirFilter) && !name.endsWith("~");
		}

	}

}
