<?php

/*****the process takes two input files:  matrix.xml matrix.xsl
      it is to create out directory if not exisiting
       matrix.htm,  matrix_.zip are generated under out/
******/

/* Prevent Caching */
header("Cache-Control: no-cache, no-store, must-revalidate"); // HTTP 1.1.
header("Pragma: no-cache"); // HTTP 1.0.
header("Expires: 0"); // Proxies.

//Turn off php runtime error reporting 
error_reporting(E_ERROR | E_PARSE);

// Load XML file

//Read input filename 
if ($argc < 4 ) {
 die ("Error: No input files or output path.  Please pass in the xml and xsl files as input, and output path\n");
} else {
$xml_in=$argv[1];
$xsl_in=$argv[2];
$zip_filepath=$argv[3];
}

//Create the output directory if not existing
if (!file_exists($zip_filepath)) {
mkdir($zip_filepath, 0777, true);
}

//htm output files
$output_htm = $zip_filepath . "matrix.htm";

//Xml tranformprocess
$xml = new DOMDocument;
$xml->load($xml_in);

// Load XSL file
$xsl = new DOMDocument;
$xsl->load($xsl_in);

// Process transform 
$proc = new XSLTProcessor;
$proc->importStyleSheet($xsl);
$newXml= $proc->transformToxml($xml);

//1.Capture new xml, output to buffer
ob_start();
echo $newXml;
file_put_contents($output_htm, ob_get_contents());

$file =ob_get_contents();
ob_end_clean();

//2. Capture all  href elements, save the list to output 
 $regexp = "<a\s[^>]*href=(\"??)([^\" >]*?)\\1[^>]*>(.*)<\/a>";
 preg_match_all("/$regexp/siU", $file, $matches);

//Output content Url list -- this is for debug
ob_start();

foreach($matches[0] as $val){
echo $val ."<br/>" ;
} 
$urllst = ob_get_contents();
ob_end_clean();

file_put_contents($zip_filepath ."downurl.htm", $urllst);

//3. copy all the contents to output, and ZipArchive them if need
/*
$zip_filename = $zip_filepath."matrix_".time().".zip";
$zip = new ZipArchive();
if ($zip->open($zip_filename,  ZipArchive::CREATE) !== TRUE) {
die ("ERROR: Could not open the archive file $zip_filename.");
}

//capture all the href text
foreach ($matches[0] as $tmpf){

        $f= strip_tags($tmpf);

        //copy the content files in the output folder via contentname
        if (file_exists($f)) {
        $fileout= $zip_filepath . basename($f);
        copy($f,$fileout);
        echo "Content file $f is copied completely \n";
        
	//add the content file to the zip
        $content = file_get_contents($f);
        $zip->addFromString(pathinfo ( $f, PATHINFO_BASENAME), $content) or die ("ERROR: Could not add file: $tmpf");
        }
}

$zip->close();
*/
echo "Archive created successfully. \n";

?>
