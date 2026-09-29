package dev.ultreon.ubo.types.util;

import java.util.Objects;

public class Vector4<T> {
    public T x;
    public T y;
    public T z;
    public T w;

    public Vector4(T x, T y, T z, T w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4(Vector4<T> value) {
        this.x = value.x;
        this.y = value.y;
        this.z = value.z;
        this.w = value.w;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector4<?> vector4 = (Vector4<?>) o;
        return Objects.equals(x, vector4.x) && Objects.equals(y, vector4.y) && Objects.equals(z, vector4.z) && Objects.equals(w, vector4.w);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
