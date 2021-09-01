/*
 * Copyright 2000-2021 JetBrains s.r.o.
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

import com.intellij.openapi.project.Project;

import java.lang.reflect.Method;

public class YouTrackPluginApiWrapper {

  private static final Class<?> ourYouTrackPluginApiClass;
  private static final Method ourOpenIssueInToolWidowMethod;

  static {
    Method method = null;
    Class<?> clazz = null;
    try {
      clazz = Class.forName("com.github.jk1.ytplugin.YouTrackPluginApi");
      method = clazz.getDeclaredMethod("openIssueInToolWidow", String.class);
    } catch (Throwable ignored) {
    }
    ourYouTrackPluginApiClass = clazz;
    ourOpenIssueInToolWidowMethod = method;
  }


  public static boolean open(Project project, String id) {
    if (ourYouTrackPluginApiClass != null) {
      Object service = project.getServiceIfCreated(ourYouTrackPluginApiClass);
      if (service != null) {
        try {
          ourOpenIssueInToolWidowMethod.invoke(service, id);
          return true;
        } catch (Throwable ignored) {
        }
      }
      Object component = project.getComponent(ourYouTrackPluginApiClass);
      if (component != null) {
        try {
          ourOpenIssueInToolWidowMethod.invoke(component, id);
          return true;
        } catch (Throwable ignored) {
        }
      }
    }
    return false;
  }
}
