/*
 * This file is part of LazyLumberjack - https://github.com/Aaron2404/LazyLumberjack
 * Copyright (C) 2024 Aaron and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.boostio.lazylumberjack.schedulers;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface IScheduler {
    void runAsyncTask(Consumer<Object> task);

    void runAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit);

    void runTask(Consumer<Object> task);

    void runTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit);

    void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit);
}
