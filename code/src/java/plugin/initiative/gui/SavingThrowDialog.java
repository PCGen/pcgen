/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2003 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  SavingThrowDialog.java
 */
package plugin.initiative.gui;

import gmgen.plugin.Combatant;
import gmgen.plugin.dice.Dice;
import gmgen.plugin.PcgCombatant;
import gmgen.plugin.PlayerCharacterOutput;
import gmgen.plugin.SystemAttribute;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;

import pcgen.core.Globals;
import pcgen.core.PCCheck;
import pcgen.core.PlayerCharacter;
import plugin.initiative.SaveModel;
import plugin.initiative.XMLCombatant;

/**
 * <p>
 * Dialog class that represents a d20 saving throw.
 * </p>
 *
 * @author Devon Jones
 *
 */
public class SavingThrowDialog extends JDialog
{
	/** Statis for save types */
	public static final int NULL_SAVE = 0;
	public static final int FORT_SAVE = 1;
	public static final int REF_SAVE = 2;
	public static final int WILL_SAVE = 3;
	/** Statics for pass/fail/cancel */
	public static final int CANCEL_OPTION = 0;
	public static final int PASS_OPTION = 1;
	public static final int FAIL_OPTION = 2;

	private ButtonGroup saveTypeGroup;
	private final Combatant cbt;
	private JButton cancelButton;
	private JButton failButton;
	private JButton passButton;
	private JButton rollButton;
	private JLabel characterName;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel6;
	private JLabel jLabel7;
	private JLabel jLabel8;
	private JLabel jLabel9;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JRadioButton fortitudeSelection;
	private JRadioButton reflexSelection;
	private JRadioButton willSelection;
	private JSeparator jSeparator1;
	private JSlider saveDCSlider;
	private JSlider saveMagicSlider;
	private JSlider saveTempSlider;
	private JTextField saveAbility;
	private JTextField saveBase;
	private JFormattedTextField saveDC;
	private JFormattedTextField saveMagic;
	private JTextField saveMisc;
	private JFormattedTextField saveTemp;
	private JTextField saveTotal;
	private int lastRoll = 0;
	private int retValue = CANCEL_OPTION;
	private SaveModel m_saveModel;

	/**
	 *
	 * <p>
	 * Creates new dialog witth specified parent, modal property, and combatant.
	 * </p>
	 * @param parent
	 * @param modal
	 * @param cbt
	 */
	public SavingThrowDialog(Frame parent, boolean modal, Combatant cbt)
	{
		this(parent, modal, cbt, 0, NULL_SAVE);
	}

	/**
	 *
	 * <p>
	 * Creates new dialog witth specified parent, modal property, combatant,
	 * dc, and save type.
	 * </p>
	 * @param parent
	 * @param modal
	 * @param cbt
	 * @param dc
	 * @param saveType
	 */
	public SavingThrowDialog(Frame parent, boolean modal,
		Combatant cbt, int dc, int saveType)
	{
		super(parent, modal);
		initComponents();
		this.cbt = cbt;
		setLocation(parent.getX() + 100, parent.getY() + 100);
		setCharacterName(cbt);
		setSaveType(dc, saveType);
		setDefaults(saveType);
	}

	/**
	 * <p>
	 * Creates new saving throw dialog with specified parent, modal property,
	 * combatant, and save model.
	 * </p>
	 *
	 * @param parent
	 * @param modal
	 * @param cbt
	 * @param model
	 */
	public SavingThrowDialog(Frame parent, boolean modal,
		Combatant cbt, SaveModel model)
	{
		super(parent, modal);
		initComponents();
		this.cbt = cbt;
		setLocation(parent.getX() + 100, parent.getY() + 100);
		setCharacterName(cbt);
		int saveType;
		if (SaveModel.SAVE_TYPE_FORTITUDE.equals(model.getSaveType()))
		{
			saveType = FORT_SAVE;
		}
		else if (SaveModel.SAVE_TYPE_REFLEX.equals(model.getSaveType()))
		{
			saveType = REF_SAVE;
		}
		else if (SaveModel.SAVE_TYPE_WILL.equals(model.getSaveType()))
		{
			saveType = WILL_SAVE;
		}
		else
		{
			saveType = NULL_SAVE;
		}
		setSaveModel(model);
		setSaveType(model.getDc(), saveType);
		setDefaults(saveType);
	}

	/**
	 * <p>Sets the save model</p>
	 *
	 * @param model
	 */
	private void setSaveModel(SaveModel model)
	{
		m_saveModel = model;
	}

	/**
	 *
	 * <p>Gets the dc</p>
	 * @return DC
	 */
	public int getDC()
	{
		return getFieldValue(saveDC);
	}

	/**
	 *
	 * <p>Gets the return value for the dialog</p>
	 * @return One of the option constants.
	 */
	public int getReturnValue()
	{
		return retValue;
	}

	/**
	 * <p>Gets the last roll value</p>
	 *
	 * @return The most recent roll value.
	 */
	public int getRoll()
	{
		return lastRoll;
	}

	/**
	 * <p>
	 * Gets the abbreviation for the specified integer save constant.
	 * </p>
	 * @param save
	 * @return A string representing the save constant.
	 */
	public String getSaveAbbrev(int save)
	{
		if (save == FORT_SAVE)
		{
			return "Fort";
		}
		else if (save == REF_SAVE)
		{
			return "Ref";
		}
		else if (save == WILL_SAVE)
		{
			return "Will";
		}

		return "";
	}

	/**
	 * <p>Gets the save type.</p>
	 * @return One of the save type constants.
	 */
	public int getSaveType()
	{
		if (fortitudeSelection.isSelected())
		{
			return FORT_SAVE;
		}
		else if (reflexSelection.isSelected())
		{
			return REF_SAVE;
		}
		else if (willSelection.isSelected())
		{
			return WILL_SAVE;
		}

		return NULL_SAVE;
	}

	/**
	 *
	 * <p>Gets the total.</p>
	 * @return The modifier total
	 */
	public int getTotal()
	{
		return getFieldValue(saveTotal);
	}

	/**
	 *
	 * <p>Sets the character name based on the combatant.</p>
	 * @param cbt
	 */
	private void setCharacterName(Combatant cbt)
	{
		setCharacterName(cbt.getName());
	}

	/**
	 *
	 * <p>Sets the character name based on the string value.</p>
	 * @param name
	 */
	private void setCharacterName(String name)
	{
		characterName.setText(name);
	}

	/**
	 * <p>
	 * Sets all field defaults based on the combatant, dc, save type, etc.
	 * </p>
	 * @param saveType
	 */
	private void setDefaults(int saveType)
	{
		int base = 0;
		int ability = 0;
		int magic = 0;
		int misc = 0;

		if (cbt instanceof PcgCombatant)
		{
			PcgCombatant pcgcbt = (PcgCombatant) cbt;
			PlayerCharacter pc = pcgcbt.getPC();
			new PlayerCharacterOutput(pc);
			List<PCCheck> checkList = Globals.getContext().getReferenceContext()
					.getOrderSortedCDOMObjects(PCCheck.class);

			if (saveType == FORT_SAVE)
			{
				PCCheck fort = checkList.get(0);
				base = pc.calculateSaveBonus(fort, "BASE");
				ability = pc.calculateSaveBonus(fort, "STATMOD");
				magic = pc.calculateSaveBonus(fort, "MAGIC");
				misc = pc.calculateSaveBonus(fort, "MISC.NOMAGIC.NOSTAT");
			}
			else if (saveType == REF_SAVE)
			{
				PCCheck ref = checkList.get(1);
				base = pc.calculateSaveBonus(ref, "BASE");
				ability = pc.calculateSaveBonus(ref, "STATMOD");
				magic = pc.calculateSaveBonus(ref, "MAGIC");
				misc = pc.calculateSaveBonus(ref, "MISC.NOMAGIC.NOSTAT");
			}
			else if (saveType == WILL_SAVE)
			{
				PCCheck will = checkList.get(2);
				base = pc.calculateSaveBonus(will, "BASE");
				ability = pc.calculateSaveBonus(will, "STATMOD");
				magic = pc.calculateSaveBonus(will, "MAGIC");
				misc = pc.calculateSaveBonus(will, "MISC.NOMAGIC.NOSTAT");
			}
		}
		else if (cbt instanceof XMLCombatant)
		{
			XMLCombatant xmlcbt = (XMLCombatant) cbt;

			if (saveType == FORT_SAVE)
			{
				int mod =
						new SystemAttribute("Constitution", xmlcbt
							.getAttribute("Constitution")).getModifier();
				ability = mod;
				base = xmlcbt.getSave("Fortitude") - mod;
			}
			else if (saveType == REF_SAVE)
			{
				int mod =
						new SystemAttribute("Dexterity", xmlcbt
							.getAttribute("Dexterity")).getModifier();
				ability = mod;
				base = xmlcbt.getSave("Reflex") - mod;
			}
			else if (saveType == WILL_SAVE)
			{
				int mod =
						new SystemAttribute("Wisdom", xmlcbt
							.getAttribute("Wisdom")).getModifier();
				ability = mod;
				base = xmlcbt.getSave("Will") - mod;
			}

			magic = parseInt(saveMagic.getText());
			misc = parseInt(saveMisc.getText());
		}

		setDefaults(base, ability, magic, misc, parseInt(saveTemp.getText()));
	}

	/**
	 * <p>
	 * Sets the defaults of the fields as specified.
	 * </p>
	 * @param base
	 * @param ability
	 * @param magic
	 * @param misc
	 * @param temp
	 */
	private void setDefaults(int base, int ability, int magic, int misc,
		int temp)
	{
		saveBase.setText(Integer.toString(base));
		saveAbility.setText(Integer.toString(ability));
		saveMagic.setValue(magic);
		saveMisc.setText(Integer.toString(misc));
		saveTemp.setValue(temp);
		calculate();
	}

	/**
	 *
	 * <p>Gets the value of the specified field, as an integer.</p>
	 * @param field
	 * @return value
	 */
	private int getFieldValue(JTextField field)
	{
		try
		{
			return Integer.parseInt(field.getText());
		}
		catch (NumberFormatException e)
		{
			field.setText("0");
		}

		return 0;
	}

	/**
	 * <p>Sets the save type as the specified value, along with the DC</p>
	 * @param dc
	 * @param saveType
	 */
	private void setSaveType(int dc, int saveType)
	{
		saveDC.setValue(dc);

		if (saveType == FORT_SAVE)
		{
			fortitudeSelection.setSelected(true);
		}
		else if (saveType == REF_SAVE)
		{
			reflexSelection.setSelected(true);
		}
		else if (saveType == WILL_SAVE)
		{
			willSelection.setSelected(true);
		}
	}

	/**
	 * <p>If the {@code cbt} is an <code>XMLCombatant</code>, sets
	 * the combatants save values based on the totals for the dialog box.</p>
	 * @param total
	 */
	private void setXMLCache(int total)
	{
		if (cbt instanceof XMLCombatant)
		{
			XMLCombatant xmlcbt = (XMLCombatant) cbt;

			if (getSaveType() == FORT_SAVE)
			{
				xmlcbt.setSave("Fortitude", total);
			}
			else if (getSaveType() == REF_SAVE)
			{
				xmlcbt.setSave("Reflex", total);
			}
			else if (getSaveType() == WILL_SAVE)
			{
				xmlcbt.setSave("Will", total);
			}
		}
	}

	/**
	 *
	 * <p>Calculates the save total based on the current values
	 * of all fields.</p>
	 *
	 */
	private void calculate()
	{
		int total = 0;
		total += getFieldValue(saveBase);
		total += getFieldValue(saveAbility);
		total += getFieldValue(saveMagic);
		total += getFieldValue(saveMisc);
		total += getFieldValue(saveTemp);
		saveTotal.setText(Integer.toString(total));
		setXMLCache(total);
	}

	private void roll()
	{
		int total = getFieldValue(saveTotal);
		int dc = getFieldValue(saveDC);
		int roll = new Dice(1, 20).roll();

		if ((total + roll) >= dc)
		{
			retValue = PASS_OPTION;
			JOptionPane.showMessageDialog(this, getSaveAbbrev(getSaveType())
				+ " DC " + dc + " Passed.  Save: " + total + " + Roll: " + roll
				+ " = " + (total + roll), "Save Passed",
				JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			retValue = FAIL_OPTION;
			JOptionPane.showMessageDialog(this, getSaveAbbrev(getSaveType())
				+ " DC " + dc + " Failed.  Save: " + total + " + Roll: " + roll
				+ " = " + (total + roll), "Save Failed",
				JOptionPane.INFORMATION_MESSAGE);
		}
		updateModel();

		this.lastRoll = roll;
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>Builds a formatted text field with specified min and max</p>
	 * @param min
	 * @param max
	 * @return JFormattedTextField
	 */
	private JFormattedTextField buildIntegerField(int min, int max)
	{
		final JFormattedTextField returnValue =
				Utils.buildIntegerField(min, max);
		returnValue.addPropertyChangeListener(new PropertyChangeListener()
		{

            @Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if ("value".equals(evt.getPropertyName()))
				{
					calculate();
				}
			}
		});
		return returnValue;
	}

	/**
	 *
	 * <p>Parses a string representing an integer value.</p>
	 * @param number
	 * @return int
	 */
	private int parseInt(String number)
	{
		try
		{
			return Integer.parseInt(number);
		}
		catch (NumberFormatException e)
		{
			// TODO:  Exception Needs to be handled
		}

		return 0;
	}

	/**
	 *
	 * <p>Updates DC and save type values back to the dialog's model.</p>
	 *
	 */
	private void updateModel()
	{
		if (m_saveModel != null)
		{
			m_saveModel.setDc(getFieldValue(saveDC));
			if (fortitudeSelection.isSelected())
			{
				m_saveModel.setSaveType(SaveModel.SAVE_TYPE_FORTITUDE);
			}
			else if (reflexSelection.isSelected())
			{
				m_saveModel.setSaveType(SaveModel.SAVE_TYPE_REFLEX);
			}
			else if (willSelection.isSelected())
			{
				m_saveModel.setSaveType(SaveModel.SAVE_TYPE_WILL);
			}
			else
			{
				m_saveModel.setSaveType(SaveModel.SAVE_TYPE_NONE);
			}
		}
	}

	/**
	 * <p>Returns the save model.</p>
	 *
	 * @return Returns the saveModel.
	 */
	public SaveModel getSaveModel()
	{
		return m_saveModel;
	}

	/**
	 *
	 * <p>Initializes all form components</p>
	 *
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		saveTypeGroup = new ButtonGroup();

		fortitudeSelection = new JRadioButton();
		reflexSelection = new JRadioButton();
		willSelection = new JRadioButton();

		rollButton = new JButton();
		passButton = new JButton();
		failButton = new JButton();
		cancelButton = new JButton();

		jPanel1 = new JPanel();
		jPanel2 = new JPanel();
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jLabel4 = new JLabel();
		jLabel5 = new JLabel();
		jLabel6 = new JLabel();
		jPanel3 = new JPanel();

		jLabel7 = new JLabel();
		jLabel8 = new JLabel();
		characterName = new JLabel();
		jLabel9 = new JLabel();

		jSeparator1 = new JSeparator();

		saveTempSlider = Utils.buildSlider(-5, 20);
		saveMagicSlider = Utils.buildSlider(-5, 20);
		saveDCSlider = Utils.buildSlider(0, 50);

		saveBase = buildIntegerField(-50, 50);
		saveAbility = buildIntegerField(-50, 50);
		saveMisc = buildIntegerField(-50, 50);
		saveTotal = buildIntegerField(-50, 50);

		saveMagic = Utils.buildIntegerFieldWithSlider(saveMagicSlider);
		saveTemp = Utils.buildIntegerFieldWithSlider(saveTempSlider);
		saveDC = Utils.buildIntegerFieldWithSlider(saveDCSlider);

		setTitle("Saving Throw");
		addWindowListener(new WindowAdapter()
		{
            @Override
			public void windowClosing(WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));

		rollButton.setText("Roll");
		rollButton.addActionListener(this::rollButtonActionPerformed);

		jPanel1.add(rollButton);

		passButton.setText("Pass");
		passButton.addActionListener(this::passButtonActionPerformed);

		jPanel1.add(passButton);

		failButton.setText("Fail");
		failButton.addActionListener(this::failButtonActionPerformed);

		jPanel1.add(failButton);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(this::cancelButtonActionPerformed);

		jPanel1.add(cancelButton);

		getContentPane().add(jPanel1, BorderLayout.SOUTH);

		jPanel2.setLayout(new GridBagLayout());

		jLabel1.setText("Base Save");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);
		jPanel2.add(jLabel1, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel2.add(saveBase, gridBagConstraints);

		jLabel2.setText("Ability Modifier");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);
		jPanel2.add(jLabel2, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel2.add(saveAbility, gridBagConstraints);

		jLabel3.setText("Magic Modifier");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);
		jPanel2.add(jLabel3, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel2.add(saveMagic, gridBagConstraints);

		jLabel4.setText("Misc Modifier");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);
		jPanel2.add(jLabel4, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel2.add(saveMisc, gridBagConstraints);

		jLabel5.setText("Temp Modifier");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);
		jPanel2.add(jLabel5, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel2.add(saveTemp, gridBagConstraints);

		jLabel6.setText("Total");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 10, 5);
		jPanel2.add(jLabel6, gridBagConstraints);

		saveTotal.setBackground(new Color(204, 204, 204));
		saveTotal.setEditable(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		jPanel2.add(saveTotal, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 2);
		jPanel2.add(saveTempSlider, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 2);
		jPanel2.add(saveMagicSlider, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		jPanel2.add(saveDC, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(10, 5, 0, 2);
		jPanel2.add(saveDCSlider, gridBagConstraints);

		jLabel7.setText("Difficulty Class");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(10, 5, 0, 5);
		jPanel2.add(jLabel7, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(0, 10, 0, 10);
		jPanel2.add(jLabel8, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(0, 2, 0, 0);
		jPanel2.add(jSeparator1, gridBagConstraints);

		jLabel9.setText("Save Type");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 0);
		jPanel2.add(jLabel9, gridBagConstraints);

		jPanel3.setLayout(new FlowLayout(FlowLayout.LEFT));

		fortitudeSelection.setText("Fortitude");
		saveTypeGroup.add(fortitudeSelection);
		fortitudeSelection
			.addActionListener(this::saveSelectedActionPerformed);

		jPanel3.add(fortitudeSelection);

		reflexSelection.setText("Reflex");
		saveTypeGroup.add(reflexSelection);
		reflexSelection.addActionListener(this::saveSelectedActionPerformed);

		jPanel3.add(reflexSelection);

		willSelection.setText("Will");
		saveTypeGroup.add(willSelection);
		willSelection.addActionListener(this::saveSelectedActionPerformed);

		jPanel3.add(willSelection);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 5, 0, 2);
		jPanel2.add(jPanel3, gridBagConstraints);

		characterName.setFont(new Font("Dialog", 1, 14));
		characterName.setText(" ");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(0, 5, 10, 0);
		jPanel2.add(characterName, gridBagConstraints);

		saveAbility.addKeyListener(new EnterKeyAdapter());
		saveBase.addKeyListener(new EnterKeyAdapter());
		saveDC.addKeyListener(new EnterKeyAdapter());
		saveMagic.addKeyListener(new EnterKeyAdapter());
		saveMisc.addKeyListener(new EnterKeyAdapter());
		saveTemp.addKeyListener(new EnterKeyAdapter());
		saveTotal.addKeyListener(new EnterKeyAdapter());

		getContentPane().add(jPanel2, BorderLayout.WEST);

		pack();
	}

	//The following are all event listeners fo some sort:
	/**
	 *
	 * <p>Cancels the dialog.</p>
	 * @param evt
	 */
	private void cancelButtonActionPerformed(ActionEvent evt)
	{
		retValue = CANCEL_OPTION;
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>Closes the dialog</p>
	 * @param evt
	 */
	private void closeDialog(WindowEvent evt)
	{
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>"Fails" the save and cancels the dialog.</p>
	 * @param evt
	 */
	private void failButtonActionPerformed(ActionEvent evt)
	{
		updateModel();
		retValue = FAIL_OPTION;
		setVisible(false);
		dispose();
	}

	/**
	 * <p>"Passes" the save and closes the dialog.</p>
	 * @param evt
	 */
	private void passButtonActionPerformed(ActionEvent evt)
	{
		updateModel();
		retValue = PASS_OPTION;
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>Rolls the save, passes or fails, and closes the dialog.</p>
	 * @param evt
	 */
	private void rollButtonActionPerformed(ActionEvent evt)
	{
		roll();
	}

	/**
	 * <p>
	 * Sets the defaults based on the selected save type
	 * </p>
	 * @param evt
	 */
	private void saveSelectedActionPerformed(ActionEvent evt)
	{
		setDefaults(getSaveType());
	}

	private class EnterKeyAdapter extends KeyAdapter
	{
        @Override
		public void keyReleased(KeyEvent evt)
		{
			if (evt.getKeyCode() == KeyEvent.VK_ENTER)
			{
				roll();
			}
		}
	}
}
