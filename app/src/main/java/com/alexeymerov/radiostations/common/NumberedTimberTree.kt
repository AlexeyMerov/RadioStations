package com.alexeymerov.radiostations.common

import timber.log.Timber

/**
 * Tree realization for Timber. Taken from somewhere a long time ago.
 * */
class NumberedTimberTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "$DEFAULT_TAG: (%s:%s)::%s",
            element.fileName,
            element.lineNumber,
            element.methodName
        )
    }

    companion object {
        private const val DEFAULT_TAG = "Merov --> "
    }
}
