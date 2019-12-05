/*
 * Copyright 2010(C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 */
package pcgen.system;

import pcgen.core.SettingsHandler;

/**
 * The Class {@code LegacySettings} stores the settings managed by the
 * original SettingsHandler class. It is expected that most settings will be
 * migrated away to other PropertyContexts as part of the CDOM UI project.
 */
final class LegacySettings extends PropertyContext
{
    /**
     * Our singleton instance
     */
    private static final LegacySettings INSTANCE = new LegacySettings();

    /**
     * Create a new LegacySettings instance. Private to avoid multiples.
     */
    private LegacySettings()
    {
        super("legacy.ini", null, SettingsHandler.getOptions());
    }

    /**
     * @return The singleton LegacySettings instance.
     */
    public static LegacySettings getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void afterPropertiesLoaded()
    {
        SettingsHandler.getOptionsFromProperties(null);

        super.afterPropertiesLoaded();
    }

    @Override
    protected void beforePropertiesSaved()
    {
        SettingsHandler.setOptionsProperties(null);
        super.beforePropertiesSaved();
    }

}
