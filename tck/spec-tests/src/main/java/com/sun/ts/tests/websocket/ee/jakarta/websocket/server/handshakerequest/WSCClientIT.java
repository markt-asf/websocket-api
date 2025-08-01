/*
 * Copyright (c) 2013, 2025 Oracle and/or its affiliates and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.ts.tests.websocket.ee.jakarta.websocket.server.handshakerequest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.sun.ts.tests.websocket.common.client.WebSocketCommonClient;
import com.sun.ts.tests.websocket.common.impl.ClientConfigurator;
import com.sun.ts.tests.websocket.common.util.IOUtil;
import com.sun.ts.tests.websocket.common.util.StringUtil;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.HandshakeResponse;

/*
 * @class.setup_props: webServerHost;
 *                     webServerPort;
 *                     ws_wait;
 */
@ExtendWith(ArquillianExtension.class)
public class WSCClientIT extends WebSocketCommonClient {

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        WebArchive archive = ShrinkWrap.create(WebArchive.class, "wsc_ee_jakarta_websocket_handshakerequest_web.war");
        archive.addPackages(true, Filters.exclude(WSCClientIT.class),
                "com.sun.ts.tests.websocket.ee.jakarta.websocket.server.handshakerequest");
        archive.addPackages(true, "com.sun.ts.tests.websocket.common.stringbean");
        archive.addClasses(IOUtil.class);
        return archive;
    }

    public WSCClientIT() throws Exception {
        setContextRoot("wsc_ee_jakarta_websocket_handshakerequest_web");
    }

    static final String KEY = "aFirstKey";

    static final String[] HEADERS = { "header1", "header2", "header3", "header4", "header5", "header6", "header7",
            "header8" };

    /* Run test */

    /*
     * @testName: getHeadersIsReadonlyTest
     *
     * @assertion_ids: WebSocket:JAVADOC:174; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getHeaders HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Return the read only Map of Http Headers that came with the handshake
     * request.
     */
    @Test
    public void getHeadersIsReadonlyTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator() {
            @Override
            public void afterResponse(HandshakeResponse hr) {
                super.afterResponse(hr);
                boolean contains = hr.getHeaders().containsKey(ReadonlyGetHeadersConfigurator.KEY);
                assertTrue(!contains, "HandshakeRequest.getHeaders is not readonly, it contains [",
                        ReadonlyGetHeadersConfigurator.KEY, ",",
                        StringUtil.collectionToString(hr.getHeaders().get(ReadonlyGetHeadersConfigurator.KEY)), "]");
                logMsg("HandshakeRequest.getHeaders is readonly as expected");
            }
        };
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("readonlygetheaders", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getHeadersHasCaseInsensitiveNamesTest
     *
     * @assertion_ids: WebSocket:JAVADOC:174; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:16; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getHeaders HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ClientEndpointConfig.Configurator.beforeRequest
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * The header names are case insensitive.
     */
    @Test
    public void getHeadersHasCaseInsensitiveNamesTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToRequest(CaseInsensitiveHeaderNamesConfigurator.REQUEST_KEY,
                CaseInsensitiveHeaderNamesConfigurator.REQUEST_VALUES);
        configurator.addToResponse(CaseInsensitiveHeaderNamesConfigurator.RESPONSE_KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("caseinsensitiveheadernames", "anything", "anything");
        configurator.assertBeforeRequestHasBeenCalled();
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getParameterMapIsUnmodifiableTest
     *
     * @assertion_ids: WebSocket:JAVADOC:176; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getParameterMap HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Returns: the unmodifiable map of the request parameters.
     */
    @Test
    public void getParameterMapIsUnmodifiableTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator() {
            @Override
            public void afterResponse(HandshakeResponse hr) {
                super.afterResponse(hr);
                boolean contains = hr.getHeaders().containsKey(ReadonlyGetParamsConfigurator.KEY);
                assertTrue(!contains, "HandshakeRequest.getParameterMap is not readonly, it contains [",
                        ReadonlyGetParamsConfigurator.KEY, ",",
                        StringUtil.collectionToString(hr.getHeaders().get(ReadonlyGetParamsConfigurator.KEY)), "]");
                logMsg("HandshakeRequest.getParameterMap is readonly as expected");
            }
        };
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("readonlygetparams", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getParameterMapOneParamTest
     *
     * @assertion_ids: WebSocket:JAVADOC:176; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getParameterMap HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Returns: the unmodifiable map of the request parameters.
     */
    @Test
    public void getParameterMapOneParamTest() throws Exception {
        String param = "firstParam";
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse("first", param);
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        setProperty(Property.SEARCH_STRING, param);
        invoke("getoneparam/" + param, "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getParameterMapOneParamOneQueryParamTest
     *
     * @assertion_ids: WebSocket:JAVADOC:176; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getParameterMap HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Returns: the unmodifiable map of the request parameters.
     */
    @Test
    public void getParameterMapOneParamOneQueryParamTest() throws Exception {
        String param = "firstParam";
        String queryParam = "secondParam";
        String query = "?second=" + queryParam + "&second=" + param;
        ClientConfigurator configurator = new ClientConfigurator();
        // configurator.addToResponse("first", param);
        configurator.addToResponse("second", queryParam, param);
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        setProperty(Property.SEARCH_STRING, param);
        invoke("getoneparam/" + param + query, "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getParameterMapTwoParamsTest
     *
     * @assertion_ids: WebSocket:JAVADOC:176; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getParameterMap HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Returns: the unmodifiable map of the request parameters.
     */
    @Test
    public void getParameterMapTwoParamsTest() throws Exception {
        String[] params = { String.valueOf(123L), String.valueOf(123.456d) };
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse("first", params[0]);
        configurator.addToResponse("second", params[1]);
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        setProperty(Property.SEARCH_STRING, params[0]);
        setProperty(Property.SEARCH_STRING, params[1]);
        invoke("gettwoparams/" + params[0] + "/" + params[1], "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getQueryStringTest
     *
     * @assertion_ids: WebSocket:JAVADOC:177; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getQueryString HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Return the query string associated with the request.
     */
    @Test
    public void getQueryStringTest() throws Exception {
        String query = "abc=123&def=456";
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetQueryStringConfigurator.KEY, query);
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getquerystring?" + query, "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getRequestUriTest
     *
     * @assertion_ids: WebSocket:JAVADOC:178; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getRequestUri HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Returns: the request uri of the handshake request.
     */
    @Test
    public void getRequestUriTest() throws Exception {
        String uri = "getrequesturi";
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetRequestUriConfigurator.KEY, uri);
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke(uri, "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: getUserPrincipalNotAuthenticatedTest
     *
     * @assertion_ids: WebSocket:JAVADOC:179; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.getUserPrincipal
     * HandshakeResponse.getHeaders ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * Return the authenticated user or null if no user is authenticated for this
     * handshake.
     */
    @Test
    public void getUserPrincipalNotAuthenticatedTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetUserPrincipalNotAuthenticatedConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getuserprincipal", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    /*
     * @testName: isUserInRoleNotAuthenticatedTest
     *
     * @assertion_ids: WebSocket:JAVADOC:180; WebSocket:JAVADOC:77;
     * WebSocket:JAVADOC:15; WebSocket:JAVADOC:210;
     *
     * @test_Strategy: HandshakeRequest.isUserInRole HandshakeResponse.getHeaders
     * ClientEndpointConfig.Configurator.afterResponse
     * ServerEndpointConfig.Configurator.modifyHandshake
     *
     * If the user has not been authenticated, the method returns false.
     */
    @Test
    public void isUserInRoleNotAuthenticatedTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(IsUserInRoleNotAuthenticatedConfigurator.KEY, String.valueOf(false));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("isuserinrole", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getUserX509CertificateChainNotAuthenticatedTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetUserX509CertificateChainNotAuthenticatedConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getuserx509certificatechain", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getLocalAddressTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetLocalAddressConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getlocaladdress", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getLocalHostNameTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetLocalHostNameConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getlocalhostname", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getLocalPortTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetLocalPortConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getlocalport", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getRemoteAddressTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetRemoteAddressConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getremoteaddress", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getRemoteHostNameTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetRemoteHostNameConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getremotehostname", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getRemotePortTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetRemotePortConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getremoteport", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getLocaleNoHeaderTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToResponse(GetLocaleNoHeaderConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getlocalenoheader", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getLocaleFrenchTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToRequest(GetLocaleFrenchConfigurator.REQUEST_KEY,
                GetLocaleFrenchConfigurator.REQUEST_VALUES);
        configurator.addToResponse(GetLocaleFrenchConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getlocalefrench", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }

    @Test
    public void getLocaleGermanTest() throws Exception {
        ClientConfigurator configurator = new ClientConfigurator();
        configurator.addToRequest(GetLocaleGermanConfigurator.REQUEST_KEY,
                GetLocaleGermanConfigurator.REQUEST_VALUES);
        configurator.addToResponse(GetLocaleGermanConfigurator.KEY, String.valueOf(true));
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().configurator(configurator).build();
        setClientEndpointConfig(config);
        invoke("getlocalegerman", "anything", "anything");
        configurator.assertAfterResponseHasBeenCalled();
    }
}
