package org.kepocnhh.xfiles.module.router

import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel

// todo
internal class RouterViewModel(private val injection: Injection): AbstractViewModel() {
    private val logger = injection.loggers.newLogger("[Router|VM]")
}
