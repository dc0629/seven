package top.flagshen.myqq.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 加密
 *
 * @author 17460
 * @date 2023/1/21
 */
public class EncryptionSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(final String s, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.isNotBlank(s)) {
            try {
                String phonePattern = "(\\w{3})\\w*(\\w{4})";
                jsonGenerator.writeObject(StringUtils.isEmpty(s) ? "" : s.replaceAll(phonePattern, "$1****$2"));
            } catch (Exception e) {
                jsonGenerator.writeString(s);
            }
        } else {
            jsonGenerator.writeString(s);
        }

    }

}
