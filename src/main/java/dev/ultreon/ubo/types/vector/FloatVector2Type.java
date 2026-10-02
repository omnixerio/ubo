package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.FloatType;
import dev.ultreon.ubo.types.Vector2Type;
import dev.ultreon.ubo.types.util.Vector2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of two {@code float} values, written as {@code fvec2[1.5,2.5]} in USO.
 */
public class FloatVector2Type extends NumericVector2Type<Float> {
    public FloatVector2Type(float x, float y) {
        super(x, y);
    }

    public FloatVector2Type(Vector2<Float> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.FLOAT_VECTOR_2;
    }

    @Override
    public Class<Float> elementType() {
        return Float.class;
    }

    @Override
    protected void writeElement(DataOutput output, Float element) throws IOException {
        output.writeFloat(element);
    }

    public static FloatVector2Type read(DataInput input) throws IOException {
        return new FloatVector2Type(input.readFloat(), input.readFloat());
    }

    @Override
    public FloatVector2Type copy() {
        return new FloatVector2Type(getValue());
    }

    public static FloatVector2Type from(Vector2Type<?> vector) {
        return new FloatVector2Type(VectorElements.number(vector.getX(), Float.class), VectorElements.number(vector.getY(), Float.class));
    }

    public Vector2Type<FloatType> toVector2Type() {
        return new Vector2Type<>(new FloatType(getX()), new FloatType(getY()));
    }
}
