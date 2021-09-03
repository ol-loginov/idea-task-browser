package org.github.olloginov.ideataskbrowser.util

import com.intellij.util.ui.UIUtil

fun invokeAndWait(block: () -> Unit) {
    UIUtil.invokeAndWaitIfNeeded(Runnable { block() })
}
