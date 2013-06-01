package com.intellij.tasks.navigation;

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
    final String text = comment.getText();
    if (text== null) {
      return PsiReference.EMPTY_ARRAY;
    }
    final List<PsiCommentToTaskReference> references = new ArrayList<PsiCommentToTaskReference>();
    final TaskRepository[] repositories = TaskManager.getManager(comment.getProject()).getAllRepositories();
    for (TaskRepository repository : repositories) {
      final String id = repository.extractId(text);
      if (id != null) {
        final int i = text.indexOf(id);
        if (i >= 0) {
          references.add(new PsiCommentToTaskReference(comment, new TextRange(i, i + id.length())));
        }
      }
    }
    if (references.isEmpty()) {
      return PsiReference.EMPTY_ARRAY;
    }
    return references.toArray(new PsiReference[references.size()]);
  }
}
