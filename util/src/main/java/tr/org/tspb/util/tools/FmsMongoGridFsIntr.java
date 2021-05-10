/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.util.tools;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import org.bson.types.ObjectId;
import tr.org.tspb.dao.FmsFile;
import tr.org.tspb.dao.MyFile;

/**
 *
 * @author telman
 */
public interface FmsMongoGridFsIntr extends Serializable {

    public MyFile findFileAsMyFile(String db, ObjectId objectId) throws IOException;

    public MyFile findFileAsMyFileInputStream(String db, ObjectId objectId) throws IOException;

    public List<MyFile> findFilesAsMyFile(String db, DBObject filter) throws IOException;

    public GridFSDBFile findFile(String db, DBObject filter) throws RuntimeException;

    public GridFSDBFile findFile(String db, ObjectId objectId) throws RuntimeException;

    public List<MyFile> findFileList(String db, BasicDBObject basicDBObject, int skip, int limit);

    public List<FmsFile> findFilesAsFmsFileNoContent(String db, BasicDBObject basicDBObject, int skip, int limit);

    public List<GridFSDBFile> findFiles(String db, DBObject filter);

    public List<GridFSDBFile> findFiles(String db, String filename);

    public int countFile(String db, BasicDBObject basicDBObject);

    public GridFSInputFile createFile(String gridfsdb, File file) throws IOException;

    public GridFSInputFile createFile(String gridfsdb, InputStream inputStream);

    public GridFSInputFile createFile(String gridfsdb, byte[] bytes);

    public void removeFile(String db, DBObject filter) throws RuntimeException;

    public void removeFile(String db, ObjectId objectId) throws RuntimeException;

    public void copyFiles(String fromDb, String toDb, DBObject fromSearch) throws IOException;

}
