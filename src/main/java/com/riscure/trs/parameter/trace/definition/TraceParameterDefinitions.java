package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.parameter.TraceParameter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the header definitions of all user-added parameters in the trace format
 *
 * This explicitly implements LinkedHashMap to ensure that the data is retrieved in the same order as it was added
 */
public class TraceParameterDefinitions extends LinkedHashMap<String, TraceParameterDefinition<TraceParameter>> {
    public int totalSize() {
        return values().stream().mapToInt(definition -> definition.getLength() * definition.getType().getByteSize()).sum();
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        //Write NE
        dos.writeShort(size());
        for (Map.Entry<String, TraceParameterDefinition<TraceParameter>> entry : entrySet()) {
            byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
            //Write NL
            dos.writeShort(nameBytes.length);
            //Write N
            dos.write(nameBytes);
            TraceParameterDefinition<? extends TraceParameter> value = entry.getValue();
            value.serialize(dos);
        }
        dos.flush();
        return baos.toByteArray();
    }

    public static TraceParameterDefinitions deserialize(byte[] bytes) throws IOException {
        TraceParameterDefinitions result = new TraceParameterDefinitions();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            DataInputStream dis = new DataInputStream(bais);
            //Read NE
            short numberOfEntries = dis.readShort();
            for (int k = 0; k < numberOfEntries; k++) {
                //Read NL
                short nameLength = dis.readShort();
                byte[] nameBytes = new byte[nameLength];
                int read = dis.read(nameBytes, 0, nameLength);
                if (read != nameLength) throw new IOException("Error reading parameter name");
                //Read N
                String name = new String(nameBytes, StandardCharsets.UTF_8);
                //Read definition
                result.put(name, TraceParameterDefinition.deserialize(dis));
            }
        }
        return result;
    }
}
