package io.github.plixo2.sodalite.category.power;

import lombok.Getter;

/// Wrapper for return type of [Power#getPowerInfo()]
@Getter
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
        record Unknown() implements Value {}
        record Present(int value) implements Value {}

        private static Value of(int value) {
            if (value == -1) {
                return new Unknown();
            } else {
                return new Present(value);
            }
        }
    }

}
