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

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.tasks.LocalTask;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.doc.TaskPsiElement;
import com.intellij.util.containers.SoftHashMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Vladislav.Rassokhin
 */
public class ToTaskReference<T extends PsiElement> extends PsiReferenceBase<T> {

  private static final Key<CachedValue<Map<String, TaskPsiElement>>> ISSUE_REFERENCE_CACHE = Key.create("ISSUE_REFERENCE_CACHE");
  private static final CachedValueProvider<Map<String, TaskPsiElement>> CACHED_VALUE_PROVIDER = new CachedValueProvider<Map<String, TaskPsiElement>>() {
    @Nullable
    @Override
    public Result<Map<String, TaskPsiElement>> compute() {
      return Result.<Map<String, TaskPsiElement>>create(new SoftHashMap<String, TaskPsiElement>(), new ModificationTracker() {
        @Override
        public long getModificationCount() {
          return 0;
        }
      });
    }
  };

  public ToTaskReference(T element, TextRange range) {
    super(element, range, true);
  }

  public ToTaskReference(T element) {
    super(element, true);
  }

  @Nullable
  static Task getTask(@NotNull final String id, @NotNull final TaskManager manager) {
    for (Task task : manager.getCachedIssues(true)) {
      if (id.equals(task.getId())) {
        LocalTask localTask = manager.findTask(id);
        return localTask != null ? localTask : task;
      }
    }
    for (TaskRepository repository : manager.getAllRepositories()) {
      if (repository.extractId(id) == null) {
        continue;
      }
      try {
        Task issue = repository.findTask(id);
        if (issue != null) {
          LocalTask localTask = manager.findTask(id);
          return localTask != null ? localTask : issue;
        }
      } catch (Exception e) {
        NavigableTaskReferenceContributor.LOG.info(e);
      }
    }
    return null;
  }

  @Nullable
  public PsiElement resolve() {
    @NonNls String id = getValue();
    if (id.isEmpty()) {
      return null;
    }
    final Project project = getElement().getProject();
    final Map<String, TaskPsiElement> cache = CachedValuesManager.getManager(project).getCachedValue(project, ISSUE_REFERENCE_CACHE, CACHED_VALUE_PROVIDER, false);
    TaskPsiElement value = cache.get(id);
    if (value == null) {
      Task founded = null;
      final TaskManager manager = TaskManager.getManager(project);
      if (manager != null) {
        founded = getTask(id, manager);
      }
      if (founded != null) {
        value = new OpenableTaskPsiElement(PsiManager.getInstance(project), founded);
        cache.put(id, value);
      }
    }
    return value;
  }

  @NotNull
  public Object[] getVariants() {
    final List<Object> list = new ArrayList<Object>();
    final TaskManager manager = TaskManager.getManager(getElement().getProject());
    final LinkedHashSet<Task> tasks = new LinkedHashSet<Task>();
    if (manager != null) {
      final LocalTask active = manager.getActiveTask();
      tasks.add(active);
      for (Task task : manager.getLocalTasks()) {
        tasks.add(task);
      }
      for (Task task : manager.getIssues(null)) {
        tasks.add(task);
      }
      for (Task task : tasks) {
        list.add(LookupElementBuilder.create(task, task.getId()));
      }
    }
    return list.toArray();
  }
}
