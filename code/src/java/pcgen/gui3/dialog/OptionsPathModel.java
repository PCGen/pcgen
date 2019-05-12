/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.dialog;

import pcgen.system.ConfigurationSettings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Used for for first load to determine where to store settings.
 */

class OptionsPathModel
{
	private final StringProperty selectedDirectory = new SimpleStringProperty(
			ConfigurationSettings.getSettingsDirFromFilePath(ConfigurationSettings.getDefaultSettingsFilesPath()));

	StringProperty getSelectedDirectory()
	{
		return selectedDirectory;
	}
}
