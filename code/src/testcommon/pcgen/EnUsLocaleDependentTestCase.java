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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract TestCase framework for tests that are US Locale dependent. Before the tests this automatically switch the
 * locale, and also does at the end. Manually calling the methods, or the super classes', especially {@link #before},
 * will break the locale for further tests.
 */
public abstract class EnUsLocaleDependentTestCase extends
        LocaleDependentTestCase
{

    @BeforeEach
    public void changeLocale()
    {
        before(Locale.US);
    }

    @AfterEach
    public void restoreLocale()
    {
        LocaleDependentTestCase.after();
    }

}
