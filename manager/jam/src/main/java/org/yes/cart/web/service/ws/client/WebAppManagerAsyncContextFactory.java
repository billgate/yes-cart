/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.service.ws.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.domain.ManagerService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12/08/2016
 * Time: 21:53
 */
public class WebAppManagerAsyncContextFactory implements AsyncContextFactory {

    private final ManagerService managerService;

    public WebAppManagerAsyncContextFactory(final ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncContext getInstance() {
        return getInstance(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncContext getInstance(Map<String, Object> attributes) {

        final Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;

        Manager manager = null;
        if (auth != null && auth.isAuthenticated()) {
            final List<Manager> managers = managerService.findByEmail(auth.getName());
            if (!managers.isEmpty()) {
                for (final Manager mgr : managers) {
                    if (auth.getName().equals(mgr.getEmail())) {
                        manager = mgr;
                    }
                }
            }
        }

        return new AsyncWebAppContextImpl(manager, attributes);
    }

    /**
     * This context uses ThreadLocal context from FlexContext. Ensure that it is instantiated
     * when FlexContext exists.
     *
     * User: denispavlov
     * Date: 12-11-09
     * Time: 8:36 AM
     */
    public static class AsyncWebAppContextImpl implements AsyncContext {

        private final Map<String, Object> attributes = new HashMap<String, Object>();

        public AsyncWebAppContextImpl(Manager manager, Map<String, Object> attributes) {

            if (manager == null || !manager.getEnabled()) {
                throw new IllegalStateException("WebApp context created outside of authenticated environment");
            }

            if (attributes != null) {
                this.attributes.putAll(attributes);
            }
            this.attributes.put(USERNAME, manager.getEmail());
            this.attributes.put(CREDENTIALS, null);
            this.attributes.put(CREDENTIALS_HASH, manager.getPassword());
            this.attributes.put(SECURITY_CTX, SecurityContextHolder.getContext());
        }

        /** {@inheritDoc} */
        public Map<String, Object> getAttributes() {
            return Collections.unmodifiableMap(this.attributes);
        }

        /** {@inheritDoc} */
        public <T> T getAttribute(final String name) {
            if (this.attributes.containsKey(name)) {
                return (T) this.attributes.get(name);
            }
            return null;
        }
    }
}
