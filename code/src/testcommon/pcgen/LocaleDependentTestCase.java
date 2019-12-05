/*
 * Copyright 2012 Vincent Lhote
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
package pcgen;

import java.util.Locale;

import pcgen.system.LanguageBundle;

/**
 * Abstract TestCase framework for tests that are Locale dependent.
 */
public abstract class LocaleDependentTestCase
{
    private static Locale PREVIOUS_LOCALE;

    /**
     * Change locale to the new value, reloading the bundle and remembering the old value for {@link #after}.
     *
     * @param l new locale
     */
    public static void before(Locale l)
    {
        PREVIOUS_LOCALE = Locale.getDefault();
        Locale.setDefault(l);
        LanguageBundle.reload();
    }

    public static void after()
    {
        Locale.setDefault(PREVIOUS_LOCALE);
        LanguageBundle.reload();
    }
}
