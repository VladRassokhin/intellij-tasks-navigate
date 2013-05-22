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

import com.intellij.ui.components.JBLabel;

import javax.swing.*;

/**
 * @author Vladislav.Rassokhin
 */
public class AnnotationConfigEditForm {
  private JPanel myPanel;
  private JTextField myAnnotationField;
  private JTextField myElementField;
  private JBLabel myAnnotationLabel;
  private JBLabel myElementLabel;

  public void setData(AnnotationConfig config) {
    myAnnotationField.setText(config.getAnnotation());
    myElementField.setText(config.getElement());
  }

  public AnnotationConfig getData() {
    return new AnnotationConfig(myAnnotationField.getText(), myElementField.getText());
  }

  public JPanel getPanel() {
    return myPanel;
  }
}
