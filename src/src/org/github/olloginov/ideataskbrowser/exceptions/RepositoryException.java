package org.github.olloginov.ideataskbrowser.exceptions;

public class RepositoryException extends Exception {
    public RepositoryException(String message) {
        super(message, null);
    }

    public RepositoryException(String message, Throwable e) {
        super(message, e);
    }
}
