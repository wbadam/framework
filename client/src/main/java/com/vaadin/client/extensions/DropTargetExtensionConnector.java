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
package com.vaadin.client.extensions;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
import com.vaadin.shared.ui.dnd.DropTargetState;

@Connect(DropTargetExtension.class)
public class DropTargetExtensionConnector extends AbstractExtensionConnector {

    private static final String CLASS_DRAG_OVER = "v-drag-over";

    @Override
    protected void extend(ServerConnector target) {
        Element dropTargetElement = getDropTargetElement();

        // dragenter event
        addEventListener(dropTargetElement, BrowserEvents.DRAGENTER,
                this::onDragEnter);

        // dragover event
        addEventListener(dropTargetElement, BrowserEvents.DRAGOVER,
                this::onDragOver);

        // dragleave event
        addEventListener(dropTargetElement, BrowserEvents.DRAGLEAVE,
                this::onDragLeave);

        // drop event
        addEventListener(dropTargetElement, BrowserEvents.DROP, this::onDrop);

        // TODO: 23/01/2017 Consider removing event listeners on detach
    }

    /**
     * Finds the drop target element within the widget. By default, returns the
     * topmost element.
     *
     * @return the drop target element in the parent widget.
     */
    protected Element getDropTargetElement() {
        return ((ComponentConnector) getParent()).getWidget().getElement();
    }

    protected void onDragEnter(Event event) {
        addTargetIndicator(getDropTargetElement());
    }

    protected void onDragOver(Event event) {
        if (dragOverAllowed(event)) {
            // Set dropEffect parameter
            if (getState().dropEffect != null) {
                event.getDataTransfer().setDropEffect(
                        DataTransfer.DropEffect.valueOf(getState().dropEffect));
            }

            // Prevent default to allow drop
            event.preventDefault();
        } else {
            // Remove drop effect
            event.getDataTransfer().setDropEffect(DataTransfer.DropEffect.NONE);

            // Remove drop target indicator
            removeTargetIndicator(getDropTargetElement());
        }
    }

    private boolean dragOverAllowed(Event event) {
        if (getState().dragOverCriteria != null) {
            return executeScript(event, getState().dragOverCriteria);
        }

        // Allow when criteria not set
        return true;
    }

    protected void onDragLeave(Event event) {
        removeTargetIndicator(getDropTargetElement());
    }

    protected void onDrop(Event event) {
        if (dropAllowed(event)) {
            event.preventDefault();

            // Initiate firing server side drop event
            JsArrayString types = getTypes(event.getDataTransfer());
            Map<String, String> data = new LinkedHashMap<>();
            for (int i = 0; i < types.length(); i++) {
                data.put(types.get(i),
                        event.getDataTransfer().getData(types.get(i)));
            }

            getRpcProxy(DropTargetRpc.class).drop(data, getState().dropEffect);
        }
    }

    private boolean dropAllowed(Event event) {
        if (getState().dropCriteria != null) {
            return executeScript(event, getState().dropCriteria);
        }

        // Allow when criteria not set
        return true;
    }

    private void addTargetIndicator(Element element) {
        element.addClassName(CLASS_DRAG_OVER);
    }

    private void removeTargetIndicator(Element element) {
        element.removeClassName(CLASS_DRAG_OVER);
    }

    private native void addEventListener(Element element, String eventName,
            EventListener listener)/*-{
        var listenerFunction = function (event) {
            listener.@com.google.gwt.user.client.EventListener::onBrowserEvent(*)(event);
        }

        element.addEventListener(eventName, listenerFunction, false);
    }-*/;

    private native boolean executeScript(NativeEvent event, String script)/*-{
        return new Function('event', script)(event);
    }-*/;

    private native JsArrayString getTypes(DataTransfer dataTransfer)/*-{
        return dataTransfer.types;
    }-*/;

    @Override
    public DropTargetState getState() {
        return (DropTargetState) super.getState();
    }
}
