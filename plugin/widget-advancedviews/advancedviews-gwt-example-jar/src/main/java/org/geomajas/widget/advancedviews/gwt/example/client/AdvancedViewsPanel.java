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

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.widgets.Canvas;
import org.geomajas.gwt.client.Geomajas;
import org.geomajas.gwt.client.map.event.MapModelEvent;
import org.geomajas.gwt.client.map.event.MapModelHandler;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.gwt.client.widget.LayerTree;
import org.geomajas.gwt.client.widget.Legend;
import org.geomajas.gwt.client.widget.LoadingScreen;
import org.geomajas.gwt.client.widget.LocaleSelect;
import org.geomajas.gwt.client.widget.MapWidget;
import org.geomajas.gwt.client.widget.OverviewMap;
import org.geomajas.gwt.client.widget.Toolbar;
import org.geomajas.gwt.example.base.SamplePanel;
import org.geomajas.gwt.example.base.SamplePanelFactory;
import org.geomajas.widget.advancedviews.client.widget.ExpandingThemeWidget;
import org.geomajas.widget.advancedviews.client.widget.ThemeWidget;
import org.geomajas.widget.advancedviews.gwt.example.client.i18n.ApplicationMessages;
import org.geomajas.widget.advancedviews.gwt.example.client.pages.AbstractTab;
import org.geomajas.widget.advancedviews.gwt.example.client.pages.FeatureListGridPage;
import org.geomajas.widget.advancedviews.gwt.example.client.pages.SearchPage;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * Sample to demonstrate use of the advancedviews plug-in.
 *
 * @author Oliver May
 */
public class AdvancedViewsPanel extends SamplePanel {

	public static final ApplicationMessages MESSAGES = GWT.create(ApplicationMessages.class);

	private ThemeWidget themes;
	
	private ExpandingThemeWidget exthemes;

	public static final SamplePanelFactory FACTORY = new SamplePanelFactory() {
		public SamplePanel createPanel() {
			return new AdvancedViewsPanel();
		}
	};

	public AdvancedViewsPanel () {

	}


	public static final String TITLE = "Advancedviews plug-in";

	public Canvas getViewPanel() {

		HLayout layout = new HLayout();
		layout.setWidth100();
		layout.setHeight100();
		layout.setMembersMargin(5);
		layout.setMargin(5);
		layout.setShowEdges(true);

		// ---------------------------------------------------------------------
		// Create the left-side (map and tabs):
		// ---------------------------------------------------------------------
		final MapWidget map = new MapWidget("mapMain", "advancedViewsApp");
		final Toolbar toolbar = new Toolbar(map);
		toolbar.setButtonSize(Toolbar.BUTTON_SIZE_BIG);

		layout.addMember(map);

		// ---------------------------------------------------------------------
		// Theme
		// ---------------------------------------------------------------------
		themes = new ThemeWidget(map);
		themes.setParentElement(map);
		themes.setSnapTo("BL");
		themes.setSnapOffsetTop(-50);
		themes.setSnapOffsetLeft(10);
		themes.setWidth(150);

		exthemes = new ExpandingThemeWidget(map);
		exthemes.setParentElement(map);
		exthemes.setSnapTo("BR");
		exthemes.setSnapOffsetTop(-50);
		exthemes.setSnapOffsetLeft(-20);
		exthemes.setWidth(160);

		return layout;
	}

	@Override
	public String getDescription() {
		return MESSAGES.applicationTitle("");
	}

	@Override
	public String[] getConfigurationFiles() {
		return new String[]{
				"classpath:org/geomajas/widget/advancedviews/gwt/example/context/applicationContext.xml",
				"classpath:org/geomajas/widget/advancedviews/gwt/example/context/mapMain.xml",
				"classpath:org/geomajas/widget/advancedviews/gwt/example/context/themesInfo.xml"};
	}
}
