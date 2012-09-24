/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.plugin.deskmanager.command.common;

import org.geomajas.command.Command;
import org.geomajas.command.dto.GetConfigurationRequest;
import org.geomajas.command.dto.GetConfigurationResponse;
import org.geomajas.configuration.client.ClientApplicationInfo;
import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.plugin.deskmanager.domain.Geodesk;
import org.geomajas.plugin.deskmanager.service.common.GeodeskConfigurationService;
import org.geomajas.plugin.deskmanager.service.common.GeodeskIdService;
import org.geomajas.plugin.deskmanager.service.common.GeodeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Override van het {@link org.geomajas.command.configuration.GetConfigurationCommand} van geomajas.
 * 
 * @author Oliver May
 * @author Kristof Heirwegh
 * 
 */
public class GetConfigurationCommand implements Command<GetConfigurationRequest, GetConfigurationResponse> {

	@Autowired
	private GeodeskIdService geodeskIdService;

	@Autowired
	private GeodeskService geodeskService;

	@Autowired
	private GeodeskConfigurationService configurationService;

	public GetConfigurationResponse getEmptyCommandResponse() {
		return new GetConfigurationResponse();
	}

	@Transactional(readOnly = true)
	public void execute(GetConfigurationRequest request, GetConfigurationResponse response) throws Exception {
		// TODO: is this needed for magdageo?
		if (null == request.getApplicationId()) {
			throw new GeomajasException(ExceptionCode.PARAMETER_MISSING, "applicationId");
		}

		String id = geodeskIdService.getGeodeskIdentifier();
		Geodesk loket = geodeskService.getGeodeskByPublicId(id); // this checks if loket is allowed

		if (loket != null) {
			ClientApplicationInfo loketConfig = configurationService.createClonedGeodeskConfiguration(loket, true);
			response.setApplication(loketConfig);
		} else {
			throw new GeomajasException(ExceptionCode.APPLICATION_NOT_FOUND, request.getApplicationId());
		}
	}

}