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

package com.intellij.tasks.navigation.injection;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.injection.ReferenceInjector;
import com.intellij.tasks.navigation.ToTaskReference;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class TaskIDReferenceInjector extends ReferenceInjector {
  @NotNull
  @Override
  public String getId() {
    return "task-reference";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Task Reference";
  }

  @NotNull
  @Override
  public PsiReference[] getReferences(@NotNull PsiElement element, @NotNull ProcessingContext context, @NotNull TextRange range) {
    return new PsiReference[]{new ToTaskReference<>(element)};
  }
}
