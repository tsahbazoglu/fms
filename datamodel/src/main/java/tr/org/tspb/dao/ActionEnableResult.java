/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.dao;

/**
 *
 * @author telman
 */
public class ActionEnableResult {

    private boolean enable = false;
    private String style;
    private String caption;
    private String dynamicButtonName;
    private String myaction;
    private String myActionType;
    private String javaFunc;
    private String successMessage;
    private String failMessage;
    private String dialog;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDynamicButtonName() {
        return dynamicButtonName;
    }

    public void setDynamicButtonName(String dynamicButtonName) {
        this.dynamicButtonName = dynamicButtonName;
    }

    public String getMyaction() {
        return myaction;
    }

    public void setMyaction(String myaction) {
        this.myaction = myaction;
    }

    public String getMyActionType() {
        return myActionType;
    }

    public void setMyActionType(String myActionType) {
        this.myActionType = myActionType;
    }

    public String getJavaFunc() {
        return javaFunc;
    }

    public void setJavaFunc(String javaFunc) {
        this.javaFunc = javaFunc;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public String getDialog() {
        return dialog;
    }

    public void setDialog(String dialog) {
        this.dialog = dialog;
    }

}
