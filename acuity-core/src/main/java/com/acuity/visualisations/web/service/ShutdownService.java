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
