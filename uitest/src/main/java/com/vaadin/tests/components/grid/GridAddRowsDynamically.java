package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class GridAddRowsDynamically extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        final BeanItemContainer<Bean> container = new BeanItemContainer<Bean>(
                Bean.class, getBeans(0, 1));

        Grid grid = new Grid(container);
        grid.setHeight("350px");

        Button add = new Button("Add rows", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                container.addAll(getBeans(1, 50));
            }
        });
        add.setId("add_button");

        Layout layout = new VerticalLayout();
        layout.addComponents(grid, add);

        addComponent(layout);
    }

    private List<Bean> getBeans(int start, int end) {
        List<Bean> l = new ArrayList<Bean>();
        for (int i = start; i < end; i++) {
            l.add(new Bean("id_" + i, "value_" + i));
        }
        return l;
    }

    public static class Bean {
        String id;
        String value;

        public Bean(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 7422;
    }

    @Override
    public String getTestDescription() {
        return "Adding rows dynamically in grid causes an empty row";
    }
}
