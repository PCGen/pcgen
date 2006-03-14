/*
 * WeaponPane.java
 *
 * Created on February 4, 2004, 10:34 AM
 */

package plugin.charactersheet.gui;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

/**
 * <code>WeaponoToken</code>.
 *
 * @author  ddjone3
 * @version	$Revision$
 */
public class WeaponPane extends javax.swing.JPanel 
{
	private PlayerCharacter pc;
	private Equipment eq;
	private MeleeWeaponPane meleeWeaponPane;
	private NaturalWeaponPane naturalWeaponPane;
	private RangedWeaponPane rangedWeaponPane;
	private WeaponSpecialPane weaponSpecialPane;
	private WeaponTitlePane weaponTitlePane;

	/**
	 * Constructor
	 */
	public WeaponPane() 
	{
		initComponents();
		setLocalColor();
	}

	private void initComponents() 
	{
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

		weaponTitlePane = new WeaponTitlePane();
		meleeWeaponPane = new MeleeWeaponPane();
		naturalWeaponPane = new NaturalWeaponPane();
		rangedWeaponPane = new RangedWeaponPane();
		weaponSpecialPane = new WeaponSpecialPane();
		add(weaponTitlePane);
		add(meleeWeaponPane);
		add(naturalWeaponPane);
		add(rangedWeaponPane);
		add(weaponSpecialPane);
		setVisible(false);
	}

	/**
	 * Set the colour of the pane and sub panes.
	 */
	public void setColor() 
	{
		setLocalColor();
		meleeWeaponPane.setColor();
		naturalWeaponPane.setColor();
		rangedWeaponPane.setColor();
		weaponSpecialPane.setColor();
		weaponTitlePane.setColor();
	}

	/**
	 * Set the colour of the pane.
	 */
	public void setLocalColor() 
	{
		setBackground(CharacterPanel.white);
	}

	/**
	 * Set the Weapon.
	 * 
	 * @param pc Player Character associated with the pane.
	 * @param eq Equipment associated with the pane.
	 */
	public void setWeapon(PlayerCharacter pc, Equipment eq) 
	{
		this.pc = pc;
		this.eq = eq;
	}

	/**
	 * Refresh the pane.  Creates sub panes and sets the weapon for each 
	 * sub pane if they don't exist.
	 */
	public void refresh() 
	{
		setVisible(true);
		if(weaponTitlePane == null) {
			weaponTitlePane = new WeaponTitlePane();
			add(weaponTitlePane);
		}
		weaponTitlePane.setWeapon(pc, eq);
		if(meleeWeaponPane == null) {
			meleeWeaponPane = new MeleeWeaponPane();
			add(meleeWeaponPane);
		}
		meleeWeaponPane.setWeapon(pc, eq);
		if(naturalWeaponPane == null) {
			naturalWeaponPane = new NaturalWeaponPane();
			add(naturalWeaponPane);
		}
		naturalWeaponPane.setWeapon(pc, eq);
		if(rangedWeaponPane == null) {
			rangedWeaponPane = new RangedWeaponPane();
			add(rangedWeaponPane);
		}
		rangedWeaponPane.setWeapon(pc, eq);
		if(weaponSpecialPane == null) {
			weaponSpecialPane = new WeaponSpecialPane();
			add(weaponSpecialPane);
		}
		weaponSpecialPane.setWeapon(pc, eq);
	}

	/**
	 * Clear the pane.
	 */
	public void clear() 
	{
		meleeWeaponPane = null;
		naturalWeaponPane = null;
		rangedWeaponPane = null;
		weaponSpecialPane = null;
		weaponTitlePane = null;
	}
}
