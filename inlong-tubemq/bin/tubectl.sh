#!/bin/bash

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

if [ -z "$BASE_DIR" ] ; then
  PRG="$0"

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="`dirname "$PRG"`/$link"
    fi
  done
  BASE_DIR=`dirname "$PRG"`/..

  # make it fully qualified
  BASE_DIR=`cd "$BASE_DIR" && pwd`
  #echo "TubeMQ master is at $BASE_DIR"
fi

# display usage
function help() {
    echo "Usage: tubectl {topic|producer|consumer} --help" >&2
}
# if less than two arguments supplied
if [ $# -lt 2 ]; then
  help;
  exit 1;
fi

SERVICE=$1
case $SERVICE in
  topic)
    SERVICE_CLASS="org.apache.inlong.tubemq.server.tools.cli.CliTopicAdmin"
    SERVICE_ARGS=${@:2}
    ;;
  producer)
    SERVICE_CLASS="org.apache.inlong.tubemq.server.tools.cli.CliProducer"
    SERVICE_ARGS=${@:2}
    ;;
  consumer)
    SERVICE_CLASS="org.apache.inlong.tubemq.server.tools.cli.CliConsumer"
    SERVICE_ARGS=${@:2}
    ;;
  *)
    help;
    exit 1;
    ;;
esac
source $BASE_DIR/bin/env.sh
$JAVA $TOOLS_ARGS $SERVICE_CLASS $SERVICE_ARGS