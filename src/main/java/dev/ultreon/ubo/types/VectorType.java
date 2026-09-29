package dev.ultreon.ubo.types;

public interface VectorType<V> extends Iterable<V> {
    int size();

    void fill(V value);

    V get(int index);

    void set(int index, V value);

    /**
     * The character used in USO to mark the element type of a vector, eg. {@code i} in {@code v2:i[2,4]}.
     */
    static char writeUsoElementType(DataType<?> type) {
        if (type instanceof ByteType) return 'b';
        if (type instanceof ShortType) return 's';
        if (type instanceof IntType) return 'i';
        if (type instanceof LongType) return 'l';
        if (type instanceof FloatType) return 'f';
        if (type instanceof DoubleType) return 'd';
        if (type instanceof BigIntType) return 'I';
        if (type instanceof BigDecType) return 'D';
        if (type instanceof CharType) return 'c';
        if (type instanceof StringType) return 't';
        if (type instanceof BooleanType) return 'z';
        throw new IllegalArgumentException("Can't write vector of element type: " + type);
    }

    /**
     * Writes a single vector element without its type marker, eg. {@code 2} in {@code v2:i[2,4]}.
     */
    static String writeUsoElement(DataType<?> type) {
        char typeMarker = writeUsoElementType(type);
        if (typeMarker == 'c' || typeMarker == 't') return type.writeUso();
        return String.valueOf(type.getValue());
    }
}
