package dev.ultreon.ubo;

import dev.ultreon.ubo.types.*;
import dev.ultreon.ubo.types.vector.*;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataTypeRegistry {
    private static final Map<Integer, DataReader<? extends DataType<?>>> READERS = new HashMap<>();
    private static final Map<Integer, Class<? extends DataType<?>>> TYPES = new HashMap<>();
    private static final Map<String, Integer> ID_MAP = new HashMap<>();

    static {
        init();
    }

    public static void init() {
        register(DataTypes.BYTE, ByteType::read);
        register(DataTypes.SHORT, ShortType::read);
        register(DataTypes.INT, IntType::read);
        register(DataTypes.LONG, LongType::read);
        register(DataTypes.BIG_INT, BigIntType::read);
        register(DataTypes.FLOAT, FloatType::read);
        register(DataTypes.DOUBLE, DoubleType::read);
        register(DataTypes.BIG_DEC, BigDecType::read);
        register(DataTypes.CHAR, CharType::read);
        register(DataTypes.BOOLEAN, BooleanType::read);
        register(DataTypes.STRING, StringType::read);
        register(DataTypes.LIST, ListType::read);
        register(DataTypes.MAP, MapType::read);
        register(DataTypes.BYTE_ARRAY, ByteArrayType::read);
        register(DataTypes.SHORT_ARRAY, ShortArrayType::read);
        register(DataTypes.INT_ARRAY, IntArrayType::read);
        register(DataTypes.LONG_ARRAY, LongArrayType::read);
        register(DataTypes.FLOAT_ARRAY, FloatArrayType::read);
        register(DataTypes.DOUBLE_ARRAY, DoubleArrayType::read);
        register(DataTypes.CHAR_ARRAY, CharArrayType::read);
        register(DataTypes.BOOLEAN_ARRAY, BooleanArrayType::read);
        register(DataTypes.UUID, UUIDType::read);
        register(DataTypes.BIT_SET, BitSetType::read);
        register(DataTypes.VECTOR_2, Vector2Type::read);
        register(DataTypes.VECTOR_3, Vector3Type::read);
        register(DataTypes.VECTOR_4, Vector4Type::read);
        register(DataTypes.INT_VECTOR_2, IntVector2Type::read, IntVector2Type.class);
        register(DataTypes.INT_VECTOR_3, IntVector3Type::read, IntVector3Type.class);
        register(DataTypes.INT_VECTOR_4, IntVector4Type::read, IntVector4Type.class);
        register(DataTypes.LONG_VECTOR_2, LongVector2Type::read, LongVector2Type.class);
        register(DataTypes.LONG_VECTOR_3, LongVector3Type::read, LongVector3Type.class);
        register(DataTypes.LONG_VECTOR_4, LongVector4Type::read, LongVector4Type.class);
        register(DataTypes.FLOAT_VECTOR_2, FloatVector2Type::read, FloatVector2Type.class);
        register(DataTypes.FLOAT_VECTOR_3, FloatVector3Type::read, FloatVector3Type.class);
        register(DataTypes.FLOAT_VECTOR_4, FloatVector4Type::read, FloatVector4Type.class);
        register(DataTypes.DOUBLE_VECTOR_2, DoubleVector2Type::read, DoubleVector2Type.class);
        register(DataTypes.DOUBLE_VECTOR_3, DoubleVector3Type::read, DoubleVector3Type.class);
        register(DataTypes.DOUBLE_VECTOR_4, DoubleVector4Type::read, DoubleVector4Type.class);
    }

    public static void clear() {
        READERS.clear();
        TYPES.clear();
        ID_MAP.clear();
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T extends DataType<?>> void register(int id, DataReader<T> reader, T... type) {
        Class<? extends T> componentType = (Class<? extends T>) type.getClass().getComponentType();
        READERS.put(id, reader);
        TYPES.put(id, componentType);
        ID_MAP.put(componentType.getName(), id);
    }

    public static <T extends DataType<?>> void register(int id, DataReader<T> reader, Class<T> type) {
        READERS.put(id, reader);
        TYPES.put(id, type);
        ID_MAP.put(type.getName(), id);
    }

    public static DataType<?> read(int id, DataInput input) throws IOException {
        DataReader<? extends DataType<?>> reader = READERS.get(id);

        if (reader == null)
            throw new DataTypeException("Unknown datatype id: " + id);

        return reader.read(input);
    }

    public static Class<? extends DataType<?>> getType(int id) {
        return TYPES.get(id);
    }

    public static int getId(Class<? extends DataType<?>> dataType) {
        return ID_MAP.get(dataType.getName());
    }

    public static int getIdOrThrow(Class<? extends DataType<?>> dataType) {
        String name = dataType.getName();
        Integer id = ID_MAP.get(name);

        if (id == null)
            throw new IllegalArgumentException("No type registered for " + name);

        return id;
    }
}
