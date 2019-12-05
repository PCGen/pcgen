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
package pcgen.gui2;

import java.io.File;

import pcgen.cdom.base.Constants;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.PropertyContext;
import pcgen.util.Logging;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

/**
 * This is a property context which holds UI related user preferences such as
 * screen position and colors.
 */
@SuppressWarnings("nls")
public final class UIPropertyContext extends PropertyContext
{

    private static final String CUSTOM_ITEM_COLOR = "customItemColor";
    private static final String NOT_QUALIFIED_COLOR = "notQualifiedColor";
    private static final String AUTOMATIC_COLOR = "automaticColor";
    private static final String VIRTUAL_COLOR = "virtualColor";
    private static final String QUALIFIED_COLOR = "qualifiedColor";
    private static final String SOURCE_STATUS_RELEASE_COLOR = "sourceStatusReleaseColor";
    private static final String SOURCE_STATUS_ALPHA_COLOR = "sourceStatusAlphaColor";
    private static final String SOURCE_STATUS_BETA_COLOR = "sourceStatusBetaColor";
    private static final String SOURCE_STATUS_TEST_COLOR = "sourceStatusTestColor";
    public static final String ALWAYS_OPEN_EXPORT_FILE = "alwaysOpenExportFile";
    public static final String DEFAULT_OS_TYPE = "defaultOSType";
    public static final String DEFAULT_PDF_OUTPUT_SHEET = "defaultPdfOutputSheet";
    public static final String DEFAULT_HTML_OUTPUT_SHEET = "defaultHtmlOutputSheet";
    public static final String SAVE_OUTPUT_SHEET_WITH_PC = "saveOutputSheetWithPC";
    /**
     * Should we delete all temp files on exit that were generated during outputting character.
     */
    public static final String CLEANUP_TEMP_FILES = "cleanupTempFiles";
    /**
     * Settings key for showing the source selection dialog.
     */
    public static final String SKIP_SOURCE_SELECTION = "SourceSelectionDialog.skipOnStart"; //$NON-NLS-1$
    /**
     * Settings key for basic/advanced sources.
     */
    public static final String SOURCE_USE_BASIC_KEY = "SourceSelectionDialog.useBasic"; //$NON-NLS-1$
    /**
     * What should the chooser do with a single choice?
     */
    private static final String SINGLE_CHOICE_ACTION = "singleChoiceAction"; //$NON-NLS-1$

    /**
     * The character property for the initial tab to open
     * this property corresponds to an integer value
     */
    public static final String C_PROP_INITIAL_TAB = "initialTab";
    private static UIPropertyContext instance = null;

    private UIPropertyContext()
    {
        /* We changed the format of the color and the old format does not match in format. */
        super("UIConfig.v2.ini");
        setColor(CUSTOM_ITEM_COLOR, Color.BLUE);
        setColor(NOT_QUALIFIED_COLOR, Color.RED);
        setColor(AUTOMATIC_COLOR, Color.valueOf("0xB2B200"));
        setColor(VIRTUAL_COLOR, Color.MAGENTA);
        setColor(QUALIFIED_COLOR, Color.BLACK);

    }

    private UIPropertyContext(String name, UIPropertyContext parent)
    {
        super(name, parent);
    }

    @Override
    public UIPropertyContext createChildContext(String childName)
    {
        return new UIPropertyContext(childName, this);
    }

    public static UIPropertyContext createContext(String name)
    {
        return getInstance().createChildContext(name);
    }

    public static UIPropertyContext getInstance()
    {
        if (instance == null)
        {
            instance = new UIPropertyContext();
        }
        return instance;
    }


    private Color getColor(String key)
    {
        String prop = getProperty(key);
        if (prop == null)
        {
            Logging.debugPrint("null color for " + key);
            return null;
        }

        Logging.debugPrint("color for " + key + " = " + prop);
        return Color.valueOf(prop);
    }

    private void setColor(String key, Color color)
    {
        Logging.debugPrint("setting color " + key + " to " + color);
        setProperty(key, ColorUtilty.colorToRGBString(color));
    }

    private Color initColor(String key, Color defaultValue)
    {
        String prop = initProperty(key, ColorUtilty.colorToRGBString(defaultValue));
        Logging.debugPrint("init color " + key + " to " + prop);
        return Color.valueOf(prop);
    }

    public static Color getCustomItemColor()
    {
        return getInstance().getColor(CUSTOM_ITEM_COLOR);
    }

    public static void setQualifiedColor(Color color)
    {
        getInstance().setColor(QUALIFIED_COLOR, color);
    }

    public static Color getQualifiedColor()
    {
        return getInstance().getColor(QUALIFIED_COLOR);
    }

    public static void setNotQualifiedColor(Color color)
    {
        getInstance().setColor(NOT_QUALIFIED_COLOR, color);
    }

    public static Color getNotQualifiedColor()
    {
        return getInstance().getColor(NOT_QUALIFIED_COLOR);
    }

    public static void setAutomaticColor(Color color)
    {
        getInstance().setColor(AUTOMATIC_COLOR, color);
    }

    public static Color getAutomaticColor()
    {
        return getInstance().getColor(AUTOMATIC_COLOR);
    }

    public static void setVirtualColor(Color color)
    {
        getInstance().setColor(VIRTUAL_COLOR, color);
    }

    public static Color getVirtualColor()
    {
        return getInstance().getColor(VIRTUAL_COLOR);
    }

    public static void setSourceStatusReleaseColor(Color color)
    {
        getInstance().setColor(SOURCE_STATUS_RELEASE_COLOR, color);
    }

    public static Color getSourceStatusReleaseColor()
    {
        return getInstance().initColor(SOURCE_STATUS_RELEASE_COLOR, Color.BLACK);
    }

    public static void setSourceStatusAlphaColor(Color color)
    {
        getInstance().setColor(SOURCE_STATUS_ALPHA_COLOR, color);
    }

    public static Color getSourceStatusAlphaColor()
    {
        return getInstance().initColor(SOURCE_STATUS_ALPHA_COLOR, Color.RED);
    }

    public static void setSourceStatusBetaColor(Color color)
    {
        getInstance().setColor(SOURCE_STATUS_BETA_COLOR, color);
    }

    public static Color getSourceStatusBetaColor()
    {
        return getInstance().initColor(SOURCE_STATUS_BETA_COLOR, new Color(128.0d / 255.0d, 0, 0, 1.0));
    }

    public static void setSourceStatusTestColor(Color color)
    {
        getInstance().setColor(SOURCE_STATUS_TEST_COLOR, color);
    }

    public static Color getSourceStatusTestColor()
    {
        return getInstance().initColor(SOURCE_STATUS_TEST_COLOR, Color.MAGENTA);
    }

    public static int getSingleChoiceAction()
    {
        return getInstance().initInt(SINGLE_CHOICE_ACTION, Constants.CHOOSER_SINGLE_CHOICE_METHOD_NONE);
    }

    public static void setSingleChoiceAction(int action)
    {
        getInstance().setInt(SINGLE_CHOICE_ACTION, action);
    }

    /**
     * Attempts to create the property key for this character for the given property.
     * This allows for character specific properties such that the key created with this method
     * can be used as the key for any of the other PropertyContext methods.
     * The following is a typical example of its usage:
     * <code>
     * String charKey = UIPropertyContext.createCharacterPropertyKey(aCharacter, "allowNegativeMoney");<br>
     * if(charKey != null){<br>
     * boolean bool = UIPropertyContext.getInstance().getBoolean(charKey);<br>
     * }<br>
     * </code>
     *
     * @param character a CharacterFacade
     * @param key       a String property key
     * @return the character property key or null if it could not be created
     */
    public static String createCharacterPropertyKey(CharacterFacade character, String key)
    {
        return createFilePropertyKey(character.getFileRef().get(), key);
    }

    @Nullable
    static String createFilePropertyKey(File file, String key)
    {
        if (file == null)
        {
            return null;
        }
        String path = file.getAbsolutePath();
        return path + '.' + key;
    }

}
