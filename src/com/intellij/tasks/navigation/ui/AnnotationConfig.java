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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ComparatorUtil;
import org.jetbrains.annotations.NotNull;

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

  @NotNull
  public String getAnnotation() {
    return StringUtil.notNullize(myAnnotation);
  }

  public void setAnnotation(String annotation) {
    this.myAnnotation = annotation;
  }

  @NotNull
  public String getElement() {
    return StringUtil.notNullize(myElement);
  }

  public void setElement(String element) {
    this.myElement = element;
  }

  public String getPresentableName() {
    return String.valueOf(myAnnotation) + "#" + String.valueOf(myElement);
  }

  public void copyFrom(AnnotationConfig config) {
    this.myAnnotation = config.myAnnotation;
    this.myElement = config.myElement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AnnotationConfig that = (AnnotationConfig) o;

    if (!ComparatorUtil.equalsNullable(myAnnotation, that.myAnnotation)) return false;
    if (!ComparatorUtil.equalsNullable(myElement, that.myElement)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = myAnnotation != null ? myAnnotation.hashCode() : 0;
    result = 31 * result + (myElement != null ? myElement.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return getPresentableName();
  }
}
