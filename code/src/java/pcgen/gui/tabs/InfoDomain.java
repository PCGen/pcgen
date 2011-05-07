/*
 * InfoDomain.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 * Modified Nov 14, 2002 by David Hibbs
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import static pcgen.gui.HTMLUtils.BOLD;
import static pcgen.gui.HTMLUtils.BR;
import static pcgen.gui.HTMLUtils.END_BOLD;
import static pcgen.gui.HTMLUtils.END_FONT;
import static pcgen.gui.HTMLUtils.END_HTML;
import static pcgen.gui.HTMLUtils.FONT_PLUS_1;
import static pcgen.gui.HTMLUtils.HTML;
import static pcgen.gui.HTMLUtils.THREE_SPACES;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.SettingsHandler;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.DescriptionFormatting;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.GuiConstants;
import pcgen.gui.LstEditorMain;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.TableColumnManager;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.editor.EditorConstants;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.InfoViewModelBuilder;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TableSorter;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 * This class is responsible for drawing the domain related window - including
 * indicating what deity and domains are available, which ones are selected,
 * and handling the selection/de-selection of both.
 *
 * @author Mario Bonassin
 * @version $Revision$
 *          modified by Bryan McRoberts (merton_monk@yahoo.com) to connect to
 *          pcgen.core package
 *          modified by David Hibbs to use Deity and Domain objects instead of Strings
 *          and to clean up the code
 */
public class InfoDomain extends FilterAdapterPanel implements CharacterInfoTab
{
	static final long serialVersionUID = -4223585346813683966L;

	private static final Tab tab = Tab.DOMAINS;

	private static List<Domain> selectedDomainList = new ArrayList<Domain>();
	private static boolean needsUpdate = true;
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	// if you change these, you also have to change
	// the case statement in the DeityModel declaration
	private static final int COL_NAME = 0;
	private static final int COL_ALIGNMENT = 1;
	private static final int COL_DOMAINS = 2;
	private static final int COL_SOURCE = 3;

	// Note these arrays must be set after we have loaded the values of the
	// properties above.
	private DeityModel deityModel = null;
	private DomainModel domainModel = new DomainModel();
	private FlippingSplitPane aSplit;
	private FlippingSplitPane bSplit;
	private FlippingSplitPane splitPane;
	private JButton deitySelect;
	private JButton domainSelect;
	private JLabel deityName;
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JTextField textDeityQFilter = new JTextField();
	private JButton clearDeityQFilterButton = new JButton(PropertyFactory.getString("in_clear"));
	private static Integer saveDeityViewMode = null;
	private JTextField textDomainQFilter = new JTextField();
	private JButton clearDomainQFilterButton = new JButton(PropertyFactory.getString("in_clear"));

	// author: Thomas Behr 08-02-02
	private JLabel domChosen = new JLabel();
	private JLabel domSelected;
	private JLabel domTotal = new JLabel();
	private JLabel ofLabel;
	private JLabelPane deityInfo = new JLabelPane();
	private JPanel center = new JPanel();
	private JTreeTable deityTable = null;
	private JTableEx domainTable = null;

	// sage_sam updated 11/13/2002 to match a change elsewhere in the code
	private JLabelPane domainInfo = new JLabelPane();
	private JTreeTableSorter deitySorter = null;
	private TableSorter domainSorter = null;
	private boolean hasBeenSized = false;
	private int numDomains = 0;
	private int viewMode = 0;
	private TreePath selPath;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Default constructor for this tab.
	 * @param pc
	 */
	public InfoDomain(PlayerCharacter pc)
	{
		this.pc = pc;
		// we will use the component's name to save
		// component specific settings
		setName(tab.toString());

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});

		FilterFactory.restoreFilterSettings(this);
	}

	public void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Domain.Order", tab
			.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Domain.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();

		if (pc.getDomainCount() < pc.getMaxCharacterDomains())
		{
			toDoList.add(PropertyFactory.getString("in_domTodoDomainsLeft")); //$NON-NLS-1$
		}
		else if (pc.getDomainCount() > pc.getMaxCharacterDomains())
		{
			toDoList.add(PropertyFactory.getString("in_domTodoTooManyDomains")); //$NON-NLS-1$
		}
		return toDoList;
	}

	public void refresh()
	{
		if (pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if (readyForRefresh)
		{
			needsUpdate = true;
			updateCharacterInfo();
		}
		else
		{
			serial = 0;
		}
	}

	public JComponent getView()
	{
		return this;
	}

	/**
	 * specifies whether the "match any" option should be available
	 * @return true
	 */
	@Override
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 */
	@Override
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.MULTI_MULTI_MODE = 2
	 */
	@Override
	public final int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/*
	 * ##########################################################
	 * filter stuff
	 * ##########################################################
	 */

	/**
	 * implementation of Filterable interface
	 */
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllDeityFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public final void refreshFiltering()
	{
		forceRefresh();
	}

	/**
	 * This method displays the descriptive information about the
	 * selected deity.
	 * @param aDeity
	 */
	private void setDeityInfoText(Deity aDeity)
	{
		if (aDeity != null)
		{
			StringBuffer infoText =
					new StringBuffer().append(HTML).append(FONT_PLUS_1).append(BOLD).append(
						OutputNameFormatting.piString(aDeity, false)).append(END_BOLD).append(END_FONT);

			String aString = aDeity.get(StringKey.TITLE);

			if (aString != null)
			{
				infoText.append(THREE_SPACES).append("(").append(aString).append(")");
			}

			infoText.append(BR);
			infoText.append(PropertyFactory.getFormattedString(
				"in_InfoDescription", //$NON-NLS-1$
				DescriptionFormatting.piDescString(pc, aDeity)));

			List<CDOMReference<WeaponProf>> dwp = aDeity.getListFor(
					ListKey.DEITYWEAPON);
			if (dwp != null)
			{
				infoText.append(THREE_SPACES);
				infoText.append(PropertyFactory.getFormattedString(
					"in_deityFavWeap", //$NON-NLS-1$
					ReferenceUtilities.joinLstFormat(dwp, "|")));
			}

			aString = aDeity.get(StringKey.HOLY_ITEM);
			if (aString != null)
			{
				infoText.append(THREE_SPACES);
				infoText.append(PropertyFactory.getFormattedString(
					"in_deityHolyIt", //$NON-NLS-1$
					aString));
			}

			aString = aDeity.get(StringKey.WORSHIPPERS);
			if (aString != null)
			{
				infoText.append(THREE_SPACES);
				infoText.append(PropertyFactory.getFormattedString(
					"in_deityWorshippers", //$NON-NLS-1$
					aString));
			}

			aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			aDeity.getPrerequisiteList(), false);
			if (aString.length() != 0)
			{
				infoText.append(PropertyFactory.getFormattedString(
					"in_InfoRequirements", //$NON-NLS-1$
					aString));
			}

			aString = SourceFormat.getFormattedString(aDeity,
			Globals.getSourceDisplay(), true);
			if (aString.length() > 0)
			{
				infoText.append(PropertyFactory.getFormattedString(
					"in_InfoSource", //$NON-NLS-1$
					aString));
			}

			infoText.append(END_HTML);
			deityInfo.setText(infoText.toString());
		}
		else
		{
			deityInfo.setText(HTML + END_HTML);
		}
	}

	/**
	 * This method displays the descriptive information about the
	 * selected domain.
	 * @param aDomain
	 */
	private void setDomainInfoText(Domain aDomain, List<Prerequisite> prereqs)
	{
		StringBuffer infoText = new StringBuffer().append(HTML);

		if (aDomain != null)
		{
			infoText.append(FONT_PLUS_1).append(BOLD).append(
				OutputNameFormatting.piString(aDomain, false)).append(END_BOLD).append(END_FONT);

			String aString = pc.getDescription(aDomain);
			if (aString.length() != 0)
			{
				infoText.append(BR);
				infoText.append(PropertyFactory.getFormattedString(
					"in_domainGrant", //$NON-NLS-1$
					aString));
			}

			aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			aDomain.getPrerequisiteList(), false);
			if (aString.length() != 0)
			{
				infoText.append(PropertyFactory.getFormattedString(
					"in_InfoRequirements", //$NON-NLS-1$
					aString));
			}
			
			aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, prereqs, false);
			if (aString.length() != 0)
			{
				infoText.append(BR);
				infoText.append(PropertyFactory.getFormattedString(
					"in_domainRequirements", //$NON-NLS-1$
					aString));
			}

			aString = SourceFormat.getFormattedString(aDomain,
			Globals.getSourceDisplay(), true);
			if (aString.length() > 0)
			{
				infoText.append(PropertyFactory.getFormattedString(
					"in_InfoSource", //$NON-NLS-1$
					aString));
			}
			

		}

		infoText.append(END_HTML);
		domainInfo.setText(infoText.toString());
	}

	/**
	 * This method returns all available domains, without filtering.
	 * 
	 * @param pcDeity
	 *            Deity selected for the current character
	 *            
	 * @return availDomainList
	 */
	private final List<SourcedQualObject<Domain>> getUnfilteredDomains(final Deity pcDeity)
	{
		List<SourcedQualObject<Domain>> availDomainList = new ArrayList<SourcedQualObject<Domain>>();
		
		if (pcDeity != null)
		{
			for (CDOMReference<Domain> domains : pcDeity.getSafeListMods(Deity.DOMAINLIST))
			{
				Collection<AssociatedPrereqObject> assoc = pcDeity.getListAssociations(Deity.DOMAINLIST, domains);
				for (AssociatedPrereqObject apo : assoc)
				{
					for (Domain d : domains.getContainedObjects())
					{
						if (!isDomainInList(availDomainList, d))
						{
							availDomainList.add(new SourcedQualObject<Domain>(d, pcDeity,
									apo.getPrerequisiteList()));
						}
					}
				}
			}
		}

		// Loop through the available prestige domains
		for (PCClass aClass : pc.getClassSet())
		{
			/*
			 * Need to do for the class, for compatibility, since level 0 is
			 * loaded into the class itself
			 */
			processDomainList(aClass, availDomainList);
			processAddDomains(aClass, availDomainList);
			for (int lvl = 0; lvl <= pc.getLevel(aClass); lvl++)
			{
				PCClassLevel cl = pc.getActiveClassLevel(aClass, lvl);
				processAddDomains(cl, availDomainList);
				processDomainList(cl, availDomainList);
			}
		}
		return availDomainList;
	}

	private void processAddDomains(CDOMObject cdo,
			final List<SourcedQualObject<Domain>> availDomainList)
	{
		Collection<CDOMReference<Domain>> domains = cdo.getListMods(PCClass.ALLOWED_DOMAINS);
		if (domains != null)
		{
			for (CDOMReference<Domain> ref : domains)
			{
				Collection<AssociatedPrereqObject> assoc = cdo
						.getListAssociations(PCClass.ALLOWED_DOMAINS, ref);
				for (AssociatedPrereqObject apo : assoc)
				{
					for (Domain d : ref.getContainedObjects())
					{
						/*
						 * TODO This gate produces a rather interesting, and
						 * potentially wrong situation. What if two ADDDOMAINS
						 * exist with different PRE? Doesn't this fail?
						 */
						if (!isDomainInList(availDomainList, d))
						{
							availDomainList.add(new SourcedQualObject<Domain>(d, cdo,
									apo.getPrerequisiteList()));
						}
					}
				}
			}
		}
	}

	private void processDomainList(CDOMObject obj,
			final List<SourcedQualObject<Domain>> availDomainList)
	{
		for (QualifiedObject<CDOMSingleRef<Domain>> qo : obj.getSafeListFor(ListKey.DOMAIN))
		{
			CDOMSingleRef<Domain> ref = qo.getRawObject();
			Domain domain = ref.resolvesTo();
			if (!isDomainInList(availDomainList, domain))
			{
				availDomainList.add(new SourcedQualObject<Domain>(domain, obj, qo
						.getPrerequisiteList()));
			}
		}
	}

	/**
	 * Check if a domain is a list of domains, irrespective of prerequisites.
	 *  
	 * @param qualDomainList The list of domains with their prerequisites.
	 * @param qualDomain The domain to search for.
	 * @return tue if the domain is in the list 
	 */
	private boolean isDomainInList(
		List<SourcedQualObject<Domain>> qualDomainList,
		Domain domain)
	{
		for (QualifiedObject<Domain> row : qualDomainList)
		{
			if (domain.equals(row.getRawObject()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * executed when the component is shown
	 */
	private void formComponentShown()
	{
		int width;

		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(PropertyFactory
			.getString("in_statusBarDeity"));
		refresh();

		int splitPaneDividerLocation = splitPane.getDividerLocation();
		int bSplitDividerLocation = bSplit.getDividerLocation();
		int aSplitDividerLocation = aSplit.getDividerLocation();

		if (!hasBeenSized)
		{
			hasBeenSized = true;

			final double thisWidth = this.getSize().getWidth();
			splitPaneDividerLocation =
					SettingsHandler.getPCGenOption("InfoDomain.splitPane",
						(int) ((thisWidth * 4) / 10));
			bSplitDividerLocation =
					SettingsHandler.getPCGenOption("InfoDomain.bSplit",
						(int) ((this.getSize().getHeight() * 75) / 100));
			aSplitDividerLocation =
					SettingsHandler.getPCGenOption("InfoDomain.aSplit",
						(int) ((thisWidth * 5) / 10));

			// set the prefered width on deityTable
			for (int i = 0; i < deityTable.getColumnCount(); i++)
			{
				TableColumn sCol = deityTable.getColumnModel().getColumn(i);
				width =
						Globals.getCustColumnWidth(PropertyFactory
							.getString("in_deity"), i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					deityTable, PropertyFactory.getString("in_deity"), i));
			}

			// set the prefered width on domainTable
			for (int i = 0; i < domainTable.getColumnCount(); i++)
			{
				TableColumn sCol = domainTable.getColumnModel().getColumn(i);
				width =
						Globals.getCustColumnWidth(PropertyFactory
							.getString("in_domains"), i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					domainTable, PropertyFactory.getString("in_domains"), i));
			}
		}

		if (splitPaneDividerLocation > 0)
		{
			splitPane.setDividerLocation(splitPaneDividerLocation);
			SettingsHandler.setPCGenOption("InfoDomain.splitPane",
				splitPaneDividerLocation);
		}

		if (bSplitDividerLocation > 0)
		{
			bSplit.setDividerLocation(bSplitDividerLocation);
			SettingsHandler.setPCGenOption("InfoDomain.bSplit",
				bSplitDividerLocation);
		}

		if (aSplitDividerLocation > 0)
		{
			aSplit.setDividerLocation(aSplitDividerLocation);
			SettingsHandler.setPCGenOption("InfoDomain.aSplit",
				aSplitDividerLocation);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new DeityPopupListener(treeTable,
			new DeityPopupMenu()));
	}

	private void hookupPopupMenu(JTableEx treeTable)
	{
		treeTable.addMouseListener(new DomainPopupListener(treeTable,
			new DomainPopupMenu(treeTable)));
	}

	private final void createTreeTables()
	{
		deityTable = new JTreeTable(deityModel);
		deityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JTree atree = deityTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());

		deityTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						final int idx = getSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						final Object temp =
								atree.getPathForRow(idx).getLastPathComponent();

						if (temp == null)
						{
							return;
						}

						PObjectNode fNode = (PObjectNode) temp;

						if (fNode.getItem() instanceof Deity)
						{
							Deity aDeity = (Deity) fNode.getItem();
							setDeityInfoText(aDeity);
						}
					}
				}
			});

		MouseListener aml = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				final int avaRow = atree.getRowForLocation(e.getX(), e.getY());
				final TreePath avaPath =
						atree.getPathForLocation(e.getX(), e.getY());

				if (avaRow != -1)
				{
					if ((e.getClickCount() == 1) && (avaPath != null))
					{
						atree.setSelectionPath(avaPath);
					}
					else if (e.getClickCount() == 2)
					{
						selButton();
					}
				}
			}
		};

		atree.addMouseListener(aml);

		// create the rightclick popup menus
		hookupPopupMenu(deityTable);
	}

	private static int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model =
				(DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	/**
	 * This method builds the GUI components.
	 */
	private void initComponents()
	{
		readyForRefresh = true;

		final int iView = SettingsHandler.getDomainTab_ListMode();

		if ((iView >= GuiConstants.INFODOMAIN_VIEW_NAME)
			&& (iView <= GuiConstants.INFODOMAIN_VIEW_SOURCE))
		{
			viewMode = iView;
		}
		SettingsHandler.setDomainTab_ListMode(viewMode);

		viewComboBox.addItem(PropertyFactory.getString("in_nameLabel") + "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_alignmentName")
			+ "   ");
		viewComboBox
			.addItem(PropertyFactory.getString("in_domainName") + "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_pantheonName")
			+ "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_sourceName") + " ");
		viewComboBox.setSelectedIndex(viewMode);

		// initialize the deityModel
		deityModel = new DeityModel(viewMode);

		// create the tree's from the deityModel
		createTreeTables();

		// Set the tab description
		Utility
			.setDescription(this, PropertyFactory.getString("in_tabToolTip"));

		// Deity table tooltip
		Utility.setDescription(deityTable, PropertyFactory
			.getString("in_deityTableTip"));

		// Domain table Setup
		domainSorter = new TableSorter(domainModel);
		domainTable = new JTableEx(domainSorter);
		domainSorter.addMouseListenerToHeaderInTable(domainTable);
		domainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Domain table tooltip
		Utility.setDescription(domainTable, PropertyFactory
			.getString("in_domainTableTip"));

		// Domain table mouse listener
		final DomainMouseAdapter domainMouse = new DomainMouseAdapter();
		domainTable.addMouseListener(domainMouse);

		center.setLayout(new BorderLayout());

		buildTopPane();

		JPanel bLeftPane = new JPanel(new BorderLayout());
		JPanel bRightPane = new JPanel(new BorderLayout());

		TitledBorder title1 =
				BorderFactory.createTitledBorder(PropertyFactory
					.getString("in_deityInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		//deityInfo.setBackground(rightPane.getBackground());
		JScrollPane deityScroll = new JScrollPane(deityInfo);
		deityScroll.setBorder(title1);
		bLeftPane.add(deityScroll, BorderLayout.CENTER);
		deityInfo.setBackground(bLeftPane.getBackground());
		Utility.setDescription(bLeftPane, PropertyFactory
			.getString("in_infoScrollTip"));

		TitledBorder title2 =
				BorderFactory.createTitledBorder(PropertyFactory
					.getString("in_domainInfo"));
		title2.setTitleJustification(TitledBorder.CENTER);
		JScrollPane domainScroll = new JScrollPane(domainInfo);
		domainScroll.setBorder(title2);
		bRightPane.add(domainScroll, BorderLayout.CENTER);
		domainInfo.setBackground(bRightPane.getBackground());
		Utility.setDescription(bRightPane, PropertyFactory
			.getString("in_infoScrollTip"));

		aSplit =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane,
					bRightPane);
		aSplit.setOneTouchExpandable(true);
		aSplit.setDividerSize(10);
		aSplit.setDividerLocation(300);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(aSplit, BorderLayout.CENTER);
		bSplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, center,
					botPane);
		bSplit.setOneTouchExpandable(true);
		bSplit.setDividerSize(10);
		bSplit.setDividerLocation(300);

		this.setLayout(new BorderLayout());
		this.add(bSplit, BorderLayout.CENTER);

		// Make sure it updates when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});

		hookupPopupMenu(deityTable);
		hookupPopupMenu(domainTable);
	}

	private void buildTopPane()
	{
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(new BorderLayout());
		splitPane =
				new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(350);
		center.add(splitPane, BorderLayout.CENTER);

		leftPane.add(InfoTabUtils.createFilterPane(new JLabel(PropertyFactory
			.getString("in_irSortDeities")), viewComboBox,
			new JLabel(PropertyFactory.getString("InfoTabs.FilterLabel")), textDeityQFilter, clearDeityQFilterButton),
			BorderLayout.NORTH);

		JScrollPane scrollPane =
				new JScrollPane(deityTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText(PropertyFactory.getString("in_caretSymbol"));
		new TableColumnManager(deityTable, columnButton, deityModel);

		leftPane.add(scrollPane);

		JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		leftBottom
			.add(new JLabel(PropertyFactory.getString("in_deity") + ": "));
		deityName = new JLabel(PropertyFactory.getString("in_nameLabel"));
		leftBottom.add(deityName);
		deitySelect = new JButton(PropertyFactory.getString("in_select"));
		Utility.setDescription(deitySelect, PropertyFactory
			.getString("in_deityButTip"));
		leftBottom.add(deitySelect);
		leftPane.add(leftBottom, BorderLayout.SOUTH);

		rightPane.setLayout(new BorderLayout());

		rightPane.add(InfoTabUtils.createFilterPane(null, null, new JLabel(
			PropertyFactory.getString("InfoTabs.FilterLabel")), textDomainQFilter, clearDomainQFilterButton),
			BorderLayout.NORTH);

		JPanel rightBottom =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		domSelected =
				new JLabel(PropertyFactory.getString("in_domainSelected")
					+ ": ");
		ofLabel = new JLabel(PropertyFactory.getString("in_ofString"));
		rightBottom.add(domSelected);
		rightBottom.add(domChosen);
		rightBottom.add(ofLabel);
		rightBottom.add(domTotal);
		domainSelect = new JButton(PropertyFactory.getString("in_select"));
		Utility.setDescription(domainSelect, PropertyFactory
			.getString("in_domainButTip"));
		domainSelect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final ListSelectionModel lsm = domainTable.getSelectionModel();
				final int selectedRow =
						domainSorter.getRowTranslated(lsm
							.getMinSelectionIndex());
				selectDomainIndex(selectedRow);
			}
		});
		rightBottom.add(domainSelect);
		rightPane.add(rightBottom, BorderLayout.SOUTH);

		JScrollPane scrollPane2 =
				new JScrollPane(domainTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton columnButton2 = new JButton();
		scrollPane2.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText(PropertyFactory.getString("in_caretSymbol"));
		new TableColumnManager(domainTable, columnButton2, domainModel);
		rightPane.add(scrollPane2);
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed();
			}
		});
		deitySelect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				selButton();
			}
		});

		FilterFactory.restoreFilterSettings(this);

		textDeityQFilter.getDocument().addDocumentListener(
			new DocumentListener()
			{
				public void changedUpdate(DocumentEvent evt)
				{
					setDeityQFilter();
				}

				public void insertUpdate(DocumentEvent evt)
				{
					setDeityQFilter();
				}

				public void removeUpdate(DocumentEvent evt)
				{
					setDeityQFilter();
				}
			});
		clearDeityQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearDeityQFilter();
			}
		});

		textDomainQFilter.getDocument().addDocumentListener(
			new DocumentListener()
			{
				public void changedUpdate(DocumentEvent evt)
				{
					setDomainQFilter();
				}

				public void insertUpdate(DocumentEvent evt)
				{
					setDomainQFilter();
				}

				public void removeUpdate(DocumentEvent evt)
				{
					setDomainQFilter();
				}
			});
		clearDomainQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearDomainQFilter();
			}
		});

		splitPane.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (hasBeenSized)
					{
						int s = splitPane.getDividerLocation();
						if (s > 0)
						{
							SettingsHandler.setPCGenOption(
								"InfoDomain.splitPane", s);
						}
					}
				}
			});
		bSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (hasBeenSized)
					{
						int s = bSplit.getDividerLocation();
						if (s > 0)
						{
							SettingsHandler.setPCGenOption("InfoDomain.bSplit",
								s);
						}
					}
				}
			});
		aSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (hasBeenSized)
					{
						int s = aSplit.getDividerLocation();
						if (s > 0)
						{
							SettingsHandler.setPCGenOption("InfoDomain.aSplit",
								s);
						}
					}
				}
			});
		
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setDomainTab_ListMode(viewMode);
			createModel();
			deityTable.updateUI();
		}
	}

	/**
	 * creates the DeityModel that will be used
	 **/
	private final void createModel()
	{
		if (deityModel == null)
		{
			deityModel = new DeityModel(viewMode);
		}
		else
		{
			deityModel.resetModel(viewMode);
		}

		if (deitySorter != null)
		{
			deitySorter.setRoot((PObjectNode) deityModel.getRoot());
			deitySorter.sortNodeOnColumn();
		}
	}

	/**
	 * This method is called when a deity is selected from the list of displayed
	 * deities.
	 */
	private void selButton()
	{
		TreePath aPath = deityTable.getTree().getSelectionPath();

		if (aPath == null)
		{
			return;
		}

		Object endComp = aPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof Deity))
		{
			return;
		}

		Deity aDeity = (Deity) fNode.getItem();

		// Don't do anything if the same deity was selected
		if ((pc.getDeity() != null) && (aDeity.equals(pc.getDeity())))
		{
			return;
		}

		if (!pc.canSelectDeity(aDeity))
		{
			final ListSelectionModel lsm = deityTable.getSelectionModel();
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getFormattedString("in_reqMess",
				aDeity.getDisplayName()), Constants.APPLICATION_NAME,
				MessageType.INFORMATION);
			lsm.clearSelection();

			return;
		}

		List<SourcedQualObject<Domain>> potentialDomains = getUnfilteredDomains(aDeity);

		// Validate that no domains will be lost when changing deities
		boolean allDomainsAvailable = true;

		for (Domain domain : selectedDomainList)
		{
			if (!isDomainInList(potentialDomains, domain))
			{
				allDomainsAvailable = false;

				break;
			}
		}

		if (!allDomainsAvailable)
		{
			final int areYouSure =
					JOptionPane.showConfirmDialog(null, PropertyFactory
						.getFormattedString("in_confDomLost1",
						aDeity.getDisplayName())
						+ System.getProperty("line.separator")
						+ PropertyFactory.getString("in_confDomLost2"),
						Constants.APPLICATION_NAME, JOptionPane.OK_CANCEL_OPTION);

			if (areYouSure != JOptionPane.OK_OPTION)
			{
				return;
			}
		}

		pc.setDeity(aDeity);
		deityName.setText(OutputNameFormatting.piString(aDeity, true));

		buildDomainLists();

		////		deityModel.fireTableDataChanged();
	}

	/**
	 * <code>updateCharacterInfo</code> update data for a changed PC
	 */
	private final void updateCharacterInfo()
	{
		if ((pc != null) && (numDomains != pc.getDomainCount()))
		{
			needsUpdate = true;
			numDomains = pc.getDomainCount();
		}

		if (needsUpdate || (pc == null))
		{
			if (pc == null)
			{
				return;
			}

			// Update the list of deities
			createModel();
			deityTable.updateUI();

			// Set the displayed deity name
			if (pc.getDeity() != null)
			{
				deityName.setText(OutputNameFormatting.piString(pc.getDeity(), true));
			}
			else
			{
				deityName.setText(PropertyFactory.getString("in_tempName"));
			}

			// Display the deity description
			setDeityInfoText(pc.getDeity());

			// Build the domain lists
			buildDomainLists();

			needsUpdate = false;
		}
	}

	/**
	 * This method builds the lists of domains
	 * The lists built by this method include the list of available
	 * domains and the list of domains currently selected for the PC
	 */
	private void buildDomainLists()
	{
		// Init the lists
		selectedDomainList.clear();

		// Get all available domains and filter them
		List<SourcedQualObject<Domain>> availDomainList = getUnfilteredDomains(pc.getDeity());
		domainModel.setAvailDomainList(availDomainList);

		// Loop through the character's selected domains
		for (Domain d : pc.getDomainSet())
		{
			boolean found = false;
			for (SourcedQualObject<Domain> availDomain : availDomainList)
			{
				found = availDomain.getRawObject().getKeyName().equals(
						d.getKeyName());
				if (found)
				{
					break;
				}

			}
			if (!found)
			{
				selectedDomainList.remove(d);
				pc.removeDomain(d);
			}

			else if (!selectedDomainList.contains(d))
			{
				selectedDomainList.add(d);
			}
		}

		// Filter the available domains

		for (Iterator<SourcedQualObject<Domain>> domainIter = availDomainList.iterator(); domainIter
			.hasNext();)
		{
			SourcedQualObject<Domain> qualDomain = domainIter.next();
			Domain domain = qualDomain.getObject(pc);

			if (domain != null && !accept(pc, domain) && !selectedDomainList.contains(domain))
			{
				domainIter.remove();
			}
		}

		// Update the display of available/selected domain counts
		domTotal.setText(Integer.toString(pc.getMaxCharacterDomains()));

		// use star (*) to identify which are chosen in the table
		domChosen.setText(Integer.toString(pc.getDomainCount()) + "*");

		domainModel.resetModel();

		// Notify the table and sorter that the table data has changed
		domainSorter.tableChanged(null);
		domainModel.fireTableDataChanged();
	}

	/**
	 * This method is called when a domain is selected
	 * from the list of displayed domains
	 *
	 * @param selectedRow int row in the domain table model for domain
	 */
	private final void selectDomainIndex(int selectedRow)
	{
		if (selectedRow < 0 || selectedRow >= domainModel.getRowCount())
		{
			return;
		}

		if (pc.getMaxCharacterDomains() <= 0)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_errorNotAllowedToChooseADomain"), Constants.APPLICATION_NAME,
				MessageType.INFORMATION);

			return;
		}

		final SourcedQualObject<Domain> qualDomain =
			(SourcedQualObject<Domain>) domainModel.getValueAt(selectedRow, -1);
		final Domain addedDomain = qualDomain.getRawObject();

		if (addedDomain == null)
		{
			return;
		}

		// Make sure a valid domain was selected
		if (!addedDomain.qualifies(pc, addedDomain) || !qualDomain.qualifies(pc))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getFormattedString("in_qualifyMess",
				addedDomain.getDisplayName()), Constants.APPLICATION_NAME,
				MessageType.INFORMATION);

			return;
		}

		// If adding a domain already selected, remove the domain
		if (pc.hasDomain(addedDomain))
		{
			selectedDomainList.remove(addedDomain);
			pc.removeDomain(addedDomain);
		}
		else
		{
			// Check selected domains vs Max number allowed
			if (pc.getDomainCount() >= pc.getMaxCharacterDomains())
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_errorNoMoreDomains"),
						Constants.APPLICATION_NAME, MessageType.INFORMATION);
				
				return;
			}
			
			// space remains for another domain, so add it
			pc.addDomain(addedDomain);
			DomainApplication.applyDomain(pc, addedDomain);
			
			if (!selectedDomainList.contains(addedDomain))
			{
				selectedDomainList.add(addedDomain);
			}
		}

		pc.calcActiveBonuses();

		// Update the displayed domain count,
		// using star (*) to indicate selected domains
		int domCount = pc.getDomainCount();
		domChosen.setText(Integer.toString(domCount) + (domCount == 0 ? "" : "*"));

		domainSorter.tableChanged(null);
		domainModel.fireTableDataChanged();
		forceUpdates();
	}

	private static void createDeityButtonClick()
	{
		//new DeityEditorMain(Globals.getRootFrame(), null).show();
		LstEditorMain lem = new LstEditorMain();
		lem.setVisible(true);
		lem.editIt(null, EditorConstants.EDIT_DEITY);
	}

	private void forceUpdates()
	{
		pc.setDirty(true);
		needsUpdate = true;

		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoAbilities());
		pane.refresh();
	}

	private void deleteDeityButtonClick()
	{
		TreePath aPath = deityTable.getTree().getSelectionPath();

		if (aPath == null)
		{
			return;
		}

		Object endComp = aPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof Deity))
		{
			return;
		}

		Deity aDeity = (Deity) fNode.getItem();

		if (aDeity != null)
		{
			if (aDeity.isType(Constants.s_CUSTOM))
			{
				final int areYouSure =
						JOptionPane.showConfirmDialog(null, PropertyFactory
							.getFormattedString("in_delDeity2",
							aDeity.getDisplayName()),
							Constants.APPLICATION_NAME, JOptionPane.OK_CANCEL_OPTION);

				if (areYouSure != JOptionPane.OK_OPTION)
				{
					return;
				}

				Globals.getContext().ref.forget(aDeity);
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory
					.getString("in_domIDEr4"), Constants.APPLICATION_NAME,
					MessageType.ERROR);
			}
		}
	}

	private void editDeityButtonClick()
	{
		TreePath aPath = deityTable.getTree().getSelectionPath();

		if (aPath == null)
		{
			return;
		}

		Object endComp = aPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof Deity))
		{
			return;
		}

		Deity aDeity = (Deity) fNode.getItem();

		LstEditorMain lem = new LstEditorMain();
		lem.setVisible(true);
		lem.editIt(aDeity, EditorConstants.EDIT_DEITY);
	}

	private void clearDeityQFilter()
	{
		deityModel.clearQFilter();
		if (saveDeityViewMode != null)
		{
			viewMode = saveDeityViewMode.intValue();
			saveDeityViewMode = null;
		}
		deityModel.resetModel(viewMode);
		clearDeityQFilterButton.setEnabled(false);
		deityModel.setQFilter(null);
		textDeityQFilter.setText(null);
		viewComboBox.setEnabled(true);
		forceRefresh();
	}

	private void setDeityQFilter()
	{
		String filterStr = textDeityQFilter.getText();

		if (filterStr.length() == 0)
		{
			clearDeityQFilter();
			return;
		}
		deityModel.setQFilter(filterStr);

		if (saveDeityViewMode == null)
		{
			saveDeityViewMode = Integer.valueOf(viewMode);
		}
		viewMode = GuiConstants.INFODOMAIN_VIEW_NAME;
		clearDeityQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		forceRefresh();
	}

	private void clearDomainQFilter()
	{
		domainModel.clearQFilter();
		domainModel.resetModel();
		clearDomainQFilterButton.setEnabled(false);
		domainModel.setQFilter(null);
		textDomainQFilter.setText(null);
		forceRefresh();
	}

	private void setDomainQFilter()
	{
		String filterStr = textDomainQFilter.getText();

		if (filterStr.length() == 0)
		{
			clearDomainQFilter();
			return;
		}
		domainModel.setQFilter(filterStr);
		domainModel.resetModel();
		clearDomainQFilterButton.setEnabled(true);
		forceRefresh();
	}

	private class DeityPopupListener extends MouseAdapter
	{
		private JTree tree;
		private DeityPopupMenu menu;

		DeityPopupListener(JTreeTable treeTable, DeityPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;

			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke =
								KeyStroke.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final Component menuComponent =
									menu.getComponent(i);

							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks =
										((JMenuItem) menuComponent)
											.getAccelerator();

								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									((JMenuItem) menuComponent).doClick(2);

									return;
								}
							}
						}
					}

					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};

			treeTable.addKeyListener(myKeyListener);
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		@Override
		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				selPath =
						tree.getClosestPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private class DeityPopupMenu extends JPopupMenu
	{
		DeityPopupMenu()
		{
			DeityPopupMenu.this.add(createAddMenuItem(PropertyFactory
				.getString("in_select"), "shortcut EQUALS"));
			this.addSeparator();
			DeityPopupMenu.this.add(createEditMenuItem(PropertyFactory
				.getString("in_editDeity"), "alt E"));
			DeityPopupMenu.this.add(createCreateMenuItem(PropertyFactory
				.getString("in_createDeity"), "alt C"));
			DeityPopupMenu.this.add(createDeleteMenuItem(PropertyFactory
				.getString("in_delDeity"), "DELETE"));
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddDeityActionListener(),
				PropertyFactory.getString("in_select"), '\0', accelerator,
				PropertyFactory.getString("in_irSelDeityTip"), "Add16.gif",
				true);
		}

		private JMenuItem createEditMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new EditDeityActionListener(),
				PropertyFactory.getString("in_editDeity"), '\0', accelerator,
				null, null, true);
		}

		private JMenuItem createCreateMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new CreateDeityActionListener(), PropertyFactory
					.getString("in_createDeity"), '\0', accelerator, null,
				null, true);
		}

		private JMenuItem createDeleteMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new DeleteDeityActionListener(), PropertyFactory
					.getString("in_delDeity"), '\0', accelerator, null, null,
				true);
		}

		private class AddDeityActionListener extends DeityActionListener
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				selButton();
			}
		}

		private class EditDeityActionListener extends DeityActionListener
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				editDeityButtonClick();
			}
		}

		private class CreateDeityActionListener extends DeityActionListener
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				createDeityButtonClick();
			}
		}

		private class DeleteDeityActionListener extends DeityActionListener
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				deleteDeityButtonClick();
			}
		}

		private class DeityActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}
	}

	/**
	 * This class is a listener for the pop-up menus on the domain and
	 * deity tables.
	 */
	private final class DomainPopupListener extends MouseAdapter
	{
		private DomainPopupMenu menu;
		private JTableEx aTable;

		DomainPopupListener(JTableEx treeTable, DomainPopupMenu aMenu)
		{
			menu = aMenu;
			aTable = treeTable;

			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke =
								KeyStroke.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final Component menuComponent =
									menu.getComponent(i);

							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks =
										((JMenuItem) menuComponent)
											.getAccelerator();

								if ((ks != null) && keyStroke.equals(ks))
								{
									((JMenuItem) menuComponent).doClick(2);

									return;
								}
							}
						}
					}

					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};

			treeTable.addKeyListener(myKeyListener);
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		@Override
		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				int selRow = aTable.getSelectedRow();

				if (selRow == -1)
				{
					return;
				}

				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	/**
	 * This class is used as a pop-up menu for the domain and deity tables.
	 */
	private final class DomainPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = -4223585346813683966L;

		DomainPopupMenu(JTableEx treeTable)
		{
			/*
			 * jikes says:
			 *   "Ambiguous reference to member 'add' inherited from
			 *    type 'javax/swing/JPopupMenu' but also declared or
			 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
			 *    Explicit qualification is required."
			 * Well, let's do what jikes wants us to do ;-)
			 *
			 * author: Thomas Behr 08-02-02
			 *
			 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
			 * get "control PLUS" to function on standard US keyboard with Windows 98
			 */
			DomainPopupMenu.this.add(createRemoveMenuItem(PropertyFactory
				.getString("in_select"), "shortcut EQUALS"));
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new RemoveClassActionListener(), PropertyFactory
					.getString("in_select"), (char) 0, accelerator,
				PropertyFactory.getString("in_selDomain"), "Add16.gif", true);
		}

		private class ClassActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}

		private class RemoveClassActionListener extends ClassActionListener
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				InfoDomain.this.domainSelect.doClick();
			}
		}
	}

	/**
	 * This is the Model that populates the table for Deities
	 */
	private final class DeityModel extends AbstractTreeTableModel implements
			TableColumnManagerModel
	{
		// this is the root node
		private PObjectNode deityRoot;

		// list of column names
		private final String[] deityNameList =
				{PropertyFactory.getString("in_nameLabel"),
					PropertyFactory.getString("in_alignLabel"),
					PropertyFactory.getString("in_domains"),
					PropertyFactory.getString("in_sourceLabel")};
		private final int[] deityColList = {200, 100, 100, 100};

		private List<Boolean> displayList = null;

		private DeityModel(int mode)
		{
			super(null);
			resetModel(mode);
			displayList = new ArrayList<Boolean>();
			int i = 1;
			displayList.add(Boolean.TRUE);
			displayList.add(Boolean.valueOf(getColumnViewOption(
				deityNameList[i++], true)));
			displayList.add(Boolean.valueOf(getColumnViewOption(
				deityNameList[i++], true)));
			displayList.add(Boolean.valueOf(getColumnViewOption(
				deityNameList[i++], true)));
		}

		/**
		 * return the Class for a column
		 * @param column
		 * @return Class
		 **/
		@Override
		public Class<?> getColumnClass(int column)
		{
			if (column == COL_NAME)
			{
				return TreeTableModel.class;
			}
			return String.class;
		}

		/**
		 * the number of columns
		 * @return column count
		 **/
		public int getColumnCount()
		{
			return deityNameList.length;
		}

		/**
		 * the name of each column (for the headers)
		 * @param column
		 * @return column name
		 **/
		public String getColumnName(int column)
		{
			return deityNameList[column];
		}

		/**
		 * return the root node
		 * @return root
		 **/
		@Override
		public Object getRoot()
		{
			return super.getRoot();
		}

		/**
		 * return the value of a column
		 * @param node
		 * @param column
		 * @return value
		 **/
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode) node;

			if (fn == null)
			{
				Logging
                                        .errorPrintLocalised("Errors.TreeTableModel.NoActiveNode",this.getClass().toString());
				return null;
			}

			switch (column)
			{
				case COL_NAME:
					return getColumnName(fn);

				case COL_ALIGNMENT:
					return getColumnAlignment(fn);

				case COL_DOMAINS:
					return getColumnDomainList(fn);

				case COL_SOURCE:
					return getColumnSource(fn);

				default:
					Logging
						.errorPrint(PropertyFactory.getFormattedString("in_domInTheColumn",
							String.valueOf(column)));
					break;
			}
			return null;
		}

		private Object getColumnName(PObjectNode fn)
		{
			return fn.toString();
		}

		private Object getColumnAlignment(PObjectNode fn)
		{
			if (fn.getItem() instanceof Deity)
			{
				Deity aDeity = (Deity) fn.getItem();
				PCAlignment al = aDeity.get(ObjectKey.ALIGNMENT);
				return al == null ? "" : al.getAbb();
			}
			return null;
		}

		private Object getColumnDomainList(PObjectNode fn)
		{
			if (fn.getItem() instanceof Deity)
			{
				return getDomainListPIString((Deity) fn.getItem());
			}
			return null;
		}
		
		/**
		 * @return a comma-separated string of the PI-formatted domains this
		 *         deity has
		 */
		public String getDomainListPIString(Deity aDeity)
		{
			Set<String> set = new TreeSet<String>();
			for (CDOMReference<Domain> ref : aDeity.getSafeListMods(Deity.DOMAINLIST))
			{
				for (Domain d : ref.getContainedObjects())
				{
					set.add(OutputNameFormatting.piString(d, false));
				}
			}
			final StringBuffer piString = new StringBuffer(100);
			piString.append("<html>");
			piString.append(StringUtil.joinToStringBuffer(set, ","));
			piString.append("</html>");
			return piString.toString();
		}

		private Object getColumnSource(PObjectNode fn)
		{
			if (fn.getItem() instanceof Deity)
			{
				Deity aDeity = (Deity) fn.getItem();
				return SourceFormat.getFormattedString(aDeity,
				Globals.getSourceDisplay(), true);
			}
			return null;
		}

		/**
		 * There must be a root node, but we keep it hidden
		 * @param aNode
		 **/
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		private void resetModel(int mode)
		{
			// set the root node
			deityRoot = new PObjectNode();
			setRoot(deityRoot);

			switch (mode)
			{
				// deities by name
				case GuiConstants.INFODOMAIN_VIEW_NAME:
					setRoot(InfoViewModelBuilder.buildNameView(InfoDomain.this,
						pc,
						Globals.getContext().ref.getConstructedCDOMObjects(Deity.class),
						getQFilter()));
					break; // end VIEW_NAME

				case GuiConstants.INFODOMAIN_VIEW_ALIGNMENT:
					setRoot(InfoViewModelBuilder.buildAlignmentView(InfoDomain.this,
						pc,
						Globals.getContext().ref.getConstructedCDOMObjects(Deity.class)));
					break; // end VIEW_ALIGNMENT

				case GuiConstants.INFODOMAIN_VIEW_DOMAIN:
					setRoot(InfoViewModelBuilder.buildDomainView(InfoDomain.this,
						pc,
						Globals.getContext().ref.getConstructedCDOMObjects(Deity.class)));
					break; // end VIEW_DOMAIN

				case GuiConstants.INFODOMAIN_VIEW_PANTHEON:
					setRoot(InfoViewModelBuilder.buildPantheonView(InfoDomain.this,
						pc,
						Globals.getContext().ref.getConstructedCDOMObjects(Deity.class)));
					break; // end VIEW_PANTHEON

				case GuiConstants.INFODOMAIN_VIEW_SOURCE:
					setRoot(InfoViewModelBuilder.buildSourceView(InfoDomain.this,
						pc,
						Globals.getContext().ref.getConstructedCDOMObjects(Deity.class)));
					break; // end VIEW_SOURCE

				default:
					Logging
						.errorPrint(PropertyFactory.getFormattedString("in_domInTheMode",
							String.valueOf(mode)));
					break;
			} // end of switch(mode)

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super
					.getRoot()));
			}
		}

		public List<String> getMColumnList()
		{
			List<String> retList = new ArrayList<String>();
			for (int i = 1; i < deityNameList.length; i++)
			{
				retList.add(deityNameList[i]);
			}
			return retList;
		}

		public boolean isMColumnDisplayed(int col)
		{
			return (displayList.get(col)).booleanValue();
		}

		public void setMColumnDisplayed(int col, boolean disp)
		{
			setColumnViewOption(deityNameList[col], disp);
			displayList.set(col, Boolean.valueOf(disp));
		}

		public int getMColumnOffset()
		{
			return 1;
		}

		public int getMColumnDefaultWidth(int col)
		{
			return SettingsHandler.getPCGenOption("InfoDomain.deity.sizecol."
				+ deityNameList[col], deityColList[col]);
		}

		public void setMColumnDefaultWidth(int col, int width)
		{
			SettingsHandler.setPCGenOption("InfoDomain.deity.sizecol."
				+ deityNameList[col], width);
		}

		private boolean getColumnViewOption(String colName, boolean defaultVal)
		{
			return SettingsHandler.getPCGenOption("InfoDomain.deity.viewcol."
				+ colName, defaultVal);
		}

		private void setColumnViewOption(String colName, boolean val)
		{
			SettingsHandler.setPCGenOption("InfoDomain.deity.viewcol."
				+ colName, val);
		}

		public void resetMColumn(int col, TableColumn column)
		{
			// TODO Auto-generated method stub

		}
	}

	class SourcedQualObject<T> extends QualifiedObject<T>
	{
		
		private final CDOMObject sourceObject;

		public SourcedQualObject(T anObj, CDOMObject source,
				List<Prerequisite> prereqList)
		{
			super(anObj, prereqList);
			sourceObject = source;
		}

		public SourcedQualObject(T anObj, CDOMObject source, Prerequisite prereq)
		{
			super(anObj, prereq);
			sourceObject = source;
		}

		public SourcedQualObject(T anObj)
		{
			super(anObj);
			sourceObject = null;
		}

		public boolean qualifies(PlayerCharacter pc)
		{
			return qualifies(pc, sourceObject);
		}
		
		public T getObject(PlayerCharacter pc)
		{
			return getObject(pc, sourceObject);
		}

	}
	/**
	 * This is the Model that populate the table for Domains
	 */
	private final class DomainModel extends AbstractTableModel implements
			TableColumnManagerModel
	{
		private List<SourcedQualObject<Domain>> availDomainList =
				new ArrayList<SourcedQualObject<Domain>>();
		private List<SourcedQualObject<Domain>> displayDomainList =
				new ArrayList<SourcedQualObject<Domain>>();
		private String qFilter = null;
		private List<Boolean> displayList = null;

		private final String[] domainColList =
				new String[]{PropertyFactory.getString("in_domains"),
					PropertyFactory.getString("in_sourceLabel")};

		private final int[] domainWidthList = new int[]{200, 100};

		private DomainModel()
		{
			displayList = new ArrayList<Boolean>();
			displayList.add(Boolean.TRUE);
			displayList.add(Boolean.valueOf(getColumnViewOption(
				domainColList[1], true)));
		}

		public void setAvailDomainList(List<SourcedQualObject<Domain>> dl)
		{
			availDomainList = dl;
		}

		@Override
		public Class<?> getColumnClass(int col)
		{
			return getValueAt(0, col).getClass();
		}

		public int getColumnCount()
		{
			return domainColList.length;
		}

		/**
		 * Reset the model
		 */
		public void resetModel()
		{
			displayDomainList.clear();
			for (int i = 0; i < availDomainList.size(); i++)
			{
				SourcedQualObject<Domain> dom = availDomainList.get(i);
				//TODO Does anyone know why we don't call
				//aFN.setIsValid(aFeat.passesPreReqToGain()) here?
				if (qFilter == null
					|| dom.getRawObject().getDisplayName().toLowerCase().indexOf(qFilter) >= 0)
				{
					displayDomainList.add(dom);
				}
			}
		}

		// The default implementations of these methods in
		// AbstractTableModel would work, but we can refine them.
		@Override
		public String getColumnName(int column)
		{
			return domainColList[column];
		}

		public int getRowCount()
		{
			return displayDomainList.size();
		}

		public Object getValueAt(int row, int col)
		{
			if ((row < 0) || (row >= displayDomainList.size()))
			{
				return "";
			}

			final SourcedQualObject<Domain> aQualDomain = displayDomainList.get(row);
			final Domain aDomain = aQualDomain.getRawObject();
			//Logging.errorPrint("Checking prereq of " + aQualDomain.toString());

			if (aDomain == null)
			{
				return null;
			}

			StringBuffer retVal = new StringBuffer(80);

			switch (col)
			{
				case -1: // return domain object for the row selected
					return aQualDomain;

				case 0:

					// the case where selected domains are
					// bolded is insufficent becuase it
					// conflicts with PI-formatting
					// (bold-italic), so I added an asterisk
					if (selectedDomainList.contains(displayDomainList.get(row).getRawObject()))
					{
						retVal.append("<html><b>")
							.append(OutputNameFormatting.piString(aDomain, false)).append(
								"*</b></html>");
					}
					else if (!aDomain.qualifies(pc, aDomain) || !aQualDomain.qualifies(pc))
					{
						retVal.append("<html>").append(
							SettingsHandler.getPrereqFailColorAsHtmlStart())
							.append(OutputNameFormatting.piString(aDomain, false)).append(
								SettingsHandler.getPrereqFailColorAsHtmlEnd())
							.append("</html>");
					}
					else
					{
						retVal.append(OutputNameFormatting.piString(aDomain, true));
					}

					break;

				case 1:
					try
					{
						retVal.append(SourceFormat.getFormattedString(aDomain,
						Globals.getSourceDisplay(), true));
					}
					catch (Exception exc)
					{
						Logging.errorPrint(PropertyFactory
							.getString("in_errorMess"), exc);
					}

					break;

				default:
					Logging.errorPrint(PropertyFactory.getFormattedString("in_domInTheColumn2",
						String.valueOf(col)));

					break;
			}

			return retVal.toString();
		}

		/**
		 * Get the QuickFilter
		 * @return QuickFilter
		 */
		public String getQFilter()
		{
			return qFilter;
		}

		/**
		 * Set theQuickFilter
		 * @param quickFilter
		 */
		public void setQFilter(String quickFilter)
		{
			if (quickFilter != null)
			{
				this.qFilter = quickFilter.toLowerCase();
			}
			else
			{
				this.qFilter = null;
			}
		}

		/**
		 * Clear the QuickFilter
		 */
		public void clearQFilter()
		{
			this.qFilter = null;
		}

		public List<String> getMColumnList()
		{
			List<String> retList = new ArrayList<String>();
			retList.add(domainColList[1]);
			return retList;
		}

		public boolean isMColumnDisplayed(int col)
		{
			return (displayList.get(col)).booleanValue();
		}

		public void setMColumnDisplayed(int col, boolean disp)
		{
			setColumnViewOption(domainColList[col], disp);
			displayList.set(col, Boolean.valueOf(disp));
		}

		public int getMColumnOffset()
		{
			return 1;
		}

		public int getMColumnDefaultWidth(int col)
		{
			return SettingsHandler.getPCGenOption("InfoDomain.domain.sizecol."
				+ domainColList[col], domainWidthList[col]);
		}

		public void setMColumnDefaultWidth(int col, int width)
		{
			SettingsHandler.setPCGenOption("InfoDomain.domain.sizecol."
				+ domainColList[col], width);
		}

		private boolean getColumnViewOption(String colName, boolean defaultVal)
		{
			return SettingsHandler.getPCGenOption("InfoDomain.domain.viewcol."
				+ colName, defaultVal);
		}

		private void setColumnViewOption(String colName, boolean val)
		{
			SettingsHandler.setPCGenOption("InfoDomain.domain.viewcol."
				+ colName, val);
		}

		public void resetMColumn(int col, TableColumn column)
		{
			// TODO Auto-generated method stub

		}
	}

	/**
	 * This class is a MouseAdapter to handle mouse clickes on the domain table.
	 * Double-Clicks select the domain while single clicks simply display the
	 * information about the selected domain.
	 */
	private final class DomainMouseAdapter extends MouseAdapter
	{
		private final ListSelectionModel lsm = domainTable.getSelectionModel();

		@Override
		public void mouseClicked(MouseEvent f)
		{
			final int selectedRow =
					domainSorter.getRowTranslated(lsm.getMinSelectionIndex());

			if (selectedRow < 0)
			{
				return;
			}

			final int clickCount = f.getClickCount();

			switch (clickCount)
			{
				case (1):

					final QualifiedObject<Domain> qualDomain =
							(QualifiedObject<Domain>) domainModel.getValueAt(
								selectedRow, -1);
					final String domainKey =
						qualDomain.getRawObject().toString();

					if (domainKey != null)
					{
						final Domain aDomain =
								Globals.getContext().ref.silentlyGetConstructedCDOMObject(Domain.class, domainKey);
						setDomainInfoText(aDomain, qualDomain.getPrerequisiteList());
					}

					break;

				case (2):
					//No break
				default:

					//Assuming that anyone who manages to click more than twice actually meant to click twice.
					selectDomainIndex(selectedRow);

					break;
			}
		}
	}
}
