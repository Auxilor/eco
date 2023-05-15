package com.willfp.eco.internal.logging

import java.util.logging.LogRecord
import java.util.logging.Logger

object NOOPLogger : Logger("eco_noop", null as String?) {
    override fun log(record: LogRecord?) {
        return
    }
}
