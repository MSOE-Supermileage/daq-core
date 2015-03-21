<?
$op=$_POST['operation'];
if($op=="pull"){
	$file=fopen("databuffer.txt","r") or die("cant open file");
	echo fread($file,filesize("databuffer.txt"));
	fclose($file);
	$file=fopen("databuffer.txt","w");
	fclose($file);
}else if ($op=="push"){
	$file=fopen("databuffer.txt","r") or die("cant open file");
	$data= fread($file,filesize("databuffer.txt"));
	fclose($file);
	$newdata=$_POST['data'];
	if(strlen($data)>0){
		$newdata= $data . "\r\n" . $newdata;
	}
	$file=fopen("databuffer.txt","w") or die("cant open file");
	fwrite($file,$newdata);
	fclose($file);
}

?>