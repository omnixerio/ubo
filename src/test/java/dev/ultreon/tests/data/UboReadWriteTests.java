package dev.ultreon.tests.data;

import dev.ultreon.ubo.DataIo;
import dev.ultreon.ubo.types.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

import static dev.ultreon.tests.data.Utils.file;

class UboReadWriteTests {
    @Test
    @DisplayName("MapTypes")
    void readWriteMap() {
        MapType type = Utils.createExampleMap();

        try {
            System.out.println("Writing map data as normal UBO...");
            DataIo.write(type, file("map-normal.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println("Writing map data as compressed UBO...");
            DataIo.writeCompressed(type, file("map-compressed.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MapType readMap;
        try {
            System.out.println("Reading map data from normal UBO...");
            readMap = DataIo.read(file("map-normal.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(readMap, type);

        MapType readCompressedMap;
        try {
            System.out.println("Reading map data from compressed UBO...");
            readCompressedMap = DataIo.readCompressed(file("map-compressed.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(readCompressedMap, type);
    }

    @Test
    @DisplayName("ListTypes")
    void readWriteList() {
        ListType<StringType> list = new ListType<>();
        list.add(new StringType("Apple"));
        list.add(new StringType("Banana"));
        list.add(new StringType("Pear"));
        list.add(new StringType("Orange"));
        list.add(new StringType("Watermelon"));

        try {
            System.out.println("Writing normal list data...");
            DataIo.write(list, file("list-normal.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println("Writing compressed list data...");
            DataIo.writeCompressed(list, file("list-compressed.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ListType<StringType> readList;
        try {
            System.out.println("Reading normal list data...");
            readList = DataIo.read(file("list-normal.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(readList, list);

        ListType<StringType> readCompressedList;
        try {
            System.out.println("Reading compressed list data...");
            readCompressedList = DataIo.readCompressed(file("list-compressed.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(readCompressedList, list);
    }

    @Test
    @DisplayName("VectorTypes")
    void readWriteVectors() {
        try {
            UboReadWriteTests.readWriteTest(() -> new Vector2Type<>(new IntType(2), new IntType(4)), file("vec2.ubo"));
            UboReadWriteTests.readWriteTest(() -> new Vector3Type<>(new IntType(2), new IntType(4), new IntType(6)), file("vec3.ubo"));
            UboReadWriteTests.readWriteTest(() -> new Vector4Type<>(new IntType(2), new IntType(4), new IntType(6), new IntType(8)), file("vec4.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("PrimitiveTypes")
    void readWritePrimitive() {
        try {
            readWriteTest(() -> new StringType("Apple"), file("string.ubo"));
            for (char i = 0x20; i <= 0xff; i++) {
                char finalI = i;
                readWriteTest(() -> new CharType(finalI), file("char-" + ((int)i) + ".ubo"));
            }
            readWriteTest(() -> new CharType('A'), file("char.ubo"));
            readWriteTest(() -> new ByteType((byte) 1), file("byte.ubo"));
            readWriteTest(() -> new ShortType((short) 1), file("short.ubo"));
            readWriteTest(() -> new IntType(1), file("int.ubo"));
            readWriteTest(() -> new LongType(1L), file("long.ubo"));
            readWriteTest(() -> new FloatType(1f), file("float.ubo"));
            readWriteTest(() -> new DoubleType(1d), file("double.ubo"));
            readWriteTest(() -> new BigIntType(new BigInteger("1")), file("bigint.ubo"));
            readWriteTest(() -> new BigDecType(new BigDecimal("1")), file("bigdec.ubo"));
            readWriteTest(() -> new BooleanType(true), file("boolean.ubo"));
            readWriteTest(() -> new UUIDType(UUID.fromString("00000000-0000-0000-0000-000000000000")), file("uuid.ubo"));
            readWriteTest(() -> new BitSetType(new BitSet()), file("integer.ubo"));
            readWriteTest(() -> new CharArrayType(new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}), file("chararray.ubo"));
            readWriteTest(() -> new BooleanArrayType(new boolean[]{true, true, false, true, false, true, true, false, true, false, true, false, true, false, true, false}), file("booleanarray.ubo"));
            readWriteTest(() -> new ByteArrayType(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}), file("bytearray.ubo"));
            readWriteTest(() -> new ShortArrayType(new short[]{0, 32, 128, 512, 2048, 8192, 32767}), file("shortarray.ubo"));
            readWriteTest(() -> new IntArrayType(new int[]{0, 128, 512, 2048, 8192, 32767, 262140, 1048576, 4194304, 16777216, 67108864, 268435456, 1073741824, 2147483647}), file("intarray.ubo"));
            readWriteTest(() -> new LongArrayType(new long[]{0, 1024, 1048576, 1073741824, 2147483647, 4294967295L, 4398046510080L, 4503599626321920L, 4611686017353646080L, 9223372036854775807L}), file("longarray.ubo"));
            readWriteTest(() -> new FloatArrayType(new float[]{0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f}), file("floatarray.ubo"));
            readWriteTest(() -> new DoubleArrayType(new double[]{0.000000001, 0.0000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0}), file("doublearray.ubo"));
            readWriteTest(() -> new ListType<>(new StringType("Apple"), new StringType("Banana"), new StringType("Pear")), file("list.ubo"));
            HashMap<String, DataType<?>> map = new HashMap<>();
            map.put("Apple", new StringType("Apple"));
            map.put("Banana", new StringType("Banana"));
            map.put("Pear", new StringType("Pear"));
            readWriteTest(() -> new MapType(map), file("map.ubo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    private static <T extends DataType<?>> void readWriteTest(Supplier<T> supplier, File file, T... typeGetter) throws IOException {
        System.out.println("Writing primitive data for " + file.getName() + "...");
        DataIo.write(supplier.get(), file);

        System.out.println("Reading primitive data for " + file.getName() + "...");
        Assertions.assertEquals(DataIo.read(file, typeGetter), supplier.get());

        System.out.println("Writing compressed primitive data for " + file.getName() + "...");
        DataIo.writeCompressed(supplier.get(), file);

        System.out.println("Reading compressed primitive data for " + file.getName() + "...");
        Assertions.assertEquals(DataIo.readCompressed(file, typeGetter), supplier.get());
    }
}
