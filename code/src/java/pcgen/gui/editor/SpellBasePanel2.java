/*
 * SpellBasePanel2.java
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
 * Created on January 21, 2003, 2:49 PM
 *
 * @(#) $Id: SpellBasePanel2.java,v 1.16 2005/10/18 20:23:42 binkley Exp $
 */
package pcgen.gui.editor;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.spell.Spell;
import pcgen.gui.utils.IconUtilitities;
import pcgen.util.PropertyFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>SpellBasePanel2</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.16 $
 */
public class SpellBasePanel2 extends JPanel implements PObjectUpdater
{
	static final long serialVersionUID = -4883465552783045888L;
	private JButton btnAddVariant;
	private JButton btnRemoveVariant;
	private JList lstVariants;
	private JPanel pnlVariants;
	private JScrollPane scpVariants;
	private JTextField txtVariant;
	private TypePanel pnlType;

	/** Creates new form SpellBasePanel2 */
	public SpellBasePanel2()
	{
		initComponents();
	}

	public void updateData(PObject thisPObject)
	{
		final Spell s = (Spell) thisPObject;

		Object[] sel = pnlType.getSelectedList();
		s.setTypeInfo(".CLEAR");
		s.setTypeInfo(EditUtil.delimitArray(sel, '.'));

		sel = ((JListModel) lstVariants.getModel()).getElements();
		s.clearVariants();
		for (int i=0 ; i<sel.length ; i++)
		{
			s.addVariant(sel[i].toString());
		}
	}

	public void updateView(PObject thisPObject)
	{
		Iterator e;
		String aString;

		final List variants = ((Spell) thisPObject).getVariants();

		if (variants.size() != 0)
		{
			((JListModel) lstVariants.getModel()).setData(variants);
		}

		//
		// Populate the types
		//
		List availableList = new ArrayList();
		List selectedList = new ArrayList();

		for (e = Globals.getSpellMap().values().iterator(); e.hasNext();)
		{
			final Object obj = e.next();

			if (obj instanceof List)
			{
				for (int i = 0; i < ((ArrayList) obj).size(); ++i)
				{
					EditUtil.addPObjectTypes((Spell) ((List) obj).get(i), availableList);
				}
			}
			else
			{
				EditUtil.addPObjectTypes((Spell) obj, availableList);
			}
		}

		//
		// remove this template's type from the available list and place into selected list
		//
		for (int i = thisPObject.getMyTypeCount(); i > 0;)
		{
			aString = thisPObject.getMyType(--i);

			if (!aString.equals(Constants.s_CUSTOM))
			{
				selectedList.add(aString);
				availableList.remove(aString);
			}
		}

		pnlType.setAvailableList(availableList, true);
		pnlType.setSelectedList(selectedList, true);
	}

	private void btnAddVariantActionPerformed()
	{
		btnAddVariant.setEnabled(false);

		final JListModel lmd = (JListModel) lstVariants.getModel();
		lmd.addElement(txtVariant.getText());
		txtVariant.setText("");
	}

	private void btnRemoveVariantActionPerformed()
	{
		btnRemoveVariant.setEnabled(false);

		final JListModel lms = (JListModel) lstVariants.getModel();
		final Object[] x = lstVariants.getSelectedValues();

		for (int i = 0; i < x.length; ++i)
		{
			txtVariant.setText(x[i].toString());
			lms.removeElement(x[i]);
		}

		txtVariantKeyReleased();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gbc;

		pnlType = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));
		pnlVariants = new JPanel();
		scpVariants = new JScrollPane();
		lstVariants = new JList(new JListModel(new ArrayList(), true));
		txtVariant = new JTextField();

		//
		// There's got to be a better/easier way to do this...
		//
		try
		{
			btnAddVariant = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
			btnRemoveVariant = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		}
		catch (Exception exc)
		{
			btnAddVariant = new JButton(">");
			btnRemoveVariant = new JButton("<");
		}

		btnRemoveVariant.setEnabled(false);

		setLayout(new GridBagLayout());

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 2, 2, 2);
		add(pnlType, gbc);

		pnlVariants.setLayout(new GridBagLayout());

		lstVariants.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					lstVariantsMouseClicked(evt);
				}
			});
		lstVariants.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				if (lstVariants.getSelectedIndex() >= 0)
				{
					lstVariants.ensureIndexIsVisible(lstVariants
						.getSelectedIndex());
				}
			}
		});
		pnlVariants.setBorder(new TitledBorder(PropertyFactory.getString("in_demVariants")));
		scpVariants.setViewportView(lstVariants);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 4;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.4;
		gbc.insets = new Insets(2, 2, 2, 2);
		pnlVariants.add(scpVariants, gbc);

		btnAddVariant.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnAddVariantActionPerformed();
				}
			});

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.insets = new Insets(2, 2, 2, 2);
		pnlVariants.add(btnAddVariant, gbc);

		btnRemoveVariant.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnRemoveVariantActionPerformed();
				}
			});

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.insets = new Insets(2, 2, 2, 2);
		pnlVariants.add(btnRemoveVariant, gbc);

		txtVariant.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent evt)
				{
					super.keyReleased(evt);
					txtVariantKeyReleased();
				}
			});

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		pnlVariants.add(txtVariant, gbc);

		gbc = new GridBagConstraints();
		gbc.gridy = 2;
		gbc.gridwidth = 5;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(2, 2, 2, 2);
		add(pnlVariants, gbc);
	}

	private void lstVariantsMouseClicked(MouseEvent evt)
	{
		if (EditUtil.isDoubleClick(evt, lstVariants, btnRemoveVariant))
		{
			btnRemoveVariantActionPerformed();
		}
	}

	private void txtVariantKeyReleased()
	{
		btnAddVariant.setEnabled(txtVariant.getText().trim().length() != 0);
	}
}
