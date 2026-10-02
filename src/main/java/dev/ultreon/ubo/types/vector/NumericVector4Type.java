package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.types.DataType;
import dev.ultreon.ubo.types.util.Vector4;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A vector of four numbers of the same type, eg. four {@code int} values, written as {@code ivec4[1,2,3,4]} in USO.
 *
 * @param <T> the type of the vector's elements.
 * @see IntVector4Type
 * @see LongVector4Type
 * @see FloatVector4Type
 * @see DoubleVector4Type
 */
public abstract class NumericVector4Type<T extends Number> implements NumericVector<T>, DataType<Vector4<T>> {
    private final Vector4<T> value;

    protected NumericVector4Type(T x, T y, T z, T w) {
        this.value = new Vector4<>(check(0, x), check(1, y), check(2, z), check(3, w));
    }

    protected NumericVector4Type(Vector4<T> value) {
        if (value == null) throw new IllegalArgumentException("Value can't be set to null");
        this.value = new Vector4<>(check(0, value.x), check(1, value.y), check(2, value.z), check(3, value.w));
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

    public T getW() {
        return value.w;
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

    public void setW(T w) {
        value.w = check(3, w);
    }

    @Override
    public int size() {
        return 4;
    }

    public void setAll(T x, T y, T z, T w) {
        T newX = check(0, x);
        T newY = check(1, y);
        T newZ = check(2, z);
        T newW = check(3, w);
        value.x = newX;
        value.y = newY;
        value.z = newZ;
        value.w = newW;
    }

    @Override
    public T get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        if (index == 2) return getZ();
        if (index == 3) return getW();
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
        } else if (index == 3) {
            setW(element);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    @Override
    public void fill(T element) {
        setAll(element, element, element, element);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new NumericVector4Iterator<>(this);
    }

    @Override
    public Vector4<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Vector4<T> obj) {
        if (obj == null) throw new IllegalArgumentException("Value can't be set to null");
        T newX = check(0, obj.x);
        T newY = check(1, obj.y);
        T newZ = check(2, obj.z);
        T newW = check(3, obj.w);
        this.value.x = newX;
        this.value.y = newY;
        this.value.z = newZ;
        this.value.w = newW;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        writeElement(output, getX());
        writeElement(output, getY());
        writeElement(output, getZ());
        writeElement(output, getW());
    }

    @Override
    public String writeUso() {
        return VectorElements.usoType(elementType()) + "vec4[" + getX() + "," + getY() + "," + getZ() + "," + getW() + "]";
    }

    @Override
    public abstract NumericVector4Type<T> copy();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumericVector4Type<?> that = (NumericVector4Type<?>) o;
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

class NumericVector4Iterator<T extends Number> implements Iterator<T> {
    private final NumericVector4Type<T> vector;
    private int index;

    NumericVector4Iterator(NumericVector4Type<T> vector) {
        this.vector = vector;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < 4;
    }

    @Override
    public T next() {
        if (index >= 4) throw new NoSuchElementException("No more elements");
        int current = index++;
        if (current == 0) return vector.getX();
        if (current == 1) return vector.getY();
        if (current == 2) return vector.getZ();
        return vector.getW();
    }
}
