/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

import com.audaque.vp.utils.DataFormats;
import com.audaque.vp.utils.FileUtil;
import com.audaque.vpbase.account.Account;
import com.audaque.vpbase.vp.VP;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 从多个Middle File中依次读取每一行的记录.
 * <p>
 * 因是单行记录逐一读取，故内存占用小</p>
 *
 * @author Liyu.Cai@Audaque.com
 */
public class MutipleMidFileReader {

    private final List<String> filePaths;
    private final String split;

    public MutipleMidFileReader(String directory, boolean searchSubFolder, String split) {
        Collection<File> files = org.apache.commons.io.FileUtils.listFiles(new File(directory), null, searchSubFolder);
        this.filePaths = new ArrayList<>(files.size());
        for (File file : files) {
            this.filePaths.add(file.getAbsolutePath());
        }
        this.split = split;
    }

    public MutipleMidFileReader(List<String> filePaths, String split) {
        this.filePaths = filePaths;
        this.split = split;
    }

    public MutipleMidFileReader(String singleFile, String split) {
        File sFile = new File(singleFile);
        Preconditions.checkArgument(sFile.exists() && sFile.isFile(), "The input file arg doesn't exists or is not a file");

        this.filePaths = new ArrayList<>(1);
        this.filePaths.add(sFile.getAbsolutePath());
        this.split = split;
    }

    public Iterator<VP> VPs() {
        try {
            final Iterator<String> lineIterator = FileUtil.getLineIterator(filePaths, false, 0);
            return new Iterator<VP>() {

                @Override
                public boolean hasNext() {
                    return lineIterator.hasNext();
                }

                @Override
                public VP next() {
                    /**
                     * TODO:split should be a para
                     */
                    String[] accountIds = lineIterator.next().split(split);
                    final ArrayList<Account> accounts = new ArrayList<>(accountIds.length);
                    for (final String uid : accountIds) {
                        final String[] fields = uid.split(DataFormats.ACCOUNT_TYPE_ID_SPLIT);
                        Preconditions.checkState(fields.length == 2, "格式错误:" + uid);
                        accounts.add(new Account() {

                            @Override
                            public String serviceId() {
                                return fields[0];
                            }

                            @Override
                            public String userId() {
                                return fields[1];
                            }

                            @Override
                            public String uniqueId() {
                                return uid;
                            }

                            @Override
                            public double boost() {
                                throw new UnsupportedOperationException("Not supported yet."); 
                            }

                        });
                    }
                    return new VP() {
                        @Override
                        public String uniqueId() {
                            throw new UnsupportedOperationException("Not supported yet."); 
                        }

                        @Override
                        public Iterator<Account> accounts() {
                            return accounts.iterator();
                        }
                    };
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet."); 
                }
            };
        } catch (Exception ex) {
            throw new IllegalStateException();
        }
    }

}
