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

package com.intellij.tasks.navigation;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav Rassokhin
 */
@State(
    name = "TaskNavigationConfig",
    storages = {@Storage(file = StoragePathMacros.PROJECT_FILE)})
public class TaskNavigationConfig implements PersistentStateComponent<TaskNavigationConfig> {
  @Tag("configuration")
  public static class SharedConfiguration {
    @Attribute("annotation")
    public String annotation;
    @Attribute("element")
    public String element;
  }

  @Property(surroundWithTag = false)
  @AbstractCollection(surroundWithTag = false)
  public List<SharedConfiguration> configurations = new ArrayList<SharedConfiguration>();


  @Nullable
  @Override
  public TaskNavigationConfig getState() {
    final TaskNavigationConfig config = new TaskNavigationConfig();
    config.configurations = new ArrayList<SharedConfiguration>();
    config.configurations.addAll(this.configurations);
    return this;
  }

  @Override
  public void loadState(TaskNavigationConfig state) {
    this.configurations = new ArrayList<SharedConfiguration>();
    this.configurations.addAll(state.configurations);
  }
}