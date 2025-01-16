package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

public class MicrosoftGraphException extends RuntimeException {

    public MicrosoftGraphException(final String message) {
        super(message);
    }

    public MicrosoftGraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
