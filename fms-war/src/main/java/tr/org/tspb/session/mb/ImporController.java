package tr.org.tspb.session.mb;

import tr.org.tspb.common.services.AppScopeSrvCtrl;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import tr.org.tspb.service.RepositoryService;
import tr.org.tspb.datamodel.custom.TuikData;
import tr.org.tspb.util.stereotype.MyController;
import tr.org.tspb.service.FormService;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
public class ImporController implements Serializable {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private AppScopeSrvCtrl uysApplicationMB;

    @Inject
    FormService formService;

    private UploadedFile uploadedFile;
    private List<TuikData> listOfToBeUpsert = new ArrayList<>();

    public void uploadImportDataMB(FileUploadEvent event) {

        uysApplicationMB.initKpbMemberCache();

        listOfToBeUpsert = new ArrayList<>();

        uploadedFile = event.getFile();

        try {

            InputStream is = uploadedFile.getInputstream();

            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

            if ("imimTuik".equals(formService.getMyForm().getKey())) {
                uploadImimTuik(sheet);
            }

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String writeToDb() {
        if ("imimTuik".equals(formService.getMyForm().getKey())) {
            writeImimTuik();
        }
        return null;
    }

    private void uploadImimTuik(XSSFSheet sheet) {
        int rowcount = sheet.getLastRowNum() + 1;

        for (int i = 1; i < rowcount; i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            XSSFCell cell1 = row.getCell(1);
            XSSFCell cell2 = row.getCell(2);
            XSSFCell cell3 = row.getCell(3);
            XSSFCell cell4 = row.getCell(4);

            listOfToBeUpsert.add(new TuikData(cell0.getStringCellValue(),
                    cell1.getNumericCellValue(),
                    cell2.getNumericCellValue(),
                    cell3.getNumericCellValue(),
                    cell4.getNumericCellValue()));

        }
    }

    private void writeImimTuik() {
        repositoryService.writeImimTuik(formService.getMyForm(), listOfToBeUpsert);
    }

    protected void showNOKMessage(String nokMessage) {
        addMessage(null,//
                MessageFormat.format("{0}", nokMessage),//
                nokMessage,
                FacesMessage.SEVERITY_ERROR);
    }

    protected void addMessage(String componentId, String summary, String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(componentId, new FacesMessage(severity, summary, message));
    }

    public AppScopeSrvCtrl getUysApplicationMB() {
        return uysApplicationMB;
    }

    public void setUysApplicationMB(AppScopeSrvCtrl uysApplicationMB) {
        this.uysApplicationMB = uysApplicationMB;
    }

    public String getImportTextFormat() {
        return formService.getMyForm() == null ? "no structure" : formService.getMyForm().getImportTextFormat();
    }

    public List<TuikData> getListOfToBeUpsert() {
        return Collections.unmodifiableList(listOfToBeUpsert);
    }

    public void setListOfToBeUpsert(List<TuikData> listOfToBeUpsert) {
        this.listOfToBeUpsert = listOfToBeUpsert;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

}
