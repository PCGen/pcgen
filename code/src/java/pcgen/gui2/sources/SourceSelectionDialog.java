/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.sources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.core.LoadableFacade.LoadingState;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.SortedListFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.UIContext;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.dialog.DataInstaller;
import pcgen.gui2.tools.CommonMenuText;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.tools.InfoPaneLinkAction;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FacadeListModel;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Comparators;

public class SourceSelectionDialog extends JDialog implements ActionListener, ChangeListener, ListSelectionListener
{

	private static final UIPropertyContext CONTEXT =
			UIPropertyContext.createContext("SourceSelectionDialog"); //$NON-NLS-1$
	private static final String PROP_SELECTED_SOURCE = "selectedSource"; //$NON-NLS-1$
	private static final String LOAD_COMMAND = "Load"; //$NON-NLS-1$
	private static final String CANCEL_COMMAND = "Cancel"; //$NON-NLS-1$
	private static final String SAVE_COMMAND = "Save"; //$NON-NLS-1$
	private static final String DELETE_COMMAND = "Delete"; //$NON-NLS-1$
	private static final String INSTALLDATA_COMMAND = "Install"; //$NON-NLS-1$
	private final PCGenFrame frame;
	private final QuickSourceSelectionPanel basicPanel;
	private final AdvancedSourceSelectionPanel advancedPanel;
	private final JTabbedPane tabs;
	private final JPanel buttonPanel;
	private final JButton loadButton;
	private final JButton cancelButton;
	private final JButton deleteButton;
	private final JButton installDataButton;
	private final JButton saveButton;
	private final JCheckBox alwaysAdvancedCheck;

	public SourceSelectionDialog(PCGenFrame frame, UIContext uiContext)
	{
		super(frame, true);
		this.frame = frame;
		setTitle(LanguageBundle.getString("in_mnuSourcesLoadSelect")); //$NON-NLS-1$
		this.tabs = new JTabbedPane();
		this.basicPanel = new QuickSourceSelectionPanel();
		this.advancedPanel = new AdvancedSourceSelectionPanel(frame, uiContext);
		this.buttonPanel = new JPanel();
		this.loadButton = new JButton();
		CommonMenuText.name(loadButton, "load"); //$NON-NLS-1$
		this.cancelButton = new JButton();
		CommonMenuText.name(cancelButton, "cancel"); //$NON-NLS-1$

		this.deleteButton = new JButton();
		CommonMenuText.name(deleteButton, "delete"); //$NON-NLS-1$
		this.installDataButton = new JButton();
		CommonMenuText.name(installDataButton, "mnuSourcesInstallData"); //$NON-NLS-1$
		this.saveButton = new JButton();
		CommonMenuText.name(saveButton, "saveSelection"); //$NON-NLS-1$
		this.alwaysAdvancedCheck = new JCheckBox(LanguageBundle.getString("in_sourceAlwaysAdvanced"), //$NON-NLS-1$
			!UIPropertyContext.getInstance().initBoolean(UIPropertyContext.SOURCE_USE_BASIC_KEY, true));
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		initComponents();
		initDefaults();
		pack();
	}

	public static boolean skipSourceSelection()
	{
		return UIPropertyContext.getInstance().initBoolean(UIPropertyContext.SKIP_SOURCE_SELECTION, false);
	}

	private void initComponents()
	{
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		tabs.add(LanguageBundle.getString("in_basic"), basicPanel); //$NON-NLS-1$
		tabs.add(LanguageBundle.getString("in_advanced"), advancedPanel); //$NON-NLS-1$
		tabs.addChangeListener(this);
		pane.add(tabs, BorderLayout.CENTER);

		loadButton.setDefaultCapable(true);
		getRootPane().setDefaultButton(loadButton);

		loadButton.setActionCommand(LOAD_COMMAND);
		cancelButton.setActionCommand(CANCEL_COMMAND);
		deleteButton.setActionCommand(DELETE_COMMAND);
		saveButton.setActionCommand(SAVE_COMMAND);
		installDataButton.setActionCommand(INSTALLDATA_COMMAND);

		loadButton.addActionListener(this);
		cancelButton.addActionListener(this);
		saveButton.addActionListener(this);
		deleteButton.addActionListener(this);
		installDataButton.addActionListener(this);

		Box buttons = Box.createHorizontalBox();
		buttons.add(buttonPanel);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(installDataButton);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(loadButton);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(cancelButton);
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pane.add(buttons, BorderLayout.SOUTH);

		Utility.installEscapeCloseOperation(this);
	}

	private void initDefaults()
	{
		boolean useBasic = CONTEXT.initBoolean("useBasic", true); //$NON-NLS-1$
		SourceSelectionFacade selection = basicPanel.getSourceSelection();
		if (selection != null && useBasic)
		{
			basicPanel.makeSourceSelected(selection);
		}
		else
		{
			deleteButton.setEnabled(false);
		}
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		if (useBasic)
		{
			setBasicButtons();
			advancedPanel.setSourceSelection(basicPanel.getSourceSelection());
		}
		else
		{
			setAdvancedButtons();
			tabs.setSelectedIndex(1);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			SourceSelectionFacade selection = basicPanel.getSourceSelection();
			deleteButton.setEnabled(selection.isModifiable());
			advancedPanel.setSourceSelection(selection);
		}
	}

	private void setBasicButtons()
	{
		buttonPanel.removeAll();
		buttonPanel.add(deleteButton);
		buttonPanel.revalidate();
	}

	private void setAdvancedButtons()
	{
		buttonPanel.removeAll();
		buttonPanel.add(saveButton);
		buttonPanel.add(alwaysAdvancedCheck);
		buttonPanel.revalidate();
	}

	/**
	 * Set the selected advanced sources for a particular game mode and
	 * remember them. Overrides existing selections.
	 *
	 * @param sourceSel A selection facade of the sources to set.
	 */
	public void setAdvancedSources(final SourceSelectionFacade sourceSel)
	{
		advancedPanel.setSourceSelection(sourceSel);
		advancedPanel.rememberSelectedSources();
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (tabs.getSelectedComponent() == basicPanel)
		{
			setBasicButtons();
		}
		else
		{
			setAdvancedButtons();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		switch (command)
		{
			case SAVE_COMMAND -> {
				final JList sourcesList = new JList<>();
				final JTextField nameField = new JTextField();
				ListFacade<SourceSelectionFacade> sources = new SortedListFacade<>(
						Comparators.toStringIgnoreCaseCollator(),
						FacadeFactory.getCustomSourceSelections()
				);
				sourcesList.setModel(new FacadeListModel<>(sources));
				sourcesList.addListSelectionListener(lse -> nameField.setText(sourcesList.getSelectedValue()
				                                                                         .toString()));
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(new JScrollPane(sourcesList), BorderLayout.CENTER);
				panel.add(nameField, BorderLayout.SOUTH);
				int ret = JOptionPane.showOptionDialog(this, panel, "Save the source selection as...",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null
				);
				if (ret == JOptionPane.OK_OPTION)
				{
					String name = nameField.getText();
					List<Campaign> selectedCampaigns = advancedPanel.getSelectedCampaigns();
					GameMode selectedGameMode = advancedPanel.getSelectedGameMode();

					SourceSelectionFacade selection = null;
					for (SourceSelectionFacade sourceSelectionFacade : sources)
					{
						if (sourceSelectionFacade.toString().equals(name))
						{
							selection = sourceSelectionFacade;
							break;

						}
					}
					if (selection == null)
					{
						selection = FacadeFactory.createCustomSourceSelection(name);
					}
					selection.setCampaigns(selectedCampaigns);
					selection.setGameMode(selectedGameMode);
					basicPanel.setSourceSelection(selection);
				}
			}
			case DELETE_COMMAND -> FacadeFactory.deleteCustomSourceSelection(basicPanel.getSourceSelection());
			case LOAD_COMMAND -> fireSourceLoad();
			case INSTALLDATA_COMMAND -> {
				// Swap to the install data dialog.
				setVisible(false);
				DataInstaller di = new DataInstaller();
				di.setVisible(true);
			}
			default -> //must be the cancel command
					setVisible(false);
		}
	}

	private void fireSourceLoad()
	{
		SourceSelectionFacade selection;
		if (tabs.getSelectedComponent() == basicPanel)
		{
			selection = basicPanel.getSourceSelection();
		}
		else
		{
			selection = FacadeFactory.createSourceSelection(advancedPanel.getSelectedGameMode(),
				advancedPanel.getSelectedCampaigns());
		}
		if (selection == null)
		{
			return;
		}
		List<Campaign> campaigns = ListFacades.wrap(selection.getCampaigns());
		if (FacadeFactory.passesPrereqs(campaigns))
		{
			setVisible(false);
			frame.loadSourceSelection(selection);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "Some sources have unfulfilled prereqs", "Cannot Load Selected Sources",
				JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			advancedPanel.refreshDisplay();
		}
		else
		{
			UIPropertyContext.getInstance().setBoolean(UIPropertyContext.SOURCE_USE_BASIC_KEY,
				!alwaysAdvancedCheck.isSelected());
		}
		super.setVisible(visible);
	}

	private class QuickSourceSelectionPanel extends JPanel implements ListSelectionListener
	{

		private static final String DEFAULT_SOURCE = "Pathfinder RPG for Players"; //$NON-NLS-1$
		private final JList sourceList;
		private final InfoPane infoPane;
		private final InfoPaneLinkAction linkAction;

		public QuickSourceSelectionPanel()
		{
			sourceList = new JList<>();
			infoPane = new InfoPane(LanguageBundle.getString("in_src_info")); //$NON-NLS-1$
			linkAction = new InfoPaneLinkAction(infoPane);

			initComponents();
			initDefaults();
		}

		private void initComponents()
		{
			setLayout(new BorderLayout());
			JLabel label = new JLabel(LanguageBundle.getString("in_qsrc_intro"));
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			add(label, BorderLayout.NORTH);
			ListFacade<SourceSelectionFacade> sources = new SortedListFacade<>(Comparators.toStringIgnoreCaseCollator(),
				FacadeFactory.getDisplayedSourceSelections());
			sourceList.setModel(new FacadeListModel<>(sources));
			sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			sourceList.setCellRenderer(new SourceListCellRenderer());
			sourceList.addMouseListener(new MouseAdapter()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getClickCount() > 1)
					{
						Point p = e.getPoint();
						int index = sourceList.locationToIndex(p);
						if (sourceList.getCellBounds(index, index).contains(p))
						{
							fireSourceLoad();
						}
					}

				}

			});
			FlippingSplitPane mainPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			mainPane.setTopComponent(new JScrollPane(sourceList));

			linkAction.install();
			infoPane.setPreferredSize(new Dimension(800, 150));
			mainPane.setBottomComponent(infoPane);
			add(mainPane, BorderLayout.CENTER);
		}

		private void initDefaults()
		{
			final ListModel sortedModel = sourceList.getModel();
			String defaultSelectedSource = CONTEXT.initProperty(PROP_SELECTED_SOURCE, DEFAULT_SOURCE);
			int index = Collections.binarySearch(new AbstractList<Object>()
			{

				@Override
				public Object get(int idx)
				{
					return sortedModel.getElementAt(idx);
				}

				@Override
				public int size()
				{
					return sortedModel.getSize();
				}

			}, defaultSelectedSource, Comparators.toStringIgnoreCaseCollator());
			if (index >= 0)
			{
				sourceList.setSelectedIndex(index);
			}
			else if (sortedModel.getSize() > 0)
			{
				sourceList.setSelectedIndex(0);
			}
			sourceList.addListSelectionListener(this);
		}

		public SourceSelectionFacade getSourceSelection()
		{
			return (SourceSelectionFacade) sourceList.getSelectedValue();
		}

		void setSourceSelection(SourceSelectionFacade source)
		{
			sourceList.setSelectedValue(source, true);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			SourceSelectionFacade selection = getSourceSelection();
			if (selection != null)
			{
				CONTEXT.setProperty(PROP_SELECTED_SOURCE, selection.toString());
				makeSourceSelected(selection);
			}
			else
			{
				deleteButton.setEnabled(false);
			}
		}

		/**
		 * Take the necessary action to show the source the currently selected 
		 * source. This assumes that the source list has already been updated, 
		 * either as a default setting, or from a user action. It does not set
		 * the source to be remembered as the last selection as that may 
		 * interfere with startup defaults, so the caller must do that if 
		 * responding to a user action. 
		 * @param selection The sources selected.
		 */
		public void makeSourceSelected(SourceSelectionFacade selection)
		{
			advancedPanel.setSourceSelection(selection);
			deleteButton.setEnabled(selection.isModifiable());
			infoPane.setText(FacadeFactory.getCampaignInfoFactory().getHTMLInfo(selection));
		}

		private class SourceListCellRenderer extends DefaultListCellRenderer
		{

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setToolTipText("");
				if (value instanceof SourceSelectionFacade selection)
				{
					if (selection.isModifiable())
					{
						setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getCustomItemColor()));
					}
					if (selection.getLoadingState() == LoadingState.LOADED_WITH_ERRORS)
					{
						setForeground(Color.LIGHT_GRAY);
						setToolTipText(selection.getLoadingErrorMessage());
					}
				}
				return this;
			}

		}

	}

}
