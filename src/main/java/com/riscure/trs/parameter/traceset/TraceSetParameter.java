package com.riscure.trs.parameter.traceset;

import com.riscure.trs.parameter.ParameterType;
import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.primitive.ParameterUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class TraceSetParameter {
    private final TraceParameter value;

    public TraceSetParameter(TraceParameter instance) {
        this.value = instance;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeByte(value.getType().getValue());
        dos.writeShort(value.length());
        ParameterUtils.serialize(value, dos);
    }

    public static TraceSetParameter deserialize(DataInputStream dis) throws IOException {
        ParameterType type = ParameterType.fromValue(dis.readByte());
        short length = dis.readShort();
        return new TraceSetParameter(ParameterUtils.deserialize(type, length, dis));
    }

    public TraceParameter getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraceSetParameter that = (TraceSetParameter) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
