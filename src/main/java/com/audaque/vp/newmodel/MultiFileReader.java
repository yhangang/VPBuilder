/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.audaque.vp.newmodel;

import java.util.Iterator;
import java.util.List;

/**
 * 逐行读取多个文件.
 *
 * @see
 * 引自:<a>http://code.hammerpig.com/read-from-multiple-text-files-in-java.html</a>
 * @author Liyu.Cai@Audaque.com(仅做修订)
 */
public class MultiFileReader implements Iterable<String> {

    private List<String> _files;
    private boolean _filesHaveHeaders;
    private int _numSkipLines;

    public MultiFileReader(List<String> files, boolean filesHaveHeaders, int numSkipLines) throws Exception {
        _files = files;
        _filesHaveHeaders = filesHaveHeaders;
        _numSkipLines = numSkipLines;
    }

    public MultiFileIterator iterator() {
        return new MultiFileIterator(_files);
    }

    private class MultiFileIterator implements Iterator<String> {

        private List<String> _iteratorFiles;
        private Iterator<String> _currentFileIterator;

        public String HeaderLine = "";

        public MultiFileIterator(List<String> files) {
            _iteratorFiles = files;
        }

        private boolean IterateFile() throws Exception {
            while (_iteratorFiles.size() > 0) {
                _currentFileIterator = new BigFile(_iteratorFiles.remove(0)).iterator();

                if (_currentFileIterator.hasNext()) {
                    if (_filesHaveHeaders) {
                        HeaderLine = _currentFileIterator.next();
                    }

                    for (int i = 0; i < _numSkipLines; i++) {
                        if (_currentFileIterator.hasNext()) {
                            _currentFileIterator.next();
                        }
                    }
                    if ((!_filesHaveHeaders) && (_numSkipLines == 0)) {
                        return true;
                    } else if (_currentFileIterator.hasNext()) {
                        return true;
                    }
                }
            }

            return false;
        }

        public boolean hasNext() {
            try {
                if (_currentFileIterator == null) {
                    return IterateFile();
                }

                if (_currentFileIterator.hasNext()) {
                    return true;
                } else {
                    return IterateFile();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        public String next() {
            return _currentFileIterator.next();
        }

        public void remove() {
            _currentFileIterator.remove();
        }
    }
}
