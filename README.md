Copyright
=========
```text
Copyright 2000-2013 JetBrains s.r.o.

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

Usage
=====

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
    @util.TestFor(issues = {"IDEA-40484"})
    public String testSomething() {
        return myName;
    }
}
```

Ensure you have Tasks server defined at Project Settings -> Tasks -> Servers (http://youtrack.jetbrains.com/ in my case)

Define you annotation in Project Settings -> Tasks -> Navigation 
    as "util.TestFor#issues"

Now, "Go to Definition" on string literal "IDEA-40484" will open http://youtrack.jetbrains.com/issue/IDEA-40484 in your browser.

Also quick documentation for string literals available.
