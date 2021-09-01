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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
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
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Vladislav.Rassokhin
 */
public class ToTaskReference<T extends PsiElement> extends PsiReferenceBase<T> {
  private static final Logger LOG = Logger.getInstance(ToTaskReference.class);


  private static final Key<CachedValue<Map<String, TaskPsiElement>>> ISSUE_REFERENCE_CACHE = Key.create("ISSUE_REFERENCE_CACHE");
  private static final CachedValueProvider<Map<String, TaskPsiElement>> CACHED_VALUE_PROVIDER = new CachedValueProvider<Map<String, TaskPsiElement>>() {
    @NotNull
    @Override
    public Result<Map<String, TaskPsiElement>> compute() {
      return Result.<Map<String, TaskPsiElement>>create(ContainerUtil.createSoftMap(), new ModificationTracker() {
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

  /**
   * Is task should be resolved if it's ID is not matches with input text
   */
  protected boolean isAllowPartialMatch() {
    return false;
  }

  @Nullable
  private Task getTask(@NotNull final String id, @NotNull final String text, @NotNull final TaskManager manager) {
    for (Task task : manager.getCachedIssues(true)) {
      if (id.equals(task.getId())) {
        LocalTask localTask = manager.findTask(id);
        return localTask != null ? localTask : task;
      }
    }
    for (TaskRepository repository : manager.getAllRepositories()) {
      final String id2 = repository.extractId(text);
      if (id2 == null) {
        continue;
      }
      if (!id2.equals(id)) continue;
      try {
        Task issue = repository.findTask(id);
        if (issue != null) {
          LocalTask localTask = manager.findTask(id);
          return localTask != null ? localTask : issue;
        }
      } catch (Exception e) {
        LOG.info(e);
      }
    }
    return null;
  }

  private static String getId(@NotNull final String text, @NotNull final TaskManager manager) {
    for (Task task : manager.getCachedIssues(true)) {
      if (task.getId().equals(text)) return task.getId();
      final TaskRepository repository = task.getRepository();
      if (repository != null) {
        final String id = repository.extractId(text);
        if (id != null) {
          return id;
        }
      }
    }
    for (Task task : manager.getLocalTasks(true)) {
      if (task.getId().equals(text)) return task.getId();
      final TaskRepository repository = task.getRepository();
      if (repository != null) {
        final String id = repository.extractId(text);
        if (id != null) {
          return id;
        }
      }
    }
    for (TaskRepository repository : manager.getAllRepositories()) {
      final String id = repository.extractId(text);
      if (id != null) return id;
    }
    return null;
  }

  @Nullable
  public PsiElement resolve() {
    @NonNls String text = getValue();
    if (text.isEmpty()) {
      return null;
    }
    final Project project = getElement().getProject();
    final TaskManager manager = TaskManager.getManager(project);
    if (manager == null) return null;
    final Map<String, TaskPsiElement> cache = CachedValuesManager.getManager(project).getCachedValue(project, ISSUE_REFERENCE_CACHE, CACHED_VALUE_PROVIDER, false);
    final String id = getId(text, manager);
    if (id == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Cannot determine id for '" + StringUtil.shortenPathWithEllipsis(text, 50) + "'");
      }
      return null;
    }
    if (!id.equals(text) && !isAllowPartialMatch()) {
      return null;
    }
    TaskPsiElement value = cache.get(id);
    if (value == null) {
      final Task founded = getTask(id, text, manager);
      if (founded != null) {
        value = new OpenableTaskPsiElement(PsiManager.getInstance(project), founded);
        cache.put(id, value);
      }
    }
    return value;
  }

  @NotNull
  public Object[] getVariants() {
    final TaskManager manager = TaskManager.getManager(getElement().getProject());
    if (manager == null) return EMPTY_ARRAY;
    final LinkedHashSet<Task> tasks = new LinkedHashSet<Task>();
    final LocalTask active = manager.getActiveTask();
    tasks.add(active);
    tasks.addAll(manager.getLocalTasks());
    tasks.addAll(manager.getIssues(null, false));

    if (tasks.isEmpty()) return EMPTY_ARRAY;
    final List<LookupElement> list = new SmartList<LookupElement>();
    for (Task task : tasks) {
      list.add(LookupElementBuilder.create(task, task.getId()).withPresentableText(task.getPresentableName()));
    }
    return list.toArray();
  }
}
