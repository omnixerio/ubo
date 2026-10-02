package dev.ultreon.tests.data;

import dev.ultreon.ubo.DataIo;
import dev.ultreon.ubo.types.*;
import dev.ultreon.ubo.types.vector.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.UUID;

import static dev.ultreon.tests.data.Utils.file;

class UsoReadWriteTests {
    @Test
    @DisplayName("MapTypes")
    void readWriteMap() throws IOException {
        MapType type = Utils.createExampleMap();

        String uso;
        System.out.println("Writing map data as USO...");
        uso = DataIo.toUso(type);

        Files.write(file("map.uso").toPath(), uso.getBytes(StandardCharsets.UTF_8));

        MapType readMap;
        try {
            System.out.println("Reading map data from USO...");
            readMap = DataIo.fromUso(uso);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(type, readMap);
    }

    @Test
    @DisplayName("ListTypes")
    void readWriteList() throws IOException {
        ListType<StringType> list = new ListType<>();
        list.add(new StringType("Apple"));
        list.add(new StringType("Banana"));
        list.add(new StringType("Pear"));
        list.add(new StringType("Orange"));
        list.add(new StringType("Watermelon"));

        System.out.println("Writing normal list data...");
        String uso = DataIo.toUso(list);

        Files.write(file("list.uso").toPath(), uso.getBytes(StandardCharsets.UTF_8));

        ListType<StringType> readList;
        try {
            System.out.println("Reading normal list data...");
            readList = DataIo.fromUso(uso);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(readList, list);
    }
    
    @Test
    @DisplayName("PrimitiveTypes")
    void readWritePrimitive() {
        Assertions.assertEquals("\"Apple\"", DataIo.toUso(new StringType("Apple")));
        Assertions.assertEquals("true", DataIo.toUso(new BooleanType(true)));
        Assertions.assertEquals("5b", DataIo.toUso(new ByteType(5)));
        Assertions.assertEquals("5s", DataIo.toUso(new ShortType(5)));
        Assertions.assertEquals("5i", DataIo.toUso(new IntType(5)));
        Assertions.assertEquals("5l", DataIo.toUso(new LongType(5)));
        Assertions.assertEquals("5.5f", DataIo.toUso(new FloatType(5.5f)));
        Assertions.assertEquals("5.5d", DataIo.toUso(new DoubleType(5.5)));
        Assertions.assertEquals("'a'", DataIo.toUso(new CharType('a')));
        Assertions.assertEquals("1I", DataIo.toUso(new BigIntType(BigInteger.ONE)));
        Assertions.assertEquals("1D", DataIo.toUso(new BigDecType(BigDecimal.ONE)));
        Assertions.assertEquals("<00000000-0000-0000-0000-000000000000>", DataIo.toUso(new UUIDType(UUID.fromString("00000000-0000-0000-0000-000000000000"))));
        Assertions.assertEquals("x;", DataIo.toUso(new BitSetType(new BitSet())));
        Assertions.assertEquals("[\"Apple\"]", DataIo.toUso(new ListType<>(new StringType("Apple"))));
        Assertions.assertEquals("{}", DataIo.toUso(new MapType()));
        Assertions.assertEquals("v2:i[2,4]", DataIo.toUso(new Vector2Type<>(new IntType(2), new IntType(4))));
        Assertions.assertEquals("v3:i[2,4,6]", DataIo.toUso(new Vector3Type<>(new IntType(2), new IntType(4), new IntType(6))));
        Assertions.assertEquals("v4:i[2,4,6,8]", DataIo.toUso(new Vector4Type<>(new IntType(2), new IntType(4), new IntType(6), new IntType(8))));
    }

    @Test
    @DisplayName("VectorTypes")
    void readWriteVector() throws IOException {
        Vector2Type<IntType> vec2 = new Vector2Type<>(new IntType(2), new IntType(4));
        Vector3Type<IntType> vec3 = new Vector3Type<>(new IntType(2), new IntType(4), new IntType(6));
        Vector4Type<IntType> vec4 = new Vector4Type<>(new IntType(2), new IntType(4), new IntType(6), new IntType(8));

        Assertions.assertEquals("v2:i[2,4]", DataIo.toUso(vec2));
        Assertions.assertEquals("v3:i[2,4,6]", DataIo.toUso(vec3));
        Assertions.assertEquals("v4:i[2,4,6,8]", DataIo.toUso(vec4));

        Vector2Type<?> readVec2 = DataIo.fromUso("v2:i[2,4]");
        Vector3Type<?> readVec3 = DataIo.fromUso("v3:i[2,4,6]");
        Vector4Type<?> readVec4 = DataIo.fromUso("v4:i[2,4,6,8]");

        Assertions.assertEquals(vec2, readVec2);
        Assertions.assertEquals(vec3, readVec3);
        Assertions.assertEquals(vec4, readVec4);

        // Whitespace between the elements is optional.
        Assertions.assertEquals(vec2, DataIo.fromUso("v2:i[ 2, 4 ]"));
        Assertions.assertEquals(vec3, DataIo.fromUso("v3:i[2, 4,6]"));
    }

    @Test
    @DisplayName("VectorElementTypes")
    void readWriteVectorElementTypes() throws IOException {
        Assertions.assertEquals("v2:b[1,2]", DataIo.toUso(new Vector2Type<>(new ByteType((byte) 1), new ByteType((byte) 2))));
        Assertions.assertEquals("v2:s[1,2]", DataIo.toUso(new Vector2Type<>(new ShortType((short) 1), new ShortType((short) 2))));
        Assertions.assertEquals("v2:i[1,2]", DataIo.toUso(new Vector2Type<>(new IntType(1), new IntType(2))));
        Assertions.assertEquals("v2:l[1,2]", DataIo.toUso(new Vector2Type<>(new LongType(1L), new LongType(2L))));
        Assertions.assertEquals("v2:f[1.5,2.5]", DataIo.toUso(new Vector2Type<>(new FloatType(1.5f), new FloatType(2.5f))));
        Assertions.assertEquals("v2:d[1.5,2.5]", DataIo.toUso(new Vector2Type<>(new DoubleType(1.5), new DoubleType(2.5))));
        Assertions.assertEquals("v2:I[1,2]", DataIo.toUso(new Vector2Type<>(new BigIntType(BigInteger.ONE), new BigIntType(BigInteger.valueOf(2)))));
        Assertions.assertEquals("v2:D[1.5,2.5]", DataIo.toUso(new Vector2Type<>(new BigDecType(new BigDecimal("1.5")), new BigDecType(new BigDecimal("2.5")))));
        Assertions.assertEquals("v2:c['a','b']", DataIo.toUso(new Vector2Type<>(new CharType('a'), new CharType('b'))));
        Assertions.assertEquals("v2:t[\"Apple\",\"Banana\"]", DataIo.toUso(new Vector2Type<>(new StringType("Apple"), new StringType("Banana"))));
        Assertions.assertEquals("v2:z[true,false]", DataIo.toUso(new Vector2Type<>(new BooleanType(true), new BooleanType(false))));

        Vector2Type<?> readVec2 = DataIo.fromUso("v2:b[1,2]");
        Assertions.assertEquals(new Vector2Type<>(new ByteType((byte) 1), new ByteType((byte) 2)), readVec2);

        readVec2 = DataIo.fromUso("v2:s[1,2]");
        Assertions.assertEquals(new Vector2Type<>(new ShortType((short) 1), new ShortType((short) 2)), readVec2);

        readVec2 = DataIo.fromUso("v2:i[1,2]");
        Assertions.assertEquals(new Vector2Type<>(new IntType(1), new IntType(2)), readVec2);

        readVec2 = DataIo.fromUso("v2:l[1,2]");
        Assertions.assertEquals(new Vector2Type<>(new LongType(1L), new LongType(2L)), readVec2);

        readVec2 = DataIo.fromUso("v2:f[1.5,2.5]");
        Assertions.assertEquals(new Vector2Type<>(new FloatType(1.5f), new FloatType(2.5f)), readVec2);

        readVec2 = DataIo.fromUso("v2:d[1.5,2.5]");
        Assertions.assertEquals(new Vector2Type<>(new DoubleType(1.5), new DoubleType(2.5)), readVec2);

        readVec2 = DataIo.fromUso("v2:I[1,2]");
        Assertions.assertEquals(new Vector2Type<>(new BigIntType(BigInteger.ONE), new BigIntType(BigInteger.valueOf(2))), readVec2);

        readVec2 = DataIo.fromUso("v2:D[1.5,2.5]");
        Assertions.assertEquals(new Vector2Type<>(new BigDecType(new BigDecimal("1.5")), new BigDecType(new BigDecimal("2.5"))), readVec2);

        readVec2 = DataIo.fromUso("v2:c['a','b']");
        Assertions.assertEquals(new Vector2Type<>(new CharType('a'), new CharType('b')), readVec2);

        readVec2 = DataIo.fromUso("v2:t[\"Apple\",\"Banana\"]");
        Assertions.assertEquals(new Vector2Type<>(new StringType("Apple"), new StringType("Banana")), readVec2);

        readVec2 = DataIo.fromUso("v2:z[true,false]");
        Assertions.assertEquals(new Vector2Type<>(new BooleanType(true), new BooleanType(false)), readVec2);
    }

    @Test
    @DisplayName("NumericVectorTypes")
    void readWriteNumericVector() throws IOException {
        IntVector2Type ivec2 = new IntVector2Type(2, 4);
        IntVector3Type ivec3 = new IntVector3Type(2, 4, 6);
        IntVector4Type ivec4 = new IntVector4Type(2, 4, 6, 8);

        Assertions.assertEquals("ivec2[2,4]", DataIo.toUso(ivec2));
        Assertions.assertEquals("ivec3[2,4,6]", DataIo.toUso(ivec3));
        Assertions.assertEquals("ivec4[2,4,6,8]", DataIo.toUso(ivec4));

        Assertions.assertEquals(ivec2, DataIo.fromUso("ivec2[2,4]"));
        Assertions.assertEquals(ivec3, DataIo.fromUso("ivec3[2,4,6]"));
        Assertions.assertEquals(ivec4, DataIo.fromUso("ivec4[2,4,6,8]"));

        // Whitespace between the elements is optional.
        Assertions.assertEquals(ivec2, DataIo.fromUso("ivec2[ 2, 4 ]"));
        Assertions.assertEquals(ivec3, DataIo.fromUso("ivec3[2, 4,6]"));
    }

    @Test
    @DisplayName("NumericVectorElementTypes")
    void readWriteNumericVectorElementTypes() throws IOException {
        Assertions.assertEquals("lvec2[1,2]", DataIo.toUso(new LongVector2Type(1L, 2L)));
        Assertions.assertEquals("fvec2[1.5,2.5]", DataIo.toUso(new FloatVector2Type(1.5f, 2.5f)));
        Assertions.assertEquals("dvec2[1.5,2.5]", DataIo.toUso(new DoubleVector2Type(1.5, 2.5)));

        Assertions.assertEquals("lvec3[1,2,3]", DataIo.toUso(new LongVector3Type(1L, 2L, 3L)));
        Assertions.assertEquals("fvec3[1.5,2.5,3.5]", DataIo.toUso(new FloatVector3Type(1.5f, 2.5f, 3.5f)));
        Assertions.assertEquals("dvec3[1.5,2.5,3.5]", DataIo.toUso(new DoubleVector3Type(1.5, 2.5, 3.5)));

        Assertions.assertEquals("lvec4[1,2,3,4]", DataIo.toUso(new LongVector4Type(1L, 2L, 3L, 4L)));
        Assertions.assertEquals("fvec4[1.5,2.5,3.5,4.5]", DataIo.toUso(new FloatVector4Type(1.5f, 2.5f, 3.5f, 4.5f)));
        Assertions.assertEquals("dvec4[1.5,2.5,3.5,4.5]", DataIo.toUso(new DoubleVector4Type(1.5, 2.5, 3.5, 4.5)));

        Assertions.assertEquals(new LongVector2Type(1L, 2L), DataIo.fromUso("lvec2[1,2]"));
        Assertions.assertEquals(new FloatVector2Type(1.5f, 2.5f), DataIo.fromUso("fvec2[1.5,2.5]"));
        Assertions.assertEquals(new DoubleVector2Type(1.5, 2.5), DataIo.fromUso("dvec2[1.5,2.5]"));

        Assertions.assertEquals(new LongVector3Type(1L, 2L, 3L), DataIo.fromUso("lvec3[1,2,3]"));
        Assertions.assertEquals(new FloatVector3Type(1.5f, 2.5f, 3.5f), DataIo.fromUso("fvec3[1.5,2.5,3.5]"));
        Assertions.assertEquals(new DoubleVector3Type(1.5, 2.5, 3.5), DataIo.fromUso("dvec3[1.5,2.5,3.5]"));

        Assertions.assertEquals(new LongVector4Type(1L, 2L, 3L, 4L), DataIo.fromUso("lvec4[1,2,3,4]"));
        Assertions.assertEquals(new FloatVector4Type(1.5f, 2.5f, 3.5f, 4.5f), DataIo.fromUso("fvec4[1.5,2.5,3.5,4.5]"));
        Assertions.assertEquals(new DoubleVector4Type(1.5, 2.5, 3.5, 4.5), DataIo.fromUso("dvec4[1.5,2.5,3.5,4.5]"));
    }

    @Test
    @DisplayName("NumericVectorSpecialNumbers")
    void readWriteNumericVectorSpecialNumbers() throws IOException {
        IntVector2Type negatives = new IntVector2Type(-2, 4);
        Assertions.assertEquals("ivec2[-2,4]", DataIo.toUso(negatives));
        Assertions.assertEquals(negatives, DataIo.fromUso("ivec2[-2,4]"));

        DoubleVector3Type decimals = new DoubleVector3Type(-1.5, 0.0, 2.5E-3);
        Assertions.assertEquals("dvec3[-1.5,0.0,0.0025]", DataIo.toUso(decimals));
        Assertions.assertEquals(decimals, DataIo.fromUso("dvec3[-1.5,0.0,0.0025]"));
        Assertions.assertEquals(decimals, DataIo.fromUso("dvec3[-1.5,0.0,2.5E-3]"));

        LongVector3Type bounds = new LongVector3Type(Long.MIN_VALUE, 0L, Long.MAX_VALUE);
        Assertions.assertEquals("lvec3[" + Long.MIN_VALUE + ",0," + Long.MAX_VALUE + "]", DataIo.toUso(bounds));
        Assertions.assertEquals(bounds, DataIo.fromUso("lvec3[" + Long.MIN_VALUE + ",0," + Long.MAX_VALUE + "]"));
    }

    @Test
    @DisplayName("InvalidNumericVectors")
    void readInvalidNumericVector() throws IOException {
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("ivec2[1]"), "too few elements");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("ivec2[1,2,3]"), "too many elements");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("ivec5[1,2]"), "unknown vector count");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("ivec2 1,2]"), "missing bracket");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("ivec2[1 2]"), "missing comma");

        // A boolean starts with the same character as a float vector.
        Assertions.assertEquals(new BooleanType(false), DataIo.fromUso("false"));
        Assertions.assertEquals(new FloatVector2Type(1.5f, 2.5f), DataIo.fromUso("fvec2[1.5,2.5]"));
    }

    @Test
    @DisplayName("NumericVectorLists")
    void readWriteNumericVectorList() throws IOException {
        ListType<IntVector2Type> list = new ListType<>(IntVector2Type.class);
        list.add(new IntVector2Type(1, 2));
        list.add(new IntVector2Type(3, 4));

        String uso = DataIo.toUso(list);
        Files.write(file("ivec2-list.uso").toPath(), uso.getBytes(StandardCharsets.UTF_8));

        Assertions.assertEquals("[ivec2[1,2], ivec2[3,4]]", uso);
        Assertions.assertEquals(list, DataIo.fromUso(uso));
    }

    @Test
    @DisplayName("VectorSpecialNumbers")
    void readWriteVectorSpecialNumbers() throws IOException {
        Vector2Type<IntType> negatives = new Vector2Type<>(new IntType(-2), new IntType(4));
        Assertions.assertEquals("v2:i[-2,4]", DataIo.toUso(negatives));
        Assertions.assertEquals(negatives, DataIo.fromUso("v2:i[-2,4]"));

        Vector3Type<DoubleType> decimals = new Vector3Type<>(new DoubleType(-1.5), new DoubleType(0.0), new DoubleType(2.5E-3));
        Assertions.assertEquals("v3:d[-1.5,0.0,0.0025]", DataIo.toUso(decimals));
        Assertions.assertEquals(decimals, DataIo.fromUso("v3:d[-1.5,0.0,0.0025]"));
        Assertions.assertEquals(decimals, DataIo.fromUso("v3:d[-1.5,0.0,2.5E-3]"));

        Vector2Type<BigDecType> bigDecimals = new Vector2Type<>(new BigDecType(new BigDecimal("1.5")), new BigDecType(new BigDecimal("2.5")));
        Assertions.assertEquals(bigDecimals, DataIo.fromUso("v2:D[1.5,2.5]"));
    }

    @Test
    @DisplayName("InvalidVectors")
    void readInvalidVector() {
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:x[1,2]"), "unknown element type");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v5:i[1,2]"), "unknown vector count");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2i[1,2]"), "missing element type");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:i[1]"), "too few elements");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:i[1,2,3]"), "too many elements");
    }

    @Test
    @DisplayName("VectorMapElements")
    void readWriteVectorMapElements() throws IOException {
        MapType first = new MapType();
        first.putInt("x", 2);

        MapType second = new MapType();
        second.putInt("x", 4);

        Vector2Type<MapType> vector = new Vector2Type<>(first, second);
        Assertions.assertEquals("v2:M[{\"x\": 2i, },{\"x\": 4i, }]", DataIo.toUso(vector));
        Assertions.assertEquals(vector, DataIo.fromUso("v2:M[{\"x\": 2i, },{\"x\": 4i, }]"));
        Assertions.assertEquals(vector, DataIo.fromUso("v2:M[{\"x\": 2i},{\"x\": 4i}]"));

        MapType multiple = new MapType();
        multiple.putInt("x", 2);
        multiple.putString("name", "Apple");

        Vector2Type<MapType> withMultipleEntries = new Vector2Type<>(multiple, multiple.copy());
        Assertions.assertEquals(withMultipleEntries, DataIo.fromUso(DataIo.toUso(withMultipleEntries)));

        Vector3Type<MapType> nested = new Vector3Type<>(first, second, first.copy());
        Assertions.assertEquals(nested, DataIo.fromUso(DataIo.toUso(nested)));
    }

    @Test
    @DisplayName("VectorListElements")
    void readWriteVectorListElements() throws IOException {
        ListType<IntType> first = new ListType<>(new IntType(1), new IntType(2));
        ListType<StringType> second = new ListType<>(new StringType("Apple"), new StringType("Banana"));

        Vector2Type<ListType<?>> vector = new Vector2Type<>(first, second);
        Assertions.assertEquals("v2:L[[1i, 2i],[\"Apple\", \"Banana\"]]", DataIo.toUso(vector));
        Assertions.assertEquals(vector, DataIo.fromUso("v2:L[[1i,2i],[\"Apple\",\"Banana\"]]"));

        Vector4Type<ListType<IntType>> uniform = new Vector4Type<>(
                new ListType<>(new IntType(1), new IntType(2)),
                new ListType<>(new IntType(3), new IntType(4)),
                new ListType<>(new IntType(5), new IntType(6)),
                new ListType<>(new IntType(7), new IntType(8)));
        Assertions.assertEquals("v4:L[[1i, 2i],[3i, 4i],[5i, 6i],[7i, 8i]]", DataIo.toUso(uniform));
        Assertions.assertEquals(uniform, DataIo.fromUso(DataIo.toUso(uniform)));
    }

    @Test
    @DisplayName("VectorArrayElements")
    void readWriteVectorArrayElements() throws IOException {
        Vector2Type<IntArrayType> ints = new Vector2Type<>(new IntArrayType(new int[]{1, 2}), new IntArrayType(new int[]{3, 4}));
        Assertions.assertEquals("v2:Ai[(1,2),(3,4)]", DataIo.toUso(ints));
        Assertions.assertEquals(ints, DataIo.fromUso("v2:Ai[(1, 2),(3, 4)]"));

        Vector2Type<ByteArrayType> bytes = new Vector2Type<>(new ByteArrayType(new byte[]{1, -2}), new ByteArrayType(new byte[]{3}));
        Assertions.assertEquals("v2:Ab[(1,-2),(3)]", DataIo.toUso(bytes));
        Assertions.assertEquals(bytes, DataIo.fromUso("v2:Ab[(1,-2),(3)]"));

        Vector3Type<CharArrayType> chars = new Vector3Type<>(
                new CharArrayType(new char[]{'a', '\''}),
                new CharArrayType(new char[]{'b'}),
                new CharArrayType(new char[]{'c', 'd'}));
        Assertions.assertEquals("v3:Ac[('a','\\''),('b'),('c','d')]", DataIo.toUso(chars));
        Assertions.assertEquals(chars, DataIo.fromUso(DataIo.toUso(chars)));

        Vector2Type<BooleanArrayType> booleans = new Vector2Type<>(new BooleanArrayType(new boolean[]{true, false}), new BooleanArrayType(new boolean[]{false}));
        Assertions.assertEquals("v2:Az[(true,false),(false)]", DataIo.toUso(booleans));
        Assertions.assertEquals(booleans, DataIo.fromUso(DataIo.toUso(booleans)));

        Vector2Type<DoubleArrayType> doubles = new Vector2Type<>(new DoubleArrayType(new double[]{-1.5, 2.0}), new DoubleArrayType(new double[]{0.0}));
        Assertions.assertEquals("v2:Ad[(-1.5,2.0),(0.0)]", DataIo.toUso(doubles));
        Assertions.assertEquals(doubles, DataIo.fromUso(DataIo.toUso(doubles)));

        Vector2Type<IntArrayType> empty = new Vector2Type<>(new IntArrayType(new int[0]), new IntArrayType(new int[]{1}));
        Assertions.assertEquals("v2:Ai[(),(1)]", DataIo.toUso(empty));
        Assertions.assertEquals(empty, DataIo.fromUso(DataIo.toUso(empty)));
    }

    @Test
    @DisplayName("VectorVectorElements")
    void readWriteVectorVectorElements() throws IOException {
        Vector2Type<Vector2Type<IntType>> vector = new Vector2Type<>(
                new Vector2Type<>(new IntType(1), new IntType(2)),
                new Vector2Type<>(new IntType(3), new IntType(4)));
        Assertions.assertEquals("v2:V[v2:i[1,2],v2:i[3,4]]", DataIo.toUso(vector));
        Assertions.assertEquals(vector, DataIo.fromUso("v2:V[v2:i[1,2],v2:i[3,4]]"));

        Vector2Type<Vector3Type<MapType>> mixed = new Vector2Type<>(
                new Vector3Type<>(new MapType("a", new IntType(1)), new MapType("a", new IntType(2)), new MapType("a", new IntType(3))),
                new Vector3Type<>(new MapType("a", new IntType(4)), new MapType("a", new IntType(5)), new MapType("a", new IntType(6))));
        Assertions.assertEquals(mixed, DataIo.fromUso(DataIo.toUso(mixed)));
    }

    @Test
    @DisplayName("InvalidVectorElementValues")
    void readInvalidVectorElementValues() {
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:M[1i,2i]"), "int instead of map");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:L[1i,2i]"), "int instead of list");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:V[1i,2i]"), "int instead of vector");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:Ai[1i,2i]"), "int instead of array");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:Aq[(1,2),(3,4)]"), "unknown array element type");
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("v2:Ai[(1,\"a\")]"), "mismatching array element types");
    }

    @Test
    @DisplayName("UnsupportedVectorElementTypes")
    void writeUnsupportedVectorElementTypes() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> DataIo.toUso(new Vector2Type<>(new UUIDType(uuid), new UUIDType(uuid))));
    }
}
