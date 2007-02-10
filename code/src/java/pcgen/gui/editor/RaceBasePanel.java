/*
 * RaceBasePanel
 * Copyright 2003 (C) James Dempsey <jdempsey@users.sourceforge.net >
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
 * Created on January 8, 2003, 8:15 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.core.*;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>RaceBasePanel</code>
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 */
public class RaceBasePanel extends BasePanel
{
	private static final String[] sizeTitles = new String[]
		{
			"Fine", "Diminutive", "Tiny", "Small", "Medium", "Large", "Huge", "Gargantuan", "Colossal"
		};
	private static final String[] sizeAbbrev = new String[]{ "F", "D", "T", "S", "M", "L", "H", "G", "C" };
	private static final String[] hitDiceSizeValues = new String[]
		{
			"1", "2", "4", "6", "8", "10", "12", "14", "16", "18", "20"
		};
	private static final String[] crValues =
	{
		"1/10", "1/8", "1/6", "1/4", "1/3", "1/2", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
		"13", "14", "15", "16", "17", "18", "19", "20"
	};
	private JComboBoxEx cmbBonusFeats;
	private JComboBoxEx cmbBonusSkillPoints;

	//private JTextField txtCR;
	private JComboBoxEx cmbCR;
	private JComboBoxEx cmbHands;
	private JComboBoxEx cmbHitDiceNumber;
	private JComboBoxEx cmbHitDiceSize;
	private JComboBoxEx cmbLegs;
	private JComboBoxEx cmbMonsterClass;
	private JComboBoxEx cmbMonsterLevel;
	private JComboBoxEx cmbReach;
	private JComboBoxEx cmbSize;
	private JComboBoxEx cmbSkillMult;
	private JPanel pnlTemplateMisc;
	private JTextField txtDisplayName;
	private JTextField txtHitDiceAdvancement;
	private JTextField txtLevelAdj;

//	private AvailableSelectedPanel pnlTemplateTypes;
	private TypePanel pnlTemplateTypes;

	/** Creates new form TemplateBasePanel */
	public RaceBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	public void setBonusFeats(final int bonusFeats)
	{
		if ((bonusFeats >= 0) && (bonusFeats < cmbBonusFeats.getItemCount()))
		{
			cmbBonusFeats.setSelectedIndex(bonusFeats);
		}
	}

	public int getBonusFeats()
	{
		return cmbBonusFeats.getSelectedIndex();
	}

	public void setBonusSkillPoints(final int bonusSkillPoints)
	{
		if ((bonusSkillPoints >= 0) && (bonusSkillPoints < cmbBonusSkillPoints.getItemCount()))
		{
			cmbBonusSkillPoints.setSelectedIndex(bonusSkillPoints);
		}
	}

	public int getBonusSkillPoints()
	{
		return cmbBonusSkillPoints.getSelectedIndex();
	}

	public void setCR(final int argCR)
	{
		//txtCR.setText(String.valueOf(argCR));
		String txtCR;

		if (argCR < 0)
		{
			txtCR = "1/" + Integer.toString(-argCR);
		}
		else
		{
			txtCR = Integer.toString(argCR);
		}

		cmbCR.setSelectedItem(txtCR);
	}

	public int getCR()
	{
		String txtCR = null;

		try
		{
			txtCR = cmbCR.getSelectedItem().toString();

			if (txtCR == null)
			{
				return 0;
			}

			if (txtCR.startsWith("1/"))
			{
				return Integer.parseInt(txtCR.substring(2));
			}

			return Integer.parseInt(txtCR);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Couldn't figure out what CR " + txtCR + " means.");
		}

		return 0;
	}

	public void setDisplayName(final String dislayName)
	{
		txtDisplayName.setText(dislayName);
	}

	public String getDisplayName()
	{
		return txtDisplayName.getText();
	}

	public void setHands(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbHands.getItemCount()))
		{
			cmbHands.setSelectedIndex(aNumber);
		}
	}

	public int getHands()
	{
		return cmbHands.getSelectedIndex();
	}

	public void setHitDiceAdvancement(final Race thisRace)
	{
		if ((thisRace == null) || (thisRace.getNumberOfHitDiceAdvancements() == 0))
		{
			txtHitDiceAdvancement.setText("");
		}
		else
		{
			StringBuffer adv = new StringBuffer();

			for (int index = 0; index < thisRace.getNumberOfHitDiceAdvancements(); index++)
			{
				if (index > 0)
				{
					adv.append(',');
				}

				if ((thisRace.getHitDiceAdvancement(index) == -1) && thisRace.isAdvancementUnlimited())
				{
					adv.append('*');
				}
				else
				{
					adv.append(String.valueOf(thisRace.getHitDiceAdvancement(index)));
				}
			}

			txtHitDiceAdvancement.setText(adv.toString());
		}
	}

	public int[] getHitDiceAdvancement()
	{
		if ((txtHitDiceAdvancement.getText() == null) || (txtHitDiceAdvancement.getText().trim().length() == 0))
		{
			return new int[]{  };
		}
		final StringTokenizer advancement = new StringTokenizer(txtHitDiceAdvancement.getText(), ",");
		String temp;
		int[] hitDiceAdvancement = new int[advancement.countTokens()];
		for (int x = 0; x < hitDiceAdvancement.length; ++x)
		{
			temp = advancement.nextToken();
			if ((temp.length() > 0) && (temp.charAt(0) == '*'))
			{
				hitDiceAdvancement[x] = -1;
			}
			else
			{
				hitDiceAdvancement[x] = Integer.parseInt(temp);
			}
		}

		return hitDiceAdvancement;
	}

	public boolean getHitDiceAdvancementUnlimited()
	{
		if ((txtHitDiceAdvancement.getText() != null) && (txtHitDiceAdvancement.getText().trim().length() > 0))
		{
			final StringTokenizer advancement = new StringTokenizer(txtHitDiceAdvancement.getText(), ",");
			String temp;

			int[] hitDiceAdvancement = new int[advancement.countTokens()];

			for (int x = 0; x < hitDiceAdvancement.length; ++x)
			{
				temp = advancement.nextToken();

				if ((temp.length() > 0) && (temp.charAt(0) == '*'))
				{
					return true;
				}
			}
		}

		return false;
	}

	public void setHitDiceNumber(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbMonsterLevel.getItemCount()))
		{
			cmbHitDiceNumber.setSelectedItem(String.valueOf(aNumber));
		}
	}

	public int getHitDiceNumber()
	{
		return Integer.parseInt((String) cmbHitDiceNumber.getSelectedItem());
	}

	public void setHitDiceSize(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbMonsterLevel.getItemCount()))
		{
			cmbHitDiceSize.setSelectedItem(String.valueOf(aNumber));
		}
	}

	public int getHitDiceSize()
	{
		return Integer.parseInt((String) cmbHitDiceSize.getSelectedItem());
	}

	public void setLegs(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbLegs.getItemCount()))
		{
			cmbLegs.setSelectedIndex(aNumber);
		}
	}

	public int getLegs()
	{
		return cmbLegs.getSelectedIndex();
	}

	public void setLevelAdjustment(final String levelAdj)
	{
		txtLevelAdj.setText(levelAdj);
	}

	public String getLevelAdjustment()
	{
		return txtLevelAdj.getText();
	}

	public void setMonsterClass(final String aString)
	{
		if ((aString == null) || (aString.length() == 0))
		{
			cmbMonsterClass.setSelectedItem("(None)");
		}
		else
		{
			cmbMonsterClass.setSelectedItem(aString);
		}
	}

	public String getMonsterClass()
	{
		return (String) cmbMonsterClass.getSelectedItem();
	}

	public void setMonsterClassList(final List<String> classList)
	{
		cmbMonsterClass.setModel(new DefaultComboBoxModel(classList.toArray()));
	}

	public void setMonsterLevel(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbMonsterLevel.getItemCount()))
		{
			cmbMonsterLevel.setSelectedIndex(aNumber);
		}
	}

	public int getMonsterLevel()
	{
		return cmbMonsterLevel.getSelectedIndex();
	}

	public void setRaceSize(final String aString)
	{
		cmbSize.setSelectedIndex(0);

		for (int index = 0; index < sizeAbbrev.length; index++)
		{
			if (sizeAbbrev[index].equals(aString))
			{
				cmbSize.setSelectedIndex(index);

				break;
			}
		}
	}

	public String getRaceSize()
	{
		int index = cmbSize.getSelectedIndex();

		if ((index >= 0) && (index < sizeAbbrev.length))
		{
			return sizeAbbrev[index];
		}
		return "";
	}

	public void setReach(final int aNumber)
	{
		cmbReach.setSelectedItem(String.valueOf(aNumber));
	}

	public int getReach()
	{
		return Integer.parseInt((String) cmbReach.getSelectedItem());
	}

	public void setSkillMultiplier(final int aNumber)
	{
		if ((aNumber >= 0) && (aNumber < cmbSkillMult.getItemCount()))
		{
			cmbSkillMult.setSelectedIndex(aNumber);
		}
	}

	public int getSkillMultiplier()
	{
		return cmbSkillMult.getSelectedIndex();
	}

	public void setTypesAvailableList(final List<String> aList, final boolean sort)
	{
		pnlTemplateTypes.setAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final List<String> aList, final boolean sort)
	{
		pnlTemplateTypes.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlTemplateTypes.getSelectedList();
	}

	public void updateData(PObject thisPObject)
	{
		Race thisRace = (Race) thisPObject;

		StringBuffer sb = new StringBuffer();
        	sb.append("FEAT|POOL|").append(getBonusFeats());
        	final BonusObj bon = Bonus.newBonus(sb.toString());
        	thisRace.setBonusInitialFeats(bon);
        
		thisRace.setBonusSkillsPerLevel(getBonusSkillPoints());
		thisRace.setCR(getCR());
		thisRace.setDisplayName(getDisplayName());
		thisRace.setHands(getHands());
		thisRace.setHitDiceAdvancement(getHitDiceAdvancement());
		thisRace.setAdvancementUnlimited(getHitDiceAdvancementUnlimited());
		thisRace.setLegs(getLegs());
		thisRace.setLevelAdjustment(getLevelAdjustment());
		thisRace.setMonsterClass(getMonsterClass());
		thisRace.setMonsterClassLevels(getMonsterLevel());
		thisRace.setSize(getRaceSize());
		thisRace.setReach(getReach());
		thisRace.setInitialSkillMultiplier(getSkillMultiplier());
		thisRace.setHitDice(getHitDiceNumber());
		thisRace.setHitDiceSize(getHitDiceSize());

		//
		// Save types
		//
		Object[] sel = getTypesSelectedList();
		thisPObject.setTypeInfo(".CLEAR");

		for (int i = 0; i < sel.length; ++i)
		{
			thisPObject.setTypeInfo(sel[i].toString());
		}
	}

	public void updateView(PObject thisPObject)
	{
		Iterator e;
		String aString;
		Race thisRace = (Race) thisPObject;

		//
		// Populate the types
		//
		List<String> availableList = new ArrayList<String>();
		List<String> selectedList = new ArrayList<String>();

		for ( final Race race : Globals.getAllRaces() )
		{
			for (String type : race.getTypeList(false))
			{
				if (!type.equals(Constants.s_CUSTOM))
				{
					if (!availableList.contains(type))
					{
						availableList.add(type);
					}
				}
			}
		}

		// remove this race's type from the available list and place into selected list
		for (String type : thisRace.getTypeList(false))
		{
			if (!type.equals(Constants.s_CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
			}
		}

		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		///
		/// Populate the monster classes
		///
		availableList.clear();
		availableList.add("(None)");

		for (e = Globals.getClassList().iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			if (aClass.isMonster())
			{
				availableList.add(aClass.getKeyName());
			}
		}

		setMonsterClassList(availableList);

		setBonusFeats(0);
		setBonusSkillPoints(thisRace.getBonusSkillsPerLevel());
		setCR(thisRace.getCR());
		setDisplayName(thisRace.getDisplayName());
		setHands(thisRace.getHands());
		setHitDiceAdvancement(thisRace);
		setLegs(thisRace.getLegs());
		setLevelAdjustment(thisRace.getLevelAdjustmentFormula());
		setMonsterClass(thisRace.getMonsterClass(null, false));
		setMonsterLevel(thisRace.getMonsterClassLevels(null, false));
		setRaceSize(thisRace.getSize());
		setReach(thisRace.getReach());
		setSkillMultiplier(thisRace.getInitialSkillMultiplier());
		setHitDiceNumber(thisRace.hitDice(null, false));
		setHitDiceSize(thisRace.getHitDiceSize(null, false));
	}

	private static GridBagConstraints buildConstraints(int gridx, int gridy)
	{
		GridBagConstraints gridBagConstraints;
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;

		return gridBagConstraints;
	}

	private void initComponentContents()
	{
		cmbSize.setModel(new DefaultComboBoxModel(sizeTitles));

		String[] values = new String[20];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i);
		}

		cmbBonusSkillPoints.setModel(new DefaultComboBoxModel(values));
		cmbBonusFeats.setModel(new DefaultComboBoxModel(values));
		cmbLegs.setModel(new DefaultComboBoxModel(values));
		cmbHands.setModel(new DefaultComboBoxModel(values));
		cmbSkillMult.setModel(new DefaultComboBoxModel(values));

		values = new String[10];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i * 5);
		}

		cmbReach.setModel(new DefaultComboBoxModel(values));

		values = new String[40];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i);
		}

		cmbMonsterLevel.setModel(new DefaultComboBoxModel(values));
		cmbHitDiceNumber.setModel(new DefaultComboBoxModel(values));

		cmbHitDiceSize.setModel(new DefaultComboBoxModel(hitDiceSizeValues));

		cmbCR.setModel(new DefaultComboBoxModel(crValues));
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;
		JLabel tempLabel;

		cmbBonusSkillPoints = new JComboBoxEx();
		cmbBonusFeats = new JComboBoxEx();

		//txtCR = new JTextField();
		cmbCR = new JComboBoxEx();
		txtDisplayName = new JTextField();
		cmbHands = new JComboBoxEx();
		txtHitDiceAdvancement = new JTextField();
		cmbHitDiceNumber = new JComboBoxEx();
		cmbHitDiceSize = new JComboBoxEx();
		cmbLegs = new JComboBoxEx();
		txtLevelAdj = new JTextField();
		cmbMonsterClass = new JComboBoxEx();
		cmbMonsterLevel = new JComboBoxEx();
		cmbReach = new JComboBoxEx();
		cmbSize = new JComboBoxEx();
		cmbSkillMult = new JComboBoxEx();

		pnlTemplateMisc = new JPanel();

		//pnlTemplateTypes = new AvailableSelectedPanel();
		pnlTemplateTypes = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));

		setLayout(new GridBagLayout());

		pnlTemplateMisc.setLayout(new GridBagLayout());

		tempLabel = new JLabel("Display Name");
		gridBagConstraints = buildConstraints(0, 0);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 0);
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtDisplayName, gridBagConstraints);

		tempLabel = new JLabel("Hands");
		gridBagConstraints = buildConstraints(0, 1);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 1);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbHands, gridBagConstraints);

		tempLabel = new JLabel("Legs");
		gridBagConstraints = buildConstraints(2, 1);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 1);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbLegs, gridBagConstraints);

		tempLabel = new JLabel("Reach");
		gridBagConstraints = buildConstraints(0, 2);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 2);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbReach, gridBagConstraints);

		tempLabel = new JLabel("Skill Multiplier");
		gridBagConstraints = buildConstraints(2, 2);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 2);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbSkillMult, gridBagConstraints);

		tempLabel = new JLabel("Bonus Skill Pts / Level");
		gridBagConstraints = buildConstraints(0, 3);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 3);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbBonusSkillPoints, gridBagConstraints);

		tempLabel = new JLabel("Bonus Starting Feats");
		gridBagConstraints = buildConstraints(2, 3);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 3);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbBonusFeats, gridBagConstraints);

		tempLabel = new JLabel("CR");
		gridBagConstraints = buildConstraints(0, 4);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		cmbCR.setEditable(true);
		gridBagConstraints = buildConstraints(1, 4);
		gridBagConstraints.weightx = 0.4;

		//pnlTemplateMisc.add(txtCR, gridBagConstraints);
		pnlTemplateMisc.add(cmbCR, gridBagConstraints);

		tempLabel = new JLabel("Level Adjustment");
		gridBagConstraints = buildConstraints(2, 4);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 4);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtLevelAdj, gridBagConstraints);

		tempLabel = new JLabel("Size");
		gridBagConstraints = buildConstraints(0, 5);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbSize, gridBagConstraints);

		tempLabel = new JLabel("Hit Dice Advancement");
		gridBagConstraints = buildConstraints(2, 5);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtHitDiceAdvancement, gridBagConstraints);

		tempLabel = new JLabel("Monster Class");
		gridBagConstraints = buildConstraints(0, 6);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 6);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbMonsterClass, gridBagConstraints);

		tempLabel = new JLabel("Monster Level");
		gridBagConstraints = buildConstraints(2, 6);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 6);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbMonsterLevel, gridBagConstraints);

		tempLabel = new JLabel("Number of Hit Dice");
		gridBagConstraints = buildConstraints(0, 7);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(1, 7);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbHitDiceNumber, gridBagConstraints);

		tempLabel = new JLabel("Hit Dice Size");
		gridBagConstraints = buildConstraints(2, 7);
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(3, 7);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbHitDiceSize, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlTemplateMisc, gridBagConstraints);

		//pnlTemplateTypes.setHeader(PropertyFactory.getString("in_type"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlTemplateTypes, gridBagConstraints);
	}
}
