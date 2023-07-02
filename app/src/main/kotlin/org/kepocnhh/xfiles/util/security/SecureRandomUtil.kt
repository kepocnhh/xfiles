package org.kepocnhh.xfiles.util.security

import android.os.Build
import java.security.SecureRandom

internal fun getSecureRandom(): SecureRandom {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        SecureRandom.getInstanceStrong()
    } else {
        SecureRandom.getInstance("SHA1PRNG")
    }
}
