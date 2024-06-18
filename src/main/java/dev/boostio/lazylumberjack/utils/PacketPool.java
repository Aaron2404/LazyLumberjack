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

package dev.boostio.lazylumberjack.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class PacketPool<T> {
    private final ConcurrentLinkedQueue<T> pool = new ConcurrentLinkedQueue<>();
    private final Supplier<T> packetSupplier;

    public PacketPool(Supplier<T> packetSupplier) {
        this.packetSupplier = packetSupplier;
    }

    public T acquire() {
        T packet = pool.poll();
        return (packet != null) ? packet : packetSupplier.get();
    }

    public void release(T packet) {
        pool.offer(packet);
    }
}
