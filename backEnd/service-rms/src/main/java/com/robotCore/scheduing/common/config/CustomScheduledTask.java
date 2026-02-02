package com.robotCore.scheduing.common.config;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.config.*;

import java.util.concurrent.ScheduledFuture;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/13
 **/
public class CustomScheduledTask {
    private final Task task;

    @Nullable
    volatile ScheduledFuture<?> future;

    CustomScheduledTask(Task task) {
        this.task = task;
    }

    /**
     * Return the underlying task (typically a {@link CronTask},
     * {@link FixedRateTask} or {@link FixedDelayTask}).
     * @since 5.0.2
     */
    public Task getTask() {
        return this.task;
    }

    /**
     * Trigger cancellation of this scheduled task.
     * <p>This variant will force interruption of the task if still running.
     * @see #cancel(boolean)
     */
    public void cancel() {
        cancel(true);
    }

    /**
     * Trigger cancellation of this scheduled task.
     * @param mayInterruptIfRunning whether to force interruption of the task
     * if still running (specify {@code false} to allow the task to complete)
     * @since 5.3.18
     * @see ScheduledFuture#cancel(boolean)
     */
    public void cancel(boolean mayInterruptIfRunning) {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(mayInterruptIfRunning);
        }
    }

    @Override
    public String toString() {
        return this.task.toString();
    }
}
