/*
 * KitSelector.java
 *
 *
 * @(#) $Id: KitSelector.java,v 1.65 2006/01/16 17:10:30 soulcatcher Exp $
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
 * version $Revision: 1.65 $
 */

/**
  * This class is responsible for the kit selection dialog.  It allows the user
  * to select a kit to be applied from a list of qualifying kits.
  *
  * @author  Greg Bingleman
  * @version $Revision: 1.65 $
  */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.PrereqHandler;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.Utility;

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

		this.setSize(new Dimension(640, 460));
		Utility.centerFrame(this, false);
	}

	/**
	 * Dismisses the dialog box.
	 */
	public void closeDialog()
	{
		//
		// TODO: save window size and position of scroll pane dividers
		//
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

		List thingsToAdd = new ArrayList();
		List warnings    = new ArrayList();
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

		if (theKit.getApplyMode() == Kit.APPLY_PERMANENT)
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

	private void addSelections(JList lst, List kits, List excluded, PlayerCharacter aPlayerCharacter)
	{
		if ((kits == null) || (kits.size() == 0))
		{
			return;
		}

		for (Iterator e = kits.iterator(); e.hasNext();)
		{
			final Kit aKit = (Kit) e.next();

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
		IconUtilitities.maybeSetIcon(this, "PcgenIcon.gif");

		lstAvailable = new JList(new KitListModel(new ArrayList()));
		lstAvailable.setCellRenderer(new MyCellRenderer());
		lstAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scpAvailable.setViewportView(lstAvailable);

		lstSelected = new JList(new KitListModel(new ArrayList()));
		lstSelected.setCellRenderer(new MyCellRenderer());
		lstSelected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scpSelected.setViewportView(lstSelected);

		final List pcKitInfo = aPC.getKitInfo();
		addSelections(lstAvailable, Globals.getKitInfo(), pcKitInfo, aPC);
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
		btnAdd.setEnabled(false);
		btnAdd.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnAddActionPerformed();
				}
			});

		pnlAvailable.add(btnAdd, new GridBagConstraints());

		lblAvailable.setText("Available");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlAvailable.add(lblAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlAvailable.add(scpAvailable, gridBagConstraints);

		spChoices.setLeftComponent(pnlAvailable);

		pnlSelected.setLayout(new GridBagLayout());

		pnlSelected.setBorder(new EmptyBorder(new Insets(1, 3, 1, 3)));
		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnRemoveActionPerformed();
				}
			});

		pnlSelected.add(btnRemove, new GridBagConstraints());

		lblSelected.setText("Selected");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSelected.add(lblSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlSelected.add(scpSelected, gridBagConstraints);

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
	}

	private boolean kitPassesPrereqs(Kit theKit)
	{
		return PrereqHandler.passesAll(theKit.getPreReqList(), aPC, null);
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

	private int showWarnings(List warnings)
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

			for (Iterator e = warnings.iterator(); e.hasNext();)
			{
				warningInfo.append((String) e.next()).append("<br>");
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

	private class KitListModel extends AbstractListModel
	{
		public KitListModel(final List aList)
		{
			theList = new ArrayList(aList);
		}
		public Object getElementAt(int index)
		{
			return theList.get(index);
		}
		public int getSize()
		{
			return theList.size();
		}
		public void addItem(Object anObj)
		{
			theList.add(anObj);
			Collections.sort(theList);
		}
		public void removeItem(Object item)
		{
			theList.remove(item);
		}
		private ArrayList theList = null;
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
					dispString = "<html>" + SettingsHandler.getPrereqFailColorAsHtmlStart() + kit.getName()
						+ SettingsHandler.getPrereqFailColorAsHtmlStart() + "</html>";
				}
				else
				{
					dispString = "";
					if (kit.getApplyMode() == Kit.APPLY_INSTANT)
					{
						dispString = "<html><font color=\"" + SettingsHandler.getFeatVirtualColor() + "\">";
					}
					dispString += kit.getName();
					if (kit.getApplyMode() == Kit.APPLY_INSTANT)
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
