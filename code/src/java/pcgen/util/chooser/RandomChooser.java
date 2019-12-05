/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 */
package pcgen.util.chooser;

import pcgen.base.util.RandomUtil;
import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.util.ListFacade;

/**
 * An implementation of the Chooser Interface that does not display a GUI but
 * simply selects a random choice from the available list of options.
 */
@Deprecated()
public final class RandomChooser
{
    public boolean makeChoice(ChooserFacade chooserFacade)
    {
        while ((chooserFacade.getRemainingSelections().get() > 0) && !chooserFacade.getAvailableList().isEmpty())
        {
            ListFacade<InfoFacade> availableList = chooserFacade.getAvailableList();
            final InfoFacade addObj = availableList.getElementAt(RandomUtil.getRandomInt(availableList.getSize() - 1));
            chooserFacade.addSelected(addObj);
        }

        if ((chooserFacade.getRemainingSelections().get() == 0) || !chooserFacade.isRequireCompleteSelection())
        {
            chooserFacade.commit();
            return true;
        }

        chooserFacade.rollback();
        return false;
    }

}
