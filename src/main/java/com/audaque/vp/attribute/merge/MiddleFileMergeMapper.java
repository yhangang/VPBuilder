package com.audaque.vp.attribute.merge;

import com.audaque.vp.attribute.format.FormatMapper;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 *
 * @author Hang.Yang
 */
public class MiddleFileMergeMapper extends Mapper<Object, Text, Object, Text> {

    private String filedsSplit;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //获取参数
        Configuration conf = context.getConfiguration();
        filedsSplit = conf.get(FormatMapper.FIELDS_SPLIT_CONFIG);
        filedsSplit=new String(Base64.decode(filedsSplit));
        super.setup(context);
    }

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] strs = value.toString().split(filedsSplit, -1);

        for (int i = 0; i < strs.length; i++) {
            context.write(new Text(strs[i]), value);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
    }

}
