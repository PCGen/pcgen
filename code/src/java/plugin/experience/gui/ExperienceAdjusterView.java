package plugin.experience.gui;

import javax.swing.*;

/**
 * The View for the Experience Adjuster.  This view is independant and will be
 * created by the <code>ExperienceAdjusterController</code>.  The view is
 * implemented as a <code>JPanel</code> so it can be added to the
 * <code>JTabbedPane</code> of the <code>GMGenSystemView</code>.<br>
 * Created on February 19, 2003<br>
 * Updated on March 6, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class ExperienceAdjusterView extends javax.swing.JPanel
{
	/** Button to add an enemy to the experience calculation. */
	private javax.swing.JButton addEnemyButton;

	/** The <b>Add Experience to Selected Character</b> button. */
	private javax.swing.JButton addExperienceToCharButton;

	/** The <b>Add Experience to Selected Group</b> button. */
	private javax.swing.JButton addExperienceToPartyButton;

	/** The <b>Add Experience to Selected Character</b> button. */
	private javax.swing.JButton adjustCRButton;

	/** Button to delete an enemy from the experience calculation. */
	private javax.swing.JButton removeEnemyButton;

	/** The <b>Total Experience from Combat:</b> label. */
	private javax.swing.JLabel experienceFromCombat;

	/** The multiplier label. */
	private javax.swing.JLabel experienceMultLabel;

	/** The Name for the eperience multiplier label. */
	private javax.swing.JLabel experienceMultNameLabel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;

	//Various components to shape the form properly
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel7;

	/** The GUI component that holds the list of PC combatants. */
	private javax.swing.JList characterList;

	/** The GUI component that holds the list of enemy combatants. */
	private javax.swing.JList enemyList;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;

	/** The <b>Experience Multiplier</b> slider. */
	private javax.swing.JSlider experienceMultSlider;

	/** The user editable field that takes in experience to add. */
	private javax.swing.JTextField experienceToAdd;

	/**
	 * Creates an instance of <code>ExperienceAdjusterView</code>
	 * <code>ExperienceAdjusterController</code>.
	 */
	public ExperienceAdjusterView()
	{
		initComponents();
	}

	/**
	 * Gets the add enemy button
	 * @return the add enemy button
	 */
	public JButton getAddEnemyButton()
	{
		return addEnemyButton;
	}

	/**
	 * Gets the <b>Add Experience to Selected Character</b> button.
	 * @return the <code>addExperienceToCharButton</code>.
	 */
	public JButton getAddExperienceToCharButton()
	{
		return addExperienceToCharButton;
	}

	/**
	 * Gets the <b>Add Experience to Selected Party</b> button.
	 * @return the <code>addExperienceToPartyButton</code>.
	 */
	public JButton getAddExperienceToPartyButton()
	{
		return addExperienceToPartyButton;
	}

	/**
	 * Gets the <b>Adjust CR</b> button.
	 * @return the <code>adjustCRButton</code>.
	 */
	public JButton getAdjustCRButton()
	{
		return adjustCRButton;
	}

	/**
	 * Gets the list of characters from the GUI.
	 * @return the <code>characterList</code>.
	 */
	public JList getCharacterList()
	{
		return characterList;
	}

	/**
	 * Set enemies
	 * @param enemies
	 */
	public void setEnemies(DefaultListModel enemies)
	{
		enemyList.setModel(enemies);
	}

	/**
	 * Get enemy list
	 * @return enemy list
	 */
	public JList getEnemyList()
	{
		return enemyList;
	}

	/**
	 * Gets the field for experience to add.
	 * @return the <code>experienceToAdd<code> field.
	 */
	public JTextField getExperienceField()
	{
		return experienceToAdd;
	}

	/**
	 * Sets the experience from combat value on the GUI.
	 * @param experience the value of experience that has come from the combat.
	 */
	public void setExperienceFromCombat(int experience)
	{
		experienceFromCombat.setText(Integer.toString(experience));
	}

	/**
	 * Sets the experience from combat value on the GUI.
	 * @param experience the value of experience that has come from the combat.
	 */
	public void setExperienceFromCombat(String experience)
	{
		experienceFromCombat.setText(experience);
	}

	/**
	 * Gets the value from the experience from combat label.
	 * @return the value for experience from combat.
	 */
	public int getExperienceFromCombat()
	{
		return Integer.parseInt(experienceFromCombat.getText());
	}

	/** The multiplier label.
	 * @return JLabel*/
	public javax.swing.JLabel getExperienceMultLabel()
	{
		return experienceMultLabel;
	}

	/** The Name for the eperience multiplier label.
	 * @return JLabel*/
	public javax.swing.JLabel getExperienceMultNameLabel()
	{
		return experienceMultNameLabel;
	}

	/** The <b>Experience Multiplier</b> slider.
	 * @return JSlider*/
	public javax.swing.JSlider getExperienceMultSlider()
	{
		return experienceMultSlider;
	}

	/**
	 * Sets the experience to add field if needed.
	 * @param experience the value for the experience to add to the character.
	 */
	public void setExperienceToAdd(int experience)
	{
		experienceToAdd.setText(Integer.toString(experience));
	}

	/**
	 * Sets the experience to add field if needed.
	 * @param experience the value for the experience to add to the character.
	 */
	public void setExperienceToAdd(String experience)
	{
		experienceToAdd.setText(experience);
	}

	/**
	 * Gets the experience to add that the user has input.
	 * @return the experience to add as an <code>int</code>.
	 */
	public int getExperienceToAdd()
	{
		return Integer.parseInt(experienceToAdd.getText());
	}

	/**
	 * sets the party in the main list display
	 * @param party
	 */
	public void setParty(DefaultListModel party)
	{
		characterList.setModel(party);
	}

	/**
	 * Get the remove enemy button
	 * @return the remove enemy button
	 */
	public JButton getRemoveEnemyButton()
	{
		return removeEnemyButton;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		java.awt.GridBagConstraints gridBagConstraints;

		jPanel5 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		characterList = new javax.swing.JList();
		jPanel3 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jPanel1 = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		enemyList = new javax.swing.JList();
		jPanel6 = new javax.swing.JPanel();
		jPanel7 = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		experienceToAdd = new javax.swing.JTextField();
		addExperienceToCharButton = new javax.swing.JButton();
		jPanel8 = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		experienceFromCombat = new javax.swing.JLabel();
		experienceMultNameLabel = new javax.swing.JLabel();
		experienceMultSlider = new javax.swing.JSlider();
		addExperienceToPartyButton = new javax.swing.JButton();
		experienceMultLabel = new javax.swing.JLabel();
		adjustCRButton = new javax.swing.JButton();
		addEnemyButton = new javax.swing.JButton();
		removeEnemyButton = new javax.swing.JButton();
		jScrollPane1 = new JScrollPane(characterList);
		jScrollPane2 = new JScrollPane(enemyList);

		setLayout(new java.awt.BorderLayout());

		jPanel5.setLayout(new java.awt.GridLayout(0, 1));

		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

		jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3,
			javax.swing.BoxLayout.Y_AXIS));

		jLabel1.setText("Characters");
		jPanel3.add(jLabel1);

		jLabel2.setText("Name (Level) Experience");
		jLabel2.setToolTipText("null");
		jPanel3.add(jLabel2);

		jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

		jPanel5.add(jPanel2);

		jPanel1.setLayout(new java.awt.BorderLayout());

		jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4,
			javax.swing.BoxLayout.Y_AXIS));

		jLabel3.setText("Enemies");
		jPanel4.add(jLabel3);

		jLabel4.setText("Name (CR)");
		jPanel4.add(jLabel4);

		jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

		jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

		jPanel5.add(jPanel1);

		add(jPanel5, java.awt.BorderLayout.CENTER);

		jPanel6.setLayout(new java.awt.GridLayout(0, 1));

		jPanel7.setLayout(new java.awt.GridBagLayout());

		jLabel7.setText("Experience to Add:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		jPanel7.add(jLabel7, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		jPanel7.add(experienceToAdd, gridBagConstraints);

		addExperienceToCharButton.setText("Add Experience to Character");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel7.add(addExperienceToCharButton, gridBagConstraints);

		jPanel6.add(jPanel7);

		jPanel8.setLayout(new java.awt.GridBagLayout());

		jLabel5.setText("Experience From Combat");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
		jPanel8.add(jLabel5, gridBagConstraints);

		experienceFromCombat.setText("0");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		jPanel8.add(experienceFromCombat, gridBagConstraints);

		experienceMultNameLabel.setText("Normal");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(experienceMultNameLabel, gridBagConstraints);

		experienceMultSlider.setMaximum(10);
		experienceMultSlider.setMinimum(-5);
		experienceMultSlider.setValue(0);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 4);
		jPanel8.add(experienceMultSlider, gridBagConstraints);

		addExperienceToPartyButton.setText("Add Experience to Party");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 140);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(addExperienceToPartyButton, gridBagConstraints);

		experienceMultLabel.setText("1x");
		experienceMultLabel.setToolTipText("null");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(experienceMultLabel, gridBagConstraints);

		adjustCRButton.setText("Adjust CR/Level");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(adjustCRButton, gridBagConstraints);

		addEnemyButton.setText("Add Enemy");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(addEnemyButton, gridBagConstraints);

		removeEnemyButton.setText("Remove Enemy");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel8.add(removeEnemyButton, gridBagConstraints);

		jPanel6.add(jPanel8);

		add(jPanel6, java.awt.BorderLayout.EAST);
	}
}
