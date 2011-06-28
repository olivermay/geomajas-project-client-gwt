/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.layer.wms.mvc;

import junit.framework.Assert;
import org.geomajas.layer.wms.WmsAuthentication;
import org.geomajas.layer.wms.WmsHttpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Test for {@link WmsController}.
 *
 * @author Joachim Van der Auwera
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/geomajas/spring/geomajasContext.xml", "/wmsContext.xml"})
public class WmsControllerTest {

	private static final String TEST_VALUE = "A test value";

	@Autowired
	private WmsController wmsController;

	@Test
	@DirtiesContext // changing the controller
	public void testDoSomething() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		wmsController.setHttpService(new MockHttpService());

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("SERVICE", "WMS");
		parameters.put("layers", "bluemarble");
		parameters.put("WIDTH", "512");
		parameters.put("HEIGHT", "512");
		parameters.put("bbox", "-52.01245495052001,-28.207099921352835,11.947593278789554,35.75294830795673");
		parameters.put("format", "image/jpeg");
		parameters.put("version", "1.1.1");
		parameters.put("srs", "EPSG:4326");
		parameters.put("styles", "");
		parameters.put("request", "GetMap");
		request.setParameters(parameters);
		request.setRequestURI("d/wms/proxyBlue/");
		request.setQueryString("SERVICE=WMS&layers=bluemarble&" +
				"WIDTH=512&HEIGHT=512&bbox=-52.01245495052001,-28.207099921352835,11.947593278789554," +
				"35.75294830795673&format=image/jpeg&version=1.1.1&srs=EPSG:4326&styles=&request=GetMap");
		request.setMethod("GET");
		wmsController.getWms(request, response);
		Assert.assertEquals(TEST_VALUE, new String(response.getContentAsByteArray(), "UTF-8"));
	}

	private class MockHttpService implements WmsHttpService {

		public String addCredentialsToUrl(String url, WmsAuthentication authentication) {
			return url;
		}

		public InputStream getStream(String url, WmsAuthentication authentication) throws IOException {
			return new ByteArrayInputStream(TEST_VALUE.getBytes("UTF-8"));
		}
	}
}
