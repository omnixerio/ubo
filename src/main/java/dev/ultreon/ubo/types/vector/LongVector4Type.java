package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.LongType;
import dev.ultreon.ubo.types.Vector4Type;
import dev.ultreon.ubo.types.util.Vector4;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of four {@code long} values, written as {@code lvec4[1,2,3,4]} in USO.
 */
public class LongVector4Type extends NumericVector4Type<Long> {
    public LongVector4Type(long x, long y, long z, long w) {
        super(x, y, z, w);
    }

    public LongVector4Type(Vector4<Long> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.LONG_VECTOR_4;
    }

    @Override
    public Class<Long> elementType() {
        return Long.class;
    }

    @Override
    protected void writeElement(DataOutput output, Long element) throws IOException {
        output.writeLong(element);
    }

    public static LongVector4Type read(DataInput input) throws IOException {
        return new LongVector4Type(input.readLong(), input.readLong(), input.readLong(), input.readLong());
    }

    @Override
    public LongVector4Type copy() {
        return new LongVector4Type(getValue());
    }

    public static LongVector4Type from(Vector4Type<?> vector) {
        return new LongVector4Type(VectorElements.number(vector.getX(), Long.class),
                VectorElements.number(vector.getY(), Long.class),
                VectorElements.number(vector.getZ(), Long.class),
                VectorElements.number(vector.getW(), Long.class));
    }

    public Vector4Type<LongType> toVector4Type() {
        return new Vector4Type<>(new LongType(getX()), new LongType(getY()), new LongType(getZ()), new LongType(getW()));
    }
}
