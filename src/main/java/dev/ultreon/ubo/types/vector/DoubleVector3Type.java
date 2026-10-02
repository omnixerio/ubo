package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.DoubleType;
import dev.ultreon.ubo.types.Vector3Type;
import dev.ultreon.ubo.types.util.Vector3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of three {@code double} values, written as {@code dvec3[1.5,2.5,3.5]} in USO.
 */
public class DoubleVector3Type extends NumericVector3Type<Double> {
    public DoubleVector3Type(double x, double y, double z) {
        super(x, y, z);
    }

    public DoubleVector3Type(Vector3<Double> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.DOUBLE_VECTOR_3;
    }

    @Override
    public Class<Double> elementType() {
        return Double.class;
    }

    @Override
    protected void writeElement(DataOutput output, Double element) throws IOException {
        output.writeDouble(element);
    }

    public static DoubleVector3Type read(DataInput input) throws IOException {
        return new DoubleVector3Type(input.readDouble(), input.readDouble(), input.readDouble());
    }

    @Override
    public DoubleVector3Type copy() {
        return new DoubleVector3Type(getValue());
    }

    public static DoubleVector3Type from(Vector3Type<?> vector) {
        return new DoubleVector3Type(VectorElements.number(vector.getX(), Double.class),
                VectorElements.number(vector.getY(), Double.class),
                VectorElements.number(vector.getZ(), Double.class));
    }

    public Vector3Type<DoubleType> toVector3Type() {
        return new Vector3Type<>(new DoubleType(getX()), new DoubleType(getY()), new DoubleType(getZ()));
    }
}
