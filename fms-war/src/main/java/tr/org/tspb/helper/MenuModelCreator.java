package tr.org.tspb.helper;

import java.util.List;
import javax.faces.model.SelectItem;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import tr.org.tspb.datamodel.gui.ModuleItem;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class MenuModelCreator {

    private MenuModel model;

    public MenuModelCreator() {
        model = new DefaultMenuModel();

        //First submenu
        DefaultSubMenu firstSubmenu = new DefaultSubMenu("Dynamic Submenu");

        DefaultMenuItem item = new DefaultMenuItem("External");
        item.setUrl("http://www.primefaces.org");
        item.setIcon("ui-icon-home");
        firstSubmenu.addElement(item);

        model.addElement(firstSubmenu);

        //Second submenu
        DefaultSubMenu secondSubmenu = new DefaultSubMenu("Dynamic Actions");

        item = new DefaultMenuItem("Save");
        item.setIcon("ui-icon-disk");
        item.setCommand("#{menuBean.save}");
        // item.setUpdate("messages");
        secondSubmenu.addElement(item);

        item = new DefaultMenuItem("Delete");
        item.setIcon("ui-icon-close");
        //item.setCommand("#{menuBean.delete}");
        item.setAjax(false);
        secondSubmenu.addElement(item);

        item = new DefaultMenuItem("Redirect");
        item.setIcon("ui-icon-search");
        //item.setCommand("#{menuBean.redirect}");
        secondSubmenu.addElement(item);

        model.addElement(secondSubmenu);
    }

    public MenuModelCreator(List<ModuleItem> accordionItems) {

        model = new DefaultMenuModel();
        for (ModuleItem moduleItem : accordionItems) {
            DefaultSubMenu submenu = new DefaultSubMenu(moduleItem.getName());
            model.addElement(submenu);
            for (SelectItem selectItem : moduleItem.getMyLinks()) {
                createMenuItem(submenu, selectItem);
            }
        }
    }

    private void createMenuItem(DefaultSubMenu submenu, SelectItem selectItem) {
        DefaultMenuItem item = new DefaultMenuItem(selectItem.getLabel());
        item.setUrl("http://www.primefaces.org");
        item.setIcon("ui-icon-home");
        submenu.addElement(item);
    }

    public MenuModel getModel() {
        return model;
    }
}
