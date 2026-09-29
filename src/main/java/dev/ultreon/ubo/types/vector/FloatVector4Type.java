package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.FloatType;
import dev.ultreon.ubo.types.Vector4Type;
import dev.ultreon.ubo.types.util.Vector4;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of four {@code float} values, written as {@code fvec4[1.5,2.5,3.5,4.5]} in USO.
 */
public class FloatVector4Type extends NumericVector4Type<Float> {
    public FloatVector4Type(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    public FloatVector4Type(Vector4<Float> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.FLOAT_VECTOR_4;
    }

    @Override
    public Class<Float> elementType() {
        return Float.class;
    }

    @Override
    protected void writeElement(DataOutput output, Float element) throws IOException {
        output.writeFloat(element);
    }

    public static FloatVector4Type read(DataInput input) throws IOException {
        return new FloatVector4Type(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
    }

    @Override
    public FloatVector4Type copy() {
        return new FloatVector4Type(getValue());
    }

    public static FloatVector4Type from(Vector4Type<?> vector) {
        return new FloatVector4Type(VectorElements.number(vector.getX(), Float.class),
                VectorElements.number(vector.getY(), Float.class),
                VectorElements.number(vector.getZ(), Float.class),
                VectorElements.number(vector.getW(), Float.class));
    }

    public Vector4Type<FloatType> toVector4Type() {
        return new Vector4Type<>(new FloatType(getX()), new FloatType(getY()), new FloatType(getZ()), new FloatType(getW()));
    }
}
