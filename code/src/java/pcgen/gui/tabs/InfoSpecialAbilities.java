/*
 * InfoSpecialAbilities.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.CDOMChoiceManager;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.AddSpecialAbility;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Tab;

/**
 * This class is responsible for drawing Special Ability, Language and Weapon Prefociency sections.
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision$
 */
public final class InfoSpecialAbilities extends JPanel implements
		CharacterInfoTab
{
	static final long serialVersionUID = -7316622743996841985L;

	private static final Tab tab = Tab.SABILITIES;

	private JButton weaponButton = null;
	private JButton langButton = null;
	private JButton spAddButton = null;
	private JButton spRemButton = null;
	private JTextArea languageText = new JTextArea();
	private JTextArea saText = new JTextArea();
	private JTextArea weaponText = new JTextArea();

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	private JPanel lPanel;

	/**
	 * Constructor.
	 * @param pc
	 */
	public InfoSpecialAbilities(PlayerCharacter pc)
	{
		this.pc = pc;
		setName(tab.toString());
		initComponents();
		initActionListeners();
	}

	public void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			serial = pc.getSerial();
			resetLangButtons();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Abilities.Order", tab
			.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Abilities.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown()
	{
		return SettingsHandler.isAbilitiesShownAsATab();
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();

		if (pc.getDisplay().getTotalLevels() <= 1
			|| Globals.checkRule(RuleConstants.INTBONUSLANG))
		{
			int bonusLangCount = pc.getBonusLanguageCount();
			Ability a = Globals.getContext().ref
					.silentlyGetConstructedCDOMObject(Ability.class,
							AbilityCategory.LANGBONUS, "*LANGBONUS");
			int currentLangCount = pc.getDetailedAssociationCount(a);

			if (currentLangCount < bonusLangCount)
			{
				if (Globals.checkRule(RuleConstants.INTBONUSLANG))
				{
					toDoList.add(LanguageBundle
						.getString("in_isaTodoLangRemain")); //$NON-NLS-1$
				}
				else
				{
					toDoList.add(LanguageBundle
						.getString("in_isaTodoLangRemainFirstOnly")); //$NON-NLS-1$
				}
			}
			else if (currentLangCount > bonusLangCount)
			{
				toDoList
					.add(LanguageBundle.getString("in_isaTodoLangTooMany")); //$NON-NLS-1$
			}
		}

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
	 * <code>updateCharacterInfo</code> update data when changing PC.
	 */
	private void updateCharacterInfo()
	{
		if (pc == null)
		{
			return;
		}

		List<String> specialAbilities = pc.getSpecialAbilityTimesList();
		String languages = StringUtil.join(pc.getSortedLanguageSet(), ", ");

		if (specialAbilities.size() > 0)
		{
			saText.setText(StringUtil.join(specialAbilities, ", "));
		}
		else
		{
			saText.setText(Constants.NONE);
		}

		if (languages.length() > 0)
		{
			languageText.setText(languages);
		}
		else
		{
			languageText.setText(Constants.NONE);
		}

		showWeaponProfList();
	}

	private List<Object> getOptionalWeaponProficiencies()
	{
		if (pc != null)
		{
			List<Object> bonusCategory = new ArrayList<Object>();
			final Race pcRace = pc.getRace();

			if (pcRace != null)
			{
				if (pcRace.getListMods(WeaponProf.STARTING_LIST) != null)
				{
					bonusCategory.add(pcRace);
				}
			}

			for (PCClass aClass : pc.getClassSet())
			{
				if (aClass.getListMods(WeaponProf.STARTING_LIST) != null)
				{
					bonusCategory.add(aClass);
				}
			}

			for (PCTemplate aTemplate : pc.getTemplateSet())
			{
				if (aTemplate.getListMods(WeaponProf.STARTING_LIST) != null)
				{
					bonusCategory.add(aTemplate);
				}
			}

			for (Domain d : pc.getDomainSet())
			{
				if (d.get(ObjectKey.CHOOSE_INFO) != null
					&& WeaponProf.class.equals(d.get(ObjectKey.CHOOSE_INFO)
						.getClassIdentity().getChoiceClass()))
				{
					bonusCategory.add(d);
				}
			}

			return bonusCategory;
		}

		return null;
	}

	private void addSpecialAbility()
	{
		if ((pc == null) || !pc.hasClass())
		{
			return;
		}
		new AddSpecialAbility(pc, this);
		refresh();
	}

	private void ensureFocus()
	{
		//
		// Get focus in case the chooser popped up
		//
		getRootPane().getParent().requestFocus();
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(LanguageBundle
			.getString("in_iaLangTip"));
		refresh();
	}

	private void initActionListeners()
	{
		langButton.addActionListener(new racialLanguageButtonListener());
		spAddButton.addActionListener(new addSpecialButtonListener());
		spRemButton.addActionListener(new removeSpecialButtonListener());
		weaponButton.addActionListener(new weaponSelectButtonListener());
		addComponentListener(new componentShownAdapter());
		addFocusListener(new focusAdapter());
	}

	private void initComponents()
	{
		readyForRefresh = true;
		this.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gridbag = new GridBagLayout();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(gridbag);

		// Languages setup
		JPanel langPanel = new JPanel();
		langPanel.setLayout(new BorderLayout());

		resetLangButtons();
		langPanel.add(lPanel, BorderLayout.NORTH);

		JScrollPane languageScroll = new JScrollPane();
		languageText.setLineWrap(true);
		languageText.setWrapStyleWord(true);
		languageText.setEditable(false);
		languageScroll.setViewportView(languageText);
		langPanel.add(languageScroll, BorderLayout.CENTER);

		Utility.buildConstraints(gbc, 0, 0, 1, 1, 1.0, .5);
		gridbag.setConstraints(langPanel, gbc);
		bottomPanel.add(langPanel);

		// Special abilities panel setup
		JPanel specialPanel = new JPanel();
		specialPanel.setLayout(new BorderLayout());

		JPanel sPanel = new JPanel();
		sPanel.setLayout(new FlowLayout());
		sPanel.add(new JLabel(LanguageBundle.getString("in_specialAb")));

		spAddButton = new JButton(LanguageBundle.getString("in_add"));
		sPanel.add(spAddButton);

		spRemButton = new JButton(LanguageBundle.getString("in_remove"));
		sPanel.add(spRemButton);

		specialPanel.add(sPanel, BorderLayout.NORTH);

		JScrollPane saScroll = new JScrollPane();
		saText.setLineWrap(true);
		saText.setWrapStyleWord(true);
		saText.setEditable(false);
		saScroll.setViewportView(saText);
		specialPanel.add(saScroll, BorderLayout.CENTER);

		Utility.buildConstraints(gbc, 0, 1, 1, 1, 0.0, .5);
		gridbag.setConstraints(specialPanel, gbc);
		bottomPanel.add(specialPanel);

		// Weapon profs setup
		JPanel weaponProfPanel = new JPanel();
		weaponProfPanel.setLayout(new BorderLayout());

		JPanel bPanel = new JPanel();
		bPanel.setLayout(new FlowLayout());

		JLabel aLabel = new JLabel(LanguageBundle.getString("in_weaProfs"));
		bPanel.add(aLabel);
		weaponButton = new JButton(LanguageBundle.getString("in_optProfs"));
		pcgen.gui.utils.Utility.setDescription(weaponButton, LanguageBundle
			.getString("in_iaOptTip"));
		bPanel.add(weaponButton);
		weaponProfPanel.add(bPanel, BorderLayout.NORTH);

		weaponText.setLineWrap(true);
		weaponText.setWrapStyleWord(true);
		weaponText.setEditable(false);

		JScrollPane weaponScroll = new JScrollPane();
		weaponScroll.setViewportView(weaponText);
		weaponProfPanel.add(weaponScroll, BorderLayout.CENTER);

		Utility.buildConstraints(gbc, 0, 2, 1, 1, 0.0, .5);
		gridbag.setConstraints(weaponProfPanel, gbc);
		bottomPanel.add(weaponProfPanel);

		// Now finish the layout of the outer panel
		add(bottomPanel, BorderLayout.CENTER);
	}

	private void resetLangButtons()
	{
		lPanel = new JPanel();
		lPanel.setLayout(new FlowLayout());
		lPanel.add(new JLabel(LanguageBundle.getString("in_languages")));

		langButton = new JButton(LanguageBundle.getString("in_other"));

		if (pc != null)
		{
			for (Skill sk : Globals.getContext().ref.getConstructedCDOMObjects(Skill.class))
			{
				if (ChooseActivation.hasChooseToken(sk))
				{
					JButton button = new JButton(LanguageBundle
							.getString("in_skill")
							+ " " + sk.getOutputName());
					lPanel.add(button);
					button.addActionListener(new skillLanguageButtonListener(sk));
				}
			}
		}

		lPanel.add(langButton);
	}

	private void racialLanguageSelectPressed()
	{
		if (pc != null)
		{
			pc.setDirty(true);
			Ability a = Globals.getContext().ref
					.silentlyGetConstructedCDOMObject(Ability.class,
							AbilityCategory.LANGBONUS, "*LANGBONUS");

			ChooserUtilities.modChoices(a, new ArrayList<Language>(),
					new ArrayList<Language>(), true, pc, true,
					AbilityCategory.LANGBONUS);

			refresh();
			ensureFocus();
		}
	}

	private void removeSpecialAbility()
	{
		List<String> aList = new ArrayList<String>();
		List<String> bList = new ArrayList<String>();
		List<SpecialAbility> cList = new ArrayList<SpecialAbility>();

		for (PCClass aClass : pc.getClassSet())
		{
			List<? extends SpecialAbility> salist = pc.getUserSpecialAbilityList(aClass);
			if (salist != null)
			{
				for (SpecialAbility sa : salist)
				{
					aList.add(sa.getKeyName());
					cList.add(sa);
				}
			}
		}

		ChooserInterface lc = ChooserFactory.getChooserInstance();
		lc.setVisible(false);
		lc.setTitle(LanguageBundle.getString("in_iaReSpeAb"));
		lc.setMessageText(LanguageBundle.getString("in_iaSelSpeAb"));
		lc.setAvailableList(aList);
		lc.setSelectedList(bList);
		lc.setTotalChoicesAvail(aList.size());
		lc.setPoolFlag(false);
		lc.setVisible(true);

		for (String aString : (List<String>) lc.getSelectedList())
		{
			final int ix = aList.indexOf(aString);

			if ((ix < 0) || (ix >= cList.size()))
			{
				continue;
			}

			SpecialAbility sa = cList.get(ix);
			PCClass aClass = pc.getClassList().get(0);

			if (aClass == null)
			{
				continue;
			}

			pc.removeUserSpecialAbility(sa, aClass);
		}

		//		pc = null; // forces everything to re-display it's broken
		serial = 0; // forces everything to re-display works
		refresh();
		ensureFocus();
	}

	private void showWeaponProfList()
	{
		if (weaponButton != null)
		{
			List<Object> bonusCategory = getOptionalWeaponProficiencies();

			weaponButton.setEnabled((bonusCategory != null)
				&& (bonusCategory.size() > 0));
		}

		// This is now going to be sorted on key
		SortedSet<WeaponProf> weaponProfs = pc.getSortedWeaponProfs();
		StringBuffer buf;
		if (weaponProfs.size() > 0)
		{
			buf = new StringBuffer();
			boolean first = true;

			for (Iterator<WeaponProf> i = weaponProfs.iterator(); i.hasNext();)
			{
				final WeaponProf wp = i.next();
				if (wp == null)
				{
					continue;
				}
				if (first == false)
				{
					buf.append(", ");
				}
				buf.append(wp.toString());
				first = false;
			}
			weaponText.setText(buf.toString());
		}
		else
		{
			weaponText.setText(Constants.NONE);
		}

		weaponText.setCaretPosition(0);
	}

	private void skillLanguageSelectPressed(Skill sk)
	{
		ChooserUtilities.modChoices(sk, new ArrayList<Language>(),
				new ArrayList<Language>(), true, pc, true, null);
		pc.setDirty(true);
		refresh();
		ensureFocus();
	}

	/**
	 * This method is run when the weapon proficiency button is pressed.
	 */
	private void weaponSelectPressed()
	{
		if (pc != null)
		{
			List<Object> bonusCategory = getOptionalWeaponProficiencies();

			if (bonusCategory.size() == 0)
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle
					.getString("in_iaNoOptProfs"), Constants.APPLICATION_NAME,
					MessageType.INFORMATION);

				return;
			}

			int selIdx = 0;

			for (;;)
			{
				//
				// If there is only one set of choices allowed, then use it
				//
				Object profBonusObject;

				if (bonusCategory.size() == 1)
				{
					profBonusObject = bonusCategory.get(0);
				}
				else
				{
					//TODO: Is this right? This loop will execute exactly once. Either it will break inside the if, or it will return. XXX
					for (;;)
					{
						InputInterface ii = InputFactory.getInputInstance();
						Object selectedValue =
								ii.showInputDialog(null, LanguageBundle
									.getString("in_iaMultiChoice1")
									+ Constants.LINE_SEPARATOR
									+ LanguageBundle
										.getString("in_iaMultiChoice2"),
									Constants.APPLICATION_NAME,
									MessageType.INFORMATION, bonusCategory
										.toArray(), bonusCategory.get(selIdx));

						if (selectedValue != null)
						{
							profBonusObject = selectedValue;
							selIdx = bonusCategory.indexOf(selectedValue);

							break;
						}

						ensureFocus();

						return;
					}
				}

				if (profBonusObject instanceof Domain)
				{
					final Domain aDomain = (Domain) profBonusObject;
					ChooserUtilities.modChoices(aDomain, new ArrayList(),
						new ArrayList(), true, pc, true, null);
				}
				else
				{
					if (profBonusObject instanceof PCClass
							|| profBonusObject instanceof Race
							|| profBonusObject instanceof PCTemplate)
					{
						CDOMObject cdo = (CDOMObject) profBonusObject;
						Collection<CDOMReference<WeaponProf>> wplist = cdo
								.getListMods(WeaponProf.STARTING_LIST);
						ChooseInformation<WeaponProf> tc = new BasicChooseInformation<WeaponProf>(
								"WEAPONBONUS", new ReferenceChoiceSet<WeaponProf>(wplist));
						tc.setChoiceActor(WeaponProf.STARTING_ACTOR);
						CDOMChoiceManager<WeaponProf> mgr = new CDOMChoiceManager<WeaponProf>(
								cdo, tc, 1, 1);

						List<WeaponProf> availableList = new ArrayList<WeaponProf>();
						List<WeaponProf> selectedList = new ArrayList<WeaponProf>();
						mgr.getChoices(pc, availableList, selectedList);
						List<WeaponProf> newSelections = mgr.doChooser(pc,
								availableList, selectedList,
								new ArrayList<String>());
						mgr.applyChoices(pc, newSelections);
					}
				}

				pc.setDirty(true);

				pc.aggregateFeatList();

				showWeaponProfList();

				if (bonusCategory.size() == 1)
				{
					break;
				}
			}

			ensureFocus();

			return;
		}
	}

	private class racialLanguageButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			racialLanguageSelectPressed();
		}
	}

	private class skillLanguageButtonListener implements ActionListener
	{
		private final Skill skill;
		public skillLanguageButtonListener(Skill sk)
		{
			skill = sk;
		}

		public void actionPerformed(ActionEvent evt)
		{
			skillLanguageSelectPressed(skill);
		}
	}

	private class addSpecialButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			addSpecialAbility();
		}
	}

	private class removeSpecialButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			removeSpecialAbility();
		}
	}

	private class weaponSelectButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			weaponSelectPressed();
		}
	}

	private class componentShownAdapter extends ComponentAdapter
	{
		public void componentShown(ComponentEvent evt)
		{
			formComponentShown();
		}
	}

	private class focusAdapter extends FocusAdapter
	{
		public void focusGained(FocusEvent evt)
		{
			refresh();
		}
	}
}
