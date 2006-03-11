/*
 * Constants.java
 * Copyright 2002 (C) Jonas Karlsson
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
 * $Id: GuiConstants.java,v 1.19 2006/01/31 12:43:40 karianna Exp $
 */
package pcgen.gui;


/**
 * This interface holds a few gui constants.
 * These constants were moved from assorted GUI classes in order to reduce connections between
 * the core and gui packages.
 *
 * @author     Jonas Karlsson
 * @version    $Revision: 1.19 $
 */
public interface GuiConstants
{
	// view modes for tables on race tab
	/** INFORACE_VIEW_NAME = 0 */
	int INFORACE_VIEW_NAME           			= 0;
	/** INFORACE_VIEW_SOURCE = 5 */
	int INFORACE_VIEW_SOURCE         			= 5;
	/** INFORACE_VIEW_TYPE = 3 */
	int INFORACE_VIEW_TYPE           			= 3;
	/** INFORACE_VIEW_RACETYPE_NAME = 1 */
	int INFORACE_VIEW_RACETYPE_NAME				= 1;
	/** INFORACE_VIEW_RACETYPE_SUBTYPE_NAME = 2 */
	int INFORACE_VIEW_RACETYPE_SUBTYPE_NAME		= 2;
	/** INFORACE_VIEW_ALL_TYPES = 4 */
	int INFORACE_VIEW_ALL_TYPES					= 4;

	//view modes for tables on classes tab
	/** INFOCLASS_VIEW_NAME = 0 */
	int INFOCLASS_VIEW_NAME        = 0;
	/** INFOCLASS_VIEW_TYPE_NAME = 1 */
	int INFOCLASS_VIEW_TYPE_NAME   = 1;
	/** INFOCLASS_VIEW_SOURCE_NAME = 2 */
	int INFOCLASS_VIEW_SOURCE_NAME = 2;

	//view modes for tables on skills tab
	/** INFOSKILLS_VIEW_STAT_TYPE_NAME = 0 */
	int INFOSKILLS_VIEW_STAT_TYPE_NAME = 0;
	/** INFOSKILLS_VIEW_STAT_NAME = 1 */
	int INFOSKILLS_VIEW_STAT_NAME      = 1;
	/** INFOSKILLS_VIEW_TYPE_NAME = 2 */
	int INFOSKILLS_VIEW_TYPE_NAME      = 2;
	/** INFOSKILLS_VIEW_COST_TYPE_NAME = 3 */
	int INFOSKILLS_VIEW_COST_TYPE_NAME = 3;
	/** INFOSKILLS_VIEW_COST_NAME = 4 */
	int INFOSKILLS_VIEW_COST_NAME      = 4;
	/** INFOSKILLS_VIEW_NAME = 5 */
	int INFOSKILLS_VIEW_NAME           = 5;

	// view modes for tables on feat tab
	/** view mode for Type->Name */
	int INFOFEATS_VIEW_TYPENAME      = 0;
	/** view mode for Name (essentially a JTable) */
	int INFOFEATS_VIEW_NAMEONLY      = 1;
	/** view in requirement tree mode */
	int INFOFEATS_VIEW_PREREQTREE    = 2;
	/** view mode for Source->Name */
	int INFOFEATS_VIEW_SOURCENAME    = 3; 

	//view modes for tables on inventory tab
	/** Type/SubType/Name */
	int INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME = 0;
	/** Type/Name */
	int INFOINVENTORY_VIEW_TYPE_NAME         = 1;
	/** Name */
	int INFOINVENTORY_VIEW_NAME              = 2;
	/** All Types */
	int INFOINVENTORY_VIEW_ALL_TYPES         = 3;
	/** Source/Name */
	int INFOINVENTORY_VIEW_SOURCE_NAME       = 4;

	//view modes for tables on gear tab
	/** INFOEQUIPPING_VIEW_NAME = 0 */
	int INFOEQUIPPING_VIEW_NAME     = 0;
	/** INFOEQUIPPING_VIEW_LOCATION = 1 */
	int INFOEQUIPPING_VIEW_LOCATION = 1;
	/** INFOEQUIPPING_VIEW_EQUIPPED = 2 */
	int INFOEQUIPPING_VIEW_EQUIPPED = 2;
	/** INFOEQUIPPING_VIEW_TYPE = 3 */
	int INFOEQUIPPING_VIEW_TYPE     = 3;

	// view modes for tables on domain tab
	/** INFODOMAIN_VIEW_NAME = 0 */
	int INFODOMAIN_VIEW_NAME      = 0;
	/** INFODOMAIN_VIEW_ALIGNMENT = 1 */
	int INFODOMAIN_VIEW_ALIGNMENT = 1;
	/** INFODOMAIN_VIEW_DOMAIN = 2 */
	int INFODOMAIN_VIEW_DOMAIN    = 2;
	/** INFODOMAIN_VIEW_PANTHEON = 3 */
	int INFODOMAIN_VIEW_PANTHEON  = 3;
	/** INFODOMAIN_VIEW_SOURCE = 4 */
	int INFODOMAIN_VIEW_SOURCE    = 4;

	//view modes for tables on spells tab
	/** INFOSPELLS_VIEW_CLASS = 0 */
	int INFOSPELLS_VIEW_CLASS      = 0;
	/** INFOSPELLS_VIEW_LEVEL = 1 */
	int INFOSPELLS_VIEW_LEVEL      = 1;
	/** INFOSPELLS_VIEW_DESCRIPTOR = 2 */
	int INFOSPELLS_VIEW_DESCRIPTOR = 2;
	/** INFOSPELLS_VIEW_RANGE = 3 */
	int INFOSPELLS_VIEW_RANGE      = 3;
	/** INFOSPELLS_VIEW_DURATION = 4 */
	int INFOSPELLS_VIEW_DURATION   = 4;
	/** INFOSPELLS_VIEW_TYPE = 5 */
	int INFOSPELLS_VIEW_TYPE       = 5;
	/** INFOSPELLS_VIEW_SCHOOL = 6 */
	int INFOSPELLS_VIEW_SCHOOL     = 6;
	/** INFOSPELLS_VIEW_NOTHING = 7 */
	int INFOSPELLS_VIEW_NOTHING    = 7;

	// Available list types for the spells tab
	/** Only the known spells should be displayed. */
	int INFOSPELLS_AVAIL_KNOWN = 0;
	/** All spells in the character's spell lists should be displayed. */
	int INFOSPELLS_AVAIL_SPELL_LIST = 1;
	/** All spells in all spell lists should be displayed. */
	int INFOSPELLS_AVAIL_ALL_SPELL_LISTS = 2;
	
	//output orders for tables
	/** INFOSKILLS_OUTPUT_BY_NAME_ASC = 0 */
	int INFOSKILLS_OUTPUT_BY_NAME_ASC    = 0;
	/** INFOSKILLS_OUTPUT_BY_NAME_DSC = 1 */
	int INFOSKILLS_OUTPUT_BY_NAME_DSC    = 1;
	/** INFOSKILLS_OUTPUT_BY_TRAINED_ASC = 2 */
	int INFOSKILLS_OUTPUT_BY_TRAINED_ASC = 2;
	/** INFOSKILLS_OUTPUT_BY_TRAINED_DSC = 3 */
	int INFOSKILLS_OUTPUT_BY_TRAINED_DSC = 3;
	/** INFOSKILLS_OUTPUT_BY_MANUAL = 4 */
	int INFOSKILLS_OUTPUT_BY_MANUAL      = 4;

	// EXPORT OPTIONS
	/** EXPORT_AS_TEXT = "Text" */
	final static String EXPORT_AS_TEXT = "Text";
	/** EXPORT_AS_HTML_XML = "HTML_XML" */
	final static String EXPORT_AS_HTML_XML = "HTML_XML";
}
