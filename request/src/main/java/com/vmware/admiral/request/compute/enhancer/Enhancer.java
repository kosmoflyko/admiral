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

import java.util.Map;

import com.vmware.admiral.compute.profile.ProfileService.ProfileStateExpanded;
import com.vmware.photon.controller.model.resources.ResourceState;
import com.vmware.xenon.common.DeferredResult;

/**
 * Enhancer is used to extend/adapt/enhance a specific Resource.
 */
public interface Enhancer<T extends ResourceState> {

    /**
     * Enhance resource T. The callback will be invoke with the enhanced resource, in case of an
     * error the second parameter in the callback will be not null.
     */
    DeferredResult<T> enhance(EnhanceContext context, T resource);

    static class EnhanceContext {
        public String profileLink;
        // Lazy loaded using the profileLink
        public ProfileStateExpanded profile;
        public String endpointLink;
        public String resourcePoolLink;
        public String regionId;
        public String zoneId;
        public String endpointType;

        /**
         * Optional image reference as configured by the profile.
         * Set by {@link ComputeDescriptionImageEnhancer}.
         */
        public String resolvedImage;

        /**
         * Optional link of the ImageState as configured by the profile.
         * Set by {@link ComputeDescriptionImageEnhancer}.
         */
        public String resolvedImageLink;

        public Map<String, Object> content;
        public boolean skipNetwork;


    }
}
