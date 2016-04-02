package com.sylva.ddg

import groovy.sql.Sql
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.hssf.util.Region

/**
 * Created by sylva on 2016/4/2.
 */
class DataDictGenerator {
    static url="jdbc:mysql://221.207.236.156:33066/whbdb?useUnicode=true&amp;characterEncoding=utf-8",userName="zhongyu",password="123456"
    static Sql link() {
        Sql.newInstance(url,userName,
                password, "com.mysql.jdbc.Driver")
    }
    static void main(String[] args){
        def sql=link()
        def wb=new HSSFWorkbook()
        def sheet=wb.createSheet("数据字典")
        sheet.defaultColumnWidth=(short)15
        def style = wb.createCellStyle();
        style.fillForegroundColor=HSSFColor.SKY_BLUE.index
        style.fillPattern=HSSFCellStyle.SOLID_FOREGROUND
        style.borderBottom=HSSFCellStyle.BORDER_THIN
        style.borderLeft=HSSFCellStyle.BORDER_THIN
        style.borderRight=HSSFCellStyle.BORDER_THIN
        style.borderTop=HSSFCellStyle.BORDER_THIN
        style.alignment=HSSFCellStyle.ALIGN_CENTER
        def rowIndex=0
        sql.eachRow("select TABLE_NAME,TABLE_COMMENT from information_schema.tables WHERE table_type!='SYSTEM VIEW' and TABLE_SCHEMA='whbdb'"){
            def title= "$it.table_name($it.table_comment)"
            def firstRow=sheet.createRow(rowIndex)
            def tableNameCell=firstRow.createCell(0)
            def c1=firstRow.createCell(1)
            def c2=firstRow.createCell(2)
            tableNameCell.cellStyle=style
            c1.cellStyle=style
            c2.cellStyle=style
            mergeRange(sheet,rowIndex,0,rowIndex,2)
            rowIndex++
            tableNameCell.setCellValue(title)
            sql.eachRow("select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT from information_schema.columns where table_name=$it.table_name;"){
                def row=sheet.createRow(rowIndex++)
                def cell1=row.createCell(0)
                def cell2=row.createCell(1)
                def cell3=row.createCell(2)
                cell1.cellValue=it.column_name
                cell2.cellValue=it.column_type
                cell3.cellValue=it.column_comment
            }
            sheet.createRow(rowIndex++)
        }
        wb.write(new File("d:/whbdict.xls").newOutputStream())
        sql.close()
        println "finish"
    }
    static mergeRange(HSSFSheet sheet,int y1,int x1,int y2,int x2){
        sheet.addMergedRegion(new CellRangeAddress(y1,y2,x1,x2))
    }
}
