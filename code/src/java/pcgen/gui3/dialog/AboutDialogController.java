/* * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import pcgen.gui2.tools.DesktopBrowserLauncher;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenPropBundle;
import pcgen.util.Logging;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;

/**
 * Controller for about panel.
 * Much of this logic can be removed and replaced with property references
 * if we build our property bundles correctly.
 */
public class AboutDialogController
{
	@FXML
	private TextArea licenseArea;
	@FXML
	private Button mailingList;
	@FXML
	private Button wwwSite;
	@FXML
	private TextArea monkeyList_code;
	@FXML
	private TextArea monkeyList_list;
	@FXML
	private TextArea monkeyList_test;
	@FXML
	private TextArea monkeyList_eng;
	@FXML
	private Text projectLead;
	@FXML
	private Text javaVersion;
	@FXML
	private TextArea librariesArea;
	@FXML
	private Text releaseDate;
	@FXML
	private Text pcgenVersion;

	private void initLicenseArea()
	{
		URL lgpl = getClass().getResource("LICENSE"); //$NON-NLS-1$

		if (lgpl != null)
		{
			try
			{
				licenseArea.setText(Files.readString(Paths.get(lgpl.getPath())));
			}
			catch (IOException ioe)
			{
				Logging.errorPrint("lgpl is not null but error occurred", ioe);
				licenseArea.setText(LanguageBundle.getString("in_abt_license_read_err1")); //$NON-NLS-1$
			}
		}
		else
		{
			Logging.errorPrint("lgpl is null");
			licenseArea.setText(LanguageBundle.getString("in_abt_license_read_err2")); //$NON-NLS-1$
		}
	}

	/**
	 * Utility method opens a textual URI in a browser
	 * @param uri String that can be converted to a URI
	 */
	private static void openUriInBrowser(String uri)
	{
		try
		{
			DesktopBrowserLauncher.viewInBrowser(URI.create(uri));
		} catch (IOException ioe)
		{
			Logging.errorPrint(LanguageBundle.getString("in_err_browser_err"), ioe); //$NON-NLS-1$
		}
	}

	private void initButtons()
	{
		this.mailingList.setText(PCGenPropBundle.getMailingList());
		this.wwwSite.setText(PCGenPropBundle.getWWWHome());
	}

	private void initMonkies()
	{
		monkeyList_code.setText(PCGenPropBundle.getCodeMonkeys());
		monkeyList_list.setText(PCGenPropBundle.getListMonkeys());
		monkeyList_test.setText(PCGenPropBundle.getTestMonkeys());
		monkeyList_eng.setText(PCGenPropBundle.getEngineeringMonkeys());
	}

	private void initText()
	{
		projectLead.setText(PCGenPropBundle.getHeadCodeMonkey());
		javaVersion
				.setText(String.format(
						"%s (%s)",
						System.getProperty("java.runtime.version"),
						System.getProperty("java.vm.vendor")
				));

		String s = LanguageBundle.getString("in_abt_lib_apache"); //$NON-NLS-1$
		s += LanguageBundle.getString("in_abt_lib_jdom"); //$NON-NLS-1$
		librariesArea.setText(s);

		String releaseDateStr = PCGenPropBundle.getReleaseDate();
		if (StringUtils.isNotBlank(PCGenPropBundle.getAutobuildDate()))
		{
			releaseDateStr = PCGenPropBundle.getAutobuildDate();
		}
		releaseDate.setText(releaseDateStr);

		String versionNum = PCGenPropBundle.getVersionNumber();
		if (StringUtils.isNotBlank(PCGenPropBundle.getAutobuildNumber()))
		{
			versionNum += " autobuild #" + PCGenPropBundle.getAutobuildNumber();
		}
		pcgenVersion.setText(versionNum);

	}

	@FXML
	void initialize()
	{
		initText();
		initMonkies();
		initButtons();
		initLicenseArea();
	}

	@FXML
	private void openInBrowser(final ActionEvent actionEvent)
	{
		Labeled source = (Button) actionEvent.getSource();
		openUriInBrowser(source.getText());
	}
}
