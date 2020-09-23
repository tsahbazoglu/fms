package tr.org.tspb.converter.component;

/**
 *
 * @author Telman Şahbazoğlu
 */
public abstract class PanelPopupCustom {

    private boolean open = true;
    private String message;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }
    private boolean renderedSouth = false;

    public boolean isRenderedSouth() {
        return renderedSouth;
    }

    public void setRenderedSouth(boolean renderedSouth) {
        this.renderedSouth = renderedSouth;
    }

    public PanelPopupCustom() {
    }

    public PanelPopupCustom(String title, String message) {
        this.title = title;
        this.message = message;

    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean getOpen() {
        return open;
    }

    public abstract String save();

    public String doNotSave() {
        return null;
    }

    public String saveAndLogout() {
        return null;
    }

    public String close() {
        open = false;
        return null;
    }

    public String open() {
        open = true;
        return null;
    }
}
