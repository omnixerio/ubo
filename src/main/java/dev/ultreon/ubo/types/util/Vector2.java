package dev.ultreon.ubo.types.util;

import java.util.Objects;

public class Vector2<T> {
    public T x;
    public T y;

    public Vector2(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2<T> value) {
        this.x = value.x;
        this.y = value.y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector2<?> vector2 = (Vector2<?>) o;
        return Objects.equals(x, vector2.x) && Objects.equals(y, vector2.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
