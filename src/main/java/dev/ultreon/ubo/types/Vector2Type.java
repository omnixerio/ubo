package dev.ultreon.ubo.types;

import dev.ultreon.ubo.DataTypeRegistry;
import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.util.Vector2;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Vector2Type<T extends DataType<?>> implements VectorType<T>, DataType<Vector2<T>> {
    private final Vector2<T> value;

    public Vector2Type(Vector2<T> value) {
        this.value = new Vector2<>(value);
        checkElementTypes();
    }

    public Vector2Type(T x, T y) {
        this.value = new Vector2<>(x, y);
        checkElementTypes();
    }

    public T getX() {
        return value.x;
    }

    public T getY() {
        return value.y;
    }

    public void setX(T x) {
        checkElementType(0, x, value.y.id());
        value.x = x;
    }

    public void setY(T y) {
        checkElementType(1, y, value.x.id());
        value.y = y;
    }

    public int size() {
        return 2;
    }

    public void set(T x, T y) {
        int id = x.id();
        checkElementType(1, y, id);
        value.x = x;
        value.y = y;
    }

    @Override
    public T get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    @Override
    public void set(int index, T value) {
        if (index == 0) {
            setX(value);
        } else if (index == 1) {
            setY(value);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    public void fill(T value) {
        set(value, value);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new Vector2Iterator<>(this);
    }

    @Override
    public Vector2<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Vector2<T> obj) {
        int id = obj.x.id();
        checkElementType(1, obj.y, id);
        this.value.x = obj.x;
        this.value.y = obj.y;
    }

    @Override
    public int id() {
        return DataTypes.VECTOR_2;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(value.x.id());
        value.x.write(output);
        value.y.write(output);
    }

    public static Vector2Type<?> read(DataInput input) throws IOException {
        int id = input.readUnsignedByte();
        return new Vector2Type<>(DataTypeRegistry.read(id, input), DataTypeRegistry.read(id, input));
    }

    public static Vector2Type<ByteType> ofByte(byte x, byte y) {
        return new Vector2Type<>(new ByteType(x), new ByteType(y));
    }

    public static Vector2Type<ShortType> ofShort(short x, short y) {
        return new Vector2Type<>(new ShortType(x), new ShortType(y));
    }

    public static Vector2Type<IntType> ofInt(int x, int y) {
        return new Vector2Type<>(new IntType(x), new IntType(y));
    }

    public static Vector2Type<LongType> ofLong(long x, long y) {
        return new Vector2Type<>(new LongType(x), new LongType(y));
    }

    public static Vector2Type<FloatType> ofFloat(float x, float y) {
        return new Vector2Type<>(new FloatType(x), new FloatType(y));
    }

    public static Vector2Type<DoubleType> ofDouble(double x, double y) {
        return new Vector2Type<>(new DoubleType(x), new DoubleType(y));
    }

    public static Vector2Type<CharType> ofChar(char x, char y) {
        return new Vector2Type<>(new CharType(x), new CharType(y));
    }

    public static Vector2Type<BooleanType> ofBoolean(boolean x, boolean y) {
        return new Vector2Type<>(new BooleanType(x), new BooleanType(y));
    }

    public static Vector2Type<StringType> ofString(String x, String y) {
        return new Vector2Type<>(new StringType(x), new StringType(y));
    }

    public static Vector2Type<BigIntType> ofBigInteger(BigInteger x, BigInteger y) {
        return new Vector2Type<>(new BigIntType(x), new BigIntType(y));
    }

    public static Vector2Type<BigDecType> ofBigDecimal(BigDecimal x, BigDecimal y) {
        return new Vector2Type<>(new BigDecType(x), new BigDecType(y));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector2Type<?> that = (Vector2Type<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public Vector2Type<T> copy() {
        return new Vector2Type<>(value);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final <C extends DataType<?>> Vector2Type<C> vectorCast(C... type) {
        return this.vectorCast((Class<C>) type.getClass().getComponentType());
    }

    @SuppressWarnings("unchecked")
    public final <C extends DataType<?>> Vector2Type<C> vectorCast(Class<C> type) {
        int id = DataTypeRegistry.getIdOrThrow(type);
        checkElementType(0, getX(), id);
        checkElementType(1, getY(), id);
        return new Vector2Type<>((Vector2<C>) value);
    }

    private void checkElementType(int index, T element, int id) {
        if (element.id() != id)
            throw new IllegalArgumentException("Type at index " + index + " has invalid id: " + element.id() + " (expected " + id + ")");
    }

    private void checkElementTypes() {
        int id = value.x.id();
        checkElementType(1, value.y, id);
    }

    @Override
    public String writeUso() {
        return "v2:" + VectorType.writeUsoElementType(getX()) + "[" + VectorType.writeUsoElement(getX()) + "," + VectorType.writeUsoElement(getY()) + "]";
    }

    @Override
    public String toString() {
        return writeUso();
    }
}


class Vector2Iterator<T extends DataType<?>> implements Iterator<T> {
    private final Vector2Type<T> vector;
    private int index;

    public Vector2Iterator(Vector2Type<T> vector) {
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
