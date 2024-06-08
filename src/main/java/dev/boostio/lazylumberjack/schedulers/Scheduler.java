package dev.boostio.lazylumberjack.schedulers;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface Scheduler {
    void runAsyncTask(Consumer<Object> task);

    void runAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit);

    void runTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit);

    void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit);
}
