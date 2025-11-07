#!/usr/bin/env bash

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass any JVM options to Gradle and Java processes.
DEFAULT_JVM_OPTS=""

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "ERROR: $*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched.
if ${cygwin} ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done

APP_HOME=`dirname "$PRG"`

# Absolutize APP_HOME
APP_HOME=`cd "$APP_HOME" > /dev/null && pwd`


#
# Helper to complain about an old Gradle version.
#
old_gradle_version() {
    die "Your build is currently configured to use Gradle ${gradle_version}, which is not supported by the wrapper. Please update the wrapper to one that supports this Gradle version."
}


#
# Helper to complain about looking for wrong wrapper jar
#
wrong_wrapper_jar() {
    die "The wrapper has been unable to locate a valid installation of the wrapper JAR in this build's cache."
}


#
# Helper to complain about unavailable JAVA_HOME
#
unavailable_java_home() {
    die "JAVA_HOME is not set and no 'java' command could be found in your PATH."
}


#
# Discover the purpose of the wrapper and trigger the appropriate action.
#
discover_gradle_version() {
    if [ -f "$APP_HOME/gradle/wrapper/gradle-wrapper.properties" ]; then
        distributionUrl=`grep 'distributionUrl' "$APP_HOME/gradle/wrapper/gradle-wrapper.properties" | cut -d'=' -f2`
        if [ -n "$distributionUrl" ]; then
            gradle_version=`basename "$distributionUrl" | sed -e 's/.*\-\([0-9\.]*\)\-.*/\1/'`
            if [ -z "$gradle_version" ]; then
                # This is not likely to happen, but we can't rule it out.
                old_gradle_version
            else
                # Normalize the version number to make it comparable.
                normalized_gradle_version=`echo "$gradle_version" | sed -e 's/-.*//'`
                major_version=`echo "$normalized_gradle_version" | cut -d'.' -f1`
                minor_version=`echo "$normalized_gradle_version" | cut -d'.' -f2`
                if [ "$major_version" -lt 6 ]; then
                    if [ "$major_version" -eq 5 ] && [ "$minor_version" -ge 4 ]; then
                        : # 5.4+ is fine
                    else
                        old_gradle_version
                    fi
                fi
            fi
        else
            old_gradle_version
        fi
    else
        old_gradle_version
    fi
}


# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || unavailable_java_home
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock.
if $darwin; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    APP_HOME=`cygpath --path --windows "$APP_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# Split up the JVM options only if the variable is not quoted.
if [ -z "${JVM_OPTS_QUOTED:-}" ]; then
    DEFAULT_JVM_OPTS=($DEFAULT_JVM_OPTS)
fi

discover_gradle_version

#
# Locate the wrapper JAR.
#
if [ -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
    WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
else
    wrong_wrapper_jar
fi

#
# Run Gradle
#
exec "$JAVACMD" "${DEFAULT_JVM_OPTS[@]}" "$JAVA_OPTS" "$GRADLE_OPTS" "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
