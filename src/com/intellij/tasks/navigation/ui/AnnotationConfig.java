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

/**
 * @author Vladislav Rassokhin
 */
public class AnnotationConfig {
  private String myAnnotation;
  private String myElement;

  public AnnotationConfig() {
  }

  public AnnotationConfig(AnnotationConfig other) {
    this(other.myAnnotation, other.myElement);
  }

  public AnnotationConfig(String annotation, String element) {
    this.myAnnotation = annotation;
    this.myElement = element;
  }

  public String getAnnotation() {
    return myAnnotation;
  }

  public void setAnnotation(String annotation) {
    this.myAnnotation = annotation;
  }

  public String getElement() {
    return myElement;
  }

  public void setElement(String element) {
    this.myElement = element;
  }

  public String getPresentableName() {
    return myAnnotation + "#" + myElement;
  }

  public void copyFrom(AnnotationConfig config) {
    this.myAnnotation = config.myAnnotation;
    this.myElement = config.myElement;
  }
}
