package tr.org.tspb.factory.cp;

import java.io.Serializable;
import tr.org.tspb.exceptions.MongoOrmFailedException;
import tr.org.tspb.exceptions.NullNotExpectedException;
import tr.org.tspb.dao.MyField;
import tr.org.tspb.dao.FmsForm;
import tr.org.tspb.dao.MyProject;
import tr.org.tspb.pojo.RoleMap;
import java.util.Map;
import org.bson.Document;
import tr.org.tspb.dao.MyActions;
import tr.org.tspb.dao.MyMap;
import tr.org.tspb.dao.TagLogin;
import tr.org.tspb.exceptions.FormConfigException;
import tr.org.tspb.pojo.UserDetail;

/**
 *
 * @author Telman Şahbazoğlu
 */
public interface OgmCreatorIntr extends Serializable {

    MyProject getMyProject(Document dbo, TagLogin tagLogin) throws NullNotExpectedException, FormConfigException;

    MyProject getMyProject(String projectKey, TagLogin login) throws NullNotExpectedException, FormConfigException;

    FmsForm getMyFormExternal(MyProject myProject, String configCollection, Map formSearch, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    FmsForm getMyFormXsmall(MyProject myProject, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    FmsForm getMyFormSmall(MyProject myProject, Document dboForm, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    FmsForm getMyFormMedium(MyProject myProject, Document dboForm, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    FmsForm getMyFormLarge(MyProject myProject, String configCollection, Map formSearch, Map searchObject,
            RoleMap loginController, UserDetail userDetail)
            throws NullNotExpectedException, MongoOrmFailedException;

    MyField getMyField(FmsForm myForm, Document docField, Map searchObject, RoleMap roleMap, UserDetail userDetail) throws FormConfigException;

    MyField getMyFieldPivot(FmsForm myForm, Document docField, Map searchObject, RoleMap roleMap, UserDetail userDetail) throws FormConfigException;

    public MyMap getCrudObject();

    public MyActions getMyActions(FmsForm myFormLarge, RoleMap roleMap, Document filter, UserDetail userDetail);

}
