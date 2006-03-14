/*
 * CharacterSheetUtils.java
 *
 * Created on March 24, 2004, 3:23 PM
 */

package plugin.charactersheet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.util.List;

/**
 *
 * @author  ddjone3
 */
public class CharacterSheetUtils {

	/**
	 * Add cell
	 * @param cont
	 * @param text
	 * @param gridX
	 * @param gridY
	 * @param gridHeight
	 * @param gridWidth
	 * @param fill
	 * @param anchor
	 * @param layout
	 * @param color
	 */
	public static void addGbCell(Container cont, String text, int gridX, int gridY, int gridHeight, int gridWidth, int fill, int anchor, int layout, Color color) {
		CharacterSheetUtils.addGbCell(cont, text, gridX, gridY, gridHeight, gridWidth, fill, anchor, layout, color, null);
	}

	/**
	 * Add cell
	 * @param cont
	 * @param text
	 * @param gridX
	 * @param gridY
	 * @param gridHeight
	 * @param gridWidth
	 * @param fill
	 * @param anchor
	 * @param layout
	 * @param color
	 * @param componentList
	 */
	public static void addGbCell(Container cont, String text, int gridX, int gridY, int gridHeight, int gridWidth, int fill, int anchor, int layout, Color color, List componentList) {
		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		JPanel cell = new JPanel();
		JLabel cellText = new JLabel();
		cell.setLayout(new java.awt.FlowLayout(layout, 1, 0));
		cell.setBackground(color);
		cellText.setFont(new java.awt.Font("Dialog", 0, 10));
		cellText.setText(text);
		cell.add(cellText);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = gridX;
		gridBagConstraints.gridy = gridY;
		gridBagConstraints.gridwidth = gridHeight;
		gridBagConstraints.gridwidth = gridWidth;
		gridBagConstraints.fill = fill;
		gridBagConstraints.anchor = anchor;
		cont.add(cell, gridBagConstraints);
		if(componentList != null) {
			componentList.add(cell);
		}
	}

	/**
	 * Add cell
	 * @param cont
	 * @param comp
	 * @param gridX
	 * @param gridY
	 * @param gridHeight
	 * @param gridWidth
	 * @param insets
	 * @param fill
	 * @param anchor
	 */
	public static void addGbComponentCell(Container cont, Component comp, int gridX, int gridY, int gridHeight, int gridWidth, Insets insets, int fill, int anchor) {
		addGbComponentCell(cont, comp, gridX, gridY, gridHeight, gridWidth, insets, fill, anchor, null);
	}

	/**
	 * Add cell
	 * @param cont
	 * @param comp
	 * @param gridX
	 * @param gridY
	 * @param gridHeight
	 * @param gridWidth
	 * @param insets
	 * @param fill
	 * @param anchor
	 * @param componentList
	 */
	public static void addGbComponentCell(Container cont, Component comp, int gridX, int gridY, int gridHeight, int gridWidth, Insets insets, int fill, int anchor, List componentList) {
		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = gridX;
		gridBagConstraints.gridy = gridY;
		gridBagConstraints.gridheight = gridHeight;
		gridBagConstraints.gridwidth = gridWidth;
		gridBagConstraints.fill = fill;
		gridBagConstraints.anchor = anchor;
		gridBagConstraints.insets = insets;
		cont.add(comp, gridBagConstraints);
		if(componentList != null) {
			componentList.add(comp);
		}
	}

	/**
	 * Get the title
	 * @param fullTitle
	 * @return title
	 */
	public static String getTitle(String fullTitle) {
		return getTitle(fullTitle, "", "");
	}

	/**
	 * Get the title
	 * @param fullTitle
	 * @param pre
	 * @param post
	 * @return title
	 */
	public static String getTitle(String fullTitle, String pre, String post) {
		StringBuffer sb = new StringBuffer();
		if(fullTitle.indexOf("(") > -1) {
			sb.append(pre);
			sb.append(fullTitle.substring(0, fullTitle.indexOf("(")).trim());
			sb.append(post);
		}
		else {
			sb.append(pre);
			sb.append(fullTitle);
			sb.append(post);
		}
		return sb.toString();
	}

	/**
	 * Get sub title
	 * @param fullTitle
	 * @return sub title
	 */
	public static String getSubTitle(String fullTitle) {
		return getSubTitle(fullTitle, "", "");
	}

	/**
	 *
	 * @param fullTitle
	 * @param pre
	 * @param post
	 * @return sub title
	 */
	public static String getSubTitle(String fullTitle, String pre, String post) {
		if(fullTitle.indexOf("(") > -1) {
			StringBuffer sb = new StringBuffer();
			sb.append(pre);
			sb.append(fullTitle.substring(fullTitle.indexOf("(")).trim());
			sb.append(post);
			return sb.toString();
		}
		return "";
	}
}
