<?
$op=$_POST['operation'];
if($op=="pull"){
	if(file_exists("databuffer.txt")){
		$file=fopen("databuffer.txt","r") or die("cant open file");
		$newdata=fread($file,filesize("databuffer.txt")+1);
		if($newdata==""){
			echo "****";
		}else {
			echo $newdata . "\r\n****";
		}
		fclose($file);
		$file=fopen("databuffer.txt","w");
		fclose($file);
	}else{
		echo "****";
		$file=fopen("databuffer.txt","w");
		fclose($file);
	}
}else if ($op=="push"){
	$file=fopen("databuffer.txt","r") or die("cant open file");
	$data= fread($file,filesize("databuffer.txt")+1);
	fclose($file);
	$newdata=$_POST['data'];
	if(strlen($data)>0){
		$newdata= $data . "\r\n" . $newdata;
	}
	$file=fopen("databuffer.txt","w") or die("cant open file");
	fwrite($file,$newdata);
	fclose($file);
	echo $newdata . "\r\n****";
}

?>