/*
 * Copyright 2009 (C) James Dempsey
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
 */
package pcgen.gui2.prefs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.GameMode;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code HouseRulesPanel} is responsible for
 * displaying the house rules preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public class HouseRulesPanel extends PCGenPrefsPanel
{
	private static final String in_houseRules =
		LanguageBundle.getString("in_Prefs_houseRules");
	private final Collection<RuleCheck> ruleCheckList;
	
	private static final String HOUSE_RULE_STR = "{0} ({1})";
	
	private JCheckBox[] hrBoxes = null;
	private ButtonGroup[] hrGroup = null;
	private JRadioButton[] hrRadio = null;

	/**
	 * Instantiates a new house rules panel.
	 */
	public HouseRulesPanel()
	{
		JPanel mainPanel = new JPanel();

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_houseRules);

		title1.setTitleJustification(TitledBorder.LEFT);
		mainPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		mainPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		// build a list of checkboxes from the current gameMode Rules
		int gridNum = 1;
		GameMode gameMode = SettingsHandler.getGame();
		ruleCheckList = gameMode.getModeContext().getReferenceContext()
				.getConstructedCDOMObjects(RuleCheck.class);

		// initialize all the checkboxes
		hrBoxes = new JCheckBox[ruleCheckList.size()];

		int excludeCount = 0;
		int boxNum = 0;

		for (RuleCheck aRule : ruleCheckList)
		{
			aRule.getName();
			String aKey = aRule.getKeyName();
			String aDesc = aRule.getDesc();
			boolean aBool = aRule.getDefault();

			if (aRule.isExclude())
			{
				++excludeCount;

				continue;
			}

			if (SettingsHandler.hasRuleCheck(aKey))
			{
				aBool = SettingsHandler.getRuleCheck(aKey);
			}

			hrBoxes[boxNum] = new JCheckBox(MessageFormat.format(HOUSE_RULE_STR, aDesc, aKey), aBool);
			hrBoxes[boxNum].setActionCommand(aKey);

			Utility.buildConstraints(c, 0, gridNum, 2, 1, 0, 0);
			gridbag.setConstraints(hrBoxes[boxNum], c);
			mainPanel.add(hrBoxes[boxNum]);
			++boxNum;
			++gridNum;
		}

		hrRadio = new JRadioButton[excludeCount];

		int exNum = 0;

		for (RuleCheck aRule : ruleCheckList)
		{
			aRule.getName();
			String aKey = aRule.getKeyName();
			aRule.getDesc();
			boolean aBool = aRule.getDefault();

			if (!aRule.isExclude())
			{
				continue;
			}

			hrRadio[exNum] = new JRadioButton(aKey);
			hrRadio[exNum].setActionCommand(aKey);

			if (SettingsHandler.hasRuleCheck(aKey))
			{
				aBool = SettingsHandler.getRuleCheck(aKey);
			}

			hrRadio[exNum].setSelected(aBool);
			++exNum;
		}

		hrGroup = new ButtonGroup[excludeCount];

		addRulesToPanel(mainPanel, gridbag, gridNum, gameMode);

		Utility.buildConstraints(c, 0, 60, GridBagConstraints.REMAINDER, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel();
		gridbag.setConstraints(label, c);
		mainPanel.add(label);

		this.setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		
	}

	/**
	 * Add a control to the panel for each of the game mode's house rule options.
	 * 
	 * @param mainPanel The panel to add the entries to.
	 * @param gridbag The panel's layout 
	 * @param gridNum The current row in the layout grid
	 * @param gameMode The game mode being processed
	 */
	private void addRulesToPanel(JPanel mainPanel, GridBagLayout gridbag,
			int gridNum, GameMode gameMode) 
	{
		int groupNum = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);
		
		List<String> doneList = new ArrayList<>();

		for (int i = 0; i < hrRadio.length; i++)
		{
			if (hrRadio[i] == null)
			{
				continue;
			}

			String aKey = hrRadio[i].getActionCommand();
			RuleCheck aRule = gameMode.getModeContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(RuleCheck.class, aKey);

			if (aRule == null)
			{
				continue;
			}
			if (doneList.contains(aKey))
			{
				continue;
			}

			CDOMSingleRef<RuleCheck> excludedRef = aRule.getExclude();
			if ((excludedRef != null)
					&& doneList.contains(excludedRef.getLSTformat(false)))
			{
				continue;
			}

			String aDesc = aRule.getDesc();

			hrGroup[groupNum] = new ButtonGroup();
			hrGroup[groupNum].add(hrRadio[i]);
			doneList.add(aKey);

			Utility.buildConstraints(c, 0, gridNum, 3, 1, 0, 0);

			JPanel subPanel = new JPanel();
			gridbag.setConstraints(subPanel, c);

			subPanel.setLayout(gridbag);

			GridBagConstraints cc = new GridBagConstraints();
			cc.fill = GridBagConstraints.HORIZONTAL;
			cc.insets = new Insets(0, 4, 0, 0);

			Border aBord = BorderFactory.createEtchedBorder();
			subPanel.setBorder(aBord);

			cc.anchor = GridBagConstraints.LINE_START;
			Utility.buildConstraints(cc, 0, 0, 2, 1, 2, 0);
			hrRadio[i].setText(MessageFormat.format(HOUSE_RULE_STR, aDesc, aKey));
			gridbag.setConstraints(hrRadio[i], cc);
			subPanel.add(hrRadio[i]);

			for (int ii = 0; ii < hrRadio.length; ii++)
			{
				if (hrRadio[i] == null)
				{
					continue;
				}

				String exKey = hrRadio[ii].getActionCommand();

				if ((excludedRef != null) && excludedRef.hasBeenResolved()
						&& exKey.equals(excludedRef.get().getKeyName()))
				{
					aRule = excludedRef.get();
					aDesc = aRule.getDesc();
					hrGroup[groupNum].add(hrRadio[ii]);
					doneList.add(excludedRef.getLSTformat(false));

					cc.anchor = GridBagConstraints.LINE_START;
					Utility.buildConstraints(cc, 0, 1, 2, 1, 2, 0);
					hrRadio[ii].setText(MessageFormat.format(HOUSE_RULE_STR, aDesc, exKey));
					gridbag.setConstraints(hrRadio[ii], cc);
					subPanel.add(hrRadio[ii]);
				}
			}

			mainPanel.add(subPanel);
			++gridNum;
			++groupNum;
		}
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_houseRules;
	}
	
	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#setOptionsBasedOnControls()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		final GameMode gameMode = SettingsHandler.getGame();

		for (int i = 0; i < hrBoxes.length; i++)
		{
			if (hrBoxes[i] != null)
			{
				String aKey = hrBoxes[i].getActionCommand();
				boolean aBool = hrBoxes[i].isSelected();

				// Save settings
				if (gameMode.getModeContext().getReferenceContext()
						.containsConstructedCDOMObject(RuleCheck.class, aKey))
				{
					SettingsHandler.setRuleCheck(aKey, aBool);
				}
			}
		}

		for (int i = 0; i < hrRadio.length; i++)
		{
			if (hrRadio[i] != null)
			{
				String aKey = hrRadio[i].getActionCommand();
				boolean aBool = hrRadio[i].isSelected();

				// Save settings
				if (gameMode.getModeContext().getReferenceContext()
						.containsConstructedCDOMObject(RuleCheck.class, aKey))
				{
					SettingsHandler.setRuleCheck(aKey, aBool);
				}
			}
		}
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#applyOptionValuesToControls()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		// Values get set on display
	}

}
