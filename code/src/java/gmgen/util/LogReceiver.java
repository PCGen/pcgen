/*
 * Defines an interface for receiving Log events
 * Copyright (C) 2003 Tod Milam
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
package gmgen.util;

/**
 * LogReceiver is an interface specifying methods to be called for logging messages.
 * It is used by the LogUtilities class to allow log messages to be sent to any
 * number of logging destinations.
 */
@FunctionalInterface
public interface LogReceiver
{
	/**
	 * Logs a message associated with a specific owner.
	 *
	 * @param owner the owner of the message being logged.
	 * @param message the message to log.
	 */
	public void logMessage(String owner, String message);
}
