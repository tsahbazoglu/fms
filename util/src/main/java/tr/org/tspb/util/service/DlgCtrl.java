package tr.org.tspb.util.service;

import static tr.org.tspb.constants.ProjectConstants.MESSAGE_DIALOG;
import static tr.org.tspb.constants.ProjectConstants.WARNING;
import java.io.Serializable;
import org.primefaces.PrimeFaces;
import tr.org.tspb.util.stereotype.MyController;

/**
 *
 * @author Telman Şahbazoğlu
 */
@MyController
public class DlgCtrl implements Serializable {

    private String position = "center";
    private String imageLocation;
    private String imageAlt;
    private String title;
    private String message;
    private String style = "font-size:13px;";
    private boolean renderedButon;
    private boolean renderedOkButon;
    private boolean rendered;
    private boolean visible;
    private boolean esignInstallNote = true;

    public DlgCtrl() {
    }

    public DlgCtrl(String imageLocation, String imageAlt, String title, String msg, boolean rendered, boolean visible) {
        this.imageLocation = imageLocation;
        this.imageAlt = imageAlt;
        this.title = title;
        this.message = msg;
        this.rendered = rendered;
        this.visible = visible;
    }

    public void bulkSet(String imageLocation, String imageAlt, String title, String msg, boolean rendered, boolean visible) {
        this.imageLocation = imageLocation;
        this.imageAlt = imageAlt;
        this.title = title;
        this.message = msg;
        this.rendered = rendered;
        this.visible = visible;
    }

    private static final String BILGILENDIRME = "Bilgilendirme";

    public void showPopup(String title, String msg, String clientSideDialogName) {
        bulkSet(null, null, title, msg, true, true);
        setRenderedButon(false);
        setRenderedOkButon(false);
        setRendered(false);
        setStyle("font-size:13px;");
        setTitle(title);
        showPopup(clientSideDialogName);
    }

    public void showPopupInfo(String msg, String clientSideDialogName) {
        bulkSet(null, null, BILGILENDIRME, msg, true, true);
        setRenderedButon(false);
        setRenderedOkButon(false);
        setRendered(false);
        setStyle("font-size:13px;");
        setTitle(BILGILENDIRME);
        showPopup(clientSideDialogName);
    }

    public void showPopupWarning(String msg, String clientSideDialogName) {
        bulkSet(null, null, "Uyarı", msg, true, true);
        setRenderedButon(false);
        setRenderedOkButon(false);
        setRendered(false);
        setStyle("font-size:13px;");
        setTitle("Uyarı");
        showPopup(clientSideDialogName);
    }

    public void showPopupError(String msg) {
        bulkSet(null, null, "Hata", new StringBuilder("<br/><br/>").append(msg).toString(), true, true);
        setRenderedButon(false);
        setRenderedOkButon(false);
        setRendered(false);
        setStyle("font-size:13px;");
        setTitle("Hata");
        showPopup(MESSAGE_DIALOG);
    }

    /**
     *
     * @param title
     * @param msg
     */
    public void showPopupInfo2(String title, String msg) {
        bulkSet(null, null, title, msg, true, true);
        setRenderedButon(false);
        setRenderedOkButon(false);
        setRendered(false);
        setStyle("font-size:13px;");
        showPopup(MESSAGE_DIALOG);
    }

    public void showPopupInfoWithOk(String msg, String clientSideDialogName) {
        bulkSet(null, null, BILGILENDIRME, msg, true, true);
        setRenderedButon(false);
        setRenderedOkButon(true);
        setRendered(false);
        setStyle("font-size:13px;");
        setTitle(BILGILENDIRME);
        showPopup(clientSideDialogName);
    }

    /**
     *
     * @param msg
     */
    public void showMsgDlgWarnWithB(String msg) {
        bulkSet(null, null, WARNING, msg, true, true);
        setRenderedButon(true);
        setRenderedOkButon(false);
        setRendered(false);
        setStyle("font-size:13px;");
        setTitle(WARNING);
        showPopup(MESSAGE_DIALOG);
    }

    public void showPopup(String clientSideDialogName) {
        PrimeFaces.current().executeScript("PF('" + clientSideDialogName + "').show()");
    }

    public void hidePopup(String clientSideDialogName) {
        PrimeFaces.current().executeScript("PF('" + clientSideDialogName + "').hide()");
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public boolean isRenderedButon() {
        return renderedButon;
    }

    public void setRenderedButon(boolean renderedButon) {
        this.renderedButon = renderedButon;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public boolean isRenderedOkButon() {
        return renderedOkButon;
    }

    public void setRenderedOkButon(boolean renderedOkButon) {
        this.renderedOkButon = renderedOkButon;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isEsignInstallNote() {
        return esignInstallNote;
    }

    public void setEsignInstallNote(boolean esignInstallNote) {
        this.esignInstallNote = esignInstallNote;
    }

}
