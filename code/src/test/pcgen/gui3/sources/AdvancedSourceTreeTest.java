/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;

import javafx.scene.control.TreeItem;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Advanced tab's available-tree construction
 * ({@link AdvancedSourceSelectionController#buildTree}): campaigns are grouped
 * Publisher → Setting → leaf, settings sort before setting-less leaves, groups
 * are alphabetised, and groups appear only when they contain campaigns (so a
 * filtered list yields no empty publisher/setting nodes).
 */
class AdvancedSourceTreeTest
{
	private static Campaign campaign(String name, String publisher, String setting)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		if (publisher != null)
		{
			campaign.put(StringKey.DATA_PRODUCER, publisher);
		}
		if (setting != null)
		{
			campaign.put(StringKey.CAMPAIGN_SETTING, setting);
		}
		return campaign;
	}

	private static String label(TreeItem<SourceTreeNode> item)
	{
		return item.getValue().displayLabel();
	}

	@Test
	void buildTree_should_haveNoChildren_when_empty()
	{
		var root = AdvancedSourceSelectionController.buildTree(List.of());
		assertEquals(0, root.getChildren().size());
	}

	@Test
	void buildTree_should_groupBySettingUnderPublisher_when_settingPresent()
	{
		var root = AdvancedSourceSelectionController.buildTree(
				List.of(campaign("Core Rulebook", "Paizo", "Pathfinder")));

		assertEquals(1, root.getChildren().size());
		var publisher = root.getChildren().get(0);
		assertInstanceOf(SourceTreeNode.Publisher.class, publisher.getValue());
		assertEquals("Paizo", label(publisher));

		assertEquals(1, publisher.getChildren().size());
		var setting = publisher.getChildren().get(0);
		assertInstanceOf(SourceTreeNode.Setting.class, setting.getValue());
		assertEquals("Pathfinder", label(setting));

		var leaf = setting.getChildren().get(0);
		assertInstanceOf(SourceTreeNode.Leaf.class, leaf.getValue());
		assertEquals("Core Rulebook", label(leaf));
	}

	@Test
	void buildTree_should_attachDirectly_when_noSetting()
	{
		var root = AdvancedSourceSelectionController.buildTree(
				List.of(campaign("Loose Supplement", "Paizo", null)));

		var publisher = root.getChildren().get(0);
		assertEquals(1, publisher.getChildren().size());
		var leaf = publisher.getChildren().get(0);
		assertInstanceOf(SourceTreeNode.Leaf.class, leaf.getValue());
		assertEquals("Loose Supplement", label(leaf));
	}

	@Test
	void buildTree_should_sortPublishersAlphabetically()
	{
		var root = AdvancedSourceSelectionController.buildTree(List.of(
				campaign("Z book", "Zorro Press", null),
				campaign("A book", "Acme Games", null)));

		assertEquals(List.of("Acme Games", "Zorro Press"),
				root.getChildren().stream().map(AdvancedSourceTreeTest::label).toList());
	}

	@Test
	void buildTree_should_orderSettingsBeforeSettinglessLeaves_withinPublisher()
	{
		var root = AdvancedSourceSelectionController.buildTree(List.of(
				campaign("Loose", "Paizo", null),
				campaign("Grouped", "Paizo", "Golarion")));

		var publisher = root.getChildren().get(0);
		var children = publisher.getChildren();
		// Setting node first, then the setting-less leaf.
		assertInstanceOf(SourceTreeNode.Setting.class, children.get(0).getValue());
		assertEquals("Golarion", label(children.get(0)));
		assertInstanceOf(SourceTreeNode.Leaf.class, children.get(1).getValue());
		assertEquals("Loose", label(children.get(1)));
	}

	@Test
	void buildTree_should_omitEmptyGroups_when_listFiltered()
	{
		// Simulates a filtered list: only one publisher's campaign survives, so
		// only that publisher node appears — no empty publisher/setting nodes.
		var root = AdvancedSourceSelectionController.buildTree(
				List.of(campaign("Survivor", "Paizo", "Pathfinder")));

		assertEquals(1, root.getChildren().size());
		assertEquals("Paizo", label(root.getChildren().get(0)));
	}

	@Test
	void buildTree_should_expandGroups_soMatchesAreVisible()
	{
		var root = AdvancedSourceSelectionController.buildTree(
				List.of(campaign("Core", "Paizo", "Pathfinder")));

		var publisher = root.getChildren().get(0);
		assertTrue(publisher.isExpanded());
		assertTrue(publisher.getChildren().get(0).isExpanded());
	}
}
