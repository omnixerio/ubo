package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.LongType;
import dev.ultreon.ubo.types.Vector3Type;
import dev.ultreon.ubo.types.util.Vector3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of three {@code long} values, written as {@code lvec3[1,2,3]} in USO.
 */
public class LongVector3Type extends NumericVector3Type<Long> {
    public LongVector3Type(long x, long y, long z) {
        super(x, y, z);
    }

    public LongVector3Type(Vector3<Long> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.LONG_VECTOR_3;
    }

    @Override
    public Class<Long> elementType() {
        return Long.class;
    }

    @Override
    protected void writeElement(DataOutput output, Long element) throws IOException {
        output.writeLong(element);
    }

    public static LongVector3Type read(DataInput input) throws IOException {
        return new LongVector3Type(input.readLong(), input.readLong(), input.readLong());
    }

    @Override
    public LongVector3Type copy() {
        return new LongVector3Type(getValue());
    }

    public static LongVector3Type from(Vector3Type<?> vector) {
        return new LongVector3Type(VectorElements.number(vector.getX(), Long.class),
                VectorElements.number(vector.getY(), Long.class),
                VectorElements.number(vector.getZ(), Long.class));
    }

    public Vector3Type<LongType> toVector3Type() {
        return new Vector3Type<>(new LongType(getX()), new LongType(getY()), new LongType(getZ()));
    }
}
