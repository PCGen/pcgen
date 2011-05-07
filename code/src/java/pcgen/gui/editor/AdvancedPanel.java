/*
 * AdvancedPanel.java
 * Copyright 2005 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on Jul 5, 2005
 *
 * $Id$
 *
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.BrowserLauncher;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>AdvancedPanel</code> is ...
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class AdvancedPanel extends JPanel
{
	//static final long serialVersionUID = 608648521263089459L;

//
// tags from PObject:
// ADD, AUTO
// BONUS
// CCSKILL, CSKILL
// CHOOSE
// DEFINE, DESC, DESCISPI, DR
// KEY, KIT
// LANGAUTO
// NAME, NAMEISPI
// OUTPUTNAME
// PRExxx
// REGION, RESTRICT
// SA, SPELL, SPELLLEVEL:CLASS, SPELLLEVEL:DOMAIN, SR
// TYPE
// UDAM, UMULT
// VISION
// WEAPONAUTO
//
// tags from Class:
// ABB
// ADDDOMAINS
// ATTACKCYCLE
// BAB
// CAST
// CASTAS
// DEF
// DEITY
// DOMAIN
// EXCHANGELEVEL
// EXCLASS
// FEAT
// FEATAUTO
// HASSUBCLASS
// HD
// ITEMCREATE
// KNOWN
// KNOWNSPELLS
// KNOWNSPELLSFROMSPECIALTY
// LANGBONUS
// LEVELSPERFEAT
// MAXLEVEL
// MEMORIZE
// MODTOSKILLS
// MULTIPREREQS
// PROHIBITED
// QUALIFY
// SKILLLIST
// SPECIALS
// SPECIALTYKNOWN
// SPELLBOOK
// SPELLLIST
// SPELLSTAT
// SPELLTYPE
// STARTSKILLPTS
// SUBCLASS
// TEMPLATE
// UATT
// VFEAT
// VISIBLE
// WEAPONBONUS
// XTRAFEATS
// MONSKILL
// PRERACETYPE
//
// tags from Deity:
// ALIGN
// DEITYWEAP
// DOMAINS
// FOLLOWERALIGN
// PANTHEON
// QUALIFY
// RACE
// SYMBOL
//
// tags from Domain:
// FEAT
// QUALIFY
//
// tags from Feat:
// ADD
// COST
// MULT
// QUALIFY
// STACK
// VISIBLE
//
// tags from Language:
// --none--
//
// tags from Race:
// AC
// BAB
// CHOOSE:LANGAUTO
// CR
// FACE
// FAVCLASS
// FEAT
// HANDS
// HITDICE
// HITDIE
// HITDICEADVANCEMENT
// INIT
// LANGBONUS
// LANGNUM
// LEGS
// LEVELADJUSTMENT
// MFEAT
// MONSTERCLASS
// MOVE
// NATURALATTACKS
// PROF
// QUALIFY
// RACENAME
// REACH
// SIZE
// SKILL
// SKILLMULT
// STARTFEATS
// TEMPLATE
// VFEAT
// WEAPONBONUS
// XTRASKILLPTSPERLVL
//
// tags from Skill:
// ACHECK
// CLASSES
// EXCLUSIVE
// KEYSTAT
// QUALIFY
// REQ
// ROOT
// SYNERGY
// USEUNTRAINED
//
// tags from Spell:
// CASTTIME
// CLASSES
// COMPS
// COST
// DOMAINS
// EFFECTS (deprecated use DESC)
// EFFECTTYPE (deprecated use TARGETAREA)
// CT
// DESCRIPTOR
// DURATION
// ITEM
// LVLRANGE (Wheel of Time)
// QUALIFY
// RANGE
// SAVEINFO
// SCHOOL
// SPELLLEVEL (deprecated use CLASSES or DOMAINS)
// SPELLRES
// SUBSCHOOL
// TARGETAREA
// STAT
// VARIANTS
// XPCOST
//
	private static final String[] tags =
		new String[] {
			"ADD",
			"AUTO",
			"BONUS",
			"CHOOSE",
			"DEFINE",
			"DR",
			"KEY",
			"PANTHEON",
			"PREALIGN",
			"PREARMORPROF",
			"PREARMORTYPE",
			"PREATT",
			"PREBASESIZEGT",
			"PREBASESIZEGTEQ",
			"PREBASESIZELT",
			"PREBASESIZELTEQ",
			"PREBASESIZEQ",
			"PREBASESIZENEQ",
			"PREBIRTHPLACE",
			"PRECITY",
			"PRECHECK",
			"PRECHECKBASE",
			"PRECLASS",
			"PREDR",
			"PREDEFAULTMONSTER",
			"PREDEITY",
			"PREDEITYALIGN",
			"PREDEITYDOMAIN",
			"PREDOMAIN",
			"PREEQUIP",
			"PREEQUIPBOTH",
			"PREEQUIPPRIMARY",
			"PREEQUIPSECONDARY",
			"PREEQUIPTWOWEAPON",
			"PREFEAT",
			"PREGENDER",
			"PREHANDSEQ",
			"PREHANDSGT",
			"PREHANDSGTEQ",
			"PREHANDSLT",
			"PREHANDSLTEQ",
			"PREHANDSNEQ",
			"PREHD",
			"PREHP",
			"PREITEM",
			"PRELANG",
			"PRELEGS",
			"PRELEVEL",
			"PRELEVELMAX",
			"PREMOVE",
			"PREPOINTBUYMETHOD",
			"PRERACE",
			"PREREGION",
			"PRESA",
			"PRESIZEEQ",
			"PRESIZELT",
			"PRESIZELTEQ",
			"PRESIZEGT",
			"PRESIZEGTEQ",
			"PRESIZENEQ",
			"PRESKILL",
			"PRESKILLMULT",
			"PRESKILLTOT",
			"PRESPELL",
			"PRESPELLCAST",
			"PRESPELLDESCRIPTOR",
			"PRESPELLSCHOOL",
			"PRESPELLSCHOOLSUB",
			"PRESPELLTYPE",
			"PRESR",
			"PRESTAT",
			"PRETEMPLATE",
			"PRETEXT",
			"PRETYPE",
			"PREUATT",
			"PREVAR",
			"PREWEAPONPROF",
			"QUALIFY",
			"SAB",
			"SPELLLEVEL",
			"SPELLS",
			"SR",
			"UDAM",
			"UMULT",
			"VISION" };

	private static final List<String> singleFireTags =
		Arrays.asList(new String[] {
			"CHOOSE",
			"KEY",
			"SR" });

	private JButton btnAddAdvanced;
	private JButton btnHelpAdvanced;
	private JButton btnRemoveAdvanced;
	private JComboBoxEx cmbAdvancedTag;
	private JLabel lblAdvancedHeader;
	private JLabel lblAdvancedSelected;
	private JLabel lblAdvancedTag;
	private JLabel lblAdvancedTagValue;
	private JList lstAdvancedSelected;
	private JPanel pnlAdvancedAvailable;
	private JPanel pnlAdvancedButtons;
	private JPanel pnlAdvancedHeader;
	private JPanel pnlAdvancedSelected;
	private JPanel pnlAdvancedTag;
	private JPanel pnlAdvancedTagValue;

	private JPanel pnllstAdvancedSelected;
	private JPanel pnllstAdvancedTagValue;

	private JScrollPane scpAdvancedSelected;
	private JScrollPane scpAdvancedTagValue;
	private JTextArea txtAdvancedTagValue;

	private PObject thisPObject = null;

	/**
	 * Creates new Advanced Panel
	 *
	 * @param argPObject
	 */
	public AdvancedPanel(PObject argPObject)
	{
		if (argPObject == null)
		{
			throw new NullPointerException();
		}
		thisPObject = argPObject;

		initComponents();
	}

	private void btnAddAdvancedActionPerformed()
	{
		btnAddAdvanced.setEnabled(false);

		String newEntry = (String) cmbAdvancedTag.getSelectedItem() + ":" + txtAdvancedTagValue.getText().trim();
		boolean result = false;
		try
		{
			final String token = newEntry.trim();
			final int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
			}
			else
			{
				String key = token.substring(0, colonLoc);
				String value = (colonLoc == token.length() - 1) ? null : token
						.substring(colonLoc + 1);
				LoadContext context = SettingsHandler.getGame().getContext();
				if (context.processToken(thisPObject, key, value))
				{
					context.commit();
					result = true;
				}
				else
				{
					context.rollback();
					Logging.replayParsedMessages();
				}
				Logging.clearParseMessages();
			}
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint(ple.getMessage(), ple);
		}
		if (!result)
		{
			// Throw up an error dialog here
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_demTagInvalid"),
				Constants.APPLICATION_NAME,
				MessageType.ERROR);
			btnAddAdvanced.setEnabled(true);
		}
		else
		{
			final JListModel lmd = (JListModel) lstAdvancedSelected.getModel();
			lmd.addElement(newEntry);
		}
	}

	private void btnRemoveAdvancedActionPerformed()
	{
		btnRemoveAdvanced.setEnabled(false);

		final JListModel lms = (JListModel) lstAdvancedSelected.getModel();
		final Object[] x = lstAdvancedSelected.getSelectedValues();

		for (int i = 0; i < x.length; ++i)
		{
			String entry = (String) x[i];
			final int idx = entry.indexOf(':');

			if (idx >= 0)
			{
				final String tag = entry.substring(0, idx);
				cmbAdvancedTag.setSelectedItem(tag);
				entry = entry.substring(idx + 1);
				txtAdvancedTagValue.setText(entry);
				btnAddAdvanced.setEnabled(true);
			}

			lms.removeElement(x[i]);
		}
	}

	private void btnHelpAdvancedActionPerformed()
	{
		try
		{
			BrowserLauncher.openURL(SettingsHandler.getPcgenDocsDir()
				.getAbsolutePath()
				+ File.separator + "index.html");
		}
		catch (IOException ex)
		{
			ShowMessageDelegate.showMessageDialog(
				"Could not open docs in external browser. "
					+ "Have you set your default browser in the "
					+ "Preference menu? Sorry...", Constants.APPLICATION_NAME,
				MessageType.ERROR);
			Logging.errorPrint("Could not open docs in external browser", ex);
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		//
		// There's got to be a better/easier way to do this...
		//
		try
		{
			btnAddAdvanced = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
			btnRemoveAdvanced = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		}
		catch (Exception exc)
		{
			btnAddAdvanced = new JButton(">");
			btnRemoveAdvanced = new JButton("<");
		}

		btnHelpAdvanced = new JButton();
		cmbAdvancedTag = new JComboBoxEx();
		lblAdvancedHeader = new JLabel();
		lblAdvancedSelected = new JLabel();
		lblAdvancedTag = new JLabel();
		lblAdvancedTagValue = new JLabel();
		lstAdvancedSelected = new JList();
		pnlAdvancedAvailable = new JPanel();
		pnlAdvancedButtons = new JPanel();
		pnlAdvancedHeader = new JPanel();
		pnlAdvancedSelected = new JPanel();
		pnlAdvancedTag = new JPanel();
		pnlAdvancedTagValue = new JPanel();
		pnllstAdvancedSelected = new JPanel();
		pnllstAdvancedTagValue = new JPanel();
		scpAdvancedSelected = new JScrollPane();
		scpAdvancedTagValue = new JScrollPane();
		txtAdvancedTagValue = new JTextArea();



		//The Advanced Tab has no meaning in the Source File Editor and
		//therefore should only be shown when needed.
		this.setLayout(new GridBagLayout());

		this.setName(PropertyFactory.getString("in_demLangTab"));
		pnlAdvancedAvailable.setLayout(new GridBagLayout());

		pnlAdvancedAvailable.setPreferredSize(new Dimension(259, 147));
		pnlAdvancedTag.setLayout(new GridBagLayout());

		lblAdvancedTag.setText(PropertyFactory.getString("in_demTag"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedTag.add(lblAdvancedTag, gridBagConstraints);

		cmbAdvancedTag.setEditable(true);
		cmbAdvancedTag.setPreferredSize(new Dimension(180, 25));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedTag.add(cmbAdvancedTag, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedAvailable.add(pnlAdvancedTag, gridBagConstraints);

		pnlAdvancedTagValue.setLayout(new GridBagLayout());

		lblAdvancedTagValue.setText(PropertyFactory.getString("in_demTagVal"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		pnlAdvancedTagValue.add(lblAdvancedTagValue, gridBagConstraints);

		pnllstAdvancedTagValue.setLayout(new BorderLayout());

		pnllstAdvancedTagValue.setPreferredSize(new Dimension(100, 16));
		scpAdvancedTagValue.setPreferredSize(new Dimension(259, 131));
		txtAdvancedTagValue.setLineWrap(true);
		txtAdvancedTagValue.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent evt)
			{
				super.keyReleased(evt);
				txtAdvancedTagValueKeyReleased();
			}
		});

		scpAdvancedTagValue.setViewportView(txtAdvancedTagValue);

		pnllstAdvancedTagValue.add(scpAdvancedTagValue, BorderLayout.CENTER);

		btnHelpAdvanced.setText(PropertyFactory.getString("in_demHelp"));
		btnHelpAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnHelpAdvancedActionPerformed();
			}
		});
		pnllstAdvancedTagValue.add(btnHelpAdvanced, BorderLayout.SOUTH);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.8;
		pnlAdvancedTagValue.add(pnllstAdvancedTagValue, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.8;
		pnlAdvancedAvailable.add(pnlAdvancedTagValue, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAdvancedAvailable, gridBagConstraints);

		pnlAdvancedButtons.setLayout(new GridBagLayout());

		btnAddAdvanced.setEnabled(false);
		btnAddAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnAddAdvancedActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlAdvancedButtons.add(btnAddAdvanced, gridBagConstraints);

		btnRemoveAdvanced.setEnabled(false);
		btnRemoveAdvanced.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				btnRemoveAdvancedActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		pnlAdvancedButtons.add(btnRemoveAdvanced, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAdvancedButtons, gridBagConstraints);

		pnlAdvancedSelected.setLayout(new GridBagLayout());

		lblAdvancedSelected.setText(PropertyFactory.getString("in_selected"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlAdvancedSelected.add(lblAdvancedSelected, gridBagConstraints);

		pnllstAdvancedSelected.setLayout(new BorderLayout());

		lstAdvancedSelected.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					lstAdvancedSelectedMouseClicked(evt);
				}
			});
		lstAdvancedSelected
			.addListSelectionListener(new ListSelectionListener()
				{
				public void valueChanged(ListSelectionEvent evt)
				{
					if (lstAdvancedSelected.getSelectedIndex() >= 0)
					{
						lstAdvancedSelected
							.ensureIndexIsVisible(lstAdvancedSelected
									.getSelectedIndex());
					}
				}
			});

		scpAdvancedSelected.setViewportView(lstAdvancedSelected);

		pnllstAdvancedSelected.add(scpAdvancedSelected, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlAdvancedSelected.add(pnllstAdvancedSelected, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAdvancedSelected, gridBagConstraints);

		lblAdvancedHeader.setText(PropertyFactory.getString("in_demMiscTags"));
		pnlAdvancedHeader.add(lblAdvancedHeader);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		this.add(pnlAdvancedHeader, gridBagConstraints);

	}

	private void lstAdvancedSelectedMouseClicked(MouseEvent evt)
	{
		if (EditUtil.isDoubleClick(evt, lstAdvancedSelected, btnRemoveAdvanced))
		{
			btnRemoveAdvancedActionPerformed();
		}
	}

	///////////////////////
	// Advanced tab
	//
	private void txtAdvancedTagValueKeyReleased()
	{
		boolean hasValue = txtAdvancedTagValue.getText().trim().length() != 0;
		Object selectedTag = cmbAdvancedTag.getSelectedItem();
		boolean multProhibited = singleFireTags.contains(selectedTag);
		boolean enable = hasValue;
		if (multProhibited)
		{
			final JListModel lms = (JListModel) lstAdvancedSelected.getModel();

			for (Object entry : lms.getElements())
			{
				String en = entry.toString();
				final int idx = en.indexOf(':');

				if (idx >= 0)
				{
					final String tag = en.substring(0, idx);
					if (selectedTag.equals(tag))
					{
						enable = false;
						break;
					}
				}
			}
		}
		btnAddAdvanced.setEnabled(enable);
	}


	void setSelected(List selectedList)
	{
		lstAdvancedSelected.setModel(new JListModel(selectedList, true));
	}


	/**
	 * Retrieve the selected item list for the advanced tab.
	 * @return An arrya of selected items.
	 */
	Object[] getSelectedList()
	{
		return ((JListModel) lstAdvancedSelected.getModel()).getElements();
	}

	/**
	 * Set the tags that may be added via the advanced tab.
	 *
	 * @param editType The type of object being edited.
	 */
	void setAvailableTagList(int editType)
	{
		//
		// Tags on advanced tag
		//
		cmbAdvancedTag.setModel(new DefaultComboBoxModel(tags));
		cmbAdvancedTag.setSelectedIndex(0);

		switch (editType)
		{
			case EditorConstants.EDIT_DEITY:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("CHOOSE");
				break;

			case EditorConstants.EDIT_LANGUAGE:
			case EditorConstants.EDIT_SPELL:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("CHOOSE");
				cmbAdvancedTag.removeItem("PANTHEON");

				break;

			case EditorConstants.EDIT_DOMAIN:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("PANTHEON");
				cmbAdvancedTag.removeItem("SPELLLEVEL");
				break;
				
			case EditorConstants.EDIT_SKILL:
			case EditorConstants.EDIT_RACE:
			case EditorConstants.EDIT_TEMPLATE:
				cmbAdvancedTag.removeItem("ADD");
				cmbAdvancedTag.removeItem("PANTHEON");
				break;

			case EditorConstants.EDIT_FEAT:
				//cmbAdvancedTag.removeItem("CHOOSE");
				cmbAdvancedTag.removeItem("PANTHEON");
				break;

			default:
				break;
		}
	}
}
