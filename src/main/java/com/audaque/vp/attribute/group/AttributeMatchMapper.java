package com.audaque.vp.attribute.group;

import com.audaque.vp.attribute.format.FormatMapper;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

/**
 *
 * @author Hang.Yang
 */
public class AttributeMatchMapper extends Mapper<Object, Text, Text, Text> {

    public static final String RECORD_LENGTH_CONFIG = AttributeMatchMapper.class.getName() + ".RECORD_LENGTH_CONFIG_KEY";
    public static final String RULES_CONFIG = AttributeMatchMapper.class.getName() + ".RULES_CONFIG";

    //匹配规则的序号 (1,4)代表第2字段与第5字段必须相同
    private List<Integer> rules;
    public static Counter counter = null;

    private String accoutTypeSplit;
    private String fieldSplit;
    private int recordLength;

    private Text keyText = new Text();
    private Text valueText = new Text();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        rules = new ArrayList<Integer>();
        //获取参数
        Configuration conf = context.getConfiguration();

        recordLength = Integer.valueOf(conf.get(RECORD_LENGTH_CONFIG));
        accoutTypeSplit = conf.get(FormatMapper.TYPE_ID_CONFIG);
        fieldSplit = conf.get(FormatMapper.FIELDS_SPLIT_CONFIG);
        
        fieldSplit=new String(Base64.decode(fieldSplit));
        accoutTypeSplit=new String(Base64.decode(accoutTypeSplit));

        //匹配规则字符串分隔符写死用","
        String[] rulesStr = conf.get(RULES_CONFIG).split(",", -1);

        //装入序号，若非法输入则终止程序
        for (int i = 0; i < rulesStr.length; i++) {
                rules.add(Integer.valueOf(rulesStr[i]));
        }
        //不符合条件个记录数
        counter = context.getCounter("Flag", "Invalid Record Numbers");
        super.setup(context);
    }

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] strs = value.toString().split(fieldSplit, -1);
        StringBuilder sb = new StringBuilder();
        //标识匹配字段是否有空值，空值则无法匹配，自己和自己一组
        boolean hasBlank = false;
        //判断输入数据长度是否合法，不合法计数器++，然后跳过
        if (strs.length < recordLength) {
            counter.increment(1);
            return;
        }
        //判断该序号是否合法，并且对应字段是否为空值
        for (Integer i : rules) {
            if (!StringUtils.isNotBlank(strs[i])) {
                hasBlank = true;
            }
            sb.append(strs[i] + fieldSplit);
        }

        if (hasBlank) {
            valueText.set(strs[1] + accoutTypeSplit + strs[0]);
            context.write(value, valueText);
        } else {
            keyText.set(sb.toString().substring(0, sb.length() - 1));
            valueText.set(strs[1] + accoutTypeSplit + strs[0]);
            context.write(keyText, valueText);
        }

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
    }

}
