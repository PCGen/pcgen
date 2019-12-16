/*
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
 */
package pcgen.gui2.facade;

import java.net.URI;
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
import pcgen.cdom.helper.AllowUtilities;
import pcgen.core.Campaign;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.facade.core.CampaignInfoFactory;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.system.LanguageBundle;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code Gui2CampaignInfoFactory} is responsible for producing
 * HTML formatted information on campaigns for the new user interface.
 *
 * 
 */
public class Gui2CampaignInfoFactory implements CampaignInfoFactory
{

	@Override
	public String getHTMLInfo(Campaign campaign, List<Campaign> testList)
	{
		PersistenceManager pman = PersistenceManager.getInstance();
		List<URI> oldList = setSourcesForPrereqTesting(testList, pman);
		String htmlInfo = getHTMLInfo(campaign);
		pman.setChosenCampaignSourcefiles(oldList);
		return htmlInfo;
	}

	private List<URI> setSourcesForPrereqTesting(List<Campaign> testList, PersistenceManager pman)
	{
		List<URI> oldList = pman.getChosenCampaignSourcefiles();
		List<URI> uris = new ArrayList<>();
		for (Campaign campaign : testList)
		{
			uris.add(campaign.getSourceURI());
		}
		pman.setChosenCampaignSourcefiles(uris);
		return oldList;
	}

	@Override
	public String getHTMLInfo(Campaign campaign)
	{
		if (campaign == null)
		{
			return "";
		}
		final HtmlInfoBuilder infoText = new HtmlInfoBuilder(campaign.getDisplayName());
		appendCampaignInfo(campaign, infoText);

		return infoText.toString();
	}

	private void appendCampaignInfo(Campaign aCamp, final HtmlInfoBuilder infoText)
	{
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
		String bString = SourceFormat.getFormattedString(aCamp, SourceFormat.MEDIUM, true);
		if (StringUtils.isEmpty(bString))
		{
			bString = SourceFormat.getFormattedString(aCamp, SourceFormat.LONG, true);
		}
		infoText.appendI18nElement("in_sumSource", bString); //$NON-NLS-1$
		infoText.appendI18nFormattedElement("in_infByPub", //$NON-NLS-1$
			aCamp.getSafe(StringKey.PUB_NAME_LONG));
		infoText.appendLineBreak();
		// Add the data set release status
		Status status = aCamp.getSafe(ObjectKey.STATUS);
		infoText.appendI18nElement(
				"in_infStatus", //$NON-NLS-1$
				String.format("<font color=\"#%s\">%s</font>", ColorUtilty.colorToRGBString(
						status.getColor()), status)
		).appendLineBreak();
		String descr = aCamp.get(StringKey.DESCRIPTION);
		if (descr != null)
		{
			infoText.appendI18nElement("in_infDesc", descr); //$NON-NLS-1$
			infoText.appendLineBreak();
		}
		// Add the website URLs
		List<CampaignURL> webURLs = getUrlListForKind(aCamp, URLKind.WEBSITE);
		if (!webURLs.isEmpty())
		{
			infoText.appendI18nElement("in_infWebsite", buildURLListString(webURLs)); //$NON-NLS-1$
			infoText.appendLineBreak();
		}
		if (!aCamp.getType().isEmpty())
		{
			infoText.appendI18nElement("in_infType", aCamp.getType()); //$NON-NLS-1$
			infoText.appendSpacer();
		}
		infoText.appendI18nElement("in_infRank", String.valueOf(aCamp //$NON-NLS-1$
			.getSafe(IntegerKey.CAMPAIGN_RANK)));
		if (!StringUtil.join(aCamp.getSafeListFor(ListKey.GAME_MODE), ", ").isEmpty())
		{
			infoText.appendSpacer();
			infoText.appendI18nElement("in_infGame", //$NON-NLS-1$
				StringUtil.join(aCamp.getSafeListFor(ListKey.GAME_MODE), ", "));
		}
		infoText.appendLineBreak();
		// Add the purchase URLs
		List<CampaignURL> purchaseURLs = getUrlListForKind(aCamp, URLKind.PURCHASE);
		if (!purchaseURLs.isEmpty())
		{
			infoText.appendI18nElement("in_infPurchase", buildURLListString(purchaseURLs)); //$NON-NLS-1$
			infoText.appendLineBreak();
		}
		// Add the survey URLs
		List<CampaignURL> surveyURLs = getUrlListForKind(aCamp, URLKind.SURVEY);
		if (!surveyURLs.isEmpty())
		{
			infoText.appendI18nElement("in_infSurvey", buildURLListString(surveyURLs)); //$NON-NLS-1$
			infoText.appendLineBreak();
		}
		String preString =
				PrerequisiteUtilities.preReqHTMLStringsForList(null, null, aCamp.getPrerequisiteList(), false);
		if (!preString.isEmpty())
		{
			infoText.appendI18nFormattedElement("in_InfoRequirements", preString); //$NON-NLS-1$
		}
		String aString = AllowUtilities.getAllowInfo(null, aCamp);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}
		boolean infoDisplayed = false;
		List<String> info = aCamp.getListFor(ListKey.INFO_TEXT);
		if (info != null)
		{
            infoText.appendLineBreak();
            infoText.appendSmallTitleElement(LanguageBundle.getString("in_infInf")); //$NON-NLS-1$
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
			infoText.appendSmallTitleElement(LanguageBundle.getString("in_infCopyright")); //$NON-NLS-1$
			infoText.appendLineBreak();
			for (String license : copyright)
			{
				infoText.append(license);
				infoText.appendLineBreak();
			}
		}
		List<Campaign> subCampaigns = aCamp.getSubCampaigns();
		List<CampaignSourceEntry> notFoundSubCampaigns = aCamp.getNotFoundSubCampaigns();
		if (subCampaigns != null && notFoundSubCampaigns != null
			&& (!subCampaigns.isEmpty() || !notFoundSubCampaigns.isEmpty()))
		{
			infoText.appendLineBreak();
			infoText.appendSmallTitleElement(LanguageBundle.getString("in_infIncludedCampaigns")); //$NON-NLS-1$
			infoText.appendLineBreak();
			for (Campaign subCamp : subCampaigns)
			{
				infoText.append(subCamp.getDisplayName());
				infoText.appendLineBreak();
			}
			for (CampaignSourceEntry subCse : notFoundSubCampaigns)
			{
				infoText.append(LanguageBundle.getFormattedString("in_infMissingCampaign", subCse.getURI()));
				infoText.appendLineBreak();
			}
		}
		infoText.appendLineBreak();
		infoText.appendI18nElement("in_infPccPath", aCamp.getSourceURI().getPath());
	}

	@Override
	public String getHTMLInfo(SourceSelectionFacade selection)
	{
		if (selection.getCampaigns().getSize() == 1)
		{
			return getHTMLInfo(selection.getCampaigns().getElementAt(0));
		}

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder(selection.toString());
		for (Campaign campaign : selection.getCampaigns())
		{
			if (campaign == null)
			{
				continue;
			}
			infoText.appendLineBreak();
			infoText.appendLineBreak();
			infoText.appendTitleElement(campaign.getDisplayName());
			appendCampaignInfo(campaign, infoText);
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
		StringBuilder sb = new StringBuilder(250);
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
		List<CampaignURL> kindList = new ArrayList<>();
		for (CampaignURL url : c.getSafeListFor(ListKey.CAMPAIGN_URL))
		{
			if (url.getUrlKind() == kind)
			{
				kindList.add(url);
			}
		}
		return kindList;
	}

	@Override
	public String getRequirementsHTMLString(Campaign campaign, List<Campaign> testList)
	{
		if (campaign == null)
		{
			return "";
		}
		PersistenceManager pman = PersistenceManager.getInstance();
		List<URI> oldList = setSourcesForPrereqTesting(testList, pman);
		pman.setChosenCampaignSourcefiles(oldList);

		String sb = PrerequisiteUtilities.preReqHTMLStringsForList(null, null, campaign.getPrerequisiteList(), false)
				+ AllowUtilities.getAllowInfo(null, campaign);
		return sb;
	}

}
