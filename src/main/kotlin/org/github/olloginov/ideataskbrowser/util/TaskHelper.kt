package org.github.olloginov.ideataskbrowser.util

import com.intellij.tasks.Task
import com.intellij.tasks.gitlab.GitlabTask
import com.intellij.tasks.gitlab.model.GitlabIssue
import java.lang.reflect.InaccessibleObjectException
import java.util.Date

object TaskHelper {
    fun getChangeDate(task: Task): Date? {
        return max(task.created, task.updated)
    }

    private fun max(a: Date?, b: Date?): Date? {
        if (a == null || b == null) {
            return a ?: b
        }
        return if (a.after(b)) a else b
    }

    fun getPresentationTitle(task: Task): String {
        return if (task.isIssue) {
            task.presentableId + ": " + task.summary
        } else {
            task.summary
        }
    }

    fun getGitlabDescription(task: GitlabTask): String? {
        return try {
            val field = task.javaClass.getDeclaredField("myIssue")
            field.isAccessible = true
            val issue = field.get(task) as GitlabIssue
            issue.description
        } catch (e: NoSuchFieldException) {
            null
        } catch (e: InaccessibleObjectException) {
            null
        } catch (e: SecurityException) {
            null
        }
    }
}
