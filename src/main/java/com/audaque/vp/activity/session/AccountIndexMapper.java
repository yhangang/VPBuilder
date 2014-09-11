/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.activity.session;

import com.audaque.vp.utils.FileUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Liyu.Cai@Audaque.com
 */
public class AccountIndexMapper {

    public static final String SPLIT = "\t";
    /**
     * temporary implementation
     */
    public final BiMap<String, Integer> mapper;
    public int maxIndex = -1;

    public AccountIndexMapper(int initSize) {
        mapper = HashBiMap.create(initSize);
    }

    /**
     * return the index of an accountString.The index starts at 0.
     *
     * @param accountString
     * @return the index of the id
     */
    public int putIndex(String accountString) {
        Preconditions.checkArgument(!StringUtils.isBlank(accountString), "空白字符串");

        Integer index = mapper.get(accountString);
        if (index != null) {
            return index;
        } else {
            mapper.put(accountString, ++maxIndex);
            return maxIndex;
        }
    }

    public int size() {
        return maxIndex;
    }

    public String getAccountString(Integer index) {
        return mapper.inverse().get(index);
    }

    public void toFile(String indexFile) throws IOException {
        BufferedWriter wt = FileUtil.getBufferedWriter(indexFile);

        Iterator<Entry<String, Integer>> iter = mapper.entrySet().iterator();

        wt.write("%size=" + size() + "\n");

        while (iter.hasNext()) {
            Entry<String, Integer> entry = iter.next();
            wt.write(entry.getKey() + SPLIT + entry.getValue() + "\n");
        }
        wt.close();
    }

}
