package harvesterUI.shared.dataTypes;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 11-04-2011
 * Time: 13:54
 */
public class DataContainer extends BaseTreeModel {

    public DataContainer() {}

    public DataContainer(String id) {
        set("id", id);
    }

    public void setId(String id) {
        set("id",id);
    }

    public String getId() {
        return (String) get("id");
    }
}
