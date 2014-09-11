package com.audaque.vp.attribute.merge;

import static com.audaque.vp.attribute.merge.MiddleFileMergeReducer.DEL;
import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;

/**
 *
 * @author Hang.Yang
 */
public class MergeReducerTwo extends Reducer<Text, Text, Text, NullWritable> {

    public static Counter counter = null;

    Text text = new Text();

    protected void reduce(Text t, Iterable<Text> it,
            //有DEL标记的组就略过不输出
            Context context)
            throws IOException, InterruptedException {
        counter = context.getCounter("Flag", "DEL");
        for (Text value : it) {
            if (DEL.equals(value.toString())) {
                counter.increment(1);
                return;
            }
        }
        context.write(t, NullWritable.get());
    }
}
