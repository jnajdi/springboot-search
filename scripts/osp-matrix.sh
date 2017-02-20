#!/bin/bash
#
#
#

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

source /apps/common/lib/common.sh ||exit
source $DIR/default.sh ||exit

set -e
export TZ="US/Eastern"

START_DATE=$(date)


dry_run="false"

if [ "$1" = "--dry-run" ] || [ "$2" = "--dry-run" ]; then
	dry_run="true"
fi 

refresh="false"

if [ "$1" = "--refresh" ] || [ "$2" = "--refresh" ] ; then
	refresh="true"
fi 

#Generate Matrix achives for all sites. 
require_dir $ARCHIVE_SCRIPT_DIR


require_readable $ARCHIVE_SCRIPT_DIR/target/$ARCHIVE_SCRIPT

cd $ARCHIVE_SCRIPT_DIR
echo "java -jar $ARCHIVE_SCRIPT_DIR/target/$ARCHIVE_SCRIPT"
if [ $dry_run == "false" ]; then
	java -jar target/$ARCHIVE_SCRIPT
fi 


# Get the list of all the matrices in the a directory
matrix_files=()
MATRICES_LIST_FILE="matrices_list.tmp"
MATRIX_FILE="matrix.xml"

require_dir $ARCHIVE_OUTPUT_DIR
#echo "cd $ARCHIVE_OUTPUT_DIR"
cd $ARCHIVE_OUTPUT_DIR

#Delete tmp file if already exists
echo "find . -name $MATRICES_LIST_FILE -exec rm -fvr {} \;"
find . -name $MATRICES_LIST_FILE -exec rm -fvr {} \;

#Populate the list of all the matrices
 
echo "find . -name $MATRIX_FILE  | sed -e "s/.\///" | tee $MATRICES_LIST_FILE" 
find . -name $MATRIX_FILE  | sed -e "s/.\///" | tee $MATRICES_LIST_FILE 

declare -A files 
while read line           
do
    IFS=':' read -ra filenames <<< "$line"
    filename=${filenames[0]}
    matrix_files+=($ARCHIVE_OUTPUT_DIR$filename)
    #echo $filename
done < $MATRICES_LIST_FILE 

number_of_lines=$(wc -l $MATRICES_LIST_FILE | awk '{print $1}' )
echo "Number of files: $number_of_lines"
echo "Number of matrices: ${#matrix_files[@]}"

#Delete tmp file
find . -name $MATRICES_LIST_FILE -exec rm -fvr {} \;

#Generate HTML file by applying the XSL script to the archive matrix.xml file.  

require_dir $XSL_SCRIPT_DIR

cd $DIR
for file in "${matrix_files[@]}"; do
	filedir=$(dirname $file)
        echo "/apps/expmatrix-dev/local/httpd/php/bin/php $XSL_SCRIPT $file $XSL_FILE $filedir/"

	cd $filedir/
	directory_name=${PWD##*/} 
	echo "creating $filedir/$directory_name.zip"
	zip -r "$directory_name.zip" ./*
done

# Zip directories


