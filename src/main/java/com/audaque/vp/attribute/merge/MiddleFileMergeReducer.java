package com.audaque.vp.attribute.merge;

import com.audaque.vp.attribute.format.FormatMapper;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 *
 * @author Hang.Yang
 */
public class MiddleFileMergeReducer extends Reducer<Text, Text, Text, Text> {

    public static final String KEEP = MiddleFileMergeReducer.class.getName() + ".KEEP";
    public static final String DEL = MiddleFileMergeReducer.class.getName() + ".DEL";

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
        //待合并的子集中，最长的长度，判断并集是否比每个子集都大
        int maxLength = 0;
        //复制一份迭代器
        List<String> list = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();
        Set<String> set = new TreeSet<String>();

        for (Text value : it) {
            list.add(value.toString());
            String[] strs = value.toString().split(filedsSplit, -1);
            if (maxLength < strs.length) {
                maxLength = strs.length;
            }
            //将所有id放到set中，自动去重
            for (int i = 0; i < strs.length; i++) {
                set.add(strs[i]);
            }
        }
        Iterator<String> it2 = list.iterator();

        //并集等于最大的子集
        if (maxLength == set.size()) {
            while (it2.hasNext()) {
                Text value = new Text(it2.next());
                String[] strs = value.toString().split(filedsSplit, -1);
                if (strs.length < maxLength) {
                    context.write(value, new Text(DEL));
                }
            }
        } else {
            while (it2.hasNext()) {
                Text value = new Text(it2.next());
                context.write(value, new Text(DEL));
            }
        }
        //将set中元素取出合并
        for (String str : set) {
            sb.append(str).append(filedsSplit);
        }

        text.set(sb.toString().substring(0, sb.length() - 1));
        context.write(text, new Text(KEEP));
//        super.reduce(t, it, context);
    }
}
