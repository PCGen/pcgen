/*
 * PCGenFrame.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 14, 2008, 1:00:34 PM
 */
package pcgen.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.logging.LogRecord;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.Sponsor;
import pcgen.core.Globals;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CharacterLevelFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.core.facade.CharacterStubFacade;
import pcgen.core.facade.ChooserFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.CompanionFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.PartyFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.UIDelegate;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.dialog.AboutDialog;
import pcgen.gui2.dialog.ChooserDialog;
import pcgen.gui2.dialog.TipOfTheDay;
import pcgen.gui2.sources.SourceSelectionDialog;
import pcgen.gui2.tabs.InfoTabbedPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.ShowMessageGuiObserver;
import pcgen.gui2.util.SwingWorker;
import pcgen.gui2.util.table.TableCellUtilities;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.SourceFileLoader;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.system.Main;
import pcgen.system.PCGenPropBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContext;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;

/**
 * The main window for PCGen. In addition this class is responsible for providing 
 * global UI functions such as message dialogs. 
 *
 * <br/>
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 * 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 * @version $Revision:  $
 */
@SuppressWarnings("serial")
public final class PCGenFrame extends JFrame implements UIDelegate
{

	private final PCGenActionMap actionMap;
	private final CharacterTabs characterTabs;
	private final PCGenStatusBar statusBar;
	private final DefaultReferenceFacade<SourceSelectionFacade> currentSourceSelection;
	private final DefaultReferenceFacade<CharacterFacade> currentCharacterRef;
	private final DefaultReferenceFacade<DataSetFacade> currentDataSetRef;
	private final FilenameListener filenameListener;
	//Since creating a new JFileChooser requires a bit of overhead we reuse the same one every time
	private final JFileChooser chooser;
	private JDialog sourceSelectionDialog = null;
	private SourceLoadWorker sourceLoader = null;
	private String section15 = null;

	public PCGenFrame()
	{
		this.currentSourceSelection = new DefaultReferenceFacade<SourceSelectionFacade>();
		this.currentCharacterRef = new DefaultReferenceFacade<CharacterFacade>();
		this.currentDataSetRef = new DefaultReferenceFacade<DataSetFacade>();
		this.actionMap = new PCGenActionMap(this);
		this.characterTabs = new CharacterTabs(this);
		this.statusBar = new PCGenStatusBar(this);
		this.filenameListener = new FilenameListener();
		this.chooser = new JFileChooser();
		Globals.setRootFrame(this);
		Observer messageObserver = new ShowMessageGuiObserver(this);
		ShowMessageDelegate.getInstance().addObserver(messageObserver);
		ChooserFactory.setDelegate(this);
		initComponents();
		pack();
		initSettings();
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		JComponent root = getRootPane();
		root.setActionMap(actionMap);
		root.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 createInputMap(actionMap));

		characterTabs.add(new InfoGuidePane(this));
		setJMenuBar(new PCGenMenuBar(this));
		add(new PCGenToolBar(this), BorderLayout.NORTH);
		add(characterTabs, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		updateTitle();
		setIconImage(Icons.createImageIcon(Icons.RESOURCE_APP_ICON).getImage());
	}

	/**
	 * This checks to make sure that the given rectangle will be visible
	 * on the current graphics environment
	 */
	private boolean checkBounds(Rectangle rect)
	{
		if (rect.isEmpty())
		{
			return false;
		}
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

		for (GraphicsDevice device : env.getScreenDevices())
		{
			Rectangle bounds = device.getDefaultConfiguration().getBounds();
			if (bounds.contains(rect) || bounds.intersects(rect))
			{
				return true;
			}
		}
		return false;
	}

	private void initSettings()
	{
		final UIPropertyContext frameContext = UIPropertyContext.createContext("PCGenFrame");

		Rectangle screenBounds = getGraphicsConfiguration().getBounds();

		setSize(1060, 685);//this is the default frame dimensions
		setLocationRelativeTo(null);//center the frame
		if (!screenBounds.contains(getBounds()))
		{
			setSize((5 * screenBounds.width) / 6, (5 * screenBounds.height) / 6);
			setLocationRelativeTo(null);//center the frame
		}
		Rectangle frameBounds = getBounds();
		frameBounds.x = frameContext.initInt("bounds.x", frameBounds.x);
		frameBounds.y = frameContext.initInt("bounds.y", frameBounds.x);
		frameBounds.width = frameContext.initInt("bounds.width", frameBounds.width);
		frameBounds.height = frameContext.initInt("bounds.height", frameBounds.height);

		int extendedState = frameContext.initInt("extendedState", NORMAL);
		if (extendedState == ICONIFIED)
		{
			extendedState = NORMAL;
			frameContext.setInt("extendedState", NORMAL);
		}
		setExtendedState(extendedState);
		if (checkBounds(frameBounds))
		{
			setBounds(frameBounds);
		}
		addPropertyChangeListener("extendedState", frameContext);
		addComponentListener(new ComponentAdapter()
		{

			@Override
			public void componentResized(ComponentEvent e)
			{
				updateBounds();
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
				updateBounds();
			}

			private void updateBounds()
			{
				Rectangle bounds = getBounds();
				frameContext.setInt("bounds.x", bounds.x);
				frameContext.setInt("bounds.y", bounds.y);
				frameContext.setInt("bounds.width", bounds.width);
				frameContext.setInt("bounds.height", bounds.height);
			}

		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				PCGenUIManager.closePCGen();
			}

		});
	}

	/**
	 * This is called after initialization and starts up the PCGenFrame
	 * by setting it visible and then performing other startup actions
	 * as the user's preferences dictate.
	 */
	public void startPCGenFrame()
	{
		setVisible(true);
		new StartupWorker().start();
	}

	/**
	 * This thread does the work of starting up the UI.
	 * If command arguments are present it handles them, otherwise it starts up normally.
	 */
	private class StartupWorker extends Thread
	{

		@Override
		public void run()
		{
			try
			{
				boolean alternateStartup = false;
				alternateStartup |= maybeLoadCampaign();
				alternateStartup |= maybeLoadOrCreateCharacter();
				alternateStartup |= maybeStartInNPCGen();
				alternateStartup |= maybeStartInGMGen();

				if (!alternateStartup)
				{
					//Do a default startup
					SwingUtilities.invokeLater(new Runnable()
					{

						@Override
						public void run()
						{
							if (TipOfTheDay.showTipOfTheDay())
							{
								showTipsOfTheDay();
							}
							showSourceSelectionDialog();
						}

					});
				}
			}
			catch (InterruptedException ex)
			{
				Logging.errorPrint("Unexepected exception", ex);
			}
			catch (InvocationTargetException ex)
			{
				Logging.errorPrint("Unexepected exception", ex.getCause());
			}
		}

		private boolean maybeLoadCampaign() throws InterruptedException
		{
			String camp = Main.getStartupCampaign();
			if (camp != null)
			{
				SourceSelectionFacade selection = null;
				ListFacade<SourceSelectionFacade> sources = FacadeFactory.getSourceSelections();
				for (SourceSelectionFacade sourceSelectionFacade : sources)
				{
					if (sourceSelectionFacade.toString().equals(camp))
					{
						selection = sourceSelectionFacade;
						break;
					}
				}
				if (selection != null)
				{
					loadSourceSelection(selection);
					sourceLoader.join();
					return true;
				}
				else
				{
					//did not find source
					//TODO: do something, complain, deal with it, anything
					return false;
				}
			}
			return false;
		}

		/**
		 * loads or creates a character based upon the command line arguments.
		 * This also handles the start in character sheet command option
		 * @return
		 * @throws InterruptedException
		 * @throws InvocationTargetException
		 */
		private boolean maybeLoadOrCreateCharacter() throws InterruptedException, InvocationTargetException
		{
			if (Main.getStartupCharacterFile() == null)
			{
				return false;
			}
			final File file = new File(Main.getStartupCharacterFile());
			final DataSetFacade dataset = currentDataSetRef.getReference();
			if (!file.exists() && dataset == null)
			{
				//TODO: complain about it
				return false;
			}
			if (Main.shouldStartInCharacterSheet())
			{
				String key = UIPropertyContext.C_PROP_INITIAL_TAB;
				key = UIPropertyContext.createFilePropertyKey(file, key);
				UIPropertyContext.getInstance().setInt(key, InfoTabbedPane.CHARACTER_SHEET_TAB);
			}
			SwingUtilities.invokeAndWait(new Runnable()
			{

				@Override
				public void run()
				{
					if (!file.exists())
					{
						createNewCharacter(file);
					}
					else if (dataset == null)
					{
						loadCharacterFromFile(file);
					}
					else
					{
						CharacterManager.openCharacter(file, PCGenFrame.this, dataset);
					}
				}

			});
			return true;
		}

		private boolean maybeStartInNPCGen()
		{
			if (!Main.shouldStartInNPCGen())
			{
				return false;
			}
			//TODO: finish this
			return true;
		}

		private boolean maybeStartInGMGen()
		{
			if (!Main.shouldStartInGMGen())
			{
				return false;
			}
			PCGenUIManager.displayGmGen();
			return true;
		}

	}

	private static InputMap createInputMap(ActionMap actionMap)
	{
		InputMap inputMap = new InputMap();
		for (Object obj : actionMap.keys())
		{
			KeyStroke key = (KeyStroke) actionMap.get(obj).getValue(Action.ACCELERATOR_KEY);
			if (key != null)
			{
				inputMap.put(key, obj);
			}
		}
		return inputMap;
	}

	public PCGenActionMap getActionMap()
	{
		return actionMap;
	}

	public void setSelectedCharacter(CharacterFacade character)
	{
		if (currentCharacterRef.getReference() != null)
		{
			currentCharacterRef.getReference().getFileRef().removeReferenceListener(filenameListener);
		}
		currentCharacterRef.setReference(character);
		updateTitle();
		if (character != null && character.getFileRef() != null)
		{
			character.getFileRef().addReferenceListener(filenameListener);
		}
	}

	/**
	 * @return A reference to the currently loaded sources.
	 */
	public ReferenceFacade<SourceSelectionFacade> getCurrentSourceSelectionRef()
	{
		return currentSourceSelection;
	}

	/**
	 *
	 * @return a reference to the selected character
	 */
	public ReferenceFacade<CharacterFacade> getSelectedCharacterRef()
	{
		return currentCharacterRef;
	}

	/**
	 *
	 * @return a reference to the currently loaded data set
	 */
	public ReferenceFacade<DataSetFacade> getLoadedDataSetRef()
	{
		return currentDataSetRef;
	}

	/**
	 * Unload any currently loaded sources. 
	 */
	public void unloadSources()
	{
		currentSourceSelection.setReference(null);
		Globals.emptyLists();
		PersistenceManager pManager = PersistenceManager.getInstance();
		pManager.clear();
	}
	
	/**
	 * Loads a selection of sources into PCGen asynchronously and
	 * tracks the load progress on the status bar. While sources
	 * are being loaded any calls to this method are ignored until
	 * sources are finished loading.
	 * @param sources a SourceSelectionFacade specifying the sources to load
	 * @return true if the sources are loaded or are loading
	 */
	public boolean loadSourceSelection(SourceSelectionFacade sources)
	{
		if (sources == null)
		{
			return false;
		}
		if (sourceLoader != null && sourceLoader.isAlive())
		{
			return checkSourceEquality(sources, sourceLoader.sources);
		}
		if (checkSourceEquality(sources, currentSourceSelection.getReference()))
		{
			return true;
		}
		//make sure all characters are closed before loading new sources.
		if (closeAllCharacters())
		{
			sourceLoader = new SourceLoadWorker(sources, this);
			sourceLoader.start();
			return true;
		}
		return false;
	}

	private boolean checkSourceEquality(SourceSelectionFacade source1, SourceSelectionFacade source2)
	{
		if (source1 == source2)
		{
			return true;
		}
		if (source1 == null ^ source2 == null)
		{
			return false;
		}
		//we use reference equality since GameModeFacades come from a fixed database
		if (source1.getGameMode().getReference() != source2.getGameMode().getReference())
		{
			return false;
		}
		ListFacade<CampaignFacade> campaigns1 = source1.getCampaigns();
		ListFacade<CampaignFacade> campaigns2 = source2.getCampaigns();
		if (campaigns1.getSize() != campaigns2.getSize())
		{
			return false;
		}
		for (CampaignFacade campaignFacade : campaigns1)
		{
			if (!campaigns2.containsElement(campaignFacade))
			{
				return false;
			}
		}
		return true;
	}

	public boolean saveCharacter(CharacterFacade character)
	{
		File file = character.getFileRef().getReference();
		if (StringUtils.isBlank(file.getName()) || !CharacterManager.saveCharacter(character))
		{
			return showSaveCharacterChooser(character);
		}
		// We must have a file name before we prepare.
		prepareForSave(character, false);
		if (!CharacterManager.saveCharacter(character))
		{
			return showSaveCharacterChooser(character);
		}
		return true;
	}

	/**
	 * Prepare the character for a save. This is primarily concerned with 
	 * ensuring all companions (or masters) have file names before the save is 
	 * done.
	 * @param character The character being saved.
	 */
	private void prepareForSave(CharacterFacade character, boolean savingAll)
	{
		List<CompanionFacade> tobeSaved = new ArrayList<CompanionFacade>(); 
		for (CompanionFacade comp : character.getCompanionSupport().getCompanions())
		{
			if (StringUtils.isEmpty(comp.getFileRef().getReference().getName()))
			{
				tobeSaved.add(comp);
			}
		} 
		if (!tobeSaved.isEmpty())
		{
			if (savingAll
				|| showMessageConfirm(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_unsavedCompanions"))) //$NON-NLS-1$
			{
				for (CompanionFacade companionFacade : tobeSaved)
				{
					CharacterFacade compChar = CharacterManager.getCharacterMatching(companionFacade);
					showSaveCharacterChooser(compChar);
				}
			}
		}
		CharacterStubFacade master = character.getMaster();
		if (master != null
			&& (master.getFileRef().getReference() == null || StringUtils
				.isEmpty(master.getFileRef().getReference().getName())))
		{
			if (savingAll
				|| showMessageConfirm(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_unsavedMaster"))) //$NON-NLS-1$
			{
				CharacterFacade masterChar = CharacterManager.getCharacterMatching(master);
				showSaveCharacterChooser(masterChar);
			}
		}			
	}
	
	
	public void closeCharacter(CharacterFacade character)
	{
		if (character.isDirty())
		{
			int ret =
					JOptionPane.showConfirmDialog(this, LanguageBundle
						.getFormattedString("in_savePcChoice", character //$NON-NLS-1$
							.getNameRef().getReference()),
						Constants.APPLICATION_NAME,
						JOptionPane.YES_NO_CANCEL_OPTION);
			if (ret == JOptionPane.CANCEL_OPTION)
			{
				return;
			}
			if (ret == JOptionPane.YES_OPTION)
			{
				saveCharacter(character);
			}
		}
		CharacterManager.removeCharacter(character);
	}

	public boolean closeAllCharacters()
	{
		ListFacade<CharacterFacade> characters = CharacterManager.getCharacters();
		if (characters.isEmpty())
		{
			return true;
		}
		int ret = 2;

		List<CharacterFacade> characterList = new ArrayList<CharacterFacade>();
		List<CharacterFacade> unsavedPCs = new ArrayList<CharacterFacade>();
		for (CharacterFacade characterFacade : characters)
		{
			if (characterFacade.isDirty())
			{
				unsavedPCs.add(characterFacade);
			}
			else
			{
				characterList.add(characterFacade);
			}
		}
		if (unsavedPCs.size() > 1)
		{
			Object[] options = new Object[]
			{
				LanguageBundle.getString("in_closeOptSaveAll"), //$NON-NLS-1$
				LanguageBundle.getString("in_closeOptSaveNone"), //$NON-NLS-1$
				LanguageBundle.getString("in_closeOptChoose"), //$NON-NLS-1$
				LanguageBundle.getString("in_cancel") //$NON-NLS-1$
			};
			ret = JOptionPane.showOptionDialog(this, LanguageBundle.getString("in_closeOptSaveTitle"), //$NON-NLS-1$
											   Constants.APPLICATION_NAME, JOptionPane.YES_NO_CANCEL_OPTION,
											   JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		}
		if (ret == 3)
		{
			return false;
		}
		if (ret == 1)
		{
			CharacterManager.removeAllCharacters();
			return true;
		}

		for (CharacterFacade character : unsavedPCs)
		{
			int ret2 = JOptionPane.YES_OPTION;

			if (ret == 2)
			{
				ret2 =
						JOptionPane.showConfirmDialog(this, LanguageBundle
							.getFormattedString("in_savePcChoice", character //$NON-NLS-1$
								.getNameRef().getReference()),
							Constants.APPLICATION_NAME,
							JOptionPane.YES_NO_CANCEL_OPTION);
			}

			if (ret2 == JOptionPane.YES_OPTION)
			{//If you get here then the user either selected "Yes to All" or "Yes"
				if (saveCharacter(character))
				{
					characterList.add(character);
				}
			}
			else if (ret2 == JOptionPane.NO_OPTION)
			{
				characterList.add(character);
			}
			else if (ret2 == JOptionPane.CANCEL_OPTION)
			{
				return false;
			}
		}

		for (CharacterFacade character : characterList)
		{
			CharacterManager.removeCharacter(character);
		}
		return characters.isEmpty();
	}

	boolean showSavePartyChooser()
	{
		PartyFacade party = CharacterManager.getCharacters();
		PCGenSettings context = PCGenSettings.getInstance();
		String parentPath = context.getProperty(PCGenSettings.PCP_SAVE_PATH);
		chooser.setCurrentDirectory(new File(parentPath));
		File file = party.getFileRef().getReference();

		chooser.setSelectedFile(file);
		chooser.resetChoosableFileFilters();
		FileFilter filter = new PcpFileFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		int ret = chooser.showSaveDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
		{
			return false;
		}
		file = chooser.getSelectedFile();
		if (!file.getName().endsWith(Constants.EXTENSION_PARTY_FILE))
		{
			file = new File(file.getParent(), file.getName()
					+ Constants.EXTENSION_PARTY_FILE);
		}
		if (file.isDirectory())
		{
			showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getString("in_savePcDirOverwrite")); //$NON-NLS-1$
			return showSavePartyChooser();
		}
		if (file.exists())
		{
			boolean overwrite =
					showWarningConfirm(LanguageBundle.getFormattedString(
						"in_savePcConfirmOverTitle", file.getName()), //$NON-NLS-1$
						LanguageBundle.getFormattedString(
							"in_savePcConfirmOverMsg", file.getName())); //$NON-NLS-1$
			if (!overwrite)
			{
				return showSavePartyChooser();
			}
		}
		party.setFile(file);
		context.setProperty(PCGenSettings.PCP_SAVE_PATH, file.getParent());
		if (!saveAllCharacters())
		{
			showErrorMessage(LanguageBundle.getString("in_savePartyFailTitle"), //$NON-NLS-1$
				LanguageBundle.getString("in_savePartyFailMsg")); //$NON-NLS-1$
			return false;
		}
		if (!CharacterManager.saveCurrentParty())
		{
			return showSavePartyChooser();
		}
		return true;
	}

	boolean saveAllCharacters()
	{
		boolean ok = true;
		for (CharacterFacade character : CharacterManager.getCharacters())
		{
			File file = character.getFileRef().getReference();
			if (file == null || StringUtils.isEmpty(file.getName()))
			{
				ok &= showSaveCharacterChooser(character);
			}
			else
			{
				prepareForSave(character, true);
				ok &= CharacterManager.saveCharacter(character);
			}
		}
		return ok;
	}

	/**
	 * This brings up a file chooser allows the user to select
	 * the location that a character should be saved to.
	 * @param character the character to be saved
	 */
	boolean showSaveCharacterChooser(CharacterFacade character)
	{
		PCGenSettings context = PCGenSettings.getInstance();
		String parentPath = context.getProperty(PCGenSettings.PCG_SAVE_PATH);
		chooser.setCurrentDirectory(new File(parentPath));
		File file = character.getFileRef().getReference();
		File prevFile = file;
		if (file == null || StringUtils.isEmpty(file.getName()))
		{
			file = new File(parentPath, character.getNameRef().getReference()
					+ Constants.EXTENSION_CHARACTER_FILE);
		}
		chooser.setSelectedFile(file);

		chooser.resetChoosableFileFilters();
		FileFilter filter = new PcgFileFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		int ret = chooser.showSaveDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION)
		{
			file = chooser.getSelectedFile();
			if (!file.getName().endsWith(Constants.EXTENSION_CHARACTER_FILE))
			{
				file = new File(file.getParent(), file.getName()
						+ Constants.EXTENSION_CHARACTER_FILE);
			}
			UIDelegate delegate = character.getUIDelegate();
			if (file.isDirectory())
			{
				delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_savePcDirOverwrite")); //$NON-NLS-1$
				return showSaveCharacterChooser(character);
			}

			if (file.exists() && (prevFile == null || !file.getName().equals(
					prevFile.getName())))
			{
				boolean overwrite =
						delegate.showWarningConfirm(LanguageBundle
							.getFormattedString("in_savePcConfirmOverTitle", //$NON-NLS-1$
								file.getName()), LanguageBundle
							.getFormattedString("in_savePcConfirmOverMsg", //$NON-NLS-1$
								file.getName()));

				if (!overwrite)
				{
					return showSaveCharacterChooser(character);
				}
			}

			character.setFile(file);
			prepareForSave(character, false);
			if (!CharacterManager.saveCharacter(character))
			{
				return showSaveCharacterChooser(character);
			}
			return true;
		}
		return false;
	}

	void showOpenCharacterChooser()
	{
		PCGenSettings context = PCGenSettings.getInstance();
		chooser.setCurrentDirectory(new File(context.getProperty(PCGenSettings.PCG_SAVE_PATH)));
		chooser.setSelectedFile(new File("")); //$NON-NLS-1$

		chooser.resetChoosableFileFilters();
		FileFilter filter = new PcgFileFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);

		int ret = chooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			loadCharacterFromFile(file);
		}
	}

	void showOpenPartyChooser()
	{
		PCGenSettings context = PCGenSettings.getInstance();
		chooser.setCurrentDirectory(new File(context.getProperty(PCGenSettings.PCP_SAVE_PATH)));
		chooser.resetChoosableFileFilters();
		chooser.setFileFilter(new PcpFileFilter());

		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			loadPartyFromFile(file);
		}
	}

	public void createNewCharacter()
	{
		createNewCharacter(null);
	}

	/**
	 * creates a new character and sets its file if possible
	 * then sets the character as the currently selected character
	 * @param file the File for this character
	 */
	private void createNewCharacter(File file)
	{
		DataSetFacade data = getLoadedDataSetRef().getReference();
		CharacterFacade character = CharacterManager.createNewCharacter(this, data);
		//This is called before the we set it as the selected character so
		//the InfoTabbedPane can catch any character specific properties when
		//it is first displayed
		if (file != null)
		{
			character.setFile(file);
		}
		//Because CharacterManager adds the new character to the character
		//list before it returns, it is not necessary to update the character
		//tabs since they will catch that event before the call to
		//setSelectedCharacter is called
		setSelectedCharacter(character);
	}

	/**
	 * Loads a character from a file. Any sources that are required for
	 * this character are loaded first, then the character is loaded
	 * from the file and a tab is opened for it.
	 * @param pcgFile a file specifying the character to be loaded
	 */
	public void loadCharacterFromFile(final File pcgFile)
	{
		if (!PCGFile.isPCGenCharacterFile(pcgFile))
		{
			JOptionPane.showMessageDialog(this,
				LanguageBundle.getFormattedString("in_loadPcInvalid", pcgFile), //$NON-NLS-1$
				LanguageBundle.getString("in_loadPcFailTtile"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!pcgFile.canRead())
		{
			JOptionPane.showMessageDialog(this,
				LanguageBundle.getFormattedString("in_loadPcNoRead", pcgFile), //$NON-NLS-1$
				LanguageBundle.getString("in_loadPcFailTtile"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		SourceSelectionFacade sources = CharacterManager.getRequiredSourcesForCharacter(pcgFile, this);
		if (sources == null)
		{
			JOptionPane.showMessageDialog(this, LanguageBundle
				.getFormattedString("in_loadPcNoSources", pcgFile), //$NON-NLS-1$
				LanguageBundle.getString("in_loadPcFailTtile"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
		}
		else if (!sources.getCampaigns().isEmpty())
		{
			if (loadSourceSelection(sources))
			{
				new Thread()
				{

					@Override
					public void run()
					{
						try
						{
							sourceLoader.join();
							SwingUtilities.invokeLater(new Runnable()
							{

								@Override
								public void run()
								{
									CharacterManager.openCharacter(pcgFile, PCGenFrame.this, currentDataSetRef.getReference());
								}

							});
						}
						catch (InterruptedException ex)
						{
							//Do nothing
						}
					}

				}.start();
			}
			else
			{
				JOptionPane.showMessageDialog(this,
					LanguageBundle.getString("in_loadPcIncompatSource"), //$NON-NLS-1$
					LanguageBundle.getString("in_loadPcFailTtile"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			if (showWarningConfirm(Constants.APPLICATION_NAME,
				LanguageBundle.getFormattedString("in_loadPcSourcesLoadQuery", //$NON-NLS-1$
					pcgFile)))
			{
				CharacterManager.openCharacter(pcgFile, PCGenFrame.this,
					currentDataSetRef.getReference());
			}
		}
	}

	public void loadPartyFromFile(final File pcpFile)
	{
		if (!PCGFile.isPCGenPartyFile(pcpFile))
		{
			JOptionPane.showMessageDialog(this, LanguageBundle
				.getFormattedString("in_loadPartyInvalid", pcpFile), //$NON-NLS-1$
				LanguageBundle.getString("in_loadPartyFailTtile"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!pcpFile.canRead())
		{
			JOptionPane.showMessageDialog(this, LanguageBundle
				.getFormattedString("in_loadPartyNoRead", pcpFile), //$NON-NLS-1$
				LanguageBundle.getString("in_loadPartyFailTtile"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		SourceSelectionFacade sources = CharacterManager.getRequiredSourcesForParty(pcpFile, this);
		if (sources == null)
		{
			JOptionPane.showMessageDialog(this, LanguageBundle
				.getFormattedString("in_loadPartyNoSources", pcpFile), //$NON-NLS-1$
				LanguageBundle.getString("in_loadPartyFailTtile"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
		}
		else if (!sources.getCampaigns().isEmpty())
		{
			if (loadSourceSelection(sources))
			{
				new Thread()
				{

					@Override
					public void run()
					{
						try
						{
							sourceLoader.join();
							SwingUtilities.invokeLater(new Runnable()
							{

								@Override
								public void run()
								{
									CharacterManager.openParty(pcpFile, PCGenFrame.this, currentDataSetRef.getReference());
								}

							});
						}
						catch (InterruptedException ex)
						{
							//Do nothing
						}
					}

				}.start();
			}
			else
			{
				JOptionPane.showMessageDialog(this,
					LanguageBundle.getString("in_loadPcIncompatSource"), //$NON-NLS-1$
					LanguageBundle.getString("in_loadPartyFailTtile"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Set the frame's title based on the current character and source/game mode.
	 * @param characterFileName The file name (without path) of the active character 
	 * @param sourceName The name of the source selection.
	 */
	private void updateTitle()
	{
		StringBuilder title = new StringBuilder();
		File characterFile = null;
		String characterFileName = null;
		String sourceName = null;
		if (currentCharacterRef != null && currentCharacterRef.getReference() != null)
		{
			characterFile = currentCharacterRef.getReference().getFileRef().getReference();
			if (characterFile == null || StringUtils.isEmpty(characterFile.getName()))
			{
				characterFileName = LanguageBundle.getString("in_unsaved_char"); //$NON-NLS-1$
			}
			else
			{
				characterFileName = characterFile.getName();
			}
		}
		if (currentSourceSelection.getReference() != null)
		{
			sourceName = currentSourceSelection.getReference().toString();
		}

		if (characterFileName != null && characterFileName.length() > 0)
		{
			title.append(characterFileName);
			title.append(" - ");
		}
		if (sourceName != null && sourceName.length() > 0)
		{
			title.append(sourceName);
			title.append(" - ");
		}
		title.append("PCGen v");
		title.append(PCGenPropBundle.getVersionNumber());
		setTitle(title.toString());
	}

	/**
	 * display the tips of the day dialog to the user
	 */
	public void showTipsOfTheDay()
	{
		TipOfTheDay tips = new TipOfTheDay(this);
		tips.setLocationRelativeTo(this);
		tips.setVisible(true);
	}

	/**
	 * display the source selection dialog to the user
	 */
	public void showSourceSelectionDialog()
	{
		if (sourceSelectionDialog == null)
		{
			sourceSelectionDialog = new SourceSelectionDialog(this);
		}
		sourceSelectionDialog.setLocationRelativeTo(this);
		sourceSelectionDialog.setVisible(true);
	}

//TODO: This should be in a utility class.
	/**
	 * Builds a JPanel containing the supplied message, split at each new
	 * line and an optional checkbox, suitable for use in a showMessageDialog
	 * call. This is generally useful for showing messges which can turned
	 * off either in preferences or when they are displayed.
	 *
	 * @param message The message to be displayed.
	 * @param checkbox The optional checkbox to be added - may be null.
	 * @return JPanel A panel containing the message and the checkbox.
	 */
	public static JPanel buildMessageLabelPanel(String message,
												JCheckBox checkbox)
	{
		JPanel panel = new JPanel();
		JLabel label;
		String part;

		panel.setLayout(new GridBagLayout());

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = cons.gridy = 0;
		cons.gridwidth = GridBagConstraints.REMAINDER;
		cons.gridheight = 1;
		cons.anchor = GridBagConstraints.WEST;
		cons.insets = new Insets(0, 0, 3, 0);
		cons.weightx = 1;
		cons.weighty = 0;
		cons.fill = GridBagConstraints.NONE;

		int start = 0;
		int sepPos = -1;

		do
		{
			sepPos = message.indexOf("\n", start); //$NON-NLS-1$

			if (sepPos >= 0)
			{
				part = message.substring(start, sepPos);
				start = sepPos + 1;
			}
			else
			{
				part = message.substring(start);
				start = -1;
			}

			label = new JLabel(part, SwingConstants.LEADING);

			panel.add(label, cons);
			cons.gridy++;
		}
		while (start >= 0);

		if (checkbox != null)
		{
			label = new JLabel("", SwingConstants.LEADING); //$NON-NLS-1$
			panel.add(label, cons);
			cons.gridy++;
			panel.add(checkbox, cons);
			cons.gridy++;
		}

		return panel;
	}

	@Override
	public Boolean maybeShowWarningConfirm(String title, String message, String checkBoxText, final PropertyContext context, final String contextProp)
	{
		if (!context.getBoolean(contextProp, true))
		{
			return null;
		}
		final JCheckBox checkBox = new JCheckBox(checkBoxText, true);
		checkBox.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				context.setBoolean(contextProp, checkBox.isSelected());
			}

		});
		JPanel panel = buildMessageLabelPanel(message, checkBox);
		int ret = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.YES_NO_OPTION,
												JOptionPane.WARNING_MESSAGE);
		return ret == JOptionPane.YES_OPTION;
	}

	@Override
	public boolean showWarningConfirm(String title, String message)
	{
		int ret = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return ret == JOptionPane.YES_OPTION;
	}

	private boolean showMessageConfirm(String title, String message)
	{
		int ret = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
		return ret == JOptionPane.YES_OPTION;
	}

	@Override
	public void showErrorMessage(String title, String message)
	{
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showInfoMessage(String title, String message)
	{
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void showWarningMessage(String title, String message)
	{
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public boolean showWarningPrompt(String title, String message)
	{
		int ret = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		return ret == JOptionPane.OK_OPTION;
	}

	@Override
	public void showLevelUpInfo(CharacterFacade character, int oldLevel)
	{
		CharacterLevelsFacade levels = character.getCharacterLevelsFacade();
		//level-class-hp gained-hp rolled-skill points-abilities?
		int size = levels.getSize();
		if (size - oldLevel + 1 < 1)
		{
			return;
		}
		Object[][] data = new Object[size - oldLevel + 1][5];
		Map<ClassFacade, MutableInt> classLevelMap = new HashMap<ClassFacade, MutableInt>();
		int gainedTotal = 0;
		int rolledTotal = 0;
		int pointTotal = 0;
		for (int i = oldLevel; i < size; i++)
		{
			CharacterLevelFacade level = levels.getElementAt(i);
			Object[] dataRow = data[i - oldLevel];
			dataRow[0] = i + 1;
			ClassFacade c = levels.getClassTaken(level);
			dataRow[1] = c;
			if (!classLevelMap.containsKey(c))
			{
				classLevelMap.put(c, new MutableInt(0));
			}
			classLevelMap.get(c).increment();
			gainedTotal += (Integer) (dataRow[2] = levels.getHPGained(level));
			rolledTotal += (Integer) (dataRow[3] = levels.getHPRolled(level));
			pointTotal += (Integer) (dataRow[4] = levels.getGainedSkillPoints(level));
		}
		size -= oldLevel;
		data[size][0] = LanguageBundle.getString("in_sumTotal"); //$NON-NLS-1$
		StringBuilder builder = new StringBuilder();
		Iterator<ClassFacade> classes = classLevelMap.keySet().iterator();
		while (classes.hasNext())
		{
			ClassFacade c = classes.next();
			builder.append(c.getAbbrev()).append(' ');
			builder.append('(').append(classLevelMap.get(c)).append(')');
			if (classes.hasNext())
			{
				builder.append(", ");
			}
		}
		data[size][1] = builder;
		data[size][2] = gainedTotal;
		data[size][3] = rolledTotal;
		data[size][4] = pointTotal;
		Object[] columns = new Object[]
		{
			LanguageBundle.getString("in_level"), //$NON-NLS-1$
			LanguageBundle.getString("in_classString"), //$NON-NLS-1$
			LanguageBundle.getString("in_luGainedHp"), //$NON-NLS-1$
			LanguageBundle.getString("in_luRolledHp"), //$NON-NLS-1$
			LanguageBundle.getString("in_luSkillPoints") //$NON-NLS-1$
		};
		DefaultTableModel model = new DefaultTableModel(data, columns)
		{

			@Override
			public Class<?> getColumnClass(int columnIndex)
			{
				switch (columnIndex)
				{
					case 2:
					case 3:
					case 4:
						return Integer.class;
					default:
						return Object.class;
				}
			}

		};
		JTable table = new JTable(model);
		table.setFocusable(false);
		table.setCellSelectionEnabled(false);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.getColumnModel().getColumn(0).setCellRenderer(new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		JTableHeader header = table.getTableHeader();
		header.setReorderingAllowed(false);
		header.setResizingAllowed(false);
		JScrollPane pane = new JScrollPane(table);
		JOptionPane.showMessageDialog(this, pane, LanguageBundle.getString("in_luTitle"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean showGeneralChooser(ChooserFacade chooserFacade)
	{
		ChooserDialog dialog = new ChooserDialog(this, chooserFacade);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		return dialog.isCommitted();
	}

	/*
	 * This thread does all work of loading sources that the user
	 * has selected. After the sources are loaded the thread then
	 * displays the licenses for the data.
	 */
	private class SourceLoadWorker extends Thread
	{

		private final SourceSelectionFacade sources;
		private final SourceFileLoader loader;
		private final SwingWorker<List<LogRecord>> worker;
		private final UIDelegate delegate;

		public SourceLoadWorker(SourceSelectionFacade sources, UIDelegate delegate)
		{
			this.sources = sources;
			this.delegate = delegate;
			loader = new SourceFileLoader(sources, delegate);
			worker = statusBar.createWorker(LanguageBundle.getString("in_taskLoadSources"), loader); //$NON-NLS-1$
		}

		@Override
		public void run()
		{
			worker.start();
			//wait until the worker finish and post any errors that occured
			statusBar.setSourceLoadErrors(worker.get());
			//now that the SourceFileLoader has finished
			//handle licenses and whatnot
			StringBuilder sec15 = new StringBuilder(" ");
			sec15.append(readTextFromFile(ConfigurationSettings.getSystemsDir() + File.separator
					+ "opengaminglicense.10a.txt"));
			sec15.append(loader.getOGL());
			section15 = sec15.toString();
			try
			{
				showLicenses();
			}
			catch (Throwable e)
			{
				Logging.errorPrint("Failed to show licences.", e);
			}

			DataSetFacade data = loader.getDataSetFacade();
			if (data != null)
			{
				currentSourceSelection.setReference(sources);
			}
			else
			{
				currentSourceSelection.setReference(null);
			}
			currentDataSetRef.setReference(data);
			updateTitle();
		}

		private void showLicenses()
		{
			PropertyContext context = PCGenSettings.getInstance();
			if (context.initBoolean(PCGenSettings.OPTION_SHOW_LICENSE, true))
			{
				if (loader.hasOGLCampaign())
				{
					showOGLDialog();
				}
				if (loader.hasLicensedCampaign())
				{
					String licenses = loader.getLicenses();
					if (licenses.trim().length() > 0)
					{
						showLicenseDialog(LanguageBundle.getString("in_specialLicenses"), licenses); //$NON-NLS-1$
					}
					for (String license : loader.getOtherLicenses())
					{
						showLicenseDialog(LanguageBundle.getString("in_specialLicenses"), license); //$NON-NLS-1$
					}

				}
			}
			if (loader.hasMatureCampaign()
					&& context.initBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD, true))
			{
				showMatureDialog(loader.getMatureInfo());
			}
			if (context.initBoolean(PCGenSettings.OPTION_SHOW_SPONSORS_ON_LOAD, true))
			{
				showSponsorsDialog();
			}
		}

	}

	public void showOGLDialog()
	{
		showLicenseDialog(LanguageBundle.getString("in_oglTitle"), section15); //$NON-NLS-1$
	}

	private void showLicenseDialog(String title, String htmlString)
	{
		if (htmlString == null)
		{
			htmlString = LanguageBundle.getString("in_licNoInfo"); //$NON-NLS-1$
		}
		final PropertyContext context = PCGenSettings.getInstance();
		final JDialog aFrame = new JDialog(this, title, true);
		final JButton jClose = new JButton(LanguageBundle.getString("in_close")); //$NON-NLS-1$
		final JPanel jPanel = new JPanel();
		final JCheckBox jCheckBox = new JCheckBox(LanguageBundle.getString("in_licShowOnLoad")); //$NON-NLS-1$
		jPanel.add(jCheckBox);
		jCheckBox.setSelected(context.getBoolean(PCGenSettings.OPTION_SHOW_LICENSE));
		jCheckBox.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				context.setBoolean(PCGenSettings.OPTION_SHOW_LICENSE, jCheckBox.isSelected());
			}

		});
		jPanel.add(jClose);
		jClose.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				aFrame.dispose();
			}

		});
		//IconUtilitities.maybeSetIcon(aFrame, IconUtilitities.RESOURCE_APP_ICON);

		HtmlPanel htmlPanel = new HtmlPanel();
		HtmlRendererContext theRendererContext = new SimpleHtmlRendererContext(htmlPanel, new SimpleUserAgentContext());
		htmlPanel.setHtml(htmlString, "", theRendererContext);

		aFrame.getContentPane().setLayout(new BorderLayout());
		aFrame.getContentPane().add(htmlPanel, BorderLayout.CENTER);
		aFrame.getContentPane().add(jPanel, BorderLayout.SOUTH);
		aFrame.setSize(new Dimension(700, 500));
		aFrame.setLocationRelativeTo(this);
		aFrame.setVisible(true);
	}

	private void showMatureDialog(String text)
	{
		Logging.errorPrint("Warning: The following datasets contains mature themes. User discretion is advised.");
		Logging.errorPrint(text);

		final JDialog aFrame = new JDialog(this, LanguageBundle.getString("in_matureTitle"), true);

		final JPanel jPanel1 = new JPanel();
		final JPanel jPanel3 = new JPanel();
		final JLabel jLabel1 =
				new JLabel(LanguageBundle.getString("in_matureWarningLine1"), //$NON-NLS-1$
					SwingConstants.CENTER);
		final JLabel jLabel2 =
				new JLabel(LanguageBundle.getString("in_matureWarningLine2"), //$NON-NLS-1$
					SwingConstants.CENTER);
		final JCheckBox jCheckBox1 = new JCheckBox(LanguageBundle.getString("in_licShowOnLoad")); //$NON-NLS-1$
		final JButton jClose = new JButton(LanguageBundle.getString("in_close")); //$NON-NLS-1$

		jPanel1.setLayout(new BorderLayout());
		jPanel1.add(jLabel1, BorderLayout.NORTH);
		jPanel1.add(jLabel2, BorderLayout.SOUTH);

		HtmlPanel htmlPanel = new HtmlPanel();
		HtmlRendererContext theRendererContext = new SimpleHtmlRendererContext(htmlPanel, new SimpleUserAgentContext());
		htmlPanel.setHtml(text, "", theRendererContext);

		jPanel3.add(jCheckBox1);
		jPanel3.add(jClose);

		final PropertyContext context = PCGenSettings.getInstance();
		jCheckBox1.setSelected(context.getBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD));

		jClose.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				aFrame.dispose();
			}

		});

		jCheckBox1.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				context.setBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD, jCheckBox1.isSelected());
			}

		});

		aFrame.getContentPane().setLayout(new BorderLayout());
		aFrame.getContentPane().add(jPanel1, BorderLayout.NORTH);
		aFrame.getContentPane().add(htmlPanel, BorderLayout.CENTER);
		aFrame.getContentPane().add(jPanel3, BorderLayout.SOUTH);

		aFrame.setSize(new Dimension(456, 176));
		aFrame.setLocationRelativeTo(this);
		aFrame.setVisible(true);
	}

	public void showSponsorsDialog()
	{
		Collection<Sponsor> sponsors = Globals.getGlobalContext().ref.getConstructedCDOMObjects(Sponsor.class);

		if (sponsors.size() <= 1)
		{
			return;
		}

		String title = LanguageBundle.getString("in_sponsorTitle"); //$NON-NLS-1$

		final JDialog aFrame = new JDialog(this, title, true);
		final JButton jClose = new JButton(LanguageBundle.getString("in_close")); //$NON-NLS-1$
		final JPanel jPanel = new JPanel();
		final JCheckBox jCheckBox = new JCheckBox(LanguageBundle.getString("in_licShowOnLoad")); //$NON-NLS-1$
		jPanel.add(jCheckBox);
		final PropertyContext context = PCGenSettings.getInstance();
		jCheckBox.setSelected(context.getBoolean(PCGenSettings.OPTION_SHOW_SPONSORS_ON_LOAD));
		jCheckBox.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				context.setBoolean(PCGenSettings.OPTION_SHOW_SPONSORS_ON_LOAD, jCheckBox.isSelected());
			}

		});
		jPanel.add(jClose);
		jClose.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				aFrame.dispose();
			}

		});

		StringBuilder sb = new StringBuilder();

		sb.append("<html>");
		for (Sponsor sponsor : sponsors)
		{
			if (!"PCGEN".equals(sponsor.getKeyName()))
			{
				continue;
			}
			sb.append("<img src='").append(sponsor.getBannerImage()).append("'><br>");
		}

		String s = "";
		if (sponsors.size() > 2)
		{
			s = "s";
		}
		sb.append("<H2><CENTER>")
			.append(LanguageBundle.getString("in_sponsorThanks")).append(s) //$NON-NLS-1$
			.append(":</CENTER></h2>");
		int size = 172;
		for (Sponsor sponsor : sponsors)
		{
			if ("PCGEN".equals(sponsor.getKeyName()))
			{
				continue;
			}

			size += 70;
			sb.append("<img src='").append(sponsor.getBannerImage()).append("'><br>");
		}
		sb.append("</html>");

		HtmlPanel htmlPanel = new HtmlPanel();
		HtmlRendererContext theRendererContext = new SimpleHtmlRendererContext(htmlPanel, new SimpleUserAgentContext());
		htmlPanel.setHtml(sb.toString(), "", theRendererContext);

		aFrame.getContentPane().setLayout(new BorderLayout());
		aFrame.getContentPane().add(htmlPanel, BorderLayout.CENTER);
		aFrame.getContentPane().add(jPanel, BorderLayout.SOUTH);
		aFrame.setSize(new Dimension(505, size));
		aFrame.setLocationRelativeTo(this);
		aFrame.setVisible(true);
	}

	public void showAboutDialog()
	{
		new AboutDialog(this).setVisible(true);
	}

	private static String readTextFromFile(String fileName)
	{
		String aString;
		final File aFile = new File(fileName);

		if (!aFile.exists())
		{
			Logging.errorPrint("Could not find license at " + fileName);
			aString = LanguageBundle.getString("in_licNoInfo"); //$NON-NLS-1$

			return aString;
		}

		try
		{
			BufferedReader theReader = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));
			final int length = (int) aFile.length();
			final char[] inputLine = new char[length];
			theReader.read(inputLine, 0, length);
			theReader.close();
			aString = new String(inputLine);
		}
		catch (IOException e)
		{
			Logging.errorPrint("Could not read license at " + fileName, e);
			aString = "No license information found";
		}

		return aString;
	}

	/**
	 * The Class <code>FilenameListener</code> is used to update the frame title each time the 
	 * current character's file name is changed.
	 */
	private class FilenameListener implements ReferenceListener<File>
	{
		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ReferenceListener#referenceChanged(pcgen.core.facade.event.ReferenceEvent)
		 */

		@Override
		public void referenceChanged(ReferenceEvent<File> e)
		{
			PCGenFrame.this.updateTitle();
		}

	}

}
