package com.sylva.ddg


/**
 * Created by sylva on 2016/4/2.
 */
class DataDictGenerator {
    static final URL="jdbc:mysql://221.207.236.156:33066/whbdb?useUnicode=true&amp;characterEncoding=utf-8",USER_NAME="zhongyu",PASSWORD="123456",FILE_PATH="d:/dataDict.xls"

    static void main(String[] args){
        new ExporterUI().showUI()
    }

}
