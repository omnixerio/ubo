package dev.ultreon.tests.data;

import dev.ultreon.ubo.DataIo;
import dev.ultreon.ubo.DataReader;
import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.DataType;
import dev.ultreon.ubo.types.DoubleType;
import dev.ultreon.ubo.types.FloatType;
import dev.ultreon.ubo.types.IntType;
import dev.ultreon.ubo.types.LongType;
import dev.ultreon.ubo.types.Vector2Type;
import dev.ultreon.ubo.types.Vector3Type;
import dev.ultreon.ubo.types.Vector4Type;
import dev.ultreon.ubo.types.util.Vector2;
import dev.ultreon.ubo.types.util.Vector3;
import dev.ultreon.ubo.types.vector.DoubleVector2Type;
import dev.ultreon.ubo.types.vector.DoubleVector3Type;
import dev.ultreon.ubo.types.vector.DoubleVector4Type;
import dev.ultreon.ubo.types.vector.FloatVector2Type;
import dev.ultreon.ubo.types.vector.FloatVector3Type;
import dev.ultreon.ubo.types.vector.FloatVector4Type;
import dev.ultreon.ubo.types.vector.IntVector2Type;
import dev.ultreon.ubo.types.vector.IntVector3Type;
import dev.ultreon.ubo.types.vector.IntVector4Type;
import dev.ultreon.ubo.types.vector.LongVector2Type;
import dev.ultreon.ubo.types.vector.LongVector3Type;
import dev.ultreon.ubo.types.vector.LongVector4Type;
import dev.ultreon.ubo.types.vector.NumericVector;
import dev.ultreon.ubo.util.DataTypeVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

class NumericVectorTests {
    @Test
    @DisplayName("Vector2Types")
    void vector2Types() {
        IntVector2Type vector = new IntVector2Type(1, 2);

        Assertions.assertEquals(DataTypes.INT_VECTOR_2, vector.id());
        Assertions.assertEquals(Integer.class, vector.elementType());
        Assertions.assertEquals(2, vector.size());
        Assertions.assertEquals(1, vector.getX());
        Assertions.assertEquals(2, vector.getY());
        Assertions.assertEquals(1, vector.get(0));
        Assertions.assertEquals(2, vector.get(1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vector.get(2));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vector.get(-1));

        vector.setX(3);
        vector.setY(4);
        vector.set(0, 5);
        vector.set(1, 6);
        vector.setAll(7, 8);
        vector.fill(9);
        Assertions.assertEquals(new IntVector2Type(9, 9), vector);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vector.set(2, 10));

        Iterator<Integer> iterator = vector.iterator();
        Assertions.assertEquals(9, iterator.next());
        Assertions.assertEquals(9, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iterator::next);

        Assertions.assertEquals(new IntVector2Type(9, 9), vector.copy());
        Assertions.assertNotSame(vector, vector.copy());
        Assertions.assertEquals(new IntVector2Type(1, 2).hashCode(), new IntVector2Type(1, 2).hashCode());
        Assertions.assertNotEquals(new IntVector2Type(1, 2), new LongVector2Type(1L, 2L));
    }

    @Test
    @DisplayName("Vector3Types")
    void vector3Types() {
        IntVector3Type vector = new IntVector3Type(1, 2, 3);

        Assertions.assertEquals(DataTypes.INT_VECTOR_3, vector.id());
        Assertions.assertEquals(Integer.class, vector.elementType());
        Assertions.assertEquals(3, vector.size());
        Assertions.assertEquals(1, vector.getX());
        Assertions.assertEquals(2, vector.getY());
        Assertions.assertEquals(3, vector.getZ());
        Assertions.assertEquals(1, vector.get(0));
        Assertions.assertEquals(2, vector.get(1));
        Assertions.assertEquals(3, vector.get(2));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vector.get(3));

        vector.setX(4);
        vector.setY(5);
        vector.setZ(6);
        vector.set(0, 7);
        vector.set(1, 8);
        vector.set(2, 9);
        vector.setAll(10, 11, 12);
        vector.fill(13);
        Assertions.assertEquals(new IntVector3Type(13, 13, 13), vector);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vector.set(3, 14));

        Iterator<Integer> iterator = vector.iterator();
        Assertions.assertEquals(13, iterator.next());
        Assertions.assertEquals(13, iterator.next());
        Assertions.assertEquals(13, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iterator::next);

        Assertions.assertEquals(new IntVector3Type(13, 13, 13), vector.copy());
        Assertions.assertNotEquals(new IntVector3Type(1, 2, 3), new LongVector3Type(1L, 2L, 3L));
    }

    @Test
    @DisplayName("Vector4Types")
    void vector4Types() {
        IntVector4Type vector = new IntVector4Type(1, 2, 3, 4);

        Assertions.assertEquals(DataTypes.INT_VECTOR_4, vector.id());
        Assertions.assertEquals(Integer.class, vector.elementType());
        Assertions.assertEquals(4, vector.size());
        Assertions.assertEquals(1, vector.getX());
        Assertions.assertEquals(2, vector.getY());
        Assertions.assertEquals(3, vector.getZ());
        Assertions.assertEquals(4, vector.getW());
        Assertions.assertEquals(4, vector.get(3));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vector.get(4));

        vector.setX(5);
        vector.setY(6);
        vector.setZ(7);
        vector.setW(8);
        vector.set(0, 9);
        vector.set(1, 10);
        vector.set(2, 11);
        vector.set(3, 12);
        vector.setAll(13, 14, 15, 16);
        vector.fill(17);
        Assertions.assertEquals(new IntVector4Type(17, 17, 17, 17), vector);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vector.set(4, 18));

        Iterator<Integer> iterator = vector.iterator();
        Assertions.assertEquals(17, iterator.next());
        Assertions.assertEquals(17, iterator.next());
        Assertions.assertEquals(17, iterator.next());
        Assertions.assertEquals(17, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iterator::next);

        Assertions.assertEquals(new IntVector4Type(17, 17, 17, 17), vector.copy());
        Assertions.assertNotEquals(new IntVector4Type(1, 2, 3, 4), new LongVector4Type(1L, 2L, 3L, 4L));
    }

    @Test
    @DisplayName("ElementTypes")
    void elementTypes() {
        Assertions.assertEquals(Long.class, new LongVector2Type(1L, 2L).elementType());
        Assertions.assertEquals(Float.class, new FloatVector3Type(1f, 2f, 3f).elementType());
        Assertions.assertEquals(Double.class, new DoubleVector4Type(1d, 2d, 3d, 4d).elementType());

        Assertions.assertEquals(new LongVector2Type(1L, 2L), new LongVector2Type(1L, 2L));
        Assertions.assertEquals(new FloatVector3Type(1.5f, 2.5f, 3.5f), new FloatVector3Type(1.5f, 2.5f, 3.5f));
        Assertions.assertEquals(new DoubleVector4Type(1.5, 2.5, 3.5, 4.5), new DoubleVector4Type(1.5, 2.5, 3.5, 4.5));
        Assertions.assertNotEquals(new FloatVector2Type(1.5f, 2.5f), new FloatVector2Type(1.5f, 2.6f));
    }

    @Test
    @DisplayName("UsoOutput")
    void usoOutput() {
        Assertions.assertEquals("ivec2[1,2]", DataIo.toUso(new IntVector2Type(1, 2)));
        Assertions.assertEquals("lvec2[1,2]", DataIo.toUso(new LongVector2Type(1L, 2L)));
        Assertions.assertEquals("fvec2[1.5,2.5]", DataIo.toUso(new FloatVector2Type(1.5f, 2.5f)));
        Assertions.assertEquals("dvec2[1.5,2.5]", DataIo.toUso(new DoubleVector2Type(1.5, 2.5)));

        Assertions.assertEquals("ivec3[1,2,3]", DataIo.toUso(new IntVector3Type(1, 2, 3)));
        Assertions.assertEquals("lvec3[1,2,3]", DataIo.toUso(new LongVector3Type(1L, 2L, 3L)));
        Assertions.assertEquals("fvec3[1.5,2.5,3.5]", DataIo.toUso(new FloatVector3Type(1.5f, 2.5f, 3.5f)));
        Assertions.assertEquals("dvec3[-1.5,0.0,2.5]", DataIo.toUso(new DoubleVector3Type(-1.5, 0.0, 2.5)));

        Assertions.assertEquals("ivec4[1,2,3,4]", DataIo.toUso(new IntVector4Type(1, 2, 3, 4)));
        Assertions.assertEquals("lvec4[1,2,3,4]", DataIo.toUso(new LongVector4Type(1L, 2L, 3L, 4L)));
        Assertions.assertEquals("fvec4[1.5,2.5,3.5,4.5]", DataIo.toUso(new FloatVector4Type(1.5f, 2.5f, 3.5f, 4.5f)));
        Assertions.assertEquals("dvec4[1.5,2.5,3.5,4.5]", DataIo.toUso(new DoubleVector4Type(1.5, 2.5, 3.5, 4.5)));

        Assertions.assertEquals("ivec2[1,2]", new IntVector2Type(1, 2).toString());
    }

    @Test
    @DisplayName("UsoInput")
    void usoInput() throws IOException {
        Assertions.assertEquals(new IntVector2Type(1, 2), DataIo.fromUso("ivec2[1,2]"));
        Assertions.assertEquals(new LongVector2Type(1L, 2L), DataIo.fromUso("lvec2[1,2]"));
        Assertions.assertEquals(new FloatVector2Type(1.5f, 2.5f), DataIo.fromUso("fvec2[1.5, 2.5]"));
        Assertions.assertEquals(new DoubleVector2Type(-1.5, 2.5), DataIo.fromUso("dvec2[-1.5,2.5]"));

        Assertions.assertEquals(new IntVector3Type(1, 2, 3), DataIo.fromUso("ivec3[1, 2, 3]"));
        Assertions.assertEquals(new LongVector3Type(1L, 2L, 3L), DataIo.fromUso("lvec3[1,2,3]"));
        Assertions.assertEquals(new FloatVector3Type(1.5f, 2.5f, 3.5f), DataIo.fromUso("fvec3[1.5,2.5,3.5]"));
        Assertions.assertEquals(new DoubleVector3Type(1.5, 2.5, 3.5), DataIo.fromUso("dvec3[1.5,2.5,3.5]"));

        Assertions.assertEquals(new IntVector4Type(1, 2, 3, 4), DataIo.fromUso("ivec4[1,2,3,4]"));
        Assertions.assertEquals(new LongVector4Type(1L, 2L, 3L, 4L), DataIo.fromUso("lvec4[1,2,3,4]"));
        Assertions.assertEquals(new FloatVector4Type(1.5f, 2.5f, 3.5f, 4.5f), DataIo.fromUso("fvec4[1.5,2.5,3.5,4.5]"));
        Assertions.assertEquals(new DoubleVector4Type(1.5, 2.5, 3.5, 4.5), DataIo.fromUso("dvec4[1.5,2.5,3.5,4.5]"));

        // The generic vector syntax keeps producing the generic vector types.
        Assertions.assertEquals(new Vector2Type<>(new IntType(1), new IntType(2)), DataIo.fromUso("v2:i[1,2]"));
        Assertions.assertEquals(new Vector3Type<>(new IntType(1), new IntType(2), new IntType(3)), DataIo.fromUso("v3:i[1,2,3]"));
        Assertions.assertEquals(new Vector4Type<>(new IntType(1), new IntType(2), new IntType(3), new IntType(4)), DataIo.fromUso("v4:i[1,2,3,4]"));

        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("ivec2[1]"));
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("ivec5[1,2]"));
        Assertions.assertThrows(IOException.class, () -> DataIo.fromUso("qvec2[1,2]"));
    }

    @Test
    @DisplayName("BinaryFormat")
    void binaryFormat() throws IOException {
        // A numeric vector is a data type of its own, so only its elements are written, without an element type marker.
        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 2, 0, 0, 0, 4}, write(new IntVector2Type(2, 4)));
        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 2, 0, 0, 0, 4, 0, 0, 0, 6}, write(new IntVector3Type(2, 4, 6)));
        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 2, 0, 0, 0, 4, 0, 0, 0, 6, 0, 0, 0, 8}, write(new IntVector4Type(2, 4, 6, 8)));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            new LongVector2Type(2L, 4L).write(output);
        }

        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4}, buffer.toByteArray());
    }

    @Test
    @DisplayName("ReadWrite")
    void readWrite() throws IOException {
        Assertions.assertEquals(new IntVector2Type(2, 4), read(new IntVector2Type(2, 4), IntVector2Type::read));
        Assertions.assertEquals(new LongVector2Type(2L, 4L), read(new LongVector2Type(2L, 4L), LongVector2Type::read));
        Assertions.assertEquals(new FloatVector2Type(1.5f, 2.5f), read(new FloatVector2Type(1.5f, 2.5f), FloatVector2Type::read));
        Assertions.assertEquals(new DoubleVector2Type(1.5, 2.5), read(new DoubleVector2Type(1.5, 2.5), DoubleVector2Type::read));

        Assertions.assertEquals(new IntVector3Type(2, 4, 6), read(new IntVector3Type(2, 4, 6), IntVector3Type::read));
        Assertions.assertEquals(new LongVector3Type(2L, 4L, 6L), read(new LongVector3Type(2L, 4L, 6L), LongVector3Type::read));
        Assertions.assertEquals(new FloatVector3Type(1.5f, 2.5f, 3.5f), read(new FloatVector3Type(1.5f, 2.5f, 3.5f), FloatVector3Type::read));
        Assertions.assertEquals(new DoubleVector3Type(1.5, 2.5, 3.5), read(new DoubleVector3Type(1.5, 2.5, 3.5), DoubleVector3Type::read));

        Assertions.assertEquals(new IntVector4Type(2, 4, 6, 8), read(new IntVector4Type(2, 4, 6, 8), IntVector4Type::read));
        Assertions.assertEquals(new LongVector4Type(2L, 4L, 6L, 8L), read(new LongVector4Type(2L, 4L, 6L, 8L), LongVector4Type::read));
        Assertions.assertEquals(new FloatVector4Type(1.5f, 2.5f, 3.5f, 4.5f), read(new FloatVector4Type(1.5f, 2.5f, 3.5f, 4.5f), FloatVector4Type::read));
        Assertions.assertEquals(new DoubleVector4Type(1.5, 2.5, 3.5, 4.5), read(new DoubleVector4Type(1.5, 2.5, 3.5, 4.5), DoubleVector4Type::read));
    }

    @Test
    @DisplayName("DataIoReadWrite")
    void dataIoReadWrite() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataIo.write(new IntVector3Type(2, 4, 6), (DataOutput) new DataOutputStream(buffer));

        // The type of the vector itself is written next to the header.
        byte[] bytes = buffer.toByteArray();
        Assertions.assertEquals((byte) DataTypes.INT_VECTOR_3, bytes[6]);

        IntVector3Type read = DataIo.read((DataInput) new DataInputStream(new ByteArrayInputStream(bytes)),
                new IntVector3Type(0, 0, 0));
        Assertions.assertEquals(new IntVector3Type(2, 4, 6), read);
    }

    @Test
    @DisplayName("GenericVectorInterop")
    void genericVectorInterop() {
        Assertions.assertEquals(new IntVector2Type(2, 4), IntVector2Type.from(new Vector2Type<>(new IntType(2), new IntType(4))));
        Assertions.assertEquals(new LongVector3Type(2L, 4L, 6L), LongVector3Type.from(new Vector3Type<>(new LongType(2L), new LongType(4L), new LongType(6L))));
        Assertions.assertEquals(new FloatVector4Type(1.5f, 2.5f, 3.5f, 4.5f), FloatVector4Type.from(new Vector4Type<>(new FloatType(1.5f), new FloatType(2.5f), new FloatType(3.5f), new FloatType(4.5f))));
        Assertions.assertEquals(new DoubleVector2Type(1.5, 2.5), DoubleVector2Type.from(new Vector2Type<>(new DoubleType(1.5), new DoubleType(2.5))));

        Assertions.assertEquals(new Vector2Type<>(new IntType(2), new IntType(4)), new IntVector2Type(2, 4).toVector2Type());
        Assertions.assertEquals(new Vector3Type<>(new LongType(2L), new LongType(4L), new LongType(6L)), new LongVector3Type(2L, 4L, 6L).toVector3Type());
        Assertions.assertEquals(new Vector4Type<>(new DoubleType(1.5), new DoubleType(2.5), new DoubleType(3.5), new DoubleType(4.5)), new DoubleVector4Type(1.5, 2.5, 3.5, 4.5).toVector4Type());

        Assertions.assertEquals("v2:i[2,4]", new IntVector2Type(2, 4).toVector2Type().writeUso());
    }

    @Test
    @DisplayName("ElementTypeValidation")
    void elementTypeValidation() {
        IntVector2Type vector = new IntVector2Type(1, 2);

        Assertions.assertThrows(IllegalArgumentException.class, () -> vector.setX(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> vector.set(1, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> vector.fill(null));
        // The failed writes must not corrupt the vector.
        Assertions.assertEquals(new IntVector2Type(1, 2), vector);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new IntVector2Type(wrongValueVector2()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> vector.setValue(wrongValueVector2()));
        Assertions.assertEquals(new IntVector2Type(1, 2), vector);

        Assertions.assertThrows(IllegalArgumentException.class, () -> IntVector2Type.from(new Vector2Type<>(new LongType(1L), new LongType(2L))));
    }

    @Test
    @DisplayName("DeepCopy")
    void deepCopy() {
        IntVector3Type vector = new IntVector3Type(1, 2, 3);
        DataType<?> copy = DataTypeVisitor.deepCopy(vector);

        Assertions.assertEquals(vector, copy);
        Assertions.assertNotSame(vector, copy);
    }

    @Test
    @DisplayName("Value")
    void value() {
        IntVector3Type vector = new IntVector3Type(1, 2, 3);

        Assertions.assertEquals(new Vector3<>(1, 2, 3), vector.getValue());

        vector.setValue(new Vector3<>(4, 5, 6));
        Assertions.assertEquals(new IntVector3Type(4, 5, 6), vector);
        Assertions.assertThrows(IllegalArgumentException.class, () -> vector.setValue(null));

        NumericVector<Integer> view = vector;
        view.set(0, 7);
        Assertions.assertEquals(7, view.get(0));
    }

    private static <T extends DataType<?>> byte[] write(T dataType) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(buffer)) {
            dataType.write(output);
        }

        return buffer.toByteArray();
    }

    private static <T extends DataType<?>> T read(T dataType, DataReader<T> reader) throws IOException {
        return reader.read(new DataInputStream(new ByteArrayInputStream(write(dataType))));
    }

    @SuppressWarnings("unchecked")
    private static Vector2<Integer> wrongValueVector2() {
        return (Vector2<Integer>) (Vector2<?>) new Vector2<>(1L, 2L);
    }
}
