#!/bin/bash

# Set up JAVA_HOME and ANT_HOME as environment variables, 
# or they will use these hard-coded defaults
export JAVA_HOME=${JAVA_HOME:-/usr/j2sdk1.4.1_01/}
export ANT_HOME=${ANT_HOME:-/usr/apache-ant-1.5.2/}

export PATH=$PATH:$ANT_HOME/bin

ant -buildfile build/BUILD.XML "$@"
