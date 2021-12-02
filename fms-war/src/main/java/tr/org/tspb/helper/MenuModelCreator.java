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
        DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Submenu")
                .build();

        DefaultMenuItem item = DefaultMenuItem.builder().build();// new DefaultMenuItem("External");
        item.setUrl("http://www.primefaces.org");
        item.setIcon("ui-icon-home");
        firstSubmenu.getElements().add(item);

        model.getElements().add(firstSubmenu);

        //Second submenu
        DefaultSubMenu secondSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Actions")
                .build();

        item = DefaultMenuItem.builder()
                .value("Save")
                .build();
        item.setIcon("ui-icon-disk");
        item.setCommand("#{menuBean.save}");
        // item.setUpdate("messages");
        secondSubmenu.getElements().add(item);

        item = DefaultMenuItem.builder()
                .value("Delete")
                .build();
        item.setIcon("ui-icon-close");
        //item.setCommand("#{menuBean.delete}");
        item.setAjax(false);
        secondSubmenu.getElements().add(item);

        item = DefaultMenuItem.builder()
                .value("Redirect")
                .build();
        item.setIcon("ui-icon-search");
        //item.setCommand("#{menuBean.redirect}");
        secondSubmenu.getElements().add(item);

        model.getElements().add(secondSubmenu);
    }

    public MenuModelCreator(List<ModuleItem> accordionItems) {

        model = new DefaultMenuModel();
        for (ModuleItem moduleItem : accordionItems) {
            DefaultSubMenu submenu = DefaultSubMenu.builder()
                    .label(moduleItem.getName())
                    .build();
            model.getElements().add(submenu);
            for (SelectItem selectItem : moduleItem.getMyLinks()) {
                createMenuItem(submenu, selectItem);
            }
        }
    }

    private void createMenuItem(DefaultSubMenu submenu, SelectItem selectItem) {
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value(selectItem.getLabel())
                .build();
        item.setUrl("http://www.primefaces.org");
        item.setIcon("ui-icon-home");
        submenu.getElements().add(item);
    }

    public MenuModel getModel() {
        return model;
    }
}
