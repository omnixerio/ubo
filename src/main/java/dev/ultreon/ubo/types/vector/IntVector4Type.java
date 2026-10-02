package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.IntType;
import dev.ultreon.ubo.types.Vector4Type;
import dev.ultreon.ubo.types.util.Vector4;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of four {@code int} values, written as {@code ivec4[1,2,3,4]} in USO.
 */
public class IntVector4Type extends NumericVector4Type<Integer> {
    public IntVector4Type(int x, int y, int z, int w) {
        super(x, y, z, w);
    }

    public IntVector4Type(Vector4<Integer> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.INT_VECTOR_4;
    }

    @Override
    public Class<Integer> elementType() {
        return Integer.class;
    }

    @Override
    protected void writeElement(DataOutput output, Integer element) throws IOException {
        output.writeInt(element);
    }

    public static IntVector4Type read(DataInput input) throws IOException {
        return new IntVector4Type(input.readInt(), input.readInt(), input.readInt(), input.readInt());
    }

    @Override
    public IntVector4Type copy() {
        return new IntVector4Type(getValue());
    }

    public static IntVector4Type from(Vector4Type<?> vector) {
        return new IntVector4Type(VectorElements.number(vector.getX(), Integer.class),
                VectorElements.number(vector.getY(), Integer.class),
                VectorElements.number(vector.getZ(), Integer.class),
                VectorElements.number(vector.getW(), Integer.class));
    }

    public Vector4Type<IntType> toVector4Type() {
        return new Vector4Type<>(new IntType(getX()), new IntType(getY()), new IntType(getZ()), new IntType(getW()));
    }
}
