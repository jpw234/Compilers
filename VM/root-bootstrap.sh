#!/bin/bash
set -x
set -e

export DEBIAN_FRONTEND="noninteractive"

# basics - do not modify!
apt-get -qq update &>/dev/null
apt-get -qq install \
  software-properties-common \
  python-software-properties \
  wget \
  dpkg \
  m4 \
  nano vim \
  &>/dev/null

# test harness - do no modify!
WEBSEMESTER=2016sp
XTH=xth.tar.gz
wget -nv -N http://www.cs.cornell.edu/courses/cs4120/$WEBSEMESTER/project/$XTH
mkdir -m 777 -p xth
tar -zxf $XTH --overwrite -C xth

# Java 8
add-apt-repository ppa:webupd8team/java &>/dev/null
apt-get -qq update &>/dev/null
echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections
apt-get -qq install oracle-java8-installer 2>/dev/null
apt-get -qq install oracle-java8-set-default 2>/dev/null

# Ant
apt-get -qq install ant 2>/dev/null

# Maven
apt-get -qq install maven 2>/dev/null

# Scala
SCALA=scala-2.11.7.deb
wget -nv -N www.scala-lang.org/files/archive/$SCALA
dpkg -i $SCALA &>/dev/null
apt-get -qq update  &>/dev/null
apt-get -qq install scala 2>/dev/null

# Jflex
JFLEX=jflex-1.6.1.tar.gz
wget -nv -N http://jflex.de/release/$JFLEX
tar -zxf $JFLEX

# OCaml
add-apt-repository ppa:avsm/ppa &>/dev/null
apt-get -qq update &>/dev/null
apt-get -qq install ocaml ocaml-native-compilers opam 2>/dev/null