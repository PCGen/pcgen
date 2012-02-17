/*
 * SearchFilterPanel.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on May 14, 2010, 2:08:09 PM
 */
package pcgen.gui2.filter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import pcgen.gui2.tools.Icons;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * A text search filtering bar including the title, the text field and a clear 
 * button. When text is typed into the field the table contents will be 
 * filtered to only those matching the search text.
 *
 * <br/>
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 * 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 * @version $Revision:  $
 */
public class SearchFilterPanel extends JPanel
		implements DisplayableFilter, DocumentListener, ActionListener
{

	private FilterHandler filterHandler;
	private JTextField searchField = new JTextField();
	private JButton clearButton = new JButton(Icons.CloseX9.getImageIcon());

	public SearchFilterPanel()
	{
		searchField.getDocument().addDocumentListener(this);
		clearButton.addActionListener(this);
		setLayout(new BorderLayout());
		add(new JLabel(LanguageBundle.getString("in_filterLabel")), BorderLayout.WEST);
		add(searchField, BorderLayout.CENTER);
		add(clearButton, BorderLayout.EAST);
	}

	public void insertUpdate(DocumentEvent e)
	{
		refreshFilter();
	}

	public void removeUpdate(DocumentEvent e)
	{
		refreshFilter();
	}

	public void changedUpdate(DocumentEvent e)
	{
		refreshFilter();
	}

	private void refreshFilter()
	{
		String text = searchField.getText();
		filterHandler.setSearchEnabled(text != null && text.length() > 0);
		filterHandler.refilter();
	}

	public boolean accept(Object context, Object element)
	{
		return StringUtils.containsIgnoreCase(element.toString(), searchField.getText());
	}

	public Component getFilterComponent()
	{
		return this;
	}

	public void setFilterHandler(FilterHandler handler)
	{
		this.filterHandler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		searchField.setText("");
		// A refreshFilter call is triggered by the text change.
	}
}
