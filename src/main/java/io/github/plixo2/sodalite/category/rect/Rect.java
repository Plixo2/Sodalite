package io.github.plixo2.sodalite.category.rect;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.libsdl.sdl.SDL_Point;
import org.libsdl.sdl.SDL_Rect;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.List;
import java.util.Objects;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlCategory CategoryRect
/// @sdlAPI SDL_Rect
@Setter
@Getter
@ToString
public class Rect {

    private int x;
    private int y;
    private int width;
    private int height;

    ///
    /// {@index "Java Collections Framework"}
    ///
    private Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static Rect of(int x, int y, int width, int height) {
        return new Rect(x, y, width, height);
    }
    public static Rect of(Rect rect) {
        return new Rect(rect.x, rect.y, rect.width, rect.height);
    }
    private static Rect of(MemorySegment segment) {
        return new Rect(
                SDL_Rect.x(segment),
                SDL_Rect.y(segment),
                SDL_Rect.w(segment),
                SDL_Rect.h(segment)
        );
    }
    public static Rect zero() {
        return new Rect(0, 0, 0, 0);
    }

    /// @sdlAPI SDL_RectToFRect
    public FRect toFRect() {
        return FRect.of(this.x, this.y, this.width, this.height);
    }

    /// @sdlAPI SDL_RectEmpty
    public boolean empty() {
        return this.width <= 0 || this.height <= 0;
    }


    /// @sdlAPI SDL_PointInRect
    public boolean isInside(Vector2i point) {
        return this.isInside(point.x, point.y);
    }

    public boolean isInside(int x, int y) {
        // >= and <= are intentional
        return x >= this.x && x <= this.x + this.width &&
                y >= this.y && y <= this.y + this.height;
    }

    /// @sdlAPI SDL_HasRectIntersection
    public boolean inserts(Rect rect) {
        return this.x < rect.x + rect.width && this.x + this.width > rect.x
                && this.y < rect.y + rect.height && this.y + this.height > rect.y;
    }

    /// @sdlAPI SDL_GetRectUnion
    public Rect union(Rect other) {
        if (this.empty()) {
            if (other.empty()) {
                return Rect.zero();
            }
            return Rect.of(other);
        } else if (other.empty()) {
            return Rect.of(this);
        }

        int x = Math.min(this.x, other.x);
        int y = Math.min(this.y, other.y);
        return new Rect(
                x, y,
                Math.max(this.x + this.width,  other.x + other.width)  - x,
                Math.max(this.y + this.height, other.y + other.height) - y
        );
    }

    /// @sdlAPI SDL_GetRectIntersection
    public @Nullable Rect intersection(Rect other) {
        int x = Math.max(this.x, other.x);
        int y = Math.max(this.y, other.y);
        var width = Math.min(this.x + this.width, other.x + other.width) - x;
        var height = Math.min(this.y + this.height, other.y + other.height) - y;

        if (width <= 0 || height <= 0) {
            return null;
        }

        return new Rect(
                x,
                y,
                width,
                height
        );
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    public @Nullable Vector4i lineIntersection(
            int x1, int y1,
            int x2, int y2
    ) {
        return this.lineIntersection(new Vector4i(), x1, y1, x2, y2);
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    public @Nullable Vector4i lineIntersection(
            Vector2i start,
            Vector2i end
    ) {
        return this.lineIntersection(new Vector4i(), start, end);
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    public @Nullable Vector4i lineIntersection(
            Vector4i result,
            Vector2i start,
            Vector2i end
    ) {
        return this.lineIntersection(result, start.x, start.y, end.x, end.y);
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    /// @sdlAPI SDL_GetRectAndLineIntersection
    public @Nullable Vector4i lineIntersection(Vector4i result, int x1, int y1, int x2, int y2) {
        try (var arena = Arena.ofConfined()) {
            var rect = SDL_Rect.allocate(arena);
            this.put(rect);

            var pX1 = arena.allocateFrom(ValueLayout.JAVA_INT, x1);
            var pY1 = arena.allocateFrom(ValueLayout.JAVA_INT, y1);
            var pX2 = arena.allocateFrom(ValueLayout.JAVA_INT, x2);
            var pY2 = arena.allocateFrom(ValueLayout.JAVA_INT, y2);

            if (!SDL_GetRectAndLineIntersection(rect, pX1, pY1, pX2, pY2)) {
                return null;
            }

            result.set(
                    pX1.get(ValueLayout.JAVA_INT, 0),
                    pY1.get(ValueLayout.JAVA_INT, 0),
                    pX2.get(ValueLayout.JAVA_INT, 0),
                    pY2.get(ValueLayout.JAVA_INT, 0)
            );
            return result;
        }
    }

    /// @sdlAPI SDL_RectsEqual
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rect rect)) {
            return false;
        }
        return this.x == rect.x && this.y == rect.y && this.width == rect.width && this.height == rect.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.width, this.height);
    }

    /// @sdlAPI SDL_GetRectEnclosingPoints
    public static @Nullable Rect enclosingPoints(List<Vector2i> points, @Nullable Rect clip) {
        if (points.isEmpty()) {
            return null;
        }

        try (var arena = Arena.ofConfined()) {
            var pointsSegment = SDL_Point.allocateArray(points.size(), arena);
            for (int i = 0; i < points.size(); i++) {
                put(points.get(i), SDL_Point.asSlice(pointsSegment, i));
            }

            var clipSegment = MemorySegment.NULL;
            if (clip != null) {
                clipSegment = SDL_Rect.allocate(arena);
                clip.put(clipSegment);
            }

            var result = SDL_Rect.allocate(arena);
            if (!SDL_GetRectEnclosingPoints(pointsSegment, points.size(), clipSegment, result)) {
                return null;
            }

            return Rect.of(result);
        }
    }

    /// @sdlAPI SDL_Point
    private static void put(Vector2i vector2i, MemorySegment segment) {
        SDL_Point.initialize(segment,
                vector2i.x,
                vector2i.y
        );
    }

    private void put(MemorySegment segment) {
        SDL_Rect.initialize(segment,
                this.x,
                this.y,
                this.width,
                this.height
        );
    }


}
