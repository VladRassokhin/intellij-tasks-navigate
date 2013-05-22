/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.tasks.navigation.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.tasks.navigation.TaskNavigationConfig;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav Rassokhin
 */
public class TaskNavigationConfigurable extends BaseConfigurable implements Configurable.NoScroll {
  private JPanel myPanel;
  private JPanel myAnnotationsPanel;
  private JBLabel myLabel;
  private final JBList myConfigurationsList;


  private final Project myProject;
  private final List<AnnotationConfig> myConfigurations = new ArrayList<AnnotationConfig>();
  private final CollectionListModel<AnnotationConfig> myConfigurationsListModel= new CollectionListModel<AnnotationConfig>();;

  public TaskNavigationConfigurable(@NotNull final Project project) {
    myProject = project;

    myConfigurationsList = new JBList();
    myConfigurationsList.getEmptyText().setText("No annotations configured");
    myConfigurationsList.setModel(myConfigurationsListModel);

    myLabel.setLabelFor(myConfigurationsList);
    final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(myConfigurationsList).disableUpDownActions();

    decorator.setAddAction(new AnActionButtonRunnable() {
      @Override
      public void run(AnActionButton button) {
        final AnnotationConfigEditForm form = new AnnotationConfigEditForm();
        form.setData(new AnnotationConfig());
        final DialogBuilder builder = new DialogBuilder(myProject);
        builder.setTitle("Add");
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(form.getPanel());
        builder.setPreferredFocusComponent(form.getPanel());
        if (builder.show() == DialogWrapper.OK_EXIT_CODE) {
          final AnnotationConfig newConfig = form.getData();
          myConfigurations.add(newConfig);
          myConfigurationsListModel.add(newConfig);
          setModified(true);
        }
        myConfigurationsList.requestFocus();
      }
    });
    decorator.setEditAction(new AnActionButtonRunnable() {
      @Override
      public void run(AnActionButton button) {
        AnnotationConfig config = getSelectedAnnotation();
        if (config != null) {
          final AnnotationConfigEditForm form = new AnnotationConfigEditForm();
          form.setData(config);
          final DialogBuilder builder = new DialogBuilder(myProject);
          builder.setTitle("Edit");
          builder.addOkAction();
          builder.addCancelAction();
          builder.setCenterPanel(form.getPanel());
          builder.setPreferredFocusComponent(form.getPanel());
          if (builder.show() == DialogWrapper.OK_EXIT_CODE) {
            if (!config.equals(form.getData())) {
              config.copyFrom(form.getData());
              setModified(true);
            }
            myConfigurationsList.repaint();
          }
        }
        myConfigurationsList.requestFocus();
      }
    });
    decorator.setRemoveAction(new AnActionButtonRunnable() {
      @Override
      public void run(AnActionButton button) {
        AnnotationConfig config = getSelectedAnnotation();
        if (config != null) {
          myConfigurationsListModel.remove(config);
          myConfigurations.remove(config);
          setModified(true);
        }
      }
    });

    final GridConstraints constraints = new GridConstraints();
    constraints.setFill(GridConstraints.FILL_BOTH);
    constraints.setVSizePolicy(GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK);
    myAnnotationsPanel.add(decorator.createPanel(), constraints);

    myConfigurationsList.setCellRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(
          JList list,
          Object value,
          int index,
          boolean isSelected,
          boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        AnnotationConfig config = (AnnotationConfig) value;
        setText(config.getPresentableName());
        return this;
      }
    });
  }

  @Nullable
  private AnnotationConfig getSelectedAnnotation() {
    return (AnnotationConfig) myConfigurationsList.getSelectedValue();
  }

  @Nls
  public String getDisplayName() {
    return "Navigation";
  }

  public String getHelpTopic() {
    return "reference.settings.project.tasks.navigation";
  }

  public JComponent createComponent() {
    return myPanel;
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return myConfigurationsList;
  }

  public void apply() throws ConfigurationException {
    final TaskNavigationConfig config = ServiceManager.getService(myProject, TaskNavigationConfig.class);
    config.configurations = ContainerUtil.map(myConfigurations, new Function<AnnotationConfig, TaskNavigationConfig.SharedConfiguration>() {
      @Override
      public TaskNavigationConfig.SharedConfiguration fun(AnnotationConfig config) {
        final TaskNavigationConfig.SharedConfiguration shared = new TaskNavigationConfig.SharedConfiguration();
        shared.annotation = config.getAnnotation();
        shared.element = config.getElement();
        return shared;
      }
    });
  }

  public void reset() {
    myConfigurations.clear();
    myConfigurationsListModel.removeAll();

    for (AnnotationConfig config : getConfigs()) {
      myConfigurations.add(config);
      myConfigurationsListModel.add(config);
    }
  }

  @NotNull
  private List<AnnotationConfig> getConfigs() {
    final TaskNavigationConfig config = ServiceManager.getService(myProject, TaskNavigationConfig.class);
    return ContainerUtil.map(config.configurations, new Function<TaskNavigationConfig.SharedConfiguration, AnnotationConfig>() {
      @Override
      public AnnotationConfig fun(TaskNavigationConfig.SharedConfiguration shared) {
        return new AnnotationConfig(shared.annotation, shared.element);
      }
    });
  }

  public void disposeUIResources() {
    myConfigurations.clear();
    myConfigurationsListModel.removeAll();
  }
}
