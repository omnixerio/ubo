package dev.ultreon.ubo.types;

import dev.ultreon.ubo.DataTypeRegistry;
import dev.ultreon.ubo.types.util.Vector4;
import dev.ultreon.ubo.DataTypes;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Vector4Type<T extends DataType<?>> implements VectorType<T>, DataType<Vector4<T>> {
    private final Vector4<T> value;

    public Vector4Type(Vector4<T> value) {
        this.value = new Vector4<>(value);
        checkElementTypes();
    }

    public Vector4Type(T x, T y, T z, T w) {
        this.value = new Vector4<>(x, y, z, w);
        checkElementTypes();
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
        checkElementType(0, x, value.y.id());
        value.x = x;
    }

    public void setY(T y) {
        checkElementType(1, y, value.x.id());
        value.y = y;
    }

    public void setZ(T z) {
        checkElementType(2, z, value.x.id());
        value.z = z;
    }

    public void setW(T w) {
        checkElementType(3, w, value.x.id());
        value.w = w;
    }

    public int size() {
        return 4;
    }

    public void set(T x, T y, T z, T w) {
        int id = x.id();
        checkElementType(1, y, id);
        checkElementType(2, z, id);
        checkElementType(3, w, id);
        value.x = x;
        value.y = y;
        value.z = z;
        value.w = w;
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
    public void set(int index, T value) {
        if (index == 0) {
            setX(value);
        } else if (index == 1) {
            setY(value);
        } else if (index == 2) {
            setZ(value);
        } else if (index == 3) {
            setW(value);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    public void fill(T value) {
        set(value, value, value, value);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new Vector4Iterator<>(this);
    }

    @Override
    public Vector4<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Vector4<T> obj) {
        int id = obj.x.id();
        checkElementType(1, obj.y, id);
        checkElementType(2, obj.z, id);
        checkElementType(3, obj.w, id);
        this.value.x = obj.x;
        this.value.y = obj.y;
        this.value.z = obj.z;
        this.value.w = obj.w;
    }

    @Override
    public int id() {
        return DataTypes.VECTOR_4;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(value.x.id());
        value.x.write(output);
        value.y.write(output);
        value.z.write(output);
        value.w.write(output);
    }

    public static Vector4Type<?> read(DataInput input) throws IOException {
        int id = input.readUnsignedByte();
        return new Vector4Type<>(DataTypeRegistry.read(id, input), DataTypeRegistry.read(id, input), DataTypeRegistry.read(id, input), DataTypeRegistry.read(id, input));
    }

    public static Vector4Type<ByteType> ofByte(byte x, byte y, byte z, byte w) {
        return new Vector4Type<>(new ByteType(x), new ByteType(y), new ByteType(z), new ByteType(w));
    }

    public static Vector4Type<ShortType> ofShort(short x, short y, short z, short w) {
        return new Vector4Type<>(new ShortType(x), new ShortType(y), new ShortType(z), new ShortType(w));
    }

    public static Vector4Type<IntType> ofInt(int x, int y, int z, int w) {
        return new Vector4Type<>(new IntType(x), new IntType(y), new IntType(z), new IntType(w));
    }

    public static Vector4Type<LongType> ofLong(long x, long y, long z, long w) {
        return new Vector4Type<>(new LongType(x), new LongType(y), new LongType(z), new LongType(w));
    }

    public static Vector4Type<FloatType> ofFloat(float x, float y, float z, float w) {
        return new Vector4Type<>(new FloatType(x), new FloatType(y), new FloatType(z), new FloatType(w));
    }

    public static Vector4Type<DoubleType> ofDouble(double x, double y, double z, double w) {
        return new Vector4Type<>(new DoubleType(x), new DoubleType(y), new DoubleType(z), new DoubleType(w));
    }

    public static Vector4Type<CharType> ofChar(char x, char y, char z, char w) {
        return new Vector4Type<>(new CharType(x), new CharType(y), new CharType(z), new CharType(w));
    }

    public static Vector4Type<BooleanType> ofBoolean(boolean x, boolean y, boolean z, boolean w) {
        return new Vector4Type<>(new BooleanType(x), new BooleanType(y), new BooleanType(z), new BooleanType(w));
    }

    public static Vector4Type<StringType> ofString(String x, String y, String z, String w) {
        return new Vector4Type<>(new StringType(x), new StringType(y), new StringType(z), new StringType(w));
    }

    public static Vector4Type<BigIntType> ofBigInteger(BigInteger x, BigInteger y, BigInteger z, BigInteger w) {
        return new Vector4Type<>(new BigIntType(x), new BigIntType(y), new BigIntType(z), new BigIntType(w));
    }

    public static Vector4Type<BigDecType> ofBigDecimal(BigDecimal x, BigDecimal y, BigDecimal z, BigDecimal w) {
        return new Vector4Type<>(new BigDecType(x), new BigDecType(y), new BigDecType(z), new BigDecType(w));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector4Type<?> that = (Vector4Type<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public Vector4Type<T> copy() {
        return new Vector4Type<>(value);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final <C extends DataType<?>> Vector4Type<C> vectorCast(C... type) {
        return this.vectorCast((Class<C>) type.getClass().getComponentType());
    }

    @SuppressWarnings("unchecked")
    public final <C extends DataType<?>> Vector4Type<C> vectorCast(Class<C> type) {
        int id = DataTypeRegistry.getIdOrThrow(type);
        checkElementType(0, getX(), id);
        checkElementType(1, getY(), id);
        checkElementType(2, getZ(), id);
        checkElementType(3, getW(), id);
        return new Vector4Type<>((Vector4<C>) value);
    }

    private void checkElementType(int index, T element, int id) {
        if (element.id() != id)
            throw new IllegalArgumentException("Type at index " + index + " has invalid id: " + element.id() + " (expected " + id + ")");
    }

    private void checkElementTypes() {
        int id = value.x.id();
        checkElementType(1, value.y, id);
        checkElementType(2, value.z, id);
        checkElementType(3, value.w, id);
    }

    @Override
    public String writeUso() {
        return VectorType.writeUsoVector(this);
    }

    @Override
    public String toString() {
        return writeUso();
    }
}


class Vector4Iterator<T extends DataType<?>> implements Iterator<T> {
    private final Vector4Type<T> vector;
    private int index;

    public Vector4Iterator(Vector4Type<T> vector) {
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
