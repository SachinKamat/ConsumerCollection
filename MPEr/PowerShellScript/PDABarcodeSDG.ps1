# Import CSV with barcode listing
$Data = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataBarcode\sdg_barcodes.csv' -Header 'barcode'

# Import CSV with UsedBarcode listing
$UData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataBarcode\sdg_Usedbarcodes.csv' -Header 'barcode'

# Update used barcode file with used barcode
$UsedBarcodeFile = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataBarcode\sdg_Usedbarcodes.csv'

# Import CSV with uuid listing
$UUIDData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\uniqueId\sdg_uuid.csv' -Header 'uuid'

# Import CSV with UsedUUID listing
$UsedUUIDData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\uniqueId\sdg_UsedUuid.csv' -Header 'uuid'

# Update used uuid file with used uuid
$UsedUUIDFile = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\uniqueId\sdg_UsedUuid.csv'

# Import CSV with eventcode listing
$EData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\eventcode.csv' -Header 'eventcode'

# Import CSV with eventcode listing
$UEData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\UsedEventCode.csv' -Header 'eventcode'

# Update used event file with used event
$UsedEventFile = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\UsedEventCode.csv'

# Update barcpde to be passed in request to EPS
$SDGBarcode = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\IMIVerification\src\test\resources\testdata\usedBarcode.csv'

function ModifyMPErGenerateCommandWithBarcode($Barcode){
	# Load file to be modified
    $mperBatchPath = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErSDG.bat'
    $mperBatch = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErBatchSDG.bat'
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
    $mperBatchPath = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErSDG.bat'
    $mperBatch = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErBatchSDG.bat'
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
    $mperBatchPath = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErSDG.bat'
    $mperBatch = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\Script\generateMPErBatchSDG.bat'
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
        $LastBarcodeEventCode = Get-Content $SDGBarcode
        $Barcode | Set-Content $SDGBarcode
		break
	}
}

foreach($e in $EData.eventcode){
	$Eventcode = $e.split("").trimend()
	if(($UEData.eventcode -contains $Eventcode) -ne "True"){
		ModifyMPErGenerateCommandWithEventcode($Eventcode)
		$Eventcode | Add-Content $UsedEventFile
        $Barcode +',' + $Eventcode | Set-Content $SDGBarcode
		break
	} elseif($UEData.eventcode.Count -eq $EData.eventcode.Count) {
        $seperator = ","
        $pos = $LastBarcodeEventCode.Split($seperator)
        $LastUsedEventcode = $pos[1]
        $Barcode +',' + $LastUsedEventcode | Set-Content $SDGBarcode
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