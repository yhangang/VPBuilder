package com.audaque.vp.attribute.group;

import com.audaque.vp.attribute.format.FormatMapper;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 *
 * @author Hang.Yang
 */
public class AttributeMatchReducer extends Reducer<Text, Text, Text, NullWritable> {

    private String filedsSplit;

    @Override
    protected void setup(Reducer.Context context) throws IOException, InterruptedException {
        //获取参数
        Configuration conf = context.getConfiguration();
        filedsSplit = conf.get(FormatMapper.FIELDS_SPLIT_CONFIG);
        filedsSplit=new String(Base64.decode(filedsSplit));
        super.setup(context);
    }

    @Override
    protected void reduce(Text t, Iterable<Text> it,
            Context context)
            throws IOException, InterruptedException {
        Text text = new Text();
        //通过预装入SET，实现排序和去重  （2,2,2,1,1,1）->(1,2)
        Set<String> set = new TreeSet<String>();
        StringBuilder sb = new StringBuilder();

        for (Text value : it) {
            set.add(value.toString());
        }

        for (String str : set) {
            sb.append(str).append(filedsSplit);
        }

        text.set(sb.toString().substring(0, sb.length() - 1));
        context.write(text, NullWritable.get());
    }
}
