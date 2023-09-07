package org.kepocnhh.xfiles.provider.security

import java.security.KeyPair

internal interface KeyFactoryProvider {
    fun generate(public: ByteArray, private: ByteArray): KeyPair
}
