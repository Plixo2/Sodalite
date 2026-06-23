package io.github.plixo2.sodalite.category.power;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/// Wrapper for return type of [Power#getPowerInfo()]
@Getter
@EqualsAndHashCode
@ToString
public class PowerInfo {
    private final PowerState state;

    private final Value seconds;
    private final Value percent;

    PowerInfo(PowerState state, int seconds, int percent) {
        this.state = state;
        this.seconds = Value.of(seconds);
        this.percent = Value.of(percent);
    }

    public sealed interface Value {

        // when the value is -1;
        record Unknown() implements Value {
            @Override
            public @NotNull String toString() {
                return "Unknown";
            }
        }
        record Present(int value) implements Value {

            @Override
            public @NotNull String toString() {
                return String.valueOf(this.value);
            }
        }

        default int or(int defaultValue) {
            if (this instanceof Present(int value)) {
                return value;
            } else {
                return defaultValue;
            }
        }

        private static Value of(int value) {
            if (value == -1) {
                return new Unknown();
            } else {
                return new Present(value);
            }
        }


    }


}
