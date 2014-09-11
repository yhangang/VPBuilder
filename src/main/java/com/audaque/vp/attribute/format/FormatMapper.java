package com.audaque.vp.attribute.format;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.IOException;
import java.util.UUID;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 *
 * @author Hang.Yang
 */
public class FormatMapper extends Mapper<Object, Text, Text, NullWritable> {

    public static final String FIELDS_SPLIT_CONFIG = FormatMapper.class.getName() + ".FIELDS_SPLIT";
    public static final String TYPE_ID_CONFIG = FormatMapper.class.getName() + ".TYPE_ID_CONFIG";
    private String filedsSplit;
    private String typeIdSplit;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        filedsSplit = context.getConfiguration().get(FIELDS_SPLIT_CONFIG);
        typeIdSplit = context.getConfiguration().get(TYPE_ID_CONFIG);
        filedsSplit=new String(Base64.decode(filedsSplit));
        typeIdSplit=new String(Base64.decode(typeIdSplit));
    }

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] acontUniIds = value.toString().split(filedsSplit, -1);
        String vid = UUID.randomUUID().toString();
//        System.out.println("!!!SPLIT:"+filedsSplit);
//        multipleOutputs.write(new Text("SPLIT_"), value, "Split" + vid);

//        multipleOutputs.write(new Text(key.toString()), value, "Merge" + vid);
        for (int i = 0; i < acontUniIds.length; i++) {
            String[] fields = acontUniIds[i].split(typeIdSplit, -1);
            if (fields.length != 2) {
//                multipleOutputs.write(new Text(key.toString()), value, "For " + vid + "_" + i + "_" + acontUniIds[i]);
//                System.out.println(key.toString() + value + "For " + vid + "_" + i + "_" + acontUniIds[i]);
//                Thread.sleep(1000 * 60 * 10);
                continue;
            }
            String line = vid + filedsSplit + fields[0] + filedsSplit + fields[1];
            context.write(new Text(line), NullWritable.get());
        }

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
//        multipleOutputs.close();
    }

}
