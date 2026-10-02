package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.IntType;
import dev.ultreon.ubo.types.Vector2Type;
import dev.ultreon.ubo.types.util.Vector2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of two {@code int} values, written as {@code ivec2[1,2]} in USO.
 */
public class IntVector2Type extends NumericVector2Type<Integer> {
    public IntVector2Type(int x, int y) {
        super(x, y);
    }

    public IntVector2Type(Vector2<Integer> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.INT_VECTOR_2;
    }

    @Override
    public Class<Integer> elementType() {
        return Integer.class;
    }

    @Override
    protected void writeElement(DataOutput output, Integer element) throws IOException {
        output.writeInt(element);
    }

    public static IntVector2Type read(DataInput input) throws IOException {
        return new IntVector2Type(input.readInt(), input.readInt());
    }

    @Override
    public IntVector2Type copy() {
        return new IntVector2Type(getValue());
    }

    public static IntVector2Type from(Vector2Type<?> vector) {
        return new IntVector2Type(VectorElements.number(vector.getX(), Integer.class), VectorElements.number(vector.getY(), Integer.class));
    }

    public Vector2Type<IntType> toVector2Type() {
        return new Vector2Type<>(new IntType(getX()), new IntType(getY()));
    }
}
