package com.alexeymerov.radiostations.common

import timber.log.Timber

/**
 * Tree realization for Timber. Taken from somewhere a long time ago.
 *
 * New logger limits the tag column width with 35 chars, can't use method name.
 * */
class NumberedTimberTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "%s:%s",
            element.fileName,
            element.lineNumber
        )
    }

}
