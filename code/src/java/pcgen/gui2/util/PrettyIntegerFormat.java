/*
 * Copyright 2013 (C) Vincent Lhote
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
package pcgen.gui2.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * This is to format little integers, usually ability modifiers. It uses the  * plus and minus sign (and not the
 * minus-hyphen one). The minus sign has the same width than the plus one.
 * <p>
 * It will not display big modifiers correctly {@literal (modifiers > 1,000)},
 * because the thousand separator is locale dependent.
 * This is not really a problem, because their is an ability limit of 1,000 in PCGen.
 */
public final class PrettyIntegerFormat extends DecimalFormat
{

    private static final long serialVersionUID = 2551454019393922738L;

    /**
     * It is usually better to use {@link #getFormat()} to use a single instance of the formatter in the whole program.
     */
    private PrettyIntegerFormat()
    {
        // + and - should not need to be internationalized
        DecimalFormatSymbols decimalFormatSymbols = getDecimalFormatSymbols();
        decimalFormatSymbols.setMinusSign('\u2212');
        setDecimalFormatSymbols(decimalFormatSymbols);
        setPositivePrefix("+"); //$NON-NLS-1$
    }

    private static final class InstanceHolder
    {
        private static final DecimalFormat INSTANCE = new PrettyIntegerFormat();
    }

    public static DecimalFormat getFormat()
    {
        return InstanceHolder.INSTANCE;
    }
}
