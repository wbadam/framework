/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui;

import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.event.dnd.DropEvent;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.event.dnd.grid.GridDropEvent;
import com.vaadin.server.ClientConnector;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
import com.vaadin.shared.ui.grid.GridDropTargetExtensionRpc;
import com.vaadin.shared.ui.grid.GridDropTargetExtensionState;

/**
 * Makes Grid an HTML5 drop target. This is the server side counterpart of
 * GridDropTargetExtensionConnector.
 *
 * @author Vaadin Ltd
 * @since
 */
public class GridDropTargetExtension<T> extends DropTargetExtension<Grid<T>> {
    public GridDropTargetExtension(Grid<T> target) {
        super(target);
    }

    @Override
    protected void registerDropTargetRpc(Grid<T> target) {
        registerRpc(
                (GridDropTargetExtensionRpc) (types, data, dropEffect, dragSourceId, rowKey) -> {
                    GridDropEvent<T> event = new GridDropEvent<>(target, types,
                            data, dropEffect, getDragSource(dragSourceId),
                            rowKey);

                    fireEvent(event);
                });
    }

    @Override
    protected GridDropTargetExtensionState getState() {
        return (GridDropTargetExtensionState) super.getState();
    }

    @Override
    protected GridDropTargetExtensionState getState(boolean markAsDirty) {
        return (GridDropTargetExtensionState) super.getState(markAsDirty);
    }
}
