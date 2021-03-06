/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2014 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.plugin.editing.gwt.example.client.merge;

import org.geomajas.gwt.client.action.menu.ToggleSelectionAction;
import org.geomajas.gwt.client.controller.AbstractGraphicsController;
import org.geomajas.gwt.client.map.RenderSpace;
import org.geomajas.gwt.client.widget.MapWidget;

import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * Controller for selecting features that should be merged within the merging showcase.
 * 
 * @author Pieter De Graef
 */
public class MergeSelectionController extends AbstractGraphicsController {

	// -------------------------------------------------------------------------
	// Constructors:
	// -------------------------------------------------------------------------

	public MergeSelectionController(MapWidget mapWidget) {
		super(mapWidget);
	}

	// -------------------------------------------------------------------------
	// GraphicsController implementation:
	// -------------------------------------------------------------------------

	@Override
	public void onActivate() {
	}

	@Override
	public void onDeactivate() {
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		ToggleSelectionAction action = new ToggleSelectionAction(mapWidget, false, 0);
		action.toggle(getLocation(event, RenderSpace.SCREEN), false);
	}
}