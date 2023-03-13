package dev.turtywurty.turtylib.client.ui.components.tabs;

import dev.turtywurty.turtylib.client.ui.ContainerMachineScreen;

public interface TabHolder<Type extends Tab> {
    TabPage[] getPages();
    
    ContainerMachineScreen<?> getScreen();
    
    Type getSelectedTab();
    
    Type[] getTabs();
    
    void setSelectedTab(Type tab);
}
