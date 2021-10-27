package com.acuity.visualisations.web.service;


public class Hook {
    private boolean keepRunning = true;
    private final Thread thread;

    public Hook(Thread thread) {
        this.thread = thread;
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void shutdown() {
        keepRunning = false;
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {

        }
    }

}
