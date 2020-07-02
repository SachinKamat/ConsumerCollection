# Import CSV with barcode listing
$Data = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\dataBarcode\barcode.csv' -Header 'barcode'

# Import CSV with UsedBarcode listing
$UData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\dataBarcode\UsedBarcode.csv' -Header 'barcode'

# Update used barcode file with used barcode
$UsedBarcodeFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\dataBarcode\UsedBarcode.csv'

# Import CSV with uuid listing
$UUIDData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\uniqueId\uuid.csv' -Header 'uuid'

# Import CSV with UsedUUID listing
$UsedUUIDData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\uniqueId\usedUUID.csv' -Header 'uuid'

# Update used uuid file with used uuid
$UsedUUIDFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\uniqueId\usedUUID.csv'

function ModifyMPErGenerateCommandWithBarcode($Barcode){
	# Load file to be modified
	$OriginalData = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\COSS01G4_PreAdvice3_template.csv'
	$PreData = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\COSS01G4_PreAdvice3_PreData.csv'
	$PostData = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\COSS01G4_PreAdvice3_PostData.csv'
	
	$EpochDiff = New-TimeSpan "01 January 1970 00:00:00" $(Get-Date)
	$EpochMins = [INT] $EpochDiff.TotalMinutes
    $FinalData = "C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\PUT\COSS01G4_PreAdvice3_1$EpochMins.csv"

	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
	
	$pre = Get-Content $OriginalData
	$pre.Replace('VARIABLE1', '0368482000') | Set-Content $PreData
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE2', $cdate) | Set-Content $PostData
	
	$pre = Get-Content $PostData
	$pre.Replace('VARIABLE3', '461765TN') | Set-Content $PreData
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE5', $Barcode) | Set-Content $PostData
	
	$pre = Get-Content $PostData
	$pre.Replace('VARIABLE6', 'sachin.kamat@royalmail.com') | Set-Content $PreData
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE7', '07448017658') | Set-Content $PostData
	
	$pre = Get-Content $PostData
	$pre.Replace('VARIABLE8', 'TPN01') | Set-Content $PreData
}

function ModifyMPErGenerateCommandWithUuid($Uuid){
	# Load file to be modified
	$OriginalData = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\COSS01G4_PreAdvice3_template.csv'
	$PreData = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\COSS01G4_PreAdvice3_PreData.csv'
	$PostData = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\COSS01G4_PreAdvice3_PostData.csv'
	
	$rand = Get-Random -Maximum 9
	$EpochDiff = New-TimeSpan "01 January 1970 00:00:00" $(Get-Date)
	$EpochMins = [INT] $EpochDiff.TotalMinutes
	$FinalData = "C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Pre-Advice\PUT\COSS01G4_PreAdvice3_$rand$EpochMins.csv"

	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE4', $Uuid) | Set-Content $FinalData
}

foreach($d in $Data.barcode){
	$Barcode = $d.split("").trimend()
	if(($UData.barcode -contains $Barcode) -ne "True"){
		ModifyMPErGenerateCommandWithBarcode($Barcode)
		$Barcode | Add-Content $UsedBarcodeFile
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