/*
 * InfoDescription.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.AgeSet;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.NoteItem;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.NameGui;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.PortraitChooser;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.Utility;
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.DecimalNumberField;
import pcgen.util.Delta;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoDescription</code> creates a new tabbed panel.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision$
 */
public final class InfoDescription extends JPanel implements CharacterInfoTab
{
	static final long serialVersionUID = -8015559748421397718L;

	private static final Tab tab = Tab.DESCRIPTION;

	private static boolean bEditingAge = false;
	private static final int BIO_NOTEID = -2;
	private static final int DESCRIPTION_NOTEID = -3;
	private static final int COMPANION_NOTEID = -4;
	private static final int OTHERASSETS_NOTEID = -5;
	private static final int MAGICITEMS_NOTEID = -6;
	private static final int PORTRAIT_NOTEID = -7;
	private static final int DMNOTES_NOTEID = -8;
	private static final String in_noPortraitChildrenMessage =
			LanguageBundle.getString("in_noPortraitChildrenMessage");
	private static final String in_noPortraitDeletionMessage =
			LanguageBundle.getString("in_noPortraitDeletionMessage");
	private static final String in_noPortraitRenamingMessage =
			LanguageBundle.getString("in_noPortraitRenamingMessage");

	// Combobox event handlers
	private ActionListener al1 = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if ((handedComboBox != null)
				&& (handedComboBox.getSelectedItem() != null))
			{
				pc.setDirty(true);
				pc.setHanded(handedComboBox.getSelectedItem().toString());
			}
		}
	};

	private ActionListener al2 = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if ((genderComboBox != null)
				&& (genderComboBox.getSelectedItem() != null))
			{
				pc.setGender((Gender) genderComboBox.getSelectedItem());
			}
		}
	};

	private CardLayout dataLayout;
	private DecimalNumberField htText = new DecimalNumberField(0, 0);
	private DecimalNumberField wtText = new DecimalNumberField(0, 0);
	private DefaultTreeModel notesModel;

	/**
	 * This listener detects changes in the note text.
	 */
	private DocumentListener noteChangeListener = new DocumentListener()
	{
		public void insertUpdate(DocumentEvent e)
		{
			textIsDirty = true;
			updateNoteItem();
		}

		public void removeUpdate(DocumentEvent e)
		{
			textIsDirty = true;
			updateNoteItem();
		}

		public void changedUpdate(DocumentEvent e)
		{
			textIsDirty = true;
			updateNoteItem();
		}
	};

	private FlippingSplitPane splitPane;
	private JButton addButton;
	private JButton checkAll;
	private JButton deleteButton;
	private JButton moveButton;
	private JButton randAll;
	private JButton randName;
	private JButton renameButton;
	private JButton revertButton;
	private JButton uncheckAll;
	private JCheckBox ageBox = new JCheckBox();
	private JCheckBox birthplaceBox = new JCheckBox();
	private JCheckBox catchPhraseBox = new JCheckBox();
	private JCheckBox eyeColorBox = new JCheckBox();
	private JCheckBox hairColorBox = new JCheckBox();
	private JCheckBox hairStyleBox = new JCheckBox();
	private JCheckBox htwtBox = new JCheckBox();
	private JCheckBox interestsBox = new JCheckBox();
	private JCheckBox locationBox = new JCheckBox();
	private JCheckBox personality1Box = new JCheckBox();
	private JCheckBox personality2Box = new JCheckBox();
	private JCheckBox phobiaBox = new JCheckBox();
	private JCheckBox residenceBox = new JCheckBox();
	private JCheckBox skinBox = new JCheckBox();
	private JCheckBox speechPatternBox = new JCheckBox();
	private JComboBoxEx ageComboBox = new JComboBoxEx();
	private JComboBoxEx genderComboBox = new JComboBoxEx();
	private JComboBoxEx handedComboBox = new JComboBoxEx();
	private JEditorPane dataText;
	private JLabel labelHeight = null;
	private JLabel labelName = null;
	private JLabel labelWeight = null;
	private JPanel buttonPanel;
	private JPanel centerCenterPanel = new JPanel();
	private JPanel centerNorthPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel dataPanel;
	private JPanel northPanel = new JPanel();
	private JScrollPane dataScroll;
	private JScrollPane notesScroll;
	private JTextField birthplaceText = new JTextField();
	private JTextField birthdayText = new JTextField();
	private JTextField catchPhraseText = new JTextField();
	private JTextField eyeColorText = new JTextField();
	private JTextField fregionText = new JTextField();
	private JTextField hairColorText = new JTextField();
	private JTextField hairStyleText = new JTextField();
	private JTextField interestsText = new JTextField();
	private JTextField locationText = new JTextField();
	private JTextField personality1Text = new JTextField();
	private JTextField personality2Text = new JTextField();
	private JTextField phobiaText = new JTextField();
	private JTextField playerNameText = new JTextField();
	private JTextField residenceText = new JTextField();
	private JTextField skinText = new JTextField();
	private JTextField speechPatternText = new JTextField();
	private JTextField txtName = new JTextField();
	private JTree notesTree;
	private List<NoteItem> nodesToBeAddedList = new ArrayList<NoteItem>();
	private NameGui nameFrame = null;
	private NoteItem bioNote = null;
	private NoteItem companionNote = null;
	private NoteItem currentItem = null;
	private NoteItem descriptionNote = null;
	private NoteItem lastItem = null;
	private NoteItem magicItemsNote = null;
	private NoteItem otherAssetsNote = null;
	private NoteItem dmNote = null;
	private NoteItem portraitNote = null;
	private NoteTreeNode rootTreeNode;
	private PortraitChooser portrait;

	private WholeNumberField ageText = new WholeNumberField(0, 0);
	private boolean textIsDirty = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoDescription(PlayerCharacter pc)
	{
		this.pc = pc;
		setName(tab.toString());

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initNonDataDrivenEventListeners();
				refreshDisplay();
			}
		});
	}

	public void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
		{
			clear();
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	private void clear()
	{
		rootTreeNode.removeAllChildren();
		bioNote = null;
		companionNote = null;
		currentItem = null;
		descriptionNote = null;
		lastItem = null;
		magicItemsNote = null;
		otherAssetsNote = null;
		dmNote = null;
		portraitNote = null;
		textIsDirty = false;
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Description.Order", tab
			.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Description.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
		return toDoList;
	}

	public void refresh()
	{
		if (pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if (readyForRefresh)
		{
			updateCharacterInfo();
		}
		else
		{
			serial = 0;
		}
	}

	public JComponent getView()
	{
		return this;
	}

	/**
	 * Get text name
	 * @return txtname
	 */
	public JTextField getTxtName()
	{
		return txtName;
	}

	/**
	 * Process the selection of an age category.
	 */
	private void ageComboBoxActionPerformed()
	{
		final Race pcRace = pc.getRace();

		if ((pcRace != null) && !pcRace.equals(Globals.s_EMPTYRACE))
		{
			final String ageCategory = (String) ageComboBox.getSelectedItem();

			if (ageCategory != null)
			{
				final int idx = pc.getBioSet().getAgeSetNamed(ageCategory);

				if (idx >= 0)
				{
					if (!bEditingAge)
					{
						pc.getBioSet().randomize(
							"AGECAT" + Integer.toString(idx), pc);
						ageText.setText(Integer.toString(pc.getAge()));
						pc.setDirty(true);
					}
				}
				else
				{
					ageComboBox.setSelectedIndex(-1);
				}
			}
		}
	}

	/**
	 * This function is called when the "Check All" button is clicked.
	 * It sets all of the random checkboxes.
	 */
	private void checkAll_click()
	{
		ageBox.setSelected(true);
		htwtBox.setSelected(true);
		skinBox.setSelected(true);
		hairColorBox.setSelected(true);
		hairStyleBox.setSelected(true);
		eyeColorBox.setSelected(true);
		speechPatternBox.setSelected(true);
		phobiaBox.setSelected(true);
		interestsBox.setSelected(true);
		catchPhraseBox.setSelected(true);
		personality1Box.setSelected(true);
		personality2Box.setSelected(true);
		residenceBox.setSelected(true);
		locationBox.setSelected(true);
		birthplaceBox.setSelected(true);
	}

	/**
	 * Recursively build up the tree of notes.
	 * The tree is built off the root node rootTreeNode
	 * @param aNode
	 * @param note
	 */
	private void establishTreeNodes(NoteTreeNode aNode, NoteItem note)
	{
		int index = -1;

		if (aNode == null)
		{
			rootTreeNode = new NoteTreeNode(null);
			aNode = rootTreeNode;
			for (NoteItem testnote : pc.getNotesList())
			{
				if (!testnote.getName().equals("Hidden"))
				{
					nodesToBeAddedList.add(testnote);
				}
			}

			int order = 0;
			portraitNote =
					new NoteItem(PORTRAIT_NOTEID, -1, LanguageBundle
						.getString("in_portrait"), "");
			nodesToBeAddedList.add(order++, portraitNote);

			bioNote = new NoteItem(BIO_NOTEID, -1, "Bio", pc.getDisplay().getBio());
			nodesToBeAddedList.add(order++, bioNote);
			descriptionNote =
					new NoteItem(DESCRIPTION_NOTEID, -1, LanguageBundle
						.getString("in_descrip"), pc.getDisplay().getDescription());
			nodesToBeAddedList.add(order++, descriptionNote);
			companionNote =
					new NoteItem(COMPANION_NOTEID, -1, LanguageBundle
						.getString("in_companions"), pc.getSafeStringFor(StringKey.MISC_COMPANIONS));
			nodesToBeAddedList.add(order++, companionNote);
			otherAssetsNote =
					new NoteItem(OTHERASSETS_NOTEID, -1, LanguageBundle
						.getString("in_otherAssets"), pc.getSafeStringFor(StringKey.MISC_ASSETS));
			nodesToBeAddedList.add(order++, otherAssetsNote);
			magicItemsNote =
					new NoteItem(MAGICITEMS_NOTEID, -1, LanguageBundle
						.getString("in_magicItems"), pc.getSafeStringFor(StringKey.MISC_MAGIC));
			nodesToBeAddedList.add(order++, magicItemsNote);

			dmNote =
					new NoteItem(DMNOTES_NOTEID, -1, LanguageBundle
						.getString("in_dmNotes"), pc.getSafeStringFor(StringKey.MISC_DM));
			nodesToBeAddedList.add(order++, dmNote);

		}
		else
		{
			index = note.getId();
		}

		List<NoteItem> aList = new ArrayList<NoteItem>();

		for (int x = 0; x < nodesToBeAddedList.size(); x++)
		{
			NoteItem ni = nodesToBeAddedList.get(x);

			if (ni.getParentId() == index)
			{
				NoteTreeNode dNode = new NoteTreeNode(ni);
				aNode.add(dNode);
				nodesToBeAddedList.remove(x);
				x--;
				aList.add(ni);
			}
		}

		for (int i = 0; i < aNode.getChildCount(); i++)
		{
			establishTreeNodes((NoteTreeNode) aNode.getChildAt(i), aList.get(i));
		}
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving("");

		refresh();

		//buttonPanel.setPreferredSize(new Dimension((int)(this.getSize().getWidth()), 40)); -- from Notes - don't know if its needed though.
	}

	private void initComponents()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		northPanel.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelName =
				new JLabel(LanguageBundle.getString("in_nameLabel") + ": ");
		gridbag.setConstraints(labelName, c);
		northPanel.add(labelName);

		Utility.buildConstraints(c, 1, 0, 2, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(txtName, c);
		northPanel.add(txtName);

		Utility.buildConstraints(c, 3, 0, 2, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		randName = new JButton(LanguageBundle.getString("in_randomButton"));
		gridbag.setConstraints(randName, c);
		northPanel.add(randName);

		////
		Utility.buildConstraints(c, 0, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;

		JLabel label =
				new JLabel(LanguageBundle.getString("in_player") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 1, 1, 2, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(playerNameText, c);
		northPanel.add(playerNameText);

		Utility.buildConstraints(c, 7, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_gender") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 8, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(genderComboBox, c);
		northPanel.add(genderComboBox);

		genderComboBox.setAllItems(Globals.getAllGenders().toArray());
		// TODO: Indentiofy if the following undeclared genders need to be supported
		//		genderComboBox.addItem(LanguageBundle.getString("in_comboNone"));
		//		genderComboBox.addItem(LanguageBundle.getString("in_comboOther"));

		Utility.buildConstraints(c, 7, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_handString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 8, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(handedComboBox, c);
		northPanel.add(handedComboBox);

		handedComboBox.addItem(LanguageBundle.getString("in_handRight"));
		handedComboBox.addItem(LanguageBundle.getString("in_handLeft"));
		handedComboBox.addItem(LanguageBundle.getString("in_handBoth"));
		handedComboBox.addItem(LanguageBundle.getString("in_comboNone"));
		handedComboBox.addItem(LanguageBundle.getString("in_comboOther"));

		Utility.buildConstraints(c, 0, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;

		if (" ftin".equals(Globals.getGameModeUnitSet().getHeightUnit()))
		{
			labelHeight =
					new JLabel(LanguageBundle.getString("in_height")
						+ " (in.): ");
		}
		else
		{
			labelHeight =
					new JLabel(LanguageBundle.getString("in_height") + " ("
						+ Globals.getGameModeUnitSet().getHeightUnit() + "): ");
		}

		gridbag.setConstraints(labelHeight, c);
		northPanel.add(labelHeight);

		Utility.buildConstraints(c, 1, 2, 2, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(htText, c);
		northPanel.add(htText);

		Utility.buildConstraints(c, 3, 2, 1, 2, 5, 10);
		c.fill = GridBagConstraints.CENTER;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(htwtBox, c);
		northPanel.add(htwtBox);

		Utility.buildConstraints(c, 0, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelWeight =
				new JLabel(LanguageBundle.getString("in_weight") + " ("
					+ Globals.getGameModeUnitSet().getWeightUnit() + "): ");
		gridbag.setConstraints(labelWeight, c);
		northPanel.add(labelWeight);

		Utility.buildConstraints(c, 1, 3, 2, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(wtText, c);
		northPanel.add(wtText);

		Utility.buildConstraints(c, 0, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_age") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 1, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(ageText, c);
		northPanel.add(ageText);

		List<String> cats = new ArrayList<String>();

		for (String aString : pc.getBioSet().getAgeCategories())
		{
			if (!cats.contains(aString))
			{
				cats.add(aString);
			}
		}

		Collections.sort(cats);
		ageComboBox.setModel(new DefaultComboBoxModel(cats.toArray()));

		ageComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				if (evt.getStateChange() == ItemEvent.SELECTED)
				{
					ageComboBoxActionPerformed();
				}
			}
		});

		Utility.buildConstraints(c, 2, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(ageComboBox, c);
		northPanel.add(ageComboBox);

		Utility.buildConstraints(c, 3, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(ageBox, c);
		northPanel.add(ageBox);

		Utility.buildConstraints(c, 7, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_skin") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 8, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(skinText, c);
		northPanel.add(skinText);

		Utility.buildConstraints(c, 9, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(skinBox, c);
		northPanel.add(skinBox);

		Utility.buildConstraints(c, 4, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_region") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 5, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(fregionText, c);
		northPanel.add(fregionText);
		fregionText.setEditable(false); //***** disable this box until support is added for user-entries - Lone Jedi

		Utility.buildConstraints(c, 4, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_birthplace") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 5, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(birthplaceText, c);
		northPanel.add(birthplaceText);

		Utility.buildConstraints(c, 6, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(birthplaceBox, c);
		northPanel.add(birthplaceBox);

		Utility.buildConstraints(c, 4, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_phobias") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 5, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(phobiaText, c);
		northPanel.add(phobiaText);

		Utility.buildConstraints(c, 6, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(phobiaBox, c);
		northPanel.add(phobiaBox);

		Utility.buildConstraints(c, 4, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_personality") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 5, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality1Text, c);
		northPanel.add(personality1Text);

		Utility.buildConstraints(c, 6, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality1Box, c);
		northPanel.add(personality1Box);

		Utility.buildConstraints(c, 7, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_eye") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 8, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(eyeColorText, c);
		northPanel.add(eyeColorText);

		Utility.buildConstraints(c, 9, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(eyeColorBox, c);
		northPanel.add(eyeColorBox);

		Utility.buildConstraints(c, 4, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_interest") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 5, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(interestsText, c);
		northPanel.add(interestsText);

		Utility.buildConstraints(c, 6, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(interestsBox, c);
		northPanel.add(interestsBox);

		Utility.buildConstraints(c, 4, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_personality") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 5, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality2Text, c);
		northPanel.add(personality2Text);

		Utility.buildConstraints(c, 6, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(personality2Box, c);
		northPanel.add(personality2Box);

		Utility.buildConstraints(c, 7, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_hair") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 8, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairColorText, c);
		northPanel.add(hairColorText);

		Utility.buildConstraints(c, 9, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairColorBox, c);
		northPanel.add(hairColorBox);

		Utility.buildConstraints(c, 0, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_home") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		////need to get Region info
		Utility.buildConstraints(c, 1, 7, 2, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(residenceText, c);
		northPanel.add(residenceText);

		Utility.buildConstraints(c, 3, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(residenceBox, c);
		northPanel.add(residenceBox);

		Utility.buildConstraints(c, 7, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_speech") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 8, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(speechPatternText, c);
		northPanel.add(speechPatternText);

		Utility.buildConstraints(c, 9, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(speechPatternBox, c);
		northPanel.add(speechPatternBox);

		Utility.buildConstraints(c, 7, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_style") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 8, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairStyleText, c);
		northPanel.add(hairStyleText);

		Utility.buildConstraints(c, 9, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(hairStyleBox, c);
		northPanel.add(hairStyleBox);

		Utility.buildConstraints(c, 0, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_location") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 1, 6, 2, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(locationText, c);
		northPanel.add(locationText);

		Utility.buildConstraints(c, 3, 6, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(locationBox, c);
		northPanel.add(locationBox);

		Utility.buildConstraints(c, 4, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_phrase") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 5, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(catchPhraseText, c);
		northPanel.add(catchPhraseText);

		Utility.buildConstraints(c, 6, 7, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(catchPhraseBox, c);
		northPanel.add(catchPhraseBox);

		Utility.buildConstraints(c, 0, 5, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(LanguageBundle.getString("in_birthday") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 1, 5, 2, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(birthdayText, c);
		northPanel.add(birthdayText);

		centerNorthPanel.setLayout(new BorderLayout());

		JPanel pane = new JPanel(new FlowLayout());

		checkAll = new JButton(LanguageBundle.getString("in_checkButton"));
		pane.add(checkAll);

		randAll = new JButton(LanguageBundle.getString("in_randomButton"));
		pane.add(randAll);

		uncheckAll = new JButton(LanguageBundle.getString("in_uncheckButton"));
		pane.add(uncheckAll);

		centerNorthPanel.add(pane, BorderLayout.NORTH);

		// Set Sizes of everything
		txtName.setMinimumSize(new Dimension(110, 21));
		txtName.setPreferredSize(new Dimension(200, 21));
		playerNameText.setMinimumSize(new Dimension(110, 21));
		playerNameText.setPreferredSize(new Dimension(200, 21));
		htText.setMinimumSize(new Dimension(110, 21));
		htText.setPreferredSize(new Dimension(200, 21));
		wtText.setMinimumSize(new Dimension(110, 21));
		wtText.setPreferredSize(new Dimension(200, 21));
		ageText.setMinimumSize(new Dimension(110, 21));
		ageText.setPreferredSize(new Dimension(200, 21));
		genderComboBox.setMinimumSize(new Dimension(110, 21));
		genderComboBox.setPreferredSize(new Dimension(200, 21));
		handedComboBox.setMinimumSize(new Dimension(110, 21));
		handedComboBox.setPreferredSize(new Dimension(200, 21));
		skinText.setMinimumSize(new Dimension(110, 21));
		skinText.setPreferredSize(new Dimension(200, 21));
		eyeColorText.setMinimumSize(new Dimension(110, 21));
		eyeColorText.setPreferredSize(new Dimension(200, 21));
		hairColorText.setMinimumSize(new Dimension(110, 21));
		hairColorText.setPreferredSize(new Dimension(200, 21));
		hairStyleText.setMinimumSize(new Dimension(110, 21));
		hairStyleText.setPreferredSize(new Dimension(200, 21));
		speechPatternText.setMinimumSize(new Dimension(110, 21));
		speechPatternText.setPreferredSize(new Dimension(200, 21));
		phobiaText.setMinimumSize(new Dimension(110, 21));
		phobiaText.setPreferredSize(new Dimension(200, 21));
		interestsText.setMinimumSize(new Dimension(110, 21));
		interestsText.setPreferredSize(new Dimension(200, 21));
		catchPhraseText.setMinimumSize(new Dimension(110, 21));
		catchPhraseText.setPreferredSize(new Dimension(200, 21));
		personality1Text.setMinimumSize(new Dimension(110, 21));
		personality1Text.setPreferredSize(new Dimension(200, 21));
		personality2Text.setMinimumSize(new Dimension(110, 21));
		personality2Text.setPreferredSize(new Dimension(200, 21));
		fregionText.setMinimumSize(new Dimension(110, 21));
		fregionText.setPreferredSize(new Dimension(200, 21));
		residenceText.setMinimumSize(new Dimension(110, 21));
		residenceText.setPreferredSize(new Dimension(200, 21));
		locationText.setMinimumSize(new Dimension(110, 21));
		locationText.setPreferredSize(new Dimension(200, 21));
		birthplaceText.setMinimumSize(new Dimension(110, 21));
		birthplaceText.setPreferredSize(new Dimension(200, 21));
		randAll.setMinimumSize(new Dimension(90, 25));
		randAll.setPreferredSize(new Dimension(90, 25));
		checkAll.setMinimumSize(new Dimension(90, 25));
		checkAll.setPreferredSize(new Dimension(90, 25));
		uncheckAll.setMinimumSize(new Dimension(110, 25));
		uncheckAll.setPreferredSize(new Dimension(110, 25));
		randName.setMinimumSize(new Dimension(90, 21));
		randName.setPreferredSize(new Dimension(90, 21));

		// Notes code
		setLayout(new BorderLayout());
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		addButton = new JButton(LanguageBundle.getString("in_add"));
		deleteButton = new JButton(LanguageBundle.getString("in_delete"));
		renameButton = new JButton(LanguageBundle.getString("in_rename"));
		revertButton = new JButton(LanguageBundle.getString("in_revert"));
		moveButton = new JButton(LanguageBundle.getString("in_move"));

		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(renameButton);
		buttonPanel.add(moveButton);
		buttonPanel.add(revertButton);

		establishTreeNodes(null, null);
		notesModel = new DefaultTreeModel(rootTreeNode);
		notesTree = new JTree(notesModel);
		notesTree.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		notesScroll = new JScrollPane(notesTree);
		notesScroll.setViewportView(notesTree);

		dataText = new JTextPane();
		dataText.setEditable(true);
		dataText.setText("");
		dataScroll = new JScrollPane(dataText);
		dataScroll.setViewportView(dataText);

		/*
		 * have a JEditorPane and a PortraitChooser
		 * at the "same" location
		 *
		 * author: Thomas Behr 10-09-02
		 */
		portrait = new PortraitChooser(null);

		dataLayout = new CardLayout();
		dataPanel = new JPanel();
		dataPanel.setLayout(dataLayout);
		dataPanel.add(dataScroll, LanguageBundle.getString("in_notes"));
		dataPanel.add(portrait, LanguageBundle.getString("in_portraits"));

		splitPane =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, notesScroll,
					dataPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(100);

		TitledBorder title1 =
				BorderFactory.createTitledBorder(LanguageBundle
					.getString("in_notes"));
		title1.setTitleJustification(TitledBorder.LEFT);
		centerCenterPanel.setBorder(title1);

		centerCenterPanel.setLayout(new BorderLayout());
		centerCenterPanel.add(splitPane, BorderLayout.CENTER);
		centerCenterPanel.add(buttonPanel, BorderLayout.SOUTH);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
		centerPanel.add(centerNorthPanel, BorderLayout.NORTH);
		this.setLayout(new BorderLayout());
		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);

		Utility.setDescription(ageBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(htwtBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(skinBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(hairColorBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(hairStyleBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(eyeColorBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(speechPatternBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(phobiaBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(interestsBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(catchPhraseBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(personality1Box, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(personality2Box, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(residenceBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(locationBox, LanguageBundle
			.getString("in_randCheckTipString"));
		Utility.setDescription(randName, LanguageBundle
			.getString("in_randNameTipString"));
		Utility.setDescription(randAll, LanguageBundle
			.getString("in_randTraitTipString"));
		Utility.setDescription(checkAll, LanguageBundle
			.getString("in_checkTipString"));
		Utility.setDescription(uncheckAll, LanguageBundle
			.getString("in_uncheckTipString"));

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});

		addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});
		readyForRefresh = true;
	}

	/**
	 * This method creates and registers all of the action listeners for the
	 * description and notes buttons as well as the focus listener for the
	 * notes text control and the mouse click listener for the notes tree.
	 * NB: Handlers for the document change and combo actions are handled
	 * separately as they need to be shut off when we are switching
	 * characters or otherwise updating the text data programatically.
	 */
	private void initNonDataDrivenEventListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});

		txtName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				txtName_Changed();
				labelName.requestFocus();
			}
		});

		randName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (nameFrame == null)
				{
					nameFrame = new NameGui(pc);
				}
				else
				{
					nameFrame.setPc(pc);
				}

				nameFrame.setVisible(true);
			}
		});

		InputVerifier inputVerify = new InputVerifier()
		{
			@Override
			public boolean shouldYieldFocus(JComponent input)
			{
				boolean valueOk = verify(input);
				updateTextFields(input);
				return valueOk;
			}

			@Override
			public boolean verify(JComponent input)
			{
				return true;
			}
		};
		skinText.setInputVerifier(inputVerify);
		hairColorText.setInputVerifier(inputVerify);
		hairStyleText.setInputVerifier(inputVerify);
		eyeColorText.setInputVerifier(inputVerify);
		speechPatternText.setInputVerifier(inputVerify);
		phobiaText.setInputVerifier(inputVerify);
		interestsText.setInputVerifier(inputVerify);
		catchPhraseText.setInputVerifier(inputVerify);
		personality1Text.setInputVerifier(inputVerify);
		personality2Text.setInputVerifier(inputVerify);
		fregionText.setInputVerifier(inputVerify);
		residenceText.setInputVerifier(inputVerify);
		locationText.setInputVerifier(inputVerify);
		birthdayText.setInputVerifier(inputVerify);
		birthplaceText.setInputVerifier(inputVerify);
		ageText.setInputVerifier(inputVerify);
		htText.setInputVerifier(inputVerify);
		wtText.setInputVerifier(inputVerify);
		playerNameText.setInputVerifier(inputVerify);
		txtName.setInputVerifier(inputVerify);

		checkAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				checkAll_click();
			}
		});

		randAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				randAll_click();
				pc.setDirty(true);
			}
		});

		uncheckAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				uncheckAll_click();
			}
		});

		MouseListener ml = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				final int selRow =
						notesTree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath =
						notesTree.getPathForLocation(e.getX(), e.getY());

				if (selRow != -1)
				{
					if ((e.getClickCount() == 1) && (selPath != null))
					{
						selectNotesNode(selRow);
					}
				}

				lastItem = null;
			}
		};

		notesTree.addMouseListener(ml);

		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int parentId = -1;
				int newNodeId = 0;

				final TreePath selPath = notesTree.getSelectionPath();

				if (selPath == null)
				{
					return;
				}

				NoteTreeNode parentTreeNode =
						(NoteTreeNode) selPath.getLastPathComponent();

				if ((parentTreeNode != null)
					&& (parentTreeNode.getItem() != null))
				{
					parentId = parentTreeNode.getItem().getId();
				}

				/*
				 * The portrait note may not have children
				 *
				 * author: Thomas Behr 10-09-02
				 */
				if (parentId == PORTRAIT_NOTEID)
				{
					JOptionPane.showMessageDialog(null,
						in_noPortraitChildrenMessage);

					return;
				}

				for (NoteItem currItem : pc.getNotesList())
				{
					if (currItem.getId() > newNodeId)
					{
						newNodeId = currItem.getId();
					}
				}

				++newNodeId;

				NoteItem a =
						new NoteItem(newNodeId, parentId, LanguageBundle
							.getString("in_newItem"), LanguageBundle
							.getString("in_newValue"));
				NoteTreeNode aNode = new NoteTreeNode(a);

				if (parentTreeNode != null)
				{
					parentTreeNode.add(aNode);
				}

				pc.addNotesItem(a);
				pc.setDirty(true);
				notesTree.expandPath(selPath);
				notesTree.updateUI();
			}
		});
		Utility.setDescription(addButton, LanguageBundle
			.getString("in_addChild"));
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int numChildren = 0;
				int reallyDelete;

				final TreePath selPath = notesTree.getSelectionPath();

				if (selPath == null)
				{
					return;
				}

				Object anObject = selPath.getLastPathComponent();

				if ((anObject == null)
					|| (((NoteTreeNode) anObject).getItem() == null))
				{
					return;
				}

				NoteTreeNode aNode = (NoteTreeNode) anObject;

				/*
				 * The portrait note may not be removed
				 *
				 * author: Thomas Behr 10-09-02
				 */
				if (aNode.getItem().getId() == PORTRAIT_NOTEID)
				{
					JOptionPane.showMessageDialog(null,
						in_noPortraitDeletionMessage);

					return;
				}

				Enumeration<NoteTreeNode> allChildren =
						aNode.breadthFirstEnumeration();

				while (allChildren.hasMoreElements())
				{
					NoteTreeNode ancestorNode = allChildren.nextElement();

					if (ancestorNode != aNode)
					{
						numChildren++;
					}
				}

				String message;
				if (numChildren > 0)
				{
					message =
							LanguageBundle.getFormattedString("in_delNote1",
								aNode.toString(), String.valueOf(numChildren));
				}
				else
				{
					message =
							LanguageBundle.getFormattedString("in_delNote2",
								aNode.toString());
				}

				//The following line should be taken out and shot!
				reallyDelete =
						JOptionPane.showConfirmDialog(null, message,
							LanguageBundle.getString("in_delNote4"),
							JOptionPane.OK_CANCEL_OPTION);

				if (reallyDelete == JOptionPane.OK_OPTION)
				{
					NoteTreeNode parent = (NoteTreeNode) aNode.getParent();

					if (parent != null)
					{
						allChildren = aNode.breadthFirstEnumeration();

						while (allChildren.hasMoreElements())
						{
							NoteTreeNode ancestorNode =
									allChildren.nextElement();
							pc.getNotesList().remove(ancestorNode.getItem());
						}

						parent.remove(aNode);
						pc.setDirty(true);
					}

					notesTree.updateUI();
				}
			}
		});
		Utility.setDescription(deleteButton, LanguageBundle
			.getString("in_delSelIt"));
		renameButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final TreePath selPath = notesTree.getSelectionPath();

				if (selPath == null)
				{
					return;
				}

				Object anObject = selPath.getLastPathComponent();

				if ((anObject == null)
					|| (((NoteTreeNode) anObject).getItem() == null))
				{
					return;
				}

				NoteTreeNode aNode = (NoteTreeNode) anObject;

				/*
				 * The portrait note may not be renamed
				 *
				 * author: Thomas Behr 10-09-02
				 */
				if (aNode.getItem().getId() == PORTRAIT_NOTEID)
				{
					JOptionPane.showMessageDialog(null,
						in_noPortraitRenamingMessage);

					return;
				}

				String selectedValue =
						JOptionPane.showInputDialog(null, LanguageBundle
							.getString("in_idEnNewName"), Constants.APPLICATION_NAME,
							JOptionPane.QUESTION_MESSAGE);

				if ((selectedValue != null)
					&& (selectedValue.trim().length() > 0))
				{
					aNode.getItem().setName(selectedValue.trim());
					pc.setDirty(true);
					notesTree.updateUI();
				}
			}
		});
		Utility.setDescription(renameButton, LanguageBundle
			.getString("in_idRenSelIt"));
		revertButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		});
		Utility.setDescription(revertButton, LanguageBundle
			.getString("in_idLoseChan"));
		revertButton.setEnabled(false); // not coded yet
		moveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				lastItem = currentItem;
			}
		});
		Utility.setDescription(moveButton, LanguageBundle
			.getString("in_idSwitch"));

		dataText.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent evt)
			{
				updateNoteItem();
			}
		});

		notesTree.addMouseListener(new NotePopupListener(notesTree,
			new NotePopupMenu()));
	}

	/**
	 * This function is called when the "Random" button is clicked and
	 * randomly generates traits/colors for those fields which have the
	 * random checkbox checked. Not all fields can be randomly generated
	 * though.
	 */
	private void randAll_click()
	{
		int roll;

		//		final ArrayList globalColorList = Globals.getColorList();
		final List<String> globalTraitList =
				SystemCollections.getUnmodifiableTraitList();
		final List<String> globalPhobiaList =
				SystemCollections.getUnmodifiablePhobiaList();
		final List<String> globalLocationList =
				SystemCollections.getUnmodifiableLocationList();
		final List<String> globalInterestsList =
				SystemCollections.getUnmodifiableInterestsList();
		final List<String> globalPhraseList =
				SystemCollections.getUnmodifiablePhraseList();
		final List<String> globalHairStyleList =
				SystemCollections.getUnmodifiableHairStyleList();
		final List<String> globalSpeechList =
				SystemCollections.getUnmodifiableSpeechList();
		final List<String> globalCityList =
				SystemCollections.getUnmodifiableCityList();
		final List<String> globalBirthplaceList =
				SystemCollections.getUnmodifiableBirthplaceList();

		StringBuffer randomString = new StringBuffer();

		if (eyeColorBox.isSelected())
		{
			randomString.append("EYES.");
		}

		if (hairColorBox.isSelected())
		{
			randomString.append("HAIR.");
		}

		if (skinBox.isSelected())
		{
			randomString.append("SKIN.");
		}

		int iSize = globalTraitList.size();

		if ((iSize != 0) && personality1Box.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			personality1Text.setText(globalTraitList.get(roll));
		}

		if ((iSize != 0) && personality2Box.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			personality2Text.setText(globalTraitList.get(roll));
		}

		iSize = globalPhobiaList.size();

		if ((iSize != 0) && phobiaBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			phobiaText.setText(globalPhobiaList.get(roll));
		}

		iSize = globalLocationList.size();

		if ((iSize != 0) && locationBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			locationText.setText(globalLocationList.get(roll));
		}

		iSize = globalInterestsList.size();

		if ((iSize != 0) && interestsBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			interestsText.setText(globalInterestsList.get(roll));
		}

		iSize = globalPhraseList.size();

		if ((iSize != 0) && catchPhraseBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			catchPhraseText.setText(globalPhraseList.get(roll));
		}

		iSize = globalHairStyleList.size();

		if ((iSize != 0) && hairStyleBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			hairStyleText.setText(globalHairStyleList.get(roll));
		}

		iSize = globalSpeechList.size();

		if ((iSize != 0) && speechPatternBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			speechPatternText.setText(globalSpeechList.get(roll));
		}

		if (htwtBox.isSelected())
		{
			randomString.append("HT.WT.");
		}

		if (ageBox.isSelected())
		{
			final String ageCategory = (String) ageComboBox.getSelectedItem();

			if (ageCategory != null)
			{
				final int idx = pc.getBioSet().getAgeSetNamed(ageCategory);

				if (idx >= 0)
				{
					randomString.append("AGECAT").append(idx).append('.');
				}
			}
			else
			{
				randomString.append("AGE.");
			}
		}

		iSize = globalCityList.size();

		if ((iSize != 0) && residenceBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			residenceText.setText(globalCityList.get(roll));
		}

		iSize = globalBirthplaceList.size();

		if ((iSize != 0) && birthplaceBox.isSelected())
		{
			roll = Globals.getRandomInt();

			if (roll < 0)
			{
				roll = -roll;
			}

			roll %= iSize;
			birthplaceText.setText(globalBirthplaceList.get(roll));
		}

		updateTextFields();

		if (randomString.length() > 0)
		{
			pc.getBioSet().randomize(randomString.toString(), pc);
			refreshDisplay();
		}
	}

	/**
	 * Refresh the display with the new character info
	 * It is assumed that the caller has turned off any listeners
	 * that may react to this change in data
	 */
	private void refreshDisplay()
	{
		final Race pcRace = pc.getRace();

		if (!pcRace.equals(Globals.s_EMPTYRACE))
		{
			handedComboBox.setSelectedItem(pc.getDisplay().getHanded());
			genderComboBox.setSelectedItem(pc.getGenderObject());
			genderComboBox.setEnabled(pc.canSetGender());
			wtText.setText(Globals.getGameModeUnitSet().displayWeightInUnitSet(
				pc.getWeight()));
			htText.setText(Globals.getGameModeUnitSet().displayHeightInUnitSet(
				pc.getHeight()));

			if ("ftin".equals(Globals.getGameModeUnitSet().getHeightUnit()))
			{
				labelHeight.setText(LanguageBundle.getString("in_height")
					+ " (in.): ");
			}
			else
			{
				labelHeight.setText(LanguageBundle.getString("in_height")
					+ " (" + Globals.getGameModeUnitSet().getHeightUnit()
					+ "): ");
			}

			labelWeight.setText(LanguageBundle.getString("in_weight") + " ("
				+ Globals.getGameModeUnitSet().getWeightUnit() + "): ");
		}

		updateDisplayedAge();
		playerNameText.setText(pc.getPlayersName());
		txtName.setText(pc.getName());
		skinText.setText(pc.getDisplay().getSkinColor());
		fregionText.setText(pc.getFullRegion());
		hairColorText.setText(pc.getDisplay().getHairColor());
		hairStyleText.setText(pc.getDisplay().getHairStyle());
		eyeColorText.setText(pc.getDisplay().getEyeColor());
		speechPatternText.setText(pc.getDisplay().getSpeechTendency());
		phobiaText.setText(pc.getDisplay().getPhobias());
		interestsText.setText(pc.getDisplay().getInterests());
		catchPhraseText.setText(pc.getDisplay().getCatchPhrase());
		personality1Text.setText(pc.getDisplay().getTrait1());
		personality2Text.setText(pc.getDisplay().getTrait2());
		residenceText.setText(pc.getResidence());
		locationText.setText(pc.getDisplay().getLocation());
		birthplaceText.setText(pc.getBirthplace());
		birthdayText.setText(pc.getDisplay().getBirthday());

		if (portrait != null)
		{
			portrait.refresh(pc);
		}
	}

	/**
	 * Select the notes entry at the specified row in the nodes tree.
	 * Will update the stored value of the currently displayed note
	 * before moving to the specified note. In addition this method
	 * will swap the currently selected node and the specified node,
	 * if the move action has ben requested.
	 *
	 * @param rowNum The row number in the notes tree of the note to be displayed.
	 */
	private void selectNotesNode(int rowNum)
	{
		stopListeners();

		notesTree.requestFocus();
		notesTree.setSelectionRow(rowNum);

		final TreePath path = notesTree.getSelectionPath();
		Object anObj = path.getLastPathComponent();

		if ((anObj != null) && (anObj instanceof NoteTreeNode))
		{
			if (currentItem != null)
			{
				updateNoteItem();
			}

			final NoteItem selectedItem = ((NoteTreeNode) anObj).getItem();
			currentItem = selectedItem;

			/*
			 * switch cards to display portrait chooser when appropriate
			 *
			 * author: Thomas Behr 10-09-02
			 */
			if ((currentItem != null)
				&& (currentItem.getId() == PORTRAIT_NOTEID))
			{
				dataLayout.last(dataPanel);
			}
			else
			{
				dataLayout.first(dataPanel);
			}

			if (selectedItem != null)
			{
				dataText.setText(currentItem.getValue());

				if (lastItem != null) // exchange places
				{
					int oldParent = currentItem.getParentId();
					currentItem.setParentId(lastItem.getParentId());
					lastItem.setParentId(oldParent);
					establishTreeNodes(null, null);
					notesModel.setRoot(rootTreeNode);
					notesTree.updateUI();
				}

				dataText.setEnabled(true);
				dataText.setEditable(true);
			}
			else
			{
				dataText.setText(LanguageBundle.getString("in_idNoteEdit"));
				dataText.setEnabled(false);
				dataText.setEditable(false);
			}

			dataText.setCaretPosition(0);
		}

		startListeners();
	}

	/**
	 * Start the listeners that track changing data. These have to
	 * be stopped when updating data programatically to avoid
	 * spurious setting of dirty flags etc.
	 */
	private void startListeners()
	{
		handedComboBox.addActionListener(al1);
		genderComboBox.addActionListener(al2);
		if (dataText != null && dataText.getDocument() != null)
		{
			dataText.getDocument().addDocumentListener(noteChangeListener);
		}
	}

	/**
	 * Stop the listeners that track changing data. These have to
	 * be stopped when updating data programatically to avoid
	 * spurious setting of dirty flags etc.
	 */
	private void stopListeners()
	{
		handedComboBox.removeActionListener(al1);
		genderComboBox.removeActionListener(al2);
		if (dataText != null && dataText.getDocument() != null)
		{
			dataText.getDocument().removeDocumentListener(noteChangeListener);
		}
	}

	/**
	 *  This method takes the name entered in the txtName field and makes it the
	 *  name of the active tab.
	 */
	private void txtName_Changed()
	{
		if (pc != null)
		{
			pc.setName(txtName.getText());
			PCGen_Frame1.forceUpdate_PlayerTabs();
		}
	}

	/**
	 * This function is called when the "Uncheck All" button is clicked.
	 * It clears all of the random checkboxes.
	 */
	private void uncheckAll_click()
	{
		ageBox.setSelected(false);
		htwtBox.setSelected(false);
		skinBox.setSelected(false);
		hairColorBox.setSelected(false);
		hairStyleBox.setSelected(false);
		eyeColorBox.setSelected(false);
		speechPatternBox.setSelected(false);
		phobiaBox.setSelected(false);
		interestsBox.setSelected(false);
		catchPhraseBox.setSelected(false);
		personality1Box.setSelected(false);
		personality2Box.setSelected(false);
		residenceBox.setSelected(false);
		locationBox.setSelected(false);
		birthplaceBox.setSelected(false);
	}

	private void updateCharacterInfo()
	{
		stopListeners();

		// First off store the existing value
		if ((pc != null) && (currentItem != null))
		{
			updateNoteItem();
		}

		currentItem = null;
		dataText.setText("");

		if (pc == null)
		{
			startListeners();

			return;
		}

		refreshDisplay();

		establishTreeNodes(null, null);
		notesModel.setRoot(rootTreeNode);
		notesTree.updateUI();
		startListeners();

		selectNotesNode(1);
	}

	private void updateDisplayedAge()
	{
		int selIdx = -1;
		ageText.setText(Integer.toString(pc.getAge()));

		final Race pcRace = pc.getRace();

		if ((pcRace != null) && !pcRace.equals(Globals.s_EMPTYRACE))
		{
			AgeSet ageSet = pc.getAgeSet();

			if (ageSet != null)
			{
				//
				// setSelectedItem doesn't change selection if  entry is
				// not found in list, so do this the hard way...
				//
				for (int i = 0; i < ageComboBox.getModel().getSize(); ++i)
				{
					if (ageSet.getName().equals(ageComboBox.getModel().getElementAt(i)))
					{
						selIdx = i;

						break;
					}
				}
			}
		}

		bEditingAge = true;
		ageComboBox.setSelectedIndex(selIdx);
		bEditingAge = false;
	}

	private void updateNoteItem()
	{
		if ((currentItem != null) && textIsDirty)
		{
			int x = pc.getNotesList().indexOf(currentItem);
			currentItem.setValue(dataText.getText());

			if (x > -1)
			{
				pc.getNotesList().get(x).setValue(dataText.getText());
				pc.setDirty(true);
			}
			else if (currentItem == bioNote)
			{
				pc.setBio(dataText.getText());
				pc.setDirty(true);
			}
			else if (currentItem == descriptionNote)
			{
				pc.setDescription(dataText.getText());
				pc.setDirty(true);
			}
			else if (currentItem == companionNote)
			{
				pc.setStringFor(StringKey.MISC_COMPANIONS, dataText.getText());
				pc.setDirty(true);
			}
			else if (currentItem == otherAssetsNote)
			{
				pc.setStringFor(StringKey.MISC_ASSETS, dataText.getText());
				pc.setDirty(true);
			}
			else if (currentItem == magicItemsNote)
			{
				pc.setStringFor(StringKey.MISC_MAGIC, dataText.getText());
				pc.setDirty(true);
			}
			else if (currentItem == dmNote)
			{
				pc.setStringFor(StringKey.MISC_DM, dataText.getText());
				pc.setDirty(true);
			}

			textIsDirty = false;
		}
	}

	private void updateTextFields()
	{
		pc.setSkinColor(skinText.getText());
		pc.setHairColor(hairColorText.getText());
		pc.setHairStyle(hairStyleText.getText());
		pc.setEyeColor(eyeColorText.getText());
		pc.setSpeechTendency(speechPatternText.getText());
		pc.setPhobias(phobiaText.getText());
		pc.setInterests(interestsText.getText());
		pc.setCatchPhrase(catchPhraseText.getText());
		pc.setTrait1(personality1Text.getText());
		pc.setTrait2(personality2Text.getText());

		//*****this method doesn't exist yet, need one that can interpret subregions - Lone Jedi
		//aPC.setFullRegion(fregionText.getText());
		pc.setResidence(residenceText.getText());
		pc.setLocation(locationText.getText());
		pc.setBirthplace(birthplaceText.getText());
		pc.setBirthday(birthdayText.getText());
		pc.setAge(Delta.parseInt("0" + ageText.getText()));
		pc.setHeight(Globals.getGameModeUnitSet().convertHeightFromUnitSet(
			Delta.parseDouble("0" + htText.getText())));
		pc
			.setWeight((int) Globals.getGameModeUnitSet()
				.convertWeightFromUnitSet(
					Delta.parseDouble("0" + wtText.getText())));
	}

	/**
	 * Update the value of the supplied field. Normally called as part of an 
	 * InputVerify action  
	 * @param input The field being modified.
	 */
	private void updateTextFields(JComponent input)
	{
		if (input == skinText)
		{
			pc.setSkinColor(skinText.getText());
		}
		else if (input == hairColorText)
		{
			pc.setHairColor(hairColorText.getText());
		}
		else if (input == hairStyleText)
		{
			pc.setHairStyle(hairStyleText.getText());
		}
		else if (input == eyeColorText)
		{
			pc.setEyeColor(eyeColorText.getText());
		}
		else if (input == speechPatternText)
		{
			pc.setSpeechTendency(speechPatternText.getText());
		}
		else if (input == phobiaText)
		{
			pc.setPhobias(phobiaText.getText());
		}
		else if (input == interestsText)
		{
			pc.setInterests(interestsText.getText());
		}
		else if (input == catchPhraseText)
		{
			pc.setCatchPhrase(catchPhraseText.getText());
		}
		else if (input == personality1Text)
		{
			pc.setTrait1(personality1Text.getText());
		}
		else if (input == personality2Text)
		{
			pc.setTrait2(personality2Text.getText());
		}
		else if (input == residenceText)
		{
			pc.setResidence(residenceText.getText());
		}
		else if (input == locationText)
		{
			pc.setLocation(locationText.getText());
		}
		else if (input == birthplaceText)
		{
			pc.setBirthplace(birthplaceText.getText());
		}
		else if (input == birthdayText)
		{
			pc.setBirthday(birthdayText.getText());
		}
		else if (input == ageText)
		{
			pc.setDirty(true);
			pc.setAge(Delta.parseInt("0" + ageText.getText()));
			updateDisplayedAge();
		}
		else if (input == htText)
		{
			pc.setDirty(true);
			pc.setHeight(Globals.getGameModeUnitSet().convertHeightFromUnitSet(
				Delta.parseDouble("0" + htText.getText())));
		}
		else if (input == wtText)
		{
			pc.setDirty(true);
			pc.setWeight((int) Globals.getGameModeUnitSet()
				.convertWeightFromUnitSet(
					Delta.parseDouble("0" + wtText.getText())));
		}
		else if (input == playerNameText)
		{
			pc.setPlayersName(playerNameText.getText());
		}
		else if (input == txtName)
		{
			pc.setName(txtName.getText());
			PCGen_Frame1.forceUpdate_PlayerTabs();
		}
	}

	// Notes popup menu listener
	private class NotePopupListener extends MouseAdapter
	{
		private JTree tree;
		private NotePopupMenu menu;

		NotePopupListener(JTree atree, NotePopupMenu aMenu)
		{
			tree = atree;
			menu = aMenu;

			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke =
								KeyStroke.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final JMenuItem menuItem =
									(JMenuItem) menu.getComponent(i);
							KeyStroke ks = menuItem.getAccelerator();

							if ((ks != null) && keyStroke.equals(ks))
							{
								menuItem.doClick(2);

								return;
							}
						}
					}

					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};

			tree.addKeyListener(myKeyListener);
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		@Override
		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				TreePath selPath =
						tree.getClosestPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	//Notes popup menu
	private class NotePopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = -8015559748421397718L;

		NotePopupMenu()
		{
			NotePopupMenu.this.add(createAddMenuItem(LanguageBundle
				.getString("in_add"), "shortcut EQUALS"));
			NotePopupMenu.this.add(createRemoveMenuItem(LanguageBundle
				.getString("in_remove"), "shortcut MINUS"));
			NotePopupMenu.this.add(createRenameMenuItem(LanguageBundle
				.getString("in_rename"), "alt M"));
			NotePopupMenu.this.add(createMoveMenuItem(LanguageBundle
				.getString("in_move"), "alt Z"));
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddNoteActionListener(),
				LanguageBundle.getString("in_add"), (char) 0, accelerator,
				LanguageBundle.getString("in_add"), "Add16.gif", true);
		}

		private JMenuItem createMoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new MoveNoteActionListener(),
				LanguageBundle.getString("in_move"), (char) 0, accelerator,
				LanguageBundle.getString("in_move"), "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new RemoveNoteActionListener(), LanguageBundle
					.getString("in_delete"), (char) 0, accelerator,
				LanguageBundle.getString("in_delete"), "Remove16.gif", true);
		}

		private JMenuItem createRenameMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new RenameNoteActionListener(), LanguageBundle
					.getString("in_rename"), (char) 0, accelerator,
				LanguageBundle.getString("in_rename"), "Add16.gif", true);
		}

		private class AddNoteActionListener extends NoteActionListener
		{
			AddNoteActionListener()
			{
				super();
			}

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				addButton.doClick();
			}
		}

		private class MoveNoteActionListener extends NoteActionListener
		{
			MoveNoteActionListener()
			{
				super();
			}

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				moveButton.doClick();
			}
		}

		private class NoteActionListener implements ActionListener
		{

			NoteActionListener()
			{
				// Do Nothing
			}

			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}

		private class RemoveNoteActionListener extends NoteActionListener
		{
			RemoveNoteActionListener()
			{
				super();
			}

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				deleteButton.doClick();
			}
		}

		private class RenameNoteActionListener extends NoteActionListener
		{
			RenameNoteActionListener()
			{
				super();
			}

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				renameButton.doClick();
			}
		}
	}

	/**
	 * A tree node dedicated to storing notes.
	 */
	private class NoteTreeNode extends DefaultMutableTreeNode
	{
		static final long serialVersionUID = -8015559748421397718L;
		private NoteItem item;

		NoteTreeNode(NoteItem x)
		{
			item = x;
		}

		@Override
		public String toString()
		{
			if (item != null)
			{
				return item.toString();
			}

			return pc.getDisplayName();
		}

		private final NoteItem getItem()
		{
			return item;
		}
	}
}
