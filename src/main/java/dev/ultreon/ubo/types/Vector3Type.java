package dev.ultreon.ubo.types;

import dev.ultreon.ubo.DataTypeRegistry;
import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.util.Vector3;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Vector3Type<T extends DataType<?>> implements VectorType<T>, DataType<Vector3<T>> {
    private final Vector3<T> value;

    public Vector3Type(Vector3<T> value) {
        this.value = new Vector3<>(value);
        checkElementTypes();
    }

    public Vector3Type(T x, T y, T z) {
        this.value = new Vector3<>(x, y, z);
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

    public int size() {
        return 3;
    }

    public void set(T x, T y, T z) {
        int id = x.id();
        checkElementType(1, y, id);
        checkElementType(2, z, id);
        value.x = x;
        value.y = y;
        value.z = z;
    }

    @Override
    public T get(int index) {
        if (index == 0) return getX();
        if (index == 1) return getY();
        if (index == 2) return getZ();
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
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    public void fill(T value) {
        set(value, value, value);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new Vector3Iterator<>(this);
    }

    @Override
    public Vector3<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Vector3<T> obj) {
        int id = obj.x.id();
        checkElementType(1, obj.y, id);
        checkElementType(2, obj.z, id);
        this.value.x = obj.x;
        this.value.y = obj.y;
        this.value.z = obj.z;
    }

    @Override
    public int id() {
        return DataTypes.VECTOR_3;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(value.x.id());
        value.x.write(output);
        value.y.write(output);
        value.z.write(output);
    }

    public static Vector3Type<?> read(DataInput input) throws IOException {
        int id = input.readUnsignedByte();
        return new Vector3Type<>(DataTypeRegistry.read(id, input), DataTypeRegistry.read(id, input), DataTypeRegistry.read(id, input));
    }

    public static Vector3Type<ByteType> ofByte(byte x, byte y, byte z) {
        return new Vector3Type<>(new ByteType(x), new ByteType(y), new ByteType(z));
    }

    public static Vector3Type<ShortType> ofShort(short x, short y, short z) {
        return new Vector3Type<>(new ShortType(x), new ShortType(y), new ShortType(z));
    }

    public static Vector3Type<IntType> ofInt(int x, int y, int z) {
        return new Vector3Type<>(new IntType(x), new IntType(y), new IntType(z));
    }

    public static Vector3Type<LongType> ofLong(long x, long y, long z) {
        return new Vector3Type<>(new LongType(x), new LongType(y), new LongType(z));
    }

    public static Vector3Type<FloatType> ofFloat(float x, float y, float z) {
        return new Vector3Type<>(new FloatType(x), new FloatType(y), new FloatType(z));
    }

    public static Vector3Type<DoubleType> ofDouble(double x, double y, double z) {
        return new Vector3Type<>(new DoubleType(x), new DoubleType(y), new DoubleType(z));
    }

    public static Vector3Type<CharType> ofChar(char x, char y, char z) {
        return new Vector3Type<>(new CharType(x), new CharType(y), new CharType(z));
    }

    public static Vector3Type<BooleanType> ofBoolean(boolean x, boolean y, boolean z) {
        return new Vector3Type<>(new BooleanType(x), new BooleanType(y), new BooleanType(z));
    }

    public static Vector3Type<StringType> ofString(String x, String y, String z) {
        return new Vector3Type<>(new StringType(x), new StringType(y), new StringType(z));
    }

    public static Vector3Type<BigIntType> ofBigInteger(BigInteger x, BigInteger y, BigInteger z) {
        return new Vector3Type<>(new BigIntType(x), new BigIntType(y), new BigIntType(z));
    }

    public static Vector3Type<BigDecType> ofBigDecimal(BigDecimal x, BigDecimal y, BigDecimal z) {
        return new Vector3Type<>(new BigDecType(x), new BigDecType(y), new BigDecType(z));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector3Type<?> that = (Vector3Type<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public Vector3Type<T> copy() {
        return new Vector3Type<>(value);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final <C extends DataType<?>> Vector3Type<C> vectorCast(C... type) {
        return this.vectorCast((Class<C>) type.getClass().getComponentType());
    }

    @SuppressWarnings("unchecked")
    public final <C extends DataType<?>> Vector3Type<C> vectorCast(Class<C> type) {
        int id = DataTypeRegistry.getIdOrThrow(type);
        checkElementType(0, getX(), id);
        checkElementType(1, getY(), id);
        checkElementType(2, getZ(), id);
        return new Vector3Type<>((Vector3<C>) value);
    }

    private void checkElementType(int index, T element, int id) {
        if (element.id() != id)
            throw new IllegalArgumentException("Type at index " + index + " has invalid id: " + element.id() + " (expected " + id + ")");
    }

    private void checkElementTypes() {
        int id = value.x.id();
        checkElementType(1, value.y, id);
        checkElementType(2, value.z, id);
    }

    @Override
    public String writeUso() {
        return "v3:" + VectorType.writeUsoElementType(getX()) + "[" + VectorType.writeUsoElement(getX()) + "," + VectorType.writeUsoElement(getY()) + "," + VectorType.writeUsoElement(getZ()) + "]";
    }

    @Override
    public String toString() {
        return writeUso();
    }
}

class Vector3Iterator<T extends DataType<?>> implements Iterator<T> {
    private final Vector3Type<T> vector;
    private int index;

    public Vector3Iterator(Vector3Type<T> vector) {
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
