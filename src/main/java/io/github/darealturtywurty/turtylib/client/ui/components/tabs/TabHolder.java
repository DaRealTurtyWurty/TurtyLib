package io.github.darealturtywurty.turtylib.client.ui.components.tabs;

import io.github.darealturtywurty.turtylib.client.ui.ContainerMachineScreen;

public interface TabHolder<Type extends Tab> {
    TabPage[] getPages();
    
    ContainerMachineScreen<?> getScreen();
    
    Type getSelectedTab();
    
    Type[] getTabs();
    
    void setSelectedTab(Type tab);
}
