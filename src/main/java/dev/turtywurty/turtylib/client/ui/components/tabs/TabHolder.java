package dev.turtywurty.turtylib.client.ui.components.tabs;

import dev.turtywurty.turtylib.client.ui.ContainerMachineScreen;

public interface TabHolder<T extends Tab> {
    TabPage[] getPages();
    
    ContainerMachineScreen<?> getScreen();
    
    T getSelectedTab();
    
    T[] getTabs();
    
    void setSelectedTab(Tab tab);
}
