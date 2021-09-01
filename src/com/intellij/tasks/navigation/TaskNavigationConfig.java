/*
 * Copyright 2000-2021 JetBrains s.r.o.
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
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.Nullable;

/**
 * @author Vladislav Rassokhin
 */
@State(name = "TaskNavigationConfig")
public class TaskNavigationConfig implements PersistentStateComponent<TaskNavigationConfig> {
  @Tag("search-in-comments")
  public boolean searchInComments = true;

  @Nullable
  @Override
  public TaskNavigationConfig getState() {
    final TaskNavigationConfig config = new TaskNavigationConfig();
    config.searchInComments = this.searchInComments;
    return this;
  }

  @Override
  public void loadState(TaskNavigationConfig state) {
    this.searchInComments = state.searchInComments;
  }
}
