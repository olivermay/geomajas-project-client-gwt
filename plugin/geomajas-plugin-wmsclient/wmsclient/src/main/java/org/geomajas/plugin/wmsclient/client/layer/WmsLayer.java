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

package org.geomajas.plugin.wmsclient.client.layer;

import org.geomajas.annotation.Api;
import org.geomajas.puregwt.client.map.ViewPort;
import org.geomajas.puregwt.client.map.layer.HasMapScalesRenderer;
import org.geomajas.puregwt.client.map.layer.Layer;

/**
 * <p>
 * Base client-side WMS layer definition. Note that a WMS service can use either a raster data set or a vector data set,
 * we too make that distinction here. This layer definition does not support interaction with the WMS service. If you
 * need support for features or a GetFeatureInfo request, have a look at the {@link FeaturesSupportedWmsLayer}.
 * </p>
 * 
 * @author Pieter De Graef
 * @since 1.0.0
 */
@Api(allMethods = true)
public interface WmsLayer extends Layer, HasMapScalesRenderer {

	/**
	 * Get the main WMS options. These options are translated into HTTP GET parameters for the WMS calls.
	 * 
	 * @return Get the main WMS options object.
	 */
	WmsLayerConfiguration getConfig();

	/**
	 * 
	 * @return
	 */
	WmsTileConfiguration getTileConfig();

	/**
	 * Returns the view port CRS. This layer should always have the same CRS as the map!
	 * 
	 * @return The layer CRS (=map CRS).
	 */
	String getCrs();

	/**
	 * Get the view port for this layer.
	 * 
	 * @return The ViewPort, or null if the layer has not been added to a map.
	 */
	ViewPort getViewPort();
}