/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
/**
 * This is a collection of custom directives and functions that can be called
 * from within Freemarker templates as part of the PCGen export system.
 * <p>
 * Each directive class in this folder must be registered in the ExportHandler
 * so that it can be called within a template.
 * <p>
 * Developers' note: All directive classes must be thread safe - no values
 * specific to a particular export request can be stored as instance of
 * class variables.
 */
package pcgen.io.freemarker;
