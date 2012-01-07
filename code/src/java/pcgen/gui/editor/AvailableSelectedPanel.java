/*
 * AvailableSelectedPanel.java
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
 * Created on October 8, 2002, 4:15 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.gui.utils.IconUtilitities;
import pcgen.util.PropertyFactory;

/**
 * <code>AvailableSelectedPanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
class AvailableSelectedPanel extends JPanel
{
	static final long serialVersionUID = -647497427782936111L;
	private EditorAddFilter addFilter = null;
	private JButton btnAdd;
	private JButton btnAdd2 = null;
	private JButton btnRemove;
	private JButton btnRemove2;
	private JLabel lblAvailable;
	private JLabel lblHeader;
	private JLabel lblSelected;
	private JLabel lblSelected2 = null;
	private JList lstAvailable;
	private JList lstSelected;
	private JList lstSelected2 = null;
	private JPanel pnlAvailable;
	private JPanel pnlExtra;
	private JPanel pnlHeader;
	private JPanel pnlSelected;
	private JPanel pnlSelected2;
	private JScrollPane scpAvailable;
	private JScrollPane scpSelected;
	private JScrollPane scpSelected2;

	/** Creates new form AvailableSelectedPanel */
	AvailableSelectedPanel()
	{
		this(false);
	}

	AvailableSelectedPanel(boolean twoSelectedLists)
	{
		super();
		initComponents(twoSelectedLists);
	}

	final void setAddFilter(EditorAddFilter eaf)
	{
		addFilter = eaf;
	}

	void setAvailableList(Collection<?> argAvailable, boolean argSort)
	{
		final JListModel lmd = (JListModel) lstAvailable.getModel();
		lmd.setSort(argSort);

		for (Object o : argAvailable)
		{
			lmd.addElement(o);
		}
	}

	Object[] getAvailableList()
	{
		return ((JListModel) lstAvailable.getModel()).getElements();
	}

	void setExtraLayout(LayoutManager manager)
	{
		pnlExtra.setLayout(manager);
	}

	void setHeader(String argHeader)
	{
		lblHeader.setText(argHeader);
		pnlHeader.setVisible(argHeader.length() != 0);
	}

	void setLblSelected2Text(String txt)
	{
		if (lblSelected2 != null)
		{
			lblSelected2.setText(txt);
		}
	}

	void setLblSelectedText(String txt)
	{
		lblSelected.setText(txt);
	}

	void setSelectedList(List argSelected, boolean argSort)
	{
		final JListModel lmd = (JListModel) lstSelected.getModel();
		lmd.setSort(argSort);

		if (argSelected == null)
		{
			return;
		}

		for (int i = 0, x = argSelected.size(); i < x; ++i)
		{
			lmd.addElement(argSelected.get(i));
		}
	}

	Object[] getSelectedList()
	{
		return ((JListModel) lstSelected.getModel()).getElements();
	}

	void setSelectedList2(List argSelected, boolean argSort)
	{
		final JListModel lmd = (JListModel) lstSelected2.getModel();
		lmd.setSort(argSort);

		for (int i = 0, x = argSelected.size(); i < x; ++i)
		{
			lmd.addElement(argSelected.get(i));
		}
	}

	Object[] getSelectedList2()
	{
		if (lstSelected2 != null)
		{
			return ((JListModel) lstSelected2.getModel()).getElements();
		}

		return null;
	}

	void addExtra(Component comp, Object constraints)
	{
		pnlExtra.setVisible(true);
		pnlExtra.add(comp, constraints);
	}

	void addItemToSelected(final Object obj)
	{
		final JListModel lmd = (JListModel) lstSelected.getModel();
		final JListModel lms = (JListModel) lstAvailable.getModel();

		if (addFilter != null)
		{
			lmd.addElement(addFilter.encode(obj), false);
		}
		else
		{
			lmd.addElement(obj, false);
		}

		lms.removeElement(obj);
	}

	private void btnAddActionPerformed(JList lst, JButton btn)
	{
		swapEntries(lst, lstAvailable, btn, 1);

		//
		// Make sure both add buttons are disabled
		//
		if (btnAdd2 != null)
		{
			btnAdd.setEnabled(false);
			btnAdd2.setEnabled(false);
		}
	}

	private void btnRemoveActionPerformed(JList lst, JButton btn)
	{
		swapEntries(lstAvailable, lst, btn, -1);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * @param twoSelectedLists
	 */
	private void initComponents(boolean twoSelectedLists)
	{
		GridBagConstraints gridBagConstraints;

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

		lblAvailable = new JLabel();
		lblHeader = new JLabel();
		lblSelected = new JLabel();
		lstAvailable = new JList(new JListModel(new ArrayList(), true));
		lstSelected = new JList(new JListModel(new ArrayList(), true));
		pnlAvailable = new JPanel();
		pnlExtra = new JPanel();
		pnlHeader = new JPanel();
		pnlSelected = new JPanel();
		scpAvailable = new JScrollPane();
		scpSelected = new JScrollPane();

		if (twoSelectedLists)
		{
			try
			{
				btnAdd2 = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
				btnRemove2 = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
			}
			catch (Exception exc)
			{
				btnAdd2 = new JButton();
				btnRemove2 = new JButton();
			}

			lblSelected2 = new JLabel();
			lstSelected2 = new JList(new JListModel(new ArrayList(), true));
			pnlSelected2 = new JPanel();
			scpSelected2 = new JScrollPane();
		}

		setLayout(new GridBagLayout());

		pnlHeader.add(lblHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(pnlHeader, gridBagConstraints);
		pnlHeader.setVisible(false);

		pnlAvailable.setLayout(new GridBagLayout());

		lblAvailable.setText(PropertyFactory.getString("in_available"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlAvailable.add(lblAvailable, gridBagConstraints);

		btnAdd.setEnabled(false);
		btnAdd.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					btnAddActionPerformed(lstSelected, btnAdd);
				}
			});

		if (!twoSelectedLists)
		{
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			pnlAvailable.add(btnAdd, gridBagConstraints);
		}

		lstAvailable.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent evt)
				{
					lstAvailableMouseClicked(evt);
				}
			});
		lstAvailable.addListSelectionListener(new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent evt)
				{
					if (lstAvailable.getSelectedIndex() >= 0)
					{
						lstAvailable.ensureIndexIsVisible(lstAvailable.getSelectedIndex());
					}
				}
			});
		scpAvailable.setPreferredSize(new Dimension(90, 20));
		scpAvailable.setViewportView(lstAvailable);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		pnlAvailable.add(scpAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		pnlAvailable.add(pnlExtra, gridBagConstraints);
		pnlExtra.setVisible(false);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;

		if (twoSelectedLists)
		{
			gridBagConstraints.gridheight = 2;
		}

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		add(pnlAvailable, gridBagConstraints);

		pnlSelected.setLayout(new GridBagLayout());

		if (twoSelectedLists)
		{
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			pnlSelected.add(btnAdd, gridBagConstraints);
		}

		lblSelected.setText(PropertyFactory.getString("in_selected"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSelected.add(lblSelected, gridBagConstraints);

		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					btnRemoveActionPerformed(lstSelected, btnRemove);
				}
			});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlSelected.add(btnRemove, gridBagConstraints);

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
						lstSelected.ensureIndexIsVisible(lstSelected.getSelectedIndex());
					}
				}
			});
		scpSelected.setPreferredSize(new Dimension(90, 20));
		scpSelected.setViewportView(lstSelected);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;

		if (twoSelectedLists)
		{
			gridBagConstraints.gridwidth = 3;
		}
		else
		{
			gridBagConstraints.gridwidth = 2;
		}

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		pnlSelected.add(scpSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		add(pnlSelected, gridBagConstraints);

		if (twoSelectedLists)
		{
			pnlSelected2.setLayout(new GridBagLayout());

			btnAdd2.setEnabled(false);
			btnAdd2.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						btnAddActionPerformed(lstSelected2, btnAdd2);
					}
				});

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			pnlSelected2.add(btnAdd2, gridBagConstraints);

			lblSelected2.setText(PropertyFactory.getString("in_selected"));
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			pnlSelected2.add(lblSelected2, gridBagConstraints);

			btnRemove2.setEnabled(false);
			btnRemove2.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						btnRemoveActionPerformed(lstSelected2, btnRemove2);
					}
				});

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			pnlSelected2.add(btnRemove2, gridBagConstraints);

			lstSelected2.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent evt)
					{
						lstSelectedMouseClicked(evt);
					}
				});
			scpSelected2.setPreferredSize(new Dimension(90, 20));
			scpSelected2.setViewportView(lstSelected2);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridwidth = 3;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.weightx = 0.5;
			gridBagConstraints.weighty = 1.0;
			pnlSelected2.add(scpSelected2, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.weightx = 0.5;
			gridBagConstraints.weighty = 1.0;
			add(pnlSelected2, gridBagConstraints);
		}
	}

	//
	// Mouse click on available list
	//
	private void lstAvailableMouseClicked(MouseEvent evt)
	{
		if (EditUtil.isDoubleClick(evt, lstAvailable, btnAdd) && (btnAdd2 == null))
		{
			btnAddActionPerformed(lstSelected, btnAdd);
		}

		if (btnAdd2 != null)
		{
			btnAdd2.setEnabled(btnAdd.isEnabled());
		}
	}

	//
	// Mouse click on selected list
	//
	private void lstSelectedMouseClicked(MouseEvent evt)
	{
		if (evt.getSource().equals(lstSelected))
		{
			if (EditUtil.isDoubleClick(evt, lstSelected, btnRemove))
			{
				btnRemoveActionPerformed(lstSelected, btnRemove);
			}
		}
		else
		{
			if (EditUtil.isDoubleClick(evt, lstSelected2, btnRemove2))
			{
				btnRemoveActionPerformed(lstSelected2, btnRemove2);
			}
		}
	}

	/**
	 * Remove selected objects from src and insert it in dst.
	 * @param dst
	 * @param src
	 * @param btn
	 * @param dir
	 */
	private void swapEntries(JList dst, JList src, JButton btn, int dir)
	{
		btn.setEnabled(false);

		final JListModel lms = (JListModel) src.getModel();
		final JListModel lmd = (JListModel) dst.getModel();
		final Object[] selectedValues = src.getSelectedValues();

		for (int i = 0; i < selectedValues.length; ++i)
		{
			if (addFilter != null)
			{
				if (dir == 1)
				{
					lmd.addElement(addFilter.encode(selectedValues[i]));
					lms.removeElement(selectedValues[i]);
				}
				else
				{
					lmd.addElement(addFilter.decode(selectedValues[i]));
					lms.removeElement(selectedValues[i]);
				}
			}
			else
			{
				lmd.addElement(selectedValues[i]);
				lms.removeElement(selectedValues[i]);
			}
		}
	}
}
