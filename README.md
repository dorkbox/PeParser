PeParser
=========

Provides a way to parse and extract data from windows PE files, from Java.

This library can access meta-data information and details from within the PE file, and specifically it was designed to access and copy out files from the .rsrc section. 

Windows PE format and details: http://msdn.microsoft.com/en-us/library/ms809762.aspx

- This is for cross-platform use, specifically - linux 32/64, mac 32/64, and windows 32/64. Java 6+


<h4>We now release to maven!</h4> 

This project **includes** some utility classes, which are an extremely small subset of a much larger library; including only what is *necessary* for this particular project to function. Additionally this project is **kept in sync** with the utilities library, so "jar hell" is not an issue, and the latest release will always include the same utility files as all other projects in the dorkbox repository at that time.
  
  Please note that the utility classes have their source code included in the release, and eventually the entire utility library will be provided as a dorkbox repository.
```
<dependency>
  <groupId>com.dorkbox</groupId>
  <artifactId>PeParser</artifactId>
  <version>2.7</version>
</dependency>
```

Or if you don't want to use Maven, you can access the files directly here:  
https://oss.sonatype.org/content/repositories/releases/com/dorkbox/PeParser/  


<h2>License</h2>

This project is © 2014 dorkbox llc, and is distributed under the terms of the Apache v2.0 License. See file "LICENSE" for further references.

