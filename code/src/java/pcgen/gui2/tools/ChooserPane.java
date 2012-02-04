/*
 * ChooserPane.java
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
 * Created on Jun 27, 2008, 12:53:57 PM
 */
package pcgen.gui2.tools;

import java.awt.Component;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class ChooserPane extends FlippingSplitPane
{

    private static final long serialVersionUID = -8364721218562027273L;
    private FlippingSplitPane subSplitPane;
    private InfoPane infoPane;

    public ChooserPane()
    {
        this.subSplitPane = new FlippingSplitPane(VERTICAL_SPLIT);
        this.infoPane = new InfoPane();

        subSplitPane.setBottomComponent(infoPane);
        setRightComponent(subSplitPane);

        setDividerSize(7);
        setContinuousLayout(true);
        setOneTouchExpandable(true);
    }

    public void setPrimaryChooserComponent(Component c)
    {
        setLeftComponent(c);
    }

    public void setSecondaryChooserComponent(Component c)
    {
        subSplitPane.setTopComponent(c);
    }

    public void setInfoPaneText(String text)
    {
        infoPane.setText(text);
    }

    public void setInfoPaneTitle(String title)
    {
        infoPane.setTitle(title);
    }

}
