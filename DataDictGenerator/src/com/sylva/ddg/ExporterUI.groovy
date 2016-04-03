package com.sylva.ddg

import groovy.sql.Sql
import groovy.swing.SwingBuilder
import groovy.swing.impl.TableLayout
import org.apache.poi.hwpf.extractor.WordExtractor

import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.border.TitledBorder

/**
 * Created by sylva on 2016/4/3.
 */
class ExporterUI {
    def JTextField mTfUrl, mTfUserName, mTfPassword, filePath
    def JLabel mLbErr, mLbBottomHint
    def XlsDictExporter exporter=new XlsDictExporter()
    def TableLayout mImportPan
    def JTextArea mTaLog
    def JButton mBtnLink, mBtnImport
    def log(String msg) {
        mTaLog.text += "\r\n" + msg
        mTaLog.setCaretPosition(mTaLog.text.length())
    }

    static thread(Closure clo) {
        new Thread() {
            @Override
            void run() {
                clo()
            }
        }.start()
    }
    def showUI() {
        def swingBuilder = new SwingBuilder()
        swingBuilder.frame(title: "导出数据字典", defaultCloseOperation: JFrame.EXIT_ON_CLOSE, size: [600, 400], show: true) {
            panel {
                tableLayout(border: new TitledBorder("配置")) {
                    tr {
                        td {
                            label(text: "数据库链接")
                        }
                        td {
                            mTfUrl = textField(columns: 40, text: DataDictGenerator.URL)
                        }
                    }
                    tr {
                        td {
                            label(text: "用户名")
                        }
                        td {
                            mTfUserName = textField(columns: 40, text: DataDictGenerator.USER_NAME)
                        }

                    }
                    tr {
                        td {
                            label(text: "密码")
                        }
                        td {
                            mTfPassword = textField(columns: 40, text: DataDictGenerator.PASSWORD)
                        }

                    }
                    tr {
                        td {
                            mBtnLink = button(text: "连接", actionPerformed: {
                                mLbErr.text = "正在连接，请稍候"
                                mBtnLink.visible = false
                                thread {
                                    try {
                                        exporter.link()
                                        mLbErr.text = "数据库连接成功"
                                        mImportPan.visible = true
                                        mBtnLink.visible = false
                                    } catch (Exception e) {
                                        e.printStackTrace()
                                        mLbErr.text = "数据库连接失败"
                                        mBtnLink.visible = true
                                        mImportPan.visible = false
                                    }
                                }
                            })
                        }
                        td {
                            mLbErr = label()
                        }
                    }
                }
                mImportPan = tableLayout(border: new TitledBorder("导出文件")) {
                    tr {
                        td {
                            label("导出文件路径")
                        }
                        td {
                            filePath = textField(columns: 40, text: DataDictGenerator.FILE_PATH)
                        }
                    }
                    tr {
                        td(colspan: 2) {
                            mBtnImport = button(text: "导出", actionPerformed: {
                                mBtnImport.visible = false
                                log "开始导出"
                                mLbBottomHint.visible = true
                                mLbBottomHint.text = "正在导出，导出未完成请不要关闭窗口，否则会导致数据不完整"
                                thread {
                                    try {
                                        exporter.filePath=filePath.text

                                        log exporter.export()
                                        mLbBottomHint.visible = false
                                    }catch(Exception e){
                                        log "导出失败"
                                        mLbBottomHint.visible = false
                                        mBtnImport.visible = true
                                    }finally{
                                        exporter.close()
                                    }
                                }
                            })
                        }
                    }
                    tr {
                        td(colspan: 2) {
                            scrollPane() {
                                mTaLog = textArea(columns: 45, rows: 8, autoscrolls: true)
                            }
                        }
                    }
                    tr {
                        td(colspan: 2) {
                            mLbBottomHint = label()
                        }
                    }
                }
                mImportPan.visible = false
            }
        }
        exporter=new XlsDictExporter(userName: mTfUserName.text,password:mTfPassword.text,url: mTfUrl.text)
    }
}
