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

package com.intellij.tasks.navigation;

import com.github.jk1.ytplugin.YouTrackPluginApi;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiManager;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.doc.TaskPsiElement;
import com.intellij.tasks.youtrack.YouTrackRepository;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vladislav.Rassokhin
 */
class OpenableTaskPsiElement extends TaskPsiElement {
  private static final Logger LOG = Logger.getInstance(OpenableTaskPsiElement.class);

  public OpenableTaskPsiElement(@NotNull PsiManager psiManager, @NotNull Task task) {
    super(psiManager, task);
  }

  @Override
  public void navigate(boolean b) {
    final Task task = getTask();
    TaskRepository repository = task.getRepository();
    if (repository instanceof YouTrackRepository) {
      YouTrackPluginApi component = getProject().getComponent(YouTrackPluginApi.class);
      if (component != null) {
        component.openIssueInToolWidow(task.getId());
        return;
      }
    }

    final String url = task.getIssueUrl();
    if (url != null) {
      BrowserUtil.open(url);
    } else {
      LOG.warn("Cannot open task in browser: url is null for task :" + task);
    }
  }

  @Override
  public boolean canNavigate() {
    return true;
  }

  @Override
  public boolean canNavigateToSource() {
    return canNavigate();
  }

  @Override
  public String getPresentableText() {
    return "Task: " + getName();
  }

}
