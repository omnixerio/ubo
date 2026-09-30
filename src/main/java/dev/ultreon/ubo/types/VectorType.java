package dev.ultreon.ubo.types;

public interface VectorType<V> extends Iterable<V> {
    /**
     * The element type used in USO to mark a map element of a vector, eg. {@code M} in {@code v2:M[{"a":1i},{"a":2i}]}.
     */
    String USO_ELEMENT_MAP = "M";

    /**
     * The element type used in USO to mark a list element of a vector, eg. {@code L} in {@code v2:L[[1i],[2i,3i]]}.
     */
    String USO_ELEMENT_LIST = "L";

    /**
     * The element type used in USO to mark an array element of a vector, followed by the array's own element type,
     * eg. {@code Ai} in {@code v2:Ai[(1,2),(3,4)]}.
     */
    String USO_ELEMENT_ARRAY = "A";

    /**
     * The element type used in USO to mark a vector element of a vector, eg. {@code V} in {@code v2:V[v2:i[1,2],v2:i[3,4]]}.
     */
    String USO_ELEMENT_VECTOR = "V";

    int size();

    void fill(V value);

    V get(int index);

    void set(int index, V value);

    /**
     * Whether the given element type marks a composite type. Map, list and vector elements are written as complete USO
     * values, including their own type marker, eg. {@code {"a":1i}} in {@code v2:M[{"a":1i},{"a":2i}]}, while array
     * elements only carry their values, eg. {@code (1,2)} in {@code v2:Ai[(1,2),(3,4)]}.
     */
    static boolean isCompositeElementType(String elementType) {
        return elementType.startsWith(USO_ELEMENT_MAP)
                || elementType.startsWith(USO_ELEMENT_LIST)
                || elementType.startsWith(USO_ELEMENT_ARRAY)
                || elementType.startsWith(USO_ELEMENT_VECTOR);
    }

    /**
     * The element type used in USO to mark the element type of a vector, eg. {@code i} in {@code v2:i[2,4]}.
     */
    static String writeUsoElementType(DataType<?> type) {
        if (type instanceof ByteType) return "b";
        if (type instanceof ShortType) return "s";
        if (type instanceof IntType) return "i";
        if (type instanceof LongType) return "l";
        if (type instanceof FloatType) return "f";
        if (type instanceof DoubleType) return "d";
        if (type instanceof BigIntType) return "I";
        if (type instanceof BigDecType) return "D";
        if (type instanceof CharType) return "c";
        if (type instanceof StringType) return "t";
        if (type instanceof BooleanType) return "z";
        if (type instanceof MapType) return USO_ELEMENT_MAP;
        if (type instanceof ListType<?>) return USO_ELEMENT_LIST;
        if (type instanceof VectorType<?>) return USO_ELEMENT_VECTOR;
        if (type instanceof ArrayType<?, ?>) return USO_ELEMENT_ARRAY + writeUsoArrayType(type);
        throw new IllegalArgumentException("Can't write vector of element type: " + type);
    }

    /**
     * The element type used in USO to mark the value type of an array element of a vector,
     * eg. {@code i} in {@code v2:Ai[(1,2),(3,4)]}.
     */
    static char writeUsoArrayType(DataType<?> type) {
        if (type instanceof ByteArrayType) return 'b';
        if (type instanceof ShortArrayType) return 's';
        if (type instanceof IntArrayType) return 'i';
        if (type instanceof LongArrayType) return 'l';
        if (type instanceof FloatArrayType) return 'f';
        if (type instanceof DoubleArrayType) return 'd';
        if (type instanceof CharArrayType) return 'c';
        if (type instanceof BooleanArrayType) return 'z';
        throw new IllegalArgumentException("Can't write vector of array element type: " + type);
    }

    /**
     * Writes a single vector element without its type marker, eg. {@code 2} in {@code v2:i[2,4]}.
     */
    static String writeUsoElement(DataType<?> type) {
        String elementType = writeUsoElementType(type);
        char valueType = elementType.charAt(elementType.length() - 1);
        if (type instanceof ArrayType<?, ?>) return writeUsoArrayElement((ArrayType<?, ?>) type);
        if (valueType == 'c' || valueType == 't' || isCompositeElementType(elementType)) return type.writeUso();
        return String.valueOf(type.getValue());
    }

    /**
     * Writes a single array element of a vector, eg. {@code (1,2)} in {@code v2:Ai[(1,2),(3,4)]}.
     */
    static String writeUsoArrayElement(ArrayType<?, ?> type) {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0, size = type.size(); i < size; i++) {
            if (i > 0) builder.append(',');

            Object value = type.get(i);
            if (value instanceof Character) {
                char character = (Character) value;
                builder.append('\'').append(character == '\'' ? "\\'" : String.valueOf(character)).append('\'');
            } else {
                builder.append(value);
            }
        }

        return builder.append(')').toString();
    }

    /**
     * Writes a whole vector as USO, eg. {@code v2:i[2,4]}.
     */
    static String writeUsoVector(VectorType<? extends DataType<?>> vector) {
        int size = vector.size();
        StringBuilder builder = new StringBuilder("v").append(size).append(':')
                .append(writeUsoElementType(vector.get(0))).append('[');
        for (int i = 0; i < size; i++) {
            if (i > 0) builder.append(',');
            builder.append(writeUsoElement(vector.get(i)));
        }
        return builder.append(']').toString();
    }
}
