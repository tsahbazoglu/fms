package tr.org.tspb.servlet;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import tr.org.tspb.constants.ProjectConstants;
import static tr.org.tspb.constants.ProjectConstants.*;
import tr.org.tspb.dao.FmsForm;

/**
 *
 * @author Telman Şahbazoğlu
 */
@WebServlet(name = "DownloadDynamicContentServlet", urlPatterns = {"/downloadDynamicContentServlet/*"},
        initParams = {
            @WebInitParam(name = "jasperPath", value = ProjectConstants.PATH_JASPER_JRXML)
        })
public class DownloadDynamicContentServlet extends HttpServlet {

    @Inject
    private Logger logger;

    /**
     * Process GET request.
     *
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        safeProcessRequest(request, response);
    }

    private void safeProcessRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException ex) {
            logger.error("error occured", ex);
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        /* Get inputs from session */
        Reader reader = (Reader) request.getSession().getAttribute(UYS_DOWNLOAD_READER);
        FmsForm selectedForm = (FmsForm) request.getSession().getAttribute(UYS_SELECTED_FROM);
        String excelFormatPath = (String) request.getSession().getAttribute(EXCEL_FORMAT_PATH);
        String excelLibraryPoi = (String) request.getSession().getAttribute(EXCEL_LIBRARY_POI);
        Set uysDownloadRoles = (Set) request.getSession().getAttribute(UYS_DOWNLOAD_ROLES);

        String downloadFileName = URLDecoder.decode(selectedForm.getName().concat(".xls"), "UTF-8");

        String user_agent = request.getHeader("user-agent");
        boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);
        if (isInternetExplorer) {
            downloadFileName = URLEncoder.encode(downloadFileName, "utf-8");
        } else {
            downloadFileName = MimeUtility.encodeWord(downloadFileName);
        }
        String contentType = getServletContext().getMimeType(downloadFileName);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        if (contentType.startsWith("text")) {
            contentType += ";charset=UTF-8";
        }

        ServletOutputStream servletOutputStream = response.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            BufferedReader bufferedReader = new BufferedReader(reader);

            bufferedReader.readLine();

            String caption = bufferedReader.readLine();
            List<String> columns;

            if (caption != null) {
                columns = Arrays.asList(caption.split(";"));
            } else {
                throw new RuntimeException("caption is null");
            }

            List<List<String>> list = new ArrayList<>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                list.add(Arrays.asList(line.split(";")));
            }

            if (excelFormatPath != null) {
                writeToExistingExcel(excelFormatPath, list, columns, user_agent, user_agent, baos, uysDownloadRoles);
            } else if (excelLibraryPoi != null) {
                writeToExcellWithPoi(excelFormatPath, list, columns, user_agent, user_agent, baos);
            } else {
                writeToExcel(list, columns, user_agent, user_agent, baos);
            }

            //write baos to servlet output
            response.setContentLength(baos.size());
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + downloadFileName + "\"");
            response.setContentType(contentType);
            response.setCharacterEncoding("UTF8");
            baos.writeTo(servletOutputStream);

        } catch (Exception ex) {
            logger.error("error occured", ex);
            response.sendRedirect(request.getContextPath());
        } finally {
            servletOutputStream.flush();
            servletOutputStream.close();
            baos.close();
        }
    }

    public void writeToExcel(List<List<String>> reader, List<String> columns,
            String sessionID, String fileName, OutputStream outputStream) throws IOException {

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {

            HSSFSheet sheet__ALL = workbook.createSheet("Hepsi");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle cellHeaderStyle = workbook.createCellStyle();
            cellHeaderStyle.setFont(headerFont);

            CellStyle cellNumberStyle = workbook.createCellStyle();
            cellNumberStyle.setDataFormat((short) 0); //0 = "General"
            int rownum = 0;

            Row rowHeader = sheet__ALL.createRow(rownum++);
            int x = 0;
            for (String key : columns) {
                Cell cell = rowHeader.createCell(x++);
                cell.setCellStyle(cellHeaderStyle);
                cell.setCellValue(key);
            }

            for (List<String> record : reader) {
                Row row = sheet__ALL.createRow(rownum++);
                int column = 0;
                for (String key : record) {
                    Cell cell = row.createCell(column);
                    cell.setCellValue(key);
                    column++;
                }
            }

            sheet__ALL.autoSizeColumn(0);
            sheet__ALL.autoSizeColumn(1);
            sheet__ALL.autoSizeColumn(2);
            sheet__ALL.autoSizeColumn(3);
            sheet__ALL.autoSizeColumn(4);
            sheet__ALL.autoSizeColumn(5);
            sheet__ALL.autoSizeColumn(6);
            sheet__ALL.autoSizeColumn(7);
            sheet__ALL.autoSizeColumn(8);
            sheet__ALL.autoSizeColumn(9);

            workbook.write(outputStream);
        }
    }

    public void writeToExistingExcel(String excellFormatPath, List<List<String>> reader,
            List<String> columns, String sessionID, String fileName, OutputStream outputStream, Set roles) throws IOException {

        boolean support_3131 = true;
        boolean support_3134 = true;
        if (support_3131 == true && columns.indexOf("Konsolide") < 0) {
            /*
             cp /home/telman/ufrs_201306_balance_abstract_sablon.xls /home/telman/ufrs_201306_balance_abstract_sablon_solo.xls
             cp /home/telman/ufrs_201306_income_abstract_sablon.xls /home/telman/ufrs_201306_income_abstract_sablon_solo.xls
             cp /home/telman/ufrs_201306_income_detail_sablon.xls /home/telman/ufrs_201306_income_detail_sablon_solo.xls
             cp /home/telman/ufrs_201306_income_detail_sablon.xls /home/telman/ufrs_201306_income_detail_sablon_solo_pys.xls
             */
            if ("/home/telman/ufrs_201306_balance_abstract_sablon.xls".equals(excellFormatPath)) {
                excellFormatPath = "/home/telman/ufrs_201306_balance_abstract_sablon_solo.xls";
            } else if ("/home/telman/ufrs_201306_income_abstract_sablon.xls".equals(excellFormatPath)) {
                excellFormatPath = "/home/telman/ufrs_201306_income_abstract_sablon_solo.xls";
            } else if ("/home/telman/ufrs_201306_income_detail_sablon.xls".equals(excellFormatPath)) {
                excellFormatPath = "/home/telman/ufrs_201306_income_detail_sablon_solo.xls";
                if (roles.contains("pyuser")) {
                    excellFormatPath = "/home/telman/ufrs_201306_income_detail_sablon_solo_pys.xls";
                }
            }
        }

        try (InputStream inp = new FileInputStream(excellFormatPath); Workbook workbook = new HSSFWorkbook(inp);) {
            Sheet sheet__ALL = workbook.getSheetAt(0);//("Hepsi");

            Iterator<Row> rowIterator = sheet__ALL.iterator();
            Iterator<List<String>> readerIterator = reader.iterator();

            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (support_3134 == true && !readerIterator.hasNext()) {
                    continue;
                }

                List<String> record = readerIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                int column = 0;

                for (String key : record) {
                    Cell cell;
                    if (!cellIterator.hasNext() || cellIterator.next().getCellType() != Cell.CELL_TYPE_FORMULA) {
                        cell = row.createCell(column);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        try {
                            cell.setCellValue(Long.parseLong(key.replaceAll("[.]", "")));
                        } catch (NumberFormatException ex) {
                            String value = key.trim();
                            if (value.isEmpty()) {
                                cell.setCellValue(0L);
                            } else {
                                cell.setCellValue(value);
                            }
                        }
                    }
                    column++;
                }
            }
            workbook.write(outputStream);
        }
    }

    public void writeToExcellWithPoi(String excellFormatPath, List<List<String>> reader, List<String> columns, String sessionID, String fileName, OutputStream outputStream) throws IOException {

        try (InputStream inp = new FileInputStream(excellFormatPath);
                Workbook workbook = new HSSFWorkbook(inp);) {
            Sheet sheet__ALL = workbook.getSheetAt(0);//("Hepsi");

            Iterator<Row> rowIterator = sheet__ALL.iterator();
            Iterator<List<String>> readerIterator = reader.iterator();

            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<String> record = readerIterator.next();
                //For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();

                int column = 0;

                for (String key : record) {
                    Cell cell;
                    if (!cellIterator.hasNext() || cellIterator.next().getCellType() != Cell.CELL_TYPE_FORMULA) {
                        cell = row.createCell(column);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        try {
                            cell.setCellValue(Long.parseLong(key.replaceAll("[.]", "")));
                        } catch (NumberFormatException ex) {
                            String value = key.trim();
                            if (value.isEmpty()) {
                                cell.setCellValue(0L);
                            } else {
                                cell.setCellValue(value);
                            }
                        }
                    }
                    column++;
                }
            }
            workbook.write(outputStream);
        }
    }

}
