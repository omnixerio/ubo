package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.types.DataType;
import dev.ultreon.ubo.types.util.Vector2;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A vector of two numbers of the same type, eg. two {@code int} values, written as {@code ivec2[1,2]} in USO.
 *
 * @param <T> the type of the vector's elements.
 * @see IntVector2Type
 * @see LongVector2Type
 * @see FloatVector2Type
 * @see DoubleVector2Type
 */
public abstract class NumericVector2Type<T extends Number> implements NumericVector<T>, DataType<Vector2<T>> {
    private final Vector2<T> value;

    protected NumericVector2Type(T x, T y) {
        this.value = new Vector2<>(check(0, x), check(1, y));
    }

    protected NumericVector2Type(Vector2<T> value) {
        if (value == null) throw new IllegalArgumentException("Value can't be set to null");
        this.value = new Vector2<>(check(0, value.x), check(1, value.y));
    }

    public T getX() {
        return value.x;
    }

    public T getY() {
        return value.y;
    }

    public void setX(T x) {
        value.x = check(0, x);
    }

    public void setY(T y) {
        value.y = check(1, y);
    }

    @Override
    public int size() {
        return 2;
    }

    public void setAll(T x, T y) {
        T newX = check(0, x);
        T newY = check(1, y);
        value.x = newX;
        value.y = newY;
    }

    @Override
    public T get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    @Override
    public void set(int index, T element) {
        if (index == 0) {
            setX(element);
        } else if (index == 1) {
            setY(element);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    @Override
    public void fill(T element) {
        setAll(element, element);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new NumericVector2Iterator<>(this);
    }

    @Override
    public Vector2<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Vector2<T> obj) {
        if (obj == null) throw new IllegalArgumentException("Value can't be set to null");
        T newX = check(0, obj.x);
        T newY = check(1, obj.y);
        this.value.x = newX;
        this.value.y = newY;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        writeElement(output, getX());
        writeElement(output, getY());
    }

    @Override
    public String writeUso() {
        return VectorElements.usoType(elementType()) + "vec2[" + getX() + "," + getY() + "]";
    }

    @Override
    public abstract NumericVector2Type<T> copy();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumericVector2Type<?> that = (NumericVector2Type<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return writeUso();
    }

    /**
     * Writes a single element of this vector.
     *
     * @param output the output to write to.
     * @param element the element to write.
     * @throws IOException when an I/O error occurs.
     */
    protected abstract void writeElement(DataOutput output, T element) throws IOException;

    private T check(int index, T element) {
        return VectorElements.check(index, element, elementType());
    }
}

class NumericVector2Iterator<T extends Number> implements Iterator<T> {
    private final NumericVector2Type<T> vector;
    private int index;

    NumericVector2Iterator(NumericVector2Type<T> vector) {
        this.vector = vector;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < 2;
    }

    @Override
    public T next() {
        if (index >= 2) throw new NoSuchElementException("No more elements");
        int current = index++;
        if (current == 0) return vector.getX();
        return vector.getY();
    }
}
