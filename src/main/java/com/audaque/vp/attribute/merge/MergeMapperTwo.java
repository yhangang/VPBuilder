package com.audaque.vp.attribute.merge;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 *
 * @author Hang.Yang
 */
public class MergeMapperTwo extends Mapper<Object, Text, Object, Text> {

    public static final String MRDEFAULTSPLIT = "\t";

    private Text keyText = new Text();
    private Text valueText = new Text();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] strs = value.toString().split(MRDEFAULTSPLIT, -1);
        keyText = new Text(strs[0]);
        valueText = new Text(strs[1]);

        //将上一个Reduce的输出作为它的输入
        context.write(keyText, valueText);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
    }

}
