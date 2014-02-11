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
package org.geomajas.plugin.rasterizing.client;

import org.geomajas.annotation.Api;

/**
 * Constants with the ids for the rasterizing actions.
 * 
 * @author Jan De Moerloose
 * @since 1.0.0
 */
@Api(allMethods = true)
public interface RasterizingToolId {

	/** Tool id for the {@link org.geomajas.plugin.rasterizing.client.action.toolbar.GetLegendImageAction} action. */
	String GET_LEGEND_IMAGE = "GetLegendImage";

	/** Tool id for the {@link org.geomajas.plugin.rasterizing.client.action.toolbar.GetMapImageAction} action. */
	String GET_MAP_IMAGE = "GetMapImage";

}