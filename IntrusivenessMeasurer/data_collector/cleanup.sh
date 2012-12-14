#!/bin/bash

#
# Federal University of Campina Grande
# Distributed Systems Laboratory
#
# Author: Armstrong Mardilson da Silva Goes
# Contact: armstrongmsg@lsd.ufcg.edu.br
#

#
# Clean up
#
# Do clean up in the project, moving logs and data files to directories 
# named with the current date.
#

LOGS_DIRECTORY=logs
DATA_DIRECTORY=data
TO_DATA_DIRECTORY=$DATA_DIRECTORY/"`date "+%d-%m-%Y-%H-%M-%S"`"
TO_LOGS_DIRECTORY=$LOGS_DIRECTORY/"`date "+%d-%m-%Y-%H-%M-%S"`"

mkdir $TO_LOGS_DIRECTORY
mkdir $TO_DATA_DIRECTORY

mv *.cpu *.mem *.syscall $TO_DATA_DIRECTORY
mv *.log $TO_LOGS_DIRECTORY
