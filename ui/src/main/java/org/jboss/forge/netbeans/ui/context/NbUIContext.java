/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.forge.netbeans.ui.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.netbeans.runtime.FurnaceService;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class NbUIContext extends AbstractUIContext {

    private final UISelection<Resource<?>> initialSelection;

    public NbUIContext() {
        List<Resource<?>> resources = getSelectedResources();
        this.initialSelection = Selections.from(resources);
        initialize();
    }

    
    public NbUIContext(UISelection<Resource<?>> initialSelection) {
        this.initialSelection = initialSelection;
        initialize();
    }

    @Override
    public <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection() {
        return (UISelection<SELECTIONTYPE>) initialSelection;
    }

    @Override
    public UIProvider getProvider() {
        return NbUIProvider.INSTANCE;
    }

    private void initialize() {
        Imported<UIContextListener> services = FurnaceService.INSTANCE
                .lookupImported(UIContextListener.class);
        if (services != null) {
            for (UIContextListener listener : services) {
                try {
                    listener.contextInitialized(this);
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }
    }

    @Override
    public void close() {
        super.close();
        Imported<UIContextListener> services = FurnaceService.INSTANCE
                .lookupImported(UIContextListener.class);
        if (services != null) {
            for (UIContextListener listener : services) {
                try {
                    listener.contextDestroyed(this);
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }
    }
    
    /**
     * @return a list of the selected resources from the selected nodes
     */
    private List<Resource<?>> getSelectedResources() {
        ResourceFactory resourceFactory = FurnaceService.INSTANCE.getResourceFactory();
        List<Resource<?>> resources = new ArrayList<>();
        Node[] currentNodes = TopComponent.getRegistry().getCurrentNodes();
        if (currentNodes != null) {
            for (Node currentNode : currentNodes) {
                DataObject dataObject = currentNode.getLookup().lookup(DataObject.class);
                File file = FileUtil.toFile(dataObject.getPrimaryFile());
                Resource<File> resource = resourceFactory.create(file);
                resources.add(resource);
            }
        }
        return resources;
    }
}
