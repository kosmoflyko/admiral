/*
 * Copyright (c) 2016 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright notices
 * and license terms. Your use of these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package com.vmware.admiral.request.compute.enhancer;

import java.net.URI;
import java.util.logging.Level;

import com.vmware.admiral.compute.profile.ProfileService;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService.ComputeDescription;
import com.vmware.xenon.common.DeferredResult;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.common.UriUtils;

/**
 * An base class to be extended by any ComputeDescription enhancer.
 */
public abstract class ComputeDescriptionEnhancer implements Enhancer<ComputeDescription> {

    protected DeferredResult<ProfileService.ProfileStateExpanded> getProfileState(ServiceHost host,
            URI referer, EnhanceContext context) {
        if (context.profile != null) {
            return DeferredResult.completed(context.profile);
        }
        host.log(Level.INFO, "Loading profile state for %s", context.profileLink);

        URI profileUri = UriUtils.buildUri(host, context.profileLink);
        return host.sendWithDeferredResult(
                Operation.createGet(ProfileService.ProfileStateExpanded.buildUri(profileUri)).setReferer(referer),
                ProfileService.ProfileStateExpanded.class);
    }
}
