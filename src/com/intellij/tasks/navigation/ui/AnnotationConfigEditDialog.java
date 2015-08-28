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

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Vladislav.Rassokhin
 */
public class AnnotationConfigEditDialog extends DialogWrapper {
  private final List<AnnotationConfig> myExistingItems;
  @Nullable
  private final AnnotationConfig myEditable;
  private JPanel myPanel;
  private EditorTextField myAnnotationField;
  private TextFieldWithAutoCompletion<PsiField> myElementField;
  private Project myProject;

  protected AnnotationConfigEditDialog(@NotNull final List<AnnotationConfig> items, @Nullable AnnotationConfig editable, @NotNull final Project project) {
    super(null);
    myProject = project;
    setTitle(editable == null ? "Add" : "Edit");
    myExistingItems = items;
    myEditable = editable;
    if (myEditable != null) {
      myAnnotationField.setText(editable.getAnnotation().trim());
      myElementField.setText(editable.getElement().trim());
    }
    init();
  }

  public AnnotationConfig getData() {
    return new AnnotationConfig(getAnnotation(), getElement());
  }

  private String getElement() {
    return myElementField.getText().trim();
  }

  private String getAnnotation() {
    return myAnnotationField.getText().trim();
  }

  public JPanel getPanel() {
    return myPanel;
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return myPanel;
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return myAnnotationField;
  }

  @Nullable
  @Override
  protected ValidationInfo doValidate() {
    if (StringUtil.isEmpty(getAnnotation())) {
      return new ValidationInfo("Specify annotation class", myAnnotationField);
    }
    if (StringUtil.isEmpty(getElement())) {
      return new ValidationInfo("Specify element", myElementField);
    }
    final AnnotationConfig data = getData();
    if (!getData().equals(myEditable) && myExistingItems.contains(data)) {
      return new ValidationInfo("Already exists");
    }
    return null;
  }

  private void createUIComponents() {
    final String annotation = myEditable != null ? myEditable.getAnnotation().trim() : "";
    final String element = myEditable != null ? myEditable.getElement().trim() : "";

    final JavaCodeFragmentFactory factory = JavaCodeFragmentFactory.getInstance(myProject);

    final PsiTypeCodeFragment fragment = factory.createTypeCodeFragment(annotation, null, true);
    final Document document = PsiDocumentManager.getInstance(myProject).getDocument(fragment);
    final DocumentAdapter listener = new DocumentAdapter() {
      @Override
      public void documentChanged(DocumentEvent e) {
        final PsiType type;
        try {
          type = fragment.getType();
        } catch (PsiTypeCodeFragment.TypeSyntaxException e1) {
          return;
        } catch (PsiTypeCodeFragment.NoTypeException e1) {
          return;
        }
        if (!(type instanceof PsiClassType)) {
          return;
        }
        final PsiClassType ct = (PsiClassType) type;
        final PsiClass aClass = ct.resolve();
        if (aClass == null) {
          return;
        }
        if (!aClass.isAnnotationType()) {
          // TODO: Show warning?
        }
        final PsiField[] fields = aClass.getAllFields();
        myElementField.setVariants(Arrays.asList(fields));
      }
    };

    myAnnotationField = new EditorTextField(document, myProject, StdFileTypes.JAVA);
    myAnnotationField.setDocument(document);
    myAnnotationField.setOneLineMode(true);
    myAnnotationField.addDocumentListener(listener);
    final Set<PsiField> variants = calculateVariants();
    final TextFieldWithAutoCompletionListProvider<PsiField> provider = new TextFieldWithAutoCompletionListProvider<PsiField>(variants) {
      @Nullable
      @Override
      protected Icon getIcon(@NotNull PsiField item) {
        return null;
      }

      @NotNull
      @Override
      protected String getLookupString(@NotNull PsiField item) {
        return item.getName();
      }

      @Nullable
      @Override
      protected String getTailText(@NotNull PsiField item) {
        return null;
      }

      @Nullable
      @Override
      protected String getTypeText(@NotNull PsiField item) {
        return item.getType().getPresentableText();
      }

      @Override
      public int compare(PsiField item1, PsiField item2) {
        return item1.getName().compareTo(item2.getName());
      }
    };

    myElementField = new TextFieldWithAutoCompletion<PsiField>(myProject, provider, true, element);
  }

  private Set<PsiField> calculateVariants() {
    return Collections.emptySet();
  }

}
