package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.IntType;
import dev.ultreon.ubo.types.Vector3Type;
import dev.ultreon.ubo.types.util.Vector3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of three {@code int} values, written as {@code ivec3[1,2,3]} in USO.
 */
public class IntVector3Type extends NumericVector3Type<Integer> {
    public IntVector3Type(int x, int y, int z) {
        super(x, y, z);
    }

    public IntVector3Type(Vector3<Integer> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.INT_VECTOR_3;
    }

    @Override
    public Class<Integer> elementType() {
        return Integer.class;
    }

    @Override
    protected void writeElement(DataOutput output, Integer element) throws IOException {
        output.writeInt(element);
    }

    public static IntVector3Type read(DataInput input) throws IOException {
        return new IntVector3Type(input.readInt(), input.readInt(), input.readInt());
    }

    @Override
    public IntVector3Type copy() {
        return new IntVector3Type(getValue());
    }

    public static IntVector3Type from(Vector3Type<?> vector) {
        return new IntVector3Type(VectorElements.number(vector.getX(), Integer.class),
                VectorElements.number(vector.getY(), Integer.class),
                VectorElements.number(vector.getZ(), Integer.class));
    }

    public Vector3Type<IntType> toVector3Type() {
        return new Vector3Type<>(new IntType(getX()), new IntType(getY()), new IntType(getZ()));
    }
}
