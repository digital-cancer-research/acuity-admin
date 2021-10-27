/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.web.service;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShutdownService {
    private final List<Hook> hooks;

    public ShutdownService() {
        hooks = new ArrayList<Hook>();
        createShutdownHook();
    }

    private void createShutdownHook() {
        ShutdownDaemonHook shutdownDaemonHook = new ShutdownDaemonHook();
        Runtime.getRuntime().addShutdownHook(shutdownDaemonHook);
    }

    protected class ShutdownDaemonHook extends Thread {
        @Override
        public void run() {
            for (Hook hook: hooks) {
                 hook.shutdown();
            }
        }
    }

    public Hook createHook(Thread thread) {
        thread.setDaemon(true);
        Hook hook = new Hook(thread);
        hooks.add(hook);
        return hook;
    }
}
