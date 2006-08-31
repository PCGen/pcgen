package pcgen.gui.tabs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

import pcgen.gui.utils.Utility;

/**
 * Tab utilities
 */
public class InfoTabUtils 
{
	/**
	 * Gets the index of the selected row for this ListSelectionEvent.
	 * 
	 * <p>This method returns the minimum selection index and so may not work 
	 * with multiselect lists.
	 *   
	 * @param evt The ListSelectionEvent
	 * 
	 * @return The row index or -1 if the index could not be found.
	 */
	public static int getSelectedIndex(final ListSelectionEvent evt)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) evt.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}
	
	/**
	 * Create a filter pane for a tab
	 * 
	 * @param treeLabel
	 * @param treeCb
	 * @param filterLabel
	 * @param filterText
	 * @param clearButton
	 * @return the Filter pane
	 */
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
