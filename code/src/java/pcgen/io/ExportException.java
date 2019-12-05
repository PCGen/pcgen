/*
 * Copyright James Dempsey, 2014
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
package pcgen.io;

/**
 * The Class {@code ExportException} indicates an export action failed.
 * This is normally the result of a problem in the export template or in
 * the system configuration.
 */
@SuppressWarnings("serial")
public class ExportException extends Exception
{

    /**
     * Constructs an instance of {@code ExportException}
     * with the specified {@link Throwable rootCause}
     * and the specified detail message.
     *
     * @param msg       the detail message.
     * @param rootCause the root cause of the exception.
     */
    ExportException(String msg, Throwable rootCause)
    {
        super(msg, rootCause);
    }

}
