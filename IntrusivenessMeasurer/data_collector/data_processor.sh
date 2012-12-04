#!/bin/bash -e

#
# Federal University of Campina Grande
# Distributed Systems Laboratory
#
# Author: Armstrong Mardilson da Silva Goes
# Contact: armstrongmsg@lsd.ufcg.edu.br
#

#
# Data processor 
# 
# This program is intended to be used to process data 
# of the experiment on disk usage. It will process the 
# data from a file, extract the desired information and
# write the information to an output file.
# 
#
# usage:
# data_processor DATA_FILENAME OUTPUT_FILENAME
# 
# Parameters:
# DATA_FILENAME the source filename
# OUTPUT_FILENAME the output filename
#

DATA_FILENAME=$1
OUTPUT_FILENAME=$2

EXTRACTED_DATA=""

function read_data
{
	echo "`cat $DATA_FILENAME`"
}

function extract_data
{
	line=$1
}

function write_to_destination
{
	echo $EXTRACTED_DATA >> $OUTPUT_FILENAME
}

data=$(read_data)

for LINE in $data; do
	extract_data $LINE
	write_to_destination
done
