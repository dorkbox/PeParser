PeParser
=========

###### [![Dorkbox](https://badge.dorkbox.com/dorkbox.svg "Dorkbox")](https://git.dorkbox.com/dorkbox/PeParser) [![Github](https://badge.dorkbox.com/github.svg "Github")](https://github.com/dorkbox/PeParser) [![Gitlab](https://badge.dorkbox.com/gitlab.svg "Gitlab")](https://gitlab.com/dorkbox/PeParser) [![Bitbucket](https://badge.dorkbox.com/bitbucket.svg "Bitbucket")](https://bitbucket.org/dorkbox/PeParser)

Provides a light-weight way to parse and extract data from windows PE files, from Java.

This library can access meta-data information and details from within the PE file, and specifically it was designed to access and copy out files from the .rsrc section. 

Windows PE format and details: http://msdn.microsoft.com/en-us/library/ms809762.aspx

- This is for cross-platform use, specifically - linux 32/64, mac 32/64, and windows 32/64. Java 8+


&nbsp; 
&nbsp; 

Maven Info
---------
```
<dependencies>
    ...
    <dependency>
      <groupId>com.dorkbox</groupId>
      <artifactId>PeParser</artifactId>
      <version>3.1</version>
    </dependency>
</dependencies>
```

Gradle Info
---------
```
dependencies {
    ...
    implementation("com.dorkbox:PeParser:3.1")
}
```

Or if you don't want to use Maven, you can access the files directly here:  
https://repo1.maven.org/maven2/com/dorkbox/PeParser/  


License
---------
This project is © 2014 dorkbox llc, and is distributed under the terms of the Apache v2.0 License. See file "LICENSE" for further references.

