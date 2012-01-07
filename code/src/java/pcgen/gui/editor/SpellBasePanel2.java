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
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.gui.utils.IconUtilitities;
import pcgen.util.PropertyFactory;

/**
 * <code>SpellBasePanel2</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public class SpellBasePanel2 extends JPanel implements PObjectUpdater<Spell>
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

	@Override
	public void updateData(Spell s)
	{
		Object[] sel = pnlType.getSelectedList();
		s.removeListFor(ListKey.TYPE);
		for (Object o : sel)
		{
			s.addToListFor(ListKey.TYPE, Type.getConstant(o.toString()));
		}

		sel = ((JListModel) lstVariants.getModel()).getElements();
		s.removeListFor(ListKey.VARIANTS);
		for (int i=0 ; i<sel.length ; i++)
		{
			s.addToListFor(ListKey.VARIANTS, sel[i].toString());
		}
	}

	@Override
	public void updateView(Spell thisPObject)
	{
		List<String> variants = thisPObject.getListFor(ListKey.VARIANTS);
		if (variants != null)
		{
			((JListModel) lstVariants.getModel()).setData(variants);
		}

		//
		// Populate the types
		//
		List<Type> availableList = new ArrayList<Type>();
		List<Type> selectedList = new ArrayList<Type>();

		for (Iterator e = Globals.getSpellMap().values().iterator(); e.hasNext();)
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
		for (Type type : thisPObject.getTrueTypeList(false))
		{
			if (!type.equals(Type.CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
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
				@Override
				public void mouseClicked(MouseEvent evt)
				{
					lstVariantsMouseClicked(evt);
				}
			});
		lstVariants.addListSelectionListener(new ListSelectionListener()
		{
			@Override
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
				@Override
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
				@Override
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
				@Override
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
