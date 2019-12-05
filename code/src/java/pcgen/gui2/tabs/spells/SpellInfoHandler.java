/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.spells;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.SpellSupportFacade.RootNode;
import pcgen.facade.core.SpellSupportFacade.SpellNode;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JTreeViewTable;

import org.apache.commons.lang3.StringUtils;

class SpellInfoHandler implements ListSelectionListener
{

    private CharacterFacade character;
    private final JTreeViewTable<?> availableTable;
    private final JTreeViewTable<?> selectedTable;
    private final InfoPane spellsPane;
    private String currText;

    public SpellInfoHandler(CharacterFacade character, JTreeViewTable<?> table1, JTreeViewTable<?> table2,
            InfoPane spellsPane)
    {
        this.spellsPane = spellsPane;
        this.availableTable = table1;
        this.selectedTable = table2;
        this.character = character;
        this.currText = ""; //$NON-NLS-1$
    }

    public void install()
    {
        availableTable.getSelectionModel().addListSelectionListener(this);
        selectedTable.getSelectionModel().addListSelectionListener(this);
        spellsPane.setText(currText);
    }

    public void uninstall()
    {
        availableTable.getSelectionModel().removeListSelectionListener(this);
        selectedTable.getSelectionModel().removeListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            Object obj;
            if (e.getSource() == availableTable.getSelectionModel())
            {
                obj = availableTable.getSelectedObject();
            } else
            {
                obj = selectedTable.getSelectedObject();
            }
            if (obj instanceof SpellNode)
            {
                SpellNode node = (SpellNode) obj;
                String text = character.getInfoFactory().getHTMLInfo(node.getSpell());
                currText = text;
                spellsPane.setText(text);
            } else if (obj instanceof RootNode)
            {
                RootNode node = (RootNode) obj;
                String text = character.getInfoFactory().getSpellBookInfo(node.getName());
                if (!StringUtils.isEmpty(text))
                {
                    currText = text;
                    spellsPane.setText(text);
                }
            }
        }
    }

}
