package com.mes.Common.Excel;

import com.mes.Mapper.Excel.ExcelMapper;
import com.mes.mesScm.InOut.DTO.*;
import lombok.extern.slf4j.Slf4j;
import com.mes.Common.Excel.Action.ExcelFunction;
import com.mes.Common.Excel.DTO.Excel;
import com.mes.Common.Excel.Util.MakeBody;
import com.mes.Common.Excel.Util.MakeHeader;
import com.mes.Common.Excel.Util.Upload;
import com.mes.mesCrm.Crm.DTO.CRM_ORD_RECP;
import com.mes.mesCrm.Crm.DTO.CRM_OUT_SUB;
import com.mes.mesCrm.Crm.DTO.CRM_PLAN;
import com.mes.mesOut.mesOut.DTO.OUTS_IN_SUB;
import com.mes.mesOut.mesOut.DTO.OUTS_OUT_BCR;
import com.mes.mesOut.mesOut.DTO.OUTS_OUT_SUB;
import com.mes.mesQms.Import.DTO.QMS_RECV_SUB;
import com.mes.mesQms.Shipment.DTO.QMS_PROD_SUB;
import com.mes.mesScm.Close.DTO.SCM_CLOSE;
import com.mes.mesScm.Half.DTO.SCM_HIN;
import com.mes.mesScm.Half.DTO.SCM_HIN_READY;
import com.mes.mesScm.Half.DTO.SCM_HOUT_SUB;
import com.mes.mesScm.Inventory.DTO.SCM_STOCK_LIST;
import com.mes.mesScm.Inventory.DTO.SCM_STOCK_REV_LIST;
import com.mes.mesScm.Inventory.DTO.SCM_STOCK_SUM_DAY;
import com.mes.mesScm.Inventory.DTO.SCM_STOCK_SUM_MONTH;
import com.mes.mesScm.Order.DTO.SCM_IN_ORD_SUB;
import com.mes.mesScm.Order.DTO.SCM_REQ_ORD;
import com.mes.mesScm.Standard.DTO.SYS_PART_PRICE;
import com.mes.mesScm.Standard.DTO.sysBPart;
import com.mes.mesTpm.Error.DTO.tpmMachineError;
import com.mes.mesWms.InOut.DTO.WMS_IN_SUB;
import com.mes.mesWms.InOut.DTO.WMS_OUT_ORD_SUB;
import com.mes.mesWms.InOut.DTO.WMS_OUT_SUB;
import com.mes.mesWms.Stock.DTO.WMS_STOCK;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/** *
 * <pre>
 *     ExcelService
 *     ?????? ?????????, ??????????????? ???????????? ????????? ?????????
 * </pre>
 * @author ?????????
 * @since 2019-11-27
 * @version 1.0
 * @see ExcelFunction
 * **/
@Service
@Slf4j
@Transactional
public class ExcelService extends ExcelFunction {
    @Autowired
    private ExcelMapper excelMapper;

    /** *
     * <pre>
     *     ?????? ?????? ?????? ??????
     *     ???????????? ??????????????? ???????????? ????????? ????????? ????????????.
     * </pre>
     * @param response      SXSSFWorkBook
     * @param excel         ???????????? DTO
     * @throws IOException
     * **/
    public void ExcelDownload(HttpServletRequest req, HttpServletResponse response, Excel excel) throws IOException {
        // ????????? ??????
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(100);
        MakeHeader makeHeader = new MakeHeader();
        MakeBody makeBody = new MakeBody();


        // ???????????? ??????
        Row row = null;
        Cell cell = null;
        String excelName = null;
        int rowNo = 0;
        int i = 0;
        int v = 0;

        try {
            // ???????????? ???????????? ?????? ?????? ??????
            if(excel.getName().equals("sysBPart")) {
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????", "UTF-8");

                // DataTransfer [s]
                List<sysBPart> list = excelMapper.testDbList();
                List<List<Object>> rows = makeBody.sysBPart_Body(list);
                int index = makeHeader.sysBPart_Header().length;
                String[] data = makeHeader.sysBPart_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short) 512);
                for (i = 0; index > i; i++) {
                    sheet.setColumnWidth((short) i, (short) 7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i = 0; list.size() > i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v = 0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("sysPartPrice")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SYS_PART_PRICE> list = excelMapper.sysPartPriceDbList(excel);
                if(!list.isEmpty()){
                    List<List<Object>> rows = makeBody.sysPartPrice_Body(list);
                    int index = makeHeader.sysPartPrice_Header().length;
                    String[] data = makeHeader.sysPartPrice_Header();
                    // DataTransfer [e]

                    // (MakeHeader) ?????? ??????
                    row = sheet.createRow(rowNo++);
                    row.setHeight((short)512);
                    for(i=0; index > i; i++){
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }

                    // (MakeBody) ?????? ??????
                    for (i=0; list.size()>i; i++) {
                        row = sheet.createRow(rowNo++);
                        for (v=0; rows.get(i).size() > v; v++) {
                            cell = row.createCell(v);
                            cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                            cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                        }
                    }
                }else {
                    response(response,sxssfWorkbook,excelName,"fail",null);
                }
            }else if(excel.getName().equals("qmsProdErrorList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<QMS_PROD_SUB> list = excelMapper.qmsProdErrorDbList(excel);
                if(!list.isEmpty()){
                    List<List<Object>> rows = makeBody.qmsProdError_Body(list);
                    int index = makeHeader.qmsProdError_Header().length;
                    String[] data = makeHeader.qmsProdError_Header();
                    // DataTransfer [e]

                    // (MakeHeader) ?????? ??????
                    row = sheet.createRow(rowNo++);
                    row.setHeight((short)512);
                    for(i=0; index > i; i++){
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }

                    // (MakeBody) ?????? ??????
                    for (i=0; list.size()>i; i++) {
                        row = sheet.createRow(rowNo++);
                        for (v=0; rows.get(i).size() > v; v++) {
                            cell = row.createCell(v);
                            cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                            cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                        }
                    }
                }else {
                    response(response,sxssfWorkbook,excelName,"fail",null);
                }
            }else if(excel.getName().equals("qmsRecvErrorList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<QMS_RECV_SUB> list = excelMapper.qmsRecvErrorDbList(excel);
                if(!list.isEmpty()){
                    List<List<Object>> rows = makeBody.qmsRecvError_Body(list);
                    int index = makeHeader.qmsRecvError_Header().length;
                    String[] data = makeHeader.qmsRecvError_Header();
                    // DataTransfer [e]

                    // (MakeHeader) ?????? ??????
                    row = sheet.createRow(rowNo++);
                    row.setHeight((short)512);
                    for(i=0; index > i; i++){
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }

                    // (MakeBody) ?????? ??????
                    for (i=0; list.size()>i; i++) {
                        row = sheet.createRow(rowNo++);
                        for (v=0; rows.get(i).size() > v; v++) {
                            cell = row.createCell(v);
                            cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                            cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                        }
                    }
                }else {
                    response(response,sxssfWorkbook,excelName,"fail",null);
                }
            }else if(excel.getName().equals("scmOrderList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_IN_ORD_SUB> list = excelMapper.scmOrderListDbList(excel);

                List<List<Object>> rows = makeBody.scmOrderList_Body(list);
                int index = makeHeader.scmOrderList_Header().length;
                String[] data = makeHeader.scmOrderList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmIOList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("?????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("?????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_IO> list = excelMapper.scmIOListDbList(excel);

                List<List<Object>> rows = makeBody.scmIOList_Body(list);
                int index = makeHeader.scmIOList_Header().length;
                String[] data = makeHeader.scmIOList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmReqOrder")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_REQ_ORD> list = excelMapper.scmReqOrderDbList(excel);
                List<List<Object>> rows = makeBody.scmReqOrder_Body(list);
                int index = makeHeader.scmReqOrder_Header().length;
                String[] data = makeHeader.scmReqOrder_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmInList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_IN_SUB> list = excelMapper.scmInListDbList(excel);
                List<List<Object>> rows = makeBody.scmInList_Body(list);
                int index = makeHeader.scmInList_Header().length;
                String[] data = makeHeader.scmInList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmOutList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_OUT_SUB> list = excelMapper.scmOutListDbList(excel);
                List<List<Object>> rows = makeBody.scmOutList_Body(list);
                int index = makeHeader.scmOutList_Header().length;
                String[] data = makeHeader.scmOutList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmStockRetList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                System.out.println(excel);
                List<SCM_STOCK_RET_SUB> list = excelMapper.scmStockRetListDbList(excel);
                System.out.println(list.size());
                List<List<Object>> rows = makeBody.scmStockRetList_Body(list);
                int index = makeHeader.scmStockRetList_Header().length;
                String[] data = makeHeader.scmStockRetList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmInLineList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("???????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("???????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_REIN_SUB> list = excelMapper.scmInLineListDbList(excel);
                List<List<Object>> rows = makeBody.scmInLineList_Body(list);
                int index = makeHeader.scmInLineList_Header().length;
                String[] data = makeHeader.scmInLineList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmStockList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_STOCK_LIST> list = excelMapper.scmStockListDbList(excel);
                List<List<Object>> rows = makeBody.scmStockList_Body(list);
                int index = makeHeader.scmStockList_Header().length;
                String[] data = makeHeader.scmStockList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmStockSumDay")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("???????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("???????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_STOCK_SUM_DAY> list = excelMapper.scmStockSumDayListDbList(excel);
                List<List<Object>> rows = makeBody.scmStockSumDayList_Body(list);
                int index = makeHeader.scmStockSumDayList_Header().length;
                String[] data = makeHeader.scmStockSumDayList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmStockSumMonth")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("???????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("???????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_STOCK_SUM_MONTH> list = excelMapper.scmStockSumMonthListDbList(excel);
                List<List<Object>> rows = makeBody.scmStockSumMonthList_Body(list);
                int index = makeHeader.scmStockSumMonthList_Header().length;
                String[] data = makeHeader.scmStockSumMonthList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("wmsInList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<WMS_IN_SUB> list = excelMapper.wmsInListDbList(excel);
                List<List<Object>> rows = makeBody.wmsInList_Body(list);
                int index = makeHeader.wmsInList_Header().length;
                String[] data = makeHeader.wmsInList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("wmsOutList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<WMS_OUT_SUB> list = excelMapper.wmsOutListDbList(excel);
                List<List<Object>> rows = makeBody.wmsOutList_Body(list);
                int index = makeHeader.wmsOutList_Header().length;
                String[] data = makeHeader.wmsOutList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("wmsStock")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<WMS_STOCK> list = excelMapper.wmsStockDbList(excel);
                List<List<Object>> rows = makeBody.wmsStockList_Body(list);
                int index = makeHeader.wmsStockList_Header().length;
                String[] data = makeHeader.wmsStockList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("wmsStockIOSumDay")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("???????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("???????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<WMS_STOCK> list = excelMapper.wmsStockIOSumDayDbList(excel);
                List<List<Object>> rows = makeBody.wmsStockIOSumDayList_Body(list);
                int index = makeHeader.wmsStockIOSumDayList_Header().length;
                String[] data = makeHeader.wmsStockIOSumDayList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("wmsStockIOSumMonth")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("???????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("???????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<WMS_STOCK> list = excelMapper.wmsStockIOSumMonthDbList(excel);
                List<List<Object>> rows = makeBody.wmsStockIOSumMonthList_Body(list);
                int index = makeHeader.wmsStockIOSumMonthList_Header().length;
                String[] data = makeHeader.wmsStockIOSumMonthList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("wmsOutReady")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("?????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("?????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<WMS_OUT_ORD_SUB> list = excelMapper.wmsOutReadyDbList(excel);
                List<List<Object>> rows = makeBody.wmsOutReady_Body(list);
                int index = makeHeader.wmsOutReady_Header().length;
                String[] data = makeHeader.wmsOutReady_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("crmPlan")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<CRM_PLAN> list = excelMapper.crmPlanDbList(excel);
                List<List<Object>> rows = makeBody.crmPlan_Body(list);
                System.out.println(excel.getRow1());
                System.out.println(excel.getRow1().equals("1"));

                if(excel.getRow1().equals("1")){
                    int index = makeHeader.crmPlan_Header1().length;
                    String[] data = makeHeader.crmPlan_Header1();

                    row = sheet.createRow(rowNo++);
                    row.setHeight((short)512);
                    for(i=0; index > i; i++){
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }
                }else if(excel.getRow1().equals("2")){
                    int index = makeHeader.crmPlan_Header2().length;
                    String[] data = makeHeader.crmPlan_Header2();

                    row = sheet.createRow(rowNo++);
                    row.setHeight((short)512);
                    for(i=0; index > i; i++){
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }
                }else if(excel.getRow1().equals("3")){
                    int index = makeHeader.crmPlan_Header3().length;
                    String[] data = makeHeader.crmPlan_Header3();

                    row = sheet.createRow(rowNo++);
                    row.setHeight((short)512);
                    for(i=0; index > i; i++){
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }
                }else if(excel.getRow1().equals("4")){
                    int index = makeHeader.crmPlan_Header4().length;
                    String[] data = makeHeader.crmPlan_Header4();

                    row = sheet.createRow(rowNo++);
                    row.setHeight((short)512);
                    for(i=0; index > i; i++){
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }
                }
                // DataTransfer [e]
                // (MakeHeader) ?????? ??????


                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("crmWorkList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<CRM_ORD_RECP> list = excelMapper.crmWorkListDbList(excel);
                List<List<Object>> rows = makeBody.crmWorkList_Body(list);
                int index = makeHeader.crmWorkList_Header().length;
                String[] data = makeHeader.crmWorkList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    if(i==8){
                        sheet.setColumnWidth((short)i, (short)9500);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }else {
                        sheet.setColumnWidth((short)i, (short)7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("crmProdOrder")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<CRM_ORD_RECP> list = excelMapper.crmProdOrderDbList(excel);
                List<List<Object>> rows = makeBody.crmProdOrder_Body(list);
                int index = makeHeader.crmProdOrder_Header().length;
                String[] data = makeHeader.crmProdOrder_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    if(i==8){
                        sheet.setColumnWidth((short)i, (short)9500);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }else {
                        sheet.setColumnWidth((short) i, (short) 7000);
                        cell = row.createCell(i);
                        cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                        cell.setCellValue(data[i]);
                    }
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("crmOutList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<CRM_OUT_SUB> list = excelMapper.crmOutListDbList(excel);
                List<List<Object>> rows = makeBody.crmOutList_Body(list);
                int index = makeHeader.crmOutList_Header().length;
                String[] data = makeHeader.crmOutList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            } else if(excel.getName().equals("qmsRecvList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<QMS_RECV_SUB> list = excelMapper.qmsRecvDbList(excel);
                List<List<Object>> rows = makeBody.qmsRecvList_Body(list);
                int index = makeHeader.qmsRecvList_Header().length;
                String[] data = makeHeader.qmsRecvList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("qmsProdList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<QMS_PROD_SUB> list = excelMapper.qmsProdDbList(excel);
                List<List<Object>> rows = makeBody.qmsProdList_Body(list);
                int index = makeHeader.qmsProdList_Header().length;
                String[] data = makeHeader.qmsProdList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("outsOutList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<OUTS_OUT_SUB> list = excelMapper.outsOutDbList(excel);
                List<List<Object>> rows = makeBody.outsOutList_Body(list);
                int index = makeHeader.outsOutList_Header().length;
                String[] data = makeHeader.outsOutList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("outsInList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<OUTS_IN_SUB> list = excelMapper.outsInDbList(excel);
                List<List<Object>> rows = makeBody.outsInList_Body(list);
                int index = makeHeader.outsInList_Header().length;
                String[] data = makeHeader.outsInList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("outsInReady")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<OUTS_OUT_BCR> list = excelMapper.outsInReadyDbList(excel);
                List<List<Object>> rows = makeBody.outsInReady_Body(list);
                int index = makeHeader.outsInReady_Header().length;
                String[] data = makeHeader.outsInReady_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("tpmMachineError")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<tpmMachineError> list = excelMapper.tpmMachineErrorDbList(excel);
                List<List<Object>> rows = makeBody.tpmMachineError_Body(list);
                int index = makeHeader.tpmMachineError_Header().length;
                String[] data = makeHeader.tpmMachineError_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmHInList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("?????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("?????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_HIN> list = excelMapper.scmHInListDbList(excel);
                List<List<Object>> rows = makeBody.scmHInList_Body(list);
                int index = makeHeader.scmHInList_Header().length;
                String[] data = makeHeader.scmHInList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmHInReady")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("???????????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("???????????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_HIN_READY> list = excelMapper.scmHInReadyListDbList(excel);
                List<List<Object>> rows = makeBody.scmHInReadyList_Body(list);
                int index = makeHeader.scmHInReadyList_Header().length;
                String[] data = makeHeader.scmHInReadyList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmPartCloseSumList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_CLOSE> list = excelMapper.scmPartCloseDbList(excel);
                List<List<Object>> rows = makeBody.scmPartCloseList_Body(list);
                int index = makeHeader.scmPartCloseList_Header().length;
                String[] data = makeHeader.scmPartCloseList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmHOutList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("?????????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("?????????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_HOUT_SUB> list = excelMapper.scmHOutListDbList(excel);
                List<List<Object>> rows = makeBody.scmHOutList_Body(list);
                int index = makeHeader.scmHOutList_Header().length;
                String[] data = makeHeader.scmHOutList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }else if(excel.getName().equals("scmStockRevList")){
                // ?????? ??????
                Sheet sheet = sxssfWorkbook.createSheet("??????????????????");
                // ?????? ?????? ?????? <????????? ????????? ????????? ????????? ??????>
                excelName = URLEncoder.encode("??????????????????","UTF-8");

                // DataTransfer [s]
                excel.setSite_code(getSessionData(req).getSite_code());
                List<SCM_STOCK_REV_LIST> list = excelMapper.scmStockRevListDbList(excel);
                List<List<Object>> rows = makeBody.scmStockRevList_Body(list);
                int index = makeHeader.scmStockRevList_Header().length;
                String[] data = makeHeader.scmStockRevList_Header();
                // DataTransfer [e]

                // (MakeHeader) ?????? ??????
                row = sheet.createRow(rowNo++);
                row.setHeight((short)512);
                for(i=0; index > i; i++){
                    sheet.setColumnWidth((short)i, (short)7000);
                    cell = row.createCell(i);
                    cell.setCellStyle(setHeadStyle(sxssfWorkbook));
                    cell.setCellValue(data[i]);
                }

                // (MakeBody) ?????? ??????
                for (i=0; list.size()>i; i++) {
                    row = sheet.createRow(rowNo++);
                    for (v=0; rows.get(i).size() > v; v++) {
                        cell = row.createCell(v);
                        cell.setCellStyle(setBodyStyle(sxssfWorkbook));
                        cell.setCellValue(String.valueOf(rows.get(i).get(v)));
                    }
                }
            }
            response(response,sxssfWorkbook,excelName,"ok",null);
        } catch (Exception e) {
            response(response,sxssfWorkbook,excelName,"fail",null);
        } finally {
            try {
                sxssfWorkbook.close();
            } catch (Exception ignore) {
            }
        }
    }

    public List<sysBPart> ExcelUploadReader(Excel excel) throws IOException, InvalidFormatException {
        OPCPackage opcPackage = OPCPackage.open(excel.getFiles().getInputStream());
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(opcPackage);
        Upload upload = new Upload();
        XSSFRow row = null;
        XSSFCell cell = null;
        XSSFSheet sheet = null;
        return upload.sysBPartListData(xssfWorkbook,sheet,row,cell);
    }

    public String excel_upload(Excel excel, HttpServletRequest req)  throws IOException, InvalidFormatException {
        OPCPackage opcPackage = OPCPackage.open(excel.getFiles().getInputStream());
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(opcPackage);
        Upload upload = new Upload();
        XSSFRow row = null;
        XSSFCell cell = null;
        XSSFSheet sheet = null;
        List<sysBPart> list = upload.sysBPartSetListData(xssfWorkbook,sheet,row,cell, req);
        try {
            for(sysBPart vo: list){
                excelMapper.sysBPartSetListData(vo);
            }
            return "???????????? ?????????????????????.";
        }catch (Exception e){
            return " ????????? ??? ?????? ????????????????????????. \n ?????? ???????????? ?????? ??? ???????????? ????????????.";
        }
    }
}

