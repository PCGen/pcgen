/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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

import java.util.Arrays;
import java.util.List;

import pcgen.facade.core.InfoFactory;
import pcgen.facade.core.SpellFacade;
import pcgen.facade.core.SpellSupportFacade.SpellNode;
import pcgen.facade.core.SpellSupportFacade.SuperNode;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;

import org.apache.commons.lang3.StringUtils;

class SpellNodeDataView implements DataView<SuperNode>
{

    private final List<? extends DataViewColumn> columns;
    private final String prefsKey;
    private final InfoFactory infoFactory;

    public SpellNodeDataView(boolean initiallyVisible, String prefsKey, InfoFactory infoFactory)
    {
        super();
        this.prefsKey = prefsKey;
        this.infoFactory = infoFactory;
        columns = Arrays.asList(new DefaultDataViewColumn("School", String.class, initiallyVisible),
                new DefaultDataViewColumn("Subschool", String.class, initiallyVisible),
                new DefaultDataViewColumn("Descriptors", String.class, initiallyVisible),
                new DefaultDataViewColumn("Components", String.class, initiallyVisible),
                new DefaultDataViewColumn("in_descrip", String.class, initiallyVisible),
                new DefaultDataViewColumn("Range", String.class), new DefaultDataViewColumn("Duration", String.class),
                new DefaultDataViewColumn("Source", String.class), new DefaultDataViewColumn("Cast Time", String.class));
    }

    @Override
    public Object getData(SuperNode obj, int column)
    {
        if (obj instanceof SpellNode)
        {
            SpellFacade spell = ((SpellNode) obj).getSpell();
            if (spell != null)
            {
                switch (column)
                {
                    case 0:
                        return spell.getSchool();
                    case 1:
                        return spell.getSubschool();
                    case 2:
                        return StringUtils.join(spell.getDescriptors(), ", ");
                    case 3:
                        return spell.getComponents();
                    case 4:
                        return infoFactory.getDescription(spell);
                    case 5:
                        return spell.getRange();
                    case 6:
                        return spell.getDuration();
                    case 7:
                        return spell.getSource();
                    case 8:
                        return spell.getCastTime();
                    default:
                        //Case not caught, should this cause an error?
                        break;
                }
            }
        }
        return null;
    }

    @Override
    public void setData(Object value, SuperNode element, int column)
    {
    }

    @Override
    public List<? extends DataViewColumn> getDataColumns()
    {
        return columns;
    }

    @Override
    public String getPrefsKey()
    {
        return prefsKey;
    }

}
