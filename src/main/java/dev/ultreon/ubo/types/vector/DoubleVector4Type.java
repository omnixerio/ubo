package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.DoubleType;
import dev.ultreon.ubo.types.Vector4Type;
import dev.ultreon.ubo.types.util.Vector4;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of four {@code double} values, written as {@code dvec4[1.5,2.5,3.5,4.5]} in USO.
 */
public class DoubleVector4Type extends NumericVector4Type<Double> {
    public DoubleVector4Type(double x, double y, double z, double w) {
        super(x, y, z, w);
    }

    public DoubleVector4Type(Vector4<Double> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.DOUBLE_VECTOR_4;
    }

    @Override
    public Class<Double> elementType() {
        return Double.class;
    }

    @Override
    protected void writeElement(DataOutput output, Double element) throws IOException {
        output.writeDouble(element);
    }

    public static DoubleVector4Type read(DataInput input) throws IOException {
        return new DoubleVector4Type(input.readDouble(), input.readDouble(), input.readDouble(), input.readDouble());
    }

    @Override
    public DoubleVector4Type copy() {
        return new DoubleVector4Type(getValue());
    }

    public static DoubleVector4Type from(Vector4Type<?> vector) {
        return new DoubleVector4Type(VectorElements.number(vector.getX(), Double.class),
                VectorElements.number(vector.getY(), Double.class),
                VectorElements.number(vector.getZ(), Double.class),
                VectorElements.number(vector.getW(), Double.class));
    }

    public Vector4Type<DoubleType> toVector4Type() {
        return new Vector4Type<>(new DoubleType(getX()), new DoubleType(getY()), new DoubleType(getZ()), new DoubleType(getW()));
    }
}
