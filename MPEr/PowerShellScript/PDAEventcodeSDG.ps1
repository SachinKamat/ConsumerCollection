# Import CSV with eventcode listing
$EData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\eventcode.csv' -Header 'eventcode'

# Import CSV with eventcode listing
$UEData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\UsedEventCode.csv' -Header 'eventcode'

# Update used event file with used event
$UsedEventFile = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\UsedEventCode.csv'

# Update Barcode to be passed in request to EPS
$SDGBarcode = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\IMIVerification\src\test\resources\testdata\usedBarcode.csv'

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
	echo $dateformat

	(Get-Content $mperBatchPath) | ForEach-Object{
		$_ -replace $event, $Eventcode`
		-replace $timep, $dateformat`	
	} | Set-Content $mperBatch

	
	$new = Get-Content $mperBatch
	$new.Replace($timep, $dateformat) | Set-Content $mperBatchPath
}

foreach($e in $EData.eventcode){
	$Eventcode = $e.split("").trimend()
	if(($UEData.eventcode -contains $Eventcode) -ne "True"){
		ModifyMPErGenerateCommandWithEventcode($Eventcode)
		$Eventcode | Add-Content $UsedEventFile
		$bat = Get-Content $SDGBarcode
		$seperator = ","
		$pos = $bat.Split($seperator)
		$bat.Replace($pos[1], $Eventcode) | Set-Content $SDGBarcode
		break
	}
}

Start-Sleep -s 60