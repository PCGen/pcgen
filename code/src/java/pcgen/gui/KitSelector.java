/*
 * KitSelector.java
 *
 *
 * @(#) $Id$
 *
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 24, 2002, 8:59 PM
 *
 * version $Revision$
 */

/**
  * This class is responsible for the kit selection dialog.  It allows the user
  * to select a kit to be applied from a list of qualifying kits.
  *
  * @author  Greg Bingleman
  * @version $Revision$
  */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.enumeration.KitApply;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.kit.BaseKit;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.tabs.InfoTabUtils;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;

final class KitSelector extends JFrame
{
	static final long serialVersionUID = -7963863233576905914L;
	private static final int USER_NO = 0;
	private static final int USER_YES = 1;
	private FlippingSplitPane spChoices;
	private FlippingSplitPane spMain;
	private JButton btnAdd;
	private JButton btnOk;
	private JButton btnRemove;
	private JLabel lblAvailable;
	private JLabel lblSelected;

	// Variables declaration
	private JLabelPane txtInfo;
	private JPanel pnlAvailable;
	private JPanel pnlBottom;
	private JPanel pnlFrame;
	private JPanel pnlInfo;
	private JPanel pnlSelected;
	private JScrollPane scpAvailable;
	private JScrollPane scpInfo;
	private JScrollPane scpSelected;
	private JList lstAvailable;
	private JList lstSelected;
	private PlayerCharacter aPC = null;
	private int userResponse = 0;

	private final JLabel lblAvailableQFilter = new JLabel(LanguageBundle.getString("in_filter") + ":");
	private final JLabel lblSelectedQFilter = new JLabel(LanguageBundle.getString("in_filter") + ":");
	private JTextField textAvailableQFilter = new JTextField();
	private JTextField textSelectedQFilter = new JTextField();
	private JButton clearAvailableQFilterButton = new JButton(LanguageBundle.getString("in_clear"));
	private JButton clearSelectedQFilterButton = new JButton(LanguageBundle.getString("in_clear"));
	private KitListModel availableModel;
	private KitListModel selectedModel;
	
	/**
	 * Creates new form KitSelector
	 * @param argPC the PlayerCharacter the kit will be applied to
	 */
	KitSelector(PlayerCharacter argPC)
	{
		super();

		aPC = argPC;

		initComponents();
		initComponentContents();

		int t = spChoices.getDividerLocation();
		int u = spMain.getDividerLocation();

		t = SettingsHandler.getPCGenOption("kitSelector.spChoices", t); //$NON-NLS-1$
		u = SettingsHandler.getPCGenOption("kitSelector.spMain", u); //$NON-NLS-1$

		if (t > 0)
		{
			spChoices.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoSkills.bsplit", t); //$NON-NLS-1$
		}

		if (u > 0)
		{
			spMain.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoSkills.asplit", u); //$NON-NLS-1$
		}
		
		final Dimension kitSelDim = SettingsHandler.getKitSelectorDimension();
		final Point kitSelLoc = SettingsHandler.getKitSelectorLeftUpperCorner();
		int x = -11;
		int y = -11;

		if (kitSelLoc != null)
		{
			x = (int) kitSelLoc.getX();
			y = (int) kitSelLoc.getY();
		}

		if ((x < -10) || (y < -10) || (kitSelDim == null) || (kitSelDim.height == 0)
		    || (kitSelDim.width == 0))
		{
			setSize(new Dimension(640, 460));
			Utility.centerFrame(this, false);
		}
		else
		{
			setLocation(kitSelLoc);
			setSize(kitSelDim);
		}
	}

	/**
	 * Dismisses the dialog box.
	 */
	public void closeDialog()
	{
		// Save window size and position of scroll pane dividers
		SettingsHandler.setKitSelectorLeftUpperCorner(getLocationOnScreen());
		SettingsHandler.setKitSelectorDimension(getSize());
		SettingsHandler.setPCGenOption(
			"kitSelector.spChoices", spChoices.getDividerLocation()); //$NON-NLS-1$
		SettingsHandler.setPCGenOption(
			"kitSelector.spMain", spMain.getDividerLocation()); //$NON-NLS-1$
		
		setVisible(false);
		dispose();
	}

	private static boolean isDoubleClick(MouseEvent evt, JButton btn)
	{
		if (evt.getClickCount() == 2 && btn.isEnabled())
		{
			return true;
		}

		return false;
	}

	private void addKit(Kit theKit)
	{
		//
		// Make sure pass prereqs
		//
		if ((theKit == null) || !kitPassesPrereqs(theKit))
		{
			return;
		}

		List<BaseKit> thingsToAdd = new ArrayList<BaseKit>();
		List<String> warnings     = new ArrayList<String>();
		theKit.testApplyKit(aPC, thingsToAdd, warnings);

		//
		// See if user wants to apply the kit even though there were errors
		//
		if ((warnings.size() != 0) && (showWarnings(warnings) == USER_NO))
		{
			return;
		}

		// The user is applying the kit so use the real PC now.
		theKit.processKit(aPC, thingsToAdd);
		forceTabUpdate();

		if (theKit.getSafe(ObjectKey.APPLY_MODE) == KitApply.PERMANENT)
		{
			( (KitListModel) (lstAvailable.getModel())).removeItem(theKit);
			( (KitListModel) (lstSelected.getModel())).addItem(theKit);

			//
			// Need this or lists don't refresh
			// don't know if this is true or not.
			//
			lstSelected.updateUI();
			lstAvailable.updateUI();
		}
	}

	private void addSelections(JList lst, Collection<Kit> kits, Collection<Kit> excluded, PlayerCharacter aPlayerCharacter)
	{
		if ((kits == null) || (kits.size() == 0))
		{
			return;
		}

		for (Kit aKit : kits)
		{
			if (!aKit.isVisible(aPlayerCharacter))
			{
				continue;
			}

			if ((excluded != null) && excluded.contains(aKit))
			{
				continue;
			}

			((KitListModel) (lst.getModel())).addItem(aKit);
		}
	}

	private void btnAddActionPerformed()
	{
		btnAdd.setEnabled(false);

		Kit aKit = (Kit)lstAvailable.getSelectedValue();

		if (aKit != null)
		{
			addKit(aKit);
			requestFocus();
		}
		btnAdd.setEnabled(true);
	}

	private void btnOKActionPerformed()
	{
		closeDialog();
	}

	private void btnRemoveActionPerformed()
	{
		btnRemove.setEnabled(false);
	}

	private static void forceTabUpdate()
	{
		PCGen_Frame1.getInst().updateByKludge();
	}

	private void initComponentContents()
	{
		IconUtilitities.maybeSetIcon(this, IconUtilitities.RESOURCE_APP_ICON);

		
		availableModel = new KitListModel(new ArrayList<Kit>());
		lstAvailable = new JList(availableModel);
		lstAvailable.setCellRenderer(new MyCellRenderer());
		lstAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scpAvailable.setViewportView(lstAvailable);

		selectedModel = new KitListModel(new ArrayList<Kit>());
		lstSelected = new JList(selectedModel);
		lstSelected.setCellRenderer(new MyCellRenderer());
		lstSelected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scpSelected.setViewportView(lstSelected);

		final Collection<Kit> pcKitInfo = aPC.getKitInfo();
		addSelections(lstAvailable, Globals.getContext().ref.getConstructedCDOMObjects(Kit.class), pcKitInfo, aPC);
		addSelections(lstSelected, pcKitInfo, null, aPC);

		if (lstAvailable.isSelectionEmpty() == false)
		{
			btnAdd.setEnabled(true);
		}
		if (lstSelected.isSelectionEmpty() == false )
		{
			btnRemove.setEnabled(true);
		}

		lstAvailable.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					lstAvailableMouseClicked(evt);
				}
			});
		lstAvailable.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent evt)
				{
					lstAvailableSelectionChanged();
				}
			});
		lstSelected.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					lstSelectedMouseClicked(evt);
				}
			});
		lstSelected.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent evt)
				{
					lstSelectedSelectionChanged();
				}
			});
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlFrame = new JPanel();
		spMain = new FlippingSplitPane();
		spChoices = new FlippingSplitPane();
		pnlAvailable = new JPanel();
		btnAdd = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		btnRemove = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		lblAvailable = new JLabel();
		scpAvailable = new JScrollPane();
		pnlSelected = new JPanel();
		lblSelected = new JLabel();
		scpSelected = new JScrollPane();
		pnlBottom = new JPanel();
		pnlInfo = new JPanel();
		scpInfo = new JScrollPane();
		txtInfo = new JLabelPane();
		btnOk = new JButton();

		setTitle("Kit Selection");

		addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent evt)
				{
					closeDialog();
				}
			});

		pnlFrame.setLayout(new BorderLayout());

		pnlFrame.setBorder(new EmptyBorder(new Insets(3, 3, 3, 1)));
		spMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
		spMain.setResizeWeight(0.5);
		spMain.setOneTouchExpandable(true);
		spMain.setDividerSize(10);
		spChoices.setResizeWeight(0.5);
		spChoices.setOneTouchExpandable(true);
		spChoices.setDividerSize(10);
		pnlAvailable.setLayout(new GridBagLayout());

		pnlAvailable.setBorder(new EmptyBorder(new Insets(1, 3, 1, 3)));

		gridBagConstraints = new GridBagConstraints();
		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		pnlAvailable.add(InfoTabUtils.createFilterPane(null, null, lblAvailableQFilter,
			textAvailableQFilter, clearAvailableQFilterButton), gridBagConstraints);

		lblAvailable.setText("Available");
		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		pnlAvailable.add(lblAvailable, gridBagConstraints);

		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 1.0, 1.0, GridBagConstraints.BOTH,
			GridBagConstraints.WEST);
		pnlAvailable.add(scpAvailable, gridBagConstraints);

		btnAdd.setEnabled(false);
		btnAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddActionPerformed();
			}
		});
		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 1.0, 1.0, GridBagConstraints.NONE,
			GridBagConstraints.CENTER);
		pnlAvailable.add(btnAdd, new GridBagConstraints());
		
		spChoices.setLeftComponent(pnlAvailable);

		pnlSelected.setLayout(new GridBagLayout());

		pnlSelected.setBorder(new EmptyBorder(new Insets(1, 3, 1, 3)));
		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		pnlSelected.add(InfoTabUtils.createFilterPane(null, null, lblSelectedQFilter,
			textSelectedQFilter, clearSelectedQFilterButton), gridBagConstraints);

		lblSelected.setText("Selected");
		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		pnlSelected.add(lblSelected, gridBagConstraints);

		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 1.0, 1.0, GridBagConstraints.BOTH,
			GridBagConstraints.WEST);
		pnlSelected.add(scpSelected, gridBagConstraints);

		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnRemoveActionPerformed();
				}
			});
		gridBagConstraints = new GridBagConstraints();
		Utility.buildRelativeConstraints(gridBagConstraints,
			GridBagConstraints.REMAINDER, 1, 0.0, 0.0, GridBagConstraints.NONE,
			GridBagConstraints.CENTER);
		pnlSelected.add(btnRemove, gridBagConstraints);
		
		spChoices.setRightComponent(pnlSelected);

		spMain.setLeftComponent(spChoices);

		pnlBottom.setLayout(new GridBagLayout());

		pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));

		pnlInfo.setBorder(new TitledBorder("Kit Info"));
		txtInfo.setEditable(false);
		txtInfo.setBackground(pnlInfo.getBackground());
		scpInfo.setViewportView(txtInfo);

		pnlInfo.add(scpInfo);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlBottom.add(pnlInfo, gridBagConstraints);

		btnOk.setMnemonic('O');
		btnOk.setText("Ok");
		btnOk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnOKActionPerformed();
				}
			});

		pnlBottom.add(btnOk, new GridBagConstraints());

		spMain.setRightComponent(pnlBottom);

		pnlFrame.add(spMain, BorderLayout.CENTER);

		getContentPane().add(pnlFrame, BorderLayout.CENTER);
		
		textAvailableQFilter.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent evt)
			{
				setQFilter(availableModel);
			}

			public void insertUpdate(DocumentEvent evt)
			{
				setQFilter(availableModel);
			}

			public void removeUpdate(DocumentEvent evt)
			{
				setQFilter(availableModel);
			}
		});
		clearAvailableQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearQFilter(availableModel);
			}
		});
		
		textSelectedQFilter.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent evt)
			{
				setQFilter(selectedModel);
			}

			public void insertUpdate(DocumentEvent evt)
			{
				setQFilter(selectedModel);
			}

			public void removeUpdate(DocumentEvent evt)
			{
				setQFilter(selectedModel);
			}
		});
		clearSelectedQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearQFilter(selectedModel);
			}
		});
		
	}

	private boolean kitPassesPrereqs(Kit theKit)
	{
		return theKit.qualifies(aPC, theKit);
	}

	private void lstAvailableMouseClicked(MouseEvent evt)
	{
		if (isDoubleClick(evt, btnAdd))
		{
			btnAddActionPerformed();
		}
	}

	//
	// Listen for when the selection changes.
	//
	private void lstAvailableSelectionChanged()
	{
		boolean bEnable = false;

		Kit aKit = (Kit)lstAvailable.getSelectedValue();

		if (aKit != null)
		{
			showKitInfo(aKit);
			bEnable = true;
		}

		btnAdd.setEnabled(bEnable);
	}

	private void lstSelectedMouseClicked(MouseEvent evt)
	{
		if (isDoubleClick(evt, btnRemove))
		{
			btnRemoveActionPerformed();
		}
	}

	private void lstSelectedSelectionChanged()
	{
		boolean bEnable = false;

		Kit aKit = (Kit)lstSelected.getSelectedValue();

		if (aKit != null)
		{
			showKitInfo(aKit);
			bEnable = true;
		}

		btnRemove.setEnabled(bEnable);
	}

	private void showKitInfo(Kit theKit)
	{
		if (theKit == null)
		{
			txtInfo.setText();

			return;
		}
		txtInfo.setText(theKit.getInfo(aPC));
	}

	private int showWarnings(List<String> warnings)
	{
		userResponse = USER_NO;

		try
		{
			final JDialog aFrame = new JDialog(this, "Warnings", true);

			final JPanel jPanel1 = new JPanel();
			final JPanel jPanel2 = new JPanel();
			final JPanel jPanel3 = new JPanel();
			final JLabel jLabel1 = new JLabel("The following warnings were encountered");
			final JButton jApply = new JButton("Apply");
			final JButton jAbort = new JButton("Abort");

			jPanel1.add(jLabel1);

			jPanel2.setLayout(new BorderLayout());

			aFrame.getContentPane().add(jPanel1, BorderLayout.NORTH);
			aFrame.getContentPane().add(jPanel2, BorderLayout.CENTER);
			aFrame.getContentPane().add(jPanel3, BorderLayout.SOUTH);

			StringBuffer warningInfo = new StringBuffer(100);
			warningInfo.append("<html>");

			for (String s : warnings)
			{
				warningInfo.append(s).append("<br>");
			}

			warningInfo.append("</html>");

			JEditorPane a = new JEditorPane("text/html", warningInfo.toString());
			a.setEditable(false);

			JScrollPane aPane = new JScrollPane();
			aPane.setViewportView(a);
			jPanel2.add(aPane, BorderLayout.CENTER);

			jPanel3.add(jAbort);
			jPanel3.add(jApply);

			jApply.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						userResponse = USER_YES;
						aFrame.dispose();
					}
				});

			jAbort.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						userResponse = USER_NO;
						aFrame.dispose();
					}
				});

			aFrame.setSize(new Dimension(456, 352));
			aFrame.setLocationRelativeTo(this); // centre on parent
			aFrame.setVisible(true);
		}
		catch (Exception e)
		{
			//TODO: If we really should ignore this, add a note explaining why. XXX
		}

		return userResponse;
	}

	private void clearQFilter(KitListModel model)
	{
		model.clearQFilter();
		model.resetModel();
		if (model == availableModel)
		{
			textAvailableQFilter.setText("");
			clearAvailableQFilterButton.setEnabled(false);
			lstAvailable.updateUI();
		}
		else
		{
			textSelectedQFilter.setText("");
			clearSelectedQFilterButton.setEnabled(false);
			lstSelected.updateUI();
		}
		//forceRefresh();
	}

	private void setQFilter(KitListModel model)
	{
		String aString =
				model == availableModel ? textAvailableQFilter.getText()
					: textSelectedQFilter.getText();

		if (aString.length() == 0)
		{
			clearQFilter(model);
			return;
		}
		model.setQFilter(aString);

		model.resetModel();
		if (model == availableModel)
		{
			clearAvailableQFilterButton.setEnabled(true);
			lstAvailable.updateUI();
		}
		else
		{
			clearSelectedQFilterButton.setEnabled(true);
			lstSelected.updateUI();
		}
		//forceRefresh();
	}

	private class KitListModel extends AbstractListModel
	{
		private List<Kit> theList = null;
		private List<Kit> theVisibleList = null;
		private String qFilter = null;

		/**
		 * Constructor
		 * @param aList
		 */
		public KitListModel(final List<Kit> aList)
		{
			theList = new ArrayList<Kit>(aList);
			theVisibleList = new ArrayList<Kit>();
			resetModel();
		}

		public void resetModel()
		{
			theVisibleList.clear();
			for (Kit aKit : theList)
			{
				if (qFilter == null
						|| (aKit.getDisplayName().toLowerCase().indexOf(
							qFilter) >= 0 || aKit.getType().toLowerCase()
							.indexOf(qFilter) >= 0))
				{
					theVisibleList.add(aKit);
				}
			}
		}

		public Object getElementAt(int index)
		{
			return theVisibleList.get(index);
		}

		public int getSize()
		{
			return theVisibleList.size();
		}

		/**
		 * Add an item to the list model
		 * @param anObj
		 */
		public void addItem(Kit aKit)
		{
			theList.add(aKit);
			if (qFilter == null
					|| (aKit.getDisplayName().toLowerCase().indexOf(
						qFilter) >= 0 || aKit.getType().toLowerCase()
						.indexOf(qFilter) >= 0))
			{
				theVisibleList.add(aKit);
				Collections.sort(theVisibleList);
			}
			Collections.sort(theList);
		}

		/**
		 * Remove an item from hte list model
		 * @param item
		 */
		public void removeItem(Object item)
		{
			theList.remove(item);
			theVisibleList.remove(item);
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
		
	}

	private class MyCellRenderer extends DefaultListCellRenderer
	{
		private MyCellRenderer()
		{
			// Empty Constructor
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			String dispString = value.toString();
			if (value instanceof Kit)
			{
				Kit kit = (Kit)value;
				if (!kitPassesPrereqs(kit))
				{
					dispString = "<html>" + SettingsHandler.getPrereqFailColorAsHtmlStart() + kit.getDisplayName()
						+ SettingsHandler.getPrereqFailColorAsHtmlStart() + "</html>";
				}
				else
				{
					dispString = "";
					if (kit.getSafe(ObjectKey.APPLY_MODE) == KitApply.INSTANT)
					{
						dispString = "<html><font color=\"" + SettingsHandler.getFeatVirtualColor() + "\">";
					}
					dispString += kit.getDisplayName();
					if (kit.getSafe(ObjectKey.APPLY_MODE) == KitApply.INSTANT)
					{
						dispString += "<html></font>";
					}
				}
			}

			super.getListCellRendererComponent(list, dispString, index, isSelected, cellHasFocus);

			return this;
		}
	}
}
