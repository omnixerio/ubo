package dev.ultreon.ubo.types.custom;

import dev.ultreon.ubo.types.DataType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

@Deprecated
public interface Vector2Type<T, V> extends DataType<T>, VectorType<V> {
    V getX();
    V getY();

    void setX(V x);
    void setY(V y);

    @Override
    default int size() {
        return 2;
    }

    default void set(V x, V y) {
        setX(x);
        setY(y);
    }

    @Override
    default void fill(V value) {
        set(value, value);
    }

    @Override
    default V get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        throw new IndexOutOfBoundsException();
    }

    @Override
    default void set(int index, V value) {
        if (index == 0) setX(value);
        if (index == 1) setY(value);
    }

    @Override
    @NotNull
    default Iterator<V> iterator() {
        return new Vector2Iterator<>(this);
    }
}

class Vector2Iterator<V> implements Iterator<V> {
    private final Vector2Type<?, V> vector;
    private int index;

    public Vector2Iterator(Vector2Type<?, V> vector) {
        this.vector = vector;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < 2;
    }

    @Override
    public V next() {
        if (index >= 2) throw new NoSuchElementException("No more elements");
        if (index++ == 0) return vector.getX();
        if (index++ == 1) return vector.getY();
        throw new NoSuchElementException("No more elements");
    }
}
