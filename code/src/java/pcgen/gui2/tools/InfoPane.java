/*
 * InfoPane.java
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
 * Created on Jun 26, 2008, 9:32:04 PM
 */
package pcgen.gui2.tools;

import java.awt.Dimension;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import pcgen.base.lang.UnreachableError;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class InfoPane extends JScrollPane
{

	private JTextPane textPane;
	private TitledBorder titledBorder;

	public InfoPane()
	{
		this("Info");
	}

	public InfoPane(String title)
	{
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.titledBorder = BorderFactory.createTitledBorder(null, title,
															 TitledBorder.CENTER,
															 TitledBorder.DEFAULT_POSITION);
		this.textPane = new JTextPane();
		initComponents();
	}

	private void initComponents()
	{
		setBorder(BorderFactory.createCompoundBorder(titledBorder, getBorder()));

		textPane.setEditable(false);
		setViewportView(textPane);
		textPane.setContentType("text/html");
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
		}
		catch (IOException ex)
		{
			throw new UnreachableError(ex);
		}
		catch (BadLocationException ex)
		{
			throw new UnreachableError(ex);
		}
		textPane.setDocument(newDoc);
	}

}
