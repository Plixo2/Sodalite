package io.github.plixo2.sodalite.category.rect;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.libsdl.sdl.SDL_FPoint;
import org.libsdl.sdl.SDL_FRect;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.List;
import java.util.Objects;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_FRect
@Setter
@Getter
@ToString
public class FRect {

    public float x;
    public float y;
    public float width;
    public float height;

    private FRect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public static FRect of(float x, float y, float width, float height) {
        return new FRect(x, y, width, height);
    }
    public static FRect of(FRect rect) {
        return new FRect(rect.x, rect.y, rect.width, rect.height);
    }
    public static FRect of(Vector4f vector4f) {
        return new FRect(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }
    private static FRect of(MemorySegment segment) {
        return new FRect(
                SDL_FRect.x(segment),
                SDL_FRect.y(segment),
                SDL_FRect.w(segment),
                SDL_FRect.h(segment)
        );
    }
    public static FRect zero() {
        return new FRect(0, 0, 0, 0);
    }

    public Rect floor() {
        return Rect.of(
                (int) Math.floor(this.x),
                (int) Math.floor(this.y),
                (int) Math.floor(this.width),
                (int) Math.floor(this.height)
        );
    }
    public Rect round() {
        return Rect.of(
                Math.round(this.x),
                Math.round(this.y),
                Math.round(this.width),
                Math.round(this.height)
        );
    }

    /// @sdlAPI  SDL_RectEmptyFloat
    public boolean empty() {
        // '<' is intentionally
        return this.width < 0.0f || this.height < 0.0f;
    }

    /// @sdlAPI SDL_PointInRectFloat
    public boolean isInside(Vector2f point) {
        return this.isInside(point.x, point.y);
    }

    public boolean isInside(float x, float y) {
        // >= and <= are intentional
        return x >= this.x && x <= this.x + this.width &&
                y >= this.y && y <= this.y + this.height;
    }

    /// @sdlAPI SDL_HasRectIntersectionFloat
    public boolean intersects(FRect rect) {
        return this.x <= rect.x + rect.width && this.x + this.width >= rect.x
                && this.y <= rect.y + rect.height && this.y + this.height >= rect.y;
    }

    /// @sdlAPI SDL_GetRectUnionFloat
    public FRect union(FRect other) {
        if (this.empty()) {
            if (other.empty()) {
                return FRect.zero();
            }
            return FRect.of(other);
        } else if (other.empty()) {
            return FRect.of(this);
        }

        float x = Math.min(this.x, other.x);
        float y = Math.min(this.y, other.y);
        return new FRect(
                x, y,
                Math.max(this.x + this.width,  other.x + other.width)  - x,
                Math.max(this.y + this.height, other.y + other.height) - y
        );
    }

    /// @sdlAPI SDL_GetRectIntersectionFloat
    public @Nullable FRect intersection(FRect other) {
        float x = Math.max(this.x, other.x);
        float y = Math.max(this.y, other.y);
        var width = Math.min(this.x + this.width, other.x + other.width) - x;
        var height = Math.min(this.y + this.height, other.y + other.height) - y;

        if (width < 0.0f || height < 0.0f) {
            return null;
        }

        return new FRect(
                x,
                y,
                width,
                height
        );
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    public @Nullable Vector4f lineIntersection(
            float x1, float y1,
            float x2, float y2
    ) {
        return this.lineIntersection(new Vector4f(), x1, y1, x2, y2);
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    public @Nullable Vector4f lineIntersection(
            Vector2f start,
            Vector2f end
    ) {
        return this.lineIntersection(new Vector4f(), start, end);
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    public @Nullable Vector4f lineIntersection(
            Vector4f result,
            Vector2f start,
            Vector2f end
    ) {
        return this.lineIntersection(result, start.x, start.y, end.x, end.y);
    }

    /// @return the clipped line endpoints in result if there is an intersection, null otherwise
    /// @sdlAPI SDL_GetRectAndLineIntersectionFloat
    public @Nullable Vector4f lineIntersection(
            Vector4f result,
            float x1, float y1,
            float x2, float y2
    ) {
        try (var arena = Arena.ofConfined()) {
            var rect = SDL_FRect.allocate(arena);
            this.put(rect);

            var pX1 = arena.allocateFrom(ValueLayout.JAVA_FLOAT, x1);
            var pY1 = arena.allocateFrom(ValueLayout.JAVA_FLOAT, y1);
            var pX2 = arena.allocateFrom(ValueLayout.JAVA_FLOAT, x2);
            var pY2 = arena.allocateFrom(ValueLayout.JAVA_FLOAT, y2);

            if (!SDL_GetRectAndLineIntersectionFloat(rect, pX1, pY1, pX2, pY2)) {
                return null;
            }

            result.set(
                    pX1.get(ValueLayout.JAVA_FLOAT, 0),
                    pY1.get(ValueLayout.JAVA_FLOAT, 0),
                    pX2.get(ValueLayout.JAVA_FLOAT, 0),
                    pY2.get(ValueLayout.JAVA_FLOAT, 0)
            );
            return result;
        }
    }


    /// @sdlAPI SDL_RectsEqualEpsilon
    public boolean equals(FRect rect, float epsilon) {
        // '<=' is intentional
        return Math.abs(this.x - rect.x) <= epsilon &&
                Math.abs(this.y - rect.y) <= epsilon &&
                Math.abs(this.width - rect.width) <= epsilon &&
                Math.abs(this.height - rect.height) <= epsilon;
    }

    /// @sdlAPI SDL_RectsEqualFloat
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FRect fRect)) {
            return false;
        }
        return Float.compare(this.x, fRect.x) == 0
                && Float.compare(this.y, fRect.y) == 0
                && Float.compare(this.width, fRect.width) == 0
                && Float.compare(this.height, fRect.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.width, this.height);
    }

    /// @sdlAPI SDL_GetRectEnclosingPointsFloat
    public static @Nullable FRect enclosingPoints(List<Vector2f> points, @Nullable FRect clip) {
        if (points.isEmpty()) {
            return null;
        }

        try (var arena = Arena.ofConfined()) {
            var pointsSegment = SDL_FPoint.allocateArray(points.size(), arena);
            for (int i = 0; i < points.size(); i++) {
                put(points.get(i), SDL_FPoint.asSlice(pointsSegment, i));
            }

            var clipSegment = MemorySegment.NULL;
            if (clip != null) {
                clipSegment = SDL_FRect.allocate(arena);
                clip.put(clipSegment);
            }

            var result = SDL_FRect.allocate(arena);
            if (!SDL_GetRectEnclosingPointsFloat(pointsSegment, points.size(), clipSegment, result)) {
                return null;
            }

            return FRect.of(result);
        }
    }

    /// @sdlAPI SDL_FPoint
    private static void put(Vector2f vector2f, MemorySegment segment) {
        SDL_FPoint.initialize(segment,
                vector2f.x,
                vector2f.y
        );
    }

    private void put(MemorySegment segment) {
        SDL_FRect.initialize(segment,
                this.x,
                this.y,
                this.width,
                this.height
        );
    }
}
