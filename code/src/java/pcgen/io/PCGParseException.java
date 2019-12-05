/*
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
package pcgen.io;

import pcgen.cdom.base.Constants;

/**
 * {@code ParseException}<br>
 */
final class PCGParseException extends Exception
{
    private final String errorLine;
    private final String errorMessage;
    private final String errorMethod;

    PCGParseException(String errorMethod, String errorLine, String errorMessage)
    {
        this(errorMethod, errorLine, errorMessage, null);
    }

    PCGParseException(String errorMethod, String errorLine, String errorMessage, Throwable cause)
    {
        super("Method: " + errorMethod + Constants.LINE_SEPARATOR + "Line: " + errorLine + Constants.LINE_SEPARATOR
                + "Message: " + errorMessage, cause);

        this.errorMethod = errorMethod;
        this.errorLine = errorLine;
        this.errorMessage = errorMessage;
    }

    /**
     * @return error line
     */
    public String getLine()
    {
        return errorLine;
    }

    /**
     * @return error message
     */
    @Override
    public String getMessage()
    {
        return errorMessage;
    }

    /**
     * @return error method
     */
    public String getMethod()
    {
        return errorMethod;
    }
}
