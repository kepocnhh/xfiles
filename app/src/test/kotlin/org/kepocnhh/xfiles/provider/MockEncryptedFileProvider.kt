package org.kepocnhh.xfiles.provider

import java.util.concurrent.atomic.AtomicReference

internal class MockEncryptedFileProvider(
    private val exists: MutableSet<String> = mutableSetOf(),
    private val inputs: MutableMap<String, ByteArray> = mutableMapOf(),
    private val refs: Map<String, AtomicReference<ByteArray>> = emptyMap(),
) : EncryptedFileProvider {
    constructor(
        exists: Set<String>,
        inputs: Map<String, ByteArray>,
    ) : this(
        exists = exists.toMutableSet(),
        inputs = inputs.toMutableMap(),
    )

    constructor(exists: Set<String>) : this(
        exists = exists.toMutableSet(),
    )

    constructor(inputs: Map<String, ByteArray>) : this(
        inputs = inputs.toMutableMap(),
    )

    constructor(
        inputs: Map<String, ByteArray>,
        refs: Map<String, AtomicReference<ByteArray>>,
    ) : this(
        inputs = inputs.toMutableMap(),
        refs = refs,
    )

    override fun exists(pathname: String): Boolean {
        return exists.contains(pathname)
    }

    @Suppress("IgnoredReturnValue")
    override fun delete(pathname: String) {
        exists.remove(pathname)
    }

    override fun readBytes(pathname: String): ByteArray {
        return inputs[pathname]
            ?: refs[pathname]?.get()
            ?: error("No input by $pathname!")
    }

    override fun writeBytes(pathname: String, bytes: ByteArray) {
        if (bytes.isNotEmpty()) {
            exists.add(pathname)
            inputs[pathname] = bytes
        }
    }
}
