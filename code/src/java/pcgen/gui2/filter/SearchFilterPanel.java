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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SearchFilterPanel extends JPanel
		implements DisplayableFilter, DocumentListener
{

	private FilterHandler filterHandler;
	private JTextField searchField = new JTextField();

	public SearchFilterPanel()
	{
		searchField.getDocument().addDocumentListener(this);
		setLayout(new BorderLayout());
		add(new JLabel("Filter:"), BorderLayout.WEST);
		add(searchField, BorderLayout.CENTER);
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

}
