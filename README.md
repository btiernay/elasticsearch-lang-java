Java lang Plugin for ElasticSearch
==================================

The Java language plugin allows to have `java` as the language of scripts to execute.

The main value add of this plugin is that it allows users to create fast native Java based scripts inside of a query, without having to distribute the scripts to each node as a configuration step. This greatly simplifies operational concerns involved in using native scripts.

Scripts
-------
Scripts are written using Java fragments that are inlined into a templated class before compilation. As such, it is required that you provide a return value in each control path of your script.


Variables
---------

Script variables may be accessed via the `Object vars(String name)` method. Depending on the script context, the following methods will also be available:
- `doc()`
- `fields()` 
- `source()` 
- `score()` 

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
