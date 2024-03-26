package com.alexeymerov.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    // On 10 iterations:
    // min 583.2,   median 605.5,   max 630.6
    // min 439.5,   median 484.7,   max 511.3
    //     ~28%            ~22%         ~20%

    @Test
    fun startupNone() = startup(CompilationMode.None())

    @Test
    fun startupDefault() = startup()

    private fun startup(mode: CompilationMode = CompilationMode.DEFAULT) = benchmarkRule.measureRepeated(
        packageName = "com.alexeymerov.radiostations",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
        compilationMode = mode
    ) {
        pressHome()
        startActivityAndWait()
    }
}