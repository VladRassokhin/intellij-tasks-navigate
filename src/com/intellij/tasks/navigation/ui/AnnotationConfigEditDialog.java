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

package com.intellij.tasks.navigation.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author Vladislav.Rassokhin
 */
public class AnnotationConfigEditDialog extends DialogWrapper {
  private final List<AnnotationConfig> myExistingItems;
  @Nullable
  private final AnnotationConfig myEditable;
  private JPanel myPanel;
  private JTextField myAnnotationField;
  private JTextField myElementField;

  protected AnnotationConfigEditDialog(@NotNull final List<AnnotationConfig> items, @Nullable AnnotationConfig editable) {
    super(null);
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
}
