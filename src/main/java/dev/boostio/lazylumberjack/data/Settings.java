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

package dev.boostio.lazylumberjack.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {

    private Detection detection = new Detection();
    private Animations animations = new Animations();
    private Helpers helpers = new Helpers();

    @Getter
    @Setter
    public class Detection {
        private int leafRange = 2;
        private int airRange = 1;
    }

    @Getter
    @Setter
    public class Animations {
        private SlowBreak slowBreak = new SlowBreak();

        @Getter
        @Setter
        public class SlowBreak {
            private boolean enabled = true;
            private Particles particles = new Particles();
            private Delay delay = new Delay();

            @Getter
            @Setter
            public class Particles {
                private boolean enabled = true;
                private int amount = 5;
                private Offset offset = new Offset();

                @Getter
                @Setter
                public class Offset {
                    private float x = 0.0f;
                    private float y = 0.0f;
                    private float z = 0.0f;
                }
            }

            @Getter
            @Setter
            public class Delay {
                private int baseDelay = 40;
                private double speedFactor = 0.1;
            }
        }
    }

    @Getter
    @Setter
    public class Helpers {
        private boolean placeSapling = true;
    }
}
