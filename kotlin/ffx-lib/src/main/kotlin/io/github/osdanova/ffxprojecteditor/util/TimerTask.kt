package io.github.osdanova.ffxprojecteditor.util

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Periodically executes [task] every [intervalMs] milliseconds.
 *
 * Mirrors the C# TimerTask which used System.Timers.Timer. Uses a
 * single-threaded ScheduledExecutorService so each tick happens on the
 * same thread; if the task throws, the timer is stopped (matching C#).
 */
class TimerTask(
    intervalMs: Long,
    val task: () -> Unit,
) {
    @Volatile
    var intervalMs: Long = intervalMs
        private set

    private var executor: ScheduledExecutorService? = null
    private var future: ScheduledFuture<*>? = null

    val isRunning: Boolean
        get() = future != null && !future!!.isCancelled

    fun start() {
        if (isRunning) return
        val exec = Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r, "TimerTask").apply { isDaemon = true }
        }
        executor = exec
        future = exec.scheduleAtFixedRate(
            { executeTask() },
            intervalMs,
            intervalMs,
            TimeUnit.MILLISECONDS,
        )
        println("Timer started.")
    }

    fun stop() {
        if (!isRunning) {
            // Still attempt cleanup if executor is dangling
            executor?.shutdownNow()
            executor = null
            future = null
            return
        }
        future?.cancel(false)
        executor?.shutdownNow()
        future = null
        executor = null
        println("Timer stopped.")
    }

    fun changeInterval(newIntervalMs: Long) {
        if (newIntervalMs > 0) {
            println("Changing timer interval to ${newIntervalMs}ms.")
            this.intervalMs = newIntervalMs
            if (isRunning) {
                stop()
                start()
            }
        } else {
            println("Invalid interval value.")
        }
    }

    private fun executeTask() {
        try {
            task()
        } catch (ex: Exception) {
            println("Error in Timer Task: ${ex.message}")
            stop()
        }
    }
}
