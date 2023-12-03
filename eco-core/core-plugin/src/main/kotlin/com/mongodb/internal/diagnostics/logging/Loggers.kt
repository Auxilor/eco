package com.mongodb.internal.diagnostics.logging

/*
This is a terrible fix for mongo logging.

I've tried every 'solution' on the internet - setting the level with java native logging,
with Log4j / Slf4j, reflectively changing the logger delegate in the Log4J impl, every
single method under the sun - but I just couldn't get any of them to work.

So, I've 'fixed' the problem at the source - the class in the jar now always returns a useless
logger that can't do anything. At least there's no console spam anymore.
 */

@Suppress("UNUSED")
object Loggers {
    private const val PREFIX = "org.mongodb.driver"

    @JvmStatic
    fun getLogger(suffix: String): Logger = NoOpLogger("$PREFIX.$suffix")
}
