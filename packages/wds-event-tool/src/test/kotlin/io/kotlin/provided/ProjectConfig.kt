package io.kotlin.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.extensions.TestListener

// https://github.com/kotlintest/kotlintest/blob/master/doc/reference.md#executing-code-before-and-after-a-whole-project
object ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = emptyList<TestListener>()
}