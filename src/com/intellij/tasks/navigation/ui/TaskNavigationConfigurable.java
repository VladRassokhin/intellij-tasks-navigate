/*
 * Copyright 2000-2015 JetBrains s.r.o.
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
import com.intellij.tasks.navigation.TaskNavigationConfig;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Vladislav Rassokhin
 */
public class TaskNavigationConfigurable extends BaseConfigurable implements Configurable.NoScroll {
  private JPanel myPanel;
  private JCheckBox mySearchInComments;

  private final Project myProject;

  public TaskNavigationConfigurable(@NotNull final Project project) {
    myProject = project;

    mySearchInComments.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(@NotNull ChangeEvent e) {
        checkModified();
      }
    });
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
    return mySearchInComments;
  }

  public void apply() throws ConfigurationException {
    final TaskNavigationConfig config = getConfig();

    config.searchInComments = mySearchInComments.isSelected();

    checkModified();
  }

  public void reset() {
    final TaskNavigationConfig config = getConfig();

    mySearchInComments.setSelected(config.searchInComments);

    checkModified();
  }

  @NotNull
  private TaskNavigationConfig getConfig() {
    return ServiceManager.getService(myProject, TaskNavigationConfig.class);
  }

  public void disposeUIResources() {
  }

  private void checkModified() {
    setModified(isModifiedCheckImpl());
  }

  private boolean isModifiedCheckImpl() {
    final TaskNavigationConfig config = getConfig();

    return mySearchInComments.isSelected() != config.searchInComments;
  }
}
