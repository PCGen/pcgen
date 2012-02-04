/**
 * AddSpecialAbility.java
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com>
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
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 **/
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.gui.tabs.InfoSpecialAbilities;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.TableSorter;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * Title:        AddSpecialAbility.java
 * Description:  New GUI implementation for panel to add special abilities
 * Copyright:    Copyright (c) 2003
 * Company:
 *
 * @author Bryan McRoberts <merton.monk@codemonkeypublishin.com>
 * @version $Revision$
 */
public final class AddSpecialAbility extends JFrame
{
	static final long serialVersionUID = 8632071234484774756L;
	private GridBagLayout gridBagLayout = new GridBagLayout();
	private InfoSpecialAbilities owner;
	private JButton okButton;
	private JPanel saPanel;
	private JScrollPane saPane;
	private JTableEx saTable;
	private JTextField saField = new JTextField();
	private PlayerCharacter aPC = null;
	private SAModel saModel = new SAModel();
	private TableSorter saSorter = null;

	/**
	 * Constructor
	 */
	public AddSpecialAbility()
	{
		super();

		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
		}
	}

	/**
	 * @param pc
	 * @param ia
	 */
	public AddSpecialAbility(PlayerCharacter pc, InfoSpecialAbilities ia)
	{
		super();

		try
		{
			aPC = pc;
			setTitle(myGetTitle());
			IconUtilitities.maybeSetIcon(this,
				IconUtilitities.RESOURCE_APP_ICON);

			Toolkit kit = Toolkit.getDefaultToolkit();

			// since the Toolkit.getScreenSize() method is broken in the Linux implementation
			// of Java 5  (it returns double the screen size under xinerama), this method is
			// encapsulated to accomodate this with a hack.
			// TODO: remove the hack, once Java fixed this.
			// Dimension screenSize = kit.getScreenSize();
			Dimension screenSize = Utility.getScreenSize(kit);
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;

			// center frame in screen
			setSize(screenWidth >> 1, screenHeight >> 1);
			setLocation(screenWidth >> 2, screenHeight >> 2);

			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			Container contentPane = getContentPane();
			jbInit();
			contentPane.add(saPanel);
			setVisible(true);
			owner = ia;
		}
		catch (HeadlessException e)
		{
			Logging.errorPrint("This gui cannot run headless. D-oh.", e);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
		}
	}

	private static String myGetTitle()
	{
		String title = LanguageBundle.getString("in_addSA");

		return title;
	}

	private void addSpecialAbility()
	{
		if ((aPC == null) || !aPC.hasClass())
		{
			return;
		}

		String selectedValue = saField.getText();

		if (selectedValue != null)
		{
			PCClass aClass = aPC.getClassList().get(0);
			String aString = (selectedValue).trim();
			SpecialAbility sa = new SpecialAbility(aString);
			aPC.addAssoc(aClass, AssociationListKey.SPECIAL_ABILITY, sa);

			if (owner != null)
			{
				owner.forceRefresh();
			}
		}
	}

	private void jbInit() throws Exception
	{
		saPanel = new JPanel();
		saPane = new JScrollPane();

		JLabel saLabel = new JLabel("Special Ability:");
		JPanel top = new JPanel();

		GridBagConstraints c;
		saPanel.setLayout(gridBagLayout);

		//
		// Hit points per level
		//
		saSorter = new TableSorter(saModel);

//		saTable.setModel(saModel);
		saTable = new JTableEx(saSorter);
		saSorter.addMouseListenerToHeaderInTable(saTable);
		saTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		saTable.setDoubleBuffered(false);
		saPane.setViewportView(saTable);
		top.setLayout(new BorderLayout());
		top.add(saLabel, BorderLayout.WEST);
		top.add(saField, BorderLayout.CENTER);

		saPanel.add(top,
				new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

		saPanel.add(saPane,
				new GridBagConstraints(0, 1, 1, 7, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

		saTable.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					final ListSelectionModel lsm = saTable.getSelectionModel();
					final int iRow = saSorter.getRowTranslated(lsm.getMinSelectionIndex());

					if (iRow >= 0)
					{
						AddSpecialAbility.this.saField.setText((String) saModel.getValueAt(iRow, 0));
					}

					if (evt.getClickCount() == 2)
					{
						AddSpecialAbility.this.okButton.doClick();
					}
				}
			});

		okButton = new JButton();
		okButton.setText(LanguageBundle.getString("in_ok"));
		okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok"));
		okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addSpecialAbility();
					AddSpecialAbility.this.setVisible(false);
				}
			});

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton, c);

		saPanel.add(buttonPanel,
				new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
	}

	static final class SAModel extends AbstractTableModel
	{
		public boolean isCellEditable(int rowIndex, int colIndex)
		{
			return false;
		}

		public Class<?> getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public int getColumnCount()
		{
			return 2;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Ability";

				case 1:
					return "Source";

				default:
					break;
			}

			return "Out Of Bounds";
		}

		public int getRowCount()
		{
			return Globals.getSASet().size();
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (rowIndex < 0)
			{
				return null;
			}

			SpecialAbility sa = (SpecialAbility) Globals.getSASet().toArray()[rowIndex];

			if (sa == null)
			{
				return null;
			}

			switch (columnIndex)
			{
				case 0: // Name
					return sa.getDisplayName();

				case 1: // Source
					return "";

				default:
					break;
			}

			return null;
		}
	}
}
