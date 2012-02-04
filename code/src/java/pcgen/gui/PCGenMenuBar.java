/*
 * PCGenMenuBar.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 8/12/2007
 *
 * $Id$
 */
package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.filter.FilterDialogFactory;
import pcgen.gui.utils.BrowserLauncher;
import pcgen.gui.utils.JOpenRecentMenu;
import pcgen.gui.utils.Utility;
import pcgen.util.FOPResourceChecker;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * Main menu bar of the PCGen application.
 * <p/>
 * There is still a tight integration with the 
 * PCGen_Frame1 class though, where this was originally 
 * defined.
 * <p/>
 * The {@link Options}
 * and {@link GameModes} are all created externally.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class PCGenMenuBar extends JMenuBar
{
	/** The PCGen window we are attached to. */
	private final PCGen_Frame1 mainFrame;

	static final long serialVersionUID = 1042236188732008819L;

	/** Instantiated popup frame {@link ExportPopup}. */
	public ExportPopup exportPopup = null;

	/** Instantiated popup frame {@link AboutFrame}. */
	AboutFrame aboutFrame = null;

	/** Instantiated popup frame {@link DebugFrame}. */
	DebugFrame debugFrame = null;

	/** Instantiated popup frame {@link ExportPDFPopup}. */
	ExportPDFPopup exportPDFPopup = null;

	/** Instantiated popup frame {@link ExportTextPopup}. */
	ExportTextPopup exportTextPopup = null;

	private JCheckBoxMenuItem debugMode;
	JMenu exportMenu;
	private JMenu filtersMenu;
	private JMenu helpMenu;
	JMenuItem addKit;
	JMenuItem closeAllItem;
	JMenuItem closeItem;
	JMenuItem exitItem;
	JMenuItem exportItem;
	private JMenuItem exportPDFItem;
	private JMenuItem installDataItem;
	JMenuItem listEditor;
	JMenuItem newItem;
	JMenuItem newNPCItem;
	JMenuItem openItem;
	JMenuItem partyCloseItem;
	JMenuItem partyOpenItem;
	JMenuItem partySaveAsItem;
	JMenuItem partySaveItem;
	JMenuItem printItem;
	JMenuItem printPreviewItem;
	JMenuItem revertToSavedItem;
	JMenuItem saveAllItem;
	JMenuItem saveAsItem;
	JMenuItem saveItem;
	JMenuItem treasureItem;
	JOpenRecentMenu openRecentPCMenu;
	JOpenRecentMenu openRecentPartyMenu;
	private LoggingLevelMenu loggingMenu;

	/** Instantiated popup frame {@link PrintFrame}. */
	PrintFrame printFrame = null;
	boolean enablePDF;

	private GameModes gameModeMenu;

	/**
	 * Create a new menu item bar including all menu items.  
	 * @param pcgenframe The PCGen window we are attached to.
	 * @param frameActionListener Listener for Menubar and toolbar actions.
	 */
	public PCGenMenuBar(PCGen_Frame1 pcgenFrame,
		FrameActionListener frameActionListener)
	{
		mainFrame = pcgenFrame;
		// check for resources needed by FOP
		enablePDF = (FOPResourceChecker.getMissingResourceCount() == 0);
		mainFrame.checkResources();

		//File Menu
		JMenu fileMenu = createFileMenu(frameActionListener);
		this.add(fileMenu);

		//Options Menu (called Settings in the UI)
		Options optionMenu = new Options();
		this.add(optionMenu);
		gameModeMenu = optionMenu.getGameModeMenu();

		//Tools Menu
		JMenu toolsMenu = createToolsMenu();
		this.add(toolsMenu);

		//Debug Menu
		JMenu debugMenu = createDebugMenu();
		this.add(debugMenu);

		//Help Menu
		helpMenu = createHelpMenu();
		this.add(helpMenu);

		// Scootch the Help menu over to the right
		separateHelpMenu(!UIFactory.isWindowsUI());
	}

	/**
	 * Create the file menu, which handles character related things such as 
	 * creating, loading, saving and printing character.
	 * 
	 * @param frameActionListener The listener interested in events from us.
	 * @return The file menu.
	 */
	private JMenu createFileMenu(FrameActionListener frameActionListener)
	{
		JMenu fileMenu = Utility.createMenu("mnuFile", null, true);

		newItem =
				Utility.createMenuItem("mnuFileNew",
					frameActionListener.newActionListener, "file.new",
					"shortcut N", "New16.gif", false);
		fileMenu.add(newItem);

		newNPCItem =
				Utility.createMenuItem("mnuFileNewNPC",
					frameActionListener.newNPCActionListener, "file.newNPC",
					null, "NewNPC16.gif", false);
		fileMenu.add(newNPCItem);

		openItem =
				Utility.createMenuItem("mnuFileOpen",
					frameActionListener.openActionListener, "file.open",
					"shortcut O", "Open16.gif", true);
		fileMenu.add(openItem);

		openRecentPCMenu =
				new JOpenRecentMenu(new JOpenRecentMenu.OpenRecentCallback()
				{
					public void openRecentPerformed(ActionEvent e, File file)
					{
						PCGenMenuBar.this.mainFrame.loadPCFromFile(file);
					}
				});
		fileMenu.add(openRecentPCMenu);

		fileMenu.addSeparator();

		closeItem =
				Utility.createMenuItem("mnuFileClose",
					frameActionListener.closeActionListener, "file.close",
					"shortcut W", "Close16.gif", false);
		fileMenu.add(closeItem);

		closeAllItem =
				Utility.createMenuItem("mnuFileCloseAll",
					frameActionListener.closeAllActionListener,
					"file.closeall", null, "CloseAll16.gif", false);

		// Special so that Close A_l_l, not C_l_ose All
		//closeAllItem.setDisplayedMnemonicIndex(7); // JDK 1.4
		fileMenu.add(closeAllItem);

		saveItem =
				Utility.createMenuItem("mnuFileSave",
					frameActionListener.saveActionListener, "file.save",
					"shortcut S", "Save16.gif", false);
		fileMenu.add(saveItem);

		saveAsItem =
				Utility.createMenuItem("mnuFileSaveAs",
					frameActionListener.saveAsActionListener, "file.saveas",
					"shift-shortcut S", "SaveAs16.gif", false);

		// Special so that Save _A_s..., not S_a_ve As...
		//saveAsItem.setDisplayedMnemonicIndex(5); // JDK 1.4
		fileMenu.add(saveAsItem);

		saveAllItem =
				Utility.createMenuItem("mnuFileSaveAll",
					frameActionListener.saveAllActionListener, "file.saveall",
					null, "SaveAll16.gif", false);
		fileMenu.add(saveAllItem);

		revertToSavedItem =
				Utility.createMenuItem("mnuFileRevertToSaved",
					frameActionListener.revertToSavedActionListener,
					"file.reverttosaved", "shortcut R", null, false);
		fileMenu.add(revertToSavedItem);

		fileMenu.addSeparator();

		JMenu partyMenu = Utility.createMenu("mnuFileParty", null, true);
		fileMenu.add(partyMenu);

		partyOpenItem =
				Utility.createMenuItem("mnuFilePartyOpen",
					frameActionListener.partyOpenActionListener,
					"file.party.open", null, "Open16.gif", true);
		partyMenu.add(partyOpenItem);

		openRecentPartyMenu =
				new JOpenRecentMenu(new JOpenRecentMenu.OpenRecentCallback()
				{
					public void openRecentPerformed(ActionEvent e, File file)
					{
						PCGenMenuBar.this.mainFrame.loadPartyFromFile(file);
					}
				});
		partyMenu.add(openRecentPartyMenu);

		partyCloseItem =
				Utility.createMenuItem("mnuFilePartyClose",
					frameActionListener.partyCloseActionListener,
					"file.party.close", null, "Close16.gif", false);
		partyMenu.addSeparator();

		partyMenu.add(partyCloseItem);

		partySaveItem =
				Utility.createMenuItem("mnuFilePartySave",
					frameActionListener.partySaveActionListener,
					"file.party.save", null, "Save16.gif", false);
		partyMenu.add(partySaveItem);

		partySaveAsItem =
				Utility.createMenuItem("mnuFilePartySaveAs",
					frameActionListener.partySaveAsActionListener,
					"file.party.saveas", null, "SaveAs16.gif", false);
		partyMenu.add(partySaveAsItem);

		fileMenu.addSeparator();

		printPreviewItem =
				Utility.createMenuItem("mnuFilePrintPreview",
					frameActionListener.printPreviewActionListener,
					"file.printpreview", null, "PrintPreview16.gif", false);
		fileMenu.add(printPreviewItem);

		printItem =
				Utility.createMenuItem("mnuFilePrint",
					frameActionListener.printActionListener, "file.print",
					"shortcut P", "Print16.gif", false);
		fileMenu.add(printItem);

		fileMenu.addSeparator();

		// importMenu =
		// Utility.createMenu("Import", 'I',
		// "Import from other file formats", "Import16.gif", true);

		// Do not add until we get some formats to
		// import.  --bko XXX
		//fileMenu.add(importMenu);
		exportMenu = Utility.createMenu("mnuFileExport", "Export16.gif", false);
		fileMenu.add(exportMenu);

		exportItem =
				Utility.createMenuItem("mnuFileExportStandard",
					frameActionListener.exportToStandardActionListener,
					"file.export.standard", null, null, true);
		exportMenu.add(exportItem);

		/*
		 * changed this, so a warning will pop up,
		 * if user tries to print without having the needed
		 * libraries installed
		 *
		 * author: Thomas Behr 03-01-02
		 */
		exportPDFItem =
				Utility.createMenuItem("mnuFileExportPDF", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (enablePDF)
						{
							if (exportPDFPopup == null)
							{
								exportPDFPopup =
										new ExportPDFPopup(
											PCGen_Frame1.baseTabbedPane);
							}
							exportPDFPopup.setCurrentPCSelectionByTab();
						}
						else
						{
							PCGenMenuBar.this.mainFrame.warnAboutMissingResource();
						}
					}
				}, "file.export.pdf", null, null, true);
		exportMenu.add(exportPDFItem);

		// Added New Text Export
		exportItem =
				Utility.createMenuItem("mnuFileExportText",
					frameActionListener.exportToTextActionListener,
					"file.export.text", null, null, true);
		exportMenu.add(exportItem);

		fileMenu.addSeparator();

		addKit =
				Utility.createMenuItem("mnuFileAddKit",
					frameActionListener.addKitActionListener, "assign.kit",
					"shortcut K", "Information16.gif", false);
		fileMenu.add(addKit);

		fileMenu.addSeparator();

		exitItem = Utility.createMenuItem("mnuFileExit", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mainFrame.exitItem_actionPerformed();
			}
		}, "file.exit", "shortcut Q", null, true);
		fileMenu.add(exitItem);
		return fileMenu;
	}

	/**
	 * Creates the tools menu, which handles things like filters, list 
	 * editors, generators and GMGen. 
	 * 
	 * @return The tools menu
	 */
	private JMenu createToolsMenu()
	{
		JMenu toolsMenu = Utility.createMenu("mnuTools", "wrench.gif", true);

		treasureItem =
				Utility.createMenuItem("mnuToolsTreasure", new ActionListener()
				{
					public void actionPerformed(final ActionEvent e)
					{
						new TreasureGeneratorDlg(mainFrame);
					}
				}, "tools.teasure", "shortcut T", null, true);
		toolsMenu.add(treasureItem);
		/*JMenuItem converterItem = Utility.createMenuItem("mnuToolsLstConverter",
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						String basePath = null;

						if (lstConverter == null)
						{
							if (SettingsHandler.getPccFilesLocation() != null)
							{
								basePath = SettingsHandler.getPccFilesLocation().toString();
							}
						}
						else
						{
							basePath = lstConverter.getBasePath();
						}

						JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fc.setDialogTitle("Select base directory to convert");

						if (System.getProperty("os.name").startsWith("Mac OS"))
						{
							// On MacOS X, do not traverse file bundles
							fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
						}

						if (basePath != null)
						{
							final File baseFile = new File(basePath);
							fc.setCurrentDirectory(baseFile.getParentFile());
							fc.setSelectedFile(baseFile);
						}

						final int returnVal = fc.showOpenDialog(getParent().getParent()); //ugly, but it works

						if (returnVal == JFileChooser.APPROVE_OPTION)
						{
							final File file = fc.getSelectedFile();

							if ((lstConverter == null) || (basePath == null) || !basePath.equals(file.toString()))
							{
								lstConverter = new LstConverter(file.toString());
							}

							lstConverter.setVisible(true);
						}
					}
				}, "tools.converter", null, "wrench.gif", true);
		toolsMenu.add(converterItem);*/

		filtersMenu = Utility.createMenu("mnuToolsFilters", "Zoom16.gif", true);
		toolsMenu.add(filtersMenu);

		JMenuItem openFiltersItem =
				Utility.createMenuItem("mnuToolsFiltersOpen",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							FilterDialogFactory.showHideFilterSelectDialog();
						}
					}, "tools.filters.open", null, "Zoom16.gif", true);
		filtersMenu.add(openFiltersItem);

		JMenuItem clearFiltersItem =
				Utility.createMenuItem("mnuToolsFiltersClear",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							FilterDialogFactory
								.clearSelectedFiltersForSelectedFilterable();
						}
					}, "tools.filters.clear", null, "RemoveZoom16.gif", true);
		filtersMenu.add(clearFiltersItem);

		JMenuItem customFiltersItem =
				Utility.createMenuItem("mnuToolsFiltersCustom",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							FilterDialogFactory.showHideFilterCustomDialog();
						}
					}, "tools.filters.custom", null, "CustomZoom16.gif", true);
		filtersMenu.add(customFiltersItem);

		JMenuItem editFiltersItem =
				Utility.createMenuItem("mnuToolsFiltersEdit",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							FilterDialogFactory.showHideFilterEditorDialog();
						}
					}, "tools.filters.edit", null, "EditZoom16.gif", true);
		filtersMenu.add(editFiltersItem);

		toolsMenu.addSeparator();

		JMenuItem gmgenItem =
				Utility.createMenuItem("mnuToolsGMGen", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGenMenuBar.this.mainFrame.openGMGen_actionPerformed();
					}
				}, "tools.gmgen", null, "gmgen_icon.png", true);
		toolsMenu.add(gmgenItem);

		toolsMenu.addSeparator();

		installDataItem =
				Utility.createMenuItem("mnuToolsInstallData", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						DataInstaller di = new DataInstaller(PCGenMenuBar.this.mainFrame);
						di.setVisible(true);
					}
				}, "tools.installdata", null, null, true);
		toolsMenu.add(installDataItem);

		toolsMenu.addSeparator();
		
		//
		// List Editors
		//
		listEditor =
				Utility.createMenuItem("mnuToolsListEditors",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							new LstEditorMain().setVisible(true);
						}
					}, "tools.editors", null, null, true);
		toolsMenu.add(listEditor);
		return toolsMenu;
	}

	/**
	 * Create the debug menu, which handles logging output and 
	 * console display.
	 * 
	 * @return The debug menu.
	 */
	private JMenu createDebugMenu()
	{
		JMenu debugMenu = Utility.createMenu("mnuDebug", null, true);

		loggingMenu = new LoggingLevelMenu();
		debugMode = new JCheckBoxMenuItem();
		debugMode.setText(LanguageBundle.getString("in_mnuDebugMode"));
		debugMode
			.setMnemonic(LanguageBundle.getMnemonic("in_mn_mnuDebugMode"));
		Utility.setDescription(debugMode, LanguageBundle
			.getString("in_mnuDebugModeTip"));
		debugMode.setSelected(Logging.isDebugMode());
		debugMode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Logging.setDebugMode(debugMode.isSelected());
				loggingMenu.updateMenu();

				if (exportPopup != null)
				{
					exportPopup.refreshTemplates();
				}
			}
		});
		debugMenu.add(debugMode);

		// Logging Level menu
		debugMenu.add(loggingMenu);

		JMenuItem consoleMenuItem =
				Utility.createMenuItem("mnuDebugConsole", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (debugFrame == null)
						{
							debugFrame = new DebugFrame();
						}

						debugFrame.setVisible(true);
					}
				}, "debug.console", null, null, true);
		debugMenu.add(consoleMenuItem);
		return debugMenu;
	}

	/**
	 * Create the help menu
	 * @return The help menu
	 */
	private JMenu createHelpMenu()
	{
		JMenu helpMenu = Utility.createMenu("mnuHelp", "Help16.gif", true);

		JMenuItem contextHelpItem =
				Utility.createMenuItem("mnuHelpContext", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PToolBar.displayHelpPanel(true);
					}
				}, "help.context", null, "ContextualHelp16.gif", true);
		helpMenu.add(contextHelpItem);

		JMenuItem docsItem =
				Utility.createMenuItem("mnuHelpDocumentation",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							new DocsFrame();
						}
					}, "help.docs", "F1", "Help16.gif", true);
		helpMenu.add(docsItem);

		helpMenu.addSeparator();

		JMenuItem oglItem =
				Utility.createMenuItem("mnuHelpOGL", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						pcGenGUI.showLicense();
					}
				}, "help.ogl", null, null, true);
		helpMenu.add(oglItem);

		JMenuItem sponsorItem =
				Utility.createMenuItem("mnuHelpSponsors", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						pcGenGUI.showSponsors();
					}
				}, "help.sponsors", null, null, true);
		helpMenu.add(sponsorItem);

		/*            JMenuItem d20Item = CoreUtility.createMenuItem("mnuHelpD20", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				pcGenGUI.showMandatoryD20Info();
			}
		}, "help.d20", null, null, true);
		helpMenu.add(d20Item);
		*/
		JMenuItem todItem =
				Utility.createMenuItem("mnuHelpTipOfTheDay",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							pcGenGUI.showTipOfTheDay();
						}
					}, "help.tod", null, "TipOfTheDay16.gif", true);
		helpMenu.add(todItem);

		helpMenu.addSeparator();

		JMenuItem aboutItem =
				Utility.createMenuItem("mnuHelpAbout", new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						mainFrame.aboutItem_actionPerformed();
					}
				}, "help.about", null, "About16.gif", true);
		helpMenu.add(aboutItem);
		return helpMenu;
	}

	/**
	 * Seperate the help menu, make sure it sticks to the RHS
	 * @param b
	 */
	public void separateHelpMenu(boolean b)
	{
		if (helpMenu == null) // broken!
		{
			throw new IllegalStateException();
		}

		int i = getComponentIndex(helpMenu);

		if (i == -1) // not found!
		{
			throw new IllegalStateException();
		}

		Object o = this.getComponent(i - 1);

		// If help menu is preceded by a menu, it isn't the
		// glue; otherwise, it's the horizontal glue.
		boolean hasGlue = !(o instanceof JMenu);

		if (b && hasGlue)
		{
			return;
		}

		if (!b && !hasGlue)
		{
			return;
		}

		if (b)
		{
			this.add(Box.createHorizontalGlue(), i);
		}
		else
		{
			this.remove(i - 1);
		}
	}

	void checkPrintFrame()
	{
		if (printFrame == null)
		{
			printFrame = new PrintFrame();
		}
	}

	/**
	 * Popup frame with about info
	 */
	final class AboutFrame extends PCGenPopup
	{
		/** Constructor for the About window */
		public AboutFrame()
		{
			super("About PCGen", new MainAbout());
		}
	}

	/**
	 * Popup frame with debug console
	 */
	final class DebugFrame extends PCGenPopup
	{
		/** Constructor for the Debug winsow */
		public DebugFrame()
		{
			super("Debug Console", new MainDebug());
		}
	}

	/**
	 * Popup frame with print options
	 * 
	 * author: Thomas Behr 16-12-01
	 */
	final class PrintFrame extends PCGenPopup
	{
		MainPrint mainPrint = null;

		/** Constructor for the print menu window */
		public PrintFrame()
		{
			super("Print a PC or Party");
			mainPrint = new MainPrint(this, MainPrint.PRINT_MODE);
			setPanel(mainPrint);
			PCGenMenuBar.this.mainFrame.pack();
			PCGenMenuBar.this.mainFrame.setVisible(true);
		}

		/** Set the current PC to be printed by checking hte active tab */
		public void setCurrentPCSelectionByTab()
		{
			if (mainPrint != null)
			{
				mainPrint.setCurrentPCSelection(PCGen_Frame1.baseTabbedPane
					.getSelectedIndex());
			}
		}
	}

	//end PrintFrame

	/**
	 * Pop up frame with Documentation.
	 */
	private final class DocsFrame extends JFrame
	{
		/** Constructor for documentation window */
		public DocsFrame()
		{
			try
			{
				BrowserLauncher.openURL(SettingsHandler.getPcgenDocsDir()
					.getAbsolutePath()
					+ File.separator + "index.html");
			}
			catch (IOException ex)
			{
				ShowMessageDelegate.showMessageDialog(
					"Could not open docs in external browser. "
						+ "Have you set your default browser in the "
						+ "Preference menu? Sorry...", Constants.APPLICATION_NAME,
					MessageType.ERROR);
				Logging.errorPrint("Could not open docs in external browser",
					ex);
			}
		}
	}

	void handleAbout()
	{
		if (aboutFrame == null)
		{
			aboutFrame = new AboutFrame();
			aboutFrame.pack();
		}

		aboutFrame.setVisible(true);
	}

	/**
	 * @return the filtersMenu
	 */
	JMenu getFiltersMenu()
	{
		return filtersMenu;
	}

	/**
	 * @return the gameModeMenu
	 */
	public GameModes getGameModeMenu()
	{
		return gameModeMenu;
	}
}