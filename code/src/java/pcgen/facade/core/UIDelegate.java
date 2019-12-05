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

import java.util.Optional;

import pcgen.system.PropertyContext;

/**
 * This class acts as delegate for UI functions that may
 * be called within the facade layer.
 */
public interface UIDelegate
{

    /**
     * Displays a message to the user in a warning dialog box.
     * This method has a parameter 'contextProp' which is used as
     * a boolean property in the UI's property context. It is used to enable
     * or disable the message from displaying to the user. The value of this property
     * is initially set to True and upon displaying the dialog box the user can toggle
     * property's value with a checkbox.
     *
     * @param title        the title for the dialog box
     * @param message      the message to display
     * @param checkBoxText the text that the check box should display
     * @param context      the PropertyContext that contains the contextProp
     * @param contextProp  the name of the boolean property for this message
     * @return Boolean.TRUE if user clicked Yes<br>
     * Boolean.FALSE if the user clicked No<br>
     * null if the dialog box was never displayed due to user disabling it
     */
    Boolean maybeShowWarningConfirm(String title, String message, String checkBoxText, PropertyContext context,
            String contextProp);

    /**
     * Displays a yes/no dialog
     *
     * @param title   the title for the dialog box
     * @param message the message to display
     * @return true if user clicked Yes, false otherwise
     */
    boolean showWarningConfirm(String title, String message);

    /**
     * Displays an ok dialog with a message and warning icon.
     *
     * @param title   the title for the dialog box
     * @param message the message to display
     */
    void showWarningMessage(String title, String message);

    /**
     * Displays an ok dialog with a message and error icon.
     *
     * @param title   the title for the dialog box
     * @param message the message to display
     */
    void showErrorMessage(String title, String message);

    /**
     * Displays an ok dialog with a message and information icon.
     *
     * @param title   the title for the dialog box
     * @param message the message to display
     */
    void showInfoMessage(String title, String message);

    /**
     * This displays a dialog containing a summary of character changes
     * that occurred after levels were added to the character.<br>
     * This should be called after relevant levels have been added to the
     * given character.
     *
     * @param character the character that levelled up
     * @param oldLevel  the level of the character before the levelup
     */
    void showLevelUpInfo(CharacterFacade character, int oldLevel);

    /**
     * Display a modal dialog to a user requesting that they select one or more
     * choices. The rules for what selections are available and how many
     * selections can be made are contained in the chooserFacade. The selected
     * list of the chooserFacade will contain any choices the user made.
     *
     * @param chooserFacade The choice rules.
     * @return false if the user cancelled the choice, true if it was final.
     */
    boolean showGeneralChooser(ChooserFacade chooserFacade);

    /**
     * Display a modal dialog requesting a value from the user.
     *
     * @param title        the title for the dialog box
     * @param message      the message to display
     * @param initialValue The starting value of the dialog.
     * @return The entered value, or null if cancelled.
     */
    Optional<String> showInputDialog(String title, String message, String initialValue);

    /**
     * Present a dialog to the user to allow them to build up a custom
     * piece of equipment.
     *
     * @param character    The character the equipment would be for.
     * @param equipBuilder The EquipmentBuilderFacade instance to be used for creating the item.
     * @return The result of the dialog.
     */
    CustomEquipResult showCustomEquipDialog(CharacterFacade character, EquipmentBuilderFacade equipBuilder);

    /**
     * The result of creation of a custom equipment item.
     */
    enum CustomEquipResult
    {
        CANCELLED, OK, PURCHASE
    }

    /**
     * Present a dialog to the user to allow them to build up a custom
     * spell.
     *
     * @param spellBuilderFacade The SpellBuilderFacade instance to be used for creating the spell.
     * @return true if the spell was built, false if it was cancelled.
     */
    boolean showCustomSpellDialog(SpellBuilderFacade spellBuilderFacade);
}
