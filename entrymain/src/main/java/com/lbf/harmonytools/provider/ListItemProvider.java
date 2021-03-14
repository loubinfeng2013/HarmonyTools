package com.lbf.harmonytools.provider;

import com.lbf.harmonytools.ResourceTable;
import com.lbf.lib.imageloader.ImageLoader;
import ohos.agp.components.*;
import ohos.app.AbilityContext;

import java.util.List;

public class ListItemProvider extends BaseItemProvider {

    private List<String> urls;
    private AbilityContext context;
    private LayoutScatter scatter;

    public ListItemProvider(List<String> urls, AbilityContext context) {
        this.urls = urls;
        this.context = context;
        this.scatter = LayoutScatter.getInstance(context);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int i) {
        return urls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        ItemHolder holder = null;
        if (component == null) {
            component = scatter.parse(ResourceTable.Layout_list_item, null, false);
            holder = new ItemHolder(component);
            component.setTag(holder);
        } else {
            holder = (ItemHolder) component.getTag();
        }


        ImageLoader.with(context).loading(ResourceTable.Media_icon).load(urls.get(i)).into(holder.image);

        return component;
    }

    static class ItemHolder {
        Image image;

        public ItemHolder(Component component) {
            image = (Image) component.findComponentById(ResourceTable.Id_list_image);
        }
    }
}
