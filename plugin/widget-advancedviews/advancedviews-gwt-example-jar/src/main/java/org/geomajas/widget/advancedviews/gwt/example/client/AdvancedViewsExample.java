/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2013 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.widget.advancedviews.gwt.example.client;

import com.google.gwt.core.client.GWT;

import com.google.gwt.core.client.EntryPoint;
import org.geomajas.gwt.example.base.SampleTreeNode;
import org.geomajas.gwt.example.base.SampleTreeNodeRegistry;
import org.geomajas.widget.advancedviews.gwt.example.client.i18n.ApplicationMessages;

/**
 * Entry point and main class for GWT application. This class defines the layout and functionality of this application.
 *
 * @author Oliver May
 */
public class AdvancedViewsExample implements EntryPoint {

	private static final ApplicationMessages MESSAGES = GWT.create(ApplicationMessages.class);

	public void onModuleLoad() {

		SampleTreeNodeRegistry.addSampleTreeNode(new SampleTreeNode("Advanced Views Widgets",
				"[ISOMORPHIC]/geomajas/silk/plugin.png", "Advanced Views Widgets", "Plugins"));
		SampleTreeNodeRegistry.addSampleTreeNode(new SampleTreeNode(MESSAGES.applicationTitle("Advanced views"),
				"[ISOMORPHIC]/geomajas/osgeo/layer-raster.png", AdvancedViewsPanel.TITLE, "Advanced Views Widgets",
				AdvancedViewsPanel.FACTORY));
		SampleTreeNodeRegistry.addSampleTreeNode(new SampleTreeNode(MESSAGES.applicationTitle("Advanced views " +
				"editor"),
				"[ISOMORPHIC]/geomajas/osgeo/layer-raster.png", AdvancedViewsEditorPanel.TITLE, "Advanced Views Widgets",
				AdvancedViewsEditorPanel.FACTORY));
	}
}
