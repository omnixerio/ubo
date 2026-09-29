package dev.ultreon.ubo.types.custom;

import dev.ultreon.ubo.types.DataType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

@Deprecated
public interface Vector3Type<T, V> extends DataType<T>, VectorType<V> {
    V getX();
    V getY();
    V getZ();

    void setX(V x);
    void setY(V y);
    void setZ(V z);

    default int size() {
        return 3;
    }

    default void set(V x, V y, V z) {
        setX(x);
        setY(y);
    }

    @Override
    default V get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        if (index == 2) return getZ();
        throw new IndexOutOfBoundsException();
    }

    @Override
    default void set(int index, V value) {
        if (index == 0) setX(value);
        if (index == 1) setY(value);
        if (index == 2) setZ(value);
    }

    default void fill(V value) {
        set(value, value, value);
    }

    @Override
    @NotNull
    default Iterator<V> iterator() {
        return new Vector3Iterator<>(this);
    }
}

class Vector3Iterator<T, V> implements Iterator<V> {
    private final Vector3Type<T, V> vector;
    private int index;
    public Vector3Iterator(Vector3Type<T, V> vector) {
        this.vector = vector;
        this.index = 0;
    }
    @Override
    public boolean hasNext() {
        return index < 3;
    }
    @Override
    public V next() {
        if (index >= 3) throw new NoSuchElementException("No more elements");
        if (index++ == 0) return vector.getX();
        if (index++ == 1) return vector.getY();
        if (index++ == 2) return vector.getZ();
        throw new NoSuchElementException("No more elements");
    }
}
