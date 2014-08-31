#!/bin/bash

# init.sh: use immediately after cloning sbt-skeleton to remove the .git directory and perform other new-project setup tasks.

if [ $# -lt 2 ]; then
  echo "usage: ./init.sh <projectname> <orgname>"
  exit
fi

PROJECTNAME=$1
ORGNAME=$2

# Substitute $replacement for $placeholder in the given file
function substitute {
  placeholder=$1
  replacement=$2
  filename=$3

  echo "Setting $filename $placeholder to '$replacement'"
  sed -i.bak "s/$placeholder/$replacement/" $filename 

  echo "Checking substitution worked"
  subcount=`grep -c $replacement $filename`
  if [ "$subcount" -gt "0" ]; then
    echo "Substitution fine; deleting .bak file"
    rm ${filename}.bak
  fi
}

# Substitute the project name into the given file
# params: filename
function subname {
  filename=$1
  substitute "projectname" $PROJECTNAME $filename
}

# Substitute the org name into the given file
# params: filename
function suborg {
  filename=$1
  substitute "orgname" $ORGNAME $filename
}

# Construct the necessary directories under src
#
function builddirectories {
  withslashes=`echo $ORGNAME | tr . /`
  srcpath="src/main/scala/$withslashes/$PROJECTNAME"
  tstpath="src/test/scala/$withslashes/$PROJECTNAME/test"
  mkdir -pv $srcpath
  mkdir -pv src/main/resources 
  mkdir -pv $tstpath
  mkdir -pv src/test/resources 
}

echo "Removing old .git directory"
rm -rf .git/

subname build.sbt
subname README.md 

suborg build.sbt
builddirectories 


echo "Doing git init"
git init


