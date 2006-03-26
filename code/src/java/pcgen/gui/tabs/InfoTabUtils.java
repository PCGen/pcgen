package pcgen.gui.tabs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pcgen.gui.utils.Utility;

public class InfoTabUtils {
	public static JPanel createFilterPane(JLabel treeLabel, JComboBox treeCb, JLabel filterLabel, JTextField filterText, JButton clearButton)
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel filterPanel = new JPanel(new GridBagLayout());
		int i = 0;

		if(treeLabel != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(treeLabel, c);
		}
		
		if(treeCb != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(treeCb, c);
		}

		if(filterLabel != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(filterLabel, c);
		}
		
		if(filterText != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 95, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(filterText, c);
		}
		
		if(clearButton != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(0, 2, 0, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			clearButton.setEnabled(false);
			filterPanel.add(clearButton, c);
		}
		return filterPanel;
	}

}
