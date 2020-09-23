package tr.org.tspb.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import tr.org.tspb.dao.NullRecord;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class NullRecordCodec implements Codec<NullRecord> {

    @Override
    public void encode(BsonWriter writer, NullRecord t, EncoderContext ec) {
        if (t.getObjectId() == null) {
            writer.writeNull();
        } else {
            writer.writeObjectId(t.getObjectId());
        }
    }

    @Override
    public Class<NullRecord> getEncoderClass() {
        return NullRecord.class;
    }

    @Override
    public NullRecord decode(BsonReader reader, DecoderContext dc) {
        return null;
    }

}
