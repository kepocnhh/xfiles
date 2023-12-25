package org.kepocnhh.xfiles.entity

import java.math.BigInteger
import java.security.spec.DSAParameterSpec

internal class MockDSAParameterSpec(
    p: BigInteger = BigInteger.valueOf(1),
    q: BigInteger = BigInteger.valueOf(2),
    g: BigInteger = BigInteger.valueOf(3),
) : DSAParameterSpec(p, q, g)
