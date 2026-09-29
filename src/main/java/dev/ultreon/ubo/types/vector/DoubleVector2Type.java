package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.DoubleType;
import dev.ultreon.ubo.types.Vector2Type;
import dev.ultreon.ubo.types.util.Vector2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of two {@code double} values, written as {@code dvec2[1.5,2.5]} in USO.
 */
public class DoubleVector2Type extends NumericVector2Type<Double> {
    public DoubleVector2Type(double x, double y) {
        super(x, y);
    }

    public DoubleVector2Type(Vector2<Double> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.DOUBLE_VECTOR_2;
    }

    @Override
    public Class<Double> elementType() {
        return Double.class;
    }

    @Override
    protected void writeElement(DataOutput output, Double element) throws IOException {
        output.writeDouble(element);
    }

    public static DoubleVector2Type read(DataInput input) throws IOException {
        return new DoubleVector2Type(input.readDouble(), input.readDouble());
    }

    @Override
    public DoubleVector2Type copy() {
        return new DoubleVector2Type(getValue());
    }

    public static DoubleVector2Type from(Vector2Type<?> vector) {
        return new DoubleVector2Type(VectorElements.number(vector.getX(), Double.class), VectorElements.number(vector.getY(), Double.class));
    }

    public Vector2Type<DoubleType> toVector2Type() {
        return new Vector2Type<>(new DoubleType(getX()), new DoubleType(getY()));
    }
}
