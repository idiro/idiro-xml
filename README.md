# idiro-xml
Xml parser utility

Problem
-------

When developing a programme that takes complex arguments, XML is a standard.
In Java or in any other programming language you need to:
* create your requirements
* create an xml schema
* create the xml parser
* create the actual running task
* create a documentation


The following library helps you to implement a little bit faster your solution:
* you do not need an xml schema
* the help is actually written in the classes, object by object
* the parsing is done automatically and includes complex validations


How it works
------------

Your process should implements FieldProcess (an XMLWord, see below)

You have to produce a running task with:
 * init
 * run
 * finalCheck

In the initParser method you can add XML object children to it: 
* XmlWord (xml fields), in XMLWord you can add other XMLWord and XMLLeaf
* XmlLeaf (Text nodes), it is a template class you can choose to implement boolean, integer, double or string.

Each chid is associated with a property: 
* name
* default value
* comment
* validator
* minimum number of occurrence 
* maximum number of occurence.

XMLWord
-------

XMLWord objects should implement:
* initParser: Create the XML children
* checkParsing: For extra validation: not that it is not a duplication from a Validator. 
* getDescription: Write object user help.
