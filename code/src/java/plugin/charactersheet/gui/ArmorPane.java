/*
 * UnarmedPane.java
 *
 * Created on February 3, 2004, 3:23 PM
 */

package plugin.charactersheet.gui;

import gmgen.plugin.PlayerCharacterOutput;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Confirmed no memory Leaks Dec 10, 2004
 * @author  ddjone3
 */
public class ArmorPane extends javax.swing.JPanel {
	private PlayerCharacter pc;
	private ArrayList fields = new ArrayList();

	private JLabel typeName;
	private JPanel typeNamePanel;
	private JPanel typePanel;
	private JPanel acPanel;
	private JPanel maxDexPanel;
	private JPanel checkPanel;
	private JPanel failurePanel;

	private static final Font FONT_TEN = new Font("Dialog", 0, 10);
	private static final String ARMOR_TEXT = " ARMOR ";
	private static final String TYPE_TEXT = " Type ";
	private static final String AC_TEXT = " AC ";
	private static final String MAX_DEX_TEXT = " Max Dex ";
	private static final String CHECK_TEXT = " Check ";
	private static final String FAIL_TEXT = " Spell Failure ";
	private static final String BLANK = "";
	private static final String SPACE = " ";
	private static final String ARMOR = "ARMOR";
	private static final String SHIELD = "SHIELD";
	private static final String ITEM = "ITEM";
	private static final String AC = "AC";
	private static final String NAME = "NAME";
	private static final String TYPE = "TYPE";
	private static final String ACBONUS = "ACBONUS";
	private static final String MAXDEX = "MAXDEX";
	private static final String ACCHECK = "ACCHECK";
	private static final String SPELLFAIL = "SPELLFAIL";
	private static final String SPROPS = "SPROPS";

	/**
	 * Constructor
	 */
	public ArmorPane() {
		initComponents();
		setColor();
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		typeNamePanel = new JPanel();
		typeName = new JLabel();
		typePanel = new JPanel();
		acPanel = new JPanel();
		maxDexPanel = new JPanel();
		checkPanel = new JPanel();
		failurePanel = new JPanel();

		setLayout(new java.awt.GridBagLayout());

		typeNamePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));
		typeName.setFont(FONT_TEN);
		typeName.setText(ARMOR_TEXT);
		typeNamePanel.add(typeName);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(typeNamePanel, gridBagConstraints);

		typePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));
		JLabel typeLabel = new JLabel();
		typeLabel.setFont(FONT_TEN);
		typeLabel.setText(TYPE_TEXT);
		typePanel.add(typeLabel);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(typePanel, gridBagConstraints);

		acPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));
		JLabel acLabel = new JLabel();
		acLabel.setFont(FONT_TEN);
		acLabel.setText(AC_TEXT);
		acPanel.add(acLabel);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(acPanel, gridBagConstraints);

		maxDexPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));
		JLabel maxDexLabel = new JLabel();
		maxDexLabel.setFont(FONT_TEN);
		maxDexLabel.setText(MAX_DEX_TEXT);
		maxDexPanel.add(maxDexLabel);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(maxDexPanel, gridBagConstraints);

		checkPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));
		JLabel checkLabel = new JLabel();
		checkLabel.setFont(FONT_TEN);
		checkLabel.setText(CHECK_TEXT);
		checkPanel.add(checkLabel);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(checkPanel, gridBagConstraints);

		failurePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));
		JLabel failureLabel = new JLabel();
		failureLabel.setFont(FONT_TEN);
		failureLabel.setText(FAIL_TEXT);
		failurePanel.add(failureLabel);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(failurePanel, gridBagConstraints);
	}

	/**
	 * Sets the colors
	 */
	public void setColor() {
		refresh();
		setBackground(CharacterPanel.border);
		typeNamePanel.setBackground(CharacterPanel.header);
		typePanel.setBackground(CharacterPanel.header);
		acPanel.setBackground(CharacterPanel.header);
		maxDexPanel.setBackground(CharacterPanel.header);
		checkPanel.setBackground(CharacterPanel.header);
		failurePanel.setBackground(CharacterPanel.header);
		setBorder(new javax.swing.border.LineBorder(CharacterPanel.border));
		typeNamePanel.setBorder(new javax.swing.border.LineBorder(CharacterPanel.border));
		typePanel.setBorder(new javax.swing.border.LineBorder(CharacterPanel.border));
		acPanel.setBorder(new javax.swing.border.LineBorder(CharacterPanel.border));
		maxDexPanel.setBorder(new javax.swing.border.LineBorder(CharacterPanel.border));
		checkPanel.setBorder(new javax.swing.border.LineBorder(CharacterPanel.border));
		failurePanel.setBorder(new javax.swing.border.LineBorder(CharacterPanel.border));
	}

	/**
	 * Sets the pc
	 * @param pc
	 * @param type
	 */
	public void setPc(PlayerCharacter pc, String type) {
		this.pc = pc;
		this.typeName.setText(type);
	}

	/**
	 * Refreshes the pane
	 */
	public void refresh() {
		PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);
		final String armorType = typeName.getText();

		for(int i = 0; i < fields.size(); i++) {
			remove((java.awt.Component)fields.get(i));
		}
		fields.clear();

		List armorList = getArmorList(armorType);

		if(armorList.size() == 0) {
			setVisible(false);
		}
		else {
			setVisible(true);
			int gridY = 1;

			for(int i = 0; i < armorList.size(); i++) {
				Color color;
				if(i % 2 == 0) {
					color = CharacterPanel.bodyLight;
				}
				else {
					color = CharacterPanel.bodyMedLight;
				}
				armorList.get(i);

				StringBuffer sb = new StringBuffer();
				sb.append(ARMOR).append('.').append(armorType).append('.').append(i).append('.');

				addField(0, gridY, pcOut.getExportToken(sb.toString() + NAME), color);
				addField(1, gridY, pcOut.getExportToken(sb.toString() + TYPE), color);
				addField(2, gridY, pcOut.getExportToken(sb.toString() + ACBONUS), color);
				addField(3, gridY, pcOut.getExportToken(sb.toString() + MAXDEX), color);
				addField(4, gridY, pcOut.getExportToken(sb.toString() + ACCHECK), color);
				addField(5, gridY, pcOut.getExportToken(sb.toString() + SPELLFAIL), color);
				String sprops = pcOut.getExportToken(sb.toString() + SPROPS);

				if(!sprops.equals(BLANK)) {
					gridY++;
					addSprops(gridY, sprops, color);
				}
				gridY++;
			}
		}
		revalidate();
		repaint();
	}

	private List getArmorList(String armorType) {
		List armorList = new ArrayList();

		if(armorType.equals(ARMOR)) {
			armorList = pc.getEquipmentOfTypeInOutputOrder(ARMOR, 3);
		}
		else if(armorType.equals(SHIELD)) {
			armorList = pc.getEquipmentOfTypeInOutputOrder(SHIELD, 3);
		}
		else if(armorType.equals(ITEM)) {
			for (Iterator e = pc.getEquipmentListInOutputOrder().iterator(); e.hasNext();) {
				Equipment eq = (Equipment) e.next();

				if (eq.getBonusListString(AC) && (!eq.isArmor() && !eq.isShield())) {
					armorList.add(eq);
				}
			}
		}
		return armorList;
	}

	private void addField(int gridx, int gridy, String text, Color color) {
		javax.swing.JPanel field = new javax.swing.JPanel();
		javax.swing.JLabel data = new javax.swing.JLabel();

		field.setBackground(color);
		field.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));
		data.setFont(FONT_TEN);
		if(text.equals(BLANK)) {
			text = SPACE;
		}
		data.setText(text);
		field.add(data);

		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(field, gridBagConstraints);
		fields.add(field);
	}

	private void addSprops (int gridY, String text, Color color) {
		JTextArea sprops = new JTextArea();
		sprops.setFont(FONT_TEN);
		sprops.setLineWrap(true);
		sprops.setWrapStyleWord(true);
		sprops.setText(text);
		sprops.setBackground(color);
		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridY;
		gridBagConstraints.gridwidth = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(sprops, gridBagConstraints);
		fields.add(sprops);
	}

	/**
	 * Destroy panel
	 */
	public void destruct() {
		// Put any code here that is needed to prevent memory leaks
		// when this panel is destroyed
	}
}
