package tr.org.tspb.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import tr.org.tspb.dao.MyBaseRecord;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MyBaseRecordCodec implements Codec<MyBaseRecord> {

    @Override
    public void encode(BsonWriter writer, MyBaseRecord t, EncoderContext ec) {
        if (t.getObjectId() == null) {
            writer.writeNull();
        } else {
            writer.writeObjectId(t.getObjectId());
        }
    }

    @Override
    public Class<MyBaseRecord> getEncoderClass() {
        return MyBaseRecord.class;
    }

    @Override
    public MyBaseRecord decode(BsonReader reader, DecoderContext dc) {
        return null;
    }

}
