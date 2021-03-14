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
public class TagMultiUpload {

    private boolean enable;
    private boolean upload;
    private boolean delete;
    private boolean download;
    private boolean list;

    public TagMultiUpload(boolean enable, boolean upload, boolean delete, boolean download, boolean list) {
        this.enable = enable;
        this.upload = upload;
        this.delete = delete;
        this.download = download;
        this.list = list;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isUpload() {
        return upload;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isDownload() {
        return download;
    }

    public boolean isList() {
        return list;
    }

}
