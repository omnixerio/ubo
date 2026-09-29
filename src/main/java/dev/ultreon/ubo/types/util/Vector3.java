package dev.ultreon.ubo.types.util;

import java.util.Objects;

public class Vector3<T> {
    public T x;
    public T y;
    public T z;

    public Vector3(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3<T> value) {
        this.x = value.x;
        this.y = value.y;
        this.z = value.z;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector3<?> vector3 = (Vector3<?>) o;
        return Objects.equals(x, vector3.x) && Objects.equals(y, vector3.y) && Objects.equals(z, vector3.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
