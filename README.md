![Java](http://www.binary-studio.com/media/36243/java_development_services.png)

Java lang Plugin for ElasticSearch
==================================

The Java language plugin enables the use of `java` as the language of scripts to execute.

The main value add of this plugin is that it allows users to create fast native Java based scripts inside of a query, without having to distribute the scripts to each node as a configuration step. This greatly simplifies the operational concerns associated with using the alternative: [native scripts](http://www.elasticsearch.org/guide/reference/modules/scripting/).

Installation
------------

To install the latest development version:

- `git clone git@github.com:btiernay/elasticsearch-lang-java.git`
- `mvn clean package -DskipTests=true`
- `bin/plugin --url file:./target/elasticsearch-lang-java-<VERSION>.zip --install lang-java`

For more information, see http://www.elasticsearch.org/guide/reference/modules/plugins/.

Scripts
-------

Scripts are written using Java fragments that are inlined into a class template before being compiled with [JavaCompiler](http://docs.oracle.com/javase/6/docs/api/javax/tools/JavaCompiler.html). As such, it is required that you provide a `return` value in each control path of your script.

Be warned that calling methods such as `System.exit()` will succeed in terminating the host JVM.  

Variables
---------

Script variables may be accessed via the `Object vars(String name)` method. Depending on the script context, the following members will also be available:
- `lookup` of type [SearchLookup](https://github.com/elasticsearch/elasticsearch/blob/master/src/main/java/org/elasticsearch/search/lookup/SearchLookup.java)
- `doc()` of type [DocLookup](https://github.com/elasticsearch/elasticsearch/blob/master/src/main/java/org/elasticsearch/search/lookup/DocLookup.java)
- `fields()` of type [FieldsLookup](https://github.com/elasticsearch/elasticsearch/blob/master/src/main/java/org/elasticsearch/search/lookup/FieldsLookup.java)
- `source()` of type [SourceLookup](https://github.com/elasticsearch/elasticsearch/blob/master/src/main/java/org/elasticsearch/search/lookup/SourceLookup.java)
- `score()` of type `float`

Not that with `vars`, casting the return value is required in order to access object members. This is because compilation occurs before the variable types are supplied / known at runtime. 

Examples
--------

TBD

Disclaimer
----------

This plugin has not been tested in production and is currently for informational purposes only :)

License
-------

    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2013 Bob Tiernay

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
