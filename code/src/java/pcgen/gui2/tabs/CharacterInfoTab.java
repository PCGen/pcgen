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
package pcgen.gui2.tabs;

import java.util.HashMap;

import pcgen.facade.core.CharacterFacade;

/**
 * This interface must be implemented by all tabs that display character
 * information: summary tab, classes tab, abilities tab, inventory tab, etc....
 * The goal of this class is to create a separation of UI models and the UI
 * components that use them. By doing this, knowledge of the CharacterFacade can
 * be isolated from the tab itself and stored solely within the models that use
 * the CharacterFacade. These models can then be cached so that new models are
 * not created each time a charcter is selected. Reusing models, although it
 * increases overhead, can make swiching between character tabs several times
 * faster than if a new model was created each time. A fast UI greatly enhances
 * a user's experience when using a program.
 * <p>
 * To achieve this speed goal this interface defines the following methods:<br>
 * <code>
 * createModels(Character)<br>
 * restoreModels(Hashtable)<br>
 * storeModels(Hashtable)<br>
 * </code> These methods are used by the character tab handler to allow each tab
 * to independently store and manage its own UI models within a hashtable of its
 * own creation. Each of these methods are used in a defined order. When a new
 * character is created the {@code createModels} is called by the tab
 * handler to create the models for this tab. Whenever characters selection
 * occurs, the tab handler calls {@code storeModels} with the old selected
 * character's state hashtable followed by a call to {@code restoreModels}
 * with the new selected character's state hashtable. It is guaranteed that any
 * call to {@code restoreModels} will be preceded by a call to
 * {@code storeModels} if a character is currently displayed on this tab.
 * <p>
 * Note: The states crated by the a tab's {@code createModels} are
 * guaranteed to not be modified in anyway by the tab handler. The only changes
 * that would occur to any state would be in a call to {@code storeModels}.
 */
public interface CharacterInfoTab
{

    /**
     * This gives the CharacterInfoTab a chance to store the models that it will
     * use for a given character into a Hashtable. This returned Hashtable will
     * be used as arguments in storeModels(Hashtable) and
     * restoreModels(Hashtable).
     * <p>
     * Note: Character tabs implementing this method should <b>NOT</b> make any
     * changes to its UI components, this method is purely meant to create
     * models and making changes to the UI components could produce undesirable
     * side effects.
     *
     * @param character the character that this state is for.
     * @return a ModelMap containing the UI models created for this character
     */
    ModelMap createModels(CharacterFacade character);

    /**
     * This restores this character tab to a state that contains the models for
     * some given character. The models in question were created during the call
     * to {@code createModels(CharacterFacade)} When this is called the tab
     * should attach the models contained within this state to the UI components
     * of this tab.
     *
     * @param models a ModelMap containing the models for this character
     */
    void restoreModels(ModelMap models);

    /**
     * This is called to save any character specific info that might have
     * changed since {@code restoreModels} was called. Implementors might
     * also use this method to detach non swappable models from the UI
     * components, i.e. listeners.
     *
     * @param models a ModelMap that contains the models for some character
     */
    void storeModels(ModelMap models);

    /**
     * This returns an Action that will be used by the InfoTabbedPane to render
     * the tab's title. Any changes in the Action's properties will be
     * immediately rendered onto the tab's title space.
     *
     * @return an Action for the tab title
     */
    TabTitle getTabTitle();

    class ModelMap
    {

        private final HashMap<Object, Object> classMap = new HashMap<>();

        public <T> T get(Class<T> key)
        {
            return (T) classMap.get(key);
        }

        public <T> void put(Class<T> key, T value)
        {
            classMap.put(key, value);
        }

    }

}
