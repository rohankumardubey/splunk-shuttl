#!/bin/bash

#
#
# Copyright (C) 2011 Splunk Inc.
#
# Splunk Inc. licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

echo "WARNING: This script (archiveBucket.sh) is now deprecated. \
    Use coldToFrozenScript.sh instead"

set -e
set -u


script_dir=$(dirname $0)
cd $script_dir

if [ $# -lt 2 ]; then
    echo 1>&2 "usage: $0 <index> <bucket>"
    exit 1
fi

index=$1
bucket=$2

source java_executable.env
exec $JAVA -cp ./*:../lib/* com.splunk.shuttl.archiver.archive.BucketFreezer $bucket
