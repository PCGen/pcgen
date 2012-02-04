/*
 * Gui2CampaignInfoFactory.java
 * Copyright James Dempsey, 2011
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
 * Created on 08/03/2011 7:18:51 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.content.CampaignURL.URLKind;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Status;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.CampaignInfoFactory;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * The Class <code>Gui2CampaignInfoFactory</code> is responsible for producing 
 * HTML formatted information on campaigns for the new user interface.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class Gui2CampaignInfoFactory implements CampaignInfoFactory
{

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CampaignInfoFactory#getHTMLInfo(pcgen.core.facade.CampaignFacade)
	 */
	public String getHTMLInfo(CampaignFacade campaign)
	{
		if (campaign == null || !(campaign instanceof Campaign))
		{
			return "";
		}
		Campaign aCamp = (Campaign) campaign;
		
		final HtmlInfoBuilder infoText = new HtmlInfoBuilder(aCamp.getDisplayName());
		infoText.appendLineBreak();
		if (aCamp.getSizeOfListFor(ListKey.FILE_COVER) > 0)
		{
			CampaignSourceEntry image = aCamp.getSafeListFor(ListKey.FILE_COVER).get(0);
			infoText.appendIconElement(image.getURI().toString());
		}
		if (aCamp.getSizeOfListFor(ListKey.FILE_LOGO) > 0)
		{
			CampaignSourceEntry image = aCamp.getSafeListFor(ListKey.FILE_LOGO).get(0);
			infoText.appendIconElement(image.getURI().toString());
		}
		if (aCamp.getSizeOfListFor(ListKey.FILE_COVER) > 0 || aCamp.getSizeOfListFor(ListKey.FILE_LOGO) > 0)
		{
			infoText.appendLineBreak();
		}


		String bString = SourceFormat.getFormattedString(aCamp,
		Globals.getSourceDisplay(), true);
		if (bString.length() == 0)
		{
			bString = SourceFormat.getFormattedString(aCamp,
					SourceFormat.LONG, true);
		}
		infoText.appendElement("SOURCE", bString);
		infoText.append(" <b>by</b> ");
		infoText.append(aCamp.getSafe(StringKey.PUB_NAME_LONG));
		infoText.appendLineBreak();

		// Add the data set release status
		Status status = aCamp.getSafe(ObjectKey.STATUS);
		infoText.appendElement("STATUS", "<font color=\"#" + Integer.toHexString(status.getColor()) + "\">" + status + "</font>");
		infoText.appendLineBreak();
			
		String descr = aCamp.get(StringKey.DESCRIPTION);
		if (descr != null)
		{
			infoText.appendElement("DESCRIPTION", descr);
			infoText.appendLineBreak();
		}
		// Add the website URLs
		List<CampaignURL> webURLs = getUrlListForKind(aCamp, URLKind.WEBSITE);
		if (!webURLs.isEmpty())
		{
			infoText.appendElement("WEBSITE", buildURLListString(webURLs));
			infoText.appendLineBreak();
		}
		
		if (aCamp.getType().length() > 0)
		{
			infoText.appendElement("TYPE", aCamp.getType());
			infoText.appendSpacer();
		}

		infoText.appendElement("RANK", String.valueOf(aCamp
			.getSafe(IntegerKey.CAMPAIGN_RANK)));
		if (StringUtil.join(aCamp.getSafeListFor(ListKey.GAME_MODE), ", ").length() > 0)
		{
			infoText.appendSpacer();
			infoText.appendElement("GAME MODE", StringUtil.join(aCamp.getSafeListFor(ListKey.GAME_MODE), ", "));
		}
		infoText.appendLineBreak();

		// Add the purchase URLs
		List<CampaignURL> purchaseURLs = getUrlListForKind(aCamp, URLKind.PURCHASE);
		if (!purchaseURLs.isEmpty())
		{
			infoText.appendElement("PURCHASE", buildURLListString(purchaseURLs));
			infoText.appendLineBreak();
		}

		// Add the survey URLs
		List<CampaignURL> surveyURLs = getUrlListForKind(aCamp, URLKind.SURVEY);
		if (!surveyURLs.isEmpty())
		{
			infoText.appendElement("SURVEY", buildURLListString(surveyURLs));
			infoText.appendLineBreak();
		}

		String preString = PrerequisiteUtilities.preReqHTMLStringsForList(null,
				null, aCamp.getPrerequisiteList(), false);
		if (preString.length() > 0)
		{
			infoText.appendI18nFormattedElement("in_InfoRequirements", preString); //$NON-NLS-1$
		}
		
		boolean infoDisplayed = false;
		List<String> info = aCamp.getListFor(ListKey.INFO_TEXT);
		if (info != null)
		{
			if (!infoDisplayed)
			{
				infoText.appendLineBreak();
			}

			infoText.appendSmallTitleElement("INFORMATION:");
			infoText.appendLineBreak();
			for (String infotext : info)
			{
				infoText.append(infotext);
				infoText.appendLineBreak();
			}
			infoDisplayed = true;
		}

		List<String> copyright = aCamp.getListFor(ListKey.SECTION_15);
		if (copyright != null)
		{
			if (!infoDisplayed)
			{
				infoText.appendLineBreak();
			}

			infoText.appendSmallTitleElement("COPYRIGHT:");
			infoText.appendLineBreak();
			for (String license : copyright)
			{
				infoText.append(license);
				infoText.appendLineBreak();
			}
		}

		return infoText.toString();
	}

	/**
	 * Builds a html display string based on the list of campaign urls.
	 * 
	 * @param urlList the list of urls
	 * 
	 * @return the display string
	 */
	private static String buildURLListString(List<CampaignURL> urlList)
	{
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (CampaignURL campaignURL : urlList)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(" | ");
			}
			sb.append("<a href=\"").append(campaignURL.getUri().toString());
			sb.append("\">").append(campaignURL.getUrlDesc());
			sb.append("</a>");
		}
		return sb.toString();
	}
	
	public static List<CampaignURL> getUrlListForKind(Campaign c, URLKind kind)
	{
		List<CampaignURL> kindList = new ArrayList<CampaignURL>();
		for (CampaignURL url : c.getSafeListFor(ListKey.CAMPAIGN_URL))
		{
			if (url.getUrlKind() == kind)
			{
				kindList.add(url);
			}
		}
		return kindList;
	}

}
