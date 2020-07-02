# Import CSV with barcode listing
$Data = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataBarcode\barcode.csv' -Header 'barcode'

# Import CSV with UsedBarcode listing
$UData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataBarcode\UsedBarcode.csv' -Header 'barcode'

# Update used barcode file with used barcode
$UsedBarcodeFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataBarcode\UsedBarcode.csv'

# Import CSV with uuid listing
$UUIDData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\uniqueId\uuid.csv' -Header 'uuid'

# Import CSV with UsedUUID listing
$UsedUUIDData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\uniqueId\usedUUID.csv' -Header 'uuid'

# Update used uuid file with used uuid
$UsedUUIDFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\uniqueId\usedUUID.csv'

# Import CSV with eventcode listing
$EData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\eventcode.csv' -Header 'eventcode'

# Import CSV with eventcode listing
$UEData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\UsedEventCode.csv' -Header 'eventcode'

# Update used event file with used event
$UsedEventFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\UsedEventCode.csv'

# Update barcpde to be passed in request to EPS
$EPSBarcode = 'C:\Users\sachin.kamat\.jenkins\workspace\EPSVerification\src\test\resources\testdata\usedBarcode.csv'

function ModifyMPErGenerateCommandWithBarcode($Barcode){
	# Load file to be modified
    $mperBatchPath = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\Script\generateMPEr.bat'
    $mperBatch = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErBatch.bat'
    $bat = Get-Content $mperBatchPath

	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
   	
	# Modify file with barcode extracted from CSV file
    $seperator = "= "
    $pos = $bat.Split($seperator)
    #echo $pos[5]
    $event = $pos[5]
    $bcode = $pos[8]
    #echo $bcode
    #echo $pos[11]
    $timep = $pos[11]
    echo $Barcode
	echo $dateformat
	
	(Get-Content $mperBatchPath) | ForEach-Object{
		$_ -replace $bcode, $Barcode`
		-replace $timep, $dateformat`	
	} | Set-Content $mperBatch
	
	$new = Get-Content $mperBatch
	$new.Replace($timep, $dateformat) | Set-Content $mperBatchPath
}

function ModifyMPErGenerateCommandWithEventcode($Eventcode){
	# Load file to be modified
    $mperBatchPath = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\Script\generateMPEr.bat'
    $mperBatch = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErBatch.bat'
    $bat = Get-Content $mperBatchPath
 	
	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
   	
	# Modify file with eventcode extracted from CSV file
    $seperator = "= "
    $pos = $bat.Split($seperator)
    #echo $pos[5]
    $event = $pos[5]
    #echo $pos[8]
    $bar = $pos[8]
    #echo $pos[11]
    $timep = $pos[11]
    echo $Eventcode
	#echo $dateformat

	(Get-Content $mperBatchPath) | ForEach-Object{
		$_ -replace $event, $Eventcode`
		-replace $timep, $dateformat`	
	} | Set-Content $mperBatch

	
	$new = Get-Content $mperBatch
	$new.Replace($timep, $dateformat) | Set-Content $mperBatchPath
}

function ModifyMPErGenerateCommandWithUuid($Uuid){
	# Load file to be modified
    $mperBatchPath = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\Script\generateMPEr.bat'
    $mperBatch = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErBatch.bat'
    $bat = Get-Content $mperBatchPath

	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
   	
	# Modify file with barcode extracted from CSV file
    $seperator = "= "
    $pos = $bat.Split($seperator)
    #echo $pos[5]
    $event = $pos[5]
    $bcode = $pos[8]
    #echo $bcode
    #echo $pos[11]
    $timep = $pos[11]
	$uid = $pos[14]
    #echo $Barcode
	echo $Uuid
	#echo $dateformat
	
	(Get-Content $mperBatchPath) | ForEach-Object{
		$_ -replace $uid, $Uuid`
		-replace $timep, $dateformat`	
	} | Set-Content $mperBatch
	
	$new = Get-Content $mperBatch
	$new.Replace($timep, $dateformat) | Set-Content $mperBatchPath
}

foreach($d in $Data.barcode){
	$Barcode = $d.split("").trimend()
	if(($UData.barcode -contains $Barcode) -ne "True"){
		ModifyMPErGenerateCommandWithBarcode($Barcode)
		$Barcode | Add-Content $UsedBarcodeFile
        $LastBarcodeEventCode = Get-Content $EPSBarcode
        $Barcode | Set-Content $EPSBarcode
		break
	}
}

foreach($e in $EData.eventcode){
	$Eventcode = $e.split("").trimend()
	if(($UEData.eventcode -contains $Eventcode) -ne "True"){
		ModifyMPErGenerateCommandWithEventcode($Eventcode)
		$Eventcode | Add-Content $UsedEventFile
        $Barcode +',' + $Eventcode | Set-Content $EPSBarcode
		break
	} elseif($UEData.eventcode.Count -eq $EData.eventcode.Count) {
        $seperator = ","
        $pos = $LastBarcodeEventCode.Split($seperator)
        $LastUsedEventcode = $pos[1]
        $Barcode +',' + $LastUsedEventcode | Set-Content $EPSBarcode
		break
    }
}

foreach($u in $UUIDData.uuid){
	$Uuid = $u.split("").trimend()
	if(($UsedUUIDData.uuid -contains $Uuid) -ne "True"){
		ModifyMPErGenerateCommandWithUuid($Uuid)
		$Uuid | Add-Content $UsedUUIDFile
		break
	}
}

Start-Sleep -s 60