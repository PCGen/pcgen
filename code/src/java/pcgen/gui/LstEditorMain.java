/*
 * LstEditorMain.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 4, 2002, 9:19 AM
 *
 * @(#) $Id$
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.CustomData;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.analysis.PCClassKeyChange;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.editor.EditorConstants;
import pcgen.gui.editor.EditorMainForm;
import pcgen.gui.utils.IconUtilitities;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>LstEditorMain</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class LstEditorMain extends JFrame
{
	static final long serialVersionUID = 9007333328425546533L;
	private static final String s_EDITTYPE_CLASS = "Class";
	private static final String s_EDITTYPE_DEITY = "Deity";
	private static final String s_EDITTYPE_DOMAIN = "Domain";
	private static final String s_EDITTYPE_FEAT = "Feat";
	private static final String s_EDITTYPE_LANGUAGE = "Language";
	private static final String s_EDITTYPE_RACE = "Race";
	private static final String s_EDITTYPE_SKILL = "Skill";
	private static final String s_EDITTYPE_SPELL = "Spell";
	private static final String s_EDITTYPE_TEMPLATE = "Template";
	private static final String s_EDITTYPE_SOURCE = "Source";
	private static final String[] supportedLsts =
	{
		s_EDITTYPE_CLASS, s_EDITTYPE_DEITY, s_EDITTYPE_DOMAIN, s_EDITTYPE_FEAT, s_EDITTYPE_LANGUAGE, s_EDITTYPE_RACE,
		s_EDITTYPE_SKILL, s_EDITTYPE_SPELL, s_EDITTYPE_TEMPLATE, s_EDITTYPE_SOURCE
	};
	private static final Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);

	private JButton btnCopy;
	private JButton btnDelete;
	private JButton btnDone;
	private JButton btnEdit;
	private JButton btnNew;
	private JLabel lblLstFileContent;
	private JLabel lblLstFileTypes;
	private JList lstLstFileContent;
	private JList lstLstFileTypes;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JPanel pnlButtons;
	private JPanel pnlLstEditorMain;
	private JPanel pnlLstFileContent;
	private JPanel pnlLstFileTypes;
	private JPanel pnllstLstFileContent;
	private JPanel pnllstLstFileTypes;
	private JScrollPane scpLstFileContent;
	private JScrollPane scpLstFileTypes;
	private int editType = EditorConstants.EDIT_NONE;

	/** Creates new form LstEditorMain */
	public LstEditorMain()
	{
		initComponents();

		IconUtilitities.maybeSetIcon(this, IconUtilitities.RESOURCE_APP_ICON);
		pcgen.gui.utils.Utility.centerFrame(this, false);
	}

	/**
	 * Edit the item
	 * @param editItem
	 * @param argEditType
	 */
	public void editIt(PObject editItem, int argEditType)
	{
		editType = argEditType;
		editIt(editItem);
	}

	private void addObject(final PObject editObject)
	{
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
			case EditorConstants.EDIT_SKILL:
			case EditorConstants.EDIT_DEITY:
			case EditorConstants.EDIT_TEMPLATE:
			case EditorConstants.EDIT_RACE:
			case EditorConstants.EDIT_LANGUAGE:
			case EditorConstants.EDIT_FEAT:
				//Fall through intentional
			case EditorConstants.EDIT_DOMAIN:
				Globals.getContext().ref.importObject(editObject);

				break;

			case EditorConstants.EDIT_SPELL:
				Globals.addToSpellMap( editObject.getKeyName(), editObject );
				//Globals.sortPObjectList(Globals.getSpellMap());
				break;

			case EditorConstants.EDIT_CAMPAIGN:
				Globals.getCampaignList().add((Campaign)editObject);
				Globals.sortPObjectList(Globals.getCampaignList());

				break;

			default:
				break;
		}

		CustomData.writeCustomFiles();
	}

	private void btnCopyActionPerformed()
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();

		if (lstItem != null)
		{
			try
			{
				final String nameEnding = " of " + lstItem.getKeyName();

				//
				// Check for a pre-existing item named "Copy of blah". Generate "Copy# of blah" until we find
				// one that's not in use
				//
				for (int idx = 1;; ++idx)
				{
					String newName = "Copy" + ((idx > 1) ? Integer.toString(idx) : "") + nameEnding;

					if (findObject(newName) == null)
					{
						final PObject newItem =
								Globals.getContext().cloneInMasterLists(
									lstItem, newName);

						prepareCopy(lstItem, newItem);

						editIt(newItem);

						break;
					}
				}
			}
			catch (Exception e)
			{
				//TODO: If we really should ignore this, add a note explaining why. XXX
				Logging.errorPrint("Failed to copy object " + lstItem.getKeyName(), e);
			}
		}
	}

	/**
	 * Update the copy with any object specific changes before it is editted for the 
	 * first time. This includes such things as copying the bio set for a race or 
	 * linking to the original class' spell list.
	 *  
	 * @param originalItem The PObject being copied.
	 * @param copyItem The copy of the original
	 */
	private void prepareCopy(final PObject originalItem, final PObject copyItem)
	{
		if (originalItem instanceof Race)
		{
			String[] unp = Globals.getContext().unparseSubtoken(originalItem, "REGION");

			String region;
			if (unp == null)
			{
				region = Constants.s_NONE;
			}
			else
			{
				region = unp[0];
			}

			Globals.getBioSet().copyRaceTags(region, originalItem.getKeyName(),
					region, copyItem.getKeyName());
		}
		else if (originalItem instanceof PCClass)
		{
			String originalKey = originalItem.getKeyName();
			PCClass copyClass = (PCClass) copyItem;
			PCClassKeyChange.changeReferences(originalKey, copyClass);
			Globals.getContext().unconditionallyProcess(copyClass, "SPELLLIST",
					"1|" + originalKey);
		}
	}

	private void btnDeleteActionPerformed()
	{
		//
		// Popup "Are you sure?"
		//
		if (JOptionPane.showConfirmDialog(
			null,
			"Are you sure?",  //$NON-NLS-2$ 
			Constants.APPLICATION_NAME,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE) != JOptionPane.NO_OPTION)
		{
			final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();

			if (removeObject(lstItem))
			{
				lstLstFileTypesValueChanged();
			}
		}
	}

	private void btnDoneActionPerformed()
	{
		setVisible(false);
		dispose();
	}

	private void btnEditActionPerformed()
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();

		if (lstItem != null)
		{
			editIt(lstItem);
		}
	}

	private void btnNewActionPerformed()
	{
		editIt(null);
	}

	private void editIt(PObject editItem)
	{
		try
		{
			PObject oldObject;

			if (editItem == null)
			{
				oldObject = null;
				editItem = newObject();
			}
			else
			{
				oldObject = findObject(editItem.getKeyName());

				//
				// Remove the pre-existing object (so renaming won't mess us up)
				//
				if (oldObject != null)
				{
					removeObject(oldObject);
					editItem = oldObject.clone();
				}
			}

			final EditorMainForm emf = new EditorMainForm(this, editItem, editType);

			for (;;)
			{
				emf.setVisible(true);

				if (emf.wasCancelled())
				{
					//
					// Need to add pre-existing object back in
					//
					if (oldObject != null)
					{
						addObject(oldObject);
					}

					return;
				}

				//
				// Make sure we aren't over-writing a pre-existing element
				//
				if (findObject(editItem.getKeyName()) != null)
				{
					ShowMessageDelegate.showMessageDialog(
						"Cannot save; already exists.",
						Constants.APPLICATION_NAME, MessageType.ERROR);

					continue;
				}

				break;
			}

			addObject(editItem);
			lstLstFileTypesValueChanged();
		}
		catch (Exception ignored)
		{
			Logging.errorPrint("Error", ignored);
		}
	}

	private PObject findObject(final String aName)
	{
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, aName);

			case EditorConstants.EDIT_DEITY:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(Deity.class, aName);

			case EditorConstants.EDIT_DOMAIN:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(Domain.class, aName);

			case EditorConstants.EDIT_FEAT:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
						Ability.class, AbilityCategory.FEAT, aName);

			case EditorConstants.EDIT_LANGUAGE:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(Language.class, aName);

			case EditorConstants.EDIT_RACE:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(Race.class, aName);

			case EditorConstants.EDIT_SKILL:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(Skill.class, aName);

			case EditorConstants.EDIT_SPELL:
				return Globals.getSpellKeyed(aName); // will return 1st entry in ArrayList

			case EditorConstants.EDIT_TEMPLATE:
				return Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCTemplate.class, aName);

			case EditorConstants.EDIT_CAMPAIGN:
				return Globals.getCampaignKeyedSilently(aName);

			default:
				break;
		}

		return null;
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlLstEditorMain = new JPanel();
		pnlLstFileTypes = new JPanel();
		lblLstFileTypes = new JLabel();
		pnllstLstFileTypes = new JPanel();
		scpLstFileTypes = new JScrollPane();
		lstLstFileTypes = new JList(supportedLsts);
		pnlLstFileContent = new JPanel();
		lblLstFileContent = new JLabel();
		pnllstLstFileContent = new JPanel();
		scpLstFileContent = new JScrollPane();
		lstLstFileContent = new JList();
		pnlButtons = new JPanel();
		btnNew = new JButton();
		btnEdit = new JButton();
		btnDelete = new JButton();
		btnCopy = new JButton();
		btnDone = new JButton();
		jPanel1 = new JPanel();
		jPanel2 = new JPanel();

		setTitle("LST Editors");

		getContentPane().setLayout(new GridBagLayout());

		addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent evt)
				{
					btnDoneActionPerformed();
				}
			});

		pnlLstEditorMain.setLayout(new GridBagLayout());

		pnlLstFileTypes.setLayout(new GridBagLayout());

		lblLstFileTypes.setText("File Types");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlLstFileTypes.add(lblLstFileTypes, gridBagConstraints);

		pnllstLstFileTypes.setLayout(new BorderLayout());

		lstLstFileTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstLstFileTypes.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent evt)
				{
					lstLstFileTypesValueChanged();
				}
			});

		scpLstFileTypes.setPreferredSize(new Dimension(90, 20));
		scpLstFileTypes.setViewportView(lstLstFileTypes);

		pnllstLstFileTypes.add(scpLstFileTypes, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlLstFileTypes.add(pnllstLstFileTypes, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.3;
		gridBagConstraints.weighty = 1.0;
		pnlLstEditorMain.add(pnlLstFileTypes, gridBagConstraints);

		pnlLstFileContent.setLayout(new GridBagLayout());

		lblLstFileContent.setText("Content");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlLstFileContent.add(lblLstFileContent, gridBagConstraints);

		pnllstLstFileContent.setLayout(new BorderLayout());

		lstLstFileContent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstLstFileContent.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent evt)
				{
					lstLstFileContentValueChanged();
				}
			});
		lstLstFileContent.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					lstLstFileContentMouseClicked(evt);
				}
			});
		FileContentCellRenderer renderer = new FileContentCellRenderer();
		lstLstFileContent.setCellRenderer(renderer);

		scpLstFileContent.setPreferredSize(new Dimension(90, 20));
		scpLstFileContent.setViewportView(lstLstFileContent);

		pnllstLstFileContent.add(scpLstFileContent, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlLstFileContent.add(pnllstLstFileContent, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.7;
		gridBagConstraints.weighty = 1.0;
		pnlLstEditorMain.add(pnlLstFileContent, gridBagConstraints);

		pnlButtons.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.1;
		pnlButtons.add(jPanel2, gridBagConstraints);

		btnNew.setText(PropertyFactory.getString("in_new"));
		btnNew.setMnemonic(PropertyFactory.getMnemonic("in_mn_new"));
		btnNew.setEnabled(false);
		btnNew.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnNewActionPerformed();
				}
			});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnNew, gridBagConstraints);

		btnEdit.setText(PropertyFactory.getString("in_edit"));
		btnEdit.setMnemonic(PropertyFactory.getMnemonic("in_mn_edit"));
		btnEdit.setEnabled(false);
		btnEdit.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnEditActionPerformed();
				}
			});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnEdit, gridBagConstraints);

		btnDelete.setText(PropertyFactory.getString("in_delete"));
		btnDelete.setMnemonic(PropertyFactory.getMnemonic("in_mn_delete"));
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnDeleteActionPerformed();
				}
			});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnDelete, gridBagConstraints);

		btnCopy.setText(PropertyFactory.getString("in_copy"));
		btnCopy.setMnemonic(PropertyFactory.getMnemonic("in_mn_copy"));
		btnCopy.setEnabled(false);
		btnCopy.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnCopyActionPerformed();
				}
			});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		pnlButtons.add(btnCopy, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.9;
		pnlButtons.add(jPanel1, gridBagConstraints);

		btnDone.setText(PropertyFactory.getString("in_close"));
		btnDone.setMnemonic(PropertyFactory.getMnemonic("in_mn_close"));
		btnDone.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnDoneActionPerformed();
				}
			});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 0, 2, 0);
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		pnlButtons.add(btnDone, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.weighty = 1.0;
		pnlLstEditorMain.add(pnlButtons, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(pnlLstEditorMain, gridBagConstraints);

		setSize(new Dimension(640, 470));
	}

	private void lstLstFileContentMouseClicked(MouseEvent evt)
	{
		if (btnEdit.isEnabled() && (evt.getClickCount() == 2))
		{
			btnEditActionPerformed();
		}
	}

	private void lstLstFileContentValueChanged()
	{
		final PObject lstItem = (PObject) lstLstFileContent.getSelectedValue();

		if (lstItem != null)
		{
			lstLstFileContent.ensureIndexIsVisible(lstLstFileContent.getSelectedIndex());
			boolean canEdit = lstItem.isType(Constants.TYPE_CUSTOM);
			final String lstType = (String) lstLstFileTypes.getSelectedValue();

			if (lstType.equalsIgnoreCase(s_EDITTYPE_SOURCE))
			{
				canEdit = true;
			}

			btnDelete.setEnabled(canEdit);
			btnEdit.setEnabled(canEdit);
			btnCopy.setEnabled(true);

			return;
		}

		btnEdit.setEnabled(false);
		btnCopy.setEnabled(false);
		btnDelete.setEnabled(false);
	}

	private void lstLstFileTypesValueChanged()
	{
		final String lstType = (String) lstLstFileTypes.getSelectedValue();
		Collection<?> possibleSelections = null;

		if (lstType != null)
		{
			if (lstType.equalsIgnoreCase(s_EDITTYPE_CLASS))
			{
				possibleSelections = Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class);
				editType = EditorConstants.EDIT_CLASS;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_DEITY))
			{
				possibleSelections = Globals.getContext().ref.getConstructedCDOMObjects(Deity.class);
				editType = EditorConstants.EDIT_DEITY;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_DOMAIN))
			{
				possibleSelections = Globals.getContext().ref.getConstructedCDOMObjects(Domain.class);
				editType = EditorConstants.EDIT_DOMAIN;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_FEAT))
			{
				possibleSelections = Globals.getContext().ref.getManufacturer(
						Ability.class, AbilityCategory.FEAT).getAllObjects();
				editType = EditorConstants.EDIT_FEAT;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_LANGUAGE))
			{
				possibleSelections = Globals.getContext().ref.getConstructedCDOMObjects(Language.class);
				editType = EditorConstants.EDIT_LANGUAGE;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_RACE))
			{
				possibleSelections = Globals.getContext().ref.getConstructedCDOMObjects(Race.class);
				editType = EditorConstants.EDIT_RACE;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_SKILL))
			{
				possibleSelections = Globals.getContext().ref.getConstructedCDOMObjects(Skill.class);
				editType = EditorConstants.EDIT_SKILL;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_SPELL))
			{
				final List<Spell> spells = new ArrayList<Spell>(Globals.getSpellMap().values().size());

				for ( final Object obj : Globals.getSpellMap().values() )
				{
					if (obj instanceof Spell)
					{
						spells.add((Spell)obj);
					}
					else
					{
						spells.addAll((List<Spell>) obj);
					}
				}
				Globals.sortPObjectList(spells);
				possibleSelections = spells;
				editType = EditorConstants.EDIT_SPELL;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_TEMPLATE))
			{
				possibleSelections = Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class);
				editType = EditorConstants.EDIT_TEMPLATE;
			}
			else if (lstType.equalsIgnoreCase(s_EDITTYPE_SOURCE))
			{
				possibleSelections = Globals.getCampaignList();
				editType = EditorConstants.EDIT_CAMPAIGN;
			}
		}

		if (possibleSelections == null)
		{
			possibleSelections = new ArrayList();
			editType = EditorConstants.EDIT_NONE;
		}

		lstLstFileContent.setListData(possibleSelections.toArray());
		btnNew.setEnabled(possibleSelections.size() != 0);
		btnEdit.setEnabled(false);
		btnCopy.setEnabled(false);
		btnDelete.setEnabled(false);
	}

	private PObject newObject()
	{
		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				return new PCClass();

			case EditorConstants.EDIT_DEITY:
				return new Deity();

			case EditorConstants.EDIT_DOMAIN:
				return new Domain();

			case EditorConstants.EDIT_FEAT:
				return new Ability();

			case EditorConstants.EDIT_LANGUAGE:
				return new Language();

			case EditorConstants.EDIT_RACE:
				return new Race();

			case EditorConstants.EDIT_SKILL:
				return new Skill();

			case EditorConstants.EDIT_SPELL:
				return new Spell();

			case EditorConstants.EDIT_TEMPLATE:
				return new PCTemplate();

			case EditorConstants.EDIT_CAMPAIGN:
				return new Campaign();

			default:
				break;
		}

		return null;
	}

	private boolean removeObject(final PObject editObject)
	{
		boolean removed = false;

		if (editObject != null)
		{
			switch (editType)
			{
				case EditorConstants.EDIT_CLASS:
				case EditorConstants.EDIT_DEITY:
				case EditorConstants.EDIT_SKILL:
				case EditorConstants.EDIT_TEMPLATE:
				case EditorConstants.EDIT_RACE:
				case EditorConstants.EDIT_LANGUAGE:
				case EditorConstants.EDIT_FEAT:
					//Fall through intentional
				case EditorConstants.EDIT_DOMAIN:
					removed = Globals.getContext().ref.forget(editObject);

					break;

				case EditorConstants.EDIT_SPELL:

					Object obj = Globals.getSpellMap().get(editObject.getKeyName());

					if (obj instanceof ArrayList)
					{
						removed = ((ArrayList) obj).remove(editObject);
					}
					else
					{
						removed = Globals.removeFromSpellMap(editObject.getKeyName()) != null;
					}

					break;

				case EditorConstants.EDIT_CAMPAIGN:
					removed = Globals.getCampaignList().remove(editObject);

					break;

				default:
					break;
			}
		}

		if (removed)
		{
			CustomData.writeCustomFiles();
		}

		return removed;
	}

	/**
	 * <code>FileContentCellRenderer</code> handles the display of objects in
	 * the content list with custom formatting. In this case, the user's custom
	 * objects are highlighted so they are erasily found.
	 *
	 * @author James Dempsey <jdempsey@users.sourceforge.net>
	 */
	class FileContentCellRenderer extends JLabel implements ListCellRenderer
	{

		private FileContentCellRenderer()
		{
			super();
			this.setOpaque(true);
		}

		/**
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
	  //Get the selected index. (The index param isn't
	  //always valid, so just use the value.)
			boolean isCustom = (value != null && value instanceof PObject) ? (((PObject) value).isType("CUSTOM")) : false;

	  if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (isCustom)
			{
				setForeground(Color.BLUE);
			}
			this.setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);
			this.setEnabled(list.isEnabled());
			this.setFont(list.getFont());

	  setText(String.valueOf(value));
	  return this;
		}


	}
}
