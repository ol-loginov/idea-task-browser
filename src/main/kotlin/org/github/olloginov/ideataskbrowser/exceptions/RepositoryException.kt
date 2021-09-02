package org.github.olloginov.ideataskbrowser.exceptions

class RepositoryException(
	message: String,
	e: Throwable? = null
) : Exception(message, e)
