package org.github.olloginov.ideataskbrowser.model

import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.Transient
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.Icon

@Suppress("unused")
@Tag("search")
class TaskSearch {
	private var uid = UUID.randomUUID().toString()
	private var repository: String = ""
	private var query: String = ""
	private var icon: Icon? = null
	private val updating = AtomicBoolean(false)

	@Tag("uid")
	fun getUid(): String = uid

	fun setUid(uid: String?) {
		this.uid = uid ?: this.uid
	}

	@Tag("repository")
	fun getRepository(): String = repository

	fun setRepository(repository: String?) {
		this.repository = repository ?: ""
	}

	@Tag("query")
	fun getQuery(): String = query

	fun setQuery(query: String?) {
		this.query = query ?: ""
	}

	@Transient
	fun getUpdating(): AtomicBoolean = updating

	@Transient
	fun getIcon(): Icon? = icon

	fun setIcon(icon: Icon?) {
		this.icon = icon
	}
}
