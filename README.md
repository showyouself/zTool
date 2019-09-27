# EnableToJson
Optimize gadgets

Use a quick annotation，Convert pojo's toString result to json

Rewrite method, no run performance loss

#usage 

1.Introduce the module first
```$xslt
implementation("com.zengbingo:zTool:0.0.1")
```

2.Add spring bood startup configuration，Need to add the ``com.zengbingo`` package to the scan content
```$xslt
@SpringBootApplication(scanBasePackages = {"com.zengbingo.*"})
```
3.Add in configuration，Which packages need to use annotations，Here is ``com.testpkg``
```$xslt
com:
  zengbingo:
    enableToJson:
      pkg: com.testpkg
```
4. add annotation ``@EnableToJson`` in pojo
```$xslt
package com.testpkg.service.dto

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@EnableToJson
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestPojo {
    String name;
}
```

demo：
````$xslt
TestPojo t = new TestPojo();
t.setName("ben");
System.out.pringln(t.toString());
````

out:
```$xslt
{"name":"ben"}
```

