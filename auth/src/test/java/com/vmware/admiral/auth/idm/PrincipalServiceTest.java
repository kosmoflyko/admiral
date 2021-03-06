/*
 * Copyright (c) 2017 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright notices
 * and license terms. Your use of these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package com.vmware.admiral.auth.idm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static com.vmware.admiral.auth.idm.PrincipalService.CRITERIA_QUERY;

import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vmware.admiral.auth.AuthBaseTest;
import com.vmware.admiral.auth.idm.PrincipalService.PrincipalResponse;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.test.TestContext;

public class PrincipalServiceTest extends AuthBaseTest {

    @Before
    public void setIndentity() throws GeneralSecurityException {
        host.assumeIdentity(buildUserServicePath(ADMIN_USERNAME));
    }

    @Test
    public void testGetPrincipalWithValidInput() {
        TestContext ctx = new TestContext(1, Duration.ofSeconds(10));
        final PrincipalResponse[] response = new PrincipalResponse[1];
        Operation get = Operation
                .createGet(host, PrincipalService.SELF_LINK + "/fritz@admiral.com")
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx.failIteration(ex);
                        return;
                    }
                    response[0] = o.getBody(PrincipalResponse.class);
                    ctx.completeIteration();
                });
        get.sendWith(host);
        ctx.await();
        assertNotNull(response[0]);
        assertEquals("fritz@admiral.com", response[0].principalId);

        TestContext ctx1 = new TestContext(1, Duration.ofSeconds(10));
        final PrincipalResponse[] response1 = new PrincipalResponse[1];
        get = Operation
                .createGet(host, PrincipalService.SELF_LINK + "/connie@admiral.com")
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx1.failIteration(ex);
                        return;
                    }
                    response1[0] = o.getBody(PrincipalResponse.class);
                    ctx1.completeIteration();
                });
        get.sendWith(host);
        ctx1.await();
        assertNotNull(response1[0]);
        assertEquals("connie@admiral.com", response1[0].principalId);
    }

    @Test
    public void testGetPrincipalWithInvalidInput() {
        // Test with empty principal id.
        TestContext ctx = new TestContext(1, Duration.ofSeconds(10));
        Operation get = Operation
                .createGet(host, PrincipalService.SELF_LINK)
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx.completeIteration();
                        return;
                    }
                    ctx.failIteration(new RuntimeException("Expected exception != null when "
                            + "searching for principal with empty id."));
                });
        get.sendWith(host);
        ctx.await();

        // Test with non present principal id.
        TestContext ctx1 = new TestContext(1, Duration.ofSeconds(10));
        get = Operation
                .createGet(host, PrincipalService.SELF_LINK)
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx1.completeIteration();
                        return;
                    }
                    ctx1.failIteration(new RuntimeException("Expected exception != null when "
                            + "searching for non present principal."));
                });
        get.sendWith(host);
        ctx1.await();
    }

    @Test
    public void testGetPrincipalsWithValidInput() {
        TestContext ctx = new TestContext(1, Duration.ofSeconds(10));
        final List<PrincipalResponse> response = new ArrayList<>();
        String criteria = "/?" + CRITERIA_QUERY + "=fritz";
        Operation get = Operation
                .createGet(host, PrincipalService.SELF_LINK + criteria)
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx.failIteration(ex);
                        return;
                    }
                    response.addAll(o.getBody(ArrayList.class));
                    ctx.completeIteration();
                });
        get.sendWith(host);
        ctx.await();
        assertEquals("fritz@admiral.com", response.get(0).principalId);

        TestContext ctx1 = new TestContext(1, Duration.ofSeconds(10));
        response.clear();
        criteria = "/?" + CRITERIA_QUERY + "=i";
        get = Operation
                .createGet(host, PrincipalService.SELF_LINK + criteria)
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx1.failIteration(ex);
                        return;
                    }
                    response.addAll(o.getBody(ArrayList.class));
                    ctx1.completeIteration();
                });
        get.sendWith(host);
        ctx1.await();

        for (PrincipalResponse resp : response) {
            assertTrue(resp.principalId.equals("fritz@admiral.com")
                    || resp.principalId.equals("connie@admiral.com")
                    || resp.principalId.equals("gloria@admiral.com"));

        }

    }

    @Test
    public void testGetPrincipalsWithInvalidInput() {
        TestContext ctx = new TestContext(1, Duration.ofSeconds(10));
        String criteria = "/?" + CRITERIA_QUERY + "=";
        Operation get = Operation
                .createGet(host, PrincipalService.SELF_LINK + criteria)
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx.completeIteration();
                        return;
                    }
                    ctx.failIteration(new RuntimeException("Expected exception != null when "
                            + "searching for principal with empty criteria."));
                });
        get.sendWith(host);
        ctx.await();

        TestContext ctx1 = new TestContext(1, Duration.ofSeconds(10));
        criteria = "/?" + CRITERIA_QUERY + "=scot";
        get = Operation
                .createGet(host, PrincipalService.SELF_LINK + criteria)
                .setReferer(host.getUri())
                .setCompletion((o, ex) -> {
                    if (ex != null) {
                        ctx1.completeIteration();
                        return;
                    }
                    ctx1.failIteration(new RuntimeException("Expected exception != null when "
                            + "searching for principal with criteria not matching anything."));
                });
        get.sendWith(host);
        ctx1.await();
    }

}
