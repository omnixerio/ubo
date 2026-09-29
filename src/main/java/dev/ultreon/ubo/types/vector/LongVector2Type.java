package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.LongType;
import dev.ultreon.ubo.types.Vector2Type;
import dev.ultreon.ubo.types.util.Vector2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of two {@code long} values, written as {@code lvec2[1,2]} in USO.
 */
public class LongVector2Type extends NumericVector2Type<Long> {
    public LongVector2Type(long x, long y) {
        super(x, y);
    }

    public LongVector2Type(Vector2<Long> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.LONG_VECTOR_2;
    }

    @Override
    public Class<Long> elementType() {
        return Long.class;
    }

    @Override
    protected void writeElement(DataOutput output, Long element) throws IOException {
        output.writeLong(element);
    }

    public static LongVector2Type read(DataInput input) throws IOException {
        return new LongVector2Type(input.readLong(), input.readLong());
    }

    @Override
    public LongVector2Type copy() {
        return new LongVector2Type(getValue());
    }

    public static LongVector2Type from(Vector2Type<?> vector) {
        return new LongVector2Type(VectorElements.number(vector.getX(), Long.class), VectorElements.number(vector.getY(), Long.class));
    }

    public Vector2Type<LongType> toVector2Type() {
        return new Vector2Type<>(new LongType(getX()), new LongType(getY()));
    }
}
