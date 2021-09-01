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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav.Rassokhin
 */
class TaskInPsiCommentReferenceProvider extends PsiReferenceProvider {
  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (!(element instanceof PsiComment)) {
      return PsiReference.EMPTY_ARRAY;
    }
    final PsiComment comment = (PsiComment) element;
    final Project project = comment.getProject();
    final TaskNavigationConfig config = project.getService(TaskNavigationConfig.class);
    if (!config.searchInComments) {
      return PsiReference.EMPTY_ARRAY;
    }
    if (comment.getTextLength() == 0) {
      return PsiReference.EMPTY_ARRAY;
    }
    final TaskManager manager = TaskManager.getManager(project);
    if (manager == null) {
      return PsiReference.EMPTY_ARRAY;
    }
    final TaskRepository[] repositories = manager.getAllRepositories();
    if (repositories.length == 0) {
      return PsiReference.EMPTY_ARRAY;
    }
    final String text = comment.getText();
    final List<TextRange> ranges = new ArrayList<>();
    for (TaskRepository repository : repositories) {
      int prev = 0;
      String toCheck = text;
      while (true) {
        final String id = repository.extractId(toCheck);
        if (id == null) {
          break;
        }
        int i = toCheck.indexOf(id);
        if (i < 0) {
          break;
        }
        ranges.add(new TextRange(prev + i, prev + i + id.length()));
        prev += i + id.length();
        toCheck = text.substring(prev);
      }
    }
    if (ranges.isEmpty()) {
      return PsiReference.EMPTY_ARRAY;
    }
    // TODO: Check intersecting ranges (between many TaskRepositories)

    final List<PsiCommentToTaskReference> references = new ArrayList<>(ranges.size());
    for (TextRange range : ranges) {
      references.add(new PsiCommentToTaskReference(comment, range));
    }
    return references.toArray(new PsiReference[0]);
  }
}
