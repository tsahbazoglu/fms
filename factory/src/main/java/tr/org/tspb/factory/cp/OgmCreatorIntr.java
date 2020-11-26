package tr.org.tspb.factory.cp;

import java.io.Serializable;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.MyForm;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.pojo.RoleMap;
import java.util.Map;
import org.bson.Document;
import tr.org.tspb.dao.MyActions;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface OgmCreatorIntr extends Serializable {

    MyProject getMyProject(Document dbo) throws NullNotExpectedException, FormConfigException;

    MyProject getMyProject(String projectKey) throws NullNotExpectedException, FormConfigException;

    MyForm getMyFormExternal(MyProject myProject, String configCollection, Map formSearch, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    MyForm getMyFormXsmall(MyProject myProject, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    MyForm getMyFormSmall(MyProject myProject, Document dboForm, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    MyForm getMyFormMedium(MyProject myProject, Document dboForm, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    MyForm getMyFormLarge(MyProject myProject, String configCollection, Map formSearch, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    MyField getMyField(MyForm myForm, Document docField, Map searchObject, RoleMap roleMap, UserDetail userDetail);

    MyField getMyFieldPivot(MyForm myForm, Document docField, Map searchObject, RoleMap roleMap, UserDetail userDetail);

    public MyMap getCrudObject();

    public MyActions getMyActions(MyForm myFormLarge, RoleMap roleMap, Document filter);

}
