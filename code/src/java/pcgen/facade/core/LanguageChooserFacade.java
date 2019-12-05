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
package pcgen.facade.core;

import pcgen.core.Language;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;

public interface LanguageChooserFacade
{

    /**
     * @return the currently available languages
     */
    ListFacade<Language> getAvailableList();

    /**
     * @return the currently selected languages
     */
    ListFacade<Language> getSelectedList();

    /**
     * adds a language to the selected list and
     * updates the available list to show that it
     * is no longer available
     *
     * @param language the language to be added
     */
    void addSelected(Language language);

    /**
     * removes a language from the selected list and
     * updates the available list to show that it
     * is now available
     *
     * @param language the language to be removed
     */
    void removeSelected(Language language);

    ReferenceFacade<Integer> getRemainingSelections();

    /**
     * applies the changes in selection to the underlying character
     */
    void commit();

    /**
     * undos any changes made to the selected and available list
     */
    void rollback();

    /**
     * this returns the string that will be displayed in the summary tab, as
     * well as in the title of the dialog box that this chooser represents
     *
     * @return the name of this chooser
     */
    String getName();

}
