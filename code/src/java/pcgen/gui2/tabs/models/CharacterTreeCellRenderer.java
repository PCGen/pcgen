/*
 * Copyright 2014 Connor
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
package pcgen.gui2.tabs.models;

import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.TreeColumnCellRenderer;
import pcgen.gui3.utilty.ColorUtilty;

public abstract class CharacterTreeCellRenderer extends TreeColumnCellRenderer
{

    protected CharacterFacade character = null;

    public CharacterTreeCellRenderer()
    {
        setTextNonSelectionColor(ColorUtilty.colorToAWTColor(UIPropertyContext.getQualifiedColor()));
    }

    public void setCharacter(CharacterFacade character)
    {
        this.character = character;
    }

    public Handler createHandler(CharacterFacade character)
    {
        return new Handler(character);
    }

    public class Handler
    {

        private final CharacterFacade character;

        public Handler(CharacterFacade character)
        {
            this.character = character;
        }

        public void install()
        {
            setCharacter(character);
        }

        public void uninstall()
        {
            setCharacter(null);
        }

    }

}
