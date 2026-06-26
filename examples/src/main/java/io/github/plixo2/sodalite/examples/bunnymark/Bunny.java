package io.github.plixo2.sodalite.examples.bunnymark;

import io.github.plixo2.sodalite.memory.Layouts;
import org.joml.Vector4f;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;

public class Bunny {
    private static final float GRAVITY = 0.75f;
    public static StructLayout LAYOUT = MemoryLayout.structLayout(
            Layouts.FLOAT_2.withName("position"),
            Layouts.FLOAT_2.withName("size"),
            Layouts.FLOAT_4.withName("color")
    );

    private float speedX;
    private float speedY;
    private float x;
    private float y;

    private final float width;
    private final float height;
    private final Vector4f color;

    public Bunny(Vector4f color, int width, int height) {
        this.x = 0f;
        this.y = 0f;

        this.speedX = (float) Math.random() * 10;
        this.speedY = (float) Math.random() * 10f;

        var size = 0.8f + (float) Math.random() * 0.4f;
        this.width = width * size;
        this.height = height * size;

        this.color = color;
    }

    void put(MemorySegment memory, long offset) {
        memory.set(ValueLayout.JAVA_FLOAT, offset, this.x);
        memory.set(ValueLayout.JAVA_FLOAT, offset + 4, this.y);
        memory.set(ValueLayout.JAVA_FLOAT, offset + 8, this.width);
        memory.set(ValueLayout.JAVA_FLOAT, offset + 12, this.height);
        memory.set(ValueLayout.JAVA_FLOAT, offset + 16, this.color.x);
        memory.set(ValueLayout.JAVA_FLOAT, offset + 20, this.color.y);
        memory.set(ValueLayout.JAVA_FLOAT, offset + 24, this.color.z);
        memory.set(ValueLayout.JAVA_FLOAT, offset + 28, this.color.w);
    }

    public void update(Vector4f viewport) {
        this.x += this.speedX;
        this.y += this.speedY;
        this.speedY += GRAVITY;

        if (this.x > viewport.z) {
            this.speedX *= -1;
            this.x = viewport.z;
        } else if (this.x < viewport.x) {
            this.speedX *= -1;
            this.x = viewport.x;
        }

        if (this.y > viewport.w) {
            this.speedY *= -1f;
            this.y = viewport.w;
        } else if (this.y < viewport.y) {
            this.speedY *= -1f;
            this.y = viewport.y;
        }

    }

}
