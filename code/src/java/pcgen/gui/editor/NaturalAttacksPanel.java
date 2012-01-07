/*
 * NaturalAttacksPanel.java Copyright 2003 (C) Richard Askham <raskham@users.
 * sourceforge. net>
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
 * Created on January 27, 2003, 09:14 AM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>NaturalAttacksPanel</code>
 *
 * The NaturalAttacksPanel class provides an interface allowing the user to
 * select and configure the various natural weapons available for a race.
 *
 * @author  Richard Askham <raskham@users.sourceforge.net>
 * @version $Revision$
 */
final class NaturalAttacksPanel extends JPanel
{
	static final long serialVersionUID = 7072891779087323350L;
	private JButton btnAdd;
	private JButton btnRemove;
	private JCheckBox chbBludgeoning;
	private JCheckBox chbPiercing;
	private JCheckBox chbSlashing;
	private JCheckBox chbWeaponPrimary;
	private JComboBoxEx cmbDamageDie;
	private JComboBoxEx cmbHandsRequired;
	private JComboBoxEx cmbNumDice;
	private JComboBoxEx cmbNumNaturalAttacks;
	private JLabel lblSelected;
	private JList lstSelected;
	private JPanel pnlAddRemove;
	private JPanel pnlAvailable;
	private JPanel pnlSelected;
	private JScrollPane scpSelected;
	private JTextField txtNaturalAttackName;

	/**
	 * Creates a new NaturalAttacksPanel
	 */
	NaturalAttacksPanel()
	{
		super();
		initComponents();
		initComponentContents();
	}

	/**
	 * Returns an arraylist containing all the natural weapons.  Each natural
	 * weapon is stored in the list as a type Equipment.
	 *
	 * @return ArrayList The natural weapons.
	 */
	public List<Equipment> getNaturalWeapons()
	{
		final JListModel lmd = (JListModel) lstSelected.getModel();
		List<Equipment> naturalWeapons = new ArrayList<Equipment>();
		Equipment anEquip;

		for (int i = 0, x = lmd.getSize(); i < x; ++i)
		{
			anEquip = new Equipment();

			String aString = (String) lmd.getElementAt(i);
			final StringTokenizer natWpn = new StringTokenizer(aString, ",", false);

			// Set the name
			anEquip.setName(natWpn.nextToken());

			// Set the weapon type
			for (String s : natWpn.nextToken().split("\\."))
			{
				anEquip.addToListFor(ListKey.TYPE, Type.getConstant(s));
			}

			// Set the number of attacks
			String attacksTxt = natWpn.nextToken();
			int anInt;

			try
			{
				anInt = Integer.parseInt(attacksTxt);
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("Natural Weapons Editor: non-numeric value for number of attacks: '" + attacksTxt
				    + "'");
				anInt = 1;
			}

			if (anInt > 1)
			{
				String bonusString = "WEAPON|ATTACKS|" + (anInt - 1);
				final BonusObj aBonus = Bonus.newBonus(Globals.getContext(), bonusString);
				
				if (aBonus != null)
				{
					anEquip.addToListFor(ListKey.BONUS, aBonus);
				}
			}

			// Set the damage dice
			EquipmentHead head = anEquip.getEquipmentHead(1);
			head.put(StringKey.DAMAGE, natWpn.nextToken());

			// Set the number of hands
			String handsTxt = natWpn.nextToken();
			int hands;

			try
			{
				hands = Integer.parseInt(handsTxt);
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("Natural Weapons Editor: non-numeric value for number of hands: '" + handsTxt + "'");
				hands = 0;
			}

			anEquip.put(IntegerKey.SLOTS, hands);

			// Set attacks progress - for now, always set to false, need to get more info on attacks progress
			anEquip.put(ObjectKey.ATTACKS_PROGRESS, false);

			naturalWeapons.add(anEquip);
		}

		return naturalWeapons;
	}

	/**
	 * Sets the selected natural weapons values to the supplied values.
	 * @param argSelected A list of string  values.
	 */
	void setSelectedList(List argSelected)
	{
		for (int i = 0, x = argSelected.size(); i < x; ++i)
		{
			boolean weaponPrimary = false;
			String naturalAttacksTxt = "";

			String naturalWeaponName = ((Equipment) argSelected.get(i)).getSimpleName();
			naturalAttacksTxt += (naturalWeaponName + ",");

			String naturalWeaponModifiedName = ((Equipment) argSelected.get(i)).modifiedName();

			if (naturalWeaponModifiedName.indexOf("Primary") >= 0)
			{
				weaponPrimary = true;
			}

			String naturalWeaponType = ((Equipment) argSelected.get(i)).getType();
			final StringTokenizer wpnType = new StringTokenizer(naturalWeaponType, ".", true);
			naturalWeaponType = "";

			String thisToken;

			while (wpnType.hasMoreTokens())
			{
				thisToken = wpnType.nextToken();
				naturalWeaponType += (thisToken.substring(0, 1).toUpperCase() + thisToken.substring(1).toLowerCase());
			}

			naturalAttacksTxt += (naturalWeaponType + ",");

			int numAttacks = 1 + (int) ((Equipment) argSelected.get(i)).bonusTo(null, "WEAPON", "ATTACKS", true);
			naturalAttacksTxt += (numAttacks + ",");

			String damage = ((Equipment) argSelected.get(i)).getDamage(null);
			naturalAttacksTxt += (damage + ",");

			int numHands = ((Equipment) argSelected.get(i)).getHands(null);
			naturalAttacksTxt += numHands;

			addToSelectedLst(naturalAttacksTxt, weaponPrimary);
		}
	}

	//
	// Enable the associated button if single click.
	// Return if true if double click on JList and the associated button is enabled.
	//
	private static boolean isDoubleClick(MouseEvent evt, JList lst, JButton btn)
	{
		if (lst.getMinSelectionIndex() >= 0)
		{
			switch (evt.getClickCount())
			{
				case 1:
					btn.setEnabled(true);

					break;

				case 2:

					if (btn.isEnabled())
					{
						return true;
					}

					break;

				default:
					break;
			}
		}

		return false;
	}

	/**
	 * Adds the specified natural weapon to the selected list. If the weapon is
	 * a primary weapon, then it is added to the beginning of the list.
	 *
	 * @param naturalWeapon A string containing the comma delimited parameters
	 * of the weapon.
	 * @param isPrimary A boolean indicating whether this weapon is a primary
	 * weapon
	 */
	private void addToSelectedLst(String naturalWeapon, boolean isPrimary)
	{
		final JListModel lmd = (JListModel) lstSelected.getModel();

		//boolean elementAdded = false;
		int addPosition = 0;

		for (int i = 0, x = lmd.getSize(); i < x; ++i)
		{
			//String weaponName = naturalWeapon.substring(0, naturalWeapon.indexOf(","));
			Object obj = lmd.getElementAt(i);

			if (((String) obj).startsWith(naturalWeapon.substring(0, naturalWeapon.indexOf(","))))
			{
				lmd.removeElement(obj);
				addPosition = i;

				break;
			}
		}

		if (isPrimary)
		{
			// if this weapon is primary, it needs to go at the beginning of the list
			lmd.addElement(0, naturalWeapon);
		}
		else
		{
			// if addPosition is not zero, then this is a an existing item, so add at the right place
			if (addPosition != 0)
			{
				lmd.addElement(addPosition, naturalWeapon);
			}

			// otherwise put it at the end
			else
			{
				lmd.addElement(naturalWeapon);
			}
		}
	}

	/**
	 * Adds the specified natural weapon to the selected list. If the weapon is
	 * a primary weapon, then it is added to the beginning of the list.
	 */
	private void btnAddActionPerformed()
	{
		String naturalAttacksString = txtNaturalAttackName.getText();

		if (naturalAttacksString.length() == 0)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_demNatWeaponNoName"),
				Constants.APPLICATION_NAME,
				MessageType.ERROR);

			return;
		}

		naturalAttacksString += ",Weapon.Natural.Melee";

		boolean boolTypeEntered = false;

		if (chbPiercing.isSelected())
		{
			naturalAttacksString += ".Piercing";
			boolTypeEntered = true;
		}

		if (chbSlashing.isSelected())
		{
			naturalAttacksString += ".Slashing";
			boolTypeEntered = true;
		}

		if (chbBludgeoning.isSelected())
		{
			naturalAttacksString += ".Bludgeoning";
			boolTypeEntered = true;
		}

		if (!boolTypeEntered)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("" +
				"in_demNatWeaponNoType"),
				Constants.APPLICATION_NAME,
				MessageType.ERROR);

			return;
		}

		naturalAttacksString += ("," + cmbNumNaturalAttacks.getSelectedItem());
		naturalAttacksString += ("," + (String) cmbNumDice.getSelectedItem() + cmbDamageDie.getSelectedItem());
		naturalAttacksString += ("," + cmbHandsRequired.getSelectedItem());

		addToSelectedLst(naturalAttacksString, chbWeaponPrimary.isSelected());
	}

	/**
	 * Removes the selected natural weapon from the selected list.
	 */
	private void btnRemoveActionPerformed()
	{
		btnRemove.setEnabled(false);

		final JListModel lms = (JListModel) lstSelected.getModel();
		final Object[] x = lstSelected.getSelectedValues();

		for (int i = 0; i < x.length; ++i)
		{
			lms.removeElement(x[i]);
		}
	}

	private void initComponentContents()
	{
	    // TODO This method currently does nothing?
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;
		JLabel lblTemp;
		JPanel aPanel;

		//
		// There's got to be a better/easier way to do this...
		//
		try
		{
			btnAdd = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
			btnRemove = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		}
		catch (Exception exc)
		{
			btnAdd = new JButton(">");
			btnRemove = new JButton("<");
		}

		lblSelected = new JLabel(PropertyFactory.getString("in_selected"));
		lstSelected = new JList(new JListModel(new ArrayList(), false));
		pnlAvailable = new JPanel();
		pnlAddRemove = new JPanel();
		pnlSelected = new JPanel();
		scpSelected = new JScrollPane();

		txtNaturalAttackName = new JTextField();
		txtNaturalAttackName.setMaximumSize(new Dimension(100, 21));
		txtNaturalAttackName.setPreferredSize(new Dimension(100, 21));

		cmbNumNaturalAttacks = new JComboBoxEx(new String[]
			    {
				    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
			    });
		cmbNumNaturalAttacks.setMaximumSize(new Dimension(21, 21));
		cmbNumNaturalAttacks.setPreferredSize(new Dimension(21, 21));

		cmbNumDice = new JComboBoxEx(new String[]{ "1", "2", "3", "4" });
		cmbNumDice.setMaximumSize(new Dimension(21, 21));
		cmbNumDice.setPreferredSize(new Dimension(21, 21));

		cmbDamageDie = new JComboBoxEx(new String[]{ "d1", "d2", "d3", "d4", "d6", "d8", "d10" });
		cmbDamageDie.setMaximumSize(new Dimension(65, 21));
		cmbDamageDie.setPreferredSize(new Dimension(65, 21));
		cmbDamageDie.setMinimumSize(new Dimension(65, 21));

		cmbHandsRequired = new JComboBoxEx(new String[]
			    {
				    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
			    });
		cmbHandsRequired.setMaximumSize(new Dimension(21, 21));
		cmbHandsRequired.setPreferredSize(new Dimension(21, 21));

		chbPiercing = new JCheckBox();
		chbBludgeoning = new JCheckBox();
		chbSlashing = new JCheckBox();
		chbWeaponPrimary = new JCheckBox();

		// Layout the available panel
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_demTag"));
		title1.setTitleJustification(TitledBorder.LEFT);
		pnlAvailable.setBorder(title1);
		pnlAvailable.setLayout(new GridBagLayout());

		// Here we have the natural weapon name
		lblTemp = new JLabel(PropertyFactory.getString("in_demNatWeaponName"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		aPanel = new JPanel();
		aPanel.add(txtNaturalAttackName);
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		txtNaturalAttackName.setPreferredSize(new Dimension(100, 21));
		txtNaturalAttackName.setMinimumSize(new Dimension(100, 21));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(aPanel, gridBagConstraints);

		// Next we have whether the natural weapon is primary or secondary
		lblTemp = new JLabel(PropertyFactory.getString("in_demWeaponPrimary"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(chbWeaponPrimary, gridBagConstraints);

		// Next we have some check boxes indicating the weapon type ie. slashing/piercing/bludgeoning
		lblTemp = new JLabel(PropertyFactory.getString("in_demWeaponBludgeoning"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(chbBludgeoning, gridBagConstraints);

		lblTemp = new JLabel(PropertyFactory.getString("in_demWeaponPiercing"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(chbPiercing, gridBagConstraints);

		lblTemp = new JLabel(PropertyFactory.getString("in_demWeaponSlashing"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(chbSlashing, gridBagConstraints);

		// Now add the number of attacks
		lblTemp = new JLabel(PropertyFactory.getString("in_demNatWeaponNumAttacks"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		aPanel = new JPanel();
		aPanel.add(cmbNumNaturalAttacks);
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cmbNumNaturalAttacks.setPreferredSize(new Dimension(35, 21));
		cmbNumNaturalAttacks.setMinimumSize(new Dimension(35, 21));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(aPanel, gridBagConstraints);

		// And the number of hands used/required
		lblTemp = new JLabel(PropertyFactory.getString("in_demNatWeaponNumHands"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		aPanel = new JPanel();
		aPanel.add(cmbHandsRequired);
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cmbHandsRequired.setPreferredSize(new Dimension(35, 21));
		cmbHandsRequired.setMinimumSize(new Dimension(35, 21));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(aPanel, gridBagConstraints);

		// And lastly, the damage dealt
		lblTemp = new JLabel(PropertyFactory.getString("in_demWeaponDamage"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 13;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		aPanel = new JPanel();
		aPanel.add(cmbNumDice);
		aPanel.add(cmbDamageDie);
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cmbNumDice.setPreferredSize(new Dimension(35, 21));
		cmbNumDice.setMinimumSize(new Dimension(35, 21));
		cmbDamageDie.setPreferredSize(new Dimension(45, 21));
		cmbDamageDie.setMinimumSize(new Dimension(45, 21));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 14;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlAvailable.add(aPanel, gridBagConstraints);

		// Layout the add/remove panel
		pnlAddRemove.setLayout(new GridBagLayout());

		btnAdd.setEnabled(true);
		btnAdd.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					btnAddActionPerformed();
				}
			});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAddRemove.add(btnAdd, gridBagConstraints);

		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					btnRemoveActionPerformed();
				}
			});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlAddRemove.add(btnRemove, gridBagConstraints);

		// Layout the selected panel
		pnlSelected.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSelected.add(lblSelected, gridBagConstraints);

		lstSelected.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent evt)
				{
					lstSelectedMouseClicked(evt);
				}
			});
		lstSelected.addListSelectionListener(new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent evt)
				{
					if (lstSelected.getSelectedIndex() >= 0)
					{
						lstSelected.ensureIndexIsVisible(lstSelected
							.getSelectedIndex());
					}
				}
			});
		scpSelected.setPreferredSize(new Dimension(90, 20));
		scpSelected.setViewportView(lstSelected);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.weighty = 1.0;
		pnlSelected.add(scpSelected, gridBagConstraints);

		this.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAddRemove, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlSelected, gridBagConstraints);
	}

	/**
	 * Used to detect when an item in the selected list is selected.  When this
	 * happens, the information for that item is loaded into the left hand panel
	 * where it can be edited.  If the item in the selected list is double-
	 * clicked, it will be removed.
	 * @param evt
	 */
	private void lstSelectedMouseClicked(MouseEvent evt)
	{
		if (evt.getSource().equals(lstSelected))
		{
			final String aString = (String) lstSelected.getSelectedValue();

			final StringTokenizer natWpn = new StringTokenizer(aString, ",", false);

			// Set the name
			txtNaturalAttackName.setText(natWpn.nextToken());

			// Set the primary/secondart selection
			if (lstSelected.getSelectedIndex() == 0)
			{
				chbWeaponPrimary.setSelected(true);
			}
			else
			{
				chbWeaponPrimary.setSelected(false);
			}

			// Set the weapon type
			String weaponTypeTxt = natWpn.nextToken();

			if (weaponTypeTxt.indexOf("Slashing") != -1)
			{
				chbSlashing.setSelected(true);
			}
			else
			{
				chbSlashing.setSelected(false);
			}

			if (weaponTypeTxt.indexOf("Piercing") != -1)
			{
				chbPiercing.setSelected(true);
			}
			else
			{
				chbPiercing.setSelected(false);
			}

			if (weaponTypeTxt.indexOf("Bludgeoning") != -1)
			{
				chbBludgeoning.setSelected(true);
			}
			else
			{
				chbBludgeoning.setSelected(false);
			}

			// Set the number of attacks
			String numAttacks = natWpn.nextToken().trim();
			cmbNumNaturalAttacks.setSelectedItem(numAttacks);

			// Set the damage dice
			String damageDice = natWpn.nextToken();
			cmbNumDice.setSelectedItem(damageDice.substring(0, 1));
			cmbDamageDie.setSelectedItem(damageDice.substring(1));

			// Set the number of hands
			String handsReq = natWpn.nextToken().trim();
			cmbHandsRequired.setSelectedItem(handsReq);

			if (isDoubleClick(evt, lstSelected, btnRemove))
			{
				btnRemoveActionPerformed();
			}
		}
	}
}
