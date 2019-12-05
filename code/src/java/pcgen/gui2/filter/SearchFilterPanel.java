/*
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

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.facade.core.InfoFacade;
import pcgen.gui2.tools.Icons;
import pcgen.system.LanguageBundle;

import org.apache.commons.lang3.StringUtils;

/**
 * A text search filtering bar including the title, the text field and a clear
 * button. When text is typed into the field the table contents will be
 * filtered to only those matching the search text.
 */
public class SearchFilterPanel extends JPanel
        implements DisplayableFilter<Object, Object>, DocumentListener, ActionListener
{

    private FilterHandler filterHandler;
    private final JTextField searchField = new JTextField();
    private final JButton clearButton = new JButton(Icons.CloseX9.getImageIcon());

    public SearchFilterPanel()
    {
        searchField.getDocument().addDocumentListener(this);
        clearButton.addActionListener(this);
        setLayout(new BorderLayout());
        add(new JLabel(LanguageBundle.getString("in_filterLabel")), BorderLayout.WEST);
        add(searchField, BorderLayout.CENTER);
        add(clearButton, BorderLayout.EAST);
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        refreshFilter();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        refreshFilter();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        refreshFilter();
    }

    private void refreshFilter()
    {
        String text = searchField.getText();
        filterHandler.setSearchEnabled(text != null && !text.isEmpty());
        filterHandler.refilter();
        filterHandler.scrollToTop();
    }

    @Override
    public boolean accept(Object context, Object element)
    {
        String typeStr = ""; //$NON-NLS-1$
        String abbStr = ""; //$NON-NLS-1$
        if (element instanceof InfoFacade)
        {
            typeStr = ((InfoFacade) element).getType();
        } else if (element instanceof Campaign)
        {
            typeStr = ((Campaign) element).getListAsString(ListKey.BOOK_TYPE);
            abbStr = ((Campaign) element).get(StringKey.SOURCE_SHORT);
        }
        final String searchText = searchField.getText();
        return StringUtils.containsIgnoreCase(element.toString(), searchText)
                || StringUtils.containsIgnoreCase(typeStr, searchText)
                || StringUtils.containsIgnoreCase(abbStr, searchText);
    }

    @Override
    public Component getFilterComponent()
    {
        return this;
    }

    @Override
    public void setFilterHandler(FilterHandler handler)
    {
        this.filterHandler = handler;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        searchField.setText("");
        // A refreshFilter call is triggered by the text change.
    }
}
