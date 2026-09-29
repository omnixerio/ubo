package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.types.DataType;
import dev.ultreon.ubo.types.util.Vector3;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A vector of three numbers of the same type, eg. three {@code int} values, written as {@code ivec3[1,2,3]} in USO.
 *
 * @param <T> the type of the vector's elements.
 * @see IntVector3Type
 * @see LongVector3Type
 * @see FloatVector3Type
 * @see DoubleVector3Type
 */
public abstract class NumericVector3Type<T extends Number> implements NumericVector<T>, DataType<Vector3<T>> {
    private final Vector3<T> value;

    protected NumericVector3Type(T x, T y, T z) {
        this.value = new Vector3<>(check(0, x), check(1, y), check(2, z));
    }

    protected NumericVector3Type(Vector3<T> value) {
        if (value == null) throw new IllegalArgumentException("Value can't be set to null");
        this.value = new Vector3<>(check(0, value.x), check(1, value.y), check(2, value.z));
    }

    public T getX() {
        return value.x;
    }

    public T getY() {
        return value.y;
    }

    public T getZ() {
        return value.z;
    }

    public void setX(T x) {
        value.x = check(0, x);
    }

    public void setY(T y) {
        value.y = check(1, y);
    }

    public void setZ(T z) {
        value.z = check(2, z);
    }

    @Override
    public int size() {
        return 3;
    }

    public void setAll(T x, T y, T z) {
        T newX = check(0, x);
        T newY = check(1, y);
        T newZ = check(2, z);
        value.x = newX;
        value.y = newY;
        value.z = newZ;
    }

    @Override
    public T get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        if (index == 2) return getZ();
        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    @Override
    public void set(int index, T element) {
        if (index == 0) {
            setX(element);
        } else if (index == 1) {
            setY(element);
        } else if (index == 2) {
            setZ(element);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    @Override
    public void fill(T element) {
        setAll(element, element, element);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new NumericVector3Iterator<>(this);
    }

    @Override
    public Vector3<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Vector3<T> obj) {
        if (obj == null) throw new IllegalArgumentException("Value can't be set to null");
        T newX = check(0, obj.x);
        T newY = check(1, obj.y);
        T newZ = check(2, obj.z);
        this.value.x = newX;
        this.value.y = newY;
        this.value.z = newZ;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        writeElement(output, getX());
        writeElement(output, getY());
        writeElement(output, getZ());
    }

    @Override
    public String writeUso() {
        return VectorElements.usoType(elementType()) + "vec3[" + getX() + "," + getY() + "," + getZ() + "]";
    }

    @Override
    public abstract NumericVector3Type<T> copy();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumericVector3Type<?> that = (NumericVector3Type<?>) o;
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

class NumericVector3Iterator<T extends Number> implements Iterator<T> {
    private final NumericVector3Type<T> vector;
    private int index;

    NumericVector3Iterator(NumericVector3Type<T> vector) {
        this.vector = vector;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < 3;
    }

    @Override
    public T next() {
        if (index >= 3) throw new NoSuchElementException("No more elements");
        int current = index++;
        if (current == 0) return vector.getX();
        if (current == 1) return vector.getY();
        return vector.getZ();
    }
}
