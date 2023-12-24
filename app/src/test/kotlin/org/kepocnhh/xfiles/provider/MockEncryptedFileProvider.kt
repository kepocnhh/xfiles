package org.kepocnhh.xfiles.provider

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.concurrent.atomic.AtomicReference

internal class MockEncryptedFileProvider(
    private val exists: MutableSet<String> = mutableSetOf(),
    private val inputs: Map<String, ByteArray> = emptyMap(),
    private val refs: Map<String, AtomicReference<ByteArray>> = emptyMap(),
) : EncryptedFileProvider {
    constructor(exists: Set<String>): this(
        exists = exists.toMutableSet(),
    )

    override fun exists(pathname: String): Boolean {
        return exists.contains(pathname)
    }

    override fun delete(pathname: String) {
        exists.remove(pathname)
    }

    override fun openInput(pathname: String): InputStream {
        val bytes = inputs[pathname]
            ?: refs[pathname]?.get()
            ?: error("No input by $pathname!")
        return ByteArrayInputStream(bytes)
    }

    override fun writeBytes(pathname: String, bytes: ByteArray) {
        // noop
    }
}
