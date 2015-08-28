# intellij-tasks-navigate

Plugin for [IntelliJ Platform](http://www.jetbrains.org/pages/viewpage.action?pageId=983889) based IDEs.
Adds support for navigation from comments and any injection host (IntelliLang) to tasks (issues) in Web.

## Usage

First you have to setup some tasks server in `Settings | Tools | Tasks | Servers` (YouTrack, Jira, GitHub or another server)
Example: YouTrack with `https://youtrack.jetbrains.com` url.

### Navigation from comments

In `Settings | Tools | Tasks | Navigation` you can enable navigation from comments, so if you have any comment in code like
```java
public class Example {
    public static void main(String[] args) {
        // IDEA-62743
    }
}
```
`Ctrl+Click` or `Go to declaration` on `IDEA-62743` would open browser on `https://youtrack.jetbrains.com/issue/IDEA-62743` page

### Navigation from reference injection

IntelliJIDEA and other IDEs comes with preinstalled plugin '[IntelliLang](https://www.jetbrains.com/idea/help/intellilang.html)'.
It's quite useful to mark some string literals as containing some other languages or references to something.

This plugin adds 'Task Reference' reference injection, so for code
```java
public class Example {
    public static void main(String[] args) {
        String task = "IDEA-62743";
        System.out.println(task);
    }
}
```
You could set caret on `IDEA-62743` and add `Task Reference` reference using `Inject language or reference` intention.
Then `Ctrl+Click` or `Go to declaration` on `IDEA-62743` would open browser on `https://youtrack.jetbrains.com/issue/IDEA-62743` page

This feature better to be used with custom annotations (in Java):

Assume you have annotation
```java
package util;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TestFor {
    String[] issues() default {};
}
```

And you use it to annotate test classes or methods.
```java
package tests;

public class TestClass {
    @util.TestFor(issues = {"IDEA-62743"})
    public String testSomething() {
        return myName;
    }
}
```

Once you `Inject language or reference` on any usage of `TestFor#issues` any other usage would also have such injection.
Also injection could be setup in setting and stored in project files, read IntelliLang [documentation](https://www.jetbrains.com/idea/help/intellilang.html) for more info.

Also quick documentation for string literals with injection is available.

## Copyright

```text
Copyright 2000-2015 JetBrains s.r.o.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```