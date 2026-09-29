package dev.ultreon.ubo.types.custom;

import dev.ultreon.ubo.types.DataType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

@Deprecated
public interface Vector4Type<T, V> extends DataType<T>, VectorType<V> {
    V getX();
    V getY();
    V getZ();
    V getW();

    void setX(V x);
    void setY(V y);
    void setZ(V z);
    void setW(V w);

    @Override
    default int size() {
        return 4;
    }

    default void set(V x, V y, V z, V w) {
        setX(x);
        setY(y);
        setZ(z);
        setW(w);
    }

    @Override
    default V get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        if (index == 2) return getZ();
        if (index == 3) return getW();
        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    @Override
    default void set(int index, V value) {
        if (index == 0) setX(value);
        if (index == 1) setY(value);
        if (index == 2) setZ(value);
        if (index == 3) setW(value);
        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    default void fill(V value) {
        set(value, value, value, value);
    }

    @Override
    @NotNull
    default Iterator<V> iterator() {
        return new Vector4Iterator<>(this);
    }
}

class Vector4Iterator<T, V> implements Iterator<V> {
    private final Vector4Type<T, V> vector;
    private int index;
    public Vector4Iterator(Vector4Type<T, V> vector) {
        this.vector = vector;
        this.index = 0;
    }
    @Override
    public boolean hasNext() {
        return index < 4;
    }
    @Override
    public V next() {
        if (index >= 4) throw new NoSuchElementException("No more elements");
        if (index++ == 0) return vector.getX();
        if (index++ == 1) return vector.getY();
        if (index++ == 2) return vector.getZ();
        if (index++ == 3) return vector.getW();

        throw new NoSuchElementException("No more elements");
    }
}
