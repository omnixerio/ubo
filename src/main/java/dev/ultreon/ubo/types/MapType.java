package dev.ultreon.ubo.types;

import dev.ultreon.ubo.DataTypeRegistry;
import dev.ultreon.ubo.DataTypes;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapType implements DataType<Map<String, DataType<?>>>, Map<String, DataType<?>> {
    private Map<String, DataType<?>> obj;

    public MapType() {
        obj = new HashMap<>();
    }

    public MapType(Map<String, DataType<?>> map) {
        setValue(map);
    }

    public MapType(String key, DataType<?> value) {
        obj = new HashMap<>();
        obj.put(key, value);
    }

    @Override
    public Map<String, DataType<?>> getValue() {
        return obj;
    }

    @Override
    public void setValue(Map<String, DataType<?>> obj) {
        this.obj = obj;
    }

    public Set<String> keys() {
        return obj.keySet();
    }

    public Set<Entry<String, DataType<?>>> entries() {
        return obj.entrySet();
    }

    public @NotNull Collection<DataType<?>> values() {
        return obj.values();
    }

    @Override
    public @NotNull Set<Entry<String, DataType<?>>> entrySet() {
        return obj.entrySet();
    }

    @Override
    public int id() {
        return DataTypes.MAP;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(obj.size());
        for (Entry<String, DataType<?>> e : obj.entrySet()) {
            output.writeUTF(e.getKey());
            DataType<?> value = e.getValue();
            output.writeByte(value.id());
            value.write(output);
        }
    }

    public static MapType read(DataInput input) throws IOException {
        int len = input.readInt();
        Map<String, DataType<?>> map = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            String key = input.readUTF();
            int id = input.readUnsignedByte();
            map.put(key, DataTypeRegistry.read(id, input));
        }

        return new MapType(map);
    }

    public boolean contains(String key, int type) {
        DataType<?> data = obj.get(key);

        return data != null && data.id() == type;
    }

    @SafeVarargs
    public final <T extends DataType<?>> boolean contains(String key, T... type) {
        DataType<?> data = obj.get(key);

        return data != null && type.getClass().getComponentType().isAssignableFrom(data.getClass());
    }

    public DataType<?> put(String key, DataType<?> dataType) {
        return obj.put(key, dataType);
    }

    @Override
    @Deprecated
    public DataType<?> remove(Object key) {
        return obj.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends DataType<?>> m) {
        obj.putAll(m);
    }

    public void putByte(String key, byte value) {
        put(key, new ByteType(value));
    }

    public void putByte(String key, int value) {
        put(key, new ByteType((byte) value));
    }

    public void putShort(String key, short value) {
        put(key, new ShortType(value));
    }

    public void putShort(String key, int value) {
        put(key, new ShortType((short) value));
    }

    public void putInt(String key, int value) {
        put(key, new IntType(value));
    }

    public void putLong(String key, long value) {
        put(key, new LongType(value));
    }

    public void putBigInt(String key, BigInteger value) {
        put(key, new BigIntType(value));
    }

    public void putFloat(String key, float value) {
        put(key, new FloatType(value));
    }

    public void putDouble(String key, double value) {
        put(key, new DoubleType(value));
    }

    public void putBigDec(String key, BigDecimal value) {
        put(key, new BigDecType(value));
    }

    public void putChar(String key, char value) {
        put(key, new CharType(value));
    }

    public void putBoolean(String key, boolean value) {
        put(key, new BooleanType(value));
    }

    public void putString(String key, String value) {
        put(key, new StringType(value));
    }

    public void putByteArray(String key, byte[] value) {
        put(key, new ByteArrayType(value));
    }

    public void putShortArray(String key, short[] value) {
        put(key, new ShortArrayType(value));
    }

    public void putIntArray(String key, int[] value) {
        put(key, new IntArrayType(value));
    }

    public void putLongArray(String key, long[] value) {
        put(key, new LongArrayType(value));
    }

    public void putFloatArray(String key, float[] value) {
        put(key, new FloatArrayType(value));
    }

    public void putDoubleArray(String key, double[] value) {
        put(key, new DoubleArrayType(value));
    }

    public void putCharArray(String key, char[] value) {
        put(key, new CharArrayType(value));
    }

    public void putBitSet(String key, byte[] value) {
        put(key, new BitSetType(value));
    }

    public void putBitSet(String key, BitSet value) {
        put(key, new BitSetType(value));
    }

    public void putUUID(String key, UUID value) {
        put(key, new UUIDType(value));
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(String key, byte def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof ByteType) {
            return ((ByteType) dataType).getByteValue();
        }
        return def;
    }

    public short getShort(String key) {
        return getShort(key, (byte) 0);
    }

    public short getShort(String key, short def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof ShortType) {
            return ((ShortType) dataType).getShortValue();
        }
        return def;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof IntType) {
            return ((IntType) dataType).getIntValue();
        }
        return def;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof LongType) {
            return ((LongType) dataType).getLongValue();
        }
        return def;
    }

    public BigInteger getBigInt(String key) {
        return getBigInt(key, BigInteger.ZERO);
    }

    public BigInteger getBigInt(String key, BigInteger def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof BigIntType) {
            return ((BigIntType) dataType).getValue();
        }
        return def;
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof FloatType) {
            return ((FloatType) dataType).getFloatValue();
        }
        return def;
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof DoubleType) {
            return ((DoubleType) dataType).getDoubleValue();
        }
        return def;
    }

    public BigDecimal getBigDec(String key) {
        return getBigDec(key, BigDecimal.ZERO);
    }

    public BigDecimal getBigDec(String key, BigDecimal def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof BigDecType) {
            return ((BigDecType) dataType).getValue();
        }
        return def;
    }

    public char getChar(String key) {
        return getChar(key, (char) 0);
    }

    public char getChar(String key, char def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof CharType) {
            return ((CharType) dataType).getCharValue();
        }
        return def;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof BooleanType) {
            return ((BooleanType) dataType).getBooleanValue();
        }
        return def;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof StringType) {
            return ((StringType) dataType).getValue();
        }
        return def;
    }

    public byte[] getByteArray(String key) {
        return getByteArray(key, null);
    }

    public byte[] getByteArray(String key, byte[] def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof ByteArrayType) {
            return ((ByteArrayType) dataType).getValue();
        }
        return def;
    }

    public short[] getShortArray(String key) {
        return getShortArray(key, null);
    }

    public short[] getShortArray(String key, short[] def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof ShortArrayType) {
            return ((ShortArrayType) dataType).getValue();
        }
        return def;
    }

    public int[] getIntArray(String key) {
        return getIntArray(key, null);
    }

    public int[] getIntArray(String key, int[] def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof IntArrayType) {
            return ((IntArrayType) dataType).getValue();
        }
        return def;
    }

    public long[] getLongArray(String key) {
        return getLongArray(key, null);
    }

    public long[] getLongArray(String key, long[] def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof LongArrayType) {
            return ((LongArrayType) dataType).getValue();
        }
        return def;
    }

    public float[] getFloatArray(String key) {
        return getFloatArray(key, null);
    }

    public float[] getFloatArray(String key, float[] def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof FloatArrayType) {
            return ((FloatArrayType) dataType).getValue();
        }
        return def;
    }

    public double[] getDoubleArray(String key) {
        return getDoubleArray(key, null);
    }

    public double[] getDoubleArray(String key, double[] def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof DoubleArrayType) {
            return ((DoubleArrayType) dataType).getValue();
        }
        return def;
    }

    public char[] getCharArray(String key) {
        return getCharArray(key, null);
    }

    public char[] getCharArray(String key, char[] def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof CharArrayType) {
            return ((CharArrayType) dataType).getValue();
        }
        return def;
    }

    public BitSet getBitSet(String key) {
        return getBitSet(key, null);
    }

    public BitSet getBitSet(String key, BitSet def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof BitSetType) {
            return ((BitSetType) dataType).getValue();
        }
        return def;
    }

    public MapType getMap(String key) {
        return getMap(key, null);
    }

    public MapType getMap(String key, MapType def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof MapType) {
            return (MapType) dataType;
        }
        return def;
    }

    @SafeVarargs
    public final <T extends DataType<?>> ListType<T> getList(String key, T... type) {
        return getList(key, new ListType<>(type));
    }

    public <T extends DataType<?>> ListType<T> getList(String key, ListType<T> def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof ListType<?>) {
            ListType<?> obj = (ListType<?>) dataType;
            if (obj.type() != def.type()) {
                return def;
            }
            return obj.listCast(def.componentType);
        }
        return def;
    }

    public UUID getUUID(String key) {
        return getUUID(key, null);
    }

    public UUID getUUID(String key, UUID def) {
        DataType<?> dataType = get(key);
        if (dataType instanceof UUIDType) {
            return ((UUIDType) dataType).getValue();
        }
        return def;
    }

    public DataType<?> get(String key) {
        return obj.get(key);
    }

    public <T extends DataType<?>> T get(String key, @NotNull T def) {
        DataType<?> dataType = get(key);
        if (dataType == null) {
            return def;
        }
        return dataType.cast((Class<T>) def.getClass(), def);
    }

    public boolean remove(String key) {
        return obj.remove(key, get(key));
    }

    public boolean contains(String key) {
        return obj.containsKey(key);
    }

    public boolean containsValue(DataType<?> value) {
        return obj.containsValue(value);
    }

    public DataType<?> pop(String key) {
        return obj.remove(key);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MapType)) return false;
        MapType mapType = (MapType) other;
        return Objects.equals(obj, mapType.obj);
    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    @Override
    public MapType copy() {
        return new MapType(obj.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().copy(), (a, b) -> b)));
    }

    public <R> Map<String, R> mapTo(Function<DataType<?>, R> mapper) {
        return mapTo(mapper, new HashMap<>());
    }

    public <R> Map<String, R> mapTo(Function<DataType<?>, R> mapper, Map<String, R> map) {
        for (Entry<String, DataType<?>> entry : obj.entrySet()) {
            if (map.put(entry.getKey(), mapper.apply(entry.getValue())) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }

    public <R> List<R> reduce(BiFunction<String, DataType<?>, R> reducer) {
        return reduce(reducer, new ArrayList<>());
    }

    public <R> List<R> reduce(BiFunction<String, DataType<?>, R> reducer, List<R> list) {
        for (Entry<String, DataType<?>> entry : obj.entrySet()) {
            list.add(reducer.apply(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    @Override
    public String writeUso() {
        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<String, DataType<?>> entry : obj.entrySet()) {
            builder.append("\"").append(entry.getKey().replace("\"", "\\\"")).append("\": ").append(entry.getValue().writeUso()).append(", ");
        }

        if (this.obj.size() > 1) {
            return builder.substring(0, builder.length() - 2) + "}";
        }

        return builder + "}";
    }

    public int size() {
        return obj.size();
    }

    public void clear() {
        obj.clear();
    }

    @Override
    public @NotNull Set<String> keySet() {
        return Collections.emptySet();
    }

    public boolean isEmpty() {
        return obj.isEmpty();
    }

    @Override
    @Deprecated
    public boolean containsKey(Object key) {
        return obj.containsKey(key);
    }

    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        return obj.containsValue(value);
    }

    @Override
    @Deprecated
    public DataType<?> get(Object key) {
        return obj.get(key);
    }

    @Override
    public String toString() {
        return writeUso();
    }
}
