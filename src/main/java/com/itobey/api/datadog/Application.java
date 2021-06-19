package com.itobey.api.datadog;

import io.micronaut.runtime.Micronaut;

/**
 * The entrypoint.
 */
public class Application {

    /**
     * The entrypoint for Micronaut.
     *
     * @param args
     */
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
