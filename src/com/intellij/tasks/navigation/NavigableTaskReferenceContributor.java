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

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Vladislav Rassokhin
 */
public class NavigableTaskReferenceContributor extends PsiReferenceContributor {
  static final Logger LOG = Logger.getInstance(NavigableTaskReferenceContributor.class);

  @NotNull
  private static List<TaskNavigationConfig.SharedConfiguration> getConfigs(@NotNull final Project project) {
    final TaskNavigationConfig config = ServiceManager.getService(project, TaskNavigationConfig.class);
    return config.configurations;
  }

  private static PsiElementPattern.Capture<PsiLiteralExpression> getElementPattern() {
    return PlatformPatterns.psiElement(PsiLiteralExpression.class).inside(PsiAnnotationParameterList.class).and(new FilterPattern(new TaskAnnotationFilter()));
  }

  @Override
  public void registerReferenceProviders(PsiReferenceRegistrar registrar) {

    registrar.registerReferenceProvider(getElementPattern(), new PsiReferenceProvider() {
      @NotNull
      public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull final ProcessingContext context) {
        return new PsiLiteralExpressionToTaskReference[]{new PsiLiteralExpressionToTaskReference((PsiLiteralExpression) element)};
      }
    });
    registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiComment.class), new TaskInPsiCommentReferenceProvider());
  }

  private static class TaskAnnotationFilter implements ElementFilter {

    public TaskAnnotationFilter() {
    }

    public boolean isAcceptable(Object element, PsiElement context) {
      final PsiNameValuePair pair = PsiTreeUtil.getParentOfType(context, PsiNameValuePair.class);
      if (null == pair) return false;
      final PsiAnnotation annotation = PsiTreeUtil.getParentOfType(pair, PsiAnnotation.class);
      if (annotation == null) return false;

      final Project project = annotation.getProject();
      for (TaskNavigationConfig.SharedConfiguration config : getConfigs(project)) {
        if (config.element.equals(pair.getName()) && config.annotation.equals(annotation.getQualifiedName())) {
          return true;
        }
      }
      return false;
    }

    public boolean isClassAcceptable(Class hintClass) {
      return PsiLiteralExpression.class.isAssignableFrom(hintClass);
    }
  }

}
