/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tools;

import java.awt.Dimension;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import pcgen.base.lang.UnreachableError;
import pcgen.system.LanguageBundle;

public class InfoPane extends JScrollPane
{

    private JTextPane textPane;
    private TitledBorder titledBorder;

    public InfoPane()
    {
        this("in_source_info"); //$NON-NLS-1$
    }

    public InfoPane(String title)
    {
        super(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        String name = title;
        if (title.startsWith("in_")) //$NON-NLS-1$
        {
            name = LanguageBundle.getString(title);
        }
        this.titledBorder =
                BorderFactory.createTitledBorder(null, name, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        this.textPane = new JTextPane();
        initComponents();
    }

    private void initComponents()
    {
        setBorder(BorderFactory.createCompoundBorder(titledBorder, getBorder()));

        textPane.setEditable(false);
        setViewportView(textPane);
        textPane.setContentType("text/html"); //$NON-NLS-1$
        setPreferredSize(new Dimension(300, 200));
    }

    public String getTitle()
    {
        return titledBorder.getTitle();
    }

    public void setTitle(String title)
    {
        titledBorder.setTitle(title);
        validate();
        repaint();
    }

    public void setText(String text)
    {
        //This is done so the vertical scroll bar goes back up to the top when the text is changed
        EditorKit kit = textPane.getEditorKit();
        Document newDoc = kit.createDefaultDocument();
        try
        {
            kit.read(new StringReader(text), newDoc, 0);
        } catch (IOException | BadLocationException ex)
        {
            throw new UnreachableError(ex);
        }
        textPane.setDocument(newDoc);
    }

    /**
     * Adds a hyperlink listener for notification of any changes, for example when a
     * link is selected and entered.
     *
     * @param linkListener The listener.
     */
    void addHyperlinkListener(HyperlinkListener linkListener)
    {
        textPane.addHyperlinkListener(linkListener);
    }

    /**
     * Removes a hyperlink listener.
     *
     * @param linkListener The listener.
     */
    void removeHyperlinkListener(HyperlinkListener linkListener)
    {
        textPane.removeHyperlinkListener(linkListener);
    }

}
