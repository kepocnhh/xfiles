package org.kepocnhh.xfiles.provider

import java.io.FileInputStream
import java.io.FileOutputStream

internal interface EncryptedFileProvider {
    fun exists(pathname: String): Boolean
    fun delete(pathname: String)
    fun openOutput(pathname: String): FileOutputStream
    fun openInput(pathname: String): FileInputStream
}
